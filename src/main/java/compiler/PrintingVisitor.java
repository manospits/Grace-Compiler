
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
        List<PIdComma> copy = new ArrayList<PIdComma>(node.getIdComma());
        for(PIdComma e : copy)
        {
            System.out.printf("%s",e.toString());
        }
        System.out.println("");
        print_spaces();
        System.out.printf("<fpar-type> : %s \n",node.getFparType().toString());
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
        print_spaces();
        System.out.printf("<ids>  : %s", node.getTId().toString());
        List<PIdComma> copy = new ArrayList<PIdComma>(node.getIdComma());
        for(PIdComma e : copy)
        {
            System.out.printf("%s",e.toString());
        }
        System.out.println("");
        print_spaces();
        System.out.printf("<type> : %s \n",node.getType().toString());
    }

    @Override
    public void outAVarDef(AVarDef node)
    {
        spaces-=1;
        print_spaces();
        System.out.println("Var-def end");
    }

    //block
    @Override
    public void inABlock(ABlock node){
        print_spaces();
        System.out.println("Block start");
        spaces+=1;
    }

    @Override
    public void outABlock(ABlock node){
        spaces-=1;
        print_spaces();
        System.out.println("Block end");
    }

    //func call
    @Override
    public void inAFuncCall(AFuncCall node){
        print_spaces();
        System.out.println("Func-call start");
        spaces+=1;
        print_spaces();
        System.out.printf("Function <id> : %s\n",node.getTId().toString());
    }

    @Override
    public void outAFuncCall(AFuncCall node){
        spaces-=1;
        print_spaces();
        System.out.println("Func-call end");
    }

    //stmt Assignment
    @Override
    public void inAStmtLvalueStmt(AStmtLvalueStmt node){
        print_spaces();
        spaces+=1;
        System.out.println("Assignment");
        print_spaces();
        System.out.printf("<l-value> : %s\n",node.getLValue().toString());
    }

    @Override
    public void outAStmtLvalueStmt(AStmtLvalueStmt node){
        spaces-=1;
        print_spaces();
        System.out.printf("Assignment end\n");
    }

    //stmt-else Assignment
    @Override
    public void inAStmtElseLvalueStmtElse(AStmtElseLvalueStmtElse node){
        print_spaces();
        spaces+=1;
        System.out.println("Stmt : Assignment");
        print_spaces();
        System.out.printf("<l-value> : %s\n",node.getLValue().toString());
    }

    @Override
    public void outAStmtElseLvalueStmtElse(AStmtElseLvalueStmtElse node){
        spaces-=1;
        print_spaces();
        System.out.printf("Assignment end\n");
    }

    //if
    @Override
    public void inAIf(AIf node){
        print_spaces();
        System.out.println("If starting");
        spaces+=1;
    }

    @Override
    public void outAIf(AIf node){
        spaces-=1;
        print_spaces();
        System.out.printf("If end\n");
    }

    //func call optional
    @Override
    public void inAFuncCallOptional(AFuncCallOptional node)
    {
        print_spaces();
        System.out.println("Parameters :");
        spaces+=1;
    }

    @Override
    public void outAFuncCallOptional(AFuncCallOptional node)
    {
        spaces-=1;
        print_spaces();
        System.out.printf("Parameters end\n");
    }

    //if header
    @Override
    public void inAIfHeader(AIfHeader node){
        print_spaces();
        System.out.println("If header start");
        spaces+=1;
    }

    @Override
    public void outAIfHeader(AIfHeader node){
        spaces-=1;
        print_spaces();
        System.out.println("If header end");
    }

    //if trail without else
    @Override
    public void inAWithoutElseIfTrail(AWithoutElseIfTrail node){
        print_spaces();
        System.out.println("If trail start (without else)");
        spaces+=1;
    }

    @Override
    public void outAWithoutElseIfTrail(AWithoutElseIfTrail node){
        spaces-=1;
        print_spaces();
        System.out.println("If trail end (without else)");
    }

    //if trail with else
    @Override
    public void inAWithElseIfTrail(AWithElseIfTrail node){
        print_spaces();
        System.out.println("If trail start (with else)");
        spaces+=1;
    }

    @Override
    public void outAWithElseIfTrail(AWithElseIfTrail node){
        spaces-=1;
        print_spaces();
        System.out.println("If trail end (with else)");
    }

    @Override
    public void inAPlusExpr(APlusExpr node){
        print_spaces();
        System.out.println("Expr type:plus start");
        spaces+=1;
    }

    @Override
    public void outAPlusExpr(APlusExpr node){
        spaces-=1;
        print_spaces();
        System.out.println("Expr type:plus end");
    }

    @Override
    public void inAMinusExpr(AMinusExpr node){
        print_spaces();
        System.out.println("Expr type:minus start");
        spaces+=1;
    }

    @Override
    public void outAMinusExpr(AMinusExpr node){
        spaces-=1;
        print_spaces();
        System.out.println("Expr type:minus end");
    }

    @Override
    public void inAPostMultExprM(APostMultExprM node){
        print_spaces();
        System.out.println("Expr_m type:mult start");
        spaces+=1;
    }

    @Override
    public void outAPostMultExprM(APostMultExprM node){
        spaces-=1;
        print_spaces();
        System.out.println("Expr_m type:mult end");
    }

    @Override
    public void inAPostDivExprM(APostDivExprM node){
        print_spaces();
        System.out.println("Expr_m type:div start");
        spaces+=1;
    }

    @Override
    public void outAPostDivExprM(APostDivExprM node){
        spaces-=1;
        print_spaces();
        System.out.println("Expr_m type:div end");
    }

    @Override
    public void inAPostModExprM(APostModExprM node){
        print_spaces();
        System.out.println("Expr_m type:mod start");
        spaces+=1;
    }

    @Override
    public void outAPostModExprM(APostModExprM node){
        spaces-=1;
        print_spaces();
        System.out.println("Expr_m type:mod end");
    }

    @Override
    public void inAInplusExprIpm(AInplusExprIpm node){
        print_spaces();
        System.out.println("Expr_ipm type:infix-plus start");
        spaces+=1;
    }

    @Override
    public void outAInminusExprIpm(AInminusExprIpm node){
        spaces-=1;
        print_spaces();
        System.out.println("Expr_ipm type:infix-minus end");
    }

    @Override
    public void inAInminusExprIpm(AInminusExprIpm node){
        print_spaces();
        System.out.println("Expr_ipm type:infix-minus start");
        spaces+=1;
    }

    @Override
    public void outAInplusExprIpm(AInplusExprIpm node){
        spaces-=1;
        print_spaces();
        System.out.println("Expr_ipm type:infix-minus end");
    }

    @Override
    public void inAConstExprBottom(AConstExprBottom node){
        print_spaces();
        System.out.println("Expr_bottom type:int-const start");
        spaces+=1;
        print_spaces();
        System.out.printf("<int-const> : %s\n",node.getTNumber().toString() );
    }

    @Override
    public void outAConstExprBottom(AConstExprBottom node){
        spaces-=1;
        print_spaces();
        System.out.println("Expr_bottom type:int-const end");
    }

    @Override
    public void inACharExprBottom(ACharExprBottom node){
        print_spaces();
        System.out.println("Expr_botom type:char-const start");
        spaces+=1;
        print_spaces();
        System.out.printf("<char-const> : %s\n",node.getTCharCon().toString());
    }

    @Override
    public void outACharExprBottom(ACharExprBottom node){
        spaces-=1;
        print_spaces();
        System.out.println("Expr_bottom type:char-const end");
    }

    @Override
    public void inAFuncCallTypeExprBottom(AFuncCallTypeExprBottom node){
        print_spaces();
        System.out.println("Expr_botom type:func-call start");
        spaces+=1;
    }

    @Override
    public void outAFuncCallTypeExprBottom(AFuncCallTypeExprBottom node){
        spaces-=1;
        print_spaces();
        System.out.println("Expr_bottom type:func-call end");
    }

    @Override
    public void inALValueTypeExprBottom(ALValueTypeExprBottom node)
    {
        print_spaces();
        System.out.println("Expr_botom type:l-value start");
        spaces+=1;
    }

    @Override
    public void outALValueTypeExprBottom(ALValueTypeExprBottom node)
    {
        spaces-=1;
        print_spaces();
        System.out.println("Expr_bottom type:l-value end");
    }

    @Override
    public void inAParenthesisExprBottom(AParenthesisExprBottom node)
    {
        print_spaces();
        System.out.println("Expr_botom type:parenthesis start");
        spaces+=1;
    }

    @Override
    public void outAParenthesisExprBottom(AParenthesisExprBottom node)
    {
        spaces-=1;
        print_spaces();
        System.out.println("Expr_bottom type:parethesis end");
    }

}
