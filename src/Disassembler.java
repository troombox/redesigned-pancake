public class Disassembler {

    private int[] code = new int[2];

    public Disassembler(int single_code){
        this.code[0] = (single_code >> 8);
        this.code[1] = (single_code & 0xff);
    }

    public  Disassembler(int[] code_array){
        this.code[0] = code_array[0];
        this.code[1] = code_array[1];
    }

    public Disassembler(int first_code, int second_code){
        this.code[0] = first_code;
        this.code[1] = second_code;
    }

    public String DisassembleOpFull(){
        String output = String.format( "%02X %02X : ", code[0], code[1]);
        return output + DisassembleOp();
    }

    public String DisassembleOp(){
        String output = "";
        int first_nibble = code[0] >> 4;
        switch (first_nibble){
            case 0x00: {
                int second_nibble = code[0] & 0x0f;
                if (second_nibble == 0x0){
                    if(code[1] == 0xe0) output += ("CLS");
                    else if(code[1] == 0xee) output += ("RET");
                    else output += String.format("ERROR, OP 00%02X", code[1]);
                } else output += String.format("Calls RCA 1802 program at address %01X%02X", second_nibble, code[1]);
            } break;
            case 0x01: {
                int addr = code[0] & 0x0f;
                output += String.format("JMP  %01X%02X", addr, code[1]);
            } break;
            case 0x02: {
                int addr = code[0] & 0x0f;
                output += String.format("CALL  %01X%02X", addr, code[1]);
            } break;
            case 0x03: {
                int reg = code[0] & 0x0f;
                output += String.format("SKP.E  V%01X, %02X", reg, code[1]);
            } break;
            case 0x04:  {
                int reg = code[0] & 0x0f;
                output += String.format("SKP.NE  V%01X, %02X", reg, code[1]);
            } break;
            case 0x05:  {
                int reg1 = code[0] & 0x0f;
                int reg2 = code[1] >> 4;
                output += String.format("SKP.E  V%01X, V%01X", reg1, reg2);
            } break;
            case 0x06: {
                int reg = code[0] & 0x0f;
                output += String.format("MOV  V%01X, %02X", reg, code[1]);
            } break;
            case 0x07:  {
                int reg = code[0] & 0x0f;
                output += String.format("ADD  V%01X, %02X", reg, code[1]);
            } break;
            case 0x08: {
                int second_nibble = code[1] & 0x0f;
                int reg1 = code[0] & 0x0f;
                int reg2 = code[1] >> 4;
                switch (second_nibble){
                    case 0x00: output += String.format("MOV  V%01X, V%01X", reg1, reg2); break;
                    case 0x01: output += String.format("OR  V%01X, V%01X", reg1, reg2); break;
                    case 0x02: output += String.format("AND  V%01X, V%01X", reg1, reg2); break;
                    case 0x03: output += String.format("XOR  V%01X, V%01X", reg1, reg2); break;
                    case 0x04: output += String.format("ADD.  V%01X, V%01X", reg1, reg2); break;
                    case 0x05: output += String.format("SUB.  V%01X, V%01X", reg1, reg2); break;
                    case 0x06: output += String.format("SHR.  V%01X, V%01X", reg1, reg2); break;
                    case 0x07: output += String.format("SUBB.  V%01X, V%01X", reg1, reg2); break;
                    case 0x0e: output += String.format("SHL.  V%01X, V%01X", reg1, reg2); break;
                }
            } break;
            case 0x09:   {
                int reg1 = code[0] & 0x0f;
                int reg2 = code[1] >> 4;
                output += String.format("SKP.NE  V%01X, V%01X", reg1, reg2);
            } break;
            case 0x0a: {
                int addr_reg_i = code[0] & 0x0f;
                output += String.format("MOV I, %01X%02X", addr_reg_i, code[1]);
            } break;
            case 0x0b:  {
                int addr = code[0] & 0x0f;
                output += String.format("JMP  V0+%01X%02X", addr, code[1]);
            } break;
            case 0x0c:  {
                int reg = code[0] & 0x0f;
                output += String.format("MOV  V%01X, RAND()&%02X", reg, code[1]);
            } break;
            case 0x0d: {
                int reg1 = code[0] & 0x0f;
                int reg2 = code[1] >> 4;
                int height = code[1] & 0x0f;
                output += String.format("SPRITE  V%01X, V%01X, %01X", reg1, reg2, height);
            } break;
            case 0x0e: {
                int reg = code[0] & 0x0f;
                if(code[1] == 0x9e) output += String.format("SKP.K  V%01X", reg);
                else if(code[1] == 0xa1) output += String.format("SKP.NK  V%01X", reg);
                else output += String.format("ERROR, OP E%01X%02X", reg, code[1]);
            } break;
            case 0x0f: {
                int reg = code[0] & 0x0f;
                switch (code[1]){
                    case 0x07: output += String.format("MOV  V%01X, DELAY", reg); break;
                    case 0x0a: output += String.format("WAITKEY  V%01X", reg); break;
                    case 0x15: output += String.format("MOV  DELAY, V%01X", reg); break;
                    case 0x18: output += String.format("MOV  SOUND, V%01X", reg); break;
                    case 0x1e: output += String.format("ADD  I, V%01X", reg); break;
                    case 0x29: output += String.format("S.CHAR  V%01X", reg); break;
                    case 0x33: output += String.format("MOVBCD  V%01X", reg); break;
                    case 0x55: output += String.format("MOVM  (I), V0-V%01X", reg); break;
                    case 0x65: output += String.format("MOVM  V0-V%01X, (I)", reg); break;
                }
            } break;
        }
        return output;
    }

}


