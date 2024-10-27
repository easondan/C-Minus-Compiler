import java.io.*;
import absyn.*;

class CM {
    public static boolean SHOW_TREE = false;
    static public void main(String[] argv) {
        /* Start the parser */

        for (String s : argv) {
            if (s.equals("-a")) {
                SHOW_TREE = true;
                break;
            }
        }

        try {
            parser p = new parser(new Lexer(new FileReader(argv[0])));
            Absyn result = (Absyn)(p.parse().value);
            if (SHOW_TREE && result != null) {
                System.out.println("The abstract syntax tree is:");
                AbsynVisitor visitor = new ShowTreeVisitor();
                result.accept(visitor, 0);
            }
        } catch (Exception e) {
            /* do cleanup here -- possibly rethrow e */
            e.printStackTrace();
        }
    }
}
