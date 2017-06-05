package seniorproject;
import static java.lang.Math.abs;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.jfugue.theory.Intervals;
import org.jfugue.theory.Note;
import org.jfugue.theory.Scale;

public class GeneticAlgorithm {
    public static final int OCTAVE = 12;
    private static final int MAXGENERATIONS = 10000; 
    private int pSize;
    private List<Note> key; 
    private double mutationRate;
    private int genHalt; 
    private double fitHalt; 
    private int genCount; 
    private Population  thisGen, nextGen, sortedPopulation;
    private Random rand = new Random(); 
    
            
    public GeneticAlgorithm(int pSize){
        this.pSize = pSize; 
        thisGen = new Population(pSize);
        genCount = 0; 
    }
    
    public Population initialPopulation(){
        thisGen.populate();
        Population initialpop = thisGen.clonePopulation();
        fitnessFunction(initialpop); 
        Arrays.sort(initialpop.population); 
        return initialpop; 
    }
    
    public void runAlgorithm(boolean genBreak, boolean fitBreak){
        int selected1, selected2;
        int count = 0; 
     
        /*Start of GA*/
        while(count < MAXGENERATIONS){
            fitnessFunction(thisGen);
            sortedPopulation = thisGen.clonePopulation(); 
            Arrays.sort(sortedPopulation.population);
            
            nextGen = new Population();
            nextGen.population[0] = sortedPopulation.population[(pSize-1)].cloneMember(); 
            nextGen.population[1] = sortedPopulation.population[(pSize-2)].cloneMember();
            nextGen.population[0].resetFitness();
            nextGen.population[1].resetFitness(); 
       
            for(int i = 2; i < pSize; i++){
                selected1 = rouletteSelection(thisGen); 
                do{
                    selected2 = rouletteSelection(thisGen); 
                }while(selected1 == selected2); 
            
                nextGen.population[i] = crossOver(thisGen, selected1, selected2); 
            }
        
            mutation(nextGen);
            
            sortedPopulation = nextGen.clonePopulation(); 
            fitnessFunction(sortedPopulation);
            Arrays.sort(sortedPopulation.population);
        
            thisGen = nextGen; 
            count++; 
            genCount++; 
            
            if(genBreak){
                if(count >= genHalt)
                    break;
            }
            if(fitBreak){
                if(getFitnessPercentage(sortedPopulation.population[pSize-1]) >= fitHalt)
                    break; 
            }
        } 
        /*end of algorithm*/
        
        sortedPopulation.population[pSize-1].finishingNote();
    }
    
    public void setKey(String k){
        Intervals intr = Scale.MAJOR.getIntervals(); 
        intr.setRoot(k); 
        key = intr.getNotes();
        //System.out.println(key);
    }
    
    public void setMutationRate(double mRate){
        mutationRate = mRate; 
    }
    
    public void setFitHalt(double minimumFitness){
        fitHalt = minimumFitness; 
    }
    
    public void setGenHalt(int minimumGenerations){
        genHalt = minimumGenerations; 
    }
    
    public String getKey(){
        String k = key.get(0).toString(); 
        k = k.substring(0, k.length()-1); 
        if(k.length()> 1){
            String l = k.substring(k.length()-1, k.length());
            return k.substring(0,1) + l.toLowerCase();
        }
        else
            return k; 
    }
    
    public double getMutationRate(){
        return mutationRate; 
    }
    public int getPSize(){
        return pSize; 
    }
    
    public int getGenCount(){
        return genCount; 
    }
    
    public Population getSortedPopulation(){
        return sortedPopulation; 
    }
    
    public int getMaxPossibleFitness(){
        return (2*Member.MEMBERSIZE) - 1; 
    }
    
    public double getFitnessPercentage(Member member){
        return member.getFitness()/(double)getMaxPossibleFitness(); 
    }
    
    private void fitnessFunction(Population pop){
        for(int i = 0; i < pSize; i++){
            for(Note note : pop.population[i].notes){          
                for(Note kNote : key){           
                    if(((abs(note.getValue()-kNote.getValue())) % OCTAVE) == 0)
                        pop.population[i].incrementFitness(); 
                }
            }
            for(int j = 0; j < (pop.population[i].notes.size() - 1); j++){
                if(abs(pop.population[i].notes.get(j).getValue() - pop.population[i].notes.get(j+1).getValue()) <= 5)
                    pop.population[i].incrementFitness(); 
                else if(abs(pop.population[i].notes.get(j).getValue() - pop.population[i].notes.get(j+1).getValue()) > OCTAVE)
                    pop.population[i].decrimentFitness();   
            }  
        }
        pop.calcTotalFitness();
    }
    
    private int rouletteSelection(Population pop){
        int sum = 0; 
        int rouletteNumber = rand.nextInt(pop.getTotalFitness()); 
        
        for(int i = 0; i < pSize; i++){
            sum += pop.population[i].getFitness(); 
            if(sum >= rouletteNumber)
                return i; 
        }
        return -1; 
    }
    
    private Member crossOver(Population pop, int p1, int p2){
        Member child = new Member(); 
        for(int i = 0; i < Member.MEMBERSIZE; i++){
            if(rand.nextBoolean())
                child.notes.add(pop.population[p1].notes.get(i)); 
            else
                child.notes.add(pop.population[p2].notes.get(i)); 
        }
        return child; 
    }
    
    private void mutation(Population pop){
        for(Member member : pop.population){
            for(int i = 0; i < Member.MEMBERSIZE; i++){
                if(rand.nextDouble() < mutationRate)
                    member.notes.set(i, pop.newNote()); 
            }
        }
    }  
    
    /* Alternate mutation method */ 
    //private void mutation(Population pop){
    //    for(Member member : pop.population){
    //        if(rand.nextDouble() < mutationRate){
    //           member.notes.set(rand.nextInt(member.MEMBERSIZE), pop.newNote()); 
    //        }
    //    }  
    //}
}
