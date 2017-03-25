
package compiler;
import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;
import java.io.*;

public class PrintingVisitor extends DepthFirstAdapter{
    @Override
    public void inAProgram(AProgram node){
        System.out.println("Program start");
    }

    @Override
    public void outAProgram(AProgram node){
        System.out.println("Program end");
    }

    @Override
    public void inAFuncDef(AFuncDef node){
        System.out.println("Func-def start");
    }

    @Override
    public void outAFuncDef(AFuncDef node){
        System.out.println("Func-def end");
    }

    @Override
    public void inAHeader(AHeader node){
        System.out.printf("Header start : <id> = %s <ret-type> = %s \n", node.getTId().toString(),node.getRetType().toString());
    }

    @Override
    public void outAHeader(AHeader node){
        System.out.printf("Header end : <id> = %s\n", node.getTId().toString());
    }

    @Override
    public void inAWithrefFparDef(AWithrefFparDef node)
    {
        System.out.println("Fpar-def start");
    }

    @Override
    public void outAWithrefFparDef(AWithrefFparDef node)
    {
        System.out.println("Fpar-def end");
    }

}
