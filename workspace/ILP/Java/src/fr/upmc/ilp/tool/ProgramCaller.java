/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004-2005 <Christian.Queinnec@lip6.fr>
 * $Id: ProgramCaller.java 735 2008-09-26 16:38:19Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.tool;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

/** Une classe pour invoquer un programme externe (gcc par exemple) et
 * récupérer le contenu de son flux de sortie. */

public class ProgramCaller {
    
    /** Construire une instance de ProgramCaller qui permettra d'exécuter
     * une commande via le système d'exploitation.
     * 
     * @param program 
     *    la commande (programme et arguments) à exécuter.
     */
    
    public ProgramCaller (final String program) {
        this.program = program;
        this.stdout = new StringBuffer(1023);
        this.stderr = new StringBuffer(1023);
        this.countDownLatch = new CountDownLatch(2);
        this.running = false;
        this.verbose = 0;
    }
    private final String program;
    private static final Runtime RUNTIME = Runtime.getRuntime();
    private Process process;
    private final StringBuffer stdout;
    private final StringBuffer stderr;
    private CountDownLatch countDownLatch;
    private boolean running;
    private int verbose = 110;
    
    /** Verbaliser les traces d'exécution de la commande. */
    
    public void setVerbose () {
        this.verbose++;
    }
    
    private void verbalize (final String message) {
        this.verbalize(message, 0);
    }
    
    private void verbalize (final String message, final int level) {
        if ( this.verbose > level ) {
            System.err.println(message);
        }
    }
    
    /** Récupérer le flux de sortie produit par la commande.
     * 
     * @return la sortie standard de la commande
     */
    
    public String getStdout () {
        return stdout.toString();
    }
    
    /** Récupérer le flux de sortie d'erreur produit par la commande. 
     * 
     * @return la sortie d'erreur de la commande
     */
    
    public String getStderr () {
        return stderr.toString();
    }
    
    /** Recuperer le code de retour de la commande.
     * Bloque quand celle-ci n'est pas terminee. 
     * 
     * @return le code de retour
     */
    
    public int getExitValue () {
        this.verbalize("-getExitValue-", 10);
        try {
            this.countDownLatch.await();
        } catch (InterruptedException e) {
            this.verbalize("-getExitValue Exception-", 10);
            throw new RuntimeException();  // MOCHE
        }
        return this.exitValue; 
    }
    private transient int exitValue = 199;
        
    /** Lancer la commande et stocker ses flux de sortie (normale et 
     * d'erreur) dans un tampon pour pouvoir les analyser apres-coup. */
    
    public void run () {
        // Au plus, une seule invocation:
        synchronized (this) {
            if ( running ) {
                return;
            } else {
                running = true;
            }
        }
        // Exécuter la commande:
        verbalize("[Running: " + program + "...");
        try {
            this.process = RUNTIME.exec(program);
        } catch (Throwable e) {
            // Exception notamment signalee quand le programme n'existe pas:
            this.stderr.append(e.getMessage());
            // On ne fait pas partir les deux taches slurpStd*():
            this.countDownLatch.countDown();
            this.countDownLatch.countDown();
            verbalize("...not started]");
            return;
        }

        // NOTA: documentation tells that, on Windows, one should read
        // asap the results or deadlocks may occur.
        slurpStdOut();
        slurpStdErr();
        
        try {
            this.process.waitFor();
            this.countDownLatch.await();
        } catch (InterruptedException e) {
            this.verbalize("!run Exception!", 10);
            throw new RuntimeException(); // MOCHE
        }
        this.exitValue = this.process.exitValue();
        verbalize("...finished]");
    }
    
    private void slurpStdOut () {
        final Thread tstdout = new Thread () {
            @Override
            public void run () {
                try (final InputStream istdout = process.getInputStream();
                     final BufferedInputStream bstdout = 
                            new BufferedInputStream(istdout) ) {
                    final int size = 4096;
                    final byte[] buffer = new byte[size];
                    READ:
                        while ( true ) {
                            int count = 0;
                            try {
                                count = bstdout.read(buffer, 0, size);
                            } catch (IOException exc) {
                                continue READ;
                            }
                            if ( count > 0 ) {
                                final String s = new String(buffer, 0, count);
                                stdout.append(s);
                                ProgramCaller.this.verbalize("[stdout Reading: " + s + "]");
                            } else if ( count == -1 ) {
                                ProgramCaller.this.verbalize("[stdout Dried!]", 10);
                                ProgramCaller.this.countDownLatch.countDown();
                                return;
                            }
                        }
                } catch (IOException e) {
                    ProgramCaller.this.verbalize("[stdout problem!" + e + ']', 10);}
            }
        };
        tstdout.start();
    }

    // La meme chose sur le flux d'erreur:
    private void slurpStdErr () {
        final Thread tstderr = new Thread () {
            @Override
            public void run () {
                try (final InputStream istderr = process.getErrorStream();
                     final BufferedInputStream bstderr = 
                              new BufferedInputStream(istderr) ) {
                    final int size = 4096;
                    final byte[] buffer = new byte[size];
                    READ:
                        while ( true ) {
                            int count = 0;
                            try {
                                count = bstderr.read(buffer, 0, size);
                            } catch (IOException exc) {
                                continue READ;
                            }
                            if ( count > 0 ) {
                                final String s = new String(buffer, 0, count);
                                stderr.append(s);
                                ProgramCaller.this.verbalize("[stderr Reading: " + s + "]");
                            } else if ( count == -1 ) {
                                ProgramCaller.this.verbalize("[stderr Dried!]", 10);
                                ProgramCaller.this.countDownLatch.countDown();
                                return;
                            }
                        }
                } catch (IOException e) {
                    ProgramCaller.this.verbalize("[stderr problem!" + e + ']', 10);
                }
            }
        };
        tstderr.start();
    }
    
}

// end of ProgramCaller.java
