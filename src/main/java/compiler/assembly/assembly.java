package compiler;
import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;
import java.io.*;
import java.util.*;

public class assembly{

    public class assembly_comm{
        String op;
        String a;
        String b;
        boolean tab;
        public assembly_comm(String op,String a,String b,boolean tab){
            this.op=op;
            this.a=a;
            this.b=b;
            this.tab=tab;
        }
    }

    public ArrayList<assembly_comm> instructions=new ArrayList<assembly_comm>();

    public void add_comm(String op,String a,String b,boolean tab){
        assembly_comm temp=new assembly_comm(op,a,b,tab);
        instructions.add(temp);
    }

    public void print_comms(){
        String tab="";
        for(assembly_comm e : instructions){
            if(e.tab){
                tab="\t";
            }
            else tab="";
            if(!e.a.equals("")&&!e.b.equals("")){
                System.out.printf("%s%s %s,%s\n",tab,e.op,e.a,e.b);
            }
            else if(!e.a.equals("")){
                System.out.printf("%s%s %s\n",tab,e.op,e.a);
            }
            else{
                System.out.printf("%s%s\n",tab,e.op,e.a);
            }
        }
    }
    //helper functions
    public void create_assembly_file(){

    }
    public void append_to(){

    }

    public String load(String register,String Value,String Type,String passway,boolean arg,boolean local){
        String as="";
        if(!arg){
            if(Type.equals("int_const")){
                as = String.format("mov %s,%s",register,Value);
            }
            else if(Type.equals("char_const")){
                as = String.format("mov %s,ASCII(%s)",register,Value);
            }
            else{
                if(local){
                    as = String.format("mov %s,ASCII(%s)",register,Value);
                }
            }
        }
        else{

        }
        return as;
    }
}
