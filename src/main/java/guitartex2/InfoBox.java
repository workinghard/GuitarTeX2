/* 
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package guitartex2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;



class InfoBox extends JFrame implements ActionListener{
	private static final long serialVersionUID = 671314678552270671L;

	private Font titleFont, bodyFont;
	
	private static final int statusWidth = 480;
	private static final int statusHeight = 200;
	private static int statusTop;
	private static int statusLeft;
	
	private final ResourceBundle resbundle;
	
	
	public InfoBox(String myInfo) {
		resbundle = ResourceBundle.getBundle ("GuitarTeX2strings", Locale.getDefault());
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		statusTop = new Double((screenSize.getHeight()/2) - (statusHeight/2)).intValue();
		statusLeft = new Double((screenSize.getWidth()/2) - (statusWidth/2)).intValue();
		
		this.setResizable(false);
		
		SymWindow aSymWindow = new SymWindow();
		this.addWindowListener(aSymWindow);
		
//	 	Initialize useful fonts
		titleFont = new Font("Lucida Grande", Font.BOLD, 14);
		if (titleFont == null) {
			titleFont = new Font("SansSerif", Font.BOLD, 14);
		}
		bodyFont  = new Font("Lucida Grande", Font.PLAIN, 10);
		if (bodyFont == null) {
			bodyFont = new Font("SansSerif", Font.PLAIN, 10);
		}
		
		Panel buttonPanel = new Panel(new GridBagLayout());
		
		JTextArea mShowArea = new JTextArea(myInfo);
		mShowArea.setEditable(false);
		JScrollPane scrollingShowArea = new JScrollPane(mShowArea);
		
		Action mOkAction = new buttonActionClass(resbundle.getString("okButton"));
		JButton mOkButton = new JButton(mOkAction);
        mOkButton.setActionCommand("mOkAction");
		
        buttonPanel.add(new JLabel());
        buttonPanel.add(mOkButton);
        buttonPanel.add(new JLabel());
        
		this.getContentPane().add (scrollingShowArea, BorderLayout.CENTER);
		this.getContentPane().add (buttonPanel, BorderLayout.PAGE_END);
		
		this.pack();
		this.setLocation(statusLeft, statusTop);
		this.setSize(statusWidth, statusHeight);
		this.setVisible(true);
	}
	
	public class buttonActionClass extends AbstractAction {		
		private static final long serialVersionUID = 3791659234149686228L;

		public buttonActionClass(String text) {
			super(text);
		}
                @Override
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}
	}
	
	class SymWindow extends java.awt.event.WindowAdapter {
                @Override
		public void windowClosing(java.awt.event.WindowEvent event) {
			setVisible(false);
		}
	}
	
        @Override
	public void actionPerformed(ActionEvent newEvent) {
		setVisible(false);
	}	
}