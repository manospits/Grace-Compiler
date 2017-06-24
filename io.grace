fun io():nothing
    var s :char[32];
    fun gets(n:int ; ref s:char[]) :nothing
       var c :char;
       var i,true :int;
    {
        if(n=0) then{
            s[0]<-'\0';
            return ;
        }
        true <- 1;
        i <- 0;
        while (true = 1) do{
            c<-getc();
            if(c = '\n') then{
                s[i]<-'\0';
                return ;
            }
            else{
                s[i]<-c;
            }
            i<-i+1;
            if(i = n) then {
                true<-0;
            }
        }
        s[i]<-'\0';
    }
{
    gets(1,s);
    puts(s);
    puts("\n");
    gets(1,s);
    puts(s);
    puts("\n");

}
