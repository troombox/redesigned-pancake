import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.Arrays;


public class Emulator extends Application {

    //size of chip state array
    private static final int CHIP_STATE = 20;

    //GUI
    Stage window;
    Label[] labels = new Label[CHIP_STATE];
    TextField[] textFields =  new TextField[CHIP_STATE];
    TextField disassembler = new TextField();

    //Emulation
    private String path = "C:\\Pong.ch8";
    private Chip chip = new Chip();
    private int[] chip_state = new int[CHIP_STATE];

    public static void main(String []args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        window = primaryStage;
        window.setTitle("Emulator State");

        //Labels: V0 - VF, I, SP, PC, OP
        setUpLabels();

        //TextFields: V0 - VF, I, SP, PC, OP
        setUpTextFields();

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.TOP_CENTER);
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        //debug option
        gridPane.setGridLinesVisible(false);
        //


        for(int i=0; i < labels.length; i++){
            int c_index = (i < 10) ? 0 : 3;
            int r_index = (i < 10) ? i : (i-10);
            gridPane.add(labels[i],c_index,r_index);
        }

        for(int i=0; i < textFields.length; i++){
            int c_index = (i < 10) ? 1 : 4;
            int r_index = (i < 10) ? i : (i-10);
            gridPane.add(textFields[i],c_index,r_index);
        }

        //disassembler data window
        disassembler.setEditable(false);
        gridPane.add(disassembler,5,9);


        //Buttons: Load, Clear, Step
        Button loadButton = new Button("Load");
        loadButton.setPrefWidth(50);
        loadButton.setOnAction(e -> {
            chip.loadProgram(path);
            updateChipState();
        });

        Button clearButton = new Button("Clear");
        clearButton.setPrefWidth(50);
        clearButton.setOnAction(e -> {
            chip = new Chip();
            updateChipState();
        });

        Button stepButton = new Button("Step");
        stepButton.setPrefWidth(50);
        stepButton.setOnAction(e -> {
            try {
                chip.emulateChip();
                updateChipState();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        gridPane.add(loadButton,1,12);
        gridPane.add(clearButton,2,12);
        gridPane.add(stepButton,3,12);



        Scene scene = new Scene(gridPane, 500, 400);
        window.setScene(scene);
        window.show();

    }

    private void setUpLabels(){
        for (int i = 0; i < 16; i++ ){
            labels[i] = new Label("V"+correctToHexString(i,1)+":");
        }
        labels[16] = new Label("I:");
        labels[17] = new Label("SP:");
        labels[18] = new Label("PC:");
        labels[19] = new Label("OP:");
    }

    private void setUpTextFields(){
        for (int i = 0; i < textFields.length; i++ ){
            textFields[i] = new TextField(correctToHexString(chip_state[i],4));
            textFields[i].setEditable(false);
            textFields[i].setPrefWidth(50);
        }
    }

    private void updateTextFields(){
        for (int i = 0; i < textFields.length; i++ ){
            textFields[i].setText(correctToHexString(chip_state[i],4));
        }
        int OPcode = chip_state[CHIP_STATE-1];
        Disassembler d = new Disassembler(OPcode);
        disassembler.setText(d.DisassembleOp());
    }

    private void updateChipState(){
        chip_state = chip.getChipState();
        updateTextFields();
        //System.out.println(Arrays.toString(chip_state));
    }

    private String correctToHexString(int data, int length_to_show){
        String temp = Integer.toHexString(data);
        while (temp.length() < length_to_show) temp = "0" + temp;
        temp = temp.toUpperCase( );
        return temp;
    }


}

        /* //OLD CODE, WILL CLEAN IT LATER
        //chip.dumpChipDataToOut();
        chip.loadProgram(path);
        //chip.dumpChipDataToOut();
        try{
            for(int i=0; i<10; i++) chip.emulateChip();
        } catch (Exception e) {
            System.out.println("Error");
        }
        //chip.dumpChipDataToOut();

       for(int pc = 0; pc < memory.getPC(); pc+=2){
            memory.DisassembleOp(pc);
        }
        System.out.println("\n" + "PC at: " + memory.getPC());
        //memory.dumpMemoryToFile("D:\\dump.txt");*/
        /*primaryStage.setTitle("Temp Emulator Window");

        Button button = new Button();
        button.setText("goto Scene2");
        button.setOnAction( e -> primaryStage.setScene(scene2));
        StackPane layout1 = new StackPane();
        layout1.getChildren().add(button);
        Scene scene1 = new Scene(layout1, 300, 250);

        VBox layout2 = new VBox(20);
        Button button2 = new Button("Hi");
        button2.setOnAction(e -> primaryStage.setScene(scene1));
        Label label = new Label("Text");
        layout2.getChildren().addAll(label,button2);
        scene2 = new Scene(layout2, 200, 200);
        primaryStage.setScene(scene1);
        primaryStage.show();
         gridPane.addColumn(0,labelRegV0, labelRegV1, labelRegV2, labelRegV3, labelRegV4, labelRegV5,
                labelRegV6, labelRegV7, labelRegV8, labelRegV9, labelRegVA, labelRegVB, labelRegVC, labelRegVD,
                labelRegVE, labelRegVF, labelI, labelSP, labelPC, labelOP);

        */