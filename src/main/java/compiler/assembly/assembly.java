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

    public boolean  check_array(String a){
        if(a.charAt(0)=='[' && a.charAt(a.length())==']'){
            return true;
        }
        else return false;
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
    public void create_assembly_file(String name){
        try{
            PrintWriter writer = new PrintWriter(name, "UTF-8");
            String tab="";
            for(assembly_comm e : instructions){
                if(e.tab){
                    tab="\t";
                }
                else tab="";
                if(!e.a.equals("")&&!e.b.equals("")){
                    writer.printf("%s%s %s,%s\n",tab,e.op,e.a,e.b);
                }
                else if(!e.a.equals("")){
                    writer.printf("%s%s %s\n",tab,e.op,e.a);
                }
                else{
                    writer.printf("%s%s\n",tab,e.op,e.a);
                }
            }
            writer.close();
        }catch (IOException e) {

        }
    }

    public void updateAl(int p,int x){
        if(p<x){
            add_comm("push","ebp","",true);
        }
        else if(p==x){
            add_comm("push","DWORD ptr [ebp+8]","",true);
        }
        else{
            int diff = p - x;
            add_comm("mov","esi","DWORD ptr [ebp+8]",true);
            for(int i=1;i<diff;i++){
                add_comm("mov","esi","DWORD ptr [esi+8]",true);
            }
            add_comm("push","DWORD ptr [esi+8]","",true);
        }
    }

    public void getAr(int p,int x){
        int diff = p - x;
        add_comm("mov","esi","DWORD ptr [ebp+8]",true);
        for(int i=1;i<diff;i++){
            add_comm("mov","esi","DWORD ptr [esi+8]",true);
        }
    }

    public void load(String R,String a,String Type,boolean ref,boolean arg,boolean constant,String address,int np,int na ){
        String ac="",R1="ebp";
        if(constant){
            if(Type.equals("int")){
                add_comm("mov",R,a,true);
            }
            if(Type.equals("char")){
                add_comm("mov",R,String.format("ASCII(%s)"),true);
            }
        }
        else{
            if(arg){
                ac="+";
            } else{
                ac="-";
            }
            if((np-na)!=0){
                getAr(np,na);
                R1="esi";
            }
            if(!ref){
                if(Type.equals("int")){
                    add_comm("mov",R,String.format("DWORD ptr[%s %s %d]",R1,ac,address),true);
                }
                else if(Type.equals("char")){
                    add_comm("mov",R,String.format("WORD ptr[%s %s %d]",R1,ac,address),true);
                }
            }
            else{
                add_comm("mov","esi",String.format("DWORD ptr[%s %s %d]",R1,ac,address),true);
                if(Type.equals("int")){
                    add_comm("mov",R,String.format("DWORD ptr[esi %s %d]",R1,ac,address),true);
                }
                else if(Type.equals("char")){
                    add_comm("mov",R,String.format("WORD ptr[esi %s %d]",ac,address),true);
                }
            }
        }
    }
}
