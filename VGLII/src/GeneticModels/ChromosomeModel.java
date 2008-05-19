package GeneticModels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class ChromosomeModel {

	private ArrayList<GeneModel> geneModels;
	private ArrayList<Float> recombinationFrequencies;

	private boolean sexChromosome;

	public ChromosomeModel(boolean sexChromosome) {
		this.sexChromosome = sexChromosome;
		geneModels = new ArrayList<GeneModel>();
		recombinationFrequencies = new ArrayList<Float>();
	}

	public void addGeneModel(GeneModel gm) {
		geneModels.add(gm);
	}

	public void addRecombinationFrequency(float rf) {
		recombinationFrequencies.add(rf);
	}
	
	public int getNumberOfGeneModels() {
		return geneModels.size();
	}
	
	public ArrayList<Phenotype> getPhenotypes(Chromosome cr1, Chromosome cr2) {
		ArrayList<Phenotype> phenos = new ArrayList<Phenotype>();
		for (int i = 0; i < geneModels.size(); i++) {
			phenos.add( 
					(geneModels.get(i)).getPhenotype(
							cr1.getAllele(i), cr2.getAllele(i)));
		}
		return phenos;
	}

	public Chromosome[] getChromosomePairWithRandomAlleles() {
		ArrayList<Allele> maternalAlleles = new ArrayList<Allele>();
		ArrayList<Allele> paternalAlleles = new ArrayList<Allele>();
		Chromosome[] chromos = new Chromosome[2];
		for (int i = 0; i < geneModels.size(); i++) {
			Allele[] allelePair = (geneModels.get(i)).getRandomAllelePair();
			maternalAlleles.add(allelePair[0]);
			paternalAlleles.add(allelePair[1]);
		}
		chromos[0] = new Chromosome(maternalAlleles);
		chromos[1] = new Chromosome(paternalAlleles);

		//if sex-chromo, one chromo needs to be all null alleles
		// half of the time
		if (sexChromosome) {
			Random r = new Random();
			//a male - so make one chromo nulls
			if (r.nextInt(2) == 0) {
				//choose which one
				if (r.nextInt(2) == 0) {
					chromos[0] = NullSexChromosome.getInstance();
				} else {
					chromos[1] = NullSexChromosome.getInstance();;
				}
			}
		}
		return chromos;
	}

	//generate a random gamete from a pair of homologous chromosomes
	//  using recombination
	public Chromosome getGamete(Chromosome cr1, Chromosome cr2) {
		Random r = new Random();

		//first, see if it's a heterogametic sex chromosome pair (XY or ZW)
		//  if so, then return without recombination
		if(sexChromosome && 
				((cr1 == NullSexChromosome.getInstance()) ||
						(cr2 == NullSexChromosome.getInstance()))) {
			if (r.nextInt(2) == 0) {
				return cr1;
			} else {
				return cr2;
			}
		}

		//so, its either an autosome or homogametic sex chromo pair
		//  so we need to do recombination
		//  recombinationFreqs(0) is the RF between gene 0 and 1, etc.
		ArrayList<Allele> oldAlleles1 = cr1.getAllAlleles();
		ArrayList<Allele> oldAlleles2 = cr2.getAllAlleles();
		ArrayList<Allele> newAlleles = new ArrayList<Allele>();

		float choiceBias = 0.5f; // equally likely to choose from
		//  either chromo on the first allele
		boolean pickedFrom1LastTime = false;

		Iterator<Float> rfIt = recombinationFrequencies.iterator();

		for (int i = 0; i < oldAlleles1.size(); i++) {
			if(r.nextFloat() >= choiceBias) {
				newAlleles.add(oldAlleles1.get(i));
				pickedFrom1LastTime = true;
			} else {
				newAlleles.add(oldAlleles2.get(i));
				pickedFrom1LastTime = false;
			}

			//if there's a recombination frequency, then need to
			//  update the 'rf' for the next round
			//  - if you picked from chromo1 last time, then
			//    the chance to get from chr1 again is rf
			if (rfIt.hasNext()) {
				float rf = rfIt.next();
				if (pickedFrom1LastTime) {
					choiceBias = rf;
				} else {
					choiceBias = 1.0f - rf;
				}
			}
		}
		return new Chromosome(newAlleles);
	}

	public String toString() {
		StringBuffer b = new StringBuffer();
		if(sexChromosome) {
			b.append("Sex Chromosome:\n");
		} else {
			b.append("Autosome:\n");
		}
		for (int i = 0; i < geneModels.size(); i++) {
			GeneModel gm = geneModels.get(i);
			if (i >= 1) {
				float rf = recombinationFrequencies.get(i - 1);
				if (rf == 0.5f) {
					b.append("unlinked to:\n");
				} else {
					b.append("recombination frequency = " + rf + " to:\n");
				}
			}
			b.append(gm.toString() + "\n");
		}
		b.append("*******\n");
		return b.toString();
	}
}