fun main () : nothing
    fun quicksort(ref a :int[]; lo,hi :int):nothing
        var h,l,p,t : int;
    {
        if (lo<hi) then{
            l<-lo;
            h<-hi;
            p<-a[hi];
            while((l<h) and (a[l]<=p)) do l<- l+1;
            while((h>l) and (a[h]>=p)) do h<- h-1;
            if(l<h) then {
                t<-a[l];
                a[l]<-a[h];
                a[h]<-t;
            }
            while(l<h) do{
                while((l<h) and (a[l]<=p)) do l<- l+1;
                while((h>l) and (a[h]>=p)) do h<- h-1;
                if(l<h) then{
                    t<-a[l];
                    a[l]<-a[h];
                    a[h]<-t;
                }
            }
            a[hi]<-a[l];
            a[l]<-p;
            quicksort(a,lo,l-1);
            quicksort(a,l+1,hi);
        }
    }

   fun putArray (ref msg : char[]; n : int; ref x : int[]) : nothing
      var i : int;
   {
      puts(msg);
      i <- 0;
      while i < n do {
        if i > 0 then puts(", ");
        puti(x[i]);
        i <- i+1;
      }
      puts("\n");
   }

   var seed, i : int;
   var x : int[16];
{
  seed <- 65;
  i <- 0;
  while i < 16 do {
    seed <- (seed * 137 + 220 + i) mod 101;
    x[i] <- seed;
    i <- i+1;
  }
  putArray("Initial array: ", 16, x);
  quicksort(x,0,15);
  putArray("Sorted array: ", 16, x);
}
