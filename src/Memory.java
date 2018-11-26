import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Arrays;


public class Memory {

    private static final int MAX_MEMORY_SIZE = 4096;
    private static final int MEMORY_START_POINT = 0x200; //should be 512 / 0x200. TODO: set the custom memory size
    private static final int STACK_START_POINT = 0xea0; // 96 bytes, up to 0xEFF
    private static final int DISPLAY_START_POINT = 0xf00; //256 bytes, up to 0xFFF

    //RAM
    private short[] memory;
    private int memory_size;

    //for custom setup
    public Memory(int size){
        memory_size = size;
        if ( memory_size > MAX_MEMORY_SIZE || memory_size <= 0){
            memory_size = MAX_MEMORY_SIZE;
            System.out.println("MEMORY set to MAX_MEMORY_SIZE");
        }
        memory = new short[memory_size];
    }

    //for standard setup
    public Memory(){
        memory = new short[MAX_MEMORY_SIZE];
        memory_size = MAX_MEMORY_SIZE;
    }

    public int getMemory_size() {
        return memory_size;
    }

    public void clearMemory(){
        memory = new short[memory_size];
    }

    public short readMemoryAtAddress(int address) throws Exception {
        if ( address < 0 || address >= memory_size ){
            System.out.println("MEMORY access error");
            throw new Exception("MEMORY access error");
        }
        return memory[address];
    }

    public void writeMemoryAtAddress(int address, short data) throws Exception{
        if ( address < 0 || address >= memory_size ){
            System.out.println("MEMORY access error");
            throw new Exception("MEMORY access error");
        }
        memory[address] = data;
    }

    public void dumpMemoryToOut(Boolean isHex){
        if (isHex){
            String temp;
            for (short data : memory) {
                temp = Integer.toHexString(data);
                if (temp.length() == 1) temp = "0" + temp;
                System.out.print( temp + "\t");
            }
            System.out.println("\n");
        }else {
            System.out.println(Arrays.toString(memory));
        }
    }

    public void dumpMemoryToFile(String path){
        try {
            PrintWriter writer = new PrintWriter(path, "UTF-8");
            String temp;
            for (short data : memory){
                temp = Integer.toHexString(data);
                if (temp.length() == 1) temp = "0" + temp + " ";
                writer.println(temp);
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMemoryFromFile(String filename, int start_pc){
        try{
            FileInputStream in = new FileInputStream(filename);
            int read;
            while((read = in.read()) != -1){
                writeMemoryAtAddress(start_pc++, (short)read);
            }
        } catch (Exception e) {System.out.println("FileInputStream error");}
    }

    void DisassembleOp(int address){
        try{
            short code[] = new short[2];
            code[0] = readMemoryAtAddress(address);
            code[1] = readMemoryAtAddress(address+1);
            System.out.printf( "%04X %02X %02X : ", address, code[0], code[1]);
            int first_nibble = code[0] >> 4;
            switch (first_nibble){
                case 0x00: {
                    int second_nibble = code[0] & 0x0f;
                    if (second_nibble == 0x0){
                        if(code[1] == 0xe0) System.out.printf("CLS\n");
                        else if(code[1] == 0xee) System.out.printf("RET\n");
                        else System.out.printf("ERROR, OP 00%02X\n", code[1]);
                    } else System.out.printf("Calls RCA 1802 program at address %01X%02X\n", second_nibble, code[1]);
                } break;
                case 0x01: {
                    int addr = code[0] & 0x0f;
                    System.out.printf("JMP\t%01X%02X\n", addr, code[1]);
                } break;
                case 0x02: {
                    int addr = code[0] & 0x0f;
                    System.out.printf("CALL\t%01X%02X\n", addr, code[1]);
                } break;
                case 0x03: {
                    int reg = code[0] & 0x0f;
                    System.out.printf("SKP.E\tV%01X, %02X\n", reg, code[1]);
                } break;
                case 0x04:  {
                    int reg = code[0] & 0x0f;
                    System.out.printf("SKP.NE\tV%01X, %02X\n", reg, code[1]);
                } break;
                case 0x05:  {
                    int reg1 = code[0] & 0x0f;
                    int reg2 = code[1] >> 4;
                    System.out.printf("SKP.E\tV%01X, V%01X\n", reg1, reg2);
                } break;
                case 0x06: {
                    int reg = code[0] & 0x0f;
                    System.out.printf("MOV\tV%01X, %02X\n", reg, code[1]);
                } break;
                case 0x07:  {
                    int reg = code[0] & 0x0f;
                    System.out.printf("ADD\tV%01X, %02X\n", reg, code[1]);
                } break;
                case 0x08: {
                    int second_nibble = code[1] & 0x0f;
                    int reg1 = code[0] & 0x0f;
                    int reg2 = code[1] >> 4;
                    switch (second_nibble){
                        case 0x00: System.out.printf("MOV\tV%01X, V%01X\n", reg1, reg2); break;
                        case 0x01: System.out.printf("OR\tV%01X, V%01X\n", reg1, reg2); break;
                        case 0x02: System.out.printf("AND\tV%01X, V%01X\n", reg1, reg2); break;
                        case 0x03: System.out.printf("XOR\tV%01X, V%01X\n", reg1, reg2); break;
                        case 0x04: System.out.printf("ADD.\tV%01X, V%01X\n", reg1, reg2); break;
                        case 0x05: System.out.printf("SUB.\tV%01X, V%01X\n", reg1, reg2); break;
                        case 0x06: System.out.printf("SHR.\tV%01X, V%01X\n", reg1, reg2); break;
                        case 0x07: System.out.printf("SUBB.\tV%01X, V%01X\n", reg1, reg2); break;
                        case 0x0e: System.out.printf("SHL.\tV%01X, V%01X\n", reg1, reg2); break;
                    }
                } break;
                case 0x09:   {
                    int reg1 = code[0] & 0x0f;
                    int reg2 = code[1] >> 4;
                    System.out.printf("SKP.NE\tV%01X, V%01X\n", reg1, reg2);
                } break;
                case 0x0a: {
                    int addr_reg_i = code[0] & 0x0f;
                    System.out.printf("MOV\tI,  %01X%02X\n", addr_reg_i, code[1]);
                } break;
                case 0x0b:  {
                    int addr = code[0] & 0x0f;
                    System.out.printf("JMP\tV0+%01X%02X\n", addr, code[1]);
                } break;
                case 0x0c:  {
                    int reg = code[0] & 0x0f;
                    System.out.printf("MOV\tV%01X, RAND()&%02X\n", reg, code[1]);
                } break;
                case 0x0d: {
                    int reg1 = code[0] & 0x0f;
                    int reg2 = code[1] >> 4;
                    int height = code[1] & 0x0f;
                    System.out.printf("SPRITE\tV%01X, V%01X, %01X\n", reg1, reg2, height);
                } break;
                case 0x0e: {
                    int reg = code[0] & 0x0f;
                    if(code[1] == 0x9e) System.out.printf("SKP.K\tV%01X\n", reg);
                    else if(code[1] == 0xa1) System.out.printf("SKP.NK\tV%01X\n", reg);
                    else System.out.printf("ERROR, OP E%01X%02X\n", reg, code[1]);
                } break;
                case 0x0f: {
                    int reg = code[0] & 0x0f;
                    switch (code[1]){
                        case 0x07: System.out.printf("MOV\tV%01X, DELAY\n", reg); break;
                        case 0x0a: System.out.printf("WAITKEY\tV%01X\n", reg); break;
                        case 0x15: System.out.printf("MOV\tDELAY, V%01X\n", reg); break;
                        case 0x18: System.out.printf("MOV\tSOUND, V%01X\n", reg); break;
                        case 0x1e: System.out.printf("ADD\tI, V%01X\n", reg); break;
                        case 0x29: System.out.printf("S.CHAR\tV%01X\n", reg); break;
                        case 0x33: System.out.printf("MOVBCD\tV%01X\n", reg); break;
                        case 0x55: System.out.printf("MOVM\t(I), V0-V%01X\n", reg); break;
                        case 0x65: System.out.printf("MOVM\tV0-V%01X, (I)\n", reg); break;
                    }

                } break;
            }
        } catch (Exception e) {System.out.println("(Disassemble) MEMORY access error");}
    }

}
