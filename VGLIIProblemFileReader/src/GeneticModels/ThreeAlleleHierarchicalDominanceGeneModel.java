package GeneticModels;

import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import VGL.Messages;

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
 * @version 1.0 $Id: ThreeAlleleHierarchicalDominanceGeneModel.java,v 1.11 2009-09-22 19:48:48 brian Exp $
 */

public class ThreeAlleleHierarchicalDominanceGeneModel extends GeneModel {

	private Trait t1;  // totally recessive trait
	private Trait t2;  // intermediate trait
	private Trait t3;  // totally dominant trait

	public ThreeAlleleHierarchicalDominanceGeneModel() {
		super();
	}

	//build from saved work file
	public ThreeAlleleHierarchicalDominanceGeneModel(
			List<Element> traitList, int chromo, int gene) {
		Iterator<Element> elIt = traitList.iterator();
		t1 = TraitFactory.getInstance().buildTrait(elIt.next(), chromo, gene, 1);
		t2 = TraitFactory.getInstance().buildTrait(elIt.next(), chromo, gene, 2);
		t3 = TraitFactory.getInstance().buildTrait(elIt.next(), chromo, gene, 3);
		setupGenoPhenoTable();
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
			// 1,1 homozygote
			allelePair[0] = new Allele(t1, 1);
			allelePair[1] = new Allele(t1, 1);
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
			// 5 possibilities 1,3 3,1 2,3 3,2 3,3
			switch(rand.nextInt(5)) {
			case 0:
				allelePair[0] = new Allele(t1, 1);
				allelePair[1] = new Allele(t3, 3);
			case 1:
				allelePair[0] = new Allele(t3, 3);
				allelePair[1] = new Allele(t1, 1);
			case 2:
				allelePair[0] = new Allele(t2, 2);
				allelePair[1] = new Allele(t3, 3);
			case 3:
				allelePair[0] = new Allele(t3, 3);
				allelePair[1] = new Allele(t2, 2);
			case 4:
				allelePair[0] = new Allele(t3, 3);
				allelePair[1] = new Allele(t3, 3);
			}
		}
		return allelePair;
	}
	
	public void pickRandomTraits() {
		//there are three alleles and three possible phenos
		// get the phenos first; then load table
		t1 = traitSet.getRandomTrait();   // fully recessive
		t2 = traitSet.getRandomTrait();   // intermediate
		t3 = traitSet.getRandomTrait();   // fully dominant
	}

	public void setupGenoPhenoTable() {
		genoPhenoTable = new Phenotype[4][4];
		
		genoPhenoTable[0][0] = null;  				//impossible
		genoPhenoTable[0][1] = new Phenotype(t1);  	// 1,Y = 1
		genoPhenoTable[0][2] = new Phenotype(t2);   // 2,Y = 2
		genoPhenoTable[0][3] = new Phenotype(t3);   // 3,Y = 3	
		
		genoPhenoTable[1][0] = new Phenotype(t1);  	// 1,Y = 1
		genoPhenoTable[1][1] = new Phenotype(t1);  	// 1,1 = 1 
		genoPhenoTable[1][2] = new Phenotype(t2);   // 1,2 = 2 (1 is rec to all)
		genoPhenoTable[1][3] = new Phenotype(t3);   // 1,3 = 3 (1 is rec to all)
		
		genoPhenoTable[2][0] = new Phenotype(t2);  	// 2,Y = 2
		genoPhenoTable[2][1] = new Phenotype(t2);   // 2,1 = 2 (2 dom to 1)
		genoPhenoTable[2][2] = new Phenotype(t2);   // 2,2 = 2
		genoPhenoTable[2][3] = new Phenotype(t3);   // 2,3 = 3 (2 rec to 3)
		
		genoPhenoTable[3][0] = new Phenotype(t3);   // 3,Y = 3
		genoPhenoTable[3][1] = new Phenotype(t3);   // 3,1 = 3 (3 is dom to all)
		genoPhenoTable[3][2] = new Phenotype(t3);   // 3,2 = 3 (3 is dom to all)
		genoPhenoTable[3][3] = new Phenotype(t3);   // 3,3 = 3
	}

	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append(Messages.getTranslatedTraitName(t1) + "<br>");
		b.append(Messages.getString("VGLII.ThreeAlleleHierarchicalDominance") + "<br>");
		b.append("<ul>");
		b.append("<li>" + Messages.getString("VGLII." + t1.getTraitName()) + " " 
				+ Messages.getString("VGLII.IsRecessiveToAll") + "</li>");
		b.append("<li>" + Messages.getString("VGLII." + t2.getTraitName()) + " " 
				+ Messages.getString("VGLII.IsInBetween") + "</li>");
		b.append("<li>" + Messages.getString("VGLII." + t3.getTraitName()) + " " 
				+ Messages.getString("VGLII.IsDominantToAll") + "</li>");
		b.append("</ul>");
		
		b.append("<table border=1>");
		b.append("<tr><th>" + Messages.getString("VGLII.Genotype") + "</th><th>"
				+ Messages.getString("VGLII.Phenotype") + "</th></tr>");
		b.append("<tr><td>" + Messages.getString("VGLII." + t1.getTraitName()) + "/" 
				+ Messages.getString("VGLII." + t1.getTraitName()) + "</td>");
		b.append("<td>" + Messages.getString("VGLII." + t1.getTraitName()) +"</td></tr>");
		
		b.append("<tr><td>" + Messages.getString("VGLII." + t1.getTraitName()) + "/" 
				+ Messages.getString("VGLII." + t2.getTraitName()) + "</td>");
		b.append("<td>" + Messages.getString("VGLII." + t2.getTraitName()) +"</td></tr>");
		
		b.append("<tr><td>" + Messages.getString("VGLII." + t2.getTraitName()) + "/" 
				+ Messages.getString("VGLII." + t2.getTraitName()) + "</td>");
		b.append("<td>" + Messages.getString("VGLII." + t2.getTraitName()) +"</td></tr>");
		
		b.append("<tr><td>" + Messages.getString("VGLII." + t2.getTraitName()) + "/" 
				+ Messages.getString("VGLII." + t3.getTraitName()) + "</td>");
		b.append("<td>" + Messages.getString("VGLII." + t3.getTraitName()) +"</td></tr>");
		
		b.append("<tr><td>" + Messages.getString("VGLII." + t3.getTraitName()) + "/" 
				+ Messages.getString("VGLII." + t3.getTraitName()) + "</td>");
		b.append("<td>" + Messages.getString("VGLII." + t3.getTraitName()) +"</td></tr>");
		
		b.append("<tr><td>" + Messages.getString("VGLII." + t3.getTraitName()) + "/" 
				+ Messages.getString("VGLII." + t1.getTraitName()) + "</td>");
		b.append("<td>" + Messages.getString("VGLII." + t3.getTraitName()) +"</td></tr>");
		
		b.append("</table>");
		return b.toString();
	}

	public Element save(int index, float rf) throws Exception {
		Element e = new Element("GeneModel");
		e.setAttribute("Index", String.valueOf(index));
		e.setAttribute("Type", "ThreeAlleleHierarchicalDominance");
		e.setAttribute("RfToPrevious", String.valueOf(rf));
		e.addContent(t1.save(1));
		e.addContent(t2.save(2));
		e.addContent(t3.save(3));
		return e;
	}
}
