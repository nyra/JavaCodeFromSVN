package GeneticModels;

import java.util.ArrayList;

public class Organism {
	
	private int id;  //organism's id#
	private int cageId; // cage's id#

	private Chromosome maternalAutosome;
	private Chromosome paternalAutosome;
	private Chromosome maternalSexChromosome;
	private Chromosome paternalSexChromosome;

	private ArrayList<Phenotype> phenotypes;

	private boolean male;

	private GeneticModel geneticModel;

	//full constructor
	public Organism(int cageId,
			Chromosome maternalAutosome,
			Chromosome paternalAutosome,
			Chromosome maternalSexChromosome,
			Chromosome paternalSexChromosome,
			ArrayList<Phenotype> phenotypes,
			boolean male,
			GeneticModel geneticModel) {
		this.cageId = cageId;
		this.maternalAutosome = maternalAutosome;
		this.paternalAutosome = paternalAutosome;
		this.maternalSexChromosome = maternalSexChromosome;
		this.paternalSexChromosome = paternalSexChromosome;
		this.phenotypes = phenotypes;
		this.male = male;
		this.geneticModel = geneticModel;
	}
	
	//constructor for field population
	//  where cageId = 1
	public Organism(Chromosome maternalAutosome,
			Chromosome paternalAutosome,
			Chromosome maternalSexChromosome,
			Chromosome paternalSexChromosome,
			ArrayList<Phenotype> phenotypes,
			boolean male,
			GeneticModel geneticModel) {
		
		this(1, 
				maternalAutosome, 
				paternalAutosome,
				maternalSexChromosome,
				paternalSexChromosome,
				phenotypes,
				male,
				geneticModel);
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCageId() {
		return cageId;
	}

	public void setCageId(int cageId) {
		this.cageId = cageId;
	}

	public Chromosome getMaternalAutosome() {
		return maternalAutosome;
	}

	public Chromosome getPaternalAutosome() {
		return paternalAutosome;
	}

	public Chromosome getMaternalSexChromosome() {
		return maternalSexChromosome;
	}

	public Chromosome getPaternalSexChromosome() {
		return paternalSexChromosome;
	}

	public ArrayList<Phenotype> getPhenotypes() {
		return phenotypes;
	}

	public boolean isMale() {
		return male;
	}

	public String getPhenotypeString() {
		StringBuffer b = new StringBuffer();
		for (Phenotype p: phenotypes) {
			b.append(p.getTrait().getTraitName());
			b.append("-");
			b.append(p.getTrait().getBodyPart());
			b.append("/");
		}
		b.deleteCharAt(b.length() - 1);
		return b.toString();
	}

	//shows genotype
	public String getToolTipTextString() {
		StringBuffer b = new StringBuffer();
		if (maternalAutosome.getAllAlleles().size() > 0) {
			b.append("A:{");
			b.append(maternalAutosome.toString());
			b.append("}/{");
			b.append(paternalAutosome.toString());
			b.append("} ");
		}

		if (geneticModel.getSexLinkageType() == GeneticModel.XX_XY) {
			b.append("X");
		} else {
			b.append("Z");
		}

		// see if there's any alleles on the sex chromosomes
		if ((maternalSexChromosome.getAllAlleles().size() > 0) ||
				(paternalSexChromosome.getAllAlleles().size() > 0)){
			
			// if so, print them, unless they're a null
			b.append(":");
			
			if (maternalSexChromosome != NullSexChromosome.getInstance()) {
				b.append("[");
				b.append(maternalSexChromosome.toString());
				b.append("]/");
			}
			
			if (paternalSexChromosome != NullSexChromosome.getInstance()) {	
				b.append("[");
				b.append(paternalSexChromosome.toString());
				b.append("]/");
			} 

			// if heterogametic, need to finish with Y or W
			if ((maternalSexChromosome == NullSexChromosome.getInstance()) ||
					(paternalSexChromosome == NullSexChromosome.getInstance())){
				if (geneticModel.getSexLinkageType() == GeneticModel.XX_XY) {
					b.append("Y");
				} else {
					b.append("W");
				}
			} else {
				// trim trailing slash
				b.deleteCharAt(b.length() - 1);
			}
			
		} else {
			
			// no alleles on sex chromosomes, so just show XX, XY, ZZ, or ZW as appropriate
			//  see if homo or heterogametic
			if ((maternalSexChromosome == NullSexChromosome.getInstance()) ||
					(paternalSexChromosome == NullSexChromosome.getInstance())) {
				
				//heterogametic
				if (geneticModel.getSexLinkageType() == GeneticModel.XX_XY) {
					b.append("Y");
				} else {
					b.append("W");
				}
			} else {
				
				//homogametic
				if (geneticModel.getSexLinkageType() == GeneticModel.XX_XY) {
					b.append("X");
				} else {
					b.append("Z");
				}
			}
		}
		return b.toString();
	}
	
	public String getSexString() {
		if (male) {
			return "male";
		} else {
			return "female";
		}
	}

	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("Organism: [");
		if (!male) {
			b.append("fe");
		}
		b.append("male]\n");
		b.append("Genotype:\n");
		b.append("Maternal auto:\n");
		b.append(maternalAutosome.toString() + "\n");
		b.append("Paternal auto:\n");
		b.append(paternalAutosome.toString() + "\n");
		b.append("Maternal sex chr:\n");
		b.append(maternalSexChromosome.toString() + "\n");
		b.append("Paternal sex chr:\n");
		b.append(paternalSexChromosome.toString() + "\n");
		b.append("Phenotypes:\n");
		for (Phenotype p: phenotypes) {
			b.append(p.toString() + "\n");
		}
		b.append("pheno string=" + getPhenotypeString() + "\n");
		b.append("**organism**\n");
		return b.toString();
	}

}
