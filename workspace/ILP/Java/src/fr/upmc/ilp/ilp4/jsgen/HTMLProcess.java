package fr.upmc.ilp.ilp4.jsgen;

import java.io.IOException;

import fr.upmc.ilp.tool.FileTool;
import fr.upmc.ilp.tool.IFinder;
import fr.upmc.ilp.tool.ProgramCaller;

public class HTMLProcess extends Process {

    public HTMLProcess (IFinder finder) throws IOException {
        super(finder);
        setCFile(new java.io.File("theProgram4.html"));
    }
    
    /** Exécution du programme compilé: */

    @Override
    public void runCompiled() {
        try {
            String totalCode = "<html><head><title>ILP</title></head>"
                + "<body><script type='text/javascript'>\n"
                + "// Runtime stuff:\n"
                + "function ILP() {};\n"
                + "ILP.pi = 3.1416;\n"
                + "ILP.print = function(o) {document.write(o);return false}\n"
                + "ILP.throw = function(o) {throw o}\n"
                + this.ccode + "\n"
                + "// Lancement programme entier:\n"
                + "try { document.write(" + Compiler.PROGRAM + "());\n"
                + "} catch (e) { print(e) }\n"
                + "</script></body></html>\n"
                ;
            FileTool.stuffFile(this.cFile, totalCode);
            // System.err.println(totalCode); // DEBUG
            /* NOTA: La fonction print(a, b) insere un blanc entre ses 
             * arguments et un retour a la ligne a la fin.            */

            // et l'executer:
            String program = "firefox "
                + this.cFile.getAbsolutePath();
            ProgramCaller pc = new ProgramCaller(program);
            pc.setVerbose();
            pc.run();
            this.executionPrinting = pc.getStdout().trim();

            this.executed = ( pc.getExitValue() == 0 );

        } catch (Throwable e) {
            this.executionFailure = e;
        }
    }
    
}
