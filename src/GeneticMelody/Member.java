package GeneticMelody;
import java.util.ArrayList;
import java.util.List;
import org.jfugue.theory.Note;

public class Member implements Comparable<Member>{
    public static final int MEMBERSIZE = 37; 
    private int fitness; 
    public List<Note> notes; 
    
    public Member(){
        fitness = 0; 
        notes = new ArrayList<>();
    }
    
    public void resetFitness(){
        this.fitness = 0; 
    }
    
    public void incrementFitness(){
        this.fitness++; 
    }
    
    public void decrimentFitness(){
        if(fitness > 0)
            this.fitness--; 
    }

    public int getFitness(){
        return fitness; 
    }
    
    public Member cloneMember(){
        Member clone = new Member(); 
        for(Note note : this.notes)
            clone.notes.add(note); 
        clone.fitness = this.fitness;    
        return clone; 
    }
    
    public void finishingNote(){
        this.notes.get((this.notes.size()-1)).setDuration("W"); 
    }
    
    @Override
    public int compareTo(Member compareMember){
	int compareQuantity = ((Member) compareMember).getFitness();
		return this.fitness - compareQuantity;
    }
}
