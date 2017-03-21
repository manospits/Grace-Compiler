
package compiler;
import compiler.parser.*;
import compiler.lexer.*;
import compiler.node.*;
import java.io.*;

class Main{
    //public static void main(String[] arguments)
    //{
        //PushbackReader reader = new PushbackReader(new InputStreamReader(System.in));
        //Lexer lexer = new Lexer(reader);

        //for(;;) {
            //try {
                //Token t = lexer.next();

                //if (t instanceof EOF)
                    //break;
                //System.out.println(t.toString());
            //} catch (Exception e)
            //{
                //System.err.println(e.getMessage());
            //}
        //}

        //System.exit(0);
    //}
    public static void main(String[] arguments){
        try{
            Parser p =
                new Parser(
                new Lexer(
                new PushbackReader(
                new InputStreamReader(System.in), 1024)));
            // Parse the input.
            try{
                Start tree = p.parse();
                System.out.println(tree.toString());
              }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
