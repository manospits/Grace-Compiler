fun main() :nothing
var c :char;
var s1,s2 :char[32];
{

    strcpy(s1,"hello");
    strcpy(s2,"hello");
    puti(strlen(s1));
    puts("\n");
    puts(s1);
    puts("\n");
    if(strcmp(s1,s2)=0) then {
        puts(s1);
        puts(" is equal ");
        puts(s2);
    }
    else{
        if(strcmp(s1,s2)<0) then{
         puts(s1);
         puts(" is smaller than ");
         puts(s2);
        }
        else{
         puts(s2);
         puts(" is smaller than ");
         puts(s1);
        }
    }
    puts("\n");
    strcpy(s1,"abc");
    strcat(s1,s1);
    puts(s1);
    puts("\n");
    puti(ord("abc"[3]));
    puts("\n");
    putc(chr(97));
    putc(chr(ord('\n')));
    return;
}
