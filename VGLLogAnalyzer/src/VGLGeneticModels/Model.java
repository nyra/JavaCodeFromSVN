package VGLGeneticModels;

import org.jdom.Element;

import LogAnalyzer.RequiredResultSet;

/**
 * Naing Naing Maw cs681-3 Fall 2002 - Spring 2003 Project VGL File:
 * ~nnmaw/VGL/Model.java
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
 * @version 1.0 $Id: Model.java,v 1.1 2004-09-24 15:30:16 brian Exp $
 */

public interface Model {
	Cage populate();

	Cage crossTwo(int id, Organism o1, Organism o2);

	String getCharacter();

	public Element save() throws Exception;

	public String getModelInfo();

	public int getModelNo();
	
	public RequiredResultSet getRequiredResultSet();
}