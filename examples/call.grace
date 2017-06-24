fun main():nothing
    var i :int;
    fun yolo():nothing
    {
        puts("yolo : ");
        puti(i);
        i<-i+1;
        puts("\n");
        }
    fun lol():nothing
        fun bla():nothing
        {
            yolo();
            puts("bla : ");
            puti(i);
            i<-i+1;
            puts("\n");
        }
    {
        bla();
        puts("lol : ");
        puti(i);
        i<-i+1;
        puts("\n");
    }
{

i<-0;
lol();
puts("main : ");
puti(i);
puts("\n");

}
