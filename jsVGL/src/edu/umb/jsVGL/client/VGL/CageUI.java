package edu.umb.jsVGL.client.VGL;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.google.gwt.user.client.ui.DialogBox;

import edu.umb.jsVGL.client.GeneticModels.Cage;
import edu.umb.jsVGL.client.GeneticModels.Organism;
import edu.umb.jsVGL.client.GeneticModels.OrganismList;
import edu.umb.jsVGL.client.GeneticModels.Phenotype;
import edu.umb.jsVGL.client.PhenotypeImages.PhenotypeImageBank;

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
	 * boolean to indicarte if this is a superCross
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
	 * This panel holds the entire list of Organism UIs for the Cage
	 */
	private JPanel organismsPanel;

	/**
	 * This panel holds the numeric count of each sex type thats currently
	 * present in the cage
	 */
	private JPanel countsPanel;

	/**
	 * this is an array of panels, one for each trait
	 */
	private JPanel[] traitPanels;

	/**
	 * This panel holds the Image for each phenotype associated with the
	 * organisms in the cage
	 */
	private JPanel picturesPanel;

	/**
	 * This panel holds the set of Organisms,their Counts and their pictures for
	 * each phenotype.
	 */
	private JPanel individualPanel;

	/**
	 * The panel that contains all the subpanels.
	 */
	private JPanel superPanel;

	/**
	 * This panel holds the Individual Panel
	 */
	private JPanel detailsPanel;

	/**
	 * This panel contains the info about the parents of the organisms of this
	 * cage, or if the cage is the initial population cage then it holds the
	 * information about the Genetic Model currently in use
	 */
	private JPanel parentInfoPanel;

	/**
	 * This variable stores the count of the number of different phenotypes
	 * associated with this cage
	 */
	private int numPhenosPresent;

	/**
	 * This variable keeps track of the Icon Image associated with the frame
	 * holding this cage
	 */
	private Image image;

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
	 * This widget displays the informations stored in the details variable.
	 */
	private JEditorPane textDetails;

	/**
	 * this is the scrollpane for the details
	 */
	private JScrollPane detailsScrollPane;

	/**
	 * This is the button used to show/hide the Genetics information.
	 */
	private JToggleButton showHideDetails;

	/**
	 * This string holds the heading to be displayed on the toggle button when
	 * the Genetics Details are being shown
	 */
	private String hideDetails = Messages.getInstance().getString("VGLII.HideGeneticModel");

	/**
	 * This string holds the heading to be displayed on the toggle button when
	 * the Genetics Details are not being shown
	 */
	private String showDetails = Messages.getInstance().getString("VGLII.ShowGeneticModel");

	/**
	 * This icon is displayed in the toggle button when the Genetics Details are
	 * not being shown
	 */
	private URL closeURL = CageUI.class.getResource("images/close.gif");

	private ImageIcon closeIcon = new ImageIcon(closeURL);

	/**
	 * This icon is displayed in the toggle button when the Genetics Details are
	 * being shown.
	 */
	private URL downURL = CageUI.class.getResource("images/down.gif");

	private ImageIcon downIcon = new ImageIcon(downURL);

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
	}

	/**
	 * This method sets up the GUI and other characteristics of the internals of
	 * the Cage
	 */
	private void components() {
		setupOrganismPanel();
		setupParentInfoPanel();
		setContentPane(superPanel);
	}

	/**
	 * This method sets up the extents and position for the Cage
	 */
	private void setupDialogBox(int panelCount) {
		int dtHeight = (int) (getGraphicsConfiguration().getDevice()
				.getDefaultConfiguration().getBounds().getHeight());
		int dtWidth = (int) (getGraphicsConfiguration().getDevice()
				.getDefaultConfiguration().getBounds().getWidth());
		int ht = (int) (768 / 5.6);
		dialogHeight = ht + (int) ((panelCount - 1) * ht)/3;
		dialogWidth = 550;
		dialogLocationX = (int) (dtWidth / 2) - dialogWidth / 2;
		dialogLocationY = (int) (dtHeight / 2) - dialogHeight / 2;
		setSize(new Dimension(dialogWidth, dialogHeight));
		setLocation(new Point(dialogLocationX, dialogLocationY));
	}

	/**
	 * This method sets up the panels for the Cage
	 */
	private void setupOrganismPanel() {

		Border emptyBorder = BorderFactory.createEmptyBorder(0, 0, 0, 0);
		BorderLayout bSelectionLayout = new BorderLayout();
		superPanel = new JPanel();
		superPanel.setLayout(bSelectionLayout);
		superPanel.setBorder(BorderFactory.createLineBorder(unselectedColor, 2));
		detailsPanel = new JPanel();
		detailsPanel.setLayout(new BorderLayout());
		if (id > 1) {
			TitledBorder border = BorderFactory.createTitledBorder(
					Messages.getInstance().getString("VGLII.Offspring"));
			border.setTitleColor(OFFSPRING_COLOR);
			border.setBorder(BorderFactory.createLineBorder(OFFSPRING_COLOR));
			detailsPanel.setBorder(border);
		} else {
			TitledBorder border = BorderFactory.createTitledBorder(
					Messages.getInstance().getString("VGLII.FieldPop"));
			border.setTitleColor(FIELD_POP_COLOR);
			border.setBorder(BorderFactory.createLineBorder(FIELD_POP_COLOR));
			detailsPanel.setBorder(border);
		}
		individualPanel = new JPanel();
		FlowLayout fl = new FlowLayout();
		fl.setHgap(1);
		fl.setVgap(1);
		individualPanel.setLayout(fl);

		JPanel organismsPanelWrapper = new JPanel();
		organismsPanelWrapper.setLayout(
				new BoxLayout(organismsPanelWrapper, BoxLayout.Y_AXIS));
		organismsPanelWrapper.add(Box.createRigidArea(new Dimension(100,1)));
		organismsPanel = new JPanel();
		organismsPanel.setLayout(new GridLayout(numPhenosPresent, 1));
		organismsPanel.setBorder(BorderFactory.createTitledBorder(
				emptyBorder, Messages.getInstance().getString("VGLII.Organisms"),
				javax.swing.border.TitledBorder.CENTER,
				javax.swing.border.TitledBorder.ABOVE_TOP));

		JPanel countsPanelWrapper = new JPanel();
		countsPanelWrapper.setLayout(
				new BoxLayout(countsPanelWrapper, BoxLayout.Y_AXIS));
		countsPanel = new JPanel();
		countsPanel.setLayout(new GridLayout(numPhenosPresent, 1));
		countsPanel.setBorder(BorderFactory.createTitledBorder(emptyBorder,
				Messages.getInstance().getString("VGLII.Count"), 
				javax.swing.border.TitledBorder.CENTER,
				javax.swing.border.TitledBorder.ABOVE_TOP));
		countsPanelWrapper.add(Box.createRigidArea(new Dimension(
				countsPanel.getFontMetrics(
						countsPanel.getFont()).stringWidth(
								Messages.getInstance().getString("VGLII.Count")) + 5 ,1)));

		// headers for the different traits
		traitPanels = new JPanel[numberOfTraits];
		JPanel[] traitPanelWrappers = new JPanel[numberOfTraits];

		// need to get the type of each trait
		//  get one organism's pheno (it doesn't matter which one)
		ArrayList<Phenotype> phenotypes = 
			childrenSortedByPhenotype[0].get(0).getPhenotypes();
		for (int i = 0; i < numberOfTraits; i++) {
			traitPanels[i] = new JPanel();
			traitPanels[i].setLayout(new GridLayout(numPhenosPresent, 1));
			traitPanels[i].setBorder(BorderFactory.createTitledBorder(
					emptyBorder,
					Messages.getInstance().getString("VGLII." +
							phenotypes.get(scrambledTraitOrder[i]).getTrait().getBodyPart()), 
							javax.swing.border.TitledBorder.CENTER,
							javax.swing.border.TitledBorder.ABOVE_TOP));
			traitPanelWrappers[i] = new JPanel();
			traitPanelWrappers[i].setLayout(
					new BoxLayout(traitPanelWrappers[i], BoxLayout.Y_AXIS));
			traitPanelWrappers[i].add(Box.createHorizontalStrut(70));
			traitPanelWrappers[i].add(traitPanels[i]);
		}

		picturesPanel = new JPanel();
		picturesPanel.setLayout(new GridLayout(numPhenosPresent, 1));
		picturesPanel.setBorder(BorderFactory.createTitledBorder(emptyBorder,
				Messages.getInstance().getString("VGLII.Images"), javax.swing.border.TitledBorder.CENTER,
				javax.swing.border.TitledBorder.ABOVE_TOP));

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
			JPanel[] phenoPanels = panelSet.getPhenotypePanels();
			for (int j = 0; j < numberOfTraits; j++) {
				traitPanels[j].add(
						phenoPanels[scrambledTraitOrder[j]]);
			}
			picturesPanel.add(showPhenotypeButtons[i]);
		}

		for (int i = 0; i < numberOfTraits; i++) {
			individualPanel.add(traitPanelWrappers[i]);
		}

		organismsPanelWrapper.add(organismsPanel);
		individualPanel.add(organismsPanelWrapper);

		countsPanelWrapper.add(countsPanel);
		individualPanel.add(countsPanelWrapper);

		individualPanel.add(picturesPanel);
		detailsPanel.add(individualPanel, BorderLayout.NORTH);
		superPanel.add(detailsPanel, BorderLayout.SOUTH);
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

		Border etched = BorderFactory.createEtchedBorder();

		JPanel topRowOfOrganismsPanel = new JPanel();
		JPanel bottomRowOfOrganismsPanel = new JPanel();

		GridLayout gridlt = null;
		if (isSuperCross) {
			gridlt = new GridLayout(1, absoluteMaxOrgsPerRow);
		} else {
			gridlt = new GridLayout(1, maxOrgsInOneRow);
		}
		gridlt.setHgap(1);
		gridlt.setVgap(2);
		topRowOfOrganismsPanel.setLayout(gridlt);
		bottomRowOfOrganismsPanel.setLayout(gridlt);

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
						while (((Organism)it.next()).getSexString().equals(
								Messages.getInstance().getString("VGLII.Male"))) {}
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
			if (i < maxOrgsInOneRow) {
				while (i < maxOrgsInOneRow) {
					topRowOfOrganismsPanel.add(Box.createRigidArea(new Dimension(15, 15)));
					i++;
				}
			}
			if (j < maxOrgsInOneRow) {
				while (j < maxOrgsInOneRow) {
					bottomRowOfOrganismsPanel.add(Box.createRigidArea(new Dimension(15, 15)));
					j++;
				}
			}
		}
		JPanel organismPanel = new JPanel();
		GridLayout gl = new GridLayout(2, 0);
		gl.setVgap(4);
		organismPanel.setLayout(gl);
		organismPanel.setBorder(etched);
		organismPanel.add(topRowOfOrganismsPanel);
		organismPanel.add(bottomRowOfOrganismsPanel);

		JPanel countPanel = new JPanel();
		GridLayout gl1 = new GridLayout(2, 0);
		gl1.setVgap(0);
		countPanel.setLayout(gl1);
		countPanel.setBorder(etched);
		URL maleLabelURL = CageUI.class.getResource("UIimages/maleblack.gif");
		JLabel maleLabel = new JLabel(new ImageIcon(maleLabelURL));
		URL femaleLabelURL = CageUI.class
		.getResource("UIimages/femaleblack.gif");
		JLabel femaleLabel = new JLabel(new ImageIcon(femaleLabelURL));
		String mCount = (new Integer(childrenSortedByPhenotype[number].getNumberOfMales())).toString();
		if (childrenSortedByPhenotype[number].getNumberOfMales() < 10)
			mCount = "0" + mCount;
		JLabel maleCountLabel = new JLabel(mCount);
		String fCount = (new Integer(childrenSortedByPhenotype[number].getNumberOfFemales()))
		.toString();
		if (childrenSortedByPhenotype[number].getNumberOfFemales() < 10)
			fCount = "0" + fCount;
		JLabel femaleCountLabel = new JLabel(fCount);
		if (isSuperCross) {
			maleCountLabel.setPreferredSize(new Dimension(35, 15));
			femaleCountLabel.setPreferredSize(new Dimension(35, 15));
		} else {
			maleCountLabel.setPreferredSize(new Dimension(25, 15));
			femaleCountLabel.setPreferredSize(new Dimension(25, 15));
		}
		maleCountLabel.setHorizontalTextPosition(javax.swing.JLabel.RIGHT);
		maleCountLabel.setHorizontalAlignment(javax.swing.JLabel.RIGHT);
		femaleCountLabel.setHorizontalTextPosition(javax.swing.JLabel.RIGHT);
		femaleCountLabel.setHorizontalAlignment(javax.swing.JLabel.RIGHT);
		JPanel malePanel = new JPanel();
		JPanel femalePanel = new JPanel();
		FlowLayout fl1 = new FlowLayout();
		fl1.setHgap(1);
		fl1.setVgap(1);
		malePanel.setLayout(fl1);
		femalePanel.setLayout(fl1);
		malePanel.add(maleCountLabel);
		malePanel.add(maleLabel);
		femalePanel.add(femaleCountLabel);
		femalePanel.add(femaleLabel);
		countPanel.add(malePanel);
		countPanel.add(femalePanel);

		JPanel picturePanel = new JPanel();
		picturePanel.setLayout(new BorderLayout());
		picturePanel.setPreferredSize(new Dimension(145, 38));
		picturePanel.setBorder(etched);
		picturePanel.add(showPhenotypeButtons[number], BorderLayout.EAST);

		JPanel[] phenotypePanels = new JPanel[numberOfTraits];
		ArrayList<Phenotype> phenoList = 
			childrenSortedByPhenotype[number].get(0).getPhenotypes();
		for (int k = 0; k < numberOfTraits; k++) {
			phenotypePanels[k] = new JPanel();
			phenotypePanels[k].setLayout(
					new BoxLayout(phenotypePanels[k], BoxLayout.X_AXIS));
			phenotypePanels[k].setBorder(etched);
			phenotypePanels[k].add(
					new JLabel(
							Messages.getInstance().getString("VGLII." +
									phenoList.get(k).getTrait().getTraitName())));
			phenotypePanels[k].add(Box.createRigidArea(new Dimension(1, 34)));
		}

		return new IndividualPanelSet(organismPanel,
				countPanel,
				phenotypePanels,
				picturePanel);
	}

	/**
	 * This method sets up the Panel that display the information about the
	 * parents or if the Cage id is 1 and beginner's mode is true then it
	 * displays the details about the underlying genetics model
	 */
	private void setupParentInfoPanel() {
		parentInfoPanel = new JPanel();
		if (id > 1) {
			TitledBorder border = BorderFactory.createTitledBorder(
					Messages.getInstance().getString("VGLII.Parents"));
			border.setTitleColor(PARENT_COLOR);
			border.setBorder(BorderFactory.createLineBorder(PARENT_COLOR));
			parentInfoPanel.setBorder(border);
			parentOrganismUIs = new OrganismUI[2];
			Organism o1 = parents.get(0);
			Organism o2 = parents.get(1);
			int cageId = o1.getCageId() + 1;
			String phenoName1 = o1.getPhenotypeString();
			parentOrganismUIs[0] = new OrganismUI(o1, true, isBeginner, vial);
			parentInfoPanel.add(parentOrganismUIs[0]);
			parentInfoPanel.add(new JLabel("("
					+ Messages.getInstance().getString("VGLII.Cage")
					+ " "
					+ cageId + ")" 
					+ " " + Messages.getInstance().translateLongPhenotypeName(phenoName1)));
			parentInfoPanel.add(new JLabel(" X "));
			cageId = o2.getCageId() + 1;
			String phenoName2 = o2.getPhenotypeString();
			parentOrganismUIs[1] = new OrganismUI(o2, true, isBeginner, vial);
			parentInfoPanel.add(parentOrganismUIs[1]);
			parentInfoPanel.add(new JLabel("(" 
					+ Messages.getInstance().getString("VGLII.Cage")
					+ " "
					+ cageId + ")"
					+ " " + Messages.getInstance().translateLongPhenotypeName(phenoName2)));
		} else {
			if (isBeginner) {
				if (details != null) {
					textDetails = new JEditorPane();
					textDetails.setContentType("text/html");
					textDetails.setEditable(false);
					textDetails.setBackground(parentInfoPanel
							.getBackground());
					textDetails.setBorder(BorderFactory.createEtchedBorder());
					textDetails.setText(details);
					showHideDetails = new JToggleButton();
					textDetails.setFont(showHideDetails.getFont());
					detailsScrollPane = new JScrollPane(textDetails);
					showHideDetails.addItemListener(new ItemListener() {
						public void itemStateChanged(ItemEvent e) {
							if (e.getStateChange() == ItemEvent.DESELECTED) {
								showDetails();
							} else if (e.getStateChange() == ItemEvent.SELECTED) {
								hideDetails();
							}
						}
					});
					JPanel showHidePanel = new JPanel();
					showHidePanel.setLayout(new BorderLayout());
					showHidePanel.add(showHideDetails, BorderLayout.NORTH);
					parentInfoPanel.add(showHidePanel);
					showHideDetails.setSelected(true);
				}
			}
		}
		superPanel.add(parentInfoPanel, BorderLayout.NORTH);
	}

	/**
	 * This method sets up the Panel that displays the genetics details when the
	 * toggle button is pressed
	 */
	private void showDetails() {
		showHideDetails.setText(hideDetails);
		showHideDetails.setIcon(downIcon);
		JPanel panel = (JPanel) showHideDetails.getParent();
		if (panel.getComponentCount() == 1) {
			dialogHeight += 200;
			int dialogWidth = this.getWidth();
			setSize(dialogWidth, dialogHeight);
			detailsScrollPane.setPreferredSize(new Dimension(320,200));
			panel.add(detailsScrollPane, BorderLayout.CENTER);
			pack();
			repaint();
		}
	}

	/**
	 * This method hides the panel thats has the genetics information
	 */
	private void hideDetails() {
		showHideDetails.setText(showDetails);
		showHideDetails.setIcon(closeIcon);
		JPanel panel = (JPanel) showHideDetails.getParent();
		if (panel.getComponentCount() == 2) {
			panel.remove(detailsScrollPane);
			int dialogWidth = this.getWidth();
			dialogHeight -= 200;
			setSize(dialogWidth, dialogHeight);
			pack();
			repaint();
		}
	}


	public int getId() {
		return id;
	}
	
	public boolean isSuperCross() {
		return isSuperCross;
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