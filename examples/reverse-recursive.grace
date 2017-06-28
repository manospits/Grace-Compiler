fun main() :nothing
    var string :char[32];
    fun reverse(ref string :char[];start :int;end :int) :nothing
    {
        if(start=end) then return;
        reverse(string,start+1,end);
        putc(string[start]);
    }

{
    strcpy(string,"\n!dlrow olleH");
    reverse(string,0,strlen(string));
}
