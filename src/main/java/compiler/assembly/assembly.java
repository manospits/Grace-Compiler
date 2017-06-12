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

    String lib= "puti_0:\n"
               +"    push ebp\n"
               +"    mov ebp, esp\n"
               +"    mov eax, DWORD PTR [ebp + 16]\n"
               +"    push eax\n"
               +"    mov eax, OFFSET FLAT:pi\n"
               +"    push eax\n"
               +"    call printf\n"
               +"    add esp,4\n"
               +"    mov esp, ebp\n"
               +"    pop ebp\n"
               +"    ret\n"
               +".data\n"
               +"    pi: .asciz \"%d\"";

    public class as_type{
        String a;
        String Type;
        String label;
        boolean ref;
        boolean arg;boolean constant;
        int address;
        int np;
        int na;
        public as_type(String a,String Type,boolean ref,boolean arg,boolean constant,int address,int np,int na ,String label){
            this.a=a;
            this.Type=Type;
            this.ref=ref;
            this.arg=arg;
            this.constant=constant;
            this.address=address;
            this.np=np;
            this.na=na;
            this.label=label;
        }
    }

    public as_type fill_as_type(String a,String Type,boolean ref,boolean arg,boolean constant,int address,int np,int na,String label){
        as_type temp= new as_type(a,Type,ref,arg,constant,address,np,na,label);
        return temp;
    }

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
            writer.printf("\n%s\n",lib);
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
            for(int i=1;i<diff+1;i++){
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

    public void load(String R,as_type as){
        String ac="",R1="ebp";
        int address=as.address;
        if(as.constant){
            if(as.Type.equals("int")){
                add_comm("mov",R,as.a,true);
            }
            if(as.Type.equals("char")){
                add_comm("mov",R,String.format("%s"),true);
            }
        }
        else{
            if(as.arg){
                ac="+";
                address+=16;
                if((as.np-as.na)!=0){
                    getAr(as.np,as.na);
                    R1="esi";
                }
                if(!as.ref){
                    if(as.Type.equals("int")){
                        add_comm("mov",R,String.format("DWORD ptr[%s %s %d]",R1,ac,address),true);
                    }
                    else if(as.Type.equals("char")){
                        add_comm("movzx",R,String.format("BYTE ptr[%s %s %d]",R1,ac,address),true);
                    }
                }
                else{
                    add_comm("mov","esi",String.format("DWORD ptr[%s %s %d]",R1,ac,address),true);
                    if(as.Type.equals("int")){
                        add_comm("mov",R,String.format("DWORD ptr[esi %s %d]",R1,ac,address),true);
                    }
                    else if(as.Type.equals("char")){
                        add_comm("movzx",R,String.format("BYTE ptr[esi %s %d]",ac,address),true);
                    }
                }
            }
            else{
                ac="-";
                address+=4;
                if((as.np-as.na)!=0){
                    getAr(as.np,as.na);
                    R1="esi";
                }
                if(!as.ref){
                    if(as.Type.equals("int")){
                        add_comm("mov",R,String.format("DWORD ptr[%s %s %d]",R1,ac,address),true);
                    }
                    else if(as.Type.equals("char")){
                        add_comm("movzx",R,String.format("BYTE ptr[%s %s %d]",R1,ac,address),true);
                    }
                }
                else{
                    add_comm("mov","esi",String.format("DWORD ptr[%s %s %d]",R1,ac,address),true);
                    if(as.Type.equals("int")){
                        add_comm("mov",R,String.format("DWORD ptr[esi %s %d]",R1,ac,address),true);
                    }
                    else if(as.Type.equals("char")){
                        add_comm("movzx",R,String.format("BYTE ptr[esi %s %d]",ac,address),true);
                    }
                }
            }
        }
    }

    public void load_addr(String R,as_type as){
        String ac="",R1="ebp";
        String address="";
        if(as.constant){
            if(as.Type.equals("string")){
                add_comm("lea",R,String.format("OFFSET FLAT:%s",as.label),true);
            }
        }
        else{
            if(as.arg){
                ac="+";
                address+=16;
                if((as.np-as.na)!=0){
                    getAr(as.np,as.na);
                    R1="esi";
                }
                if(!as.ref){
                    if(as.Type.equals("int")){
                        add_comm("lea",R,String.format("DWORD ptr[%s %s %d]",R1,ac,address),true);
                    }
                    else if(as.Type.equals("char")){
                        add_comm("lea",R,String.format("DWORD ptr[%s %s %d]",R1,ac,address),true);
                    }
                }
                else{
                    if(as.Type.equals("int")){
                        add_comm("mov",R,String.format("DWORD ptr[%s %s %d]",R1,ac,address),true);
                    }
                    else if(as.Type.equals("char")){
                        add_comm("mov",R,String.format("DWORD ptr[%s %s %d]",R1,ac,address),true);
                    }
                }
            }
            else{
                ac="-";
                address+=4;
                if((as.np-as.na)!=0){
                    getAr(as.np,as.na);
                    R1="esi";
                }
                if(!as.ref){
                    if(as.Type.equals("int")){
                        add_comm("lea",R,String.format("DWORD ptr[%s %s %d]",R1,ac,address),true);
                    }
                    else if(as.Type.equals("char")){
                        add_comm("lea",R,String.format("DWORD ptr[%s %s %d]",R1,ac,address),true);
                    }
                }
                else{
                    if(as.Type.equals("int")){
                        add_comm("mov",R,String.format("DWORD ptr[%s %s %d]",R1,ac,address),true);
                    }
                    else if(as.Type.equals("char")){
                        add_comm("mov",R,String.format("DWORD ptr[%s %s %d]",R1,ac,address),true);
                    }
                }
            }
        }
    }

    public void store(String R,as_type as){
        String ac="",R1="ebp";
        int address=0;
        if(as.constant){
            if(as.Type.equals("String")){
                //TODO
            }
        }
        else{
            if(as.arg){
                ac="+";
                address+=16;
                if((as.np-as.na)!=0){
                    getAr(as.np,as.na);
                    R1="esi";
                }
                if(!as.ref){
                    if(as.Type.equals("int")){
                        add_comm("mov",String.format("DWORD ptr[%s %s %d]",R1,ac,address),R,true);
                    }
                    else if(as.Type.equals("char")){
                        add_comm("mov",String.format("BYTE ptr[%s %s %d]",R1,ac,address),R,true);
                    }
                }
                else{
                    add_comm("mov","esi",String.format("DWORD ptr[%s %s %d]",R1,ac,address),true);
                    if(as.Type.equals("int")){
                        add_comm("mov",String.format("DWORD ptr[esi %s %d]",R1,ac,address),R,true);
                    }
                    else if(as.Type.equals("char")){
                        add_comm("mov",String.format("BYTE ptr[esi %s %d]",ac,address),R,true);
                    }
                }
            }
            else{
                ac="-";
                address+=4;
                if((as.np-as.na)!=0){
                     //System.out.printf("%d ,%d\n",as.np,as.na);
                    getAr(as.np,as.na);
                    R1="esi";
                }
                if(!as.ref){
                    if(as.Type.equals("int")){
                        add_comm("mov",String.format("DWORD ptr[%s %s %d]",R1,ac,address),R,true);
                    }
                    else if(as.Type.equals("char")){
                        add_comm("mov",String.format("BYTE ptr[%s %s %d]",R1,ac,address),R,true);
                    }
                }
                else{
                    add_comm("mov","esi",String.format("DWORD ptr[%s %s %d]",R1,ac,address),true);
                    if(as.Type.equals("int")){
                        add_comm("mov",String.format("DWORD ptr[esi %s %d]",R1,ac,address),R,true);
                    }
                    else if(as.Type.equals("char")){
                        add_comm("mov",String.format("BYTE ptr[esi %s %d]",ac,address),R,true);
                    }
                }
            }
        }
    }
}
