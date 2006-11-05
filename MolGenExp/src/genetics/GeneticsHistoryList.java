package genetics;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import molGenExp.MolGenExp;

import biochem.FoldedPolypeptide;

public class GeneticsHistoryList extends JList {
	
	DefaultListModel histListDataModel;
	
	public GeneticsHistoryList(ListModel dataModel, final MolGenExp mge) {
		super(dataModel);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setCellRenderer(new GeneticsHistoryCellRenderer());
		histListDataModel = (DefaultListModel)dataModel;
		this.setFixedCellWidth(20);
		this.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if (getValueIsAdjusting()) {
					return;
				}
				if (isSelectionEmpty()) {
					mge.getGeneticsWorkshop().setButtonsEnabled(false);
				} else {
					mge.getGeneticsWorkshop().setButtonsEnabled(true);
				}
			}
		});
	}
	
	public void add(Tray tray) {
		histListDataModel.addElement(tray);
	}
	
	public void deleteSelected() {
		if (getSelectedIndex() != -1 ) {
			histListDataModel.removeElementAt(getSelectedIndex());
		} else {
			JOptionPane.showMessageDialog(null, "You have not selected an "
					+ "item to delete.",
					"None Selected", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	public void clearList() {
		histListDataModel.removeAllElements();
	}
	
	public Object[] getAll() {
		return histListDataModel.toArray();
	}

}
