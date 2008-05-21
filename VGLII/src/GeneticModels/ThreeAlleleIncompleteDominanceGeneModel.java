package GeneticModels;

public class ThreeAlleleIncompleteDominanceGeneModel extends GeneModel {

	private Trait t1;  // homozygote 1
	private Trait t2;  // homozygote 2
	private Trait t3;  // homozygote 3
	private Trait t4;  // 1,2 heterozygote
	private Trait t5;  // 2,3 heterozygote
	private Trait t6;  // 3,1 heterozygote

	public ThreeAlleleIncompleteDominanceGeneModel() {
		super();
	}

	public Phenotype getPhenotype(Allele a1, Allele a2) {
		return genoPhenoTable[a1.getIntVal()][a2.getIntVal()];
	}

	public Allele[] getRandomAllelePair() {
		// want equal frequency of each PHENOTYPE
		Allele[] allelePair = new Allele[2];
		switch (rand.nextInt(6)) {

		case 0:
			// phenotype 1
			// 1,1 homozygote
			allelePair[0] = new Allele(t1, 1);
			allelePair[1] = new Allele(t1, 1);
			break;

		case 1:
			// phenotype 2
			// 2,2 homozygote
			allelePair[0] = new Allele(t2, 2);
			allelePair[1] = new Allele(t2, 2);
			break;
			
		case 2:
			// phenotype 3
			// 3,3 homozygote
			allelePair[0] = new Allele(t3, 3);
			allelePair[1] = new Allele(t3, 3);
			break;
			
		case 3:
			// 1,2 heterozygote
			// 2 possibilities: 1,2 and 2,1
			if(rand.nextInt(2) == 0) {
				allelePair[0] = new Allele(t1, 1);
				allelePair[1] = new Allele(t2, 2);								
			} else {
				allelePair[0] = new Allele(t2, 2);
				allelePair[1] = new Allele(t1, 1);								
			}
			
		case 4:
			// 2,3 heterozygote
			// 2 possibilities: 3,2 and 2,3
			if(rand.nextInt(2) == 0) {
				allelePair[0] = new Allele(t3, 3);
				allelePair[1] = new Allele(t2, 2);								
			} else {
				allelePair[0] = new Allele(t2, 2);
				allelePair[1] = new Allele(t3, 3);								
			}	
			
		case 5:
			// 1,3 heterozygote
			// 2 possibilities: 1,3 and 3,1
			if(rand.nextInt(2) == 0) {
				allelePair[0] = new Allele(t1, 1);
				allelePair[1] = new Allele(t3, 3);								
			} else {
				allelePair[0] = new Allele(t3, 3);
				allelePair[1] = new Allele(t1, 1);								
			}

		}
		return allelePair;
	}

	public void setupGenoPhenoTable() {
		genoPhenoTable = new Phenotype[4][4];

		//there are two alleles and two possible phenos
		// get the phenos first; then load table
		t1 = traitSet.getRandomTrait();   // homo 1
		t2 = traitSet.getRandomTrait();   // homo 2
		t3 = traitSet.getRandomTrait();   // homo 3
		t4 = traitSet.getRandomTrait();   // 1,2 het
		t5 = traitSet.getRandomTrait();   // 2,3 het
		t6 = traitSet.getRandomTrait();   // 1,3 het
		
		genoPhenoTable[0][0] = null;  				//impossible
		genoPhenoTable[0][1] = new Phenotype(t1);  	// 1,Y = 1
		genoPhenoTable[0][2] = new Phenotype(t2);   // 2,Y = 2
		genoPhenoTable[0][3] = new Phenotype(t3);   // 3,Y = 3	
		
		genoPhenoTable[1][0] = new Phenotype(t1);  	// 1,Y = 1
		genoPhenoTable[1][1] = new Phenotype(t1);  	// 1,1 = 1 
		genoPhenoTable[1][2] = new Phenotype(t2);   // 1,2 = 4 
		genoPhenoTable[1][3] = new Phenotype(t3);   // 1,3 = 6 
		
		genoPhenoTable[2][0] = new Phenotype(t2);  	// 2,Y = 2
		genoPhenoTable[2][1] = new Phenotype(t2);   // 2,1 = 4 
		genoPhenoTable[2][2] = new Phenotype(t2);   // 2,2 = 2
		genoPhenoTable[2][3] = new Phenotype(t3);   // 2,3 = 5 
		
		genoPhenoTable[3][0] = new Phenotype(t3);   // 3,Y = 3
		genoPhenoTable[3][1] = new Phenotype(t3);   // 3,1 = 6 
		genoPhenoTable[3][2] = new Phenotype(t3);   // 3,2 = 5 
		genoPhenoTable[3][3] = new Phenotype(t3);   // 3,3 = 3 
	}

	public String toString() {
		return "Three Allele " 
		+ "Incomplete Dominance; " 
		+ t1.toString()
		+ " is one homozygote, "
		+ t4.getTraitName()
		+ " is in between that and "
		+ t2.getTraitName()
		+ " (another homozygote), "
		+ t5.getTraitName()
		+ " is in between that and "
		+ t3.getTraitName()
		+ " (another homozygote), and "
		+ t6.getTraitName()
		+ " is a in between that and the "
		+ " first homozygote.";
	}

}