
package compiler;
import compiler.lexer.*;
import compiler.node.*;
import java.io.*;

class Main{
    public static void main(String[] arguments)
    {
        PushbackReader reader = new PushbackReader(new InputStreamReader(System.in));
        Lexer lexer = new Lexer(reader);

        for(;;) {
            try {
                Token t = lexer.next();

                if (t instanceof EOF)
                    break;
                System.out.println(t.toString());
            } catch (Exception e)
            {
                System.err.println(e.getMessage());
            }
        }

        System.exit(0);
    }
}
