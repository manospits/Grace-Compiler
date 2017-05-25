package compiler;
import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;
import java.io.*;
import java.util.*;

public class middlecode{

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

    int temp_variable_index = 1;
    ArrayList<quad> quads = new ArrayList<quad>();
    ArrayList<String> var_types = new ArrayList <String>();

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
        var_types.add(Type);
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
