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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;



class GTXConsole extends JFrame implements ActionListener{
	private static final long serialVersionUID = -5838495111759011319L;

	private Font titleFont, bodyFont;
	
	private static final int statusWidth = 640;
	private static final int statusHeight = 480;
	private static int statusTop;
	private static int statusLeft;
	
	private final ResourceBundle resbundle;
	
	private final JTextArea mShowArea;
	//private JTextArea mInputArea;
	private final JTextField mInputTextField; 
	
	private GTXClient gtxClient;
	private Configurations myConf;
	
	private final JButton mInputButton;
	private final Action mInputAction; 
	
	//public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_NOW = "HH:mm:ss";

	
	public GTXConsole() {
		resbundle = ResourceBundle.getBundle ("GuitarTeX2strings", Locale.getDefault());
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		statusTop = Double.valueOf((screenSize.getHeight()/2) - (statusHeight/2)).intValue();
		statusLeft = Double.valueOf((screenSize.getWidth()/2) - (statusWidth/2)).intValue();
		
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
		
		Panel inputPanel = new Panel(new FlowLayout());
		Panel buttonPanel = new Panel(new GridBagLayout());
		
		mShowArea = new JTextArea(resbundle.getString("consoleInitText"));
		mShowArea.setEditable(false);
		JScrollPane scrollingShowArea = new JScrollPane(mShowArea);
		
		// InputPanel
		
		mInputAction = new buttonActionClass(resbundle.getString("consoleSendButton"));
		mInputButton = new JButton(mInputAction);
		//mInputButton.setActionCommand("mInputAction");

		
		//mInputArea = new JTextArea();
		mInputTextField = new JTextField();
		mInputTextField.setColumns(40);
		mInputTextField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), mInputAction);
		
		
		inputPanel.add(mInputTextField);
		inputPanel.add(mInputButton);
		
		mInputButton.setEnabled(false);
		mInputAction.setEnabled(false);
		
		// OK - Button

		Action mOkAction = new buttonActionClass(resbundle.getString("okButton"));
		JButton mOkButton = new JButton(mOkAction);
        //mOkButton.setActionCommand("mOkAction");
        mOkButton.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), mOkAction);
        
        buttonPanel.add(new JLabel());
        buttonPanel.add(mOkButton);
        buttonPanel.add(new JLabel());

		this.pack();
		this.getContentPane().add (scrollingShowArea, BorderLayout.CENTER);
		this.getContentPane().add (inputPanel, BorderLayout.PAGE_START);
		this.getContentPane().add (buttonPanel, BorderLayout.PAGE_END);
		
		this.setLocation(statusLeft, statusTop);
		this.setSize(statusWidth, statusHeight);
		this.setTitle(resbundle.getString("consoleTitle"));
		this.setVisible(false);
		
	}
	
	public void setGTXClient(Configurations mConf) {
		myConf = mConf;
		gtxClient = new GTXClient(myConf.getGtxServer(), myConf.getGtxServerPort());
		int openResult = gtxClient.openConnection();
		if ( openResult == 0 ) {
			int connResult = gtxClient.checkServerConnection();
			if ( connResult == 0) {
				mInputButton.setEnabled(true);
				mInputAction.setEnabled(true);
			}
		}
		gtxClient.closeConnection();
	}
	
	public class buttonActionClass extends AbstractAction {		
		private static final long serialVersionUID = 3791659234149686228L;

		private final String mAction;
		
		public buttonActionClass(String text) {
			super(text);
			mAction = text;
		}
                @Override
		public void actionPerformed(ActionEvent e) {
			if (mAction.equals(resbundle.getString("consoleSendButton"))) {
				// Nachricht anzeigen
				addText(mInputTextField.getText());
				
				// Nachricht senden
				gtxClient = new GTXClient(myConf.getGtxServer(), myConf.getGtxServerPort());
				int openResult = gtxClient.openConnection();
				if ( openResult == 0 ) {
					String receiveText = gtxClient.sendText(mInputTextField.getText());
					addText("Server: " + receiveText);
				}
				gtxClient.closeConnection();
				
				mInputTextField.setText("");
				mInputTextField.requestFocus();
			}
			
			// Fenster schliessen
			if (mAction.equals(resbundle.getString("okButton"))) {
				setVisible(false);
			}
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
	
	public void addText(String text) {
		String prefix = this.now() + " # ";
		text = text.replaceAll("\n", "\n" + prefix);
		mShowArea.setText(mShowArea.getText() + "\n" + prefix + text);
		// TODO: Make silent output configurable
		// Uncomment this line if you don't want console output
		System.out.println(text);
	}
	
    private String now() {
    	Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
    }

}