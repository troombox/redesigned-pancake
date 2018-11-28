import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Emulator extends Application {

    //GUI
    Stage window;

    //Emulation
    String path = "C:\\Pong.ch8";
    Chip chip = new Chip();

    public static void main(String []args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        window = primaryStage;
        window.setTitle("Emulator State");

        //Labels: V0 - VF, I, SP, PC, OP
        Label labelRegV0 = new Label("V0:");
        Label labelRegV1 = new Label("V1:");
        Label labelRegV2 = new Label("V2:");
        Label labelRegV3 = new Label("V3:");
        Label labelRegV4 = new Label("V4:");
        Label labelRegV5 = new Label("V5:");
        Label labelRegV6 = new Label("V6:");
        Label labelRegV7 = new Label("V7:");
        Label labelRegV8 = new Label("V8:");
        Label labelRegV9 = new Label("V9:");
        Label labelRegVA = new Label("VA:");
        Label labelRegVB = new Label("VB:");
        Label labelRegVC = new Label("VC:");
        Label labelRegVD = new Label("VD:");
        Label labelRegVE = new Label("VE:");
        Label labelRegVF = new Label("VF:");

        Label labelI = new Label("I:");
        Label labelSP = new Label("SP:");
        Label labelPC = new Label("PC:");
        Label labelOP = new Label("OP");

        //TextFields: V0 - VF, I, SP, PC, OP
        TextField textRegV0 = new TextField();

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setGridLinesVisible(true);
        gridPane.setAlignment(Pos.TOP_CENTER);
        gridPane.addColumn(0,labelRegV0, labelRegV1, labelRegV2, labelRegV3, labelRegV4, labelRegV5,
                labelRegV6, labelRegV7, labelRegV8, labelRegV9, labelRegVA, labelRegVB, labelRegVC, labelRegVD,
                labelRegVE, labelRegVF, labelI, labelSP, labelPC, labelOP);
        gridPane.addColumn(1,textRegV0);

        //Buttons: Load, Clear, Step
        Button loadButton = new Button("Load");
        loadButton.setOnAction(e -> chip.loadProgram(path));

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> chip = new Chip());

        Button stepButton = new Button("Step");
        stepButton.setOnAction(e -> {
            try {
                chip.emulateChip();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        Scene scene = new Scene(gridPane, 200, 600);
        window.setScene(scene);
        window.show();

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
        primaryStage.show();*/