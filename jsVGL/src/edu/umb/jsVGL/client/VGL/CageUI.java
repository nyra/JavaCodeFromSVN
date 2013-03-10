package edu.umb.jsVGL.client.VGL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.umb.jsVGL.client.GeneticModels.Cage;
import edu.umb.jsVGL.client.GeneticModels.Organism;
import edu.umb.jsVGL.client.GeneticModels.OrganismList;
import edu.umb.jsVGL.client.GeneticModels.Phenotype;
import edu.umb.jsVGL.client.VGL.UIimages.UIImageResource;

/**
 * Nikunj Koolar cs681-3 Fall 2002 - Spring 2003 Project VGL File:
 * Brian White 2008
 * CustomizedFileFilter.java - Instances of this class provide for file filters
 * to show only those file that are supported by the application.
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
 * @author Nikunj Koolar & Brian White
 * @version 1.0 $Id$
 */
public class CageUI extends DialogBox implements Comparable<CageUI> {

	private static String FIELD_POP_COLOR = "#2E8B57";
	private static String PARENT_COLOR = "#8E2323";
	private static String OFFSPRING_COLOR = "#0x007FFF";

	/**
	 * sets an upper bound so the cages (esp the Super Cross)
	 *  don't get too big
	 */
	private static int absoluteMaxOrgsPerRow = 20;

	/**
	 * boolean to indicate membership in selected set for
	 * summary chart
	 */
	private boolean isSelected;

	/**
	 * boolean to indicate if this is a superCross
	 *  (and thus requires special layout
	 */
	private boolean isSuperCross;

	/**
	 * manager for membership in selected set for summary chart
	 */
	private SummaryChartManager summaryChartManager;

	/**
	 * The Id for the Cage. This value is always one more than the id of the
	 * cage it holds. This is because the Cage id's begin from 0, but on screen
	 * they have to be shown beginning from 1.
	 */
	private int id;

	/**
	 * number of traits in this problem
	 */
	private int numberOfTraits;

	/**
	 * maximum number of organisms in any row
	 * it assumes 2 rows of orgs
	 */
	private int maxOrgsInOneRow;

	/**
	 * Parameter to the set the width of the dialog
	 */
	private int dialogWidth;

	/**
	 * Parameter to the set the height of the dialog
	 */
	private int dialogHeight ;

	/**
	 * Parameter to the set the X-coordinate of the dialog
	 */
	private int dialogLocationX;

	/**
	 * Parameter to the set the Y-coordinate of the dialog
	 */
	private int dialogLocationY;

	/**
	 * The panel that contains all the subpanels.
	 */
	private DockPanel superPanel;

	/**
	 * This variable stores the count of the number of different phenotypes
	 * associated with this cage
	 */
	private int numPhenosPresent;

	/**
	 * image resource
	 */
	private UIImageResource uiImageResource;

	/**
	 * container for the details of the genetic model, if shown
	 */
	private HTML textDetails;

	/**
	 * This variable stores a reference to the hashmap of children associated
	 * with this cage
	 * sorted by phenotypeString
	 */
	private TreeMap<String, OrganismList> children;

	/**
	 * This stores a more easily accessible version of children
	 * it is an array of OrganismLists, each holding all the kids with 
	 * the same phenotype. It is indexed by a number for the phenotype
	 */
	private OrganismList[] childrenSortedByPhenotype;

	/**
	 * This variable stores a reference to the list of parents associated with
	 * this cage
	 */
	private ArrayList<Organism> parents;

	/**
	 * holds array mapping real trait # to display order number
	 * that way, the traits aren't displayed in chromosomal order
	 */
	private int[] scrambledTraitOrder;

	/**
	 * A reference to the Cage object being displayed through this UI
	 */
	private Cage cage;

	/**
	 * A reference to the selectionvial object that keeps track of the currently
	 * selected male and female organisms for crossing.
	 */
	private SelectionVial vial;

	/**
	 * This variable stores the details about the Genetic Model currently being
	 * used.
	 */
	private String details = null;

	/**
	 * This variable is used to decide the following: a. Whether to display the
	 * Genetics Model details in cage 1 b. Whether to allow the individual
	 * organisms to display allele information in balloon help active mode.
	 */
	private boolean isBeginner = false;

	/**
	 * Array of Parent Organisms. This array was initially concieved to hold
	 * simply the images of the parents (which explains its naming) but later
	 * on, in order to support linking between parents and their corresponding
	 * objects in the original cages where they were present, this array was
	 * then used to store parent OrganismUI objects.
	 */
	private OrganismUI[] parentOrganismUIs;

	/**
	 * This array of arrays stores the organismUIs for all the organisms of
	 * all the phenotypes associated with this cage. 
	 * it is stored in 2 rows where the length of the rows is
	 * (maximum number of offspring for one pheno)/2 
	 * For eg. If the pheno with the most offspring has 40 in it
	 * and there is one pheno then this variable will be [2][20] in size. If the
	 * cage contains 2 phenotypes then this variable will be [4][20] in size.
	 * - there is probably some danger with really high numbers of offspring
	 */
	private OrganismUI[][] childrenOrganismUIs;

	/**
	 * The constructor
	 * 
	 * @param importFrame
	 *            the reference frame for the dialog
	 * @param isbeginnersmode
	 *            true if user is allowed to view underlying genetics details,
	 *            false otherwise
	 * @param cage
	 *            reference to the Cage object
	 * @param sv
	 *            reference to the SelectionVial object
	 * @param details
	 *            string containing information about the underlying genetics
	 *            model
	 */
	public CageUI(boolean isbeginnersmode, 
			boolean isSuperCross,
			Cage cage,
			SelectionVial sv, 
			String details, 
			int numberOfTraits,
			int[] scrambledTraitOrder) {

		//initialize parent
		super(false);

		uiImageResource = GWT.create(UIImageResource.class);

		this.isBeginner = isbeginnersmode;
		this.isSuperCross = isSuperCross;
		this.cage = cage;
		vial = sv;

		id = cage.getId() + 1;
		children = cage.getChildren();
		parents = cage.getParents();

		this.scrambledTraitOrder = scrambledTraitOrder;

		if (id == 1)
			if (details != null)
				this.details = details;

		this.numberOfTraits = numberOfTraits;

		setTitle("Cage " + id);

		setupSubComponents();
		setupDialogBox(numPhenosPresent);

		isSelected = false;
		summaryChartManager = SummaryChartManager.getInstance();

		maxOrgsInOneRow = (cage.getMaxOrgListSize()/2) + 1;  // assumes 2 rows of orgs
		// add 1 in case rounding

		//setup the GUI of its internal components
		components();
		show();
	}

	/**
	 * This method initializes the objects and widgets that store information
	 * about the various phenotypes associated with the cage.
	 */
	private void setupSubComponents() {
		// do this all by the number of phenotypes present in the chidren
		Set<String> phenotypeStrings = children.keySet();
		Iterator<String> it1 = phenotypeStrings.iterator();
		numPhenosPresent = phenotypeStrings.size();

		String[] phenotypeNames = new String[numPhenosPresent];
		childrenSortedByPhenotype = new OrganismList[numPhenosPresent];
		
		int i = 0;
		while (it1.hasNext()) {
			phenotypeNames[i] = new String(it1.next());
			childrenSortedByPhenotype[i] = children.get(phenotypeNames[i]);
			i++;
		}
	}

	/**
	 * This method sets up the GUI and other characteristics of the internals of
	 * the Cage
	 */
	private void components() {
		setupOrganismPanel();
		setupParentInfoPanel();
		add(superPanel);
	}

	/**
	 * This method sets up the extents and position for the Cage
	 */
	private void setupDialogBox(int panelCount) {
		int dtHeight = (int) (Window.getClientHeight());
		int dtWidth = (int) (Window.getClientWidth());
		int ht = (int) (768 / 5.6);
		dialogHeight = ht + (int) ((panelCount - 1) * ht)/3;
		dialogWidth = 550;
		dialogLocationX = (int) (dtWidth / 2) - dialogWidth / 2;
		dialogLocationY = (int) (dtHeight / 2) - dialogHeight / 2;
		setWidth(dialogWidth + "px");
		setHeight(dialogHeight + "px");
		setPopupPosition(dialogLocationX, dialogLocationY);
	}

	/**
	 * This method sets up the panels for the Cage
	 */
	private void setupOrganismPanel() {

		superPanel = new DockPanel();
		DockPanel detailsPanel = new DockPanel();
		CaptionPanel captionedDetailsPanel = null;
		if (id > 1) {
			captionedDetailsPanel = new CaptionPanel("Offspring");
			captionedDetailsPanel.add(detailsPanel);
		} else {
			captionedDetailsPanel = new CaptionPanel("Field Population");
			captionedDetailsPanel.add(detailsPanel);
		}
		FlowPanel individualPanel = new FlowPanel();

		CaptionPanel captionedOrganismPanel = new CaptionPanel("Organisms");
		VerticalPanel organismsPanel = new VerticalPanel();
		organismsPanel.setWidth("100px");
		captionedOrganismPanel.add(organismsPanel);

		CaptionPanel captionedCountsPanel = new CaptionPanel("Counts");
		VerticalPanel countsPanel = new VerticalPanel();
		countsPanel.setWidth("50px");
		captionedCountsPanel.add(countsPanel);

		// headers for the different traits
		CaptionPanel[] captionedTraitPanels = new CaptionPanel[numberOfTraits];
		VerticalPanel[] traitPanels = new VerticalPanel[numberOfTraits];

		// need to get the type of each trait
		//  get one organism's pheno (it doesn't matter which one)
		ArrayList<Phenotype> phenotypes = 
				childrenSortedByPhenotype[0].get(0).getPhenotypes();
		for (int i = 0; i < numberOfTraits; i++) {
			traitPanels[i] = new VerticalPanel();
			traitPanels[i].setWidth("70px");
			captionedTraitPanels[i] = new CaptionPanel(phenotypes.get(scrambledTraitOrder[i]).getTrait().getBodyPart());
			captionedTraitPanels[i].add(traitPanels[i]);
		}

		if (isSuperCross) {
			childrenOrganismUIs = new OrganismUI[2 * numPhenosPresent][absoluteMaxOrgsPerRow];
		} else {
			childrenOrganismUIs = new OrganismUI[2 * numPhenosPresent][maxOrgsInOneRow];
		}

		//For each phenotype, setup its own panels for organismUIs,count and
		//pictures and add them to the right places in the organismpanel,
		// countspanel, phenotype panels
		//and the picturespanel
		for (int i = 0; i < numPhenosPresent; i++) {
			IndividualPanelSet panelSet = setupIndividualPanel(i);
			organismsPanel.add(panelSet.getOrganismPanel());
			countsPanel.add(panelSet.getCountsPanel());
			HorizontalPanel[] phenoPanels = panelSet.getPhenotypePanels();
			for (int j = 0; j < numberOfTraits; j++) {
				traitPanels[j].add(
						phenoPanels[scrambledTraitOrder[j]]);
			}
		}

		for (int i = 0; i < numberOfTraits; i++) {
			individualPanel.add(captionedTraitPanels[i]);
		}

		individualPanel.add(captionedOrganismPanel);

		individualPanel.add(captionedCountsPanel);

		detailsPanel.add(individualPanel, DockPanel.NORTH);
		superPanel.add(detailsPanel, DockPanel.SOUTH);
	}

	/**
	 * This method returns a JPanel containing the OrganismUIs for each phenotype
	 * associated with this cage.
	 * 
	 * @param number
	 *            index of the phenotype in the list of phenotypes for which the
	 *            panels are being set up.
	 */
	private IndividualPanelSet setupIndividualPanel(int number) {

		HorizontalPanel topRowOfOrganismsPanel = new HorizontalPanel();
		HorizontalPanel bottomRowOfOrganismsPanel = new HorizontalPanel();

		OrganismUI[] topRowOfOrganismUIs = childrenOrganismUIs[2 * number];
		OrganismUI[] bottomRowOFOrganismUIs = childrenOrganismUIs[2 * number + 1];
		Iterator<Organism> it = childrenSortedByPhenotype[number].iterator();

		//lay out two neat rows of OrganismUIs
		if (isSuperCross) {
			// if super cross, need a row of males and a row of females
			int i = 0;
			while (it.hasNext() && (i < (2 * absoluteMaxOrgsPerRow))) {
				Organism o = (Organism) it.next();
				// first, a row of males (or females, if there are no males)
				if (i < absoluteMaxOrgsPerRow) {
					topRowOfOrganismUIs[i] = new OrganismUI(o, false, isBeginner, vial);
					topRowOfOrganismsPanel.add(topRowOfOrganismUIs[i]);
					i++;
				} else {
					// second row:
					// then see if there are any females
					//   if so, then make a row of females
					//   if not, make another row of males
					if ((childrenSortedByPhenotype[number].getNumberOfFemales() > 0) 
							&& (it.hasNext())) {
						// run thru the remaining males
						while (((Organism)it.next()).getSexString().equals("Male")) {}
					} 
					bottomRowOFOrganismUIs[i - absoluteMaxOrgsPerRow] = 
							new OrganismUI(o, false, isBeginner, vial);
					bottomRowOfOrganismsPanel.add(
							bottomRowOFOrganismUIs[i - absoluteMaxOrgsPerRow]);
					i++;
				}
			}
		} else {
			int count = 0;
			int i = 0;
			int j = 0;
			while (it.hasNext()) {
				Organism o1 = (Organism) it.next();
				count++;
				if (count <= maxOrgsInOneRow) {
					topRowOfOrganismUIs[i] = new OrganismUI(o1, false, isBeginner,
							vial);
					topRowOfOrganismsPanel.add(topRowOfOrganismUIs[i]);
					i++;
				} else {
					bottomRowOFOrganismUIs[j] = new OrganismUI(o1, false, isBeginner,
							vial);
					bottomRowOfOrganismsPanel.add(bottomRowOFOrganismUIs[j]);
					j++;
				}
			}
			SimplePanel filler = new SimplePanel();
			filler.setWidth("15px");
			filler.setHeight("15px");
			if (i < maxOrgsInOneRow) {
				while (i < maxOrgsInOneRow) {
					topRowOfOrganismsPanel.add(filler);
					i++;
				}
			}
			if (j < maxOrgsInOneRow) {
				while (j < maxOrgsInOneRow) {
					bottomRowOfOrganismsPanel.add(filler);
					j++;
				}
			}
		}
		VerticalPanel organismPanel = new VerticalPanel();
		organismPanel.add(topRowOfOrganismsPanel);
		organismPanel.add(bottomRowOfOrganismsPanel);

		VerticalPanel countPanel = new VerticalPanel();

		SimplePanel maleLabel = new SimplePanel();
		maleLabel.add(new HTML("<img src=\"" + (new Image(uiImageResource.maleBlack())).getUrl() + "\">"));
		SimplePanel femaleLabel = new SimplePanel();
		femaleLabel.add(new HTML("<img src=\"" + (new Image(uiImageResource.femaleBlack())).getUrl() + "\">"));

		String mCount = (new Integer(childrenSortedByPhenotype[number].getNumberOfMales())).toString();
		if (childrenSortedByPhenotype[number].getNumberOfMales() < 10)
			mCount = "0" + mCount;
		Label maleCountLabel = new Label(mCount);

		String fCount = (new Integer(childrenSortedByPhenotype[number].getNumberOfFemales())).toString();
		if (childrenSortedByPhenotype[number].getNumberOfFemales() < 10)
			fCount = "0" + fCount;
		Label femaleCountLabel = new Label(fCount);

		if (isSuperCross) {
			maleCountLabel.setWidth("35px");
			maleCountLabel.setHeight("15px");
			femaleCountLabel.setWidth("35px");
			femaleCountLabel.setHeight("15px");
		} else {
			maleCountLabel.setWidth("25px");
			maleCountLabel.setHeight("15px");
			femaleCountLabel.setWidth("25px");
			femaleCountLabel.setHeight("15px");
		}

		FlowPanel malePanel = new FlowPanel();
		FlowPanel femalePanel = new FlowPanel();
		malePanel.add(maleCountLabel);
		malePanel.add(maleLabel);
		femalePanel.add(femaleCountLabel);
		femalePanel.add(femaleLabel);
		countPanel.add(malePanel);
		countPanel.add(femalePanel);

		HorizontalPanel[] phenotypePanels = new HorizontalPanel[numberOfTraits];
		ArrayList<Phenotype> phenoList = 
				childrenSortedByPhenotype[number].get(0).getPhenotypes();
		for (int k = 0; k < numberOfTraits; k++) {
			phenotypePanels[k] = new HorizontalPanel();
			phenotypePanels[k].add(new Label(phenoList.get(k).getTrait().getTraitName()));
			phenotypePanels[k].setHeight("34px");
		}

		return new IndividualPanelSet(organismPanel,
				countPanel,
				phenotypePanels);
	}

	/**
	 * This method sets up the Panel that display the information about the
	 * parents or if the Cage id is 1 and beginner's mode is true then it
	 * displays the details about the underlying genetics model
	 */
	private void setupParentInfoPanel() {
		CaptionPanel captionedParentInfoPanel = new CaptionPanel("Parents");
		if (id > 1) {
			FlowPanel parentInfoPanel = new FlowPanel();
			parentOrganismUIs = new OrganismUI[2];
			Organism o1 = parents.get(0);
			Organism o2 = parents.get(1);
			int cageId = o1.getCageId() + 1;
			String phenoName1 = o1.getPhenotypeString();
			parentOrganismUIs[0] = new OrganismUI(o1, true, isBeginner, vial);
			parentInfoPanel.add(parentOrganismUIs[0]);
			parentInfoPanel.add(new Label("(Cage " + cageId + ") " + phenoName1));
			parentInfoPanel.add(new Label(" X "));
			cageId = o2.getCageId() + 1;
			String phenoName2 = o2.getPhenotypeString();
			parentOrganismUIs[1] = new OrganismUI(o2, true, isBeginner, vial);
			parentInfoPanel.add(parentOrganismUIs[1]);
			parentInfoPanel.add(new Label("(Cage " + cageId + ") " + phenoName2));
		} else {
			if (isBeginner) {
				if (details != null) {
					HTML textDetails = new HTML(details);
					final ToggleButton showHideDetails = new ToggleButton("Show Genetic Model", "Hide Genetic Model");
					ScrollPanel detailsScrollPanel = new ScrollPanel(textDetails);
					showHideDetails.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							if (showHideDetails.isDown()) {
								showDetails();
							} else {
								hideDetails();
							}
						}
					});
					DockPanel showHidePanel = new DockPanel();
					showHidePanel.add(showHideDetails, DockPanel.NORTH);
					captionedParentInfoPanel.add(showHidePanel);
				}
			}
		}
		superPanel.add(captionedParentInfoPanel, DockPanel.NORTH);
	}

	/**
	 * This method sets up the Panel that displays the genetics details when the
	 * toggle button is pressed
	 */
	private void showDetails() {
		textDetails.setHTML(details);
	}

	/**
	 * This method hides the panel thats has the genetics information
	 */
	private void hideDetails() {
		textDetails.setHTML("");
	}


	public int getId() {
		return id;
	}

	public boolean isSuperCross() {
		return isSuperCross;
	}
	
	public void setIsSelected(boolean selected) {
		isSelected = selected;
	}
	
	public boolean getIsSelected() {
		return isSelected;
	}

	/**
	 * Getter method to access the Cage object associated with this UI
	 * 
	 * @return the Cage object
	 */
	public Cage getCage() {
		return cage;
	}

	/**
	 * Getter method to access the OrganismUIs for the parents for the cage of
	 * this UI
	 * 
	 * @return the array containing the OrganismUIs of the parents
	 */
	public OrganismUI[] getParentUIs() {
		if (parentOrganismUIs != null) {
			if (parentOrganismUIs[0] != null && parentOrganismUIs[1] != null)
				return parentOrganismUIs;
			else
				return null;
		} else
			return null;
	}

	/**
	 * This method returns the OrganismUI of the Organism with the sent id.
	 * 
	 * @param id
	 *            The index of the organism
	 * @return the OrganismUI of the organism
	 */
	public OrganismUI getOrganismUIFor(int id) {

		int orgsPerRow = maxOrgsInOneRow;
		if (isSuperCross) orgsPerRow = absoluteMaxOrgsPerRow;

		for (int i = 0; i < 2 * numPhenosPresent; i++) {
			for (int j = 0; j < orgsPerRow; j++) {
				OrganismUI organismUI = ((OrganismUI) (childrenOrganismUIs[i][j]));
				if (organismUI != null) {
					if (organismUI.getOrganism().getId() == id)
						return organismUI;
				} else
					break;
			}
		}
		return null;
	}

	public int compareTo(CageUI o) {
		return id - o.getId();
	}

}