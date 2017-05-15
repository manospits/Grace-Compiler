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

        public SymbolTableRecord(int line,int pos,String name,String type,String ret_type, int Depth, boolean ref,ArrayList<Integer> array_sizes, ArrayList<argument> arg_types,boolean declared ){
            this.line = line;
            this.pos = pos;
            this.name=name;
            this.type=type;
            this.Depth=Depth;
            this.ref=ref;
            this.declared = declared;
            this.ret_type = ret_type;
            if(array_sizes!=null){
                this.array_sizes = new ArrayList<Integer>(array_sizes);
            }
            if(arg_types!=null){
                this.arg_types = new ArrayList<argument>(arg_types);
            }

        }
    }

    public void print_error(int line,int pos ,String error){
        System.out.printf("Error found (l:%d,p:%d) : %s\n",line,pos,error);
    }

    class record {
        SymbolTableRecord aSymbol;
        int next;
        public record(int next,int line,int pos,String name,String type,String ret_type,int Depth, boolean ref,ArrayList<Integer> array_sizes,ArrayList<argument> arg_types,boolean declared){
            aSymbol = new SymbolTableRecord(line,pos,name,type,ret_type,Depth,ref,array_sizes,arg_types,declared);
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

    public int insert(int line, int pos,String name,String type,String ret_type,boolean ref,ArrayList<Integer> array_sizes,ArrayList<argument> arg_types,boolean declared){
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
                        error =String.format("function \"%s\" has been redeclared in (l:%d,p:%d)",name,foundSymbol.line,foundSymbol.pos);
                        print_error(line,pos,error);
                        return 1;
                    }
                    if(declared == true){
                        if(foundSymbol.declared == true){
                            error=String.format("function \"%s\" has been redefined in (l:%d,p:%d)",name,foundSymbol.line,foundSymbol.pos);
                            print_error(line,pos,error);
                            return 1;
                        }
                        else{
                            //modify existing fun record in symbol table
                            foundSymbol.declared = true;
                            return 0;
                        }
                    }
                }
                else{
                    error= String.format("id \"%s\" <%s> has been redefined in (l:%d,p:%d)",name,type,foundSymbol.line,foundSymbol.pos);
                    print_error(line,pos,error);
                    return 1;
                }
            }
        }
        //new record in symbol table
        symbol_hash.put(name,symbols.size());
        temp = new record(prev,line,pos,name,type,ret_type,curDepth,ref,array_sizes,arg_types,declared);
        symbols.add(temp);
        return 0;
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

