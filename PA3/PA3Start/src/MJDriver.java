/*
 * MJDriver.java
 *
 * usage: 
 *   java MJ [ --two-pass-interpret | --two-pass-mips ] infile
 *
 * This driver either calls a MiniJava interpreter or generates MIPS
 * code to execute the MiniJava program.
 * The default is the interpreter.
 *
 */
import java.io.*;

import mjparser.*;
import ast_visitors.*;
import ast.visitor.*;
import ast.node.*;
import symtable.*;

public class MJDriver {

      private static void usage() {
          System.err.println(
            "MJ: Specify input file in program arguments");
      }
     
      public static void main(String args[]) 
      {
        System.out.println("hello"); 
        if(args.length < 1)
        {         
            usage();
            System.exit(1);
        }

        // filename should be the last command line option
        String filename = args[args.length-1];

        try {
          
		  // construct the lexer, 
          // the lexer will be the same for all of the parsers
          Yylex lexer = new Yylex(new FileReader(filename));

          // create the parser
          mj parser = new mj(lexer);
          int lastInPath = filename.lastIndexOf('/');
          parser.programName = filename.substring(lastInPath+1);
          System.out.println("Driver finds input filename: " + parser.programName);

          // and parse
		  //parser.parse();
	
		      ast.node.Node ast_root = (ast.node.Node)(parser.parse().value); 
           
                
          // print ast to file
          java.io.PrintStream astout =
            new java.io.PrintStream(
                new java.io.FileOutputStream(filename + ".ast.dot"));
          ast_root.accept(new DotVisitor(new PrintWriter(astout)));
          System.out.println("Printing AST to " + filename + ".ast.dot");
          
          
          SymTable globalST = new SymTable();
          // print ast to file
          java.io.PrintStream STout =
            new java.io.PrintStream(
              new java.io.FileOutputStream(filename + ".ST.dot"));
          System.out.println("Printing symbol table to " + filename + ".ST.dot");
          // perform type checking 
          ast_root.accept(new CheckTypes(globalST));
          	
          java.io.PrintStream avrsout =
              new java.io.PrintStream(
                      new java.io.FileOutputStream(filename + ".s"));
          ast_root.accept(new AVRgenVisitor(new PrintWriter(avrsout),globalST));
          System.out.println("Printing Atmel assembly to " + filename + ".s");
          

        } catch(exceptions.SemanticException e) {
            System.err.println(e.getMessage());
            System.exit(1);
       
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }  
      }

}
