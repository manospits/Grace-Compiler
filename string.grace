fun hello_0():nothing
    fun strlen(ref s: char[]):int
        var i,length:int;
    {
        i<-0;
        length<-0;
        while (i>=0) do{
            if(s[i]='\0') then{
                return length;
            }
            else{
                length<-length+1;
            }
            i<-i+1;
        }
    }

    fun strcmp(ref s1,s2: char[]):int
        var i :int;
    {
        i<-0;
        while(s1[i]#'\0' and s2[i]#'\0' and s1[i] = s2[i]) do{
            i<-i+1;
        }
        if(s1[i] = s2[i]) then{
            return 0;
        }
        if(s1[i] < s2[i]) then{
            return -1;
        }
        if(s1[i] > s2[i]) then{
            return 1;
        }
    }

    fun strcpy(ref trg,src: char[]) :nothing
        var i :int;
    {
        i<-0;
        while(src[i]#'\0') do{
            trg[i]<-src[i];
            i<-i+1;
        }
        trg[i]<-'\0';
    }

    fun strcat(ref trg,src: char[]) :nothing
        var i :int;
        var j :int;
        var srclen :int;
    {
        i<-0;
        while(trg[i]#'\0') do{
            i<-i+1;
        }
        j<-0;
        srclen<-strlen(src);
        while( j < srclen ) do{
            trg[i+j]<-src[j];
            j<-j+1;
        }
        trg[i+j]<-'\0';
    }

{
    puti(strlen("hello"));
}
