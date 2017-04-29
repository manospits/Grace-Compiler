
package compiler;
import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;
import java.io.*;
import java.util.*;

public class PrintingVisitor extends DepthFirstAdapter{
    int spaces=0;

    private void print_spaces(){
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
        System.out.println("Header start");
        spaces+=1;
        print_spaces();
        System.out.printf("<id> = %s\n", node.getTId().toString());
        print_spaces();
        System.out.printf("<ret-type> = %s \n",node.getRetType().toString());
    }

    @Override
    public void outAHeader(AHeader node){
        spaces-=1;
        print_spaces();
        //System.out.printf("Header end : <id> = %s\n", node.getTId().toString());
        System.out.println("Header end");
    }

    //fpar-def
    @Override
    public void inAFparDef(AFparDef node){
        print_spaces();
        System.out.println("Fpar-def start");
        spaces+=1;
        print_spaces();
        System.out.printf("<ref> : ");
        if(node.getTRef() != null)
        {
            System.out.printf("Yes\n");
        }
        else{
            System.out.printf("No\n");

        }
        print_spaces();
        System.out.printf("<ids> : %s", node.getTId().toString());
    }

    @Override
    public void outAFparDef(AFparDef node){
        spaces-=1;
        print_spaces();
        System.out.println("Fpar-def end");
    }

    @Override
    public void inAFparType(AFparType node) {
        print_spaces();
        System.out.println("Fpar-type start");
        spaces+=1;
     }

    @Override
    public void outAFparType(AFparType node)
    {
        spaces-=1;
        print_spaces();
        System.out.println("Fpar-type end");
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

    //func-decl
    @Override
    public void inAFuncDecl(AFuncDecl node){
        print_spaces();
        System.out.println("Func-decl start");
        spaces+=1;
    }

    @Override
    public void outAFuncDecl(AFuncDecl node){
        spaces-=1;
        print_spaces();
        System.out.println("Func-decl end");
    }

    //var-def
    @Override
    public void inAVarDef(AVarDef node)
    {
        print_spaces();
        System.out.println("Var-def start");
        spaces+=1;
    }

    @Override
    public void outAVarDef(AVarDef node)
    {
        spaces-=1;
        print_spaces();
        System.out.println("Var-def end");
    }

    @Override
    public void inAStmtIfStmt(AStmtIfStmt node)
    {
        print_spaces();
        System.out.println("Stmt if start");
        spaces+=1;
        List<PStmt> copy = new ArrayList<PStmt>(node.getStmt());
        for(PStmt e : copy)
        {
            print_spaces();
            System.out.printf("if stmt");
        }

    }

    @Override
    public void outAStmtIfStmt(AStmtIfStmt node)
    {
        spaces-=1;
        print_spaces();
        System.out.println("Stmt if end");

    }

   }
