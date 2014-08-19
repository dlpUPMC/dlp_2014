/* ******************************************************************
 * ILP -- Implantation d'un langage de programmation.
 * Copyright (C) 2004 <Christian.Queinnec@lip6.fr>
 * $Id:CEASTclassDefinition.java 542 2006-10-26 18:53:30Z queinnec $
 * GPL version>=2
 * ******************************************************************/

package fr.upmc.ilp.ilp6.ast;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.upmc.ilp.annotation.ILPexpression;
import fr.upmc.ilp.ilp1.cgen.CgenerationException;
import fr.upmc.ilp.ilp1.runtime.EvaluationException;
import fr.upmc.ilp.ilp2.ast.CEASTparseException;
import fr.upmc.ilp.ilp2.interfaces.IAST2variable;
import fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment;
import fr.upmc.ilp.ilp2.interfaces.ILexicalEnvironment;
import fr.upmc.ilp.ilp4.ast.CEASTexpression;
import fr.upmc.ilp.ilp4.ast.CEASTglobalFunctionVariable;
import fr.upmc.ilp.ilp4.ast.NormalizeException;
import fr.upmc.ilp.ilp4.interfaces.IAST4functionDefinition;
import fr.upmc.ilp.ilp4.interfaces.IAST4variable;
import fr.upmc.ilp.ilp4.interfaces.IAST4visitor;
import fr.upmc.ilp.ilp4.interfaces.INormalizeLexicalEnvironment;
import fr.upmc.ilp.ilp6.interfaces.IAST6Factory;
import fr.upmc.ilp.ilp6.interfaces.IAST6classDefinition;
import fr.upmc.ilp.ilp6.interfaces.IAST6methodDefinition;
import fr.upmc.ilp.ilp6.interfaces.IAST6visitor;
import fr.upmc.ilp.ilp6.interfaces.ICgenEnvironment;
import fr.upmc.ilp.ilp6.interfaces.IClassEnvironment;
import fr.upmc.ilp.ilp6.interfaces.INormalizeGlobalEnvironment;
import fr.upmc.ilp.ilp6.interfaces.IParser6;
import fr.upmc.ilp.ilp6.runtime.ICommon;
import fr.upmc.ilp.ilp6.runtime.ILPClass;
import fr.upmc.ilp.ilp6.runtime.ILPmethod;

/** Définir une classe pour ILP6. */

public class CEASTclassDefinition
extends CEAST6
implements IAST6classDefinition {

    // Ce constructeur est invoqué par parse():
  public CEASTclassDefinition (final String className,
                               final String superClassName,
                               final String[] fieldNames,
                               final IAST6methodDefinition[] methods ) {
      this.className = className;
      this.superClassName = superClassName;
      this.fieldNames = fieldNames;
      this.methods = this.adjoinSelfToMethods(methods);
      this.methodNames = new String[methods.length];
      for ( int i=0 ; i<methods.length ; i++ ) {
          this.methodNames[i] = this.methods[i].getFunctionName();
      }
   }
  private final String className;
  private final String superClassName;
  // les champs propres:
  private final String[] fieldNames;
  // Les noms originaux des méthodes propres:
  private final String[] methodNames;
  // Les définitions élaborées des fonctions implantant les méthodes
  // propres. Attention: elles ont changé de nom global et contiennent
  // self en première variable.
  private final IAST6methodDefinition[] methods;

  public String getName () {
    return this.className;
  }
  public String getSuperClassName () {
    return this.superClassName;
  }
  public String[] getProperMethodNames () {
    return this.methodNames;
  }
  @ILPexpression(isArray=true)
  public IAST6methodDefinition[] getProperMethodDefinitions () {
    return this.methods;
  }
  public String[] getProperFieldNames () {
    return this.fieldNames;
  }

  public static IAST6classDefinition parse (
          final Element e, final IParser6 parser)
  throws CEASTparseException {
      final String className = e.getAttribute("nom");
      final String superClassName = e.getAttribute("parent");
      try {
          final XPathExpression fieldPath =
              xPath.compile("./champs/champ");
          final NodeList nlFields = (NodeList)
              fieldPath.evaluate(e, XPathConstants.NODESET);
          final List<String> fieldNames = new Vector<>();
          for ( int i=0 ; i<nlFields.getLength() ; i++ ) {
              final Element n = (Element) nlFields.item(i);
              fieldNames.add(n.getAttribute("nom"));
          }

          final XPathExpression methodPath =
              xPath.compile("./methodes/methode");
          final NodeList nlMethods = (NodeList)
              methodPath.evaluate(e, XPathConstants.NODESET);
          final List<IAST6methodDefinition> methodDefinitions = new Vector<>();
          for ( int i=0 ; i<nlMethods.getLength() ; i++ ) {
              final Element method = (Element) nlMethods.item(i);
              final IAST6methodDefinition m =
                  CEASTmethodDefinition.parse(method, parser);
              methodDefinitions.add(m);
          }
          return parser.getFactory().newClassDefinition(
                  className,
                  superClassName,
                  fieldNames.toArray(EMPTY_STRING_ARRAY),
                  methodDefinitions.toArray(EMPTY_FUNCTION_ARRAY)
                  );
      } catch (XPathExpressionException e1) {
        throw new CEASTparseException(e1);
    }
  }
  private static final XPath xPath = XPathFactory.newInstance().newXPath();
  private static final IAST6methodDefinition[] EMPTY_FUNCTION_ARRAY =
      new IAST6methodDefinition[0];
  
  // Transformer une methode en une fonction prenant self en premiere variable.
  
  private IAST6methodDefinition[] adjoinSelfToMethods (
          final IAST6methodDefinition[] methods ) {
      IAST6methodDefinition[] methods_ = new IAST6methodDefinition[methods.length];
      for ( int i=0 ; i<methods.length ; i++ ) {
          methods_[i] = adjoinSelfToMethod(methods[i]);
      }
      return methods_;
  }
  
  private IAST6methodDefinition adjoinSelfToMethod (
          final IAST6methodDefinition method ) {
      // Self a peut-etre deja ete ajoute ?
      IAST4variable[] vars = method.getVariables();
      if ( vars.length > 0 
        && vars[0] instanceof CEASTself.CEASTselfVariable ) {
          return method;
      }
      CEASTself.CEASTselfVariable selfVariable = new CEASTself.CEASTselfVariable();
      IAST4variable[] varsPlusSelf = new IAST4variable[1+vars.length];
      varsPlusSelf[0] = selfVariable;
      for ( int j=0 ; j<vars.length ; j++ ) {
            varsPlusSelf[j+1] = vars[j];
      }
      return new CEASTmethodDefinition(
                method.getMethodName(),
                method.getDefinedVariable(),
                varsPlusSelf,
                method.getBody() );   
  }

  // Plus d'acces direct aux champs a partir d'ici.

  //public @OrNull IAST6classDefinition findSuperClass (
  //        final IClassEnvironment common) {
  //    return common.findClassDefinition(getName());
  //} // Simplifie le code ???

  public String[] getFieldNames (final IClassEnvironment common) {
    final List<String> result = new Vector<>();
    // recuperer (dans l'ordre) les champs hérités:
    if ( ! "Object".equals(getSuperClassName()) ) {
      final IAST6classDefinition superCD =
        common.findClassDefinition(getSuperClassName());
      for ( String fieldName : superCD.getFieldNames(common) ) {
          result.add(fieldName);
      }
    }
    // y ajouter les champs propres:
    for ( String fieldName : getProperFieldNames() ) {
      result.add(fieldName);
    }
    return (String[]) result.toArray(EMPTY_STRING_ARRAY);
  }
  private static final String[] EMPTY_STRING_ARRAY = new String[0];

  /** Chercher la définition d'une méthode (propre ou héritée). */

  public IAST6methodDefinition getMethodDefinition (
          final String name,
          final IClassEnvironment common) {
      final IAST6methodDefinition[] methods = getProperMethodDefinitions();
      final String[] methodNames = getProperMethodNames();
      // Chercher dans les méthodes propres:
      for ( int i = 0 ; i<methods.length ; i++ ) {
          if ( name.equals(methodNames[i]) ) {
              return methods[i];
          }
      }
      // ou dans la superclasse (récursivement):
      if ( "Object".equals(getSuperClassName()) ) {
          // Sont-ce les méthodes prédéfinies ?
          if ( "print".equals(name) ) {
              return printMethod;
          } else if ( "classOf".equals(name) ) {
              return classOfMethod;
          } else {
              final String msg = "No such method " + name;
              throw new RuntimeException(msg);
          }
      } else {
          final IAST6classDefinition superCD =
              common.findClassDefinition(getSuperClassName());
          return superCD.getMethodDefinition(name, common);
      }
  }
  protected static final IAST6methodDefinition printMethod;
  static {
      final CEASTself self = new CEASTself();
      printMethod = new CEASTmethodDefinition(
              "print",
              new CEASTglobalFunctionVariable("ILP_print"),
              new IAST4variable[]{ self.getLocalVariable() },
              self ); // corps inutile
  }
  protected static final IAST6methodDefinition classOfMethod;
  static {
      final IAST4variable self = new CEASTself().getLocalVariable();
      classOfMethod = new CEASTmethodDefinition(
              "classOf",
              new CEASTglobalFunctionVariable("ILP_classOf"),
              new IAST4variable[] { self },
              CEASTexpression.voidExpression() );  // corps inutile
  }

  /** Calculer l'offset d'un champ (hérité ou propre). */

  public int getFieldOffset (final String fieldName,
                             final IClassEnvironment common) {
      String[] fieldNames = getProperFieldNames();
      for ( int i = 0 ; i<fieldNames.length ; i++ ) {
          if ( fieldNames[i].equals(fieldName) ) {
              return i + this.inheritedFieldSize(common);
          }
      }
      if ( "Object".equals(getSuperClassName()) ) {
          throw new RuntimeException("No such field");
      } else {
          final IAST6classDefinition superCD =
              common.findClassDefinition(getSuperClassName());
          return superCD.getFieldOffset(fieldName, common);
      }
  }

  /** Nombre de champs propres ainsi qu'hérités de la classe. */

  public int fieldSize (final IClassEnvironment common) {
    return getProperFieldNames().length + this.inheritedFieldSize(common);
  }

  /** Nombre de champs hérités de la classe. */

  public int inheritedFieldSize (final IClassEnvironment common) {
      if ( "Object".equals(getSuperClassName()) ) {
          return 0;
      } else {
          final IAST6classDefinition superCD =
              common.findClassDefinition(getSuperClassName());
          return superCD.fieldSize(common);
      }
  }

  /** Calculer l'offset d'une méthode. À tout message est associé un
   * entier non unique pour le programme tout entier. On recherche si
   * cet entier a déjà été attribué sinon on en utilise un nouveau. */

  public int getMethodOffset (final String methodName,
                              final IClassEnvironment common)
  throws CgenerationException {
      // MOCHE ces constantes devraient être calculées!
      if ( "print".equals(methodName) ) {
          return 0;
      } else if ( "classOf".equals(methodName) ) {
          return 1;
      }
      // On recherche d'abord parmi les méthodes héritées:
      try {
          if ( "Object".equals(getSuperClassName()) ) {
              final String msg = "Nope";
              throw new CgenerationException(msg);
          } else {
              final IAST6classDefinition superCD =
                  common.findClassDefinition(getSuperClassName());
              return superCD.getMethodOffset(methodName, common);
          }
      } catch (CgenerationException e) {
          // Pas héritée! On continue en séquence:
      }
      // Attribuer un nouveau numéro:
      String[] methodNames = getProperMethodNames();
      for ( int i = 0 ; i<methodNames.length ; i++ ) {
          if ( methodNames[i].equals(methodName) ) {
              return i + getNumberOfInheritedMethods(common);
          }
      }
      final String msg = "No such method " + methodName
          + " for " + getName();
      throw new CgenerationException(msg);
  }

  /** Calculer le nombre de méthodes héritées. */

  public int getNumberOfInheritedMethods (final IClassEnvironment common) {
      if ( "Object".equals(getSuperClassName()) ) {
          // Peu robuste! cette constante devrait être calculée      FIXME
          return 2; // print et classOf
      } else {
          final IAST6classDefinition superCD =
              common.findClassDefinition(getSuperClassName());
          return superCD.getProperMethodNames().length
               + superCD.getNumberOfInheritedMethods(common);
      }
  }

  /** Calculer le nombre total de méthodes propres ainsi qu'héritées. */

  public int getMethodsCount (final IClassEnvironment common) {
      return getProperMethodNames().length
           + getNumberOfInheritedMethods(common);
  }

  /** Interprétation. */

  @Override
  public Object eval6 (final ILexicalEnvironment lexenv,
                       final ICommon common)
  throws EvaluationException {
      final ILPClass superClass = common.findClass(getSuperClassName());
      final IAST4functionDefinition[] methods = getProperMethodDefinitions();
      final ILPmethod[] functions = new ILPmethod[methods.length];
      for ( int i = 0 ; i<methods.length ; i++ ) {
          functions[i] = (ILPmethod) methods[i].eval(lexenv, common);
      }
      ILPClass clazz = new ILPClass(getName(),
                                    superClass,
                                    getProperFieldNames(),
                                    getProperMethodNames(),
                                    functions );
      common.addClass(clazz);
      return clazz;
  }

  /** Compilation du vecteur des méthodes. */

  public void compileMethodsTable (final StringBuffer buffer,
                                   final ICgenEnvironment common,
                                   final IAST6classDefinition clazz)
    throws CgenerationException {
    // Citer les fonctions implantant les méthodes héritées:
    if ( "Object".equals(getSuperClassName()) ) {
        // Peu robuste! ces noms de méthodes devraient être calculées     FIXME
        compileMethodTableEntry(buffer, common, clazz, "print");
        compileMethodTableEntry(buffer, common, clazz, "classOf");
    } else {
        final IAST6classDefinition superCD =
            common.findClassDefinition(getSuperClassName());
        superCD.compileMethodsTable(buffer, common, clazz);
    }
    // puis les fonctions implantant les méthodes propres nouvelles:
    String[] methodNames = getProperMethodNames();
    for ( int i = 0 ; i<methodNames.length ; i++ ) {
        final int offset = this.getMethodOffset(methodNames[i], common);
        if ( offset >= this.getNumberOfInheritedMethods(common) ) {
            compileMethodTableEntry(buffer, common, clazz, methodNames[i]);
        }
    }
  }

  private void compileMethodTableEntry (final StringBuffer buffer,
                                        final ICgenEnvironment common,
                                        final IAST6classDefinition clazz,
                                        final String methodName)
    throws CgenerationException {
    IAST4functionDefinition function =
      clazz.getMethodDefinition(methodName, common);
    buffer.append(function.getDefinedVariable().getMangledName());
    buffer.append(",     /* ");
    buffer.append(methodName);
    buffer.append(" */\n   ");
  }

  /** En C la macro ILP_GenerateClass engendre le type d'une classe
   avec un certain nombre de méthodes. Ce type ne doit être spécifié
   qu'une seule fois. Il faut donc mémoriser si on a déjà émis ce code
   ou pas. */

  private void compileGenerateClassMacro (final StringBuffer buffer,
                                          final ICgenEnvironment common)
    throws CgenerationException {
    if ( !common.alreadyGeneratedClassMacro(getMethodsCount(common)) ) {
      // Engendrer le type approprié de la classe à engendrer:
      buffer.append("\nILP_GenerateClass(");
      buffer.append(getMethodsCount(common));
      buffer.append(");\n");
    }
  }

  /** Pour les besoins de citation mutuelles, on déclare les classes,
   * champs et méthodes qui apparaissent en C sous forme de variables
   * globales. */

  @Override
  public void compileHeader6 (final StringBuffer buffer,
                              final ICgenLexicalEnvironment lexenv,
                              final ICgenEnvironment common)
    throws CgenerationException {
    this.compileGenerateClassMacro(buffer, common);
    // Déclarer la classe:
    buffer.append("extern struct ILP_Class");
    buffer.append(getMethodsCount(common));
    buffer.append(" ILP_object_");
    buffer.append(getName());
    buffer.append("_class;\n");
    // Déclarer ses champs propres:
    for ( String fieldName : getProperFieldNames() ) {
      buffer.append("extern struct ILP_Field ILP_object_");
      buffer.append(fieldName);
      buffer.append("_field;\n");
    }
    // Déclarer ses méthodes propres (si ce n'est déjà fait):
    for ( IAST4functionDefinition method : getProperMethodDefinitions() ) {
        method.compileHeader(buffer, lexenv, common);
    }
  }

  /** Compiler une définition de classe. */
  @Override
  public void compile6 (final StringBuffer buffer,
                        final ICgenLexicalEnvironment lexenv,
                        final ICgenEnvironment common )
    throws CgenerationException {
    // Engendrer le code des méthodes propres:
    final String[] methodNames = getProperMethodNames();
    final IAST6methodDefinition[] methods = getProperMethodDefinitions();
    for ( int i = 0 ; i<methods.length ; i++ ) {
      buffer.append("\n/* Classe ");
      buffer.append(getName());
      buffer.append(", méthode ");
      buffer.append(methodNames[i]);
      buffer.append(": */\n");
      methods[i].compile(buffer, lexenv, common);
    }
    // Engendrer les champs propres:
    String lastFieldName = "NULL";
    if ( ! "Object".equals(getSuperClassName()) ) {
      final IAST6classDefinition superCD =
        common.findClassDefinition(getSuperClassName());
      final String[] allSuperFields = superCD.getFieldNames(common);
      if ( allSuperFields.length >= 1 ) {
          // Que s'il y a au moins un champ herite! <Francois.Nizou@etu.upmc.fr>
          lastFieldName = "&ILP_object_"
              + allSuperFields[allSuperFields.length-1] + "_field";
      }
    }
    String[] fieldNames = getProperFieldNames();
    for ( int i = 0 ; i<fieldNames.length ; i++ ) {
      buffer.append("\nstruct ILP_Field ILP_object_");
      buffer.append(fieldNames[i]);
      buffer.append("_field = {\n  &ILP_object_Field_class,\n     { { ");
      buffer.append("(ILP_Class) &ILP_object_");
      buffer.append(getName());
      buffer.append("_class,\n   ");
      buffer.append(lastFieldName);
      buffer.append(",\n   \"");
      buffer.append(fieldNames[i]);
      buffer.append("\",\n    ");
      buffer.append(i + inheritedFieldSize(common));
      buffer.append(" } }\n};\n");
      lastFieldName = "&ILP_object_" + fieldNames[i] + "_field";
    }
    // Engendrer la classe:
    buffer.append("\nstruct ILP_Class");
    buffer.append(getMethodsCount(common));
    buffer.append(" ILP_object_");
    buffer.append(getName());
    buffer.append("_class = {\n  &ILP_object_Class_class,\n  { { ");
    buffer.append("(ILP_Class) &ILP_object_");
    buffer.append(getSuperClassName());
    buffer.append("_class,\n         \"");
    buffer.append(getName());
    buffer.append("\",\n         ");
    buffer.append(fieldSize(common));
    buffer.append(",\n         ");
    buffer.append(lastFieldName);
    buffer.append(",\n         ");
    buffer.append(getMethodsCount(common));
    buffer.append(",\n { ");
    this.compileMethodsTable(buffer, common, this);
    buffer.append(" } } }\n};\n");
    // Engendrer les objets correspondant aux méthodes propres:
    for ( int i = 0 ; i<methods.length ; i++ ) {
      if ( !common.alreadyGeneratedMethodObject(methodNames[i]) ) {
        buffer.append("\nstruct ILP_Method ILP_object_");
        buffer.append(methodNames[i]);
        buffer.append("_method = {\n  &ILP_object_Method_class,\n  { { ");
        buffer.append("(struct ILP_Class*) &ILP_object_");
        buffer.append(getName());
        buffer.append("_class,\n  \"");
        buffer.append(methodNames[i]);
        buffer.append("\",\n  ");
        buffer.append(methods[i].getVariables().length);
        buffer.append(",  /* arité */\n  ");
        buffer.append(getMethodOffset(methodNames[i], common));
        buffer.append(" /* offset */ \n    } }\n};\n");
      }
    }
  }

  @Override
  public void findGlobalVariables (
          final Set<IAST2variable> globalvars,
          final fr.upmc.ilp.ilp2.interfaces.ICgenLexicalEnvironment lexenv ) {
      IAST4functionDefinition[] methods = getProperMethodDefinitions();
      for ( int i = 0 ; i<methods.length ; i++ ) {
          methods[i].findGlobalVariables(globalvars, lexenv);
      }
  }

  public IAST6classDefinition normalize (
          final INormalizeLexicalEnvironment lexenv,
          final INormalizeGlobalEnvironment common,
          final IAST6Factory factory )
    throws NormalizeException {
    final IAST6methodDefinition[] methods = getProperMethodDefinitions();
    final IAST6methodDefinition[] methods_ =
        new IAST6methodDefinition[methods.length];
    for ( int i = 0 ; i<methods.length ; i++ ) {
        methods_[i] = CEAST6.narrowToIAST6methodDefinition(
                methods[i].normalize(lexenv, common, factory) );
    }
    final IAST6classDefinition newClass =
        factory.newClassDefinition(
                getName(),
                getSuperClassName(),
                getProperFieldNames(),
                methods_);
    return newClass;
  }

  public <Data, Result, Exc extends Throwable> Result 
  accept (IAST6visitor<Data, Result, Exc> visitor, Data data) throws Exc {
      return visitor.visit(this, data);
  }
  public <Data, Result, Exc extends Throwable> Result 
  accept (IAST4visitor<Data, Result, Exc> visitor, Data data) throws Exc {
      return CEAST6.narrowToIAST6visitor(visitor).visit(this, data);
  }
  // NOTE: double methode surchargee.
}

// end of CEASTclassDefinition.java
