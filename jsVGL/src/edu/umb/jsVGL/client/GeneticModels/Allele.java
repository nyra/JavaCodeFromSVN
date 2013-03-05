package edu.umb.jsVGL.client.GeneticModels;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;


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
 * @version 1.0 $Id: Allele.java,v 1.12 2008-06-19 20:07:45 brian Exp $
 */

public class Allele {
		
	private int intVal;
	private Trait trait;
	
	public Allele(Trait trait, int intVal) {
		this.trait = trait;
		this.intVal = intVal;
	}
	
	public Trait getTrait() {
		return trait;
	}

	public int getIntVal() {
		return intVal;
	}
	
	public String toString() {
		return "#" + intVal + " " + trait.toString();
	}
	
	public Element save(int index) throws Exception {
		Document d = XMLParser.createDocument();
		Element e = d.createElement("Allele");
		e.setAttribute("GeneIndex", String.valueOf(index));
		e.setAttribute("TraitNumber", String.valueOf(intVal));
		return e;
	}
}