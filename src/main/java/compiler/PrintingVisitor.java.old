
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
        List<PIdComma> copy = new ArrayList<PIdComma>(node.getIdComma());
        for(PIdComma e : copy)
        {
            System.out.printf("%s",e.toString());
        }
        System.out.println("");
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
        print_spaces();
        System.out.printf("<data-type> : %s \n",node.getDataType().toString());
        List<PFparTypeTAr> copy = new ArrayList<PFparTypeTAr>(node.getFparTypeTAr());
        if(node.getFparTypeTArEmpty() != null || !copy.isEmpty()){
            print_spaces();
            System.out.printf("Array indexes :");
            if(node.getFparTypeTArEmpty() != null){
                System.out.printf("%s",node.getFparTypeTArEmpty().toString());
            }
            for(PFparTypeTAr e : copy){
                System.out.printf("%s",e.toString());
            }
            System.out.println("");
        }
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

    //semi
    @Override
    public void inAStmtSemiStmt(AStmtSemiStmt node) {
        print_spaces();
        spaces+=1;
        System.out.println("stmt type:Semi");
    }

    @Override
    public void outAStmtSemiStmt(AStmtSemiStmt node) {
        spaces-=1;
    }

    @Override
    public void inAStmtLvalueStmt(AStmtLvalueStmt node){
        print_spaces();
        spaces+=1;
        System.out.println("stmt type:assignment start");
        print_spaces();
        System.out.printf("<l-value> : %s\n",node.getLValue().toString());
    }

    @Override
    public void outAStmtLvalueStmt(AStmtLvalueStmt node){
        spaces-=1;
        print_spaces();
        System.out.printf("stmt type:assignment end\n");
    }

    @Override
    public void inAStmtBlockStmt(AStmtBlockStmt node){
        print_spaces();
        spaces+=1;
        System.out.println("stmt type:block start");
    }

    @Override
    public void outAStmtBlockStmt(AStmtBlockStmt node){
        spaces-=1;
        print_spaces();
        System.out.printf("stmt type:block end\n");
    }

    @Override
    public void inAStmtFuncCallStmt(AStmtFuncCallStmt node){
        print_spaces();
        spaces+=1;
        System.out.println("stmt type:func-call start");
    }

    @Override
    public void outAStmtFuncCallStmt(AStmtFuncCallStmt node){
        spaces-=1;
        print_spaces();
        System.out.printf("stmt type:func-call end\n");
    }

    @Override
    public void inAStmtIfStmt(AStmtIfStmt node){
        print_spaces();
        spaces+=1;
        System.out.println("stmt type:if start");
    }

    @Override
    public void outAStmtIfStmt(AStmtIfStmt node){
        spaces-=1;
        print_spaces();
        System.out.printf("stmt type:if end\n");
    }

    @Override
    public void inAStmtReturnStmt(AStmtReturnStmt node){
        print_spaces();
        spaces+=1;
        System.out.println("stmt type:return expr? semi start");
    }

    @Override
    public void outAStmtReturnStmt(AStmtReturnStmt node){
        spaces-=1;
        print_spaces();
        System.out.printf("stmt type:return expr? semi end\n");
    }

    @Override
    public void inAStmtWhileStmt(AStmtWhileStmt node){
        print_spaces();
        spaces+=1;
        System.out.println("stmt type:while start");
    }

    @Override
    public void outAStmtWhileStmt(AStmtWhileStmt node){
        spaces-=1;
        print_spaces();
        System.out.printf("stmt type:while end\n");
    }

    //stmt-else
    @Override
    public void inAStmtElseLvalueStmtElse(AStmtElseLvalueStmtElse node){
        print_spaces();
        spaces+=1;
        System.out.println("stmt (else) type:assignment start");
        print_spaces();
        System.out.printf("<l-value> : %s\n",node.getLValue().toString());
    }

    @Override
    public void outAStmtElseLvalueStmtElse(AStmtElseLvalueStmtElse node){
        spaces-=1;
        print_spaces();
        System.out.printf("stmt (else) type:assignment end\n");
    }

    @Override
    public void inAStmtElseBlockStmtElse(AStmtElseBlockStmtElse node){
        print_spaces();
        spaces+=1;
        System.out.println("stmt (else) type:block start");
    }

    @Override
    public void outAStmtElseBlockStmtElse(AStmtElseBlockStmtElse node){
        spaces-=1;
        print_spaces();
        System.out.printf("stmt (else) type:block end\n");
    }

    @Override
    public void inAStmtElseFuncCallStmtElse(AStmtElseFuncCallStmtElse node){
        print_spaces();
        spaces+=1;
        System.out.println("stmt (else) type:func-call start");
    }

    @Override
    public void outAStmtElseFuncCallStmtElse(AStmtElseFuncCallStmtElse node){
        spaces-=1;
        print_spaces();
        System.out.printf("stmt (else) type:func-call end\n");
    }

    @Override
    public void inAStmtElseIfStmtElse(AStmtElseIfStmtElse node){
        print_spaces();
        spaces+=1;
        System.out.println("stmt (else) type:if start");
    }

    @Override
    public void outAStmtElseIfStmtElse(AStmtElseIfStmtElse node){
        spaces-=1;
        print_spaces();
        System.out.printf("stmt (else) type:if end\n");
    }

    @Override
    public void inAStmtElseReturnStmtElse(AStmtElseReturnStmtElse node){
        print_spaces();
        spaces+=1;
        System.out.println("stmt (else) type:return expr? semi start");
    }

    @Override
    public void outAStmtElseReturnStmtElse(AStmtElseReturnStmtElse node){
        spaces-=1;
        print_spaces();
        System.out.printf("stmt (else) type:return expr? semi end\n");
    }

    @Override
    public void inAStmtElseWhileStmtElse(AStmtElseWhileStmtElse node){
        print_spaces();
        spaces+=1;
        System.out.println("stmt (else) type:While start");
    }

    @Override
    public void outAStmtElseWhileStmtElse(AStmtElseWhileStmtElse node){
        spaces-=1;
        print_spaces();
        System.out.printf("stmt (else) type:while end\n");
    }

    //while
    @Override
    public void inAWhile(AWhile node) {
        print_spaces();
        spaces+=1;
        System.out.println("While start");
    }

    @Override
    public void outAWhile(AWhile node) {
        spaces-=1;
        print_spaces();
        System.out.printf("While end\n");
    }

    @Override
    public void inAWhileElse(AWhileElse node) {
        print_spaces();
        spaces+=1;
        System.out.println("While start (while-else)");
    }

    @Override
    public void outAWhileElse(AWhileElse node) {
        spaces-=1;
        print_spaces();
        System.out.printf("While end (while-else)\n");
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

    //lvalue
    @Override
    public void inALValueIdLValue(ALValueIdLValue node)
    {
        print_spaces();
        System.out.println("l-value type:id start");
        spaces+=1;
        print_spaces();
        System.out.printf("<id> : %s\n",node.getTId().toString());
    }

    @Override
    public void outALValueIdLValue(ALValueIdLValue node)
    {
        spaces-=1;
        print_spaces();
        System.out.println("l-value type:id end");
    }

    @Override
    public void inALValueStringLValue(ALValueStringLValue node)
    {
        print_spaces();
        System.out.println("l-value type:string start");
        spaces+=1;
        print_spaces();
        System.out.printf("<string> : %s\n",node.getTString().toString());
    }

    @Override
    public void outALValueStringLValue(ALValueStringLValue node)
    {
        spaces-=1;
        print_spaces();
        System.out.println("l-value type:string end");
    }

    @Override
    public void inALValueArrayLValue(ALValueArrayLValue node)
    {
        print_spaces();
        System.out.println("l-value type:l-value [] start");
        spaces+=1;
    }

    @Override
    public void outALValueArrayLValue(ALValueArrayLValue node)
    {
        spaces-=1;
        print_spaces();
        System.out.println("l-value type:l-value [] end");
    }

    //expr
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

    //expr_m
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

    //Expr_ipm
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

    //Expr_bottom
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

    //cond
    @Override
    public void inACondOrCond(ACondOrCond node){
        print_spaces();
        System.out.println("Cond type:or start");
        spaces+=1;
    }

    @Override
    public void outACondOrCond(ACondOrCond node){
        spaces-=1;
        print_spaces();
        System.out.println("Cond type:or end");
    }

    @Override
    public void inACondandAndCondand(ACondandAndCondand node){
        print_spaces();
        System.out.println("Condand type:and start");
        spaces+=1;
    }

    @Override
    public void outACondandAndCondand(ACondandAndCondand node){
        spaces-=1;
        print_spaces();
        System.out.println("Condand type:and end");
    }

    @Override
    public void inACondnotNotCondnot(ACondnotNotCondnot node){
        print_spaces();
        System.out.println("Condnot type:not start");
        spaces+=1;
    }

    @Override
    public void outACondnotNotCondnot(ACondnotNotCondnot node){
        spaces-=1;
        print_spaces();
        System.out.println("Condnot type:not end");
    }

    @Override
    public void inACondEqualCondBottom(ACondEqualCondBottom node) {
        print_spaces();
        System.out.println("Condbottom type:equal start");
        spaces+=1;
    }

    @Override
    public void outACondEqualCondBottom(ACondEqualCondBottom node) {
        spaces-=1;
        print_spaces();
        System.out.println("Condbottom type:equal end");
    }

    @Override
    public void inACondHashCondBottom(ACondHashCondBottom node) {
        print_spaces();
        System.out.println("Condbottom type:hash start");
        spaces+=1;
    }

    @Override
    public void outACondHashCondBottom(ACondHashCondBottom node) {
        spaces-=1;
        print_spaces();
        System.out.println("Condbottom type:hash end");
    }

    @Override
    public void inACondBiggerCondBottom(ACondBiggerCondBottom node) {
        print_spaces();
        System.out.println("Condbottom type:bigger start");
        spaces+=1;
    }

    @Override
    public void outACondBiggerCondBottom(ACondBiggerCondBottom node) {
        spaces-=1;
        print_spaces();
        System.out.println("Condbottom type:bigger end");
    }

    @Override
    public void inACondLessCondBottom(ACondLessCondBottom node) {
        print_spaces();
        System.out.println("Condbottom type:less start");
        spaces+=1;
    }

    @Override
    public void outACondLessCondBottom(ACondLessCondBottom node) {
        spaces-=1;
        print_spaces();
        System.out.println("Condbottom type:less end");
    }

    @Override
    public void inACondLeqCondBottom(ACondLeqCondBottom node) {
        print_spaces();
        System.out.println("Condbottom type:leq start");
        spaces+=1;
    }

    @Override
    public void outACondLeqCondBottom(ACondLeqCondBottom node) {
        spaces-=1;
        print_spaces();
        System.out.println("Condbottom type:leq end");
    }

    @Override
    public void inACondBeqCondBottom(ACondBeqCondBottom node) {
        print_spaces();
        System.out.println("Condbottom type:beq start");
        spaces+=1;
    }

    @Override
    public void outACondBeqCondBottom(ACondBeqCondBottom node) {
        spaces-=1;
        print_spaces();
        System.out.println("Condbottom type:beq end");
    }

    @Override
    public void inACondParCondBottom(ACondParCondBottom node) {
        print_spaces();
        System.out.println("Condbottom type:parenthesis start");
        spaces+=1;
    }

    @Override
    public void outACondParCondBottom(ACondParCondBottom node) {
        spaces-=1;
        print_spaces();
        System.out.println("Condbottom type:parenthesis end");
    }
}
