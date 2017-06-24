
package compiler;
import compiler.parser.*;
import compiler.lexer.*;
import compiler.node.*;
import java.io.*;

class Main{
    public static String filename;
    public static boolean optimize=false;
    public static String stripGrace(String a){
        String str;
        str=a.substring(0, a.lastIndexOf('.'));
        return str;
    }
    public static String getGrace(String a){
        String str;
        str=a.substring(a.lastIndexOf('.')+1);
        return str;
    }

    public static void main(String[] arguments){
        try{
            File initialFile=null;
            if(arguments.length > 2 ){
                System.out.printf("Error, too many arguments\n");
                System.exit(2);
            }
            for(String str : arguments){
                if(str.equals("-O")){
                    optimize=true;
                }
                else{
                    if( str.lastIndexOf('.') != -1 || getGrace(str).equals("grace") || getGrace(str).equals("grc")){
                        initialFile = new File(str);
                        filename=stripGrace(str);
                    }
                    else{
                        System.out.printf("Error wrong type of source, expected <name>.[grc,grace] found \"%s\"\n",str);
                        System.exit(2);
                    }
                }
            }
            if(arguments.length==2 && optimize ==false ){
                System.out.printf("Wrong arguments\n");
                System.exit(2);
            }
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
