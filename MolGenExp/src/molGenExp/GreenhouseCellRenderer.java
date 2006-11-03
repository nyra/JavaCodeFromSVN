package molGenExp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class GreenhouseCellRenderer extends JButton 
	implements ListCellRenderer {
	
	public GreenhouseCellRenderer() {
		super();
		setOpaque(true);
		setLayout(new BorderLayout());
	}

	public Component getListCellRendererComponent(JList list, 
			Object value, int index, boolean isSelected, boolean cellHasFocus) {
		Organism o = (Organism)value;
		JButton button = 
			new JButton(o.getName(), 
					o.getImage());
		button.setBackground(Color.LIGHT_GRAY);
		button.setBorder(BorderFactory.createLineBorder(
				isSelected ? Color.GREEN : Color.BLACK, 2));
		button.setVerticalTextPosition(AbstractButton.BOTTOM);
		button.setHorizontalTextPosition(AbstractButton.CENTER);
		
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getSource());
			}
			
		});

		return button;
	}

}