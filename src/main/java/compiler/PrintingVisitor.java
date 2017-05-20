package compiler;
import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;
import java.io.*;
import java.util.*;

public class PrintingVisitor extends DepthFirstAdapter{
    int spaces=0;
    SymbolTable aSymbolTable = new SymbolTable();
    ArrayList<argument> temp_args;

    class type_info{
        int line,pos;
        String name;
        String Type;
        int array_max_dim;
        int array_cur_dim;
        boolean string_literal;
        type_info(int line,int pos, String name,String Type,int array_max_dim,int array_cur_dim,boolean string_literal){
            this.line=line;
            this.pos=pos;
            this.name=name;
            this.Type=Type;
            this.array_max_dim=array_max_dim;
            this.array_cur_dim=array_cur_dim;
            this.string_literal=string_literal;
        }
    }

    class fun_name_type{
        String name;
        String type;
        fun_name_type(String name,String type){
            this.name=name;
            this.type=type;
        }
    }
    String error;
    type_info int_type  = new type_info(0,0,"int","int",0,0,false);
    type_info char_type = new type_info(0,0,"char","char",0,0,false);
    type_info bool_type = new type_info(0,0,"bool","bool",0,0,false);

    int string_dim=1;
    ArrayList<type_info> type_stack = new ArrayList<type_info>();
    ArrayList<fun_name_type> function_stack = new ArrayList<fun_name_type>();

    private boolean check_both(String a,String b, String c1,String c2){
        if((a.equals(c1) && b.equals(c2)) || (b.equals(c1) && a.equals(c2) )){
            return true;
        }
        else{
            return false;
        }
    }

    private boolean equiv(type_info a, type_info b){
        int dims_dif_a=a.array_max_dim-a.array_cur_dim;
        int dims_dif_b=b.array_max_dim-b.array_cur_dim;
        if(dims_dif_a == dims_dif_b ){
            //if(dims_dif_a == 0 && dims_dif_b == 0){
                if (a.Type.equals(b.Type)){
                    return true;
                }
                else{
                    boolean integ,charac;
                    integ=check_both(a.Type,b.Type,"int","int_const");
                    charac=check_both(a.Type,b.Type,"char","char_const");
                    return (integ || charac);
                }
            //}
            //else{
                //return false;
            //}
        }
        return false;
    }

    //func-def
    @Override
    public void inAFuncDef(AFuncDef node){
        String fun_name;
        String Type;
        String ret_type;
        ArrayList<argument> temp_args = new ArrayList<argument>();
        int error;
        Type = "fun";
        fun_name = node.getTId().toString().replaceAll("\\s+","");
        ret_type = node.getRetType().toString().replaceAll("\\s+","");
        AFparDef node_f;
        List<PFparDef> copy = new ArrayList<PFparDef>(node.getFparDef());
        for(PFparDef e : copy)
        {
            node_f = (AFparDef) e;
            ArrayList<Integer> array_sizes;
            ArrayList<String> ids;
                    boolean ref = false;
            String TypeOfArg;
            argument arg;
            //REF
            if(node_f.getTRef() != null){
                ref=true;
            }
            AFparType node_fp = (AFparType) node_f.getFparType();
            //TYPE
            TypeOfArg = node_fp.getDataType().toString().replaceAll("\\s+","");
            //ARRAY
            array_sizes =  new ArrayList<Integer>();
            if(node_fp.getFparTypeTArEmpty() != null){
                array_sizes.add(-1);
            }
            List<TTNumber> array_copy = new ArrayList<TTNumber>(node_fp.getTNumber());
            for(TTNumber e_ar : array_copy)
            {
                array_sizes.add(Integer.parseInt(e_ar.toString().replaceAll("\\s+","")));
            }
            //IDS
            ids = new ArrayList<String>();
            List<TTId> id_copy = new ArrayList<TTId>(node_f.getTId());
            for(TTId id_e : id_copy)
            {
                ids.add(id_e.toString().replaceAll("\\s+",""));
            }
            arg = new argument(TypeOfArg,ids,array_sizes,ref);
            temp_args.add(arg);
        }
        error = aSymbolTable.insert(node.getTId().getLine(),node.getTId().getPos(),fun_name,Type,ret_type,false,null,temp_args,true);
        fun_name_type temp_fun_info= new fun_name_type(fun_name,ret_type);
        function_stack.add(temp_fun_info);
        if(error==1){
            //System.out.printf("Error (%d,%d) : \"%s\" < %s > has been redefined\n",node.getTId().getLine(),node.getTId().getPos(),fun_name,Type);
        }
        aSymbolTable.enter();
        for(PFparDef e : copy)
        {
            node_f = (AFparDef) e;
            ArrayList<Integer> array_sizes;
            ArrayList<String> ids;
            boolean ref = false;
            String TypeOfArg;
            //REF
            if(node_f.getTRef() != null){
                ref=true;
            }
            AFparType node_fp = (AFparType) node_f.getFparType();
            //TYPE
            TypeOfArg = node_fp.getDataType().toString().replaceAll("\\s+","");
            //ARRAY
            array_sizes =  new ArrayList<Integer>();
            if(node_fp.getFparTypeTArEmpty() != null){
                array_sizes.add(-1);
            }
            List<TTNumber> array_copy = new ArrayList<TTNumber>(node_fp.getTNumber());
            for(TTNumber e_ar : array_copy){
                array_sizes.add(Integer.parseInt(e_ar.toString().replaceAll("\\s+","")));
            }
            //IDS
            //System.out.printf("size %d\n",array_sizes.size());
            ids = new ArrayList<String>();
            List<TTId> id_copy = new ArrayList<TTId>(node_f.getTId());
            for(TTId id_e : id_copy){
                error=aSymbolTable.insert(id_e.getLine(),id_e.getPos(),id_e.toString().replaceAll("\\s+",""),TypeOfArg,"no",ref,array_sizes,null,false);
                if(error==1){
                    //System.out.printf("Error (%d,%d) : \"%s\" < %s > has been redefined\n",id_e.getLine(),id_e.getPos(),id_e.toString().replaceAll("\\s+",""),Type);
                }
            }
        }
    }

    @Override
    public void outAFuncDef(AFuncDef node){
        function_stack.remove(function_stack.size()-1);
        aSymbolTable.exit();
    }

    //func-decl
    @Override
    public void inAFuncDecl(AFuncDecl node){
        String fun_name;
        String Type;
        String ret_type;
        ArrayList<argument> temp_args = new ArrayList<argument>();
        Type = "fun";
        fun_name = node.getTId().toString().replaceAll("\\s+","");
        ret_type = node.getRetType().toString().replaceAll("\\s+","");
        AFparDef node_f;
        List<PFparDef> copy = new ArrayList<PFparDef>(node.getFparDef());
        for(PFparDef e : copy)
        {
            node_f = (AFparDef) e;
            ArrayList<Integer> array_sizes;
            ArrayList<String> ids;
            boolean ref = false;
            String TypeOfArg;
            argument arg;
            //REF
            if(node_f.getTRef() != null){
                ref=true;
            }
            AFparType node_fp = (AFparType) node_f.getFparType();
            //TYPE
            TypeOfArg = node_fp.getDataType().toString().replaceAll("\\s+","");
            //ARRAY
            array_sizes =  new ArrayList<Integer>();
            if(node_fp.getFparTypeTArEmpty() != null){
                array_sizes.add(-1);
            }
            List<TTNumber> array_copy = new ArrayList<TTNumber>(node_fp.getTNumber());
            for(TTNumber e_ar : array_copy)
            {
                array_sizes.add(Integer.parseInt(e_ar.toString().replaceAll("\\s+","")));
            }
            //IDS
            ids = new ArrayList<String>();
            List<TTId> id_copy = new ArrayList<TTId>(node_f.getTId());
            for(TTId id_e : id_copy)
            {
                ids.add(id_e.toString().replaceAll("\\s+",""));
            }
            //function
            arg = new argument(TypeOfArg,ids,array_sizes,ref);
            temp_args.add(arg);
        }
        int error;
        error=aSymbolTable.insert(node.getTId().getLine(),node.getTId().getPos(),fun_name,Type,ret_type,false,null,temp_args,false);
        if(error==1){
            //System.out.printf("Error (%d,%d) : \"%s\" < %s > has been redefined\n",node.getTId().getLine(),node.getTId().getPos(),fun_name,Type);
        }
    }

    @Override
    public void outAFuncDecl(AFuncDecl node){
    }

    //var-def
    @Override
    public void inAVarDef(AVarDef node)
    {
        //useful vars
        String Type;
        ArrayList<Integer> array_sizes;
        int index = 0;
        Type = node.getDataType().toString().replaceAll("\\s+","");
        List<TTNumber> array_copy = new ArrayList<TTNumber>(node.getTNumber());
        array_sizes=new ArrayList<Integer>();
        for(TTNumber e : array_copy)
        {
            array_sizes.add(Integer.parseInt(e.toString().replaceAll("\\s+","")));
        }
        List<TTId> id_copy = new ArrayList<TTId>(node.getTId());
        for(TTId e : id_copy){
            aSymbolTable.insert(e.getLine(),e.getPos(),e.toString().replaceAll("\\s+",""),Type,"no",false,array_sizes,null,false);
        }
        //System.out.printf("type : %s && array sizes:%s\n",Type,Arrays.toString(array_sizes.toArray()));
    }

    //EXPR

    @Override
    public void inAConstExpr(AConstExpr node)
    {
        type_info temp = new type_info(node.getTNumber().getLine(),node.getTNumber().getPos(),node.getTNumber().toString().replaceAll("\\s+",""),"int_const",0,0,false);
        type_stack.add(temp);
    }

    @Override
    public void inACharExpr(ACharExpr node)
    {
        type_info temp = new type_info(node.getTCharCon().getLine(),node.getTCharCon().getPos(),node.getTCharCon().toString().replaceAll("\\s+",""),"char_const",0,0,false);
        type_stack.add(temp);
    }

    @Override
    public void outAPlusExpr(APlusExpr node)
    {
        type_info left,right,temp;
        right = type_stack.remove(type_stack.size()-1);
        left  = type_stack.remove(type_stack.size()-1);
        if(!equiv(left,int_type) || !equiv(right,int_type)){
            if(!equiv(left,int_type)){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \"+\" operation",left.name,left.array_cur_dim,left.Type,left.array_max_dim);
                aSymbolTable.print_error(left.line,left.pos,error);
            }
            if(!equiv(right,int_type)){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \"+\" operation",right.name,right.array_cur_dim,right.Type,right.array_max_dim);
                aSymbolTable.print_error(right.line,right.pos,error);
            }
        }
        temp = new type_info(right.line,right.pos,right.name,"int_const",0,0,false);
        type_stack.add(temp);
    }

    @Override
    public void outAMinusExpr(AMinusExpr node)
    {
        type_info left,right,temp;
        right = type_stack.remove(type_stack.size()-1);
        left  = type_stack.remove(type_stack.size()-1);
        if(!equiv(left,int_type) || !equiv(right,int_type)){
            if(!equiv(left,int_type)){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \"-\" operation",left.name,left.array_cur_dim,left.Type,left.array_max_dim);
                aSymbolTable.print_error(left.line,left.pos,error);
            }
            if(!equiv(right,int_type)){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \"-\" operation",right.Type,right.array_cur_dim,right.Type,right.array_max_dim);
                aSymbolTable.print_error(right.line,right.pos,error);
            }
        }
        temp = new type_info(right.line,right.pos,right.name,"int_const",0,0,false);
        type_stack.add(temp);
    }

    @Override
    public void outAPostMultExpr(APostMultExpr node)
    {
        type_info left,right,temp;
        right = type_stack.remove(type_stack.size()-1);
        left  = type_stack.remove(type_stack.size()-1);
        if(!equiv(left,int_type) || !equiv(right,int_type)){
            if(!equiv(left,int_type)){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \"*\" operation",left.name,left.array_cur_dim,left.Type,left.array_max_dim);
                aSymbolTable.print_error(left.line,left.pos,error);
            }
            if(!equiv(right,int_type)){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \"*\" operation",right.name,right.array_cur_dim,right.Type,right.array_max_dim);
                aSymbolTable.print_error(right.line,right.pos,error);
            }
        }
        temp = new type_info(right.line,right.pos,right.name,"int_const",0,0,false);
        type_stack.add(temp);
    }

    @Override
    public void outAPostDivExpr(APostDivExpr node)
    {
        type_info left,right,temp;
        right = type_stack.remove(type_stack.size()-1);
        left  = type_stack.remove(type_stack.size()-1);
        if(!equiv(left,int_type) || !equiv(right,int_type)){
            if(!equiv(left,int_type)){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \"div\" operation",left.name,left.array_cur_dim,left.Type,left.array_max_dim);
                aSymbolTable.print_error(left.line,left.pos,error);
            }
            if(!equiv(right,int_type)){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \"div\" operation",right.name,right.array_cur_dim,right.Type,right.array_max_dim);
                aSymbolTable.print_error(right.line,right.pos,error);
            }
        }
        temp = new type_info(right.line,right.pos,right.name,"int_const",0,0,false);
        type_stack.add(temp);
    }

    @Override
    public void outAPostModExpr(APostModExpr node)
    {
        type_info left,right,temp;
        right = type_stack.remove(type_stack.size()-1);
        left  = type_stack.remove(type_stack.size()-1);
        if(!equiv(left,int_type) || !equiv(right,int_type)){
            if(!equiv(left,int_type)){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \"mod\" operation",left.name,left.array_cur_dim,left.Type,left.array_max_dim);
                aSymbolTable.print_error(left.line,left.pos,error);
            }
            if(!equiv(right,int_type)){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \"mod\" operation",right.name,right.array_cur_dim,right.Type,right.array_max_dim);
                aSymbolTable.print_error(right.line,right.pos,error);
            }
        }
        temp = new type_info(right.line,right.pos,right.name,"int_const",0,0,false);
        type_stack.add(temp);
    }

    //LVALUE

    @Override
    public void outALValueIdLValue(ALValueIdLValue node){
        int dims = node.getExpr().size();
        int pos2remove = type_stack.size() - dims;
        type_info array_index;
        SymbolTable.SymbolTableRecord aSymbol=aSymbolTable.lookup(node.getTId().toString().replaceAll("\\s+",""));
        if(aSymbol == null){
            error = String.format("id \"%s\" is not declared in this scope",node.getTId().toString().replaceAll("\\s+",""));
            aSymbolTable.print_error(node.getTId().getLine(),node.getTId().getPos(),error);
        }
        else{
            if(aSymbol.array_sizes.size()<dims){
                error = String.format("accessing dimension (%d) when id \"%s\" has (%d) ",dims,node.getTId().toString().replaceAll("\\s+",""),aSymbol.array_sizes.size());
                aSymbolTable.print_error(node.getTId().getLine(),node.getTId().getPos(),error);
            }
            //TODO TYPE check
        }
        for(int i=0; i < dims ; i++ ){
            array_index=type_stack.remove(pos2remove);
            if(!equiv(array_index,int_type)){
                error = String.format("%s <%s> is not accepted in array index (%d)",array_index.name,array_index.Type,i+1);
                aSymbolTable.print_error(array_index.line,array_index.pos,error);
            }
        }
        if(aSymbol!= null){
            type_info temp = new type_info(node.getTId().getLine(),node.getTId().getPos(),node.getTId().toString().replaceAll("\\s+",""),aSymbol.type,aSymbol.array_sizes.size(),dims,false);
            type_stack.add(temp);
        }
    }

    @Override
    public void outALValueStringLValue(ALValueStringLValue node)
    {
        int dims = node.getExpr().size();
        int pos2remove = type_stack.size() - dims;
        type_info array_index;
        if(string_dim<dims){
            error = String.format("accessing dimension (%d) when string \"%s\" has (%d) ",dims,node.getTString().toString().replaceAll("\\s+",""),string_dim);
            aSymbolTable.print_error(node.getTString().getLine(),node.getTString().getPos(),error);
        }
        for(int i=0; i < dims ; i++ ){
            array_index=type_stack.remove(pos2remove);
            if(!equiv(array_index,int_type)){
                error = String.format("%s <%s> is not accepted in array index (%d)",array_index.name,array_index.Type,i+1);
                aSymbolTable.print_error(array_index.line,array_index.pos,error);
            }
        }
        type_info temp = new type_info(node.getTString().getLine(),node.getTString().getPos(),node.getTString().toString().replaceAll("\\s+",""),"char",string_dim,dims,true);
        type_stack.add(temp);
    }

    //COND

    @Override
    public void outACondEqualCond(ACondEqualCond node)
    {
        type_info left,right,temp;
        right = type_stack.remove(type_stack.size()-1);
        left  = type_stack.remove(type_stack.size()-1);
        boolean a,b;
        a= !equiv(left,int_type) && !equiv(left,char_type);
        b= !equiv(right,int_type) && !equiv(right,char_type);
        if(a || b){
            if(a){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \"=\" operation",left.name,left.array_cur_dim,left.Type,left.array_max_dim);
                aSymbolTable.print_error(left.line,left.pos,error);
            }
            if(b){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \"=\" operation",right.name,right.array_cur_dim,right.Type,right.array_max_dim);
                aSymbolTable.print_error(right.line,right.pos,error);
            }
        }
        else{
            if(!equiv(left,right)){
                error = String.format("can't compare %s (%d) <%s> (%d) , %s (%d) <%s> (%d) (different types) in \"=\" operation",left.name,left.array_cur_dim,left.Type,left.array_max_dim,right.name,right.array_cur_dim,right.Type,right.array_max_dim);
                aSymbolTable.print_error(left.line,left.pos,error);
            }
        }
        temp = new type_info(right.line,right.pos,right.name,"bool",0,0,false);
        type_stack.add(temp);
    }

    @Override
    public void outACondHashCond(ACondHashCond node)
    {
        type_info left,right,temp;
        right = type_stack.remove(type_stack.size()-1);
        left  = type_stack.remove(type_stack.size()-1);
        boolean a,b;
        a= !equiv(left,int_type) && !equiv(left,char_type);
        b= !equiv(right,int_type) && !equiv(right,char_type);
        if(a || b){
            if(a){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \"#\" operation",left.name,left.array_cur_dim,left.Type,left.array_max_dim);
                aSymbolTable.print_error(left.line,left.pos,error);
            }
            if(b){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \"#\" operation",right.name,right.array_cur_dim,right.Type,right.array_max_dim);
                aSymbolTable.print_error(right.line,right.pos,error);
            }
        }
        else{
            if(!equiv(left,right)){
                error = String.format("can't compare %s (%d) <%s> (%d) , %s (%d) <%s> (%d) (different types) in \"#\" operation",left.name,left.array_cur_dim,left.Type,left.array_max_dim,right.name,right.array_cur_dim,right.Type,right.array_max_dim);
                aSymbolTable.print_error(left.line,left.pos,error);
            }
        }
        temp = new type_info(right.line,right.pos,right.name,"bool",0,0,false);
        type_stack.add(temp);
    }

    @Override
    public void outACondBiggerCond(ACondBiggerCond node)
    {
        type_info left,right,temp;
        right = type_stack.remove(type_stack.size()-1);
        left  = type_stack.remove(type_stack.size()-1);
        boolean a,b;
        a= !equiv(left,int_type) && !equiv(left,char_type);
        b= !equiv(right,int_type) && !equiv(right,char_type);
        if(a || b){
            if(a){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \">\" operation",left.name,left.array_cur_dim,left.Type,left.array_max_dim);
                aSymbolTable.print_error(left.line,left.pos,error);
            }
            if(b){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \">\" operation",right.name,right.array_cur_dim,right.Type,right.array_max_dim);
                aSymbolTable.print_error(right.line,right.pos,error);
            }
        }
        else{
            if(!equiv(left,right)){
                error = String.format("can't compare %s (%d) <%s> (%d) , %s (%d) <%s> (%d) (different types) in \">\" operation",left.name,left.array_cur_dim,left.Type,left.array_max_dim,right.name,right.array_cur_dim,right.Type,right.array_max_dim);
                aSymbolTable.print_error(left.line,left.pos,error);
            }
        }
        temp = new type_info(right.line,right.pos,right.name,"bool",0,0,false);
        type_stack.add(temp);
    }

    @Override
    public void outACondLessCond(ACondLessCond node)
    {
        type_info left,right,temp;
        right = type_stack.remove(type_stack.size()-1);
        left  = type_stack.remove(type_stack.size()-1);
        boolean a,b;
        a= !equiv(left,int_type) && !equiv(left,char_type);
        b= !equiv(right,int_type) && !equiv(right,char_type);
        if(a || b){
            if(a){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \"<\" operation",left.name,left.array_cur_dim,left.Type,left.array_max_dim);
                aSymbolTable.print_error(left.line,left.pos,error);
            }
            if(b){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \"<\" operation",right.name,right.array_cur_dim,right.Type,right.array_max_dim);
                aSymbolTable.print_error(right.line,right.pos,error);
            }
        }
        else{
            if(!equiv(left,right)){
                error = String.format("can't compare %s (%d) <%s> (%d) , %s (%d) <%s> (%d) (different types) in \"<\" operation",left.name,left.array_cur_dim,left.Type,left.array_max_dim,right.name,right.array_cur_dim,right.Type,right.array_max_dim);
                aSymbolTable.print_error(left.line,left.pos,error);
            }
        }
        temp = new type_info(right.line,right.pos,right.name,"bool",0,0,false);
        type_stack.add(temp);
    }

    @Override
    public void outACondLeqCond(ACondLeqCond node)
    {
        type_info left,right,temp;
        right = type_stack.remove(type_stack.size()-1);
        left  = type_stack.remove(type_stack.size()-1);
        boolean a,b;
        a= !equiv(left,int_type) && !equiv(left,char_type);
        b= !equiv(right,int_type) && !equiv(right,char_type);
        if(a || b){
            if(a){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \"<=\" operation",left.name,left.array_cur_dim,left.Type,left.array_max_dim);
                aSymbolTable.print_error(left.line,left.pos,error);
            }
            if(b){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \"<=\" operation",right.name,right.array_cur_dim,right.Type,right.array_max_dim);
                aSymbolTable.print_error(right.line,right.pos,error);
            }
        }
        else{
            if(!equiv(left,right)){
                error = String.format("can't compare %s (%d) <%s> (%d) , %s (%d) <%s> (%d) (different types) in \"<=\" operation",left.name,left.array_cur_dim,left.Type,left.array_max_dim,right.name,right.array_cur_dim,right.Type,right.array_max_dim);
                aSymbolTable.print_error(left.line,left.pos,error);
            }
        }
        temp = new type_info(right.line,right.pos,right.name,"bool",0,0,false);
        type_stack.add(temp);
    }

    @Override
    public void outACondBeqCond(ACondBeqCond node)
    {
        type_info left,right,temp;
        right = type_stack.remove(type_stack.size()-1);
        left  = type_stack.remove(type_stack.size()-1);
        boolean a,b;
        a= !equiv(left,int_type) && !equiv(left,char_type);
        b= !equiv(right,int_type) && !equiv(right,char_type);
        if(a || b){
            if(a){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \">=\" operation",left.name,left.array_cur_dim,left.Type,left.array_max_dim);
                aSymbolTable.print_error(left.line,left.pos,error);
            }
            if(b){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \">=\" operation",right.name,right.array_cur_dim,right.Type,right.array_max_dim);
                aSymbolTable.print_error(right.line,right.pos,error);
            }
        }
        else{
            if(!equiv(left,right)){
                error = String.format("can't compare %s (%d) <%s> (%d) , %s (%d) <%s> (%d) (different types) in \">=\" operation",left.name,left.array_cur_dim,left.Type,left.array_max_dim,right.name,right.array_cur_dim,right.Type,right.array_max_dim);
                aSymbolTable.print_error(left.line,left.pos,error);
            }
        }
        temp = new type_info(right.line,right.pos,right.name,"bool",0,0,false);
        type_stack.add(temp);
    }

    //@Override
    //public void outACondnotNotCond(ACondnotNotCond node)
    //{
        //type_info cond,temp;
        //cond= type_stack.remove(type_stack.size()-1);
        //boolean a;
        //a= !equiv(cond,bool_type);
        //if(a){
            //error = String.format("%s (%d) <%s> (%d) is not accepted in \">=\" operation",cond.name,cond.array_cur_dim,cond.Type,cond.array_max_dim);
            //aSymbolTable.print_error(cond.line,cond.pos,error);
        //}
        //temp = new type_info(cond.line,cond.pos,cond.name,"bool",0,0,false);
        //type_stack.add(temp);
    //}

    //STMT

    @Override
    public void outAStmtLvalueStmt(AStmtLvalueStmt node)
    {
        type_info assign,expr,temp;
        expr = type_stack.remove(type_stack.size()-1);
        assign  = type_stack.remove(type_stack.size()-1);
        boolean a,b;
        a= !equiv(expr,int_type) && !equiv(expr,char_type);
        b= !equiv(assign,int_type) && !equiv(assign,char_type);
        if(a || b){
            if(a){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \"<-\" operation",expr.name,expr.array_cur_dim,expr.Type,expr.array_max_dim);
                aSymbolTable.print_error(expr.line,expr.pos,error);
            }
            if(b){
                error = String.format("%s (%d) <%s> (%d) is not accepted in \"<-\" operation",assign.name,assign.array_cur_dim,assign.Type,assign.array_max_dim);
                aSymbolTable.print_error(assign.line,assign.pos,error);
            }
        }
        else{
            if(!equiv(assign,expr)){
                error = String.format("can't assign %s (%d) <%s> (%d) , %s (%d) <%s> (%d) (different types) in \"<-\" operation",expr.name,expr.array_cur_dim,expr.Type,expr.array_max_dim,assign.name,assign.array_cur_dim,assign.Type,assign.array_max_dim);
                aSymbolTable.print_error(assign.line,assign.pos,error);
            }
        }
    }

    @Override
    public void outAFuncCall(AFuncCall node){
        SymbolTable.SymbolTableRecord foundSymbol=aSymbolTable.lookup(node.getTId().toString().replaceAll("\\s+",""));
        String name= node.getTId().toString().replaceAll("\\s+","");
        if(foundSymbol == null){
            error = String.format("id \"%s\" is not declared in this scope",node.getTId().toString().replaceAll("\\s+",""));
            aSymbolTable.print_error(node.getTId().getLine(),node.getTId().getPos(),error);
        }
        else{
            if(!foundSymbol.type.equals( "fun")){
                error = String.format("id \"%s\" <%s> (%d) is not a functione",node.getTId().toString().replaceAll("\\s+",""),foundSymbol.type,foundSymbol.array_sizes.size());
                aSymbolTable.print_error(node.getTId().getLine(),node.getTId().getPos(),error);
            }
            int number_of_args=0;
            for(argument e : foundSymbol.arg_types ){
                number_of_args+=e.ids.size();
            }
            if (node.getExpr().size() != number_of_args){
                error=String.format("function \"%s\" call doesn't match function header (l:%d,p:%d)",name,foundSymbol.line,foundSymbol.pos);
                aSymbolTable.print_error(node.getTId().getLine(),node.getTId().getPos(),error);
            }
            else{
                int number2remove=node.getExpr().size();
                int pos2remove = type_stack.size() - number2remove;
                int arg_index = 0,arg_elements=0;
                argument temp;
                type_info arg,argf;
                for(int i=0; i < number2remove ; i++ ){
                    arg = type_stack.remove(pos2remove);
                    temp = foundSymbol.arg_types.get(arg_index);
                    argf = new type_info(0,0,temp.Type,temp.Type,temp.array_sizes.size(),0,false);
                    if(!equiv(arg,argf)){
                        error = String.format("expected <%s> (%d) ,found <%s> (%d) in argument (%d) of function \"%s\"",argf.Type,argf.array_max_dim-argf.array_cur_dim,arg.Type,arg.array_max_dim-arg.array_cur_dim,i+1,arg.name);
                        aSymbolTable.print_error(arg.line,arg.pos,error);
                    }
                    arg_elements++;
                    if(arg_elements == foundSymbol.arg_types.get(arg_index).ids.size()){
                        arg_elements=0;
                        arg_index++;
                    }
                }
                type_info return_type = new type_info(node.getTId().getLine(),node.getTId().getPos(),node.getTId().toString().replaceAll("\\s+",""),foundSymbol.ret_type,0,0,false);
                type_stack.add(return_type);
            }
        }
    }

    //return

    @Override
    public void outAStmtReturnStmt(AStmtReturnStmt node)
    {
        type_info expr;
        fun_name_type temp_fun_info;
        temp_fun_info= function_stack.get(function_stack.size()-1);
        if(temp_fun_info.type.equals("nothing")){
            if(node.getExpr()!= null){
                expr = type_stack.remove(type_stack.size()-1);
                error = String.format("returning %s (%d) <%s> (%d) in function \"%s\" that has return type <nothing>",expr.name,expr.array_cur_dim,expr.Type,expr.array_max_dim,temp_fun_info.name);
                aSymbolTable.print_error(expr.line,expr.pos,error);
            }
        }
        else{
            if(node.getExpr()!= null){
                expr = type_stack.remove(type_stack.size()-1);
                if(temp_fun_info.type.equals("int") && !equiv(expr,int_type)){
                    error = String.format("returning %s (%d) <%s> (%d) in function \"%s\" that has return type <int> ",expr.name,expr.array_cur_dim,expr.Type,expr.array_max_dim,temp_fun_info.name);
                    aSymbolTable.print_error(expr.line,expr.pos,error);
                }
                else if(temp_fun_info.type.equals("char") && !equiv(expr,char_type)){
                    error = String.format("returning %s (%d) <%s> (%d) in function \"%s\" that has return type <char> ",expr.name,expr.array_cur_dim,expr.Type,expr.array_max_dim,temp_fun_info.name);
                    aSymbolTable.print_error(expr.line,expr.pos,error);
                }
            }
        }
    }

}

