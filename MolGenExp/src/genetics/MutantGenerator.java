package genetics;


import molGenExp.ExpressedAndFoldedGene;
import molGenExp.Organism;
import preferences.MGEPreferences;
import utilities.Mutator;

public class MutantGenerator implements Runnable {

	private int mutantCount;	//number of mutants to make
	private int current;
	private Organism o;
	private int trayNum;
	private GeneticsWorkbench gw;
	private OffspringList offspringList;
	
	private MGEPreferences preferences;
	private Mutator mutator;

	MutantGenerator (Organism o,
			int mutantCount,
			int trayNum,
			OffspringList offspringList, 
			GeneticsWorkbench gw) {
		this.o = o;
		this.trayNum = trayNum;
		this.gw = gw;
		this.offspringList = offspringList;
		this.mutantCount = mutantCount;
		preferences = MGEPreferences.getInstance();
		mutator = Mutator.getInstance();
	}

	public int getLengthOfTask() {
		return mutantCount;
	}

	public int getCurrent() {
		return current;
	}

	public Organism getOrganism() {
		return o;
	}

	public void stop() {
		current = mutantCount;
	}

	boolean done() {
		if (current >= mutantCount) {
			return true;
		} else {
			return false;
		}
	}

	public void run() {
		for (current = 0; current < mutantCount; current++) {
			ExpressedAndFoldedGene efg1 = null;
			ExpressedAndFoldedGene efg2 = null;
			efg1 = mutator.mutateGene(o.getGene1());
			efg2 = mutator.mutateGene(o.getGene2());

			if (current < mutantCount) {
				offspringList.add(new Organism(trayNum + "-" + (current + 1),
						efg1,
						efg2));
			}
		}
	}
}
