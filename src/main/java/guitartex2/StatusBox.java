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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.Toolkit;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.ResourceBundle;


import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JFrame;




public class StatusBox extends JFrame implements ActionListener {
	private static final long serialVersionUID = 8854494445995604753L;

	private Font titleFont, bodyFont;
	
	private static int statusWidth = 280;
	private static int statusHeight = 100;
	private static int statusTop;
	private static int statusLeft;
	
	private ResourceBundle resbundle;
	private final JLabel myJLStatus;
	
	public StatusBox() {
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
		
		java.net.URL imgURL = StatusBox.class.getResource("/images/info.png");
		ImageIcon icon = new ImageIcon(imgURL, "");
		
		Panel textPanel = new Panel(new GridBagLayout());
		Panel imagePanel = new Panel(new GridBagLayout());
		
		myJLStatus = new JLabel();
		
		imagePanel.add(new JLabel(icon));

		textPanel.add(new JLabel());
		textPanel.add(myJLStatus);
		textPanel.add(new JLabel());

		this.getContentPane().add (imagePanel, BorderLayout.PAGE_START);
		this.getContentPane().add (textPanel, BorderLayout.CENTER);
		
		this.pack();
		this.setTitle(resbundle.getString("statusTitle"));
		this.setLocation(statusLeft, statusTop);
		this.setSize(statusWidth, statusHeight);
	}
	
	public StatusBox(String myStatus) {
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
		
		Panel textPanel = new Panel(new GridBagLayout());
		
		myJLStatus = new JLabel(myStatus);
		
		textPanel.add(new JLabel());
		textPanel.add(myJLStatus);
		textPanel.add(new JLabel());

		this.getContentPane().add (textPanel, BorderLayout.CENTER);
		
		this.pack();
		this.setLocation(statusLeft, statusTop);
		this.setSize(statusWidth, statusHeight);
		this.setVisible(true);
	}
	
	public void setStatus(String myStatus) {
		myJLStatus.setText(myStatus);
		setVisible(true);
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