package GeneticMelody;
import java.text.DecimalFormat;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jfugue.integration.LilyPondParserListener;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.theory.Note;
import org.staccato.StaccatoParser;

public class ProgramUI extends Application{
    GeneticAlgorithm GA; 
    Population initialPopulation; 
    private boolean newRun; 
    Stage window; 
    Scene scene;
    ChoiceBox<String> choiceBox; 
    Label conditionsLabel, keyLabel, pSizeLabel, mRateLabel, haltingLabel; 
    TextField sizeField, mRateField, genField, fitField; 
    CheckBox genChkBx, fitChkBx; 
    Button run; 
     
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        newRun = true; 
        
        window = primaryStage; 
        window.setTitle("Genetic Algorithm");
        
        GridPane grid = new GridPane(); 
        grid.setPadding(new Insets(15));
        grid.setVgap(8); 
        grid.setHgap(10);
        
        conditionsLabel = new Label("GA Parameters:"); 
        GridPane.setConstraints(conditionsLabel,0,0); 
        keyLabel = new Label("Key:"); 
        GridPane.setConstraints(keyLabel,0,1);
        choiceBox = new ChoiceBox();
        choiceBox.getItems().addAll("C", "C#","D","Eb","E","F","F#","G","G#","A","Bb","B");
        choiceBox.getSelectionModel().select(0);
        GridPane.setConstraints(choiceBox,1,1);
        
        pSizeLabel = new Label("Population Size:"); 
        GridPane.setConstraints(pSizeLabel, 0, 2); 
        sizeField = new TextField(); 
        GridPane.setConstraints(sizeField, 1, 2); 
        mRateLabel = new Label("Mutation Rate:"); 
        GridPane.setConstraints(mRateLabel, 0, 3); 
        mRateField = new TextField(".009"); 
        GridPane.setConstraints(mRateField, 1, 3); 
        
        haltingLabel = new Label("Halting Conditions:");
        GridPane.setConstraints(haltingLabel, 0, 5);
        
        genChkBx = new CheckBox("# of Generations:");
        fitChkBx = new CheckBox("Minimum Fitness:"); 
        genField = new TextField(); 
        fitField = new TextField(); 
        GridPane.setConstraints(genChkBx, 0, 6); 
        GridPane.setConstraints(fitChkBx, 0, 7); 
        GridPane.setConstraints(genField, 1, 6); 
        GridPane.setConstraints(fitField, 1, 7); 
        
        run = new Button("Run"); 
        GridPane.setConstraints(run, 0, 9);
        run.setOnAction(e -> runHandler());
        
        grid.getChildren().addAll(conditionsLabel, keyLabel, pSizeLabel, mRateLabel, 
                    sizeField, mRateField, choiceBox, haltingLabel, 
                    genChkBx, fitChkBx, genField, fitField, run); 
        
        scene = new Scene(grid, 450, 300); 
        window.setScene(scene); 
        window.show();
    }
    
    private void runHandler(){
        if(newRun){
            GA = new GeneticAlgorithm(Integer.parseInt(sizeField.getText())); 
            GA.setKey(choiceBox.getValue());
            GA.setMutationRate(Double.parseDouble(mRateField.getText()));
            initialPopulation = GA.initialPopulation();
        }
        if(genChkBx.isSelected())
            GA.setGenHalt(Integer.parseInt(genField.getText()));
        if(fitChkBx.isSelected())
            GA.setFitHalt(Double.parseDouble(fitField.getText()));
        
        GA.runAlgorithm(genChkBx.isSelected(), fitChkBx.isSelected()); 
        ResultsWindow rw = new ResultsWindow(); 
        rw.display(initialPopulation, GA.getSortedPopulation(), GA);
    }
    
    private void setNewRun(boolean set){
        newRun = set; 
    }
    
    private class ResultsWindow{
        private void display(Population initialPop, Population lastPop, GeneticAlgorithm GA){
            Stage window = new Stage(); 
            window.setTitle("Results");
            window.initModality(Modality.APPLICATION_MODAL);
            
            GridPane grid = new GridPane(); 
            grid.setPadding(new Insets(15));
            grid.setVgap(6); 
            grid.setHgap(30);
            
            Label keyLabel, mRateLabel, pSizeLabel, genLabel, initPopLabel, lastPopLabel; 
            Text initPopText, lastPopText;
            Button cont, startOver, play1, play2, play3, play4; 
            
            //Labels
            keyLabel = new Label("Key: " + GA.getKey()); 
            mRateLabel = new Label("Mutation Rate: " + GA.getMutationRate());
            pSizeLabel = new Label("Population Size: " + GA.getPSize());
            genLabel = new Label("Total # of Gens: " + GA.getGenCount()); 
            initPopLabel = new Label("Initial Population:");
            lastPopLabel = new Label("Last Population:"); 
            
            //Buttons
            play1 = new Button("Play Lowest"); 
            play1.setOnAction(e -> play(initialPop.population[0]));
            play2 = new Button("Play Highest"); 
            play2.setOnAction(e -> play(initialPop.population[GA.getPSize()-1]));
            play3 = new Button("Play Lowest"); 
            play3.setOnAction(e -> play(lastPop.population[0]));
            play4 = new Button("Play Highest"); 
            play4.setOnAction(e -> play(lastPop.population[GA.getPSize()-1]));
            cont = new Button("Continue");
            cont.setOnAction(e -> {
                setNewRun(false); 
                window.close();
            });
            startOver = new Button("Start Over"); 
            startOver.setOnAction(e -> {
                setNewRun(true);
                window.close();
            });
            
            //Text
            DecimalFormat df = new DecimalFormat("#0.00%"); 
            String text1, text2; 
            text1 = "Lowest Fitness: " + df.format(GA.getFitnessPercentage(initialPop.population[0]))+
                    "\nHighest Fitness: " + df.format(GA.getFitnessPercentage(initialPop.population[GA.getPSize()-1]))+
                    "\nAverage Fitness: " + df.format((initialPop.getAverageFitness()/GA.getMaxPossibleFitness()));  
            text2 = "Lowest Fitness: " + df.format(GA.getFitnessPercentage(lastPop.population[0]))+
                    "\nHighest Fitness: " + df.format(GA.getFitnessPercentage(lastPop.population[GA.getPSize()-1]))+
                    "\nAverage Fitness: " + df.format((lastPop.getAverageFitness()/GA.getMaxPossibleFitness()));  
            initPopText = new Text(text1); 
            lastPopText = new Text(text2); 
            
            //placement
            GridPane.setConstraints(keyLabel,0,0);
            GridPane.setConstraints(mRateLabel,1,0);
            GridPane.setConstraints(pSizeLabel,0,1);
            GridPane.setConstraints(genLabel,1,1);
            GridPane.setConstraints(initPopLabel,0,3);
            GridPane.setConstraints(initPopText,0,4);
            GridPane.setConstraints(play1,1,4);
            GridPane.setConstraints(play2,2,4);
            GridPane.setConstraints(lastPopLabel,0,6);
            GridPane.setConstraints(lastPopText,0,7);
            GridPane.setConstraints(play3,1,7);
            GridPane.setConstraints(play4,2,7);
            GridPane.setConstraints(cont,0,9);
            GridPane.setConstraints(startOver,1,9);

            grid.getChildren().addAll(keyLabel, mRateLabel, pSizeLabel, genLabel, 
                    initPopLabel, lastPopLabel,initPopText, lastPopText, cont, 
                    startOver, play1, play2, play3, play4); 
            Scene scene = new Scene(grid,450,300); 
            window.setScene(scene);
            window.showAndWait();
        }
        
        private void play(Member member){
            Pattern pattern = new Pattern();
            Player player = new Player(); 
            //LilyPondParserListener ly = new LilyPondParserListener(); 
            for(Note note : member.notes){
                pattern.add(note); 
            }
            player.play(pattern);
            //StaccatoParser parser = new StaccatoParser(); 
            //parser.addParserListener(ly);
            //parser.parse(pattern);
            //System.out.println(ly.getLyString());
            //System.out.println(pattern);
        }
    }
}
