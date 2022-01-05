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
import java.awt.Panel;
import java.awt.GridLayout;
import java.awt.Toolkit;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.Locale;
import java.util.ResourceBundle;


import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.ImageIcon;

public class AboutBox extends JFrame implements ActionListener {
	private static final long serialVersionUID = 9143994418412032959L;
	
	private final JLabel aboutLabel[];
	private static final int labelCount = 6;
	private static final int aboutWidth = 280;
	private static final int aboutHeight = 230;
	private static int aboutTop = 200;
	private static int aboutLeft = 350;
	private Font titleFont, bodyFont;
	private final ResourceBundle resbundle;

	public AboutBox() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		aboutTop = Double.valueOf((screenSize.getHeight()/2) - (aboutHeight/2)).intValue();
		aboutLeft = Double.valueOf((screenSize.getWidth()/2) - (aboutWidth/2)).intValue();

		this.setResizable(false);
		resbundle = ResourceBundle.getBundle ("GuitarTeX2strings", Locale.getDefault());
		SymWindow aSymWindow = new SymWindow();
		this.addWindowListener(aSymWindow);	
	
		// 	Initialize useful fonts
		titleFont = new Font("Lucida Grande", Font.BOLD, 14);
		if (titleFont == null) {
			titleFont = new Font("SansSerif", Font.BOLD, 14);
		}
		bodyFont  = new Font("Lucida Grande", Font.PLAIN, 10);
		if (bodyFont == null) {
			bodyFont = new Font("SansSerif", Font.PLAIN, 10);
		}
	
		this.getContentPane().setLayout(new BorderLayout(30, 30));
		java.net.URL imgURL = AboutBox.class.getResource("/images/gitarre1.jpg");
		ImageIcon icon = new ImageIcon(imgURL, "");
		
		aboutLabel = new JLabel[labelCount];
		aboutLabel[0] = new JLabel(resbundle.getString("frameConstructor"));
		aboutLabel[0].setFont(titleFont);
		aboutLabel[1] = new JLabel(resbundle.getString("appVersion"));
		aboutLabel[1].setFont(bodyFont);
		aboutLabel[2] = new JLabel("");
		aboutLabel[3] = new JLabel("JDK " + System.getProperty("java.version"));
		aboutLabel[3].setFont(bodyFont);
		aboutLabel[4] = new JLabel(resbundle.getString("copyright"));
		aboutLabel[4].setFont(bodyFont);
		aboutLabel[5] = new JLabel("");
	
		Panel imagePanel = new Panel(new GridLayout(0,1));
		imagePanel.add(new JLabel(icon));
		Panel textPanel2 = new Panel(new GridLayout(labelCount, 1));
		for (int i = 0; i<labelCount; i++) {
			aboutLabel[i].setHorizontalAlignment(JLabel.CENTER);
			textPanel2.add(aboutLabel[i]);
		}
		this.getContentPane().add (imagePanel, BorderLayout.PAGE_START);
		this.getContentPane().add (textPanel2, BorderLayout.CENTER);
		this.pack();
		this.setTitle(resbundle.getString("aboutTitle"));
		this.setLocation(aboutLeft, aboutTop);
		this.setSize(aboutWidth, aboutHeight);
		this.setVisible(true);
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