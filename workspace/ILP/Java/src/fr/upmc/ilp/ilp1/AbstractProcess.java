/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2006 <Christian.Queinnec@lip6.fr>
 * $Id: AbstractProcess.java 805 2009-09-14 08:50:16Z queinnec $
 * GPL version=2
 * ******************************************************************/

package fr.upmc.ilp.ilp1;

//import javax.inject.Inject; // Utiliser Guice

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thaiopensource.validate.ValidationDriver;

import fr.upmc.ilp.annotation.OrNull;
import fr.upmc.ilp.ilp1.interfaces.IAST;
import fr.upmc.ilp.ilp1.interfaces.IProcess;
import fr.upmc.ilp.tool.File;
import fr.upmc.ilp.tool.IContent;
import fr.upmc.ilp.tool.IFinder;

/** Cette classe abstraite est une implantation de IProcess avec quelques
 * methodes probablement utiles si l'on en herite. */

public abstract class AbstractProcess
implements IProcess {

    protected AbstractProcess (IFinder finder) throws IOException {
        setFinder(finder);
        // Par defaut, on cherche les grammaires dans Grammars/
        getFinder().addPath("Grammars");
        // les programmes C dans C/
        getFinder().addPath("C");
        // Le script de compilation et d'execution est C/compileThenRun.sh
        setCompileThenRunScript(getFinder().findFile("compileThenRun.sh"));
        // et les fichiers temporaires sont chez l'utilisateur:
        final File tempDir = new File(System.getProperty("user.dir"));
        setCFile(new java.io.File(tempDir, "theProgram.c"));
    }
    
    public void setVerbose (boolean verbose) {
        this.verbose = verbose;
    }
    /** Ce booleen rend Process verbeux! */
    protected boolean verbose = true;
    
    /** Definir où chercher tous les fichiers nécessaires. */
    public void setFinder (IFinder finder) {
        this.finder = finder;
    }
    public IFinder getFinder () {
        assert(this.finder != null);
        return this.finder;
    }
    //@Inject
    private IFinder finder;

    /** Initialisation: On se contente de recuperer le texte du programme
     * dont on va s'occuper.
     */

  public void initialize (IContent ic) {
      try {
          this.programText = ic.getContent();
          this.initialized = true;
      } catch (Throwable e) {
          this.initializationFailure = e;
          if ( this.verbose ) {
              System.err.print(e);
          }
      }
  }

  public boolean isInitialized() {
      return this.initialized;
  }

  public @OrNull Throwable getInitializationFailure() {
      return this.initializationFailure;
  }

  public @OrNull String getProgramText() {
      assert this.initialized;
      return this.programText;
  }

  protected boolean initialized = false;
  protected Throwable initializationFailure = null;
  protected String programText;

  /** Preparation */

  public boolean isPrepared() {
      return this.prepared;
  }
  protected boolean prepared = false;

  public @OrNull Throwable getPreparationFailure() {
      return this.preparationFailure;
  }
  protected Throwable preparationFailure = null;
  
  public @OrNull IAST getIAST() {
      assert this.prepared;
      return this.iast;
  }
  public void setIAST(IAST iast) {
      this.iast = iast;
  }
  //@Inject
  protected IAST iast;
  
  /** Un utilitaire pour la preparation. 
   * Étant donné le nom d'une grammaire, il charge la grammaire et
   * valide le texte du programme à traiter. */
  public Document getDocument (String rngBaseName) 
          throws IOException, SAXException, ParserConfigurationException {
      final java.io.File rngFile = getFinder().findFile(rngBaseName);
      return getDocument(rngFile);
  }
  
  public Document getDocument (java.io.File rngFile)
      throws IOException, SAXException, ParserConfigurationException {
      final String rngFilePath = rngFile.getAbsolutePath();
      final InputSource isg = ValidationDriver.fileInputSource(rngFilePath);
      final ValidationDriver vd = new ValidationDriver();
      vd.loadSchema(isg);
      InputSource is =
          new org.xml.sax.InputSource(
                  new StringReader(this.programText));
      // Manquait (comme signalé par Rafael.Cerioli@gmail.com):
      if ( ! vd.validate(is) ) {
          throw new SAXException("Invalid XML program!");
      }

      final DocumentBuilderFactory dbf =
          DocumentBuilderFactory.newInstance();
      final DocumentBuilder db = dbf.newDocumentBuilder();
      // Le precedent flux est tari!
      is = new org.xml.sax.InputSource(
                  new StringReader(this.programText));
      this.document = db.parse(is);
      return this.document;
  }
  /** getDocument() sans argument retourne le DOM. */
  public Document getDocument () {
      assert this.document != null;
      return this.document;
  }
  private Document document = null;

  /** Definir la grammaire a utiliser pour comprendre le programme XML */
  public void setGrammar (java.io.File rngFile) {
      this.rngFile = rngFile;
  }
  public java.io.File getGrammar() {
      assert(this.rngFile != null);
      return this.rngFile;
  }
  /** Le fichier contenant la grammaire d'ILP a utiliser: */
  //@Inject
  protected java.io.File rngFile;

  /** Interpretation */

  public boolean isInterpreted() {
      return this.interpreted;
  }
  protected boolean interpreted = false;

  public @OrNull Throwable getInterpretationFailure() {
      return this.interpretationFailure;
  }
  protected Throwable interpretationFailure = null;

  public String getInterpretationPrinting() {
      assert(this.interpreted);
      return this.printing;
  }
  protected String printing = "";

  public Object getInterpretationValue() {
      assert(this.interpreted);
      return this.result;
  }
  protected Object result = null;

  /** Compilation vers C. */

  public boolean isCompiled() {
      return this.compiled;
  }
  protected boolean compiled = false;

  public @OrNull Throwable getCompilationFailure() {
      return this.compilationFailure;
  }
  protected Throwable compilationFailure = null;

  public String getCompiledProgram() {
      assert(this.compiled);
      return this.ccode;
  }
  protected String ccode;
  
  /** Exécution du programme compilé: */

  /** Set the name of the C file to generate. */
  public void setCFile (java.io.File cFile) {
      this.cFile = cFile;
  }
  /** Le nom du fichier C qui sera engendré: */
  //@Inject
  protected java.io.File cFile;
  /** Le nom du script compilant et executant le programme C engendré: */
  //@Inject
  protected java.io.File compileThenRunScript;
  public void setCompileThenRunScript (java.io.File scriptFile) {
      this.compileThenRunScript = scriptFile;
  }

  public @OrNull Throwable getExecutionFailure() {
      return this.executionFailure;
  }
  protected Throwable executionFailure = null;

  public String getExecutionPrinting() {
      assert(this.executed);
      return this.executionPrinting;
  }
  protected String executionPrinting;

  public boolean isExecuted() {
      return this.executed;
  }
  protected boolean executed = false;
}
