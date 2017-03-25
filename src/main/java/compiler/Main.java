
package compiler;
import compiler.parser.*;
import compiler.lexer.*;
import compiler.node.*;
import java.io.*;

class Main{
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
                tree.apply(new PrintingVisitor());
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
