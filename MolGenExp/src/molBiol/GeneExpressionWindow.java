package molBiol;/* this is the class that sets up the common GUI for the application * and the applet *  * written by Brian White 2005 *  brian.white@umb.edu *   This program is free software; you can redistribute it and/or * modify it under the terms of the GNU General Public License * as published by the Free Software Foundation; either version 2 * of the License, or (at your option) any later version. * * This program is distributed in the hope that it will be useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the * GNU General Public License for more details. **/ import java.awt.BorderLayout;import java.awt.event.ActionEvent;import java.awt.event.ActionListener;import java.awt.event.KeyEvent;import java.awt.event.KeyListener;import javax.swing.BorderFactory;import javax.swing.JButton;import javax.swing.JLabel;import javax.swing.JOptionPane;import javax.swing.JPanel;import javax.swing.JScrollPane;import javax.swing.JTextPane;import javax.swing.event.CaretEvent;import javax.swing.event.CaretListener;import biochem.Attributes;public class GeneExpressionWindow extends JPanel {		Genex genex;		//the parent genex that holds this window		String title; 	JTextPane textPane;	JLabel infoLabel;	JButton resetButton;	JButton newSequenceButton;	JButton showFoldedProteinButton;	JButton foldProteinButton;		String defaultDNA;	String DNA;    String promoterSequence;    String terminatorSequence;    String intronStartSequence;    String intronEndSequence;    String polyATail;	                                        	int headerLength;        //length (as actually displayed) of the text 	                                // & labels before the start of the top DNA strand	int DNASequenceLength;		String caption;         //the caption at the bottom of the frame		String previousProteinString;  //the last protein sequence displayed	String currentProteinString;		int caretPosition;  //the number of the selected DNA base				public GeneExpressionWindow(String title, GenexParams params, final Genex genex) {		super();		this.genex = genex;		this.title = title;				caretPosition = 0;				defaultDNA = params.getDefaultDNA();		DNA = params.getDefaultDNA();		DNASequenceLength = DNA.length();				promoterSequence = params.getPromoterSequence();		terminatorSequence = params.getTerminatorSequence();		intronStartSequence = params.getIntronStartSequence();		intronEndSequence = params.getIntronEndSequence();		polyATail = params.getPolyATail();				//if it's a prokaryote, no poly A tail		if (intronStartSequence.equals("none") || intronEndSequence.equals("none")) {			polyATail = "";		}				setupUI();	}		private void setupUI() {				textPane = new JTextPane();		infoLabel = new JLabel("Selected Base = ");		resetButton = new JButton("Reset DNA Sequence");		newSequenceButton = new JButton("Enter New DNA Sequence");		showFoldedProteinButton = new JButton("Show Folded Protein");		foldProteinButton = new JButton("Fold Protein");	    JScrollPane scrollPane = new JScrollPane(textPane);	    JPanel buttonPanel = new JPanel();	    buttonPanel.add(foldProteinButton);	    buttonPanel.add(showFoldedProteinButton);	    buttonPanel.add(resetButton);	    buttonPanel.add(newSequenceButton);	    buttonPanel.add(infoLabel);	    	    setLayout(new BorderLayout());	    add(buttonPanel, BorderLayout.SOUTH);	    add(scrollPane, BorderLayout.CENTER);	 	    	    setBorder(BorderFactory.createTitledBorder(title));	       	    textPane.setContentType("text/html");	    textPane.setDragEnabled(false);	    textPane.setEditable(false);	    	    	    resetButton.setToolTipText("Restore the original DNA Sequence");	    newSequenceButton.setToolTipText("Enter an entirely new DNA sequence");	    	    caption = new String("");	    previousProteinString = new String("");	    currentProteinString = new String("");	    	    //display the default gene	    VisibleGene currentGene = expressGene(defaultDNA, -1);	    textPane.setText(currentGene.getHTML() + caption + "</pre></body></html>");	    headerLength = currentGene.getGene().getHeaderLength();	    DNASequenceLength = currentGene.getGene().getDNASequenceLength();        currentProteinString = currentGene.getGene().getProteinString();	    	    //display the gene with a selected base        textPane.addCaretListener(new CaretListener() {            public void caretUpdate(CaretEvent e) {                int dot = e.getDot();                int clickSite = dot - headerLength;                if ((clickSite >= 0) && (clickSite <= DNASequenceLength)) {                	VisibleGene vg = expressGene(DNA, clickSite);                	refreshDisplay(vg, clickSite);                    DNASequenceLength = vg.getGene().getDNASequenceLength();                    caretPosition = clickSite;                    headerLength = vg.getGene().getHeaderLength();                }  else {                    if (textPane.getCaretPosition() !=0) {                        textPane.setCaretPosition(0);                    }                }                            }        });                //allow editing of the gene        //  AGCT inserts DNA        //  agct replaces DNA        //  DELETE deletes DNA        textPane.addKeyListener(new KeyListener() {            public void keyTyped(KeyEvent e) {               String keyTyped = String.valueOf(e.getKeyChar());               int keyNum = Character.getNumericValue(e.getKeyChar());                if (keyTyped.equals("A")                   || keyTyped.equals("G")                   || keyTyped.equals("C")                   || keyTyped.equals("T") ) {                	previousProteinString = currentProteinString;                    StringBuffer workingDNAbuffer = new StringBuffer(DNA);                    workingDNAbuffer.insert(caretPosition, keyTyped);                    DNA = workingDNAbuffer.toString();                    caretPosition++;                    VisibleGene vg = expressGene(DNA, caretPosition);                    refreshDisplay(vg, caretPosition);                    currentProteinString = vg.getGene().getProteinString();                    DNASequenceLength = vg.getGene().getDNASequenceLength();                    headerLength = vg.getGene().getHeaderLength() + 1;                     //need the +1 otherwise, when you click on a base after moving + or -                    // the selected base is n+1 - why??                            }                if (keyTyped.equals("a")                   || keyTyped.equals("g")                   || keyTyped.equals("c")                   || keyTyped.equals("t") ) {                	previousProteinString = currentProteinString;                    StringBuffer workingDNAbuffer = new StringBuffer(DNA);                    workingDNAbuffer.replace(caretPosition,                                              caretPosition + 1,                                             keyTyped.toUpperCase());                    DNA = workingDNAbuffer.toString();                    VisibleGene vg = expressGene(DNA, caretPosition);                    refreshDisplay(vg, caretPosition);                    currentProteinString = vg.getGene().getProteinString();                    DNASequenceLength = vg.getGene().getDNASequenceLength();                    headerLength = vg.getGene().getHeaderLength() + 1;                     //need the +1 otherwise, when you click on a base after moving + or -                    // the selected base is n+1 - why??                            }                                if (keyNum == -1 ) {                	previousProteinString = currentProteinString;                    StringBuffer workingDNAbuffer = new StringBuffer(DNA);                    workingDNAbuffer.deleteCharAt(caretPosition);                    DNA = workingDNAbuffer.toString();                    if (caretPosition >= 0) {                        caretPosition--;                    }                    VisibleGene vg = expressGene(DNA, caretPosition);                    refreshDisplay(vg, caretPosition);                    currentProteinString = vg.getGene().getProteinString();                    DNASequenceLength = vg.getGene().getDNASequenceLength();                    headerLength = vg.getGene().getHeaderLength();                }                                if (keyTyped.equals("+")                 		|| keyTyped.equals("-")						|| keyTyped.equals("=")) {                    if (keyTyped.equals("+") || keyTyped.equals("=")) {                        caretPosition++;                        if (caretPosition > (DNA.length() - 1)) {                            caretPosition = DNA.length() - 1;                        }                    } else {                        caretPosition--;                        if (caretPosition < 0) {                            caretPosition = 0;                        }                    }                    VisibleGene vg = expressGene(DNA, caretPosition);                    refreshDisplay(vg, caretPosition);                    DNASequenceLength = vg.getGene().getDNASequenceLength();                    headerLength = vg.getGene().getHeaderLength() + 1;                           //need the +1 otherwise, when you click on a base after moving + or -                    // the selected base is n+1 - why??                            }            }                        public void keyPressed(KeyEvent e) {            	if (e.getKeyCode() == 39) {            		caretPosition++;            		if (caretPosition > (DNA.length() - 1)) {            			caretPosition = DNA.length() - 1;            		}                	VisibleGene vg = expressGene(DNA, caretPosition);                	refreshDisplay(vg, caretPosition);                	DNASequenceLength = vg.getGene().getDNASequenceLength();                	headerLength = vg.getGene().getHeaderLength() + 1;                       	//need the +1 otherwise, when you click on a base after moving + or -                	// the selected base is n+1 - why??                        	}            	if (e.getKeyCode() == 37){            		caretPosition--;            		if (caretPosition < 0) {            			caretPosition = 0;            		}      	            		VisibleGene vg = expressGene(DNA, caretPosition);            		refreshDisplay(vg, caretPosition);             		DNASequenceLength = vg.getGene().getDNASequenceLength();            		headerLength = vg.getGene().getHeaderLength() + 1;                   		//need the +1 otherwise, when you click on a base after moving + or -            		// the selected base is n+1 - why??                        	}            }                        public void keyReleased(KeyEvent e) {            }        });                //display the default DNA        resetButton.addActionListener(new ActionListener() {            public void actionPerformed(ActionEvent e) {                DNA = defaultDNA;        	    VisibleGene currentGene = expressGene(DNA, -1);        	    refreshDisplay(currentGene, -1);                currentProteinString = currentGene.getGene().getProteinString();        	    headerLength = currentGene.getGene().getHeaderLength();        	    DNASequenceLength = currentGene.getGene().getDNASequenceLength();         	    headerLength = currentGene.getGene().getHeaderLength();                       }        });                //allow the user to enter a sequence manually        newSequenceButton.addActionListener(new ActionListener() {            public void actionPerformed(ActionEvent e) {                String newDNA = (String)JOptionPane.showInputDialog(null,                                         "Enter new DNA Sequence",                                         "New DNA Sequence",                                         JOptionPane.PLAIN_MESSAGE,                                         null, null, DNA);                if (newDNA == null) return;                previousProteinString = currentProteinString;                newDNA = newDNA.toUpperCase();                newDNA = newDNA.replaceAll("[^AGCT]","");                DNA = newDNA;                caretPosition = -1;        	    VisibleGene currentGene = expressGene(DNA, -1);        	    refreshDisplay(currentGene, -1);                currentProteinString = currentGene.getGene().getProteinString();        	    headerLength = currentGene.getGene().getHeaderLength();        	    DNASequenceLength = currentGene.getGene().getDNASequenceLength();          	    headerLength = currentGene.getGene().getHeaderLength();                      }        });                //fold the protein, calculate the color, and add to history list        foldProteinButton.addActionListener(new ActionListener() {			public void actionPerformed(ActionEvent arg0) {				Gene gene = currentGene.getGene();				Attributes attributes = new Attributes();			}        });	    	  	}		public VisibleGene expressGene(String currentDNA, int selectedDNABase) {        	// set up the gene for transcription, etc.	    Gene currentGene = new Gene(currentDNA, promoterSequence, terminatorSequence,                                     intronStartSequence, intronEndSequence, polyATail);	        //process the gene before displaying it        currentGene.transcribe();        currentGene.process();        currentGene.translate();//        currentGene.showItAll();        //generate the html & return it        return new VisibleGene(currentGene.generateHTML(selectedDNABase), currentGene);    }    	public void refreshDisplay(VisibleGene vg, int selectedBase) {		if (selectedBase != -1) {            infoLabel.setText("Selected Base = " + selectedBase);		}  else {			infoLabel.setText("Selected Base = ");		}        textPane.setText(vg.getHTML()             + "<font color=blue>" + previousProteinString             + "</font></pre><br><br><br><font size=+1>" + caption             + "</font></body></html>");	}}