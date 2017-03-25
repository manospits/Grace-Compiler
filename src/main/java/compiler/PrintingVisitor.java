
package compiler;
import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;
import java.io.*;
import java.util.*;

public class PrintingVisitor extends DepthFirstAdapter{
    int spaces=0;

    public void print_spaces(){
        for(int i=0;i<spaces;i++){
            System.out.printf("  ");
        }
    }

    //program
    @Override
    public void inAProgram(AProgram node){
        print_spaces();
        System.out.println("Program start");
        spaces+=1;
    }

    @Override
    public void outAProgram(AProgram node){
        spaces-=1;
        print_spaces();
        System.out.println("Program end");
    }

    //func-def
    @Override
    public void inAFuncDef(AFuncDef node){
        print_spaces();
        System.out.println("Func-def start");
        spaces+=1;
    }

    @Override
    public void outAFuncDef(AFuncDef node){
        spaces-=1;
        print_spaces();
        System.out.println("Func-def end");
    }

    //header
    @Override
    public void inAHeader(AHeader node){
        print_spaces();
        System.out.printf("Header start : <id> = %s <ret-type> = %s \n", node.getTId().toString(),node.getRetType().toString());
        spaces+=1;
    }

    @Override
    public void outAHeader(AHeader node){
        spaces-=1;
        print_spaces();
        System.out.printf("Header end : <id> = %s\n", node.getTId().toString());
    }

    //fpar-def
    @Override
    public void inAFparDef(AFparDef node){
        print_spaces();
        System.out.println("Fpar-def start");
        spaces+=1;
        print_spaces();
        System.out.printf("Ref : ");
        if(node.getTRef() != null)
        {
            System.out.printf("Yes\n");
        }
        else{
            System.out.printf("No\n");

        }
        print_spaces();
        System.out.printf("IDs : %s", node.getTId().toString());
        List<PIdComma> copy = new ArrayList<PIdComma>(node.getIdComma());
        for(PIdComma e : copy)
        {
            System.out.printf("%s",e.toString());
        }
        System.out.println("");
        print_spaces();
        System.out.printf("Fpar-type : %s \n",node.getFparType().toString());
    }

    @Override
    public void outAFparDef(AFparDef node){
        spaces-=1;
        print_spaces();
        System.out.println("Fpar-def end");
    }

    //local-def
    @Override
    public void inAFuncDefLocalDef(AFuncDefLocalDef node)
    {
        print_spaces();
        System.out.println("Local-def start");
        spaces+=1;
    }


    @Override
    public void outAFuncDefLocalDef(AFuncDefLocalDef node){
        spaces-=1;
        print_spaces();
        System.out.println("Local-def end");
    }

}
