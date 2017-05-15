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
        String name;
        String type;
        String ret_type;
        ArrayList<Integer> array_sizes;
        ArrayList<argument> arg_types;
        int Depth;
        boolean ref;
        boolean declared;

        public SymbolTableRecord(String name,String type,String ret_type, int Depth, boolean ref,ArrayList<Integer> array_sizes, ArrayList<argument> arg_types,boolean declared ){
            this.name=name;
            this.type=type;
            this.Depth=Depth;
            this.ref=ref;
            if(array_sizes!=null){
                this.array_sizes = new ArrayList<Integer>(array_sizes);
            }
            if(arg_types!=null){
                this.arg_types = new ArrayList<argument>(arg_types);
            }

        }
    }

    class record {
        SymbolTableRecord aSymbol;
        int next;
        public record(int next,String name,String type,String ret_type,int Depth, boolean ref,ArrayList<Integer> array_sizes,ArrayList<argument> arg_types,boolean declared){
            aSymbol = new SymbolTableRecord(name,type,ret_type,Depth,ref,array_sizes,arg_types,declared);
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
        System.out.printf("curDepth: %d\n",curDepth);
        depths.add(-1);
    }

    public int insert(String name,String type,String ret_type,boolean ref,ArrayList<Integer> array_sizes,ArrayList<argument> arg_types,boolean declared){
        int prev;
        record temp;
        if(depths.get(curDepth)==-1){
            depths.set(curDepth,symbols.size());
        }
        if(symbol_hash.get(name)==null){
            prev=-1;
        }
        else{
            prev=symbol_hash.get(name);
            if (symbols.get(prev).aSymbol.Depth==curDepth){
                return 1;
            }
            if(symbols.get(prev).aSymbol.type == "fun"){
                return 1;
            }
        }
        symbol_hash.put(name,symbols.size());
        temp = new record(prev,name,type,ret_type,curDepth,ref,array_sizes,arg_types,declared);
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
            System.out.printf("curDepth: %d\n",curDepth);
            return;
        }else{
            int iterations=symbols.size()-depths.get(depths.size()-1); // number of elements in the last block all symbols - index of fisrt symbol in block
            while(iterations!=0){
                if(symbols.get(symbols.size() - 1).next!=-1){
                    //TODO make it more efficient
                    symbol_hash.put(symbols.get(symbols.size()-1).aSymbol.name,symbols.get(symbols.size() - 1).next);
                    symbols.remove(symbols.size() - 1);
                }
                else{
                    symbol_hash.remove(symbols.get(symbols.size()-1).aSymbol.name);
                    symbols.remove(symbols.size() - 1);
                }
                iterations-=1;
            }
            depths.remove(depths.size()-1);
            curDepth-=1;
            System.out.printf("curDepth: %d\n",curDepth);
            return;
        }
    }
}

