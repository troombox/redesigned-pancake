public class Display {
    private byte[][] display = new byte[64][32];

    //brute forcing the clear display
    public void clearDisplay(){
        display = new byte[64][32];
    }

    //draws a "line" of 8 pixels starting from (X,Y), and going right
    //returns true if there was a collision i.e. - pixel was unset
    public boolean setPixels(short coord_X, short coord_Y, short data){
        boolean collision = false;
        byte[] data_array = new byte[8];
        //first we separate the incoming data into 8 byte sized pieces
        //each loop giving us a single bit at an i'th place
        for(int i=0; i<8; i++){
            data_array[i] = (byte)((data >> i) & 0x01);
        }
        //now we set them into "display", starting from X,Y and going left to right
        for(int i=0; i<8; i++){
            //checking for collision
            if(this.display[coord_X+i][coord_Y] != data_array[i]){
                collision = true;
            }
            //XOR'ing the pixels in "display"
            this.display[coord_X+i][coord_Y] ^= data_array[i];
        }
        return collision;
    }
}
