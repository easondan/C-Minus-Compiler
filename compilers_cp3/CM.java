import java.io.*;
import absyn.*;

class CM {
    public static boolean SHOW_TREE = false;
    public static boolean SHOW_SYMBOL_TABLE = true;
    public static boolean GENERATE_CODE = false;

    static public void main(String[] argv) {
        String outputFileName = null;
        String outputCodeFileName = null;

        for (String s : argv) {
            if (s.equals("-a")) {
                SHOW_TREE = true;
            } else if (s.equals("-s")) {
                SHOW_SYMBOL_TABLE = true;
            } else if (s.equals("-c")) {
                GENERATE_CODE = true;
            } else if (s.endsWith(".cm")) {
                int dotIndex = s.lastIndexOf('.');
                outputFileName = s.substring(0, dotIndex) + ".abs";
            } else {
                System.out.println("Invalid argument: " + s);
            }
        }
        if (outputFileName == null) {
            System.out.println("No test file provided. Exiting...");
            return;
        }

        try {
            parser p = new parser(new Lexer(new FileReader(argv[0])));
            Absyn result = (Absyn) (p.parse().value);
            if (result == null) {
                return;
            }
            if (SHOW_TREE) {
                PrintStream output = new PrintStream(new File(outputFileName));
                System.setOut(output);
                System.out.println("The abstract syntax tree is:");
                AbsynVisitor visitor = new ShowTreeVisitor();
                result.accept(visitor, 0,false);
            }
            if (SHOW_SYMBOL_TABLE) {
                int dotIndex = outputFileName.lastIndexOf('.');
                outputFileName = outputFileName.substring(0, dotIndex) + ".sym";
                PrintStream output = new PrintStream(new File(outputFileName));
                System.setOut(output);

                System.out.println("The symbol tree is:\n");
                AbsynVisitor semanticAnalyzer = new SemanticAnalyzer();
                result.accept(semanticAnalyzer, 0,false);

                if (((SemanticAnalyzer) semanticAnalyzer).isValid) {
                    System.out.println("The abstract syntax tree is semantically correct.");

                }
                output.close();
            }
            if (GENERATE_CODE) {
                int dotIndex = outputFileName.lastIndexOf('.');
                outputFileName = outputFileName.substring(0, dotIndex) + ".tm";
                PrintStream output = new PrintStream(new File(outputFileName));
                System.setOut(output);
                AbsynVisitor codeGenerator = new CodeGenerator();
              ((CodeGenerator) codeGenerator).visit(result);
            }
        } catch (Exception e) {
            System.out.println("\nFailed to create the Abstract Syntax Tree: " + e.getMessage());
            System.out.println("Cannot proceed with the semantic analysis. Exiting...");
            System.out.println("Cannot generate Assembly Code. Exiting...");
        }
    }
}
