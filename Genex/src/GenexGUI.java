/* this is the class that sets up the common GUI for the application * and the applet *  * written by Brian White 2005 *  brian.white@umb.edu *   This program is free software; you can redistribute it and/or * modify it under the terms of the GNU General Public License * as published by the Free Software Foundation; either version 2 * of the License, or (at your option) any later version. * * This program is distributed in the hope that it will be useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the * GNU General Public License for more details. **/ import java.awt.BorderLayout;import java.awt.Container;import java.awt.event.ActionEvent;import java.awt.event.ActionListener;import java.awt.event.KeyEvent;import java.awt.event.KeyListener;import java.net.URL;import javax.swing.ImageIcon;import javax.swing.JButton;import javax.swing.JLabel;import javax.swing.JOptionPane;import javax.swing.JPanel;import javax.swing.JScrollPane;import javax.swing.JTextPane;import javax.swing.event.CaretEvent;import javax.swing.event.CaretListener;import javax.swing.text.html.HTMLDocument;public class GenexGUI { 	static JTextPane textPane = new JTextPane();	static JLabel infoLabel = new JLabel("Selected Base = ");	static JButton resetButton = new JButton("Reset DNA Sequence");	static JButton newSequenceButton = new JButton("Enter New DNA Sequence");		static JButton addCaptionButton = new JButton("Enter a Caption");	static JButton printButton = new JButton("Print");	static JButton bwPrintButton = new JButton("Print in B&W");	static JButton helpButton = new JButton("Help");	static JButton aboutButton = new JButton("About");	static JLabel versionLabel = new JLabel("1.3");	static String defaultDNA;	static String DNA;    static String promoterSequence;    static String terminatorSequence;    static String intronStartSequence;    static String intronEndSequence;    static String polyATail;	                                        	static int headerLength;        //length (as actually displayed) of the text 	                                // & labels before the start of the top DNA strand	static int DNASequenceLength;		static String caption;         //the caption at the bottom of the frame		static String previousProteinString;  //the last protein sequence displayed	static String currentProteinString;		static int caretPosition = 0;  //the number of the selected DNA base		static DocumentRenderer docRenderer;  // for printing		static boolean allowPrinting = false;     //parameter fro allowing printing	                                     //if false, it will not bother you by asking 	                                     // if you want to allow printing	                                     			public void setupGUI(Container contentPane, GenexParams params) {				defaultDNA = params.getDefaultDNA();		DNA = params.getDefaultDNA();		DNASequenceLength = DNA.length();		promoterSequence = params.getPromoterSequence();		terminatorSequence = params.getTerminatorSequence();		intronStartSequence = params.getIntronStartSequence();		intronEndSequence = params.getIntronEndSequence();		polyATail = params.getPolyATail();		allowPrinting = params.isAllowPrinting();				//if it's a prokaryote, no poly A tail		if (intronStartSequence.equals("none") || intronEndSequence.equals("none")) {			polyATail = "";		}			    JScrollPane scrollPane = new JScrollPane(textPane);	    JPanel sequencePanel = new JPanel();	    JPanel printPanel = new JPanel();	    	    if (allowPrinting) {	        docRenderer = new DocumentRenderer();	    }	    	    sequencePanel.add(resetButton);	    sequencePanel.add(newSequenceButton);	    sequencePanel.add(infoLabel);	    	    if (allowPrinting) {	        printPanel.add(addCaptionButton);	        printPanel.add(printButton);	        printPanel.add(bwPrintButton);	    }	    printPanel.add(helpButton);	    printPanel.add(aboutButton);	    printPanel.add(versionLabel);	    contentPane.add(printPanel, BorderLayout.NORTH);	    contentPane.add(sequencePanel, BorderLayout.SOUTH);	    contentPane.add(scrollPane, BorderLayout.CENTER);	 	       	    textPane.setContentType("text/html");	    textPane.setDragEnabled(false);	    textPane.setEditable(false);	    	    addCaptionButton.setToolTipText("Add a descriptive caption to the bottom of the page.");        caption = "";	    printButton.setToolTipText("<html>Print the page in color.<br>" +	    		"<font color=red>This does not work well with Windows.</font></html>");	    bwPrintButton.setToolTipText("<html>Print the page in black and white.<br>" +		        "<font color=green>This works better with Windows.</font></html>");	    helpButton.setToolTipText("Open a window with help info.");	    	    resetButton.setToolTipText("Restore the original DNA Sequence");	    newSequenceButton.setToolTipText("Enter an entirely new DNA sequence");	    	    caption = new String("");	    previousProteinString = new String("");	    currentProteinString = new String("");	    	    //display the default gene	    VisibleGene currentGene = expressGene(defaultDNA, -1);	    textPane.setText(currentGene.getColorHTML() + caption + "</pre></body></html>");	    headerLength = currentGene.getGene().getHeaderLength();	    DNASequenceLength = currentGene.getGene().getDNASequenceLength();        currentProteinString = currentGene.getGene().getProteinString();	    	    //display the gene with a selected base        textPane.addCaretListener(new CaretListener() {            public void caretUpdate(CaretEvent e) {                int dot = e.getDot();                int clickSite = dot - headerLength;                if ((clickSite >= 0) && (clickSite <= DNASequenceLength)) {                	VisibleGene vg = expressGene(DNA, clickSite);                	refreshDisplay(vg, clickSite);                    DNASequenceLength = vg.getGene().getDNASequenceLength();                    caretPosition = clickSite;                    headerLength = vg.getGene().getHeaderLength();                }  else {                    if (textPane.getCaretPosition() !=0) {                        textPane.setCaretPosition(0);                    }                }                            }        });                //allow editing of the gene        //  AGCT inserts DNA        //  agct replaces DNA        //  DELETE deletes DNA        textPane.addKeyListener(new KeyListener() {            public void keyTyped(KeyEvent e) {               String keyTyped = String.valueOf(e.getKeyChar());               int keyNum = Character.getNumericValue(e.getKeyChar());                if (keyTyped.equals("A")                   || keyTyped.equals("G")                   || keyTyped.equals("C")                   || keyTyped.equals("T") ) {                	previousProteinString = currentProteinString;                    StringBuffer workingDNAbuffer = new StringBuffer(DNA);                    workingDNAbuffer.insert(caretPosition, keyTyped);                    DNA = workingDNAbuffer.toString();                    caretPosition++;                    VisibleGene vg = expressGene(DNA, caretPosition);                    refreshDisplay(vg, caretPosition);                    currentProteinString = vg.getGene().getProteinString();                    DNASequenceLength = vg.getGene().getDNASequenceLength();                    headerLength = vg.getGene().getHeaderLength() + 1;                     //need the +1 otherwise, when you click on a base after moving + or -                    // the selected base is n+1 - why??                            }                if (keyTyped.equals("a")                   || keyTyped.equals("g")                   || keyTyped.equals("c")                   || keyTyped.equals("t") ) {                	previousProteinString = currentProteinString;                    StringBuffer workingDNAbuffer = new StringBuffer(DNA);                    workingDNAbuffer.replace(caretPosition,                                              caretPosition + 1,                                             keyTyped.toUpperCase());                    DNA = workingDNAbuffer.toString();                    VisibleGene vg = expressGene(DNA, caretPosition);                    refreshDisplay(vg, caretPosition);                    currentProteinString = vg.getGene().getProteinString();                    DNASequenceLength = vg.getGene().getDNASequenceLength();                    headerLength = vg.getGene().getHeaderLength() + 1;                     //need the +1 otherwise, when you click on a base after moving + or -                    // the selected base is n+1 - why??                            }                                if (keyNum == -1 ) {                	previousProteinString = currentProteinString;                    StringBuffer workingDNAbuffer = new StringBuffer(DNA);                    workingDNAbuffer.deleteCharAt(caretPosition);                    DNA = workingDNAbuffer.toString();                    if (caretPosition >= 0) {                        caretPosition--;                    }                    VisibleGene vg = expressGene(DNA, caretPosition);                    refreshDisplay(vg, caretPosition);                    currentProteinString = vg.getGene().getProteinString();                    DNASequenceLength = vg.getGene().getDNASequenceLength();                    headerLength = vg.getGene().getHeaderLength();                }                                if (keyTyped.equals("+")                 		|| keyTyped.equals("-")						|| keyTyped.equals("=")) {                    if (keyTyped.equals("+") || keyTyped.equals("=")) {                        caretPosition++;                        if (caretPosition > (DNA.length() - 1)) {                            caretPosition = DNA.length() - 1;                        }                    } else {                        caretPosition--;                        if (caretPosition < 0) {                            caretPosition = 0;                        }                    }                    VisibleGene vg = expressGene(DNA, caretPosition);                    refreshDisplay(vg, caretPosition);                    DNASequenceLength = vg.getGene().getDNASequenceLength();                    headerLength = vg.getGene().getHeaderLength() + 1;                           //need the +1 otherwise, when you click on a base after moving + or -                    // the selected base is n+1 - why??                            }            }                        public void keyPressed(KeyEvent e) {            	if (e.getKeyCode() == 39) {            		caretPosition++;            		if (caretPosition > (DNA.length() - 1)) {            			caretPosition = DNA.length() - 1;            		}                	VisibleGene vg = expressGene(DNA, caretPosition);                	refreshDisplay(vg, caretPosition);                	DNASequenceLength = vg.getGene().getDNASequenceLength();                	headerLength = vg.getGene().getHeaderLength() + 1;                       	//need the +1 otherwise, when you click on a base after moving + or -                	// the selected base is n+1 - why??                        	}            	if (e.getKeyCode() == 37){            		caretPosition--;            		if (caretPosition < 0) {            			caretPosition = 0;            		}      	            		VisibleGene vg = expressGene(DNA, caretPosition);            		refreshDisplay(vg, caretPosition);             		DNASequenceLength = vg.getGene().getDNASequenceLength();            		headerLength = vg.getGene().getHeaderLength() + 1;                   		//need the +1 otherwise, when you click on a base after moving + or -            		// the selected base is n+1 - why??                        	}            }                        public void keyReleased(KeyEvent e) {            }        });                //display the default DNA        resetButton.addActionListener(new ActionListener() {            public void actionPerformed(ActionEvent e) {                DNA = defaultDNA;        	    VisibleGene currentGene = expressGene(DNA, -1);        	    refreshDisplay(currentGene, -1);                currentProteinString = currentGene.getGene().getProteinString();        	    headerLength = currentGene.getGene().getHeaderLength();        	    DNASequenceLength = currentGene.getGene().getDNASequenceLength();         	    headerLength = currentGene.getGene().getHeaderLength();                       }        });                //allow the user to enter a sequence manually        newSequenceButton.addActionListener(new ActionListener() {            public void actionPerformed(ActionEvent e) {                String newDNA = (String)JOptionPane.showInputDialog(null,                                         "Enter new DNA Sequence",                                         "New DNA Sequence",                                         JOptionPane.PLAIN_MESSAGE,                                         null, null, DNA);                if (newDNA == null) return;                previousProteinString = currentProteinString;                newDNA = newDNA.toUpperCase();                newDNA = newDNA.replaceAll("[^AGCT]","");                DNA = newDNA;                caretPosition = -1;        	    VisibleGene currentGene = expressGene(DNA, -1);        	    refreshDisplay(currentGene, -1);                currentProteinString = currentGene.getGene().getProteinString();        	    headerLength = currentGene.getGene().getHeaderLength();        	    DNASequenceLength = currentGene.getGene().getDNASequenceLength();          	    headerLength = currentGene.getGene().getHeaderLength();                      }        });	    	    //allow the user to add a caption at the bottom of the textPane        // if printing is allowed        if (allowPrinting) {	        addCaptionButton.addActionListener(new ActionListener() {	            public void actionPerformed(ActionEvent e) {	                caption = (String)JOptionPane.showInputDialog(null,	                                     "Enter a Caption for this page.",	                                     "Caption:",	                                     JOptionPane.PLAIN_MESSAGE,	                                     null, null, caption);		            if (caption == null) return;	        	    VisibleGene currentGene = expressGene(DNA, -1);	        	    refreshDisplay(currentGene, -1);	        	    headerLength = currentGene.getGene().getHeaderLength();	        	    DNASequenceLength = currentGene.getGene().getDNASequenceLength(); 	        	    headerLength = currentGene.getGene().getHeaderLength();           	            	            }	        });        }	            //print the textPane in color, if allowed        if (allowPrinting) {	        printButton.addActionListener(new ActionListener() {	            public void actionPerformed(ActionEvent e) {	                docRenderer.setScaleWidthToFit(true);	                HTMLDocument htdoc = (HTMLDocument)textPane.getDocument();	                docRenderer.print(htdoc);	            }	        });        }                //print the black and white html if allowed        if (allowPrinting){    	    bwPrintButton.addActionListener(new ActionListener() {    	        public void actionPerformed(ActionEvent e) {    	            docRenderer.setScaleWidthToFit(true);    	            JTextPane bwPane = new JTextPane();    	            bwPane.setContentType("text/html");    	            VisibleGene currentGene = expressGene(DNA, caretPosition);    	            bwPane.setText(currentGene.getBwHTML() + "\n" + caption);    	            HTMLDocument htdoc = (HTMLDocument)bwPane.getDocument();    	            docRenderer.print(htdoc);    	        }    	    });        }                //display the help info	    helpButton.addActionListener(new ActionListener() {	        public void actionPerformed(ActionEvent e) {	            URL url = Genex.class.getResource("fig1.gif");	            ImageIcon pic = new ImageIcon(url);	            JOptionPane.showMessageDialog(null,	                              "<html><body>"	                  + "Click the top strand of DNA to select a base.<br>"	                  + "Move <u>right</u> with + or right-arrow<br>"	                  + "Move <u>left</u> with - or left-arrow<br>"	                  + "<u>Delete</u> a base with the Delete or Backspace Key.<br>"	                  + "<u>Insert</u> a base with AGCT.<br>"	                  + "<u>Replace</u> a base with agct.<br>"	                  + "</body></html>",	                              "Genex Help",	                              JOptionPane.PLAIN_MESSAGE,	                              pic);	        }	    });        //display the about info	    aboutButton.addActionListener(new ActionListener() {	        public void actionPerformed(ActionEvent e) {	            JOptionPane.showMessageDialog(null,	                              "<html><body>"	                  + "<center>Gene Explorer Version 1.3<br>"	                  + "<br>"	                  + "Brian White (2005)<br>"	                  + "brian.white@umb.edu"	                  + "</body></html>",	                              "About Genex",	                              JOptionPane.PLAIN_MESSAGE);	        }	    });	}		static VisibleGene expressGene(String currentDNA, int selectedDNABase) {        	// set up the gene for transcription, etc.	    Gene currentGene = new Gene(currentDNA, promoterSequence, terminatorSequence,                                     intronStartSequence, intronEndSequence, polyATail);	        //process the gene before displaying it        currentGene.transcribe();        currentGene.process();        currentGene.translate();//        currentGene.showItAll();        //generate the html & return it        return new VisibleGene(currentGene.generateHTML(selectedDNABase), currentGene);    }    	static void refreshDisplay(VisibleGene vg, int selectedBase) {		if (selectedBase != -1) {            infoLabel.setText("Selected Base = " + selectedBase);		}  else {			infoLabel.setText("Selected Base = ");		}        textPane.setText(vg.getColorHTML()             + "<font color=blue>" + previousProteinString             + "</font></pre><br><br><br><font size=+1>" + caption             + "</font></body></html>");	}}