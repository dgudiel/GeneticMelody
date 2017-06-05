package seniorproject;
import java.util.Random;
import org.jfugue.theory.Note;

public class Population{
    private static int populationSize; 
    public Member[] population; 
    private int totalFitness; 
    private Random rand = new Random(); 
    
    //contructor methods
    public Population(){        //constructor with no parameters, for use after population size is set by instantiation of initial population
        this.population = new Member[populationSize]; 
    }
    public Population(int populationSize){      //constructor with parameter, also sets the population size for subsequent populations 
        this.populationSize = populationSize; 
        population = new Member[this.populationSize]; 
        totalFitness = 0; 
    }
    
    public void populate(){     //populates the empty population array with Member objects
        for(int i = 0; i < populationSize; i++){
            population[i] = new Member();
            for(int j = 0; j < Member.MEMBERSIZE; j++)
                this.population[i].notes.add(newNote()); 
        }
    }
    
    public Note newNote(){      //returns a random Note object that is within the given octave range
        int offset = 48;    //determines at what point the octave range begins 
        return new Note((offset + rand.nextInt((3*GeneticAlgorithm.OCTAVE)))); //the number OCTAVE is multiplied by gives the range over which notes can be generated
    }

    public void calcTotalFitness(){
        for(Member member : population)
            totalFitness += member.getFitness(); 
    }
    
    public Population clonePopulation(){
        Population clone = new Population(); 
        for(int i = 0; i < populationSize; i++)
            clone.population[i] = this.population[i].cloneMember(); 
        return clone; 
    }
    
    public int getPopulationSize(){
        return populationSize; 
    }
    
    public double getAverageFitness(){
        int sum = 0;
        for(int i = 0; i < populationSize; i++)
            sum += population[i].getFitness(); 
        
        return (sum/(double)populationSize);     
    }
    
    public int getTotalFitness(){
        return totalFitness; 
    }
}

