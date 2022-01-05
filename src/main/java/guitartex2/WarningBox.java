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

//import java.awt.Dimension;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.Panel;
//import java.awt.Toolkit;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.ResourceBundle;


import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JFrame;



public class WarningBox extends JFrame implements ActionListener {
	private static final long serialVersionUID = -6792186354255191963L;

	private Font titleFont, bodyFont;
	
	private static final int warnWidth = 330;
	private static final int warnHeight = 150;
	private static int warnTop;
	private static int warnLeft;
	
	private final JLabel myJLStatus;
	private final String myInfo;
	
	private final Action mOkAction, mInfoAction, mQuitAction;
	private final ResourceBundle resbundle;
	
	public WarningBox(String myStatus, String mInfo) {
		resbundle = ResourceBundle.getBundle ("GuitarTeX2strings", Locale.getDefault());
		
		myInfo = mInfo;
		
		/* TODO: Bring WarnBox always on top
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		/warnTop = new Double((screenSize.getHeight()/2) - (warnHeight/2)).intValue();
		warnLeft = new Double((screenSize.getWidth()/2) - (warnWidth/2)).intValue(); 
		*/
		warnTop = 0;
		warnLeft = 0;
		
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
		java.net.URL imgURL = WarningBox.class.getResource("images/info.png");
		ImageIcon icon = new ImageIcon(imgURL, "");
		Panel imagePanel = new Panel(new GridBagLayout());
		Panel textPanel = new Panel(new GridBagLayout());
		Panel buttonPanel = new Panel(new GridBagLayout());
		
		myJLStatus = new JLabel(myStatus);
		
		imagePanel.add(new JLabel(icon));
		
		JLabel spaceLabel = new JLabel();
		
		textPanel.add(spaceLabel);
		textPanel.add(myJLStatus);
		textPanel.add(spaceLabel);

		mOkAction = new buttonActionClass(resbundle.getString("okButton"));
		JButton mOkButton = new JButton(mOkAction);
        mOkButton.setActionCommand("mOkAction");
		
        mInfoAction = new buttonActionClass(resbundle.getString("infoButton"));
        JButton mInfoButton = new JButton(mInfoAction);
        mInfoButton.setActionCommand("mInfoAction");
        
        mQuitAction = new buttonActionClass(resbundle.getString("quitButton"));
        JButton mQuitButton = new JButton(mQuitAction);
        mQuitButton.setActionCommand("mQuitAction");
        
        buttonPanel.add(mQuitButton);
        buttonPanel.add(mInfoButton);
        buttonPanel.add(mOkButton);
        
		this.getContentPane().add (imagePanel, BorderLayout.PAGE_START);
		this.getContentPane().add (textPanel, BorderLayout.CENTER);
		this.getContentPane().add (buttonPanel, BorderLayout.PAGE_END);
		
		this.pack();
		this.setLocation(warnLeft, warnTop);
		this.setSize(warnWidth, warnHeight);
		this.setTitle(resbundle.getString("warnTitle"));
		this.setVisible(true);
		//this.setResizable(true);
	}
	
	
	public class buttonActionClass extends AbstractAction {		
		private static final long serialVersionUID = 3791659234149686228L;

		public buttonActionClass(String text) {
			super(text);
		}
                @Override
		public void actionPerformed(ActionEvent e) {
			String mAction = e.getActionCommand();
			if ( mAction.equals("mQuitAction")) {
				System.exit(1);
			}		
			if ( mAction.equals("mOkAction")) {
				setVisible(false);
				setEnabled(false);
			}
			if ( mAction.equals("mInfoAction")) {
                new InfoBox(myInfo);
			}
		}
	}
	
	class SymWindow extends java.awt.event.WindowAdapter {
                @Override
		public void windowClosing(java.awt.event.WindowEvent event) {
			setVisible(false);
			setEnabled(false);
		}
	}

        @Override
	public void actionPerformed(ActionEvent newEvent) {
		setVisible(false);
		setEnabled(false);
	}		
}