.intel_syntax noprefix # Use Intel syntax instead of AT&T syntax
.text
    .global main
    .global abs_0
    .global puti_0
    .global puts_0
    .global putc_0

abs_0:
    push ebp
    mov ebp, esp

    mov eax, DWORD PTR [ebp + 16]
    push eax
    call abs
    add esp, 4
    mov esi, dword ptr [ebp + 12]
    mov DWORD ptr [esi], eax

    mov esp, ebp
    pop ebp
    ret

puti_0:
    push ebp
    mov ebp, esp

    mov eax, DWORD PTR [ebp + 16]
    push eax
    mov eax, OFFSET FLAT:pi
    push eax
    # Call printf()
    call printf

    call abs
    add esp,4

    mov esp, ebp
    pop ebp
    ret

puts_0:
    push ebp
    mov ebp, esp

    mov eax, DWORD PTR [ebp + 16]
    push eax
    mov eax, OFFSET FLAT:ps
    push eax
    # Call printf()
    call printf

    call abs
    add esp,4

    mov esp, ebp
    pop ebp
    ret

putc_0:
    push ebp
    mov ebp, esp

    mov eax, DWORD PTR [ebp + 16]
    push eax
    mov eax, OFFSET FLAT:pc
    push eax
    # Call printf()
    call printf

    call abs
    add esp,4

    mov esp, ebp
    pop ebp
    ret

.data
    pi: .asciz "%d"
    ps: .asciz "%s"
    pc: .asciz "%c"
