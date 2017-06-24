package compiler;
import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;
import java.io.*;
import java.util.*;

public class middlecode{
    int start_address=0;
    int unit_pos=0;

    int last_unit_pos(int next_unit){
        int last_pos=unit_pos;
        unit_pos=next_unit;
        return last_pos;
    }

    public int next_4(int n){
        int a=n&3;
        if(a==0) return n;
        return n+(4-a);
    }

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

    public quad get_quad(int i){
        return quads.get(i);
    }

    public class range{
        int from;
        int to;
        public range(int from,int to){
            this.from=from;
            this.to=to;
        }
    }

    public class block{
        range r;
        ArrayList<Integer> incoming;
        ArrayList<Integer> outcoming;
        public block(range r){
            this.r=r;
            incoming=new ArrayList<Integer>();
            outcoming=new ArrayList<Integer>();
        }
    }


    class var_info{
        String type;
        String pointing;
        int address;
        public var_info(String type,int address,String pointing){
            this.type=type;
            this.address=address;
            this.pointing=pointing;
        }
    }

    int temp_variable_index = 0;
    ArrayList<quad> quads = new ArrayList<quad>();
    ArrayList<var_info> vars = new ArrayList <var_info>();
    Map<String,range> function_vars = new HashMap<String,range>();
    Map<String,String> vars_value = new HashMap<String,String>();
    Map<String,String> vars_value_reverse = new HashMap<String,String>();

    public var_info get_var_info(String var){
        int place=Integer.parseInt(var.substring(1,var.length()));
        return vars.get(place);
    }
    public int get_var_index(){
        return temp_variable_index;
    }

    public int get_start_address(){
        return start_address;
    }
    public void set_start_address(int a){
        start_address=a;
    }

    public void add_range(String fun_name,int from,int to){
        range temp = new range(from,to);
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

    public String newtemp(String Type,String pointing){
        String Name = String.format("$%d",temp_variable_index++);
        int address;
        var_info temp_var;
        address=start_address;
        if(Type.equals("char")){
            start_address+=1;
        }else if (Type.equals("int")){
            address=next_4(address);
            start_address=address+4;
        }
        temp_var=new var_info(Type,start_address,pointing);
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

    Map<Integer,Integer> blocks_first = new HashMap<Integer,Integer>();

    public boolean op_is_op(quad q){
        String op=q.op;
        if(op.equals("+") ||op.equals("-") ||op.equals("*") ||op.equals("/") ||op.equals("%") ){
            return true;
        }
        return false;
    }

    public boolean op_is_branch(quad q){
        String op=q.op;
        if( op.equals("<") || op.equals(">") || op.equals("<=")|| op.equals(">=") || op.equals("=") || op.equals("#")  ){
            return true;
        }
        return false;
    }

    public boolean op_is_jump(quad q){
        String op=q.op;
        if( op.equals("jump") || op.equals("ret") || op_is_branch(q) ){
            return true;
        }
        else
            return false;
    }

    public boolean x_is_editable(quad q){
        String op=q.op;
        if( op_is_op(q) || op_is_branch(q) || op.equals("array") || op.equals(":=")){
            return true;
        }
        else if(op.equals("par") && !q.y.equals("R") && !q.y.equals("RET")){
            return true;
        }
        else
            return false;
    }

    public boolean y_is_editable(quad q){
        String op=q.op;
        if( op_is_op(q) || op_is_branch(q) || op.equals("array") ){
            return true;
        }
        return false;
    }

    public boolean op_assigns_z(quad q){
        String op = q.op;
        if(op_is_op(q) || op.equals("array") || op_is_branch(q) || op.equals(":=")){
            return true;
        }
        else
            return false;
    }

    public boolean op_assigns_x(quad q){
        String op = q.op;
        if(op.equals("par") && (q.y.equals("R") || q.y.equals("RET"))){
            return true;
        }
        else
            return false;
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

    public class operand_number{
        String operand;
        int number;
        public operand_number(String operand,int number){
            this.operand=operand;
            this.number=number;
        }

        @Override
        public boolean equals(Object o){
            if(o == this) return true;
            if(!(o instanceof operand_number)) return false;
            operand_number b = (operand_number) o;
            if(operand.equals(b.operand) && number==b.number){
                return true;
            }
            else
                return false;
        }

        @Override
        public int hashCode(){
            return Objects.hash(operand,number);
        }
    }

    public class rhs{
        String operator;
        operand_number x;
        operand_number y;
        public rhs(String operator, operand_number x,operand_number y){
            this.operator=operator;
            this.x=x;
            this.y=y;
        }

        @Override
        public boolean equals(Object o){
            if(o == this) return true;
            if(!(o instanceof rhs)) return false;
            rhs b = (rhs) o;
            if(operator.equals(b.operator) && x.equals(b.x) && y.equals(b.y)){
                return true;
            }
            else
                return false;
        }

        @Override
        public int hashCode(){
            return Objects.hash(operator,x,y);
        }

    }

    Map <rhs,operand_number> rhs_hash = new HashMap< rhs, operand_number >();
    Map <String,operand_number> vars_versions = new HashMap <String,operand_number>();

    public ArrayList<quad> optimize_unit( int start ){
        SortedSet<Integer> leaders=new TreeSet<Integer>();
        ArrayList<quad> op_quads=new ArrayList<quad>();
        ArrayList<block> blocks=new ArrayList<block>();
        int i=start,ld,last;
        boolean prev_jump = true;
        quad cur;
        leaders.add(i);
        i++;
        //find leaders
        while(!(cur=quads.get(i)).op.equals("endu")){
            if(prev_jump){
                ld=i;
                if(!leaders.contains(ld))
                    leaders.add(ld);
            }
            prev_jump=false;
            if(op_is_jump(cur)){
                if(!cur.op.equals("ret")){
                    ld=Integer.parseInt(cur.z);
                    if(!leaders.contains(ld))
                        leaders.add(ld);
                }
                prev_jump=true;
            }
            i++;
        }
        last=i;
        ld=i;
        if(!leaders.contains(ld))
            leaders.add(ld);
        //find blocks
        Iterator<Integer> it = leaders.iterator();
        Iterator<Integer> prev;
        range b;
        block bl;
        int current,previous;
        if(it.hasNext()){
            previous=it.next();
            while(true){
                int from,to;
                from=previous;
                if(it.hasNext()){
                    current = it.next();
                    to=current-1;
                    b=new range(from,to);
                    bl=new block(b);
                    blocks_first.put(from,blocks.size());
                    blocks.add(bl);
                }else{
                    to=last;
                    b=new range(from,to);
                    bl=new block(b);
                    blocks_first.put(from,blocks.size());
                    blocks.add(bl);
                    break;
                }
                previous=current;
            }
        }
        int cur_block=0;
        for(block m : blocks){
            quad lastquad=quads.get(m.r.to);
            int block_pos;
            if(op_is_jump(lastquad)){
                if(lastquad.op.equals("ret")){
                    block_pos=blocks_first.get(last);
                }
                else{
                    block_pos=blocks_first.get(Integer.parseInt(lastquad.z));
                }
                m.outcoming.add(block_pos);
                blocks.get(block_pos).incoming.add(cur_block);
                if(!lastquad.op.equals("jump") && !lastquad.op.equals("ret")){
                    block_pos=blocks_first.get(m.r.to+1);
                    m.outcoming.add(block_pos);
                    blocks.get(block_pos).incoming.add(cur_block);
                }
            }
            else{
                if(cur_block !=blocks.size()-1){
                    block_pos=blocks_first.get(m.r.to+1);
                    m.outcoming.add(block_pos);
                    blocks.get(block_pos).incoming.add(cur_block);
                }
            }
            cur_block++;
        }
        cur_block=0;
        for(block m : blocks){
            cur_block++;
            String op,x,y,z;
            for(int j=m.r.from;j<=m.r.to;j++){
                op=quads.get(j).op;
                x=quads.get(j).x;
                y=quads.get(j).y;
                z=quads.get(j).z;
                //
                //copy propagation
                //
                if(x_is_editable(quads.get(j)) && vars_value.get(x)!=null){
                    quads.get(j).x=vars_value.get(x);
                    x=quads.get(j).x;
                }
                if(y_is_editable(quads.get(j)) && vars_value.get(y)!=null){
                    quads.get(j).y=vars_value.get(y);
                    y=quads.get(j).y;
                }
                //
                //algebraic simplification
                if(op_is_op(quads.get(j))){
                    int xi,yi; //java ints are always 32 bit
                    if(isInteger(x,10) && isInteger(y,10)){
                        xi = Integer.parseInt(x);
                        yi = Integer.parseInt(y);
                        quads.get(j).op=":=";
                        if(op.equals("+")){
                            quads.get(j).x=String.format("%d",xi+yi);
                            quads.get(j).y="-";
                        }
                        else if(op.equals("-")){
                            quads.get(j).x=String.format("%d",xi-yi);
                            quads.get(j).y="-";
                        }
                        else if(op.equals("*")){
                            quads.get(j).x=String.format("%d",xi*yi);
                            quads.get(j).y="-";
                        }
                        else if(op.equals("/")){
                            quads.get(j).x=String.format("%d",xi/yi);
                            quads.get(j).y="-";
                        }
                        else if(op.equals("%")){
                            quads.get(j).x=String.format("%d",xi%yi);
                            quads.get(j).y="-";
                        }
                    }
                }
                x=quads.get(j).x;
                y=quads.get(j).y;
                z=quads.get(j).z;
                //
                //common subexpression elimination, a hashtable is used for storing the rhs with the version of its operands
                //if the current rhs matches a rhs from the hashtable and the corresponding variable is unchanged then
                //expression is transformed to an assignemnt of the z of the rhs in hash to the z of the current rhs
                //
                if(op_is_op(quads.get(j))){
                    op=quads.get(j).op;
                    rhs temp_rhs;
                    operand_number x_temp;
                    operand_number y_temp;
                    operand_number z_temp;
                    operand_number z_temp_rhs;
                    operand_number z_temp_vars;
                    if(isInteger(x,10)){
                        x_temp=new operand_number(x,0);
                    }
                    else{
                        if(vars_versions.get(x)!=null){
                            x_temp=new operand_number(vars_versions.get(x).operand,vars_versions.get(x).number);
                        }
                        else{
                            x_temp=new operand_number(x,j);
                            vars_versions.put(x,x_temp);
                        }
                    }
                    if(isInteger(y,10)){
                        y_temp=new operand_number(y,0);
                    }
                    else{
                        if(vars_versions.get(y)!=null){
                            y_temp=new operand_number(vars_versions.get(y).operand,vars_versions.get(y).number);
                        }
                        else{
                            y_temp=new operand_number(y,j);
                            vars_versions.put(y,y_temp);
                        }
                    }
                    z_temp=new operand_number(z,j);
                    System.out.printf("%d: %s",j,String.format("%s,%s,%s,%s\n",op,x,y,z));
                    System.out.printf("%s,%s %d, %s %d\n",op,x_temp.operand,x_temp.number,y_temp.operand,y_temp.number);
                    temp_rhs=new rhs(op,x_temp,y_temp);
                    if(rhs_hash.get(temp_rhs)!=null){
                        z_temp_rhs=rhs_hash.get(temp_rhs);
                        z_temp_vars=vars_versions.get(z_temp_rhs.operand);
                        if(z_temp_rhs.number==z_temp_vars.number){
                            quads.get(j).op=":=";
                            quads.get(j).x=z_temp_rhs.operand;
                            quads.get(j).y="-";
                        }
                        else{
                            rhs_hash.put(temp_rhs,z_temp);
                        }
                    }
                    else{
                        rhs_hash.put(temp_rhs,z_temp);
                    }
                }
                //
                //add the new versions of assigned variables in case of :=,
                //operation, array, ref, ret etc
                //update quad info variables also for copy propagation
                //
                if(op_assigns_z(quads.get(j))){
                    if((vars_value.get(z)!=null ||vars_value_reverse.get(z)!=null)){
                        if(vars_value.get(z)!=null){
                            vars_value_reverse.remove(vars_value.get(z));
                            vars_value.remove(z);
                        }else{
                            vars_value.remove(vars_value_reverse.get(z));
                            vars_value_reverse.remove(z);
                        }
                    }
                    operand_number temp =  new operand_number(z,j);
                    vars_versions.put(z,temp);
                }
                if(op_assigns_x(quads.get(j))){
                    if(vars_value.get(x)!=null||vars_value_reverse.get(z)!=null){
                        if(vars_value.get(x)!=null){
                            vars_value_reverse.remove(vars_value.get(x));
                            vars_value.remove(x);
                        }else{
                            vars_value.remove(vars_value_reverse.get(x));
                            vars_value_reverse.remove(x);
                        }
                    }
                    operand_number temp =  new operand_number(x,j);
                    vars_versions.put(x,temp);
                }
                //
                //copy propagation of assignments
                //
                if(quads.get(j).op.equals(":=")){
                    vars_value.put(z,x);
                    vars_value_reverse.put(x,z);
                }

            }
            rhs_hash.clear();
            vars_versions.clear();
            vars_value.clear();
            vars_value_reverse.clear();
        }
        //System.out.printf("end unit %d\n",start);
        return op_quads;
    }
}
