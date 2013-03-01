package edu.umb.bio.genex.Requirements;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.Element;

import edu.umb.bio.genex.client.Problems.GenexState;

public class IntronNumberRequirement extends Requirement {
	
	private int desiredNumber;
	
	public IntronNumberRequirement(List<Element> elList) {
		Iterator<Element> elIt = elList.iterator();
		while (elIt.hasNext()) {
			Element e = elIt.next();
			if (e.getName().equals("Number")) desiredNumber = Integer.parseInt(e.getTextTrim());
			if (e.getName().equals("FailString")) failureString = e.getTextTrim();
		}

	}

	public boolean isSatisfied(GenexState state) {
		return (state.getNumberOfExons() == desiredNumber + 1);
	}

}
