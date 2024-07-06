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

import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class PreferencesBox extends JFrame implements ActionListener {

    private static final long serialVersionUID = -7086306896538924587L;

    private Font titleFont, bodyFont;
    private final ResourceBundle resbundle;
    private final Configurations myConfiguration;

    //private Action mLatexAction, mXDviAction, mPdfLatexAction; 
    private final Action mPdfViewerAction, mTmpPathAction;
    private JTextField mLatexField, mXDviField, mPdfLatexField;
    private final JTextField mPdfViewerField;
    private final JTextField mTmpPathField;
    private final JTextField mGtxServerField, mGtxServerPortField;
    private final JButton tex2pdfButton;
    private final Action tex2pdfAction;

    private final String fileSeparator = System.getProperty("file.separator");

    PreferencesBox(Configurations myConf, JButton mTeX2PdfButton, Action mTeX2PdfAction) {
        myConfiguration = myConf;
        tex2pdfButton = mTeX2PdfButton;
        tex2pdfAction = mTeX2PdfAction;

        resbundle = ResourceBundle.getBundle("GuitarTeX2strings", Locale.getDefault());
        SymWindow aSymWindow = new SymWindow();
        this.addWindowListener(aSymWindow);

        // 	Initialize useful fonts
        titleFont = new Font("Lucida Grande", Font.BOLD, 14);
        if (titleFont == null) {
            titleFont = new Font("SansSerif", Font.BOLD, 14);
        }
        bodyFont = new Font("Lucida Grande", Font.PLAIN, 10);
        if (bodyFont == null) {
            bodyFont = new Font("SansSerif", Font.PLAIN, 10);
        }

		// Show local configuration
        //Create and populate the panel.
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        //int numPairs = 9;
        int numPairs = 6;
        int fieldLength = 30;

        JPanel p = new JPanel(new SpringLayout());

        // External programs
        /*
         JLabel mLatexLabel = new JLabel(resbundle.getString("mPrefLatex"), JLabel.TRAILING);
         p.add(mLatexLabel);
         mLatexField = new JTextField(fieldLength);
         mLatexField.setText(myConfiguration.getLatex());
         mLatexLabel.setLabelFor(mLatexField);
         p.add(mLatexField);
         mLatexAction = new chooseFileActionClass(resbundle.getString("choose"));
         JButton mLatexButton = new JButton(mLatexAction);
         mLatexButton.setActionCommand("mLatexAction");
         p.add(mLatexButton);
        
         JLabel mXDviLabel = new JLabel(resbundle.getString("mPrefXDvi"), JLabel.TRAILING);
         p.add(mXDviLabel);
         mXDviField = new JTextField(fieldLength);
         mXDviField.setText(myConfiguration.getXDvi());
         mXDviLabel.setLabelFor(mXDviField);
         p.add(mXDviField);
         mXDviAction = new chooseFileActionClass(resbundle.getString("choose"));
         JButton mXDviButton = new JButton(mXDviAction);
         mXDviButton.setActionCommand("mXDviAction");
         p.add(mXDviButton);

         JLabel mPdfLatexLabel = new JLabel(resbundle.getString("mPrefPdfLatex"), JLabel.TRAILING);
         p.add(mPdfLatexLabel);
         mPdfLatexField = new JTextField(fieldLength);
         mPdfLatexField.setText(myConfiguration.getPdfLatex());
         mPdfLatexLabel.setLabelFor(mPdfLatexField);
         p.add(mPdfLatexField);
         mPdfLatexAction = new chooseFileActionClass(resbundle.getString("choose"));
         JButton mPdfLatexButton = new JButton(mPdfLatexAction);
         mPdfLatexButton.setActionCommand("mPdfLatexAction");
         p.add(mPdfLatexButton);
         */
        JLabel mPdfViewerLabel = new JLabel(resbundle.getString("mPrefPdfViewer"), JLabel.TRAILING);
        p.add(mPdfViewerLabel);
        mPdfViewerField = new JTextField(fieldLength);
        mPdfViewerField.setText(myConfiguration.getPdfViewer());
        mPdfViewerLabel.setLabelFor(mPdfViewerField);
        p.add(mPdfViewerField);
        mPdfViewerAction = new chooseFileActionClass(resbundle.getString("choose"));
        JButton mPdfViewerButton = new JButton(mPdfViewerAction);
        mPdfViewerButton.setActionCommand("mPdfViewerAction");
        p.add(mPdfViewerButton);

        // Spaces
        p.add(new JLabel());
        p.add(new JLabel());
        p.add(new JLabel());

        // Tmp Path
        JLabel mTmpPathLabel = new JLabel(resbundle.getString("mPrefTmpPath"), JLabel.TRAILING);
        p.add(mTmpPathLabel);
        mTmpPathField = new JTextField(fieldLength);
        mTmpPathField.setText(myConfiguration.getTmpDir());
        mTmpPathLabel.setLabelFor(mTmpPathField);
        p.add(mTmpPathField);
        mTmpPathAction = new chooseDirActionClass(resbundle.getString("choose"));
        JButton mTmpPathButton = new JButton(mTmpPathAction);
        mTmpPathButton.setActionCommand("mTmpPathAction");
        p.add(mTmpPathButton);

        // Space
        p.add(new JLabel());
        p.add(new JLabel());
        p.add(new JLabel());

        // Remote connection
        JLabel mGtxServerLabel = new JLabel(resbundle.getString("mPrefGtxServer"), JLabel.TRAILING);
        p.add(mGtxServerLabel);
        mGtxServerField = new JTextField(fieldLength);
        mGtxServerField.setText(myConfiguration.getGtxServer());
        mGtxServerLabel.setLabelFor(mGtxServerField);
        p.add(mGtxServerField);
        p.add(new JLabel());

        JLabel mGtxServerPortLabel = new JLabel(resbundle.getString("mPrefGtxServerPort"), JLabel.TRAILING);
        p.add(mGtxServerPortLabel);
        mGtxServerPortField = new JTextField(fieldLength);
        int portNr = myConfiguration.getGtxServerPort();
        if ( portNr > 0 ) {
            mGtxServerPortField.setText(Integer.toString(portNr));
        }
        mGtxServerPortLabel.setLabelFor(mGtxServerPortField);
        p.add(mGtxServerPortField);
        p.add(new JLabel());

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));

        // Create Buttons
        JButton resetButton = new JButton(resbundle.getString("resetButton"));
        resetButton.setActionCommand("resetButtonPressed");
        resetButton.addActionListener(this);
        JButton okButton = new JButton(resbundle.getString("okButton"));
        okButton.setActionCommand("okButtonPressed");
        okButton.addActionListener(this);
        JButton cancelButton = new JButton(resbundle.getString("cancelButton"));
        cancelButton.setActionCommand("cancelButtonPressed");
        cancelButton.addActionListener(this);
        buttonsPanel.add(resetButton);
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(okButton);

        mainPanel.add(p);
        mainPanel.add(buttonsPanel);

        //Lay out the panel.
        SpringUtilities.makeCompactGrid(p,
                numPairs, 3, //rows, cols
                6, 6, //initX, initY
                6, 6);       //xPad, yPad

        //Create and set up the window.
        p.setOpaque(true);  //content panes must be opaque
        buttonsPanel.setOpaque(true);
        mainPanel.setOpaque(true);

        this.setResizable(false);
        this.getContentPane().add(mainPanel);
        this.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int aboutTop = Double.valueOf((screenSize.getHeight() / 2) - (this.getHeight() / 2)).intValue();
        int aboutLeft = Double.valueOf((screenSize.getWidth() / 2) - (this.getWidth() / 2)).intValue();

        this.setTitle(resbundle.getString("prefTitle"));
        this.setLocation(aboutLeft, aboutTop);
        this.setVisible(true);
    }

    public class chooseFileActionClass extends AbstractAction {

        private static final long serialVersionUID = 6483849350866369203L;

        public chooseFileActionClass(String text) {
            super(text);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser mFileChooser = new JFileChooser(System.getProperty("."));
            int retval = mFileChooser.showOpenDialog(PreferencesBox.this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                String actionCommand = e.getActionCommand();
                if (actionCommand.equals("mLatexAction")) {
                    mLatexField.setText(mFileChooser.getSelectedFile().getAbsolutePath());
                }
                if (actionCommand.equals("mXDviAction")) {
                    mXDviField.setText(mFileChooser.getSelectedFile().getAbsolutePath());
                }
                if (actionCommand.equals("mPdfLatexAction")) {
                    mPdfLatexField.setText(mFileChooser.getSelectedFile().getAbsolutePath());
                }
                if (actionCommand.equals("mPdfViewerAction")) {
                    mPdfViewerField.setText(mFileChooser.getSelectedFile().getAbsolutePath());
                }
                if (actionCommand.equals("mTmpPathAction")) {
                    mTmpPathField.setText(mFileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        }
    }

    public class chooseDirActionClass extends AbstractAction {

        private static final long serialVersionUID = 6483849350866369203L;

        public chooseDirActionClass(String text) {
            super(text);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser mFileChooser = new JFileChooser(System.getProperty("."));
            mFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int retval = mFileChooser.showOpenDialog(PreferencesBox.this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                String actionCommand = e.getActionCommand();
                if (actionCommand.equals("mTmpPathAction")) {
                    mTmpPathField.setText(mFileChooser.getSelectedFile().getAbsolutePath() + fileSeparator);
                }
            }
        }
    }

    private void setFields() {
		//myConfiguration.setLatex(mLatexField.getText());
        //myConfiguration.setXDvi(mXDviField.getText());
        //myConfiguration.setPdfLatex(mPdfLatexField.getText());
        myConfiguration.setPdfViewer(mPdfViewerField.getText());
        myConfiguration.setTmpDir(mTmpPathField.getText());
        myConfiguration.setGtxServer(mGtxServerField.getText());
        myConfiguration.setGtxServerPort(mGtxServerPortField.getText());
    }

    private void resetFields() {
        mPdfViewerField.setText(myConfiguration.getPdfViewer());
        mTmpPathField.setText(myConfiguration.getTmpDir());
        mGtxServerField.setText(myConfiguration.getGtxServer());
        int portNr = myConfiguration.getGtxServerPort();
        if ( portNr > 0 ) {
            mGtxServerPortField.setText(portNr + "");
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
        if (newEvent.getActionCommand().equals("resetButtonPressed")) {
            myConfiguration.loadDefaults();
            resetFields();
        }

        if (newEvent.getActionCommand().equals("okButtonPressed")) {
            setFields();
            myConfiguration.saveSettings();
            GTXClient gtxClient = new GTXClient(myConfiguration.getGtxServer(), myConfiguration.getGtxServerPort());
            gtxClient.setGTXConsole(myConfiguration.getConsole());
            int connResult = gtxClient.checkServerConnection();
            if (connResult == 0) {
                tex2pdfButton.setEnabled(true);
                tex2pdfAction.setEnabled(true);
            } else {
                tex2pdfButton.setEnabled(false);
                tex2pdfAction.setEnabled(false);
            }
            setVisible(false);
        }
        if (newEvent.getActionCommand().equals("cancelButtonPressed")) {
            setVisible(false);
        }
    }
}
