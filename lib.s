.intel_syntax noprefix # Use Intel syntax instead of AT&T syntax
.text
    .global main
    .global puti_0
    .global puts_0
    .global putc_0
    .global putx_0

    .global geti_0
    .global getc_0
    .global gets_0

    .global abs_0
    .global ord_0
    .global chr_0

    .global strlen_0
    .global strcmp_0
    .global strcpy_0
    .global strcat_0


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

ord_0:
    push ebp
    mov ebp, esp

    movzx eax, BYTE PTR [ebp + 16]
    mov esi, dword ptr [ebp + 12]
    mov  dword ptr [esi], eax

    mov esp, ebp
    pop ebp
    ret

chr_0:
    push ebp
    mov ebp, esp

    mov eax, dword PTR [ebp + 16]
    mov esi, dword ptr [ebp + 12]
    mov  BYTE ptr [esi], al

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

getc_0:
    push ebp
    mov ebp, esp

    call getchar
    mov esi,dword ptr [ebp + 12]
    mov byte ptr [esi],al

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
_string_17:

strcmp_0:
	push ebp
	mov ebp,esp
	sub esp,52
_string_18:
	mov eax,0
	mov DWORD ptr [ ebp - 4 ],eax
_string_19:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 16 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 8 ],ecx
_string_20:
	mov edi,DWORD ptr [ ebp - 8 ]
	movzx eax,BYTE ptr [edi]
	mov edx,0
	cmp eax,edx
	jne _string_22
_string_21:
	jmp _string_32
_string_22:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 20 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 12 ],ecx
_string_23:
	mov edi,DWORD ptr [ ebp - 12 ]
	movzx eax,BYTE ptr [edi]
	mov edx,0
	cmp eax,edx
	jne _string_25
_string_24:
	jmp _string_32
_string_25:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 16 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 16 ],ecx
_string_26:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 20 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 20 ],ecx
_string_27:
	mov edi,DWORD ptr [ ebp - 16 ]
	movzx eax,BYTE ptr [edi]
	mov edi,DWORD ptr [ ebp - 20 ]
	movzx edx,BYTE ptr [edi]
	cmp eax,edx
	je _string_29
_string_28:
	jmp _string_32
_string_29:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov edx,1
	add eax,edx
	mov DWORD ptr [ ebp - 24 ],eax
_string_30:
	mov eax,DWORD ptr [ ebp - 24 ]
	mov DWORD ptr [ ebp - 4 ],eax
_string_31:
	jmp _string_19
_string_32:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 16 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 28 ],ecx
_string_33:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 20 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 32 ],ecx
_string_34:
	mov edi,DWORD ptr [ ebp - 28 ]
	movzx eax,BYTE ptr [edi]
	mov edi,DWORD ptr [ ebp - 32 ]
	movzx edx,BYTE ptr [edi]
	cmp eax,edx
	je _string_36
_string_35:
	jmp _string_38
_string_36:
	mov eax,0
	mov esi,DWORD ptr [ ebp + 12 ]
	mov DWORD ptr [ esi ],eax
_string_37:
	jmp _end_strcmp_0
_string_38:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 16 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 36 ],ecx
_string_39:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 20 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 40 ],ecx
_string_40:
	mov edi,DWORD ptr [ ebp - 36 ]
	movzx eax,BYTE ptr [edi]
	mov edi,DWORD ptr [ ebp - 40 ]
	movzx edx,BYTE ptr [edi]
	cmp eax,edx
	jl _string_42
_string_41:
	jmp _string_45
_string_42:
	mov eax,0
	mov edx,1
	sub eax,edx
	mov DWORD ptr [ ebp - 44 ],eax
_string_43:
	mov eax,DWORD ptr [ ebp - 44 ]
	mov esi,DWORD ptr [ ebp + 12 ]
	mov DWORD ptr [ esi ],eax
_string_44:
	jmp _end_strcmp_0
_string_45:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 16 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 48 ],ecx
_string_46:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 20 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 52 ],ecx
_string_47:
	mov edi,DWORD ptr [ ebp - 48 ]
	movzx eax,BYTE ptr [edi]
	mov edi,DWORD ptr [ ebp - 52 ]
	movzx edx,BYTE ptr [edi]
	cmp eax,edx
	jg _string_49
_string_48:
	jmp _string_51
_string_49:
	mov eax,1
	mov esi,DWORD ptr [ ebp + 12 ]
	mov DWORD ptr [ esi ],eax
_string_50:
	jmp _end_strcmp_0
_string_51:
_end_strcmp_0:
	mov esp,ebp
	pop ebp
	ret

_string_52:
strcpy_0:
	push ebp
	mov ebp,esp
	sub esp,24
_string_53:
	mov eax,0
	mov DWORD ptr [ ebp - 4 ],eax
_string_54:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 20 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 8 ],ecx
_string_55:
	mov edi,DWORD ptr [ ebp - 8 ]
	movzx eax,BYTE ptr [edi]
	mov edx,0
	cmp eax,edx
	jne _string_57
_string_56:
	jmp _string_63
_string_57:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 16 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 12 ],ecx
_string_58:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 20 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 16 ],ecx
_string_59:
	mov edi,DWORD ptr [ ebp - 16 ]
	movzx eax,BYTE ptr [edi]
	mov edi,DWORD ptr [ ebp - 12 ]
	mov BYTE ptr [edi],al
_string_60:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov edx,1
	add eax,edx
	mov DWORD ptr [ ebp - 20 ],eax
_string_61:
	mov eax,DWORD ptr [ ebp - 20 ]
	mov DWORD ptr [ ebp - 4 ],eax
_string_62:
	jmp _string_54
_string_63:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 16 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 24 ],ecx
_string_64:
	mov eax,0
	mov edi,DWORD ptr [ ebp - 24 ]
	mov BYTE ptr [edi],al
_string_65:
_end_strcpy_0:
	mov esp,ebp
	pop ebp
	ret
_string_66:

strcat_0:
	push ebp
	mov ebp,esp
	sub esp,48
_string_67:
	mov eax,0
	mov DWORD ptr [ ebp - 4 ],eax
_string_68:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 16 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 16 ],ecx
_string_69:
	mov edi,DWORD ptr [ ebp - 16 ]
	movzx eax,BYTE ptr [edi]
	mov edx,0
	cmp eax,edx
	jne _string_71
_string_70:
	jmp _string_74
_string_71:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov edx,1
	add eax,edx
	mov DWORD ptr [ ebp - 20 ],eax
_string_72:
	mov eax,DWORD ptr [ ebp - 20 ]
	mov DWORD ptr [ ebp - 4 ],eax
_string_73:
	jmp _string_68
_string_74:
	mov eax,0
	mov DWORD ptr [ ebp - 8 ],eax
_string_75:
	mov esi,DWORD ptr [ ebp + 20 ]
	push esi
_string_76:
	lea esi,DWORD ptr [ ebp - 24 ]
	push esi
_string_77:
	push DWORD ptr [ ebp + 8 ]
	call strlen_0
	add esp,12
_string_78:
	mov eax,DWORD ptr [ ebp - 24 ]
	mov DWORD ptr [ ebp - 12 ],eax
_string_79:
	mov eax,DWORD ptr [ ebp - 8 ]
	mov edx,DWORD ptr [ ebp - 12 ]
	cmp eax,edx
	jl _string_81
_string_80:
	jmp _string_88
_string_81:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov edx,DWORD ptr [ ebp - 8 ]
	add eax,edx
	mov DWORD ptr [ ebp - 28 ],eax
_string_82:
	mov eax,DWORD ptr [ ebp - 28 ]
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 16 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 32 ],ecx
_string_83:
	mov eax,DWORD ptr [ ebp - 8 ]
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 20 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 36 ],ecx
_string_84:
	mov edi,DWORD ptr [ ebp - 36 ]
	movzx eax,BYTE ptr [edi]
	mov edi,DWORD ptr [ ebp - 32 ]
	mov BYTE ptr [edi],al
_string_85:
	mov eax,DWORD ptr [ ebp - 8 ]
	mov edx,1
	add eax,edx
	mov DWORD ptr [ ebp - 40 ],eax
_string_86:
	mov eax,DWORD ptr [ ebp - 40 ]
	mov DWORD ptr [ ebp - 8 ],eax
_string_87:
	jmp _string_79
_string_88:
	mov eax,DWORD ptr [ ebp - 4 ]
	mov edx,DWORD ptr [ ebp - 8 ]
	add eax,edx
	mov DWORD ptr [ ebp - 44 ],eax
_string_89:
	mov eax,DWORD ptr [ ebp - 44 ]
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 16 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 48 ],ecx
_string_90:
	mov eax,0
	mov edi,DWORD ptr [ ebp - 48 ]
	mov BYTE ptr [edi],al
_string_91:
_end_strcat_0:
	mov esp,ebp
	pop ebp
	ret
_string_92:

_stringinput_0:
gets_0:
	push ebp
	mov ebp,esp
	sub esp,36
_stringinput_1:
	mov eax,DWORD ptr[ ebp + 16 ]
	mov edx,0
	cmp eax,edx
	je _stringinput_3
_stringinput_2:
	jmp _stringinput_6
_stringinput_3:
	mov eax,0
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 20 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 16 ],ecx
_stringinput_4:
	mov eax,0
	mov edi,DWORD ptr [ ebp - 16 ]
	mov BYTE ptr [edi],al
_stringinput_5:
	jmp _end_gets_0
_stringinput_6:
	mov eax,1
	mov DWORD ptr [ ebp - 12 ],eax
_stringinput_7:
	mov eax,0
	mov DWORD ptr [ ebp - 8 ],eax
_stringinput_8:
	mov eax,DWORD ptr [ ebp - 12 ]
	mov edx,1
	cmp eax,edx
	je _stringinput_10
_stringinput_9:
	jmp _stringinput_27
_stringinput_10:
	lea esi,DWORD ptr [ ebp - 17 ]
	push esi
_stringinput_11:
	mov esi,DWORD ptr [ ebp + 8 ]
	push DWORD ptr [ esi + 8 ]
	call getc_0
	add esp,8
_stringinput_12:
	movzx eax,BYTE ptr [ ebp - 17 ]
	mov BYTE ptr [ ebp - 1 ],al
_stringinput_13:
	movzx eax,BYTE ptr [ ebp - 1 ]
	mov edx,10
	cmp eax,edx
	je _stringinput_15
_stringinput_14:
	jmp _stringinput_19
_stringinput_15:
	mov eax,DWORD ptr [ ebp - 8 ]
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 20 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 24 ],ecx
_stringinput_16:
	mov eax,0
	mov edi,DWORD ptr [ ebp - 24 ]
	mov BYTE ptr [edi],al
_stringinput_17:
	jmp _end_gets_0
_stringinput_18:
	jmp _stringinput_21
_stringinput_19:
	mov eax,DWORD ptr [ ebp - 8 ]
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 20 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 28 ],ecx
_stringinput_20:
	movzx eax,BYTE ptr [ ebp - 1 ]
	mov edi,DWORD ptr [ ebp - 28 ]
	mov BYTE ptr [edi],al
_stringinput_21:
	mov eax,DWORD ptr [ ebp - 8 ]
	mov edx,1
	add eax,edx
	mov DWORD ptr [ ebp - 32 ],eax
_stringinput_22:
	mov eax,DWORD ptr [ ebp - 32 ]
	mov DWORD ptr [ ebp - 8 ],eax
_stringinput_23:
	mov eax,DWORD ptr [ ebp - 8 ]
	mov edx,DWORD ptr[ ebp + 16 ]
	cmp eax,edx
	je _stringinput_25
_stringinput_24:
	jmp _stringinput_26
_stringinput_25:
	mov eax,0
	mov DWORD ptr [ ebp - 12 ],eax
_stringinput_26:
	jmp _stringinput_8
_stringinput_27:
	mov eax,DWORD ptr [ ebp - 8 ]
	mov ecx,1
	imul ecx
	mov ecx,DWORD ptr [ ebp + 20 ]
	add ecx,eax
	mov DWORD ptr [ ebp - 36 ],ecx
_stringinput_28:
	mov eax,0
	mov edi,DWORD ptr [ ebp - 36 ]
	mov BYTE ptr [edi],al
_stringinput_29:
_end_gets_0:
	mov esp,ebp
	pop ebp
	ret
_stringinput_30:

.data
    gr_pi: .asciz "%d"
    gr_ps: .asciz "%s"
    gr_pc: .asciz "%c"
    gr_px: .asciz "%x"
    gr_si: .asciz "%d"
