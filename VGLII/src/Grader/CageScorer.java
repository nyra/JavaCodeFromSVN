package Grader;

import java.util.Iterator;
import java.util.TreeMap;

import GeneticModels.Cage;
import GeneticModels.OrganismList;

/**
 * takes a Cage and determines if it shows:
 * 	-sex linkage or not
 * 		= if sex imbalance in offspring (eg red females but no red males)
 * 	-interaction evidence for one or more traits
 * 		= if offspring don't look like parents
 * 			A x B -> all A or all B
 * 			A x A -> any pheno other than A
 * 			A x B -> any pheno other than A or B
 * @author brian
 *
 */
public class CageScorer {
	
	public static String scoreCage(Cage cage) {
		StringBuffer b = new StringBuffer();
		
		b.append("<li><b>Cage ");
		b.append(cage.getId() + 1);
		b.append(" </b></li>");
		b.append("<ul>");
		
		/**
		 * look for sex linkage first:
		 * - for each child phenotype, if you find
		 * 		males but no females
		 * 			or
		 * 		females but no males
		 * -> it shows evidence of sex linkage
		 * -> if not, it does not show evidence of sex linkage
		 */
		boolean showsSexLinkage = false;
		TreeMap<String, OrganismList> children = cage.getChildren();
		Iterator<String> phenoIt = children.keySet().iterator();
		while (phenoIt.hasNext()) {
			String pheno = phenoIt.next();
			OrganismList oList = children.get(pheno);
			int males = oList.getNumberOfMales();
			int females = oList.getNumberOfFemales();
			if (((males == 0) && (females > 0)) || ((males > 0) && (females == 0))) {
				showsSexLinkage = true;
			}
		}
		b.append("<li>");
		if (showsSexLinkage) {
			b.append("Shows ");
		} else {
			b.append("Does not show ");
		}
		b.append("evidence of sex linkage.</li>");
		
		b.append("</ul>");
		return b.toString();
	}
}


