
import org.jdom.Element;

/**
 * Naing Naing Maw cs681-3 Fall 2002 - Spring 2003 Project VGL File:
 * ~nnmaw/VGL/ModelFour.java
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
 * @author Naing Naing Maw
 * @version 1.0 $Id: ModelFour.java,v 1.1 2005-06-01 13:30:44 brian Exp $
 */

public class ModelFour extends ModelEven implements Model {

	/**
	 * Constructor.
	 */
	public ModelFour() {
		super();
	}

	/**
	 * Constructor for reopening the file.
	 * 
	 * @param root
	 *            the model information in JDom Element object
	 */
	public ModelFour(Element root) {
		super(root);
	}

	/**
	 * Constructor for testing purpose.
	 */
	public ModelFour(String c, String he, String ho1, String ho2) {
		super(c, he, ho1, ho2);
	}

	/**
	 * Create the field population cage and return it.
	 * 
	 * @return the field population cage
	 */
	public Cage populate() {
		Cage c = new Cage(0);

		// get the random number for amount of organism, 20 - 40
		int count = random.nextInt(20); // 0 - 19
		count = count + 20;

		for (int i = 0; i < count; i++) {
			int num = random.nextInt(100); // (AA, BB, AB) or (AO, BO)
			int num1 = random.nextInt(100); // first allele

			if (num < 50) { // (AA, BB, AB)
				if ((num1 >= 0) && (num1 < 33))
					c.addOrganism(new Organism(1, homo1, homo1, homo1, 4),
							homo1);
				else if ((num1 >= 33) && (num1 < 66))
					c.addOrganism(new Organism(1, homo1, homo2, hete, 4), hete);
				else
					c.addOrganism(new Organism(1, homo2, homo2, homo2, 4),
							homo2);
			} else { // (AO, BO)
				if (num1 < 50)
					c.addOrganism(new Organism(0, homo1, homo1, 4), homo1);
				else
					c.addOrganism(new Organism(0, homo2, homo2, 4), homo2);
			}
		}

		return c;
	}

	/**
	 * Cross the given two organisms and return the offspring cage.
	 * 
	 * @param id
	 *            cage's id
	 * @param o1
	 *            organism to be crossed
	 * @param o2
	 *            organism to be crossed
	 * @return the offspring cage
	 */
	public Cage crossTwo(int id, Organism o1, Organism o2) {
		if (o1.getSexType() == o2.getSexType()) // same sex error
			return null;

		Cage c = new Cage(id, o1, o2);

		// get the random number for amount of organism, 20 - 40
		int count = random.nextInt(20); // 0 - 19
		count = count + 20;

		String genotype1 = null;
		String genotype2 = null;
		for (int i = 0; i < count; i++) {
			int num = random.nextInt(100);
			if (num < 50)
				genotype1 = o1.getAllele1();
			else
				genotype1 = o1.getAllele2();

			num = random.nextInt(100);
			if (num < 50)
				genotype2 = o2.getAllele1();
			else
				genotype2 = o2.getAllele2();

			if ((genotype1 == null) && (genotype2 != null)) // O*
				c.addOrganism(new Organism(0, genotype2, genotype2, 4),
						genotype2);
			else if ((genotype1 != null) && (genotype2 == null)) // *O
				c.addOrganism(new Organism(0, genotype1, genotype1, 4),
						genotype1);
			else { // **
				if (genotype1.equals(genotype2))
					c.addOrganism(new Organism(1, genotype1, genotype2,
							genotype1, 4), genotype1);
				else
					c.addOrganism(
							new Organism(1, genotype1, genotype2, hete, 4),
							hete);
			}
		}

		return c;
	}

	/**
	 * Return the model name.
	 * 
	 * @return the model name
	 */
	public String getModelName() {
		return "model 4: Sex-linked (XX = Female; XY = Male); Incomplete Dominance";
	}

	/**
	 * Return the model number.
	 * 
	 * @return the model number
	 */
	public int getModelNo() {
		return 4;
	}
}