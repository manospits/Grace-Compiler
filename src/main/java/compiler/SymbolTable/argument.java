package compiler;
import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;
import java.io.*;
import java.util.*;


public class argument {
    String Type;
    ArrayList<String> ids;
    ArrayList<Integer> array_sizes;
    boolean ref;

    public argument(String Type,ArrayList<String> ids, ArrayList<Integer> array_sizes,boolean ref){
        this.Type = Type;
        this.ids = new ArrayList<String>(ids);
        this.array_sizes = new ArrayList<Integer>(array_sizes);
        this.ref=ref;
    }
}

