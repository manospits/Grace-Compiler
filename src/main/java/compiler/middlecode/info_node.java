package compiler;
import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;
import java.io.*;
import java.util.*;

public class info_node{
    String place;
    String type;
    ArrayList<Integer> Next;
    ArrayList<Integer> True;
    ArrayList<Integer> False;
    boolean array;
    public info_node(String place,String type,ArrayList<Integer> Next, ArrayList<Integer> True, ArrayList<Integer> False,boolean array){
        this.place = place;
        this.type = type;
        if(Next!=null)
            this.Next = new ArrayList<Integer>(Next);
        else
            this.Next = new ArrayList<Integer>();
        if(True!=null)
            this.True = new ArrayList<Integer>(True);
        else
            this.True = new ArrayList<Integer>();
        if(False!=null)
            this.False = new ArrayList<Integer>(False);
        else
            this.False = new ArrayList<Integer>();
        this.array = array;
    }
}

