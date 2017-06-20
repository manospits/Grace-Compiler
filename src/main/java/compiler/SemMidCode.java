package compiler;
import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class SemMidCode extends DepthFirstAdapter{
    int spaces=0;
    SymbolTable aSymbolTable = new SymbolTable();
    ArrayList<argument> temp_args;
    middlecode aMiddleCode = new middlecode();
    assembly aAssembly = new assembly();
    String error,function_name;
    int string_dim=1;
    Pattern p = Pattern.compile("[a-zA-Z_\\d]_(\\d+)$"); //used for getting depth from a function name


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

    type_info int_type  = new type_info(0,0,"int","int",0,0,false);
    type_info char_type = new type_info(0,0,"char","char",0,0,false);
    type_info bool_type = new type_info(0,0,"bool","bool",0,0,false);

    class fun_name_type{
        String name;
        String type;
        int line;
        int pos;
        fun_name_type(String name,String type,int line,int pos){
            this.name=name;
            this.type=type;
            this.line=line;
            this.pos=pos;
        }
    }

    ArrayList<type_info> type_stack = new ArrayList<type_info>();
    ArrayList<fun_name_type> function_stack = new ArrayList<fun_name_type>();

    //MIDDLE CODE
    ArrayList<info_node> mi_info_nodes = new ArrayList<info_node>();
    String W;

    boolean return_check=false;
    boolean first_func_def=true;

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

    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    public int next_4(int n){
        int a=n&3;
        if(a==0) return n;
        return n+(4-a);
    }

    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }

    public int get_depth_fname(String fun){
        Matcher m = p.matcher(fun);
        m.find();
        String num=m.group(1);
        //System.out.printf("%s\n",num);
        return Integer.parseInt(num);
    }

    public boolean  check_array(String a){
        if(a.charAt(0)=='[' && a.charAt(a.length()-1)==']'){
            return true;
        }
        else return false;
    }

    public String remove_array(String a){
        return a.substring(1,a.length()-1);
    }

    public boolean check_temp(String a){
        if(a.charAt(0)=='$'){
            return true;
        }
        else return false;
    }

    public boolean check_const(String a){
        if(isInteger(a) || a.charAt(0)=='\'' || a.charAt(0)=='\"' ){
            return true;
        }
        else return false;
    }

    public String get_const_type(String a){
        if(isInteger(a) ){
            return "int";
        }
        else if( a.charAt(0)=='\'' ){
            return "char";
        }
        else if(a.charAt(0)=='\"'){
            return "string";
        }
        return "";
    }

    public assembly.as_type create_as_type(String a,int func_depth,String ret_type){
        String name=a;
        String Type;
        boolean ref=false;
        boolean arg=false;
        boolean constant=false;
        String label="";
        String pointing="";
        int address=0,na,np;
        na=np=func_depth;
        int size=0;
        //if(check_array(a)){
            //name=a.substring(1,a.length()-2);
            //load("edi",name,np);
            //reg="edi";
        //}
        if(name.equals("$$")){
            ref=true;
            arg=true;
            address=12;
            Type=ret_type;
        }
        else if(check_const(name)){
            constant=true;
            Type = get_const_type(name);
            if(Type.equals("string")){
                //System.out.printf("%s\n",name);
                label=aAssembly.add_str(name);
            }
        }
        else if(check_temp(name)){
           middlecode.var_info temp=aMiddleCode.get_var_info(name);
           address=temp.address;
           Type=temp.type;
           pointing=temp.pointing;
        }
        else{
            SymbolTable.SymbolTableRecord aSymbol=aSymbolTable.lookup(name);
            address=aSymbol.address;
            na=aSymbol.Depth-1;
            ref=aSymbol.ref;
            arg=aSymbol.arg;
            Type=aSymbol.type;
            address=aSymbol.address;
            int total_size=1;
            for(int s:aSymbol.array_sizes){
                total_size*=s;
            }
            size=total_size;
        }
        assembly.as_type temp_as=aAssembly.fill_as_type(a,Type,ref,arg,constant,address,np,na,label,size,pointing);
        return temp_as;
    }

    public void load(String R,String a,int depth,String ret_type){
        assembly.as_type temp_as;
        if(check_array(a)){
            temp_as=create_as_type(remove_array(a),depth,ret_type);
            aAssembly.load("edi",temp_as);
            if(temp_as.pointing.equals("int")){
                aAssembly.add_comm("mov",R,"DWORD ptr [edi]",true);
            }
            else{
                aAssembly.add_comm("movzx",R,"BYTE ptr [edi]",true);
            }
        }
        else{
            temp_as=create_as_type(a,depth,ret_type);
            aAssembly.load(R,temp_as);
        }
    }

    public void load_addr(String R,String a,int depth,String ret_type){
        //System.out.printf("%s\n",a);
        assembly.as_type temp_as;
        if(check_array(a)){
            temp_as=create_as_type(remove_array(a),depth,ret_type);
            aAssembly.load(R,temp_as);
        }
        else{
            temp_as=create_as_type(a,depth,ret_type);
            aAssembly.load_addr(R,temp_as);
        }
    }

    public String get_low_register(String R){
        return String.format("%cl",R.charAt(1));
    }

    public void store(String R,String a,int depth,String ret_type){
        assembly.as_type temp_as;
        if(check_array(a)){
            temp_as=create_as_type(remove_array(a),depth,ret_type);
            aAssembly.load("edi",temp_as);
            if(temp_as.pointing.equals("int")){
                aAssembly.add_comm("mov","DWORD ptr [edi]",R,true);
            }
            else{
                aAssembly.add_comm("mov","BYTE ptr [edi]",get_low_register(R),true);
            }
        }
        else{
            temp_as=create_as_type(a,depth,ret_type);
            if(temp_as.Type.equals("int")){
                aAssembly.store(R,temp_as);
            }
            else{
                aAssembly.store(get_low_register(R),temp_as);
            }
        }
    }

    //PROGRAM IN
    @Override
    public void inAProgram(AProgram node){
        aSymbolTable.add_basic_functions();
    }

    //PROGRAM OUT
    @Override
    public void outAProgram(AProgram node){
        aMiddleCode.print_quads();
        aAssembly.create_assembly_file(String.format("%s.s",Main.filename));
    }

    //FUNC DEF IN
    @Override
    public void inAFuncDef(AFuncDef node){
        String fun_name;
        String Type;
        String ret_type;
        ArrayList<argument> temp_args = new ArrayList<argument>();
        Type = "fun";
        fun_name = node.getTId().toString().trim();
        ret_type = node.getRetType().toString().trim();
        AFparDef node_f;
        List<PFparDef> copy = new ArrayList<PFparDef>(node.getFparDef());
        if(first_func_def == true){
            if(copy.size()!=0){
                error = String.format("function %s can't have arguments",node.getTId().toString().trim());
                aSymbolTable.print_error(node.getTId().getLine(),node.getTId().getPos(),error);
            }
            if(!node.getRetType().toString().trim().equals("nothing")){
                error = String.format("function %s can't have returning argument",node.getTId().toString().trim());
                aSymbolTable.print_error(node.getTId().getLine(),node.getTId().getPos(),error);
            }
            function_name=String.format("%s_%d",fun_name,0);
        }
        first_func_def=false;
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
            TypeOfArg = node_fp.getDataType().toString().trim();
            //ARRAY
            array_sizes =  new ArrayList<Integer>();
            if(node_fp.getFparTypeTArEmpty() != null){
                array_sizes.add(-1);
            }
            List<TTNumber> array_copy = new ArrayList<TTNumber>(node_fp.getTNumber());
            for(TTNumber e_ar : array_copy)
            {
                array_sizes.add(Integer.parseInt(e_ar.toString().trim()));
            }
            //IDS
            ids = new ArrayList<String>();
            List<TTId> id_copy = new ArrayList<TTId>(node_f.getTId());
            for(TTId id_e : id_copy)
            {
                ids.add(id_e.toString().trim());
            }
            arg = new argument(TypeOfArg,ids,array_sizes,ref);
            temp_args.add(arg);
        }
        aSymbolTable.insert(node.getTId().getLine(),node.getTId().getPos(),fun_name,Type,ret_type,false,null,temp_args,true,false);
        fun_name_type temp_fun_info= new fun_name_type(fun_name,ret_type,node.getTId().getLine(),node.getTId().getPos());
        function_stack.add(temp_fun_info);
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
            TypeOfArg = node_fp.getDataType().toString().trim();
            //ARRAY
            array_sizes =  new ArrayList<Integer>();
            if(node_fp.getFparTypeTArEmpty() != null){
                array_sizes.add(-1);
            }
            List<TTNumber> array_copy = new ArrayList<TTNumber>(node_fp.getTNumber());
            for(TTNumber e_ar : array_copy){
                array_sizes.add(Integer.parseInt(e_ar.toString().trim()));
            }
            //IDS
            //System.out.printf("size %d\n",array_sizes.size());
            ids = new ArrayList<String>();
            List<TTId> id_copy = new ArrayList<TTId>(node_f.getTId());
            for(TTId id_e : id_copy){
                //System.out.printf("arg_name %s\n",id_e.toString());
                aSymbolTable.insert(id_e.getLine(),id_e.getPos(),id_e.toString().trim(),TypeOfArg,"no",ref,array_sizes,null,false,true);
            }
        }
    }

    //FUNC DEF CASE
    @Override
    public void caseAFuncDef(AFuncDef node)
    {
        inAFuncDef(node);
        if(node.getTId() != null)
        {
            node.getTId().apply(this);
        }
        //symbol must be found before visiting local definitions in case of shadowing
        SymbolTable.SymbolTableRecord foundSymbol=aSymbolTable.lookup(node.getTId().toString().trim());
        {
            List<PFparDef> copy = new ArrayList<PFparDef>(node.getFparDef());
            for(PFparDef e : copy)
            {
                e.apply(this);
            }
        }
        if(node.getRetType() != null)
        {
            node.getRetType().apply(this);
        }
        {
            List<PLocalDef> copy = new ArrayList<PLocalDef>(node.getLocalDef());
            for(PLocalDef e : copy)
            {
                e.apply(this);
            }
        }
        {
            int from_tempv,to_tempv;
            aMiddleCode.genquad("unit",String.format("%s_%d",foundSymbol.name,foundSymbol.Depth),"-","-");
            aMiddleCode.set_start_address(aSymbolTable.get_last_depth_local_address());
            from_tempv=aMiddleCode.get_var_index();
            ArrayList<Integer> L=aMiddleCode.emptylist();
            List<PStmt> copy = new ArrayList<PStmt>(node.getStmt());
            info_node a_stmt;
            for(PStmt e : copy)
            {
                e.apply(this);
                a_stmt=mi_info_nodes.remove(mi_info_nodes.size()-1);
                L = a_stmt.Next;
                aMiddleCode.backpatch(L,aMiddleCode.nextquad());
            }
            info_node temp_mi = new info_node("","block",L,null,null,false);
            mi_info_nodes.add(temp_mi);
            to_tempv=aMiddleCode.get_var_index();
            aMiddleCode.genquad("endu",String.format("%s_%d",foundSymbol.name,foundSymbol.Depth),"-","-");
            aMiddleCode.add_range(String.format("%s_%d",foundSymbol.name,foundSymbol.Depth),from_tempv,to_tempv);
        }
        outAFuncDef(node);
    }

    //FUNC DEF OUT
    @Override
    public void outAFuncDef(AFuncDef node){
        fun_name_type tempf=function_stack.get(function_stack.size()-1);
        String ret_type;
        if(!tempf.type.equals("nothing")){
            if(!return_check){
                error = String.format("nothing returned in function %s with return type <%s>",tempf.name,tempf.type);
                aSymbolTable.print_error(tempf.line,tempf.pos,error);
            }
        }
        ret_type=tempf.type;
        return_check=false;
        function_stack.remove(function_stack.size()-1);
        //middlecode to assembly
        String m_op;
        int from,to=aMiddleCode.nextquad();
        from=aMiddleCode.last_unit_pos(to);
        aMiddleCode.optimize_unit(from);
        if(from==0){
            aAssembly.add_comm(".intel_syntax","noprefix","",false);
            aAssembly.add_comm(".text","","",false);
            aAssembly.add_comm(".global","main","",true);
            aAssembly.add_comm("main:","","",false);
            aAssembly.add_comm("push","ebp","",true);
            aAssembly.add_comm("mov","ebp","esp",true);
            aAssembly.add_comm("sub","esp","4",true);
            aAssembly.add_comm("push","ebp","",true);
            aAssembly.add_comm("call",function_name,"",true);
            aAssembly.add_comm("mov","eax","0",true);
            aAssembly.add_comm("mov","esp","ebp",true);
            aAssembly.add_comm("pop","ebp","",true);
            aAssembly.add_comm("ret","","",true);
        }
        middlecode.quad aQuad;
        ArrayList<middlecode.quad> parameters=new ArrayList<middlecode.quad>();
        middlecode.quad ret_par=null;
        int size,cur_depth=0,call_depth;
        size=aMiddleCode.get_start_address();
        size=next_4(size);
        String name="";
        boolean ret=false;
        for(int i=from;i<to;i++){
            aAssembly.add_comm(String.format("_grace_%d:",i),"","",false);
            aQuad=aMiddleCode.get_quad(i);
            //System.out.printf("%s %s %s %s\n",aQuad.op,aQuad.x,aQuad.y,aQuad.z);
            if(aQuad.op.equals("unit")){
                cur_depth=get_depth_fname(aQuad.x);
                name=aQuad.x;
                aAssembly.add_comm(String.format("%s:",aQuad.x),"","",false);
                aAssembly.add_comm("push","ebp","",true);
                aAssembly.add_comm("mov","ebp","esp",true);
                aAssembly.add_comm("sub","esp",String.format("%s",size),true);
            }
            else if (aQuad.op.equals("endu")) {
                aAssembly.add_comm(String.format("_end_%s:",aQuad.x),"","",false);
                aAssembly.add_comm("mov","esp","ebp",true);
                aAssembly.add_comm("pop","ebp","",true);
                aAssembly.add_comm("ret","","",true);
            }
            else if (aQuad.op.equals("par")){
                if(aQuad.y.equals("RET")){
                    ret=true;
                    ret_par=aQuad;
                }
                else{
                    parameters.add(aQuad);
                }
            }
            else if (aQuad.op.equals("call")){
                int par_size=parameters.size();
                for(int j = par_size-1;j>=0;j--){
                    middlecode.quad q=parameters.get(j);
                    if(q.y.equals("V")){
                        load("eax",q.x,cur_depth,ret_type);
                        aAssembly.add_comm("push","eax","",true);
                    }
                    else{
                        load_addr("esi",q.x,cur_depth,ret_type);
                        aAssembly.add_comm("push","esi","",true);
                    }
                }
                parameters.clear();
                call_depth = get_depth_fname(aQuad.z);
                if(ret==true){
                    ret=false;
                    load_addr("esi",ret_par.x,cur_depth,ret_type);
                    aAssembly.add_comm("push","esi","",true);
                }else{
                    aAssembly.add_comm("sub","esp","4",true);
                }
                aAssembly.updateAl(cur_depth,call_depth);
                aAssembly.add_comm("call",aQuad.z,"",true);
                aAssembly.add_comm("add","esp",String.format("%s",8+(par_size*4)),true);
            }
            else if(aQuad.op.equals(":=")){
                //System.out.printf("type %s %s %s\n",temp_as2.Type,temp_as2.arg,temp_as2.ref);
                load("eax",aQuad.x,cur_depth,ret_type);
                store("eax",aQuad.z,cur_depth,ret_type);
            }
            else if(aQuad.op.equals("array")){
                assembly.as_type a_type = create_as_type(aQuad.x,cur_depth,ret_type);
                load("eax",aQuad.y,cur_depth,ret_type);
                //aAssembly.add_comm("mov","eax",String.format("%s",a_type.size),true);
                //aAssembly.add_comm("sub","eax","ebx",true);
                if(a_type.Type.equals("int")){
                    aAssembly.add_comm("mov","ecx","4",true);
                }
                else{
                    aAssembly.add_comm("mov","ecx","1",true);
                }
                aAssembly.add_comm("imul","ecx","",true);
                //System.out.printf("type %s %s %s %s %d\n",a_type.a,a_type.Type,a_type.arg,a_type.ref,a_type.address);
                load_addr("ecx",aQuad.x,cur_depth,ret_type);
                aAssembly.add_comm("add","ecx","eax",true);
                store("ecx",aQuad.z,cur_depth,ret_type);
            }
            else if(aQuad.op.equals("+")){
                load("eax",aQuad.x,cur_depth,ret_type);
                load("edx",aQuad.y,cur_depth,ret_type);
                aAssembly.add_comm("add","eax","edx",true);
                store("eax",aQuad.z,cur_depth,ret_type);
            }
            else if(aQuad.op.equals("-")){
                load("eax",aQuad.x,cur_depth,ret_type);
                load("edx",aQuad.y,cur_depth,ret_type);
                aAssembly.add_comm("sub","eax","edx",true);
                store("eax",aQuad.z,cur_depth,ret_type);
            }
            else if(aQuad.op.equals("*")){
                load("eax",aQuad.x,cur_depth,ret_type);
                load("ecx",aQuad.y,cur_depth,ret_type);
                aAssembly.add_comm("imul","ecx","",true);
                store("eax",aQuad.z,cur_depth,ret_type);
            }
            else if(aQuad.op.equals("/")){
                load("eax",aQuad.x,cur_depth,ret_type);
                aAssembly.add_comm("cwd","","",true);
                load("ecx",aQuad.y,cur_depth,ret_type);
                aAssembly.add_comm("idiv","ecx","",true);
                store("eax",aQuad.z,cur_depth,ret_type);
            }
            else if(aQuad.op.equals("%")){
                load("eax",aQuad.x,cur_depth,ret_type);
                aAssembly.add_comm("cwd","","",true);
                load("ecx",aQuad.y,cur_depth,ret_type);
                aAssembly.add_comm("idiv","ecx","",true);
                store("edx",aQuad.z,cur_depth,ret_type);
            }
            else if(aQuad.op.equals("jump")){
                aAssembly.add_comm("jmp",String.format("_grace_%s",aQuad.z),"",true);
            }
            else if(aQuad.op.equals("<")){
                load("eax",aQuad.x,cur_depth,ret_type);
                load("edx",aQuad.y,cur_depth,ret_type);
                aAssembly.add_comm("cmp","eax","edx",true);
                aAssembly.add_comm("jl",String.format("_grace_%s",aQuad.z),"",true);
            }
            else if(aQuad.op.equals(">")){
                load("eax",aQuad.x,cur_depth,ret_type);
                load("edx",aQuad.y,cur_depth,ret_type);
                aAssembly.add_comm("cmp","eax","edx",true);
                aAssembly.add_comm("jg",String.format("_grace_%s",aQuad.z),"",true);
            }
            else if(aQuad.op.equals("<=")){
                load("eax",aQuad.x,cur_depth,ret_type);
                load("edx",aQuad.y,cur_depth,ret_type);
                aAssembly.add_comm("cmp","eax","edx",true);
                aAssembly.add_comm("jle",String.format("_grace_%s",aQuad.z),"",true);
            }
            else if(aQuad.op.equals(">=")){
                load("eax",aQuad.x,cur_depth,ret_type);
                load("edx",aQuad.y,cur_depth,ret_type);
                aAssembly.add_comm("cmp","eax","edx",true);
                aAssembly.add_comm("jge",String.format("_grace_%s",aQuad.z),"",true);
            }
            else if(aQuad.op.equals("=")){
                load("eax",aQuad.x,cur_depth,ret_type);
                load("edx",aQuad.y,cur_depth,ret_type);
                aAssembly.add_comm("cmp","eax","edx",true);
                aAssembly.add_comm("je",String.format("_grace_%s",aQuad.z),"",true);
            }
            else if(aQuad.op.equals("#")){
                load("eax",aQuad.x,cur_depth,ret_type);
                load("edx",aQuad.y,cur_depth,ret_type);
                aAssembly.add_comm("cmp","eax","edx",true);
                aAssembly.add_comm("jne",String.format("_grace_%s",aQuad.z),"",true);
            }
            else if(aQuad.op.equals("ret")){
                aAssembly.add_comm("jmp",String.format("_end_%s",name),"",true);
            }
        }
        aSymbolTable.exit();
    }

    //FUNC DECL
    @Override
    public void inAFuncDecl(AFuncDecl node){
        String fun_name;
        String Type;
        String ret_type;
        ArrayList<argument> temp_args = new ArrayList<argument>();
        Type = "fun";
        fun_name = node.getTId().toString().trim();
        ret_type = node.getRetType().toString().trim();
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
            TypeOfArg = node_fp.getDataType().toString().trim();
            //ARRAY
            array_sizes =  new ArrayList<Integer>();
            if(node_fp.getFparTypeTArEmpty() != null){
                array_sizes.add(-1);
            }
            List<TTNumber> array_copy = new ArrayList<TTNumber>(node_fp.getTNumber());
            for(TTNumber e_ar : array_copy)
            {
                array_sizes.add(Integer.parseInt(e_ar.toString().trim()));
            }
            //IDS
            ids = new ArrayList<String>();
            List<TTId> id_copy = new ArrayList<TTId>(node_f.getTId());
            for(TTId id_e : id_copy)
            {
                ids.add(id_e.toString().trim());
            }
            //function
            arg = new argument(TypeOfArg,ids,array_sizes,ref);
            temp_args.add(arg);
        }
        aSymbolTable.insert(node.getTId().getLine(),node.getTId().getPos(),fun_name,Type,ret_type,false,null,temp_args,false,false);
    }

    //VAR DEF
    @Override
    public void inAVarDef(AVarDef node)
    {
        //useful vars
        String Type;
        ArrayList<Integer> array_sizes;
        int index = 0;
        Type = node.getDataType().toString().trim();
        List<TTNumber> array_copy = new ArrayList<TTNumber>(node.getTNumber());
        array_sizes=new ArrayList<Integer>();
        for(TTNumber e : array_copy)
        {
            array_sizes.add(Integer.parseInt(e.toString().trim()));
        }
        List<TTId> id_copy = new ArrayList<TTId>(node.getTId());
        for(TTId e : id_copy){
            aSymbolTable.insert(e.getLine(),e.getPos(),e.toString().trim(),Type,"no",false,array_sizes,null,false,false);
        }
        //System.out.printf("type : %s && array sizes:%s\n",Type,Arrays.toString(array_sizes.toArray()));
    }

    //EXPR

    //EX
    //PR CONST
    @Override
    public void inAConstExpr(AConstExpr node)
    {
        type_info temp = new type_info(node.getTNumber().getLine(),node.getTNumber().getPos(),node.getTNumber().toString().trim(),"int_const",0,0,false);
        info_node temp_mi = new info_node(node.getTNumber().toString().trim(),"int",null,null,null,false);
        type_stack.add(temp);
        mi_info_nodes.add(temp_mi);
    }

    //EXPR CHAR
    @Override
    public void inACharExpr(ACharExpr node)
    {
        type_info temp = new type_info(node.getTCharCon().getLine(),node.getTCharCon().getPos(),node.getTCharCon().toString().trim(),"char_const",0,0,false);
        info_node temp_mi = new info_node(node.getTCharCon().toString().trim(),"char",null,null,null,false);
        type_stack.add(temp);
        mi_info_nodes.add(temp_mi);
    }

    //EXPR PLUS
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
        info_node leftm,rightm;
        rightm = mi_info_nodes.remove(mi_info_nodes.size()-1);
        leftm  = mi_info_nodes.remove(mi_info_nodes.size()-1);
        String leftplace=leftm.place,rightplace=rightm.place;
        if(leftm.array){
            leftplace = String.format("[%s]",leftm.place);
        }
        if(rightm.array){
            rightplace = String.format("[%s]",rightm.place);
        }
        W = aMiddleCode.newtemp("int","int");
        aMiddleCode.genquad("+",leftplace,rightplace,W);
        info_node temp_mi = new info_node(W,"int",null,null,null,false);
        mi_info_nodes.add(temp_mi);
    }

    //EXPR MINUS
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
        info_node leftm,rightm;
        rightm = mi_info_nodes.remove(mi_info_nodes.size()-1);
        leftm  = mi_info_nodes.remove(mi_info_nodes.size()-1);
        String leftplace=leftm.place,rightplace=rightm.place;
        if(leftm.array){
            leftplace = String.format("[%s]",leftm.place);
        }
        if(rightm.array){
            rightplace = String.format("[%s]",rightm.place);
        }
        W = aMiddleCode.newtemp("int","int");
        aMiddleCode.genquad("-",leftplace,rightplace,W);
        info_node temp_mi = new info_node(W,"int",null,null,null,false);
        mi_info_nodes.add(temp_mi);
    }

    //EXPR MULT
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
        info_node leftm,rightm;
        rightm = mi_info_nodes.remove(mi_info_nodes.size()-1);
        leftm  = mi_info_nodes.remove(mi_info_nodes.size()-1);
        String leftplace=leftm.place,rightplace=rightm.place;
        if(leftm.array){
            leftplace = String.format("[%s]",leftm.place);
        }
        if(rightm.array){
            rightplace = String.format("[%s]",rightm.place);
        }
        W = aMiddleCode.newtemp("int","int");
        aMiddleCode.genquad("*",leftplace,rightplace,W);
        info_node temp_mi = new info_node(W,"int",null,null,null,false);
        mi_info_nodes.add(temp_mi);
    }

    //EXPR DIV
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
        info_node leftm,rightm;
        rightm = mi_info_nodes.remove(mi_info_nodes.size()-1);
        leftm  = mi_info_nodes.remove(mi_info_nodes.size()-1);
        String leftplace=leftm.place,rightplace=rightm.place;
        if(leftm.array){
            leftplace = String.format("[%s]",leftm.place);
        }
        if(rightm.array){
            rightplace = String.format("[%s]",rightm.place);
        }
        W = aMiddleCode.newtemp("int","int");
        aMiddleCode.genquad("/",leftplace,rightplace,W);
        info_node temp_mi = new info_node(W,"int",null,null,null,false);
        mi_info_nodes.add(temp_mi);
    }

    //EXPR MOD
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
        info_node leftm,rightm;
        rightm = mi_info_nodes.remove(mi_info_nodes.size()-1);
        leftm  = mi_info_nodes.remove(mi_info_nodes.size()-1);
        String leftplace=leftm.place,rightplace=rightm.place;
        if(leftm.array){
            leftplace = String.format("[%s]",leftm.place);
        }
        if(rightm.array){
            rightplace = String.format("[%s]",rightm.place);
        }
        W = aMiddleCode.newtemp("int","int");
        aMiddleCode.genquad("%",leftplace,rightplace,W);
        info_node temp_mi = new info_node(W,"int",null,null,null,false);
        mi_info_nodes.add(temp_mi);
    }

    //EXPR IN PLUS
    @Override
    public void outAInplusExpr(AInplusExpr node)
    {
        type_info right,temp;
        right = type_stack.remove(type_stack.size()-1);
        if(!equiv(right,int_type)){
            error = String.format("%s (%d) <%s> (%d) is not accepted in \"+\" (infix) operation",right.name,right.array_cur_dim,right.Type,right.array_max_dim);
            aSymbolTable.print_error(right.line,right.pos,error);
        }
        temp = new type_info(right.line,right.pos,right.name,"int_const",0,0,false);
        type_stack.add(temp);
        info_node leftm,rightm;
        rightm = mi_info_nodes.remove(mi_info_nodes.size()-1);
        String rightplace=rightm.place;
        if(rightm.array){
            rightplace = String.format("[%s]",rightm.place);
        }
        W = aMiddleCode.newtemp("int","int");
        aMiddleCode.genquad("+","0",rightplace,W);
        info_node temp_mi = new info_node(W,"int",null,null,null,false);
        mi_info_nodes.add(temp_mi);
    }

    //EXPR IN MINUS
    @Override
    public void outAInminusExpr(AInminusExpr node)
    {
        type_info right,temp;
        right = type_stack.remove(type_stack.size()-1);
        if(!equiv(right,int_type)){
            error = String.format("%s (%d) <%s> (%d) is not accepted in \"-\" (infix) operation",right.name,right.array_cur_dim,right.Type,right.array_max_dim);
            aSymbolTable.print_error(right.line,right.pos,error);
        }
        temp = new type_info(right.line,right.pos,right.name,"int_const",0,0,false);
        type_stack.add(temp);
        info_node leftm,rightm;
        rightm = mi_info_nodes.remove(mi_info_nodes.size()-1);
        String rightplace=rightm.place;
        if(rightm.array){
            rightplace = String.format("[%s]",rightm.place);
        }
        W = aMiddleCode.newtemp("int","int");
        aMiddleCode.genquad("-","0",rightplace,W);
        info_node temp_mi = new info_node(W,"int",null,null,null,false);
        mi_info_nodes.add(temp_mi);
    }

    //LVALUE

    //LVALUE ID
    @Override
    public void outALValueIdLValue(ALValueIdLValue node){
        int dims = node.getExpr().size();
        int pos2remove = type_stack.size() - dims;
        type_info array_index;
        SymbolTable.SymbolTableRecord aSymbol=aSymbolTable.lookup(node.getTId().toString().trim());
        if(aSymbol == null){
            error = String.format("id \"%s\" is not declared in this scope",node.getTId().toString().trim());
            aSymbolTable.print_error(node.getTId().getLine(),node.getTId().getPos(),error);
        }
        else{
            if(aSymbol.array_sizes.size()<dims){
                error = String.format("accessing dimension (%d) when id \"%s\" has (%d) ",dims,node.getTId().toString().trim(),aSymbol.array_sizes.size());
                aSymbolTable.print_error(node.getTId().getLine(),node.getTId().getPos(),error);
            }
            //DONE TYPE check
        }
        for(int i=0; i < dims ; i++ ){
            array_index=type_stack.remove(pos2remove);
            if(!equiv(array_index,int_type)){
                error = String.format("%s <%s> is not accepted in array index (%d)",array_index.name,array_index.Type,i+1);
                aSymbolTable.print_error(array_index.line,array_index.pos,error);
            }
            if(array_index.Type.equals("int_const") && isInteger(array_index.name)){
                int value=Integer.parseInt(array_index.name);
                if(aSymbol.array_sizes.get(i)!= -1 && value >= aSymbol.array_sizes.get(i)){
                    error = String.format("accessing dimension (%d) with index (%d) bigger than array size (%d) ",dims,value,aSymbol.array_sizes.get(i));
                    aSymbolTable.print_error(array_index.line,array_index.pos,error);
                }
            }
        }
        if(aSymbol!= null){
            type_info temp = new type_info(node.getTId().getLine(),node.getTId().getPos(),node.getTId().toString().trim(),aSymbol.type,aSymbol.array_sizes.size(),dims,false);
            type_stack.add(temp);
            info_node temp_mi;
            String place;
            if(dims==0){
                if(aSymbol.type.equals("char_const"))
                    temp_mi = new info_node(node.getTId().toString().trim(),"char",null,null,null,false);
                else if(aSymbol.type.equals("int_const"))
                    temp_mi = new info_node(node.getTId().toString().trim(),"int",null,null,null,false);
                else
                    temp_mi = new info_node(node.getTId().toString().trim(),aSymbol.type,null,null,null,false);
                mi_info_nodes.add(temp_mi);
            }
            else{
                int pos2remove_m = mi_info_nodes.size() - dims;
                info_node index;
                String tW="";
                if(dims-aSymbol.array_sizes.size()==0){
                    if(dims>1){
                        for(int i=0; i < dims-1 ; i++ ){
                            index = mi_info_nodes.remove(pos2remove_m);
                            W  = aMiddleCode.newtemp("int","int");
                            int width=aSymbol.array_sizes.get(i+1);
                            for(int j=i+2;j<dims;j++){
                                width*=aSymbol.array_sizes.get(j);
                            }
                            place=index.place;
                            if(index.array){
                                place=String.format("[%s]",index.place);
                            }
                            aMiddleCode.genquad("*",String.format("%d",width),place,W);
                            if(i!=0)
                                aMiddleCode.genquad("+",tW,W,tW);
                            else
                                tW=W;
                        }
                        W  = aMiddleCode.newtemp("int","int");
                        index = mi_info_nodes.remove(pos2remove_m);
                        aMiddleCode.genquad("+",index.place,tW,W);
                        tW=W;
                    }
                    else{
                        index = mi_info_nodes.remove(pos2remove_m);
                        place=index.place;
                        if(index.array){
                            place=String.format("[%s]",index.place);
                        }
                        tW=place;
                    }
                    if(aSymbol.type.equals("char_const"))
                        W = aMiddleCode.newtemp("int","char");
                    else if(aSymbol.type.equals("int_const"))
                        W = aMiddleCode.newtemp("int","int");
                    else
                        W = aMiddleCode.newtemp("int",aSymbol.type);
                    aMiddleCode.genquad("array",node.getTId().toString().trim(),tW,W);
                    temp_mi = new info_node(W,"int",null,null,null,true);
                }else{
                    for(int i=0; i < dims ; i++ ){
                        index = mi_info_nodes.remove(pos2remove_m);
                        W  = aMiddleCode.newtemp("int","int");
                        int width=aSymbol.array_sizes.get(i+1);
                        for(int j=i+2;j<dims;j++){
                            width*=aSymbol.array_sizes.get(j);
                        }
                        place=index.place;
                        if(index.array){
                            place=String.format("[%s]",index.place);
                        }
                        aMiddleCode.genquad("*",String.format("%d",width),place,W);
                        if(i!=0)
                            aMiddleCode.genquad("+",tW,W,tW);
                        else
                            tW=W;
                    }
                    W = aMiddleCode.newtemp("int","int");
                    aMiddleCode.genquad("array",node.getTId().toString().trim(),tW,W);
                    temp_mi = new info_node(W,"int",null,null,null,true);
                }
                mi_info_nodes.add(temp_mi);
            }
        }
    }

    //LVALUE STRING
    @Override
    public void outALValueStringLValue(ALValueStringLValue node)
    {
        int dims = node.getExpr().size();
        int pos2remove = type_stack.size() - dims;
        int pos2remove_m = mi_info_nodes.size() - dims;
        type_info array_index;
        info_node temp_mi;
        String place;
        if(string_dim<dims){
            error = String.format("accessing dimension (%d) when string \"%s\" has (%d) ",dims,node.getTString().toString().trim(),string_dim);
            aSymbolTable.print_error(node.getTString().getLine(),node.getTString().getPos(),error);
        }
        info_node index;
        int size=node.getTString().toString().trim().length()-2;
        if(dims!=0){
            for(int i=0; i < dims ; i++ ){
                array_index=type_stack.remove(pos2remove);
                if(!equiv(array_index,int_type)){
                    error = String.format("%s <%s> is not accepted in array index (%d)",array_index.name,array_index.Type,i+1);
                    aSymbolTable.print_error(array_index.line,array_index.pos,error);
                }
                if(array_index.Type.equals("int_const") && isInteger(array_index.name)){
                    int value=Integer.parseInt(array_index.name);
                    if(value > size){
                        error = String.format("accessing dimension (%d) with index (%d) bigger than array size (%d) ",dims,value,size);
                        aSymbolTable.print_error(array_index.line,array_index.pos,error);
                    }
                }
                index = mi_info_nodes.remove(pos2remove_m);
                W = aMiddleCode.newtemp("int","char");
                place=index.place;
                if(index.array){
                    place=String.format("[%s]",index.place);
                }
                aMiddleCode.genquad("array",node.getTString().toString().trim(),place,W);
                temp_mi = new info_node(W,"char",null,null,null,true);
                mi_info_nodes.add(temp_mi);
            }
        }
        else{
            temp_mi = new info_node(node.getTString().toString().trim(),"char",null,null,null,false);
            mi_info_nodes.add(temp_mi);
        }
        type_info temp = new type_info(node.getTString().getLine(),node.getTString().getPos(),node.getTString().toString().trim(),"char",string_dim,dims,true);
        type_stack.add(temp);
    }

    //COND

    //COND EQUAL
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
        info_node leftm,rightm;
        rightm = mi_info_nodes.remove(mi_info_nodes.size()-1);
        leftm  = mi_info_nodes.remove(mi_info_nodes.size()-1);
        String leftplace=leftm.place,rightplace=rightm.place;
        if(leftm.array){
            leftplace = String.format("[%s]",leftm.place);
        }
        if(rightm.array){
            rightplace = String.format("[%s]",rightm.place);
        }
        ArrayList<Integer> True = aMiddleCode.makelist(aMiddleCode.nextquad());
        aMiddleCode.genquad("=",leftplace,rightplace,"*");
        ArrayList<Integer> False = aMiddleCode.makelist(aMiddleCode.nextquad());
        aMiddleCode.genquad("jump","-","-","*");
        info_node temp_mi = new info_node("","boolean",null,True,False,false);
        mi_info_nodes.add(temp_mi);
    }

    //COND HASH
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
        info_node leftm,rightm;
        rightm = mi_info_nodes.remove(mi_info_nodes.size()-1);
        leftm  = mi_info_nodes.remove(mi_info_nodes.size()-1);
        String leftplace=leftm.place,rightplace=rightm.place;
        if(leftm.array){
            leftplace = String.format("[%s]",leftm.place);
        }
        if(rightm.array){
            rightplace = String.format("[%s]",rightm.place);
        }
        ArrayList<Integer> True = aMiddleCode.makelist(aMiddleCode.nextquad());
        aMiddleCode.genquad("#",leftplace,rightplace,"*");
        ArrayList<Integer> False = aMiddleCode.makelist(aMiddleCode.nextquad());
        aMiddleCode.genquad("jump","-","-","*");
        info_node temp_mi = new info_node("","boolean",null,True,False,false);
        mi_info_nodes.add(temp_mi);
    }

    //COND BIGGER
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
        info_node leftm,rightm;
        rightm = mi_info_nodes.remove(mi_info_nodes.size()-1);
        leftm  = mi_info_nodes.remove(mi_info_nodes.size()-1);
        String leftplace=leftm.place,rightplace=rightm.place;
        if(leftm.array){
            leftplace = String.format("[%s]",leftm.place);
        }
        if(rightm.array){
            rightplace = String.format("[%s]",rightm.place);
        }
        ArrayList<Integer> True = aMiddleCode.makelist(aMiddleCode.nextquad());
        aMiddleCode.genquad(">",leftplace,rightplace,"*");
        ArrayList<Integer> False = aMiddleCode.makelist(aMiddleCode.nextquad());
        aMiddleCode.genquad("jump","-","-","*");
        info_node temp_mi = new info_node("","boolean",null,True,False,false);
        mi_info_nodes.add(temp_mi);
    }

    //COND LESS
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
        info_node leftm,rightm;
        rightm = mi_info_nodes.remove(mi_info_nodes.size()-1);
        leftm  = mi_info_nodes.remove(mi_info_nodes.size()-1);
        String leftplace=leftm.place,rightplace=rightm.place;
        if(leftm.array){
            leftplace = String.format("[%s]",leftm.place);
        }
        if(rightm.array){
            rightplace = String.format("[%s]",rightm.place);
        }
        ArrayList<Integer> True = aMiddleCode.makelist(aMiddleCode.nextquad());
        aMiddleCode.genquad("<",leftplace,rightplace,"*");
        ArrayList<Integer> False = aMiddleCode.makelist(aMiddleCode.nextquad());
        aMiddleCode.genquad("jump","-","-","*");
        info_node temp_mi = new info_node("","boolean",null,True,False,false);
        mi_info_nodes.add(temp_mi);
    }

    //COND LEQ
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
        info_node leftm,rightm;
        rightm = mi_info_nodes.remove(mi_info_nodes.size()-1);
        leftm  = mi_info_nodes.remove(mi_info_nodes.size()-1);
        String leftplace=leftm.place,rightplace=rightm.place;
        if(leftm.array){
            leftplace = String.format("[%s]",leftm.place);
        }
        if(rightm.array){
            rightplace = String.format("[%s]",rightm.place);
        }
        ArrayList<Integer> True = aMiddleCode.makelist(aMiddleCode.nextquad());
        aMiddleCode.genquad("<=",leftplace,rightplace,"*");
        ArrayList<Integer> False = aMiddleCode.makelist(aMiddleCode.nextquad());
        aMiddleCode.genquad("jump","-","-","*");
        info_node temp_mi = new info_node("","boolean",null,True,False,false);
        mi_info_nodes.add(temp_mi);
    }

    //COND BEQ
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
        info_node leftm,rightm;
        rightm = mi_info_nodes.remove(mi_info_nodes.size()-1);
        leftm  = mi_info_nodes.remove(mi_info_nodes.size()-1);
        String leftplace=leftm.place,rightplace=rightm.place;
        if(leftm.array){
            leftplace = String.format("[%s]",leftm.place);
        }
        if(rightm.array){
            rightplace = String.format("[%s]",rightm.place);
        }
        ArrayList<Integer> True = aMiddleCode.makelist(aMiddleCode.nextquad());
        aMiddleCode.genquad(">=",leftplace,rightplace,"*");
        ArrayList<Integer> False = aMiddleCode.makelist(aMiddleCode.nextquad());
        aMiddleCode.genquad("jump","-","-","*");
        info_node temp_mi = new info_node("","boolean",null,True,False,false);
        mi_info_nodes.add(temp_mi);
    }

    //COND NOT
    @Override
    public void outACondnotNotCond(ACondnotNotCond node)
    {
        info_node rightm;
        rightm = mi_info_nodes.remove(mi_info_nodes.size()-1);
        info_node temp_mi = new info_node("","boolean",null,rightm.False,rightm.True,false);
        mi_info_nodes.add(temp_mi);
    }

    //COND AND
    @Override
    public void caseACondandAndCond(ACondandAndCond node)
    {
        info_node leftm,rightm;
        inACondandAndCond(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        leftm  = mi_info_nodes.remove(mi_info_nodes.size()-1);
        aMiddleCode.backpatch(leftm.True,aMiddleCode.nextquad());
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        rightm = mi_info_nodes.remove(mi_info_nodes.size()-1);
        ArrayList<Integer> True = rightm.True;
        ArrayList<Integer> False = aMiddleCode.merge(leftm.False,rightm.False);
        info_node temp_mi = new info_node("","boolean",null,True,False,false);
        mi_info_nodes.add(temp_mi);
        outACondandAndCond(node);
    }

    //COND OR
    @Override
    public void caseACondOrCond(ACondOrCond node)
    {
        info_node leftm,rightm;
        inACondOrCond(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        leftm  = mi_info_nodes.remove(mi_info_nodes.size()-1);
        aMiddleCode.backpatch(leftm.False,aMiddleCode.nextquad());
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        rightm = mi_info_nodes.remove(mi_info_nodes.size()-1);
        ArrayList<Integer> True = aMiddleCode.merge(leftm.True,rightm.True);
        ArrayList<Integer> False = rightm.False;
        info_node temp_mi = new info_node("","boolean",null,True,False,false);
        mi_info_nodes.add(temp_mi);
        outACondOrCond(node);
    }

    //STMT

    //STMT IF
    @Override
    public void caseAStmtIfStmt(AStmtIfStmt node)
    {
        info_node stmt=null,stmt_else=null,cond=null;
        ArrayList<Integer> L1=null,L2=null;
        inAStmtIfStmt(node);
        if(node.getCond() != null)
        {
            node.getCond().apply(this);
            cond=mi_info_nodes.remove(mi_info_nodes.size()-1);
            aMiddleCode.backpatch(cond.True,aMiddleCode.nextquad());
            L1 = cond.False;
            L2 = aMiddleCode.emptylist();
        }
        {
            List<PStmt> copy = new ArrayList<PStmt>(node.getThen());
            for(PStmt e : copy)
            {
                e.apply(this);
                stmt=mi_info_nodes.remove(mi_info_nodes.size()-1);
            }
        }
        {
            List<PStmt> copy = new ArrayList<PStmt>(node.getElse());
            for(PStmt e : copy)
            {
                L1 = aMiddleCode.makelist(aMiddleCode.nextquad());
                aMiddleCode.genquad("jump","-","-","*");
                aMiddleCode.backpatch(cond.False,aMiddleCode.nextquad());
                e.apply(this);
                stmt_else=mi_info_nodes.remove(mi_info_nodes.size()-1);
                L2 =stmt_else.Next;
            }
        }
        ArrayList <Integer> L = aMiddleCode.merge(aMiddleCode.merge(L1,stmt.Next),L2);
        info_node temp_mi = new info_node("","stmt",L,null,null,false);
        mi_info_nodes.add(temp_mi);
        outAStmtIfStmt(node);
    }

    //STMT SEMI
    @Override
    public void outAStmtSemiStmt(AStmtSemiStmt node)
    {
        info_node temp_mi = new info_node("","stmt",null,null,null,false);
        mi_info_nodes.add(temp_mi);
    }

    //STMT WHILE
    @Override
    public void caseAStmtWhileStmt(AStmtWhileStmt node)
    {
        info_node stmt=null,cond=null;
        inAStmtWhileStmt(node);
        int Q = aMiddleCode.nextquad();
        if(node.getCond() != null)
        {
            node.getCond().apply(this);
            cond=mi_info_nodes.remove(mi_info_nodes.size()-1);
            aMiddleCode.backpatch(cond.True,aMiddleCode.nextquad());
        }
        if(node.getStmt() != null)
        {
            node.getStmt().apply(this);
            stmt=mi_info_nodes.remove(mi_info_nodes.size()-1);
            aMiddleCode.backpatch(stmt.Next,Q);
            aMiddleCode.genquad("jump","-","-",String.format("%d",Q));
        }
        info_node temp_mi = new info_node("","stmt",cond.False,null,null,false);
        mi_info_nodes.add(temp_mi);
        outAStmtWhileStmt(node);
    }

    //STMT ASSIGN
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
            else{
                info_node assign_m,expr_m;
                expr_m = mi_info_nodes.remove(mi_info_nodes.size()-1);
                assign_m = mi_info_nodes.remove(mi_info_nodes.size()-1);
                String exprplace=expr_m.place,assignplace=assign_m.place;
                if(expr_m.array){
                    exprplace=String.format("[%s]",expr_m.place);
                }
                if(assign_m.array){
                    assignplace=String.format("[%s]",assign_m.place);
                }
                aMiddleCode.genquad(":=",exprplace,"-",assignplace);
                info_node temp_mi = new info_node("","stmt",aMiddleCode.emptylist(),null,null,false);
                mi_info_nodes.add(temp_mi);
            }
        }
    }

    //STMT FUNC CALL
    @Override
    public void caseAFuncCall(AFuncCall node)
    {
        inAFuncCall(node);
        SymbolTable.SymbolTableRecord foundSymbol;
        if(node.getTId() != null)
        {
            node.getTId().apply(this);
        }
        {
            foundSymbol=aSymbolTable.lookup(node.getTId().toString().trim());
            String name= node.getTId().toString().trim();
            if(foundSymbol == null){
                error = String.format("id \"%s\" is not declared in this scope",node.getTId().toString().trim());
                aSymbolTable.print_error(node.getTId().getLine(),node.getTId().getPos(),error);
            }
            else{
                if(!foundSymbol.type.equals( "fun")){
                    error = String.format("id \"%s\" <%s> (%d) is not a function",node.getTId().toString().trim(),foundSymbol.type,foundSymbol.array_sizes.size());
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
            }
            List<PExpr> copy = new ArrayList<PExpr>(node.getExpr());
            int arg_index = 0,arg_elements=0,i=0;
            argument temp;
            type_info arg,argf;
            for(PExpr e : copy)
            {
                e.apply(this);
                arg = type_stack.remove(type_stack.size()-1);
                temp = foundSymbol.arg_types.get(arg_index);
                argf = new type_info(0,0,temp.Type,temp.Type,temp.array_sizes.size(),0,false);
                if(temp.ref && arg.Type.equals("int_const")){
                    error = String.format("expected <%s> (%d) ,found \"%s\" <%s> (%d) in argument (%d) of function \"%s\"",argf.Type,argf.array_max_dim-argf.array_cur_dim,arg.name,arg.Type,arg.array_max_dim-arg.array_cur_dim,i+1,foundSymbol.name);
                    aSymbolTable.print_error(arg.line,arg.pos,error);
                }
                else if(temp.ref && arg.Type.equals("char_const")){
                    error = String.format("expected <%s> (%d) ,found \"%s\" <%s> (%d) in argument (%d) of function \"%s\"",argf.Type,argf.array_max_dim-argf.array_cur_dim,arg.name,arg.Type,arg.array_max_dim-arg.array_cur_dim,i+1,foundSymbol.name);
                    aSymbolTable.print_error(arg.line,arg.pos,error);
                }
                if(!equiv(arg,argf)){
                    error = String.format("expected <%s> (%d) ,found \"%s\" <%s> (%d) in argument (%d) of function \"%s\"",argf.Type,argf.array_max_dim-argf.array_cur_dim,arg.name,arg.Type,arg.array_max_dim-arg.array_cur_dim,i+1,foundSymbol.name);
                    aSymbolTable.print_error(arg.line,arg.pos,error);
                }
                //middlecode start
                info_node expr;
                expr= mi_info_nodes.remove(mi_info_nodes.size()-1);
                String place=expr.place;
                if(expr.array){
                    place=String.format("[%s]",expr.place);
                }
                if(temp.ref){
                    aMiddleCode.genquad("par",place,"R","-");
                }
                else{
                    aMiddleCode.genquad("par",place,"V","-");
                }
                //middlecode end
                arg_elements++;
                if(arg_elements == foundSymbol.arg_types.get(arg_index).ids.size()){
                    arg_elements=0;
                    arg_index++;
                }
                i++;
            }
            type_info return_type = new type_info(node.getTId().getLine(),node.getTId().getPos(),node.getTId().toString().trim(),foundSymbol.ret_type,0,0,false);
            type_stack.add(return_type);
            if(!foundSymbol.ret_type.equals("nothing")){
                W = aMiddleCode.newtemp(foundSymbol.ret_type,foundSymbol.ret_type);
                aMiddleCode.genquad("par",W,"RET","-");
                info_node temp_mi = new info_node(W,"stmt",null,null,null,false);
                mi_info_nodes.add(temp_mi);
            }
            else{
                info_node temp_mi = new info_node("","stmt",null,null,null,false);
                mi_info_nodes.add(temp_mi);
            }
            aMiddleCode.genquad("call","-","-",String.format("%s_%d",foundSymbol.name,foundSymbol.Depth));
        }
        outAFuncCall(node);
    }

    //RETURN STMT
    @Override
    public void outAStmtReturnStmt(AStmtReturnStmt node)
    {
        type_info expr;
        fun_name_type temp_fun_info;
        temp_fun_info= function_stack.get(function_stack.size()-1);
        String place;
        if(temp_fun_info.type.equals("nothing")){
            if(node.getExpr()!= null){
                expr = type_stack.remove(type_stack.size()-1);
                error = String.format("returning %s (%d) <%s> (%d) in function \"%s\" that has return type <nothing>",expr.name,expr.array_cur_dim,expr.Type,expr.array_max_dim,temp_fun_info.name);
                aSymbolTable.print_error(expr.line,expr.pos,error);
            }
            aMiddleCode.genquad("ret","-","-","-");
            info_node temp_mi = new info_node("","stmt",null,null,null,false);
            mi_info_nodes.add(temp_mi);
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
                return_check=true;
                //System.out.printf("function %s\n",temp_fun_info.name);
                info_node expr_m =  mi_info_nodes.remove(mi_info_nodes.size()-1);
                place=expr_m.place;
                if(expr_m.array){
                    place=String.format("[%s]",expr_m.place);
                }
                aMiddleCode.genquad(":=",place,"-","$$");
                aMiddleCode.genquad("ret","-","-","-");
                info_node temp_mi = new info_node("","stmt",null,null,null,false);
                mi_info_nodes.add(temp_mi);
            }
            else{
                error = String.format("return statement returning nothing found in function \"%s\" that has return type <%s> ",temp_fun_info.name,temp_fun_info.type);
                aSymbolTable.print_error(node.getTReturn().getLine(),node.getTReturn().getPos(),error);
            }
        }
    }

    //STMT BLOCK
    @Override
    public void caseAStmtBlockStmt(AStmtBlockStmt node)
    {
        ArrayList<Integer> L=aMiddleCode.emptylist();
        inAStmtBlockStmt(node);
        {
            info_node a_stmt;
            List<PStmt> copy = new ArrayList<PStmt>(node.getStmt());
            for(PStmt e : copy)
            {
                e.apply(this);
                a_stmt=mi_info_nodes.remove(mi_info_nodes.size()-1);
                L = a_stmt.Next;
                aMiddleCode.backpatch(L,aMiddleCode.nextquad());
            }
            info_node temp_mi = new info_node("","block",L,null,null,false);
            mi_info_nodes.add(temp_mi);
        }
        outAStmtBlockStmt(node);
    }

}

