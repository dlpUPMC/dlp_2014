package fr.upmc.ilp.ilp3.jsgen;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.ast.CEASTvariable;
import fr.upmc.ilp.ilp2.cgen.CgenEnvironment;
import fr.upmc.ilp.ilp2.cgen.CgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp3.CEASTFactory;
import fr.upmc.ilp.ilp3.CEASTParser;
import fr.upmc.ilp.ilp3.IAST3Factory;
import fr.upmc.ilp.tool.FileTool;
import fr.upmc.ilp.tool.IFinder;
import fr.upmc.ilp.tool.ProgramCaller;

public class Process extends fr.upmc.ilp.ilp3.Process {

    public Process (IFinder finder) throws IOException {
        super(finder);
        setCFile(new java.io.File("theProgram4.js"));
        IAST3Factory<CEASTparseException> factory = new CEASTFactory();
        setParser(new CEASTParser(factory));
    }

    /** Compilation vers Javascript. */

    @Override
    public void compile() {
        try {
            final ICgenEnvironment common = new CgenEnvironment();
            common.bindPrimitive("throw");
            common.bindGlobal(new CEASTvariable("pi")) ; //, "ILP.pi"));
            final ICgenLexicalEnvironment lexenv =
                CgenLexicalEnvironment.CgenEmptyLexicalEnvironment.create();
            final Compiler jscompiler = new Compiler(common);
            final StringBuffer sb = new StringBuffer();
            final Compiler.Data odata = 
                new Compiler.Data(sb, lexenv, common, null);
            jscompiler.visit(getCEAST(), odata);
            this.ccode = sb.toString();
            this.compiled = true;

        } catch (Throwable e) {
            this.compilationFailure = e;
        }
    }

    /** Exécution du programme compilé: */

    @Override
    public void runCompiled() {
        try {
            String totalCode = "// Runtime stuff:\n"
                + "function ILP() {};\n"
                + "ILP.pi = 3.1416;\n"
                + "ILP['print'] = function(o) {print(o);return false};\n"
                + "ILP['throw'] = function(o) {throw o};\n"
                + this.ccode + "\n"
                + "// Lancement programme entier:\n"
                + "try { print(" + Compiler.PROGRAM + "());\n"
                + "} catch (e) { print(e) };\n";
            
            //System.err.println(totalCode); // DEBUG
            /* NOTA: La fonction print(a, b) insere un blanc entre ses 
             * arguments et un retour a la ligne a la fin. 
             * NOTA: throw est un mot-clé de Rhino qui ne supporte pas la
             * graphie ILP.throw qu'on remplace donc par ILP['throw'].      */
         
            FileTool.stuffFile(this.cFile, totalCode);
            // Optionnel: mettre en forme le programme:
            String indentProgram = "indent " + this.cFile.getAbsolutePath();
            ProgramCaller pcindent = new ProgramCaller(indentProgram);
            pcindent.run();

            // et l'executer:
            if ( useRhino ) {
                //ScriptEngine rhino;
                ScriptEngine rhino = new ScriptEngineManager()
                    .getEngineByName("JavaScript");
                if ( rhino != null ) {
                    Writer output = new StringWriter();
                    rhino.getContext().setWriter(new PrintWriter(output));
                    rhino.eval(new StringReader(totalCode));
                    this.executionPrinting = output.toString();
                    this.executed = true;
                }
            } else {
                // js plante sur u74-4 mais pas js sans optimisation!
                String program = "js -O -1 "
                    + this.cFile.getAbsolutePath();
                ProgramCaller pc = new ProgramCaller(program);
                pc.setVerbose();
                pc.run();
                this.executionPrinting = pc.getStdout().trim();
                this.executed = ( pc.getExitValue() == 0 );
            }
            
        } catch (Throwable e) {
            this.executionFailure = e;
        }
    }
    // 
    protected boolean useRhino = true;
    
}
