import java.util.concurrent.ThreadLocalRandom;

public class Chip {
    //constants, in case they'll be needed
    private static final int MAX_MEMORY_SIZE = 4096;
    private static final int MEMORY_START_POINT = 0x200; //should be 512 / 0x200.
    private static final int STACK_START_POINT = 0xea0; // 96 bytes, up to 0xEFF
    private static final int DISPLAY_START_POINT = 0xf00; //256 bytes, up to 0xFFF
    private static final int CHIP_STATE = 20; //size of array that transfers the chip state to GUI

    //RAM
    private Memory memory;

    //Display data
    private Display display;

    //8-bit registers
    private short[] V = new short[16];
    private int I = 0;

    //pointers
    private int PC = 0;
    private int SP = 0;
    private int display_pointer = 0;

    //timers
    private int delay_timer = 0;
    private int sound_timer = 0;

    public Chip(){
        memory = new Memory();
        display = new Display();
        PC = MEMORY_START_POINT;
        SP = STACK_START_POINT;
        display_pointer = DISPLAY_START_POINT;
    }

    public void emulateChip() throws Exception{
        short[] OPcode = new short[2];
        OPcode[0] = memory.readMemoryAtAddress(this.PC);
        OPcode[1] = memory.readMemoryAtAddress(this.PC+1);
        int first_nibble = (OPcode[0] & 0xf0) >> 4;
        switch(first_nibble){
            case 0x00: {
                if (OPcode[1] == 0xe0){     //CLS
                    display.clearDisplay();
                    this.PC+=2;
                }
                else if (OPcode[1] == 0xee){    //RET
                    this.SP--;
                    this.PC = memory.readMemoryAtAddress(this.SP);
                    this.PC+=2;
                }
                else System.out.printf("ERROR, OP 00%02X\n", OPcode[1]);
            } break;
            case 0x01: {    //JMP NNN
                int new_pc_address = (OPcode[0] & 0x0f) << 8 | OPcode[1];
                this.PC = new_pc_address;
            } break;
            case 0x02: {    //CALL
                int new_pc_address = (OPcode[0] & 0x0f) << 8 | OPcode[1];
                memory.writeMemoryAtAddress(this.SP,(short)this.PC);
                this.SP++;
                this.PC = new_pc_address;
            } break;
            case 0x03: {    //SKP.E
                int reg = OPcode[0] & 0x0f;
                if(V[reg] == OPcode[1]) this.PC += 2;
                this.PC+=2;
            } break;
            case 0x04: {    //SKP.NE
                int reg = OPcode[0] & 0x0f;
                if(V[reg] != OPcode[1]) this.PC += 2;
                this.PC+=2;
            } break;
            case 0x05: {    //SKP.E
                int reg1 = OPcode[0] & 0x0f;
                int reg2 = OPcode[1] >> 4;
                if(V[reg1] == V[reg2]) this.PC += 2;
                this.PC+=2;
            } break;
            case 0x06: {    //MOV
                int reg = OPcode[0] & 0x0f;
                V[reg] = OPcode[1];
                this.PC+=2;
            } break;
            case 0x07: {    //ADD
                int reg = OPcode[0] & 0x0f;
                V[reg] += OPcode[1];
                this.PC+=2;
            } break;
            case 0x08: {
                int second_nibble = OPcode[1] & 0x0f;
                int reg1 = OPcode[0] & 0x0f;
                int reg2 = OPcode[1] >> 4;
                switch (second_nibble){
                    case 0x00: {V[reg1] = V[reg2]; this.PC+=2;} break;                    //MOV
                    case 0x01: {V[reg1] = (short)(V[reg1]|V[reg2]); this.PC+=2;} break;   //OR
                    case 0x02: {V[reg1] = (short)(V[reg1]&V[reg2]); this.PC+=2;} break;   //AND
                    case 0x03: {V[reg1] = (short)(V[reg1]^V[reg2]); this.PC+=2;} break;   //XOR
                    case 0x04: {    //ADD.
                        V[reg1]+=V[reg2];
                        if(V[reg1]>0xff){
                            V[0xf] = 1; //carry
                        }
                        V[reg1] = (short)(V[reg1] & 0xff); //making sure there are no bits over
                        this.PC+=2;
                    } break;
                    case 0x05: {    //SUB.
                        while(V[reg2]>V[reg1]){
                            V[reg1]*=2;
                            V[0xf] = 0; //borrow
                        }
                        V[reg1]-=V[reg2];
                        this.PC+=2;
                    } break;
                    case 0x06: {    //SHR
                        V[0xf] = (short)(V[reg1] & 0xfe); //lsb saved to VF
                        V[reg1] = (short)(V[reg1] >> 1);
                        this.PC+=2;
                    } break;
                    case 0x07: {    //SUBB.
                        while(V[reg1]>V[reg2]){
                            V[reg1]*=2;
                            V[0xf] = 0; //borrow
                        }
                        V[reg1] = (short)(V[reg2] - V[reg1]);
                        this.PC+=2;
                    } break;
                    case 0x0e: {    //SHL
                        V[0xf] = (short)(V[reg1] & 0xef); //msb saved to VF
                        V[reg1] = (short)(V[reg1] << 1);
                        this.PC+=2;
                    } break;
                }
            } break;
            case 0x09: {    //SKP.NE
                int reg1 = OPcode[0] & 0x0f;
                int reg2 = OPcode[1] >> 4;
                if(V[reg1] != V[reg2]) this.PC += 2;
                this.PC+=2;
            } break;
            case 0x0a: {    //MOV I NNN
                int value = (OPcode[0] & 0x0f) << 8 | OPcode[1];
                this.I = value;
                this.PC+=2;
            } break;
            case 0x0b: {    //JMP V[0]+NNN
                int new_pc_address = (OPcode[0] & 0x0f) << 8 | OPcode[1];
                this.PC = V[0]+new_pc_address;
                this.PC+=2;
            } break;
            case 0x0c: {
                int reg = OPcode[0] & 0x0f;
                int random = ThreadLocalRandom.current().nextInt(0, 255 + 1);
                V[reg] = (short)(random & OPcode[1]);
                this.PC+=2;
            } break;
            case 0x0d: {
                int reg1 = OPcode[0] & 0x0f;
                int reg2 = OPcode[1] >> 4;
                int height = OPcode[1] & 0x0f;
                    opcodeDUtility(V[reg1], V[reg2], height);
                this.PC+=2;
            } break;
            case 0x0e: unimplementedOPcode(OPcode[0]); break;
            case 0x0f: unimplementedOPcode(OPcode[0]); break;
        }

    }

    //UTILITY functions:
    private void opcodeDUtility(int X_coord, int Y_coord, int height){
        try {
            for(int i = height-1; i >= 0; i--){
                if(display.setPixels((short)X_coord,(short)(Y_coord+i),memory.readMemoryAtAddress(this.I+i))){
                    V[0xf] = 1; // there was a collision
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dumpChipDataToOut(){
        System.out.println("Pointers: PC:"+this.PC+" SP:"+this.SP+" Display:"+this.display_pointer);
        System.out.println("Timers: Delay:"+this.delay_timer+" Sound:"+this.sound_timer);
        System.out.println("Registers:");
        System.out.println("I:"+this.I);
        for(int i=0;i<16;i++) System.out.println("V[" + i + "]:" + V[i]);
    }

    /*  returns an array that holds current chip state
        contents:
        [0]-[15]: V[0] - V[F]
        [16]: I
        [17]: SP
        [18]: PC
        [19]-[20]: OP code
    */
    public int[] getChipState(){
        int[] chip_state = new int[CHIP_STATE];
        try {
            for(int i=0; i<16; i++){
                chip_state[i]=this.V[i];
            }
            chip_state[16]=this.I;
            chip_state[17]=this.SP;
            chip_state[18]=this.PC;
            chip_state[19]= (memory.readMemoryAtAddress(this.PC) << 8) | memory.readMemoryAtAddress(this.PC+1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chip_state;
    }

    //TEMP FUNCTIONS
    private void unimplementedOPcode(int OPcode){
        System.out.println( Integer.toHexString(OPcode) + " not implemented yet" );
    }
    //END TEMP FUNCTIONS

    //function to load the memory from file
    //if PC is not in the beginning - we reset everything first
    public void loadProgram(String filepath){
        if(PC != MEMORY_START_POINT){
            memory.clearMemory();
            PC = MEMORY_START_POINT;
            SP = STACK_START_POINT;
            display_pointer = DISPLAY_START_POINT;
        }
        memory.loadMemoryFromFile(filepath,PC);
        PC = MEMORY_START_POINT;
    }

}
