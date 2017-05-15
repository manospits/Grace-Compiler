package compiler;
import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;
import java.io.*;
import java.util.*;

public class PrintingVisitor extends DepthFirstAdapter{
    int spaces=0;

    SymbolTable aSymbolTable = new SymbolTable();
    ArrayList<argument> temp_args;

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
            for(TTNumber e_ar : array_copy)
            {
                array_sizes.add(Integer.parseInt(e_ar.toString().replaceAll("\\s+","")));
            }

            //IDS
            ids = new ArrayList<String>();
            List<TTId> id_copy = new ArrayList<TTId>(node_f.getTId());
            for(TTId id_e : id_copy)
            {
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

    //fpar-def
    @Override
    public void inAFparDef(AFparDef node){
    }

    @Override
    public void outAFparDef(AFparDef node){
    }

    @Override
    public void inAFparType(AFparType node) {
    }

    @Override
    public void outAFparType(AFparType node)
    {
    }
    //local-def
    @Override
    public void inAFuncDefLocalDef(AFuncDefLocalDef node){
    }


    @Override
    public void outAFuncDefLocalDef(AFuncDefLocalDef node){
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
        for(TTId e : id_copy)
        {
            int error;
            error=aSymbolTable.insert(e.getLine(),e.getPos(),e.toString().replaceAll("\\s+",""),Type,"no",false,array_sizes,null,false);
            if(error==1){
                //System.out.printf("Error (%d,%d) : \"%s\" < %s > has been redefined\n",e.getLine(),e.getPos(),e.toString().replaceAll("\\s+",""),Type);
            }
        }
        //System.out.printf("type : %s && array sizes:%s\n",Type,Arrays.toString(array_sizes.toArray()));
    }

    @Override
    public void outAVarDef(AVarDef node)
    {
    }

}
