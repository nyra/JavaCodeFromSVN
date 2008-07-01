package GeneticModels;

import java.util.ArrayList;

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
 * @version 1.0 $Id: NumberTraitSet.java,v 1.5 2008-07-01 02:05:53 brian Exp $
 */

public class NumberTraitSet extends TraitSet {
	
	public NumberTraitSet(String bodyPart) {
		traits = new ArrayList<Trait>();
		traits.add(new NumberTrait("No", bodyPart));
		traits.add(new NumberTrait("One", bodyPart));
		traits.add(new NumberTrait("Two", bodyPart));
		traits.add(new NumberTrait("Three", bodyPart));
		traits.add(new NumberTrait("Four", bodyPart));
		traits.add(new NumberTrait("Five", bodyPart));
		traits.add(new NumberTrait("Six", bodyPart));
	}

}
