import java.util.concurrent.ThreadLocalRandom;

public class Chip {

    private static final int MAX_MEMORY_SIZE = 4096;
    private static final int MEMORY_START_POINT = 0x200; //should be 512 / 0x200.
    private static final int STACK_START_POINT = 0xea0; // 96 bytes, up to 0xEFF
    private static final int DISPLAY_START_POINT = 0xf00; //256 bytes, up to 0xFFF

    //RAM
    private Memory memory;

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
        PC = MEMORY_START_POINT;
        SP = STACK_START_POINT;
        display_pointer = DISPLAY_START_POINT;
    }

    private void unimplementedOPcode(int OPcode){
        System.out.println( Integer.toHexString(OPcode) + " not implemented yet" );
    }

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

    public void emulateChip() throws Exception{
        short[] OPcode = new short[2];
        OPcode[0] = memory.readMemoryAtAddress(this.PC);
        OPcode[1] = memory.readMemoryAtAddress(this.PC+1);
        int first_nibble = (OPcode[0] & 0xf0) >> 4;
        switch(first_nibble){
            case 0x00: unimplementedOPcode(OPcode[0]); break;
            case 0x01: {    //JMP NNN
                int new_pc_address = (OPcode[0] & 0x0f) << 8 | OPcode[1];
                this.PC = new_pc_address;
            } break;
            case 0x02: {
                int new_pc_address = (OPcode[0] & 0x0f) << 8 | OPcode[1];
                memory.writeMemoryAtAddress(SP,(short)this.PC);
                SP++;
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
            case 0x08: unimplementedOPcode(OPcode[0]); break;
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
            case 0x0d: unimplementedOPcode(OPcode[0]); break;
            case 0x0e: unimplementedOPcode(OPcode[0]); break;
            case 0x0f: unimplementedOPcode(OPcode[0]); break;
        }

    }

    public void dumpChipDataToOut(){
        System.out.println("Pointers: PC:"+this.PC+" SP:"+this.SP+" Display:"+this.display_pointer);
        System.out.println("Timers: Delay:"+this.delay_timer+" Sound:"+this.sound_timer);
        System.out.println("Registers:");
        System.out.println("I:"+this.I);
        for(int i=0;i<16;i++) System.out.println("V[" + i + "]:" + V[i]);
    }

}
