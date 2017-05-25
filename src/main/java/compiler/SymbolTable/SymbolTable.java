package compiler;
import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;
import java.io.*;
import java.util.*;



public class SymbolTable {
    Hashtable<String, Integer> symbol_hash;
    ArrayList<record> symbols;
    ArrayList<Integer> depths;
    int curDepth;

    public class SymbolTableRecord{
        int line,pos;
        String name;
        String type;
        String ret_type;
        ArrayList<Integer> array_sizes;
        ArrayList<argument> arg_types;
        int Depth;
        boolean ref;
        boolean declared;
        boolean arg;

        public SymbolTableRecord(int line,int pos,String name,String type,String ret_type, int Depth, boolean ref,ArrayList<Integer> array_sizes, ArrayList<argument> arg_types,boolean declared ,boolean arg){
            this.line = line;
            this.pos = pos;
            this.name=name;
            this.type=type;
            this.Depth=Depth;
            this.ref=ref;
            this.arg = arg;
            this.declared = declared;
            this.ret_type = ret_type;
            if(array_sizes!=null){
                this.array_sizes = new ArrayList<Integer>(array_sizes);
            }
            else{
                this.array_sizes = new ArrayList<Integer>();
            }
            if(arg_types!=null){
                this.arg_types = new ArrayList<argument>(arg_types);
            }
            else{
                this.arg_types = new ArrayList<argument>();
            }
        }
    }

    public void print_error(int line,int pos ,String error){
        System.out.printf("[ERROR] (l:%d,p:%d) \t: %s\n",line,pos,error);
        System.exit(0);
    }

    class record {
        SymbolTableRecord aSymbol;
        int next;
        public record(int next,int line,int pos,String name,String type,String ret_type,int Depth, boolean ref,ArrayList<Integer> array_sizes,ArrayList<argument> arg_types,boolean declared,boolean arg){
            aSymbol = new SymbolTableRecord(line,pos,name,type,ret_type,Depth,ref,array_sizes,arg_types,declared,arg);
            this.next = next;
        }
    }

    public SymbolTable(){
        symbol_hash = new Hashtable<String, Integer> ();
        symbols = new ArrayList<record>();
        depths = new ArrayList<Integer>();
        depths.add(-1);
        curDepth=0;
    }

    public void enter(){
        curDepth++;
        //System.out.printf("curDepth: %d\n",curDepth);
        depths.add(-1);
    }

    public int insert(int line, int pos,String name,String type,String ret_type,boolean ref,ArrayList<Integer> array_sizes,ArrayList<argument> arg_types,boolean declared,boolean arg){
        int prev;
        record temp;
        String error;
        if(depths.get(curDepth)==-1){
            depths.set(curDepth,symbols.size());
        }
        //System.out.printf("checking %s (%d,%d)\n",name,line,pos);
        if(symbol_hash.get(name)==null){
            //System.out.println("null");
            prev=-1;
        }
        else{
            prev=symbol_hash.get(name);
            SymbolTableRecord foundSymbol = symbols.get(prev).aSymbol;
            if (foundSymbol.Depth==curDepth){
                //System.out.println("same depth");
                if(foundSymbol.type == "fun"){
                    if(declared == false){
                        error =String.format("function \"%s\" has been redeclared (l:%d,p:%d)",name,foundSymbol.line,foundSymbol.pos);
                        print_error(line,pos,error);
                        return 1;
                    }
                    if(declared == true){
                        if(foundSymbol.declared == true){
                            error=String.format("function \"%s\" has been redefined (l:%d,p:%d)",name,foundSymbol.line,foundSymbol.pos);
                            print_error(line,pos,error);
                            return 1;
                        }
                        else{
                            if (arg_types.size() != foundSymbol.arg_types.size()){
                                error=String.format("function \"%s\" definition doesn't match function declaration (l:%d,p:%d)",name,foundSymbol.line,foundSymbol.pos);
                                print_error(line,pos,error);
                                return 1;
                            }
                            else{
                                for(int i=0;i<arg_types.size();i++){
                                    if(!arg_types.get(i).Type.equals(foundSymbol.arg_types.get(i).Type)){
                                        error=String.format("function \"%s\" definition type (%d) <%s> doesn't match ",name,i+1,arg_types.get(i).Type);
                                        error+=String.format("type (%d) <%s> of ",i+1, foundSymbol.arg_types.get(i).Type);
                                        error+=String.format("function declaration (l:%d,p:%d)",foundSymbol.line, foundSymbol.pos);
                                        print_error(line,pos,error);
                                        return 1;
                                    }
                                    if(arg_types.get(i).array_sizes.size()!=foundSymbol.arg_types.get(i).array_sizes.size()){
                                        error=String.format("function \"%s\" definition type (%d) <%s> dimension (%d) ",name,i+1,arg_types.get(i).Type,arg_types.get(i).array_sizes.size());
                                        error+=String.format("doesn't match dimension (%d) type (%d) <%s> ",foundSymbol.arg_types.get(i).array_sizes.size(), i+1, foundSymbol.arg_types.get(i).Type);
                                        error+=String.format("of function declaration (l:%d,p:%d)", foundSymbol.line, foundSymbol.pos);
                                        print_error(line,pos,error);
                                        return 1;
                                    }
                                    else{
                                        for(int j=0;j<arg_types.get(i).array_sizes.size();j++){
                                            if(arg_types.get(i).array_sizes.get(j)!=foundSymbol.arg_types.get(i).array_sizes.get(j)){
                                                if(arg_types.get(i).array_sizes.get(j)==-1){
                                                   error=String.format("function \"%s\" definition type (%d) <%s> dimension (%d) size [] ",name,i+1,arg_types.get(i).Type,arg_types.get(i).array_sizes.size());
                                                   error+=String.format("doesn't match dimension (%d) size [%d] type (%d) <%s> of",foundSymbol.arg_types.get(i).array_sizes.size(),foundSymbol.arg_types.get(i).array_sizes.get(j), i+1, foundSymbol.arg_types.get(i).Type);
                                                   error+=String.format(" function declaration (l:%d,p:%d)",  foundSymbol.line, foundSymbol.pos);
                                                   print_error(line,pos,error);
                                                }
                                                else if(foundSymbol.arg_types.get(i).array_sizes.get(j)==-1){
                                                    error=String.format("function \"%s\" definition type (%d) <%s> dimension (%d) size [%d]",name,i+1,arg_types.get(i).Type,arg_types.get(i).array_sizes.size(),arg_types.get(i).array_sizes.get(j));
                                                    error+=String.format(" doesn't match dimension (%d) size [] type (%d) <%s> ",foundSymbol.arg_types.get(i).array_sizes.size(), i+1, foundSymbol.arg_types.get(i).Type);
                                                    error+=String.format("of function declaration (l:%d,p:%d)",  foundSymbol.line, foundSymbol.pos);
                                                    print_error(line,pos,error);
                                                }
                                                else{
                                                    error=String.format("function \"%s\" definition type (%d) <%s> dimension (%d) size [%d] ",name,i+1,arg_types.get(i).Type,arg_types.get(i).array_sizes.size(),arg_types.get(i).array_sizes.get(j));
                                                    error+=String.format("doesn't match dimension (%d) size [%d] type (%d) <%s>",foundSymbol.arg_types.get(i).array_sizes.size(),foundSymbol.arg_types.get(i).array_sizes.get(j), i+1, foundSymbol.arg_types.get(i).Type);
                                                    error+=String.format(" of function declaration (l:%d,p:%d)",  foundSymbol.line, foundSymbol.pos);
                                                    print_error(line,pos,error);
                                                }
                                                return 1;
                                            }
                                        }
                                    }
                                    //numbers of arguments of above type
                                    if(arg_types.get(i).ids.size()!=foundSymbol.arg_types.get(i).ids.size()){
                                        error=String.format("function \"%s\" definition arguments (n:%d) of type (%d) <%s> dimension (%d) doesn't match function ",name,arg_types.get(i).ids.size(),i+1,arg_types.get(i).Type,arg_types.get(i).array_sizes.size());
                                        error+=String.format("declaration arguments (n:%d) of type (%d) <%s> dimension (%d) (l:%d,p:%d)",foundSymbol.arg_types.get(i).ids.size(),i+1, foundSymbol.arg_types.get(i).Type,foundSymbol.arg_types.get(i).array_sizes.size(),foundSymbol.line,foundSymbol.pos);
                                        print_error(line,pos,error);
                                        return 1;
                                    }
                                }
                            }
                            //modify existing fun record in symbol table
                            foundSymbol.declared = true;
                            return 0;
                        }
                    }
                }
                else{
                    error= String.format("id \"%s\" <%s> has been redefined (l:%d,p:%d) <%s>",name,type,foundSymbol.line,foundSymbol.pos,foundSymbol.type);
                    print_error(line,pos,error);
                    return 1;
                }
            }
            else{
                if(foundSymbol.type.equals("fun")){
                    error =String.format("overshadowing \"%s\" <%s> (l:%d,p:%d) with \"%s\" <%s> is not permitted",foundSymbol.name,foundSymbol.type,foundSymbol.line,foundSymbol.pos,name,type);
                    print_error(line,pos,error);
                    return 1;
                }
                else if(type.equals("fun")){
                    error =String.format("overshadowing \"%s\" <%s> (l:%d,p:%d) with \"%s\" <%s> is not permitted",foundSymbol.name,foundSymbol.type,foundSymbol.line,foundSymbol.pos,name,type);
                    print_error(line,pos,error);
                    return 1;
                }
            }
        }
        //new record in symbol table
        symbol_hash.put(name,symbols.size());
        temp = new record(prev,line,pos,name,type,ret_type,curDepth,ref,array_sizes,arg_types,declared,arg);
        symbols.add(temp);
        return 0;
    }

    public void add_basic_functions(){
        ArrayList<argument> args = new ArrayList<argument>();
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<Integer> b_array_sizes=new ArrayList<Integer>();
        ids.add("n");
        argument puti_arg = new argument("int",ids,b_array_sizes,false);
        args.add(puti_arg);
        this.insert(0,0,"puti","fun","nothing",false,null,args,true,false);
        ids.clear();
        args.clear();
        ids.add("c");
        argument putc_arg = new argument("char",ids,b_array_sizes,false);
        args.add(putc_arg);
        this.insert(0,0,"putc","fun","nothing",false,null,args,true,false);
        ids.clear();
        args.clear();
        ids.add("s");
        b_array_sizes.add(-1);
        argument puts_arg = new argument("char",ids,b_array_sizes,true);
        args.add(puts_arg);
        this.insert(0,0,"puts","fun","nothing",false,null,args,true,false);
        b_array_sizes.clear();
        args.clear();
        this.insert(0,0,"geti","fun","int",false,null,args,true,false);
        this.insert(0,0,"getc","fun","char",false,null,args,true,false);
        ids.clear();
        ids.add("n");
        argument gets_arg1 = new argument("int",ids,b_array_sizes,false);
        args.add(gets_arg1);
        ids.clear();
        ids.add("s");
        b_array_sizes.add(-1);
        argument gets_arg2 = new argument("char",ids,b_array_sizes,true);
        args.add(gets_arg2);
        this.insert(0,0,"gets","fun","char",false,null,args,true,false);
    }

    public SymbolTableRecord lookup(String name){
        if(symbol_hash.get(name)==null){
            return null;
        }
        else{
            int pos=symbol_hash.get(name);
            return symbols.get(pos).aSymbol;
        }
    }

    public void exit(){
        if(depths.get(depths.size()-1)==-1){
            depths.remove(depths.size()-1);
            curDepth-=1;
            //System.out.printf("curDepth: %d\n",curDepth);
            return;
        }else{
            int iterations=symbols.size()-depths.get(depths.size()-1); // number of elements in the last block all symbols - index of fisrt symbol in block
            String error;
            record rec;
            while(iterations!=0){
                rec=symbols.get(symbols.size() - 1);
                if(rec.aSymbol.type == "fun" && rec.aSymbol.declared == false){
                    error = String.format("function \"%s\" declared but not defined",rec.aSymbol.name);
                    print_error(rec.aSymbol.line,rec.aSymbol.pos,error);
                }
                if(rec.next!=-1){
                    //TODO make it more efficient
                    symbol_hash.put(rec.aSymbol.name,rec.next);
                    symbols.remove(symbols.size() - 1);
                }
                else{
                    symbol_hash.remove(rec.aSymbol.name);
                    symbols.remove(symbols.size() - 1);
                }
                iterations-=1;
            }
            depths.remove(depths.size()-1);
            curDepth-=1;
            //System.out.printf("curDepth: %d\n",curDepth);
            return;
        }
    }
}

