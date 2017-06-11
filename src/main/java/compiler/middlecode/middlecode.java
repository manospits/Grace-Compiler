package compiler;
import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;
import java.io.*;
import java.util.*;

public class middlecode{
    int start_address=0;

    public class quad{
        String op;
        String x;
        String y;
        String z;
        public quad(String op,String x,String y,String z){
            this.op=op;
            this.x=x;
            this.y=y;
            this.z=z;
        }
    }

    public class vars_range{
        int from;
        int to;
        public vars_range(int from,int to){
            this.from=from;
            this.to=to;
        }
    }

    class var_info{
        String type;
        int address;
        public var_info(String type,int address){
            this.type=type;
            this.address=address;
        }
    }

    int temp_variable_index = 0;
    ArrayList<quad> quads = new ArrayList<quad>();
    ArrayList<var_info> vars = new ArrayList <var_info>();
    Map<String,vars_range> function_vars = new HashMap<String,vars_range>();

    public int get_var_index(){
        return temp_variable_index;
    }

    public void set_start_address(int a){
        start_address=a;
    }

    public void add_range(String fun_name,int from,int to){
        vars_range temp = new vars_range(from,to);
        function_vars.put(fun_name,temp);
    }

    public int nextquad(){
        return quads.size();
    }

    public int genquad(String op,String x,String y,String z){
        quad temp_quad = new quad(op,x,y,z);
        //System.out.printf("%s,%s,%s,%s\n",op,x,y,z);
        quads.add(temp_quad);
        return quads.size()-1;
    }

    public String newtemp(String Type){
        String Name = String.format("$%d",temp_variable_index++);
        int address,pad;
        var_info temp_var;
        address=start_address;
        if(Type.equals("char")){
            start_address+=1;
        }else if (Type.equals("int")){
            pad=start_address%4;
            address+=pad;
            start_address+=1;
        }
        temp_var=new var_info(Type,address);
        vars.add(temp_var);
        return Name;
    }

    public ArrayList<Integer> emptylist(){
        ArrayList<Integer> tmp_list = new ArrayList<Integer>();
        return tmp_list;
    }

    public ArrayList<Integer> makelist(int x){
        ArrayList<Integer> tmp_list = new ArrayList<Integer>();
        tmp_list.add(x);
        return tmp_list;
    }

    public ArrayList<Integer> merge(ArrayList<Integer> a ,ArrayList<Integer> b){
        ArrayList<Integer> tmp_quads = new ArrayList<Integer>();
        tmp_quads.addAll(a);
        tmp_quads.addAll(b);
        return tmp_quads;
    }

    public void backpatch(ArrayList<Integer> l,int z){
        for(int e: l){
            quad temp_quad=quads.get(e);
            if(temp_quad.z.equals("*")){
                temp_quad.z = String.format("%d",z);
                //System.out.printf("z turned to %d\n",z);
            }
        }
    }

    void print_quads(){
        int i=0;
        for(quad q: quads){
            System.out.printf("%d:\t%s",i,String.format("%s,%s,%s,%s\n",q.op,q.x,q.y,q.z));
            i++;
        }
    }
}
