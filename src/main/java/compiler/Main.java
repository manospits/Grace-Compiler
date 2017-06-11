
package compiler;
import compiler.parser.*;
import compiler.lexer.*;
import compiler.node.*;
import java.io.*;

class Main{
    public static String filename;
    public static String stripGrace(String a){
        a=a.substring(0,a.length()-6);
        return a;
    }
    public static void main(String[] arguments){
        try{
            File initialFile = new File(arguments[0]);
            filename=stripGrace(arguments[0]);
            InputStream progr = new FileInputStream(initialFile);
            Parser p =
                new Parser(
                new Lexer(
                new PushbackReader(
                new InputStreamReader(progr), 1024)));

            // Parse the input.
            try{
                Start tree = p.parse();
                //System.out.println(tree.toString());
                tree.apply(new SemMidCode());
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
