package genetics;/* this is the main class - the enetics workshop application * written by Brian White 2004 *  brian.white@umb.edu *   This program is free software; you can redistribute it and/or * modify it under the terms of the GNU General Public License * as published by the Free Software Foundation; either version 2 * of the License, or (at your option) any later version. * * This program is distributed in the hope that it will be useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the * GNU General Public License for more details. * */ import java.awt.BorderLayout;import java.awt.Color;import java.awt.Dimension;import java.awt.GridLayout;import java.awt.event.ActionEvent;import java.awt.event.ActionListener;import java.util.regex.Pattern;import javax.swing.BorderFactory;import javax.swing.Box;import javax.swing.BoxLayout;import javax.swing.DefaultListModel;import javax.swing.JButton;import javax.swing.JOptionPane;import javax.swing.JPanel;import javax.swing.JScrollPane;import molGenExp.ColorModel;import molGenExp.MolGenExp;import molGenExp.Organism;import molGenExp.RYBColorModel;public class GeneticsWorkshop extends JPanel {	MolGenExp mge;			GeneticsWindow upperGeneticsWin;	GeneticsWindow lowerGeneticsWin;	ColorModel colorModel;	GeneticsMiddleButtonPanel geneticsMiddleButtonPanel;	GeneticsHistoryList geneticsHistoryList;	JScrollPane histListScrollPane;		int nextTrayNumber;	public GeneticsWorkshop (MolGenExp mge) {		super();		this.mge = mge;		setupUI();		nextTrayNumber = 0;	}	private void setupUI() {		colorModel = mge.getOverallColorModel();		setLayout(new BorderLayout());						JPanel mainPanel = new JPanel();				JPanel leftPanel = new JPanel();		geneticsHistoryList = 			new GeneticsHistoryList(new DefaultListModel(), mge);		histListScrollPane = new JScrollPane(geneticsHistoryList);		histListScrollPane.setBorder(				BorderFactory.createTitledBorder("History List"));		histListScrollPane.setMaximumSize(new Dimension(150,1000));		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));		leftPanel.add(Box.createRigidArea(new Dimension(150,1)));		leftPanel.add(histListScrollPane);		geneticsMiddleButtonPanel = new GeneticsMiddleButtonPanel(this);		JPanel genexPanel = new JPanel();		genexPanel.setLayout(new GridLayout(2,1));		upperGeneticsWin = new GeneticsWindow(				Organism.UPPER_GENETICS_WINDOW, this);		lowerGeneticsWin = new GeneticsWindow(				Organism.LOWER_GENETICS_WINDOW, this);		genexPanel.add(upperGeneticsWin);		genexPanel.add(lowerGeneticsWin);		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));		mainPanel.add(leftPanel);		mainPanel.add(geneticsMiddleButtonPanel);		mainPanel.add(genexPanel);				add(mainPanel, BorderLayout.CENTER);	}		public MolGenExp getMolGenExp() {		return mge;	}		public GeneticsMiddleButtonPanel getGenMidButtonPanel() {		return geneticsMiddleButtonPanel;	}		public void addTrayToHistoryList(Tray tray) {		geneticsHistoryList.add(tray);		histListScrollPane.revalidate();		histListScrollPane.repaint();	}	public void sendSelectedTrayToUP() {		if (geneticsHistoryList.getSelectedValue() != null) {			upperGeneticsWin.setCurrentTray(					(Tray)geneticsHistoryList.getSelectedValue());			mge.clearSelectedOrganisms();			mge.updateSelectedOrganismDisplay();		}	}	public void sendSelectedTrayToLP() {		if (geneticsHistoryList.getSelectedValue() != null) {			lowerGeneticsWin.setCurrentTray(					(Tray)geneticsHistoryList.getSelectedValue());			mge.clearSelectedOrganisms();			mge.updateSelectedOrganismDisplay();		}	}	public void setCrossTwoButtonsEnabled(boolean b) {		upperGeneticsWin.setCrossTwoButtonEnabled(b);		lowerGeneticsWin.setCrossTwoButtonEnabled(b);	}		public void setSelfCrossButtonsEnabled(boolean b) {		upperGeneticsWin.setSelfCrossButtonEnabled(b);		lowerGeneticsWin.setSelfCrossButtonEnabled(b);	}		public void setMutateButtonsEnabled(boolean b) {		upperGeneticsWin.setMutateButtonEnabled(b);		lowerGeneticsWin.setMutateButtonEnabled(b);	}		public ColorModel getProteinColorModel() {		return colorModel;	}		public int getNextTrayNum() {		nextTrayNumber++;		return nextTrayNumber;	}		public GeneticsWindow getLowerGeneticsWindow() {		return lowerGeneticsWin;	}		public GeneticsWindow getUpperGeneticsWindow() {		return upperGeneticsWin;	}		public void setButtonsEnabled(boolean b) {		geneticsMiddleButtonPanel.setSendToGeneticsWindowButtonEnabled(b);	}}