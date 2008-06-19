package GeneticModels;

import org.jdom.Element;

/**
 * Brian White Summer 2008
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * @author Brian White
 * @version 1.0 $Id: ThreeAlleleCircularDominanceGeneModel.java,v 1.5 2008-06-19 17:49:57 brian Exp $
 */

public class ThreeAlleleCircularDominanceGeneModel extends GeneModel {

	private Trait t1;  // totally recessive trait
	private Trait t2;  // intermediate trait
	private Trait t3;  // totally dominant trait

	public ThreeAlleleCircularDominanceGeneModel() {
		super();
	}

	public Phenotype getPhenotype(Allele a1, Allele a2) {
		return genoPhenoTable[a1.getIntVal()][a2.getIntVal()];
	}

	public Allele[] getRandomAllelePair() {
		// want equal frequency of each PHENOTYPE
		Allele[] allelePair = new Allele[2];
		switch (rand.nextInt(3)) {

		case 0:
			// phenotype 1
			// 3 possibilities 1,3 3,1 1,1
			switch (rand.nextInt(3)) {
			case 0:
				allelePair[0] = new Allele(t1, 1);
				allelePair[1] = new Allele(t3, 3);
				break;
			case 1:
				allelePair[0] = new Allele(t3, 3);
				allelePair[1] = new Allele(t1, 1);
				break;
			case 2:
				allelePair[0] = new Allele(t1, 1);
				allelePair[1] = new Allele(t1, 1);
				break;
			}
			break;

		case 1:
			// phenotype 2
			// 3 possibilities: 1,2  2,1 and 2,2
			switch (rand.nextInt(3)) {
			case 0:
				allelePair[0] = new Allele(t1, 1);
				allelePair[1] = new Allele(t2, 2);
				break;
			case 1:
				allelePair[0] = new Allele(t2, 2);
				allelePair[1] = new Allele(t1, 1);
				break;
			case 2:
				allelePair[0] = new Allele(t2, 2);
				allelePair[1] = new Allele(t2, 2);
				break;
			}
			break;
			
		case 2:
			// phenotype 3
			// 3 possibilities 2,3 3,2 3,3
			switch(rand.nextInt(3)) {
			case 0:
				allelePair[0] = new Allele(t2, 2);
				allelePair[1] = new Allele(t3, 3);
			case 1:
				allelePair[0] = new Allele(t3, 3);
				allelePair[1] = new Allele(t2, 2);
			case 2:
				allelePair[0] = new Allele(t3, 3);
				allelePair[1] = new Allele(t3, 3);
			}
		}
		return allelePair;
	}

	public void setupGenoPhenoTable() {
		genoPhenoTable = new Phenotype[4][4];

		//there are two alleles and two possible phenos
		// get the phenos first; then load table
		t1 = traitSet.getRandomTrait();   // dom to 3; rec to 2
		t2 = traitSet.getRandomTrait();   // dom to 1; rec to 3
		t3 = traitSet.getRandomTrait();   // dom to 2; rec to 1
		
		genoPhenoTable[0][0] = null;  				//impossible
		genoPhenoTable[0][1] = new Phenotype(t1);  	// 1,Y = 1
		genoPhenoTable[0][2] = new Phenotype(t2);   // 2,Y = 2
		genoPhenoTable[0][3] = new Phenotype(t3);   // 3,Y = 3	
		
		genoPhenoTable[1][0] = new Phenotype(t1);  	// 1,Y = 1
		genoPhenoTable[1][1] = new Phenotype(t1);  	// 1,1 = 1 
		genoPhenoTable[1][2] = new Phenotype(t2);   // 1,2 = 2 (1 is rec to 2)
		genoPhenoTable[1][3] = new Phenotype(t1);   // 1,3 = 1 (1 is dom to 3)
		
		genoPhenoTable[2][0] = new Phenotype(t2);  	// 2,Y = 2
		genoPhenoTable[2][1] = new Phenotype(t2);   // 2,1 = 2 (2 dom to 1)
		genoPhenoTable[2][2] = new Phenotype(t2);   // 2,2 = 2
		genoPhenoTable[2][3] = new Phenotype(t3);   // 2,3 = 3 (2 rec to 3)
		
		genoPhenoTable[3][0] = new Phenotype(t3);   // 3,Y = 3
		genoPhenoTable[3][1] = new Phenotype(t1);   // 3,1 = 1 (3 is rec to 1)
		genoPhenoTable[3][2] = new Phenotype(t3);   // 3,2 = 3 (3 is dom to 2)
		genoPhenoTable[3][3] = new Phenotype(t3);   // 3,3 = 3 
	}

	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append(t1.getBodyPart() + " " + t1.getType() + "<br>");
		b.append("Three Allele Circular Dominance<br>");
		
		b.append("<table border=1>");
		b.append("<tr><th>Genotype</th><th>Phenotype</th></tr>");
		b.append("<tr><td>" + t1.getTraitName() + "/" + t1.getTraitName() + "</td>");
		b.append("<td>" + t1.getTraitName() +"</td></tr>");
		
		b.append("<tr><td>" + t1.getTraitName() + "/" + t2.getTraitName() + "</td>");
		b.append("<td>" + t2.getTraitName() +"</td></tr>");
		
		b.append("<tr><td>" + t2.getTraitName() + "/" + t2.getTraitName() + "</td>");
		b.append("<td>" + t2.getTraitName() +"</td></tr>");
		
		b.append("<tr><td>" + t2.getTraitName() + "/" + t3.getTraitName() + "</td>");
		b.append("<td>" + t3.getTraitName() +"</td></tr>");
		
		b.append("<tr><td>" + t3.getTraitName() + "/" + t3.getTraitName() + "</td>");
		b.append("<td>" + t3.getTraitName() +"</td></tr>");
		
		b.append("<tr><td>" + t3.getTraitName() + "/" + t1.getTraitName() + "</td>");
		b.append("<td>" + t1.getTraitName() +"</td></tr>");
		
		b.append("</table>");
		return b.toString();
	}

	public Element save(int index) throws Exception {
		Element e = new Element("GeneModel");
		e.setAttribute("Index", String.valueOf(index));
		e.setAttribute("Type", "ThreeAlleleCircularDominance");
		e.addContent(new Element("Trait_1").addContent(t1.save()));
		e.addContent(new Element("Trait_2").addContent(t2.save()));
		e.addContent(new Element("Trait_3").addContent(t3.save()));
		return e;
	}

}
