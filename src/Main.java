package FinalProject;

import FinalProject.lexer.*;
import FinalProject.node.*;
import FinalProject.parser.*;
import java.io.*;
import java.util.*;

public class Main
{
      public static void main(String[] arguments)
      {
            try
            {
                  // Scan and parse the input
                  Lexer lexer = new Lexer(new PushbackReader (new InputStreamReader(System.in), 1024));
                  Parser parser = new Parser(lexer);
                  Start ast = parser.parse();

                  // Initialize symbol table and a list of string constants
                  SymbolTable symbolTable = new SymbolTable();
                  ArrayList<String> stringConstantList = new ArrayList<String>();

                  // DEBUG: Print out the AST
                  //ast.apply(new PrintTree());

                  // Create the symbol table and check for semantic errors
                  CheckTreeSemantics checkTreeSemantics = new CheckTreeSemantics(symbolTable, stringConstantList);
                  ast.apply(checkTreeSemantics);

                  if (checkTreeSemantics.hadSemanticError)
                  {
                        // If there were semantic errors, don't continue
                        System.out.println("Compilation failed! Semantic errors were found.");
                  }
                  else
                  {
                        // Translate the AST into MIPS
                        //System.out.println("Compilation succeeded!");
                        ast.apply(new TranslateTree(symbolTable, stringConstantList));
                  }
            }
            catch (Exception e)
            {
                  System.out.println("Compilation error: " + e.getMessage());
            }
      }
}
