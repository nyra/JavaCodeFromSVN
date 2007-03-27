package molBiol;/* this is the class that sets up the common GUI for the application * and the applet *  * written by Brian White 2005 *  brian.white@umb.edu *   This program is free software; you can redistribute it and/or * modify it under the terms of the GNU General Public License * as published by the Free Software Foundation; either version 2 * of the License, or (at your option) any later version. * * This program is distributed in the hope that it will be useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the * GNU General Public License for more details. * */ import java.awt.BorderLayout;import java.awt.Color;import java.awt.Dimension;import java.awt.event.ActionEvent;import java.awt.event.ActionListener;import java.awt.event.KeyEvent;import java.awt.event.KeyListener;import javax.swing.BorderFactory;import javax.swing.ImageIcon;import javax.swing.JButton;import javax.swing.JLabel;import javax.swing.JOptionPane;import javax.swing.JPanel;import javax.swing.JScrollPane;import javax.swing.JTextPane;import javax.swing.border.LineBorder;import javax.swing.event.CaretEvent;import javax.swing.event.CaretListener;import molGenExp.ColorModel;import molGenExp.MolGenExp;import molGenExp.ProteinImageFactory;import molGenExp.ProteinImageSet;import molGenExp.RYBColorModel;import biochem.Attributes;import biochem.FoldedPolypeptide;import biochem.FoldingException;import biochem.FoldingManager;import biochem.OutputPalette;public class GeneExpressionWindow extends JPanel {	Genex genex;		//the parent genex that holds this window	MolGenExp mge;		//the parent application	ColorModel colorModel;	String title;	JTextPane textPane;	JLabel infoLabel;	JPanel buttonPanel;	JButton loadSampleButton;	JButton newSequenceButton;	JButton foldProteinButton;	JLabel colorLabel;	JLabel colorChip;	String defaultDNA;	String DNA;	String promoterSequence;	String terminatorSequence;	String intronStartSequence;	String intronEndSequence;	String polyATail;	ExpressedGene currentGene;	int headerLength;        //length (as actually displayed) of the text 	// & labels before the start of the top DNA strand	int DNASequenceLength;	String caption;         //the caption at the bottom of the frame	String previousProteinString;  //the last protein sequence displayed	String currentProteinString;	int caretPosition;  //the number of the selected DNA base	public GeneExpressionWindow(String title, 			GenexParams params, 			ColorModel colorModel,			final Genex genex,			final MolGenExp mge) {		super();		this.colorModel = colorModel;		this.genex = genex;		this.title = title;		this.mge = mge;		caretPosition = 0;		defaultDNA = params.getDefaultDNA();		DNA = params.getDefaultDNA();		DNASequenceLength = DNA.length();		promoterSequence = params.getPromoterSequence();		terminatorSequence = params.getTerminatorSequence();		intronStartSequence = params.getIntronStartSequence();		intronEndSequence = params.getIntronEndSequence();		polyATail = params.getPolyATail();		//if it's a prokaryote, no poly A tail		if (intronStartSequence.equals("none") || intronEndSequence.equals("none")) {			polyATail = "";		}		setupUI();	}	private void setupUI() {		textPane = new JTextPane();		infoLabel = new JLabel("Selected Base = ");		loadSampleButton = new JButton("Load Sample DNA");		newSequenceButton = new JButton("Enter New DNA");		foldProteinButton = new JButton("Fold Protein");		colorLabel = new JLabel("Color:");		colorChip = new JLabel("     ");		colorChip.setOpaque(true);		colorChip.setBackground(Color.WHITE);		colorChip.setBorder(new LineBorder(Color.BLACK));		JScrollPane scrollPane = new JScrollPane(textPane);		buttonPanel = new JPanel();		buttonPanel.add(colorLabel);		buttonPanel.add(colorChip);		buttonPanel.add(foldProteinButton);		buttonPanel.add(loadSampleButton);		buttonPanel.add(newSequenceButton);		buttonPanel.add(infoLabel);		setLayout(new BorderLayout());		add(buttonPanel, BorderLayout.SOUTH);		add(scrollPane, BorderLayout.CENTER);	 		setBorder(BorderFactory.createTitledBorder(title));		textPane.setContentType("text/html");		textPane.setDragEnabled(false);		textPane.setEditable(false);		loadSampleButton.setToolTipText("Restore the original DNA Sequence");		newSequenceButton.setToolTipText("Enter an entirely new DNA sequence");		caption = new String("");		previousProteinString = new String("");		currentProteinString = new String("");		//display a blank starting gene		DNA = "";		currentGene = expressGene(DNA, -1);		textPane.setText(currentGene.getHTML() + caption + "</pre></body></html>");		headerLength = currentGene.getGene().getHeaderLength();		DNASequenceLength = currentGene.getGene().getDNASequenceLength();		currentProteinString = currentGene.getGene().getProteinString();		showProteinChangedButNotFolded();		//display the gene with a selected base		textPane.addCaretListener(new CaretListener() {			public void caretUpdate(CaretEvent e) {				int dot = e.getDot();				int clickSite = dot - headerLength;				if ((clickSite >= 0) && (clickSite <= DNASequenceLength)) {					currentGene = expressGene(DNA, clickSite);					refreshDisplay(currentGene, clickSite);					DNASequenceLength = currentGene.getGene().getDNASequenceLength();					caretPosition = clickSite;					headerLength = currentGene.getGene().getHeaderLength();				}  else {					if (textPane.getCaretPosition() !=0) {						textPane.setCaretPosition(0);					}				}			}		});		//allow editing of the gene		//  AGCT inserts DNA		//  agct replaces DNA		//  DELETE deletes DNA		textPane.addKeyListener(new KeyListener() {			public void keyTyped(KeyEvent e) {				String keyTyped = String.valueOf(e.getKeyChar());				int keyNum = Character.getNumericValue(e.getKeyChar());				if (keyTyped.equals("A")						|| keyTyped.equals("G")						|| keyTyped.equals("C")						|| keyTyped.equals("T") ) {					previousProteinString = currentProteinString;					StringBuffer workingDNAbuffer = new StringBuffer(DNA);					workingDNAbuffer.insert(caretPosition, keyTyped);					DNA = workingDNAbuffer.toString();					caretPosition++;					currentGene = expressGene(DNA, caretPosition);					refreshDisplay(currentGene, caretPosition);					currentProteinString = currentGene.getGene().getProteinString();					DNASequenceLength = currentGene.getGene().getDNASequenceLength();					headerLength = currentGene.getGene().getHeaderLength() + 1; 					//need the +1 otherwise, when you click on a base after moving + or -					// the selected base is n+1 - why??            					showProteinChangedButNotFolded();				}				if (keyTyped.equals("a")						|| keyTyped.equals("g")						|| keyTyped.equals("c")						|| keyTyped.equals("t") ) {					previousProteinString = currentProteinString;					StringBuffer workingDNAbuffer = new StringBuffer(DNA);					workingDNAbuffer.replace(caretPosition, 							caretPosition + 1,							keyTyped.toUpperCase());					DNA = workingDNAbuffer.toString();					currentGene = expressGene(DNA, caretPosition);					refreshDisplay(currentGene, caretPosition);					currentProteinString = currentGene.getGene().getProteinString();					DNASequenceLength = currentGene.getGene().getDNASequenceLength();					headerLength = currentGene.getGene().getHeaderLength() + 1; 					//need the +1 otherwise, when you click on a base after moving + or -					// the selected base is n+1 - why??  							showProteinChangedButNotFolded();				}				if (keyNum == -1 ) {					previousProteinString = currentProteinString;					StringBuffer workingDNAbuffer = new StringBuffer(DNA);					workingDNAbuffer.deleteCharAt(caretPosition);					DNA = workingDNAbuffer.toString();					if (caretPosition >= 0) {						caretPosition--;					}					currentGene = expressGene(DNA, caretPosition);					refreshDisplay(currentGene, caretPosition);					currentProteinString = currentGene.getGene().getProteinString();					DNASequenceLength = currentGene.getGene().getDNASequenceLength();					headerLength = currentGene.getGene().getHeaderLength();					showProteinChangedButNotFolded();				}				if (keyTyped.equals("+") 						|| keyTyped.equals("-")						|| keyTyped.equals("=")) {					if (keyTyped.equals("+") || keyTyped.equals("=")) {						caretPosition++;						if (caretPosition > (DNA.length() - 1)) {							caretPosition = DNA.length() - 1;						}					} else {						caretPosition--;						if (caretPosition < 0) {							caretPosition = 0;						}					}					currentGene = expressGene(DNA, caretPosition);					refreshDisplay(currentGene, caretPosition);					DNASequenceLength = currentGene.getGene().getDNASequenceLength();					headerLength = currentGene.getGene().getHeaderLength() + 1;       					//need the +1 otherwise, when you click on a base after moving + or -					// the selected base is n+1 - why??            				}			}			public void keyPressed(KeyEvent e) {				if (e.getKeyCode() == 39) {					caretPosition++;					if (caretPosition > (DNA.length() - 1)) {						caretPosition = DNA.length() - 1;					}					currentGene = expressGene(DNA, caretPosition);					refreshDisplay(currentGene, caretPosition);					DNASequenceLength = currentGene.getGene().getDNASequenceLength();					headerLength = currentGene.getGene().getHeaderLength() + 1;       					//need the +1 otherwise, when you click on a base after moving + or -					// the selected base is n+1 - why??            				}				if (e.getKeyCode() == 37){					caretPosition--;					if (caretPosition < 0) {						caretPosition = 0;					}      						currentGene = expressGene(DNA, caretPosition);					refreshDisplay(currentGene, caretPosition);					DNASequenceLength = currentGene.getGene().getDNASequenceLength();					headerLength = currentGene.getGene().getHeaderLength() + 1;       					//need the +1 otherwise, when you click on a base after moving + or -					// the selected base is n+1 - why??            				}			}			public void keyReleased(KeyEvent e) {			}		});		//display the sample DNA		loadSampleButton.addActionListener(new ActionListener() {			public void actionPerformed(ActionEvent e) {				DNA = defaultDNA;				currentGene = expressGene(DNA, -1);				refreshDisplay(currentGene, -1);				currentProteinString = currentGene.getGene().getProteinString();				headerLength = currentGene.getGene().getHeaderLength();				DNASequenceLength = currentGene.getGene().getDNASequenceLength(); 				headerLength = currentGene.getGene().getHeaderLength(); 				showProteinChangedButNotFolded();			}		});		//allow the user to enter a sequence manually		newSequenceButton.addActionListener(new ActionListener() {			public void actionPerformed(ActionEvent e) {				String newDNA = (String)JOptionPane.showInputDialog(null,						"Enter new DNA Sequence",						"New DNA Sequence",						JOptionPane.PLAIN_MESSAGE,						null, null, DNA);				if (newDNA == null) return;				previousProteinString = currentProteinString;				newDNA = newDNA.toUpperCase();				newDNA = newDNA.replaceAll("[^AGCT]","");				DNA = newDNA;				caretPosition = -1;				currentGene = expressGene(DNA, -1);				refreshDisplay(currentGene, -1);				currentProteinString = currentGene.getGene().getProteinString();				headerLength = currentGene.getGene().getHeaderLength();				DNASequenceLength = currentGene.getGene().getDNASequenceLength();  				showProteinChangedButNotFolded();			}		});		//fold the protein, calculate the color, and add to history list		foldProteinButton.addActionListener(new ActionListener() {			public void actionPerformed(ActionEvent arg0) {				foldExpressedProtein();			}		});	}		public String getDNA() {		return DNA;	}	public void foldExpressedProtein() {		//process the protein sequence into a form that		// the folding routine can understand		Gene gene = currentGene.getGene();		String proteinSequence = gene.getProteinString();		if (proteinSequence.indexOf("none") != -1) {			proteinSequence = "";		} else {			//remove leading/trailing spaces and the N- and C-			proteinSequence = 				proteinSequence.replaceAll(" ", "");			proteinSequence = 				proteinSequence.replaceAll("N-", "");			proteinSequence = 				proteinSequence.replaceAll("-C", "");			//insert spaces between amino acid codes			StringBuffer psBuffer = new StringBuffer(proteinSequence);			for (int i = 3; i < psBuffer.length(); i = i + 4) {				psBuffer = psBuffer.insert(i, " ");			}			proteinSequence = psBuffer.toString();		}				//fold it		Attributes attributes = new Attributes(				proteinSequence, 				3,				colorModel,				"straight",				"test");		FoldingManager manager = FoldingManager.getInstance(colorModel);		try {			manager.fold(attributes);		} catch (FoldingException e) {			e.printStackTrace();		}		//make an icon and display it in a dialog		OutputPalette op = new OutputPalette(colorModel);		manager.createCanvas(op);		Dimension requiredCanvasSize = 			op.getDrawingPane().getRequiredCanvasSize();		ProteinImageSet images = 			ProteinImageFactory.generateImages(op, requiredCanvasSize);		colorChip.setBackground(op.getProteinColor());		FoldedPolypeptide fp = new FoldedPolypeptide(				proteinSequence,				op.getDrawingPane().getGrid(), 				new ImageIcon(images.getFullScaleImage()),				new ImageIcon(images.getThumbnailImage()), 				op.getProteinColor());		currentGene.setFoldedPolypeptide(fp);		showProteinFolded();		//add to history list		genex.addExpressedToHistoryList(currentGene);	}	public ExpressedGene expressGene(String currentDNA, int selectedDNABase) {		// set up the gene for transcription, etc.		Gene currentGene = new Gene(currentDNA, promoterSequence, terminatorSequence,				intronStartSequence, intronEndSequence, polyATail);		//process the gene before displaying it		currentGene.transcribe();		currentGene.process();		currentGene.translate();//		currentGene.showItAll();		//generate the html & return it		return new ExpressedGene(currentGene.generateHTML(selectedDNABase), currentGene);	}    	public void refreshDisplay(ExpressedGene vg, int selectedBase) {		if (selectedBase != -1) {			infoLabel.setText("Selected Base = " + selectedBase);		}  else {			infoLabel.setText("Selected Base = ");		}		textPane.setText(vg.getHTML() 				+ "<font color=blue>" + previousProteinString 				+ "</font></pre><br><br><br><font size=+1>" + caption 				+ "</font></body></html>");	}	public void showProteinChangedButNotFolded() {		currentGene.setFoldedPolypeptide(null);		foldProteinButton.setEnabled(true);		this.setBackground(Color.pink);		buttonPanel.setBackground(Color.pink);		colorChip.setBackground(Color.LIGHT_GRAY);		genex.updateCombinedColor();	}	public void showProteinFolded() {		foldProteinButton.setEnabled(false);		this.setBackground(Color.LIGHT_GRAY);		buttonPanel.setBackground(Color.LIGHT_GRAY);	}	public Color getColor() {		if (currentGene.getFoldedPolypeptide() != null) {			return currentGene.getFoldedPolypeptide().getColor();		} else {			return null;		}	}	public ExpressedGene getCurrentGene() {		return currentGene;	}	public void setCurrentGene(ExpressedGene gene) {		currentGene = gene;		textPane.setText(currentGene.getHTML() + caption + "</pre></body></html>");		DNA = currentGene.getGene().getDNASequence();		headerLength = currentGene.getGene().getHeaderLength();		DNASequenceLength = currentGene.getGene().getDNASequenceLength();		currentProteinString = currentGene.getGene().getProteinString();		showProteinFolded();		colorChip.setBackground(currentGene.getFoldedPolypeptide().getColor());		genex.updateCombinedColor();	}	}