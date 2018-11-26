public class Emulator {

    public static void main(String []args) {
        String path = "C:\\Pong.ch8";
        Chip chip = new Chip();
        //chip.dumpChipDataToOut();
        chip.loadProgram(path);
        //chip.dumpChipDataToOut();
        try{
            for(int i=0; i<10; i++) chip.emulateChip();
        } catch (Exception e) {
            System.out.println("Error");
        }
        //chip.dumpChipDataToOut();


 /*       for(int pc = 0; pc < memory.getPC(); pc+=2){
            memory.DisassembleOp(pc);
        }
        System.out.println("\n" + "PC at: " + memory.getPC());
        //memory.dumpMemoryToFile("D:\\dump.txt");*/
    }

}
