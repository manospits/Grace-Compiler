fun main_____():nothing
    var i:int;
    fun fib (n:int):int
    {
        if(n = 1) then{
            return 1;
        }
        if(n=0) then{
            return 0;
        }
        return fib(n-1)+fib(n-2);
    }

{
    i<-0;
    while(i < 30) do {
        puti(fib(i));
        putc('\n');
        i<-1+i;
    }
}
