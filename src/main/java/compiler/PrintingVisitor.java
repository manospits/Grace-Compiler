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

    String error;
    type_info int_type = new type_info(0,0,"int","int",0,0,false);
    type_info char_type = new type_info(0,0,"char","char",0,0,false);

    ArrayList<type_info> type_stack = new ArrayList<type_info>();

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
                error = String.format("%s <%s> is not accepted in \"+\" operation",left.name,left.Type);
                aSymbolTable.print_error(left.line,left.pos,error);
            }
            if(!equiv(right,int_type)){
                error = String.format("%s <%s> is not accepted in \"+\" operation",right.name,right.Type);
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
                error = String.format("%s <%s> is not accepted in \"-\" operation",left.name,left.Type);
                aSymbolTable.print_error(left.line,left.pos,error);
            }
            if(!equiv(right,int_type)){
                error = String.format("%s <%s> is not accepted in \"-\" operation",right.name,right.Type);
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
                error = String.format("%s <%s> is not accepted in \"*\" operation",left.name,left.Type);
                aSymbolTable.print_error(left.line,left.pos,error);
            }
            if(!equiv(right,int_type)){
                error = String.format("%s <%s> is not accepted in \"*\" operation",right.name,right.Type);
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
                error = String.format("%s <%s> is not accepted in \"div\" operation",left.name,left.Type);
                aSymbolTable.print_error(left.line,left.pos,error);
            }
            if(!equiv(right,int_type)){
                error = String.format("%s <%s> is not accepted in \"div\" operation",right.name,right.Type);
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
                error = String.format("%s <%s> is not accepted in \"mod\" operation",left.name,left.Type);
                aSymbolTable.print_error(left.line,left.pos,error);
            }
            if(!equiv(right,int_type)){
                error = String.format("%s <%s> is not accepted in \"mod\" operation",right.name,right.Type);
                aSymbolTable.print_error(right.line,right.pos,error);
            }
        }
        temp = new type_info(right.line,right.pos,right.name,"int_const",0,0,false);
        type_stack.add(temp);
    }

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
    }

}
