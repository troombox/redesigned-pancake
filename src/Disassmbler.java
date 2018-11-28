public class Disassmbler {

    short code[];

    String DisassembleOp(short[] code){
        String output = String.format( "%02X %02X : ", code[0], code[1]);
        int first_nibble = code[0] >> 4;
        switch (first_nibble){
            case 0x00: {
                int second_nibble = code[0] & 0x0f;
                if (second_nibble == 0x0){
                    if(code[1] == 0xe0) output += ("CLS\n");
                    else if(code[1] == 0xee) output += ("RET\n");
                    else output += String.format("ERROR, OP 00%02X\n", code[1]);
                } else output += String.format("Calls RCA 1802 program at address %01X%02X\n", second_nibble, code[1]);
            } break;
            case 0x01: {
                int addr = code[0] & 0x0f;
                output += String.format("JMP\t%01X%02X\n", addr, code[1]);
            } break;
            case 0x02: {
                int addr = code[0] & 0x0f;
                output += String.format("CALL\t%01X%02X\n", addr, code[1]);
            } break;
            case 0x03: {
                int reg = code[0] & 0x0f;
                output += String.format("SKP.E\tV%01X, %02X\n", reg, code[1]);
            } break;
            case 0x04:  {
                int reg = code[0] & 0x0f;
                output += String.format("SKP.NE\tV%01X, %02X\n", reg, code[1]);
            } break;
            case 0x05:  {
                int reg1 = code[0] & 0x0f;
                int reg2 = code[1] >> 4;
                output += String.format("SKP.E\tV%01X, V%01X\n", reg1, reg2);
            } break;
            case 0x06: {
                int reg = code[0] & 0x0f;
                output += String.format("MOV\tV%01X, %02X\n", reg, code[1]);
            } break;
            case 0x07:  {
                int reg = code[0] & 0x0f;
                output += String.format("ADD\tV%01X, %02X\n", reg, code[1]);
            } break;
            case 0x08: {
                int second_nibble = code[1] & 0x0f;
                int reg1 = code[0] & 0x0f;
                int reg2 = code[1] >> 4;
                switch (second_nibble){
                    case 0x00: output += String.format("MOV\tV%01X, V%01X\n", reg1, reg2); break;
                    case 0x01: output += String.format("OR\tV%01X, V%01X\n", reg1, reg2); break;
                    case 0x02: output += String.format("AND\tV%01X, V%01X\n", reg1, reg2); break;
                    case 0x03: output += String.format("XOR\tV%01X, V%01X\n", reg1, reg2); break;
                    case 0x04: output += String.format("ADD.\tV%01X, V%01X\n", reg1, reg2); break;
                    case 0x05: output += String.format("SUB.\tV%01X, V%01X\n", reg1, reg2); break;
                    case 0x06: output += String.format("SHR.\tV%01X, V%01X\n", reg1, reg2); break;
                    case 0x07: output += String.format("SUBB.\tV%01X, V%01X\n", reg1, reg2); break;
                    case 0x0e: output += String.format("SHL.\tV%01X, V%01X\n", reg1, reg2); break;
                }
            } break;
            case 0x09:   {
                int reg1 = code[0] & 0x0f;
                int reg2 = code[1] >> 4;
                output += String.format("SKP.NE\tV%01X, V%01X\n", reg1, reg2);
            } break;
            case 0x0a: {
                int addr_reg_i = code[0] & 0x0f;
                output += String.format("MOV\tI,  %01X%02X\n", addr_reg_i, code[1]);
            } break;
            case 0x0b:  {
                int addr = code[0] & 0x0f;
                output += String.format("JMP\tV0+%01X%02X\n", addr, code[1]);
            } break;
            case 0x0c:  {
                int reg = code[0] & 0x0f;
                output += String.format("MOV\tV%01X, RAND()&%02X\n", reg, code[1]);
            } break;
            case 0x0d: {
                int reg1 = code[0] & 0x0f;
                int reg2 = code[1] >> 4;
                int height = code[1] & 0x0f;
                output += String.format("SPRITE\tV%01X, V%01X, %01X\n", reg1, reg2, height);
            } break;
            case 0x0e: {
                int reg = code[0] & 0x0f;
                if(code[1] == 0x9e) output += String.format("SKP.K\tV%01X\n", reg);
                else if(code[1] == 0xa1) output += String.format("SKP.NK\tV%01X\n", reg);
                else output += String.format("ERROR, OP E%01X%02X\n", reg, code[1]);
            } break;
            case 0x0f: {
                int reg = code[0] & 0x0f;
                switch (code[1]){
                    case 0x07: output += String.format("MOV\tV%01X, DELAY\n", reg); break;
                    case 0x0a: output += String.format("WAITKEY\tV%01X\n", reg); break;
                    case 0x15: output += String.format("MOV\tDELAY, V%01X\n", reg); break;
                    case 0x18: output += String.format("MOV\tSOUND, V%01X\n", reg); break;
                    case 0x1e: output += String.format("ADD\tI, V%01X\n", reg); break;
                    case 0x29: output += String.format("S.CHAR\tV%01X\n", reg); break;
                    case 0x33: output += String.format("MOVBCD\tV%01X\n", reg); break;
                    case 0x55: output += String.format("MOVM\t(I), V0-V%01X\n", reg); break;
                    case 0x65: output += String.format("MOVM\tV0-V%01X, (I)\n", reg); break;
                }
            } break;
        }
        return output;
    }

}


