fun main () : nothing
    fun quicksort(ref a :int[]; m,n :int):nothing
        var i,j,temp : int;
    {
        if n <= m then return;
        i<- m;j<-n;
        while i<= j do{
            while a[i] < a[(m+n) div 2] do i<- i+1;
            while a[j] > a[(m+n) div 2] do j<- j-1;
            if (i<=j) then{
                temp <- a[i];a[i]<-a[j];a[j]<-temp;
                i<-i+1;
                j<-j-1;
            }
        }
        quicksort(a,m,j);quicksort(a,i,n);
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
