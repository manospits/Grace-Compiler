#include <stdio.h>

void quicksort(int *a,int  m,int n ){
    int i,j,temp ;
    if (n < m)  return;
    i= m;j=n;
    while (i<= j){
        while (a[i] < a[(m+n) / 2] ) i= i+1;
        while (a[j] > a[(m+n) / 2] ) j= j-1;
        if (i<=j) {
            temp = a[i];a[i]=a[j];a[j]=temp;
            i=i+1;
            j=j-1;
        }
    }
    quicksort(a,m,j);quicksort(a,i,n);
}

int main (void){
    int a[32]={35, 67, 8, 6, 36, 6, 38, 80, 78, 7, 78, 9, 51, 49, 79, 49},i;
    quicksort(a,0,15);
    for(i=0;i<16;i++){
        printf("%d,",a[i]);
    }
    puts("");
}

