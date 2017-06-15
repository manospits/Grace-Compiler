.intel_syntax noprefix # Use Intel syntax instead of AT&T syntax
.text
    .global main
    .global abs_0
    .global puti_0
    .global puts_0
    .global putc_0
    .global putx_0
    .global strlen_0
    .global geti_0

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

geti_0:
    push ebp
    mov ebp, esp

    mov esi, dword ptr [ebp + 12]
    push esi
    mov eax, OFFSET FLAT:gr_si
    push eax
    call scanf
    add esp, 8

    mov esp, ebp
    pop ebp
    ret

puti_0:
    push ebp
    mov ebp, esp

    mov eax, DWORD PTR [ebp + 16]
    push eax
    mov eax, OFFSET FLAT:gr_pi
    push eax
    # Call printf()
    call printf

    call abs
    add esp,8

    mov esp, ebp
    pop ebp
    ret

putx_0:
    push ebp
    mov ebp, esp

    mov eax, DWORD PTR [ebp + 16]
    push eax
    mov eax, OFFSET FLAT:gr_px
    push eax
    # Call printf()
    call printf

    add esp,8

    mov esp, ebp
    pop ebp
    ret

puts_0:
    push ebp
    mov ebp, esp

    mov eax, DWORD PTR [ebp + 16]
    push eax
    mov eax, OFFSET FLAT:gr_ps
    push eax
    # Call printf()
    call printf

    add esp,8

    mov esp, ebp
    pop ebp
    ret

putc_0:
    push ebp
    mov ebp, esp

    mov eax, DWORD PTR [ebp + 16]
    push eax
    mov eax, OFFSET FLAT:gr_pc
    push eax
    # Call printf()
    call printf

    add esp,8

    mov esp, ebp
    pop ebp
    ret

_string_0:
strlen_0:
	push ebp
	mov ebp,esp
	sub esp,20
_string_1:
	mov eax,0
	mov DWORD ptr [ ebp - 4 ],eax
_string_2:
	mov eax,0
	mov DWORD ptr [ ebp - 8 ],eax
_string_3:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov edx,0
	cmp eax,edx
	jge _string_5
_string_4:
	jmp _string_16
_string_5:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 16 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 12 ],ecx
_string_6:
	mov edi,DWORD ptr [ ebp - 12 ]
	movzx eax,BYTE ptr [edi]
	mov edx,0
	cmp eax,edx
	je _string_8
_string_7:
	jmp _string_11
_string_8:
	mov eax,DWORD ptr [ ebp - 8 ]
	mov esi,DWORD ptr [ ebp + 12 ]
	mov DWORD ptr [ esi ],eax
_string_9:
	jmp _end_strlen_0
_string_10:
	jmp _string_13
_string_11:
	mov eax,DWORD ptr [ ebp - 8 ]
	mov edx,1
	add eax,edx
	mov DWORD ptr [ ebp - 16 ],eax
_string_12:
	mov eax,DWORD ptr [ ebp - 16 ]
	mov DWORD ptr [ ebp - 8 ],eax
_string_13:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov edx,1
	add eax,edx
	mov DWORD ptr [ ebp - 20 ],eax
_string_14:
	mov eax,DWORD ptr [ ebp - 20 ]
	mov DWORD ptr [ ebp - 4 ],eax
_string_15:
	jmp _string_3
_string_16:
_end_strlen_0:
	mov esp,ebp
	pop ebp
	ret

.data
    gr_pi: .asciz "%d"
    gr_ps: .asciz "%s"
    gr_pc: .asciz "%c"
    gr_px: .asciz "%x"
    gr_si: .asciz "%d"
