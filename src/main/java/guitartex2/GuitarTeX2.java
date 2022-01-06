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

import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;

// Naechster Schritt:
//
//  TODO: (1) Direktive (preamble) implementieren
//            {preamble:usepackage(fancyheadings)}
//            erm�glicht das einbinden von Packeten in den Headerfile
//  TODO: (1) Header komplett editierbar machen
//  TODO: (2) TeX2PDF Funktion beim Wechseln zur TeX-Vorschau aufrufen
//  TODO: (2) Warnmeldung falls der Server nicht erreichbar
//  TODO: (3) Formatierung fue Akkorde erlauben
//  TODO: (5) Syntax - Highlighting
public class GuitarTeX2 extends JFrame {

    private static final long serialVersionUID = 4527297141114166549L;

	// Highlighter
    //private SyntaxHighlighter mSynHighlighter;
    //-- Components 
    private JTabbedPane tabbedPane;
    //private JTextArea mEditArea;
    private JTextPane mEditArea;
    private JTextPane mShowTeXArea;
    private JToolBar mActionToolBar;
    private JToolBar mChordsToolBar;
    private JToolBar mTabsToolBar;
    private JToolBar mStructToolBar;
    private JToolBar mBookToolBar;
    private JToolBar mHarpToolBar;
    private JPanel mToolBars;
    private JTextField mFretBar;
    private AboutBox aboutBox;
    private PreferencesBox preferencesBox;
    private GTXParser mGTXParser;

    private JButton mNewButton, mOpenButton, mSaveButton, mSaveAsButton;
    private JButton mGtx2TeXButton, mTeX2PdfButton;
    //private JButton mLatexButton, mDvi2PsButton, mDvi2PdfButton;
    private JToggleButton mHarpUp, mHarpDown;
    // -- Status
    private boolean mFileChanged = false;
    private String mActFileName;

    private static JFileChooser mFileChooser = new JFileChooser(System.getProperty("user.home"));
    private ResourceBundle resbundle;

    //-- Actions
    private Action mNewAction;
    private Action mOpenAction;
    private Action mSaveAction;
    private Action mSaveAsAction;
    private Action mPrefAction;
    private Action mCopyAction;
    private Action mPasteAction;
    private Action mGTX2TeXAction;
	//private Action mLatexAction;
    //private Action mDvi2PsAction;
    //private Action mDvi2PdfAction;
    private Action mTeX2PdfAction;
    private Action mExitAction;
    private Action mConsoleAction;
    private Action mFAQAction, mShortcutAction;
    private Action mAboutAction;

    private Action mOpenTemplateSong1Action, mOpenTemplateSong2Action, mOpenTemplateBookAction;

    private static int gtxWidth;
    private static int gtxHeight;
    private static int gtxTop;
    private static int gtxLeft;

    private String mCopyBuffer;

    private Configurations myConf;
    //private GTXClient gtxClient;
    private int id;

    private GTXConsole consoleBox;
    private StatusBox myStatusBox;

//	int a = Rnd();
	// TODO: .gtx - Datei durch Doppelclick in Windows/Mac OS �ffnen 
    //===================================================================== main
    public static void main(String[] args) {
        String help = "Usage: \n"
                + "        GuitarTeX2  -h               help (this screen)\n"
                + "        GuitarTeX2  -o <file.gtx>    open a gtx file"
                + "\n";

        if (args.length != 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("--help") || args[i].equals("-help") || args[i].equals("-h") || args[i].equals("--h")) {
                    System.out.println(help);
                    System.exit(0);
                }
                /*
                 if ( args[i].equals("-f") || args[i].equals("--f") || args[i].equals("-file") || args[i].equals("--file") ) {
                 if ( i+1 < args.length) {
                 String fileName = args[i+1];
                 if ( !fileName.equals("")) {
                 File f = new File(fileName);
                 if ( f.canRead() ) {
                 new GuitarTeX2(f);
                 }else{
                 System.out.println("error: can't read file\n");
                 System.out.println(help);
                 System.exit(1);
                 }
                 }else{
                 System.out.println(help);
                 System.exit(1);
                 }
                 }else{
                 System.out.println("error: no file found\n");
                 System.out.println(help);
                 System.exit(1);
                 }
                 }*/
                if (args[i].equals("-o") || args[i].equals("--o") || args[i].equals("-open") || args[i].equals("--open")) {
                    if (i + 1 < args.length) {
                        String fileName = args[i + 1];
                        if (!fileName.equals("")) {
                            File f = new File(fileName);
                            if (f.canRead()) {
                                GuitarTeX2 openG = new GuitarTeX2();
                                openG.openDirectFile(f);
                                openG.setVisible(true);
                            } else {
                                System.out.println("error: can't read file\n");
                                System.out.println(help);
                                System.exit(1);
                            }
                        } else {
                            System.out.println(help);
                            System.exit(1);
                        }
                    } else {
                        System.out.println("error: no file found\n");
                        System.out.println(help);
                        System.exit(1);
                    }
                } else {
                    if (args.length > 0) {
                        String fileName = args[i];
                        if (!fileName.equals("")) {
                            File f = new File(fileName);
                            if (f.canRead()) {
                                GuitarTeX2 openG = new GuitarTeX2();
                                openG.openDirectFile(f);
                                openG.setVisible(true);
                            } else {
                                System.out.println("error: can't read file\n");
                                System.out.println(help);
                                System.exit(1);
                            }
                        } else {
                            System.out.println(help);
                            System.exit(1);
                        }
                    } else {
                        System.out.println("error: no file found\n");
                        System.out.println(help);
                        System.exit(1);
                    }
                }
            }
        } else {
            new GuitarTeX2().setVisible(true);
        }

    }//end main

    //============================================================== constructor
    public GuitarTeX2() {
        SymWindow aSymWindow = new SymWindow();
        this.addWindowListener(aSymWindow);

        mFileChooser.addChoosableFileFilter(new MyFilter());

        // create Console
        consoleBox = new GTXConsole();
        consoleBox.setVisible(false);

        resbundle = ResourceBundle.getBundle("GuitarTeX2strings", Locale.getDefault());
        myConf = new Configurations(consoleBox);

        //myConf.checkConfig();
        if (myConf.checkConfig() == false) {
            new WarningBox(resbundle.getString("confFailed"), myConf.getConfProblems());
            System.err.println("loading configuration failed!");
        }
        consoleBox.setGTXClient(myConf);

        createActions();
        this.setContentPane(new contentPanel());
        this.setJMenuBar(createMenuBar());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(resbundle.getString("frameConstructor"));
        this.pack();

		//gtxHeight = this.getHeight();
        //gtxWidth = this.getWidth();
        gtxHeight = 650;
        gtxWidth = 950;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //gtxTop = new Double((screenSize.getHeight()/2) - (gtxHeight/2)).intValue();
        gtxTop = 0;
        gtxLeft = Double.valueOf((screenSize.getWidth() / 2) - (gtxWidth / 2)).intValue();

        this.setLocation(gtxLeft, gtxTop);
        this.setSize(gtxWidth, gtxHeight);
        //this.setSize(850,650);

        GTXClient gtxClient = new GTXClient(myConf.getGtxServer(), myConf.getGtxServerPort());
        gtxClient.setGTXConsole(consoleBox);
        int openResult = gtxClient.openConnection();
        if (openResult == 0) {
            int connResult = gtxClient.checkServerConnection();
            if (connResult == 0) {
                mTeX2PdfButton.setEnabled(true);
                mTeX2PdfAction.setEnabled(true);
            }
        }
        gtxClient.closeConnection();

        // Create StatusBox
        myStatusBox = new StatusBox();

        Random generator = new Random();
        id = generator.nextInt();
        this.mEditArea.requestFocus(); // Set Focus to EditArea
    }//end constructor

    public GuitarTeX2(File f) {
        /*		try {
         FileReader reader = new FileReader(f);
         //   		mSynHighlighter = new SyntaxHighlighter();
         //			mEditArea = new JTextPane(mSynHighlighter.getDocument());
         //--Scanner scanner = new TextScanner();
         //--mEditArea = new SyntaxHighlighter(24, 80, scanner);
         mEditArea = new JTextPane();
         mEditArea.setContentType("text/html");
         mEditArea.read(reader, "");	
    		
         mGTXParser = new GTXParser(mEditArea.getText());
         mGTXParser.convertToTeX();
         System.out.println(mGTXParser.getMyTeXFile());
         } 
         catch (Exception e) {
         System.err.println("File input error:"+e);
         } */
        // TODO: Datei oeffnen
    }

    public void openDirectFile(File f) {
        try {
            FileReader reader = new FileReader(f);
            mEditArea.read(reader, "");  // Use TextComponent read
            mEditArea.getDocument().addDocumentListener(new MyDocumentListener());
            mEditArea.setContentType("text/html");
            mActFileName = f.getName();
            tabbedPane.setTitleAt(0, mActFileName);
            //highlight(mEditArea, "a");
            mShowTeXArea.setText("");
            mShowTeXArea.setEnabled(false);
            mShowTeXArea.setVisible(false);
            mShowTeXArea.setCaretPosition(0);
            tabbedPane.setEnabledAt(1, false);
            mFileChanged = false;
        } catch (IOException ioex) {
            System.out.println(ioex);
            System.exit(1);
        }
    }

    class MyFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File file) {
            String filename = file.getName();
            return filename.toLowerCase().endsWith(".gtx") || filename.toLowerCase().endsWith(".gtb") || file.isDirectory();
        }

        @Override
        public String getDescription() {
            return "*.gtx;*.gtb";
        }
    }

    ///////////////////////////////////////////////////////// class contentPanel
    private class contentPanel extends JPanel {

        private static final long serialVersionUID = -600167277476193108L;

        //========================================================== constructor
        contentPanel() {
            //-- Create components.
            mToolBars = new JPanel(new GridLayout(2, 2));
            JPanel mToolBar1 = new JPanel(new FlowLayout(FlowLayout.LEADING));
            JPanel mToolBar2 = new JPanel(new FlowLayout(FlowLayout.LEADING));
            //mToolBars = new JPanel(new GridBagLayout());
            mActionToolBar = new JToolBar(resbundle.getString("actionToolBar"));
            mActionToolBar.setFloatable(false);
            // -- // -- New
            URL mNewImageURL = GuitarTeX2.class.getResource("/images/filenew.png");
            mNewButton = mActionToolBar.add(mNewAction);
            mNewButton.setIcon(new ImageIcon(mNewImageURL));
            mNewButton.setText("");
            mNewButton.setToolTipText(resbundle.getString("newItem"));
            // -- // -- Open
            URL mOpenImageURL = GuitarTeX2.class.getResource("/images/fileopen.png");
            mOpenButton = mActionToolBar.add(mOpenAction);
            mOpenButton.setIcon(new ImageIcon(mOpenImageURL));
            mOpenButton.setText("");
            mOpenButton.setToolTipText(resbundle.getString("openItem"));
            // -- // -- Save
            URL mSaveImageURL = GuitarTeX2.class.getResource("/images/filesave.png");
            mSaveButton = mActionToolBar.add(mSaveAction);
            mSaveButton.setIcon(new ImageIcon(mSaveImageURL));
            mSaveButton.setText("");
            mSaveButton.setToolTipText(resbundle.getString("saveItem"));
            // -- // -- SaveAs
            URL mSaveAsImageURL = GuitarTeX2.class.getResource("/images/filesaveas.png");
            mSaveAsButton = mActionToolBar.add(mSaveAsAction);
            mSaveAsButton.setIcon(new ImageIcon(mSaveAsImageURL));
            mSaveAsButton.setText("");
            mSaveAsButton.setToolTipText(resbundle.getString("saveAsItem"));
            // -- // -- GTX2TeX
            URL mGTX2TeXURL = GuitarTeX2.class.getResource("/images/gtx2tex.png");
            mGtx2TeXButton = mActionToolBar.add(mGTX2TeXAction);
            mGtx2TeXButton.setIcon(new ImageIcon(mGTX2TeXURL));
            mGtx2TeXButton.setText("");
            mGtx2TeXButton.setToolTipText(resbundle.getString("gtx2tex"));
			// -- // -- LaTeX
            //URL mLatexURL = GuitarTeX2.class.getResource("images/latex.png");
            //mLatexButton = mActionToolBar.add(mLatexAction);
            //mLatexButton.setIcon(new ImageIcon(mLatexURL));
            //mLatexButton.setText("");
            //mLatexButton.setToolTipText(resbundle.getString("latex"));
            //mLatexButton.setEnabled(false);
            // -- // -- DVI2PS
            //URL mDvi2PsURL = GuitarTeX2.class.getResource("images/dvips.png");
            //mDvi2PsButton = mActionToolBar.add(mDvi2PsAction);
            //mDvi2PsButton.setIcon(new ImageIcon(mDvi2PsURL));
            //mDvi2PsButton.setText("");
            //mDvi2PsButton.setToolTipText(resbundle.getString("dvi2ps"));
            //mDvi2PsButton.setEnabled(false);
            //	-- // -- DVI2PDF
            //URL mDvi2PdfURL = GuitarTeX2.class.getResource("images/dvipdf.png");
            //mDvi2PdfButton = mActionToolBar.add(mDvi2PdfAction);
            //mDvi2PdfButton.setIcon(new ImageIcon(mDvi2PdfURL));
            //mDvi2PdfButton.setText("");
            //mDvi2PdfButton.setToolTipText(resbundle.getString("dvi2pdf"));
            //mDvi2PdfButton.setEnabled(false);
            // --  // --  TEX2PDF
            URL mTeX2PdfURL = GuitarTeX2.class.getResource("/images/tex2pdf.png");
            mTeX2PdfButton = mActionToolBar.add(mTeX2PdfAction);
            mTeX2PdfButton.setIcon(new ImageIcon(mTeX2PdfURL));
            mTeX2PdfButton.setText("");
            mTeX2PdfButton.setToolTipText(resbundle.getString("tex2pdf"));
            mTeX2PdfButton.setEnabled(false);

            mEditArea = new JTextPane();
            mEditArea.setContentType("text/plain");
            // Bugfix to change char encoding for JTextArea
            mEditArea.setText("Initialize UTF8 Br\u00E2ncu\u015Fi");
            mEditArea.setText("");
            mEditArea.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            mEditArea.setFont(new Font("monospaced", Font.PLAIN, 14));
            mEditArea.getDocument().addDocumentListener(new MyDocumentListener());

            mChordsToolBar = new JToolBar(resbundle.getString("chordsToolBar"));
            mChordsToolBar.add(createChordButton(new mChordButtonClass(resbundle.getString("mToneA")), KeyEvent.VK_A));
            mChordsToolBar.add(createChordButton(new mChordButtonClass(resbundle.getString("mToneAs"))));
            mChordsToolBar.add(createChordButton(new mChordButtonClass(resbundle.getString("mToneH")), KeyEvent.VK_H));
            mChordsToolBar.add(createChordButton(new mChordButtonClass(resbundle.getString("mToneC")), KeyEvent.VK_C));
            mChordsToolBar.add(createChordButton(new mChordButtonClass(resbundle.getString("mToneCis"))));
            mChordsToolBar.add(createChordButton(new mChordButtonClass(resbundle.getString("mToneD")), KeyEvent.VK_D));
            mChordsToolBar.add(createChordButton(new mChordButtonClass(resbundle.getString("mToneDis"))));
            mChordsToolBar.add(createChordButton(new mChordButtonClass(resbundle.getString("mToneE")), KeyEvent.VK_E));
            mChordsToolBar.add(createChordButton(new mChordButtonClass(resbundle.getString("mToneF")), KeyEvent.VK_F));
            mChordsToolBar.add(createChordButton(new mChordButtonClass(resbundle.getString("mToneFis"))));
            mChordsToolBar.add(createChordButton(new mChordButtonClass(resbundle.getString("mToneG")), KeyEvent.VK_G));
            mChordsToolBar.add(createChordButton(new mChordButtonClass(resbundle.getString("mToneGis"))));

            mTabsToolBar = new JToolBar(resbundle.getString("tabsToolBar"));
            mTabsToolBar.add(new mStructButtonClass(resbundle.getString("mGuitarTab")));
            mFretBar = new JTextField(5);
            mFretBar.setMaximumSize(new Dimension(27, 27));
            mTabsToolBar.add(mFretBar);
            mTabsToolBar.add(new mNoteButtonClass(resbundle.getString("mToneE")));
            mTabsToolBar.add(new mNoteButtonClass(resbundle.getString("mToneA")));
            mTabsToolBar.add(new mNoteButtonClass(resbundle.getString("mToneD")));
            mTabsToolBar.add(new mNoteButtonClass(resbundle.getString("mToneG")));
            mTabsToolBar.add(new mNoteButtonClass(resbundle.getString("mToneH")));
            mTabsToolBar.add(new mNoteButtonClass(resbundle.getString("mTonee")));
            mTabsToolBar.addSeparator();
            mTabsToolBar.add(new mNoteStringButtonClass("&"));
            mTabsToolBar.add(new mNoteStringButtonClass("_"));
            mTabsToolBar.add(new mNoteStringButtonClass("|"));

            mHarpToolBar = new JToolBar(resbundle.getString("harpToolBar"));
            //mHarpToolBar.setLayout(new GridLayout(2,9));
            ImageIcon upButtonIcon = createImageIcon("/images/icon-up2.png");
            ImageIcon downButtonIcon = createImageIcon("/images/icon-down2.png");
            mHarpUp = new JToggleButton(upButtonIcon);
            Action pmUpAction = new mPMHarpActionClass("up");
            mHarpUp.addActionListener(pmUpAction);
            mHarpUp.setSelected(true);
            InputMap inputMap = mEditArea.getInputMap();
            KeyStroke keyUp = KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_DOWN_MASK);
            inputMap.put(keyUp, pmUpAction);

            mHarpDown = new JToggleButton(downButtonIcon);
            Action pmDownAction = new mPMHarpActionClass("down");
            mHarpDown.addActionListener(pmDownAction);
            KeyStroke keyDown = KeyStroke.getKeyStroke(KeyEvent.VK_J, KeyEvent.CTRL_DOWN_MASK);
            inputMap.put(keyDown, pmDownAction);

            JPanel pm = new JPanel();
            pm.setLayout(new GridLayout(2, 1, 0, 0));
            pm.setPreferredSize(new Dimension(18, 6));
            pm.add(mHarpUp);
            pm.add(mHarpDown);
            mHarpToolBar.add(pm);
            mHarpToolBar.add(createHarpButton(new mHarpButtonClass(resbundle.getString("mHarpTone1")), KeyEvent.VK_1));
            mHarpToolBar.add(createHarpButton(new mHarpButtonClass(resbundle.getString("mHarpTone2")), KeyEvent.VK_2));
            mHarpToolBar.add(createHarpButton(new mHarpButtonClass(resbundle.getString("mHarpTone3")), KeyEvent.VK_3));
            mHarpToolBar.add(createHarpButton(new mHarpButtonClass(resbundle.getString("mHarpTone4")), KeyEvent.VK_4));
            mHarpToolBar.add(createHarpButton(new mHarpButtonClass(resbundle.getString("mHarpTone5")), KeyEvent.VK_5));
            mHarpToolBar.add(createHarpButton(new mHarpButtonClass(resbundle.getString("mHarpTone6")), KeyEvent.VK_6));
            mHarpToolBar.add(createHarpButton(new mHarpButtonClass(resbundle.getString("mHarpTone7")), KeyEvent.VK_7));
            mHarpToolBar.add(createHarpButton(new mHarpButtonClass(resbundle.getString("mHarpTone8")), KeyEvent.VK_8));
            mHarpToolBar.add(createHarpButton(new mHarpButtonClass(resbundle.getString("mHarpTone9")), KeyEvent.VK_9));
            mHarpToolBar.add(createHarpButton(new mHarpButtonClass(resbundle.getString("mHarpTone10")), KeyEvent.VK_0));

            mStructToolBar = new JToolBar(resbundle.getString("structToolBar"));
            mStructToolBar.add(new mStructButtonClass(resbundle.getString("mLanguage")));
            mStructToolBar.add(new mStructButtonClass(resbundle.getString("mTitle")));
            mStructToolBar.add(new mStructButtonClass(resbundle.getString("mSubtitle")));
            mStructToolBar.add(new mStructButtonClass(resbundle.getString("mBold")));
            mStructToolBar.add(new mStructButtonClass(resbundle.getString("mBridge")));
            mStructToolBar.add(new mStructButtonClass(resbundle.getString("mChorus")));

            mBookToolBar = new JToolBar(resbundle.getString("mBookToolBar"));
            mBookToolBar.add(new mBookToolBarClass(resbundle.getString("mSetBook")));
            mBookToolBar.add(new mBookToolBarClass(resbundle.getString("mBookAuthor")));
            mBookToolBar.add(new mBookToolBarClass(resbundle.getString("mBookTitle")));
            mBookToolBar.add(new mBookToolBarClass(resbundle.getString("mBookDate")));
            mBookToolBar.add(new mBookToolBarClass(resbundle.getString("mBookInclude")));

            String[] languageStrings = {"english", "usenglishmax", "dumylang",
                "nohyphenation", "arabic", "farsi", "croatian", "ukrainian",
                "russian", "bulgarian", "czech", "slovak", "danish", "dutch",
                "finnish", "basque", "french", "german", "ngerman", "ibycus",
                "greek", "monogreek", "ancientgreek", "hungarian", "italian",
                "latin", "mongolian", "norsk", "icelandic", "interlingua",
                "turkish", "coptic", "romanian", "welsh", "serbian", "slovenian",
                "estonian", "esperanto", "uppersorbian", "indonesian", "polish",
                "portuguese", "spanish", "catalan", "galician", "swedish", "ukenglish"};
            java.util.Arrays.sort(languageStrings);

			// TODO: Syntax Highlight
            //mSynHighlighter = new SyntaxHighlighter();
            //mEditArea = new JTextPane(mSynHighlighter.getDocument());
            //--Scanner scanner = new TextScanner();
            //--mEditArea = new SyntaxHighlighter(24,80, scanner);
            mShowTeXArea = new JTextPane();
            mShowTeXArea.setContentType("text/plain");
            // Bugfix to change char encoding for JTextArea
            mShowTeXArea.setText("Initialize UTF8 Br\u00E2ncu\u015Fi");
            mShowTeXArea.setText("");
            mShowTeXArea.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            mShowTeXArea.setFont(new Font("monospaced", Font.PLAIN, 14));
            mShowTeXArea.setEditable(false);

			//mShowTeXArea.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
            //mShowTeXArea.setFont(new Font("monospaced", Font.PLAIN, 14));
            //mShowTeXArea.setEditable(false);
            //mShowTeXArea.setVisible(false);
            JScrollPane scrollingEditArea = new JScrollPane(mEditArea);
            JScrollPane scrollingShowTeXArea = new JScrollPane(mShowTeXArea);

            tabbedPane = new JTabbedPane();
            tabbedPane.addTab(resbundle.getString("mNewFile"), scrollingEditArea);
            tabbedPane.addTab(resbundle.getString("mShowTeXArea"), scrollingShowTeXArea);
            tabbedPane.setEnabledAt(1, false);

			//JScrollPane scrollingText = new JScrollPane(tabbedPane);
            mToolBar1.add(mActionToolBar);
            mToolBar1.add(mChordsToolBar);
            mToolBar1.add(mTabsToolBar);
            mToolBar2.add(mStructToolBar);
            mToolBar2.add(mBookToolBar);
            mToolBar2.add(mHarpToolBar);
            mToolBars.add(mToolBar1);
            mToolBars.add(mToolBar2);

            /*
             mActionToolBar.setFloatable(false);
             mTabsToolBar.setFloatable(false);
             mChordsToolBar.setFloatable(false);
             mStructToolBar.setFloatable(false);
             mBookToolBar.setFloatable(false);
             mHarpToolBar.setFloatable(false);
             mToolBars.add(mActionToolBar, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
             GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
             mToolBars.add(mStructToolBar, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
             GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
             mToolBars.add(mChordsToolBar, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
             GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
             mToolBars.add(mTabsToolBar, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
             GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
             mToolBars.add(mBookToolBar, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
             GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
             mToolBars.add(mHarpToolBar,  new GridBagConstraints(3, 0, 1, 2, 0.0, 0.0, GridBagConstraints.WEST,
             GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0)); */
			//-- Do layout
            this.setLayout(new BorderLayout());
            this.add(mToolBars, BorderLayout.PAGE_START);
            this.add(tabbedPane, BorderLayout.CENTER);
            /*
             this.setLayout(new GridLayout(2,1));
             this.add(mToolBars);
             this.add(tabbedPane);
             */
        }//end constructor
    }//end class contentPanel

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     *
     * @param path
     * @return
     */
    public ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = GuitarTeX2.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            consoleBox.addText("Couldn't find file: " + path);
            return null;
        }
    }

    private JButton createChordButton(Action aButton, int keyEvent) {
        InputMap inputMap = mEditArea.getInputMap();
        KeyStroke key = KeyStroke.getKeyStroke(keyEvent, KeyEvent.CTRL_DOWN_MASK);
        inputMap.put(key, aButton);

        JButton chord = new JButton(aButton);
        //chord.setMnemonic(keyEvent);
        return chord;
    }

    private JButton createChordButton(Action aButton) {
        JButton chord = new JButton(aButton);
        return chord;
    }

    private JButton createHarpButton(Action hButton, int keyEvent) {
        InputMap inputMap = mEditArea.getInputMap();
        KeyStroke key = KeyStroke.getKeyStroke(keyEvent, KeyEvent.CTRL_DOWN_MASK);
        inputMap.put(key, hButton);

        JButton chord = new JButton(hButton);
        //chord.setMnemonic(keyEvent);
        return chord;
    }

    //============================================================ createMenuBar
    /**
     * Utility function to create a menubar.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = menuBar.add(new JMenu(resbundle.getString("fileMenu")));
        fileMenu.add(mNewAction);
        fileMenu.add(mOpenAction);
        fileMenu.add(mSaveAction);
        fileMenu.add(mSaveAsAction);
        fileMenu.addSeparator();

        JMenu templateMenu = new JMenu(resbundle.getString("templateItem"));
        templateMenu.add(mOpenTemplateSong1Action);
        templateMenu.add(mOpenTemplateSong2Action);
        templateMenu.add(mOpenTemplateBookAction);
        fileMenu.add(templateMenu);

        fileMenu.addSeparator();
        fileMenu.add(mExitAction);
        JMenu editMenu = menuBar.add(new JMenu(resbundle.getString("editMenu")));
        editMenu.add(mCopyAction);
        editMenu.add(mPasteAction);
        editMenu.addSeparator();
        editMenu.add(mPrefAction);
        JMenu buildMenu = menuBar.add(new JMenu(resbundle.getString("buildMenu")));
        buildMenu.add(mGTX2TeXAction);
		//buildMenu.add(mLatexAction);
        //buildMenu.add(mDvi2PsAction);
        //buildMenu.add(mDvi2PdfAction);
        buildMenu.add(mTeX2PdfAction);
        mTeX2PdfAction.setEnabled(false);

        JMenu helpMenu = menuBar.add(new JMenu(resbundle.getString("helpMenu")));
        helpMenu.add(mConsoleAction);
        helpMenu.add(mFAQAction);
        helpMenu.add(mShortcutAction);
        helpMenu.add(mAboutAction);
        return menuBar;
    }//end createMenuBar

    //============================================================ createActions
    /**
     * Utility function to define actions.
     */
    private void createActions() {
        mActFileName = resbundle.getString("mNewFile");
        int shortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        mNewAction = new newActionClass(resbundle.getString("newItem"),
                KeyStroke.getKeyStroke(KeyEvent.VK_N, shortcutKeyMask));

        mOpenAction = new openActionClass(resbundle.getString("openItem"),
                KeyStroke.getKeyStroke(KeyEvent.VK_O, shortcutKeyMask));

        mOpenTemplateSong1Action = new openActionClass(resbundle.getString("simpleSong1"));
        mOpenTemplateSong2Action = new openActionClass(resbundle.getString("simpleSong2"));
        mOpenTemplateBookAction = new openActionClass(resbundle.getString("simpleBook"));

        mSaveAction = new saveActionClass(resbundle.getString("saveItem"),
                KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcutKeyMask));

        mSaveAsAction = new saveAsActionClass(resbundle.getString("saveAsItem"));

        mCopyAction = new copyActionClass(resbundle.getString("copyItem"),
                KeyStroke.getKeyStroke(KeyEvent.VK_C, shortcutKeyMask));
        mPasteAction = new pasteActionClass(resbundle.getString("pasteItem"),
                KeyStroke.getKeyStroke(KeyEvent.VK_V, shortcutKeyMask));
        mPrefAction = new prefActionClass(resbundle.getString("mPrefs"));

        mGTX2TeXAction = new gtx2texActionClass(resbundle.getString("gtx2tex"));
		//mLatexAction = new latexActionClass ( resbundle.getString("latex"));
        //mDvi2PsAction = new dvi2psActionClass ( resbundle.getString("dvi2ps"));
        //mDvi2PdfAction = new dvi2pdfActionClass ( resbundle.getString("dvi2pdf"));
        mTeX2PdfAction = new tex2pdfActionClass(resbundle.getString("tex2pdf"));

        mConsoleAction = new consoleActionClass(resbundle.getString("consoleItem"));

        mFAQAction = new faqActionClass(resbundle.getString("faqItem"));

        mShortcutAction = new shortcutActionClass(resbundle.getString("shortcutItem"));

        mAboutAction = new aboutActionClass(resbundle.getString("aboutItem"));

        mExitAction = new exitActionClass(resbundle.getString("exitItem"),
                KeyStroke.getKeyStroke(KeyEvent.VK_Q, shortcutKeyMask));

    }//end createActions

    GTXConsole getConsole() {
        return consoleBox;
    }

    public class copyActionClass extends AbstractAction {

        private static final long serialVersionUID = 6027334751848561316L;

        public copyActionClass(String text, KeyStroke shortcut) {
            super(text);
            putValue(ACCELERATOR_KEY, shortcut);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            mCopyBuffer = mEditArea.getSelectedText();
        }
    }

    public class pasteActionClass extends AbstractAction {

        private static final long serialVersionUID = 7204847877754106290L;

        public pasteActionClass(String text, KeyStroke shortcut) {
            super(text);
            putValue(ACCELERATOR_KEY, shortcut);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                mEditArea.getDocument().insertString(mEditArea.getCaretPosition(), mCopyBuffer, new SimpleAttributeSet());
            } catch (Exception f) {
                consoleBox.addText("ERR: Paste failed: " + f);
            }
        }
    }

    // Action-Class
    public class newActionClass extends AbstractAction {

        private static final long serialVersionUID = -2269873789000630887L;

        public newActionClass(String text, KeyStroke shortcut) {
            super(text);
            putValue(ACCELERATOR_KEY, shortcut);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (mFileChanged == true) {
                int n = JOptionPane.showConfirmDialog(
                        GuitarTeX2.this,
                        resbundle.getString("mSaveQuestion"),
                        resbundle.getString("mSaveQuestionHead"),
                        JOptionPane.YES_NO_OPTION);
				// n: 0 == yes
                //    1 == no
                //   -1 == window closed
                if (n == 0) {
                    if (mActFileName.equalsIgnoreCase(resbundle.getString("mNewFile"))) {
                        mSaveAsAction.actionPerformed(e);
                        adjustAllSettings();
                    } else {
                        mSaveAction.actionPerformed(e);
                        adjustAllSettings();
                    }
                } else if (n == 1) {
                    adjustAllSettings();
                }
            } else {
                adjustAllSettings();
            }
        }

        private void adjustAllSettings() {
            String newTemplate = "{language: english }\n\n"
                    + "{title: Title}\n"
                    + "{subtitle: Subtitle}\n\n"
                    + "{a: Bla Fasel Bla}\n"
                    + "{bridge}\n"
                    + "   [B]Lin[E]e1\n"
                    + "   Line2\n"
                    + "   Line3\n"
                    + "{/bridge}\n\n"
                    + "{a: Text2}\n"
                    + "{chorus}\n"
                    + "   Line1\n"
                    + "   [C]CLin[e]e2\n"
                    + "   CLine3\n"
                    + "{/chorus}\n";
            mActFileName = resbundle.getString("mNewFile");
            mEditArea.setText(newTemplate);
            tabbedPane.setTitleAt(0, resbundle.getString("mNewFile"));
            mFileChooser.setSelectedFile(null);
            mEditArea.requestFocus();
            mShowTeXArea.setText("");
            mShowTeXArea.setEnabled(false);
            mShowTeXArea.setVisible(false);
            mShowTeXArea.setCaretPosition(0);
            tabbedPane.setEnabledAt(1, false);
            mFileChanged = false;
        }
    }

    public class openActionClass extends AbstractAction {

        private static final long serialVersionUID = -6230238426866087448L;

        public openActionClass(String text, KeyStroke shortcut) {
            super(text);
            putValue(ACCELERATOR_KEY, shortcut);
        }

        public openActionClass(String text) {
            super(text);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String fileName = "";
            if (e.getActionCommand().equals(resbundle.getString("simpleSong1"))) {
                //fileName = myConf.getSongTemplate();
                fileName = resbundle.getString("simpleSong1");
            }
            if (e.getActionCommand().equals(resbundle.getString("simpleSong2"))) {
                fileName = resbundle.getString("simpleSong2");
            }
            if (e.getActionCommand().equals(resbundle.getString("simpleBook"))) {
                fileName = myConf.getBookTemplate();
            }
            if (mFileChanged == true) {
                int n = JOptionPane.showConfirmDialog(
                        GuitarTeX2.this,
                        resbundle.getString("mSaveQuestion"),
                        resbundle.getString("mSaveQuestionHead"),
                        JOptionPane.YES_NO_OPTION);
				// n: 0 == yes
                //    1 == no
                //   -1 == window closed
                if (n == 0) {
                    if (mActFileName.equalsIgnoreCase(resbundle.getString("mNewFile"))) {
                        mSaveAsAction.actionPerformed(e);
                        if (fileName.equals("")) {
                            openFile();
                        } else {
                            openFile(fileName);
                        }
                    } else {
                        mSaveAction.actionPerformed(e);
                        if (fileName.equals("")) {
                            openFile();
                        } else {
                            openFile(fileName);
                        }
                    }
                } else if (n == 1) {
                    if (fileName.equals("")) {
                        openFile();
                    } else {
                        openFile(fileName);
                    }
                }
            } else {
                if (fileName.equals("")) {
                    openFile();
                } else {
                    openFile(fileName);
                }
            }

        }

        private void openFile() {
            int retval = mFileChooser.showOpenDialog(GuitarTeX2.this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File f = mFileChooser.getSelectedFile();
                try {
                    FileInputStream fis = new FileInputStream(f);
			        //InputStreamReader in = 
                    //       new InputStreamReader(fis, Charset.forName("UTF-8")); 
                    //    char[] buffer = new char[1024];
                    //    int n = in.read(buffer);
                    //    String text = new String(buffer, 0, n);
                    //String text = "";
                    //String encoding = ;
                    mEditArea.read(new UnicodeReader(fis, "UTF-8"), "");
                    mEditArea.getDocument().addDocumentListener(new MyDocumentListener());
			        //mEditArea.setText(text);
                    //in.close();

                    mActFileName = f.getName();
                    tabbedPane.setTitleAt(0, mActFileName);
                    //highlight(mEditArea, "a");
                    mShowTeXArea.setText("");
                    mShowTeXArea.setEnabled(false);
                    mShowTeXArea.setVisible(false);
                    mShowTeXArea.setCaretPosition(0);
                    tabbedPane.setEnabledAt(1, false);
                    mFileChanged = false;
                } catch (IOException ioex) {
                    consoleBox.addText("ERR: " + ioex);
                    new InfoBox("Error: " + ioex);
                }
            }
        }

        private void openFile(String fileName) {
            try {
                if ( fileName.contentEquals(resbundle.getString("simpleSong1")) ) {
                    mEditArea.read(new UnicodeReader(GuitarTeX2.class.getResourceAsStream("/examples/griechischer_wein.gtx"), "UTF-8"), "");
                    mActFileName = resbundle.getString("mNewFile");
                    mFileChooser.setSelectedFile(null);
                    tabbedPane.setTitleAt(0, resbundle.getString("mNewFile"));
                }else if ( fileName.contentEquals(resbundle.getString("simpleSong2"))  ) {
                    mEditArea.read(new UnicodeReader(GuitarTeX2.class.getResourceAsStream("/examples/lazy_blues.gtx"), "UTF-8"), "");
                    mActFileName = resbundle.getString("mNewFile");
                    mFileChooser.setSelectedFile(null);
                    tabbedPane.setTitleAt(0, resbundle.getString("mNewFile"));
                }else{
                    File f = new File(fileName);
                    FileInputStream fis = new FileInputStream(f);
                    mEditArea.read(new UnicodeReader(fis, "UTF-8"), "");
                    mActFileName = f.getName();
                    tabbedPane.setTitleAt(0, mActFileName);
                }
                mEditArea.getDocument().addDocumentListener(new MyDocumentListener());
                mShowTeXArea.setText("");
                mShowTeXArea.setEnabled(false);
                mShowTeXArea.setVisible(false);
                mShowTeXArea.setCaretPosition(0);
                tabbedPane.setEnabledAt(1, false);
                mFileChanged = false;
            } catch (IOException ioex) {
                consoleBox.addText("ERR: " + ioex);
                new InfoBox("Error: " + ioex);
            }
        }
    }

    public class closeActionClass extends AbstractAction {

        private static final long serialVersionUID = 5462192874364430698L;

        public closeActionClass(String text, KeyStroke shortcut) {
            super(text);
            putValue(ACCELERATOR_KEY, shortcut);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            consoleBox.addText("Close...");
        }
    }

    public class saveActionClass extends AbstractAction {

        private static final long serialVersionUID = -6554665572883956365L;

        public saveActionClass(String text, KeyStroke shortcut) {
            super(text);
            putValue(ACCELERATOR_KEY, shortcut);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (mActFileName.equalsIgnoreCase(resbundle.getString("mNewFile"))) {
                mSaveAsAction.actionPerformed(e);
            } else {
                File f = mFileChooser.getSelectedFile();
                try {
                    FileOutputStream fos
                            = new FileOutputStream(f);
                    try (OutputStreamWriter out = new OutputStreamWriter(fos, Charset.forName("UTF-8"))) {
                        out.write(mEditArea.getText());
                    }
                    mFileChanged = false;
                    mActFileName = f.getName();
                    tabbedPane.setTitleAt(0, mActFileName);
                    mEditArea.requestFocus();
                } catch (IOException ioex) {
                    consoleBox.addText("ERR: " + ioex);
                }
            }
        }
    }

    public class saveAsActionClass extends AbstractAction {

        private static final long serialVersionUID = 5317474925638206873L;

        public saveAsActionClass(String text) {
            super(text);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int retval = mFileChooser.showSaveDialog(GuitarTeX2.this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File f = mFileChooser.getSelectedFile();
                try {
                    FileOutputStream fos
                            = new FileOutputStream(f);
                    try (OutputStreamWriter out = new OutputStreamWriter(fos, Charset.forName("UTF-8"))) {
                        out.write(mEditArea.getText());
                    }
                    mFileChanged = false;
                    mActFileName = f.getName();
                    tabbedPane.setTitleAt(0, mActFileName);
                    mEditArea.requestFocus();
                } catch (IOException ioex) {
                    consoleBox.addText("ERR: " + ioex);
                }
            }
        }
    }

    public class gtx2texActionClass extends AbstractAction {

        private static final long serialVersionUID = -1997688375883172485L;

        public gtx2texActionClass(String text) {
            super(text);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
            setCursor(hourglassCursor);
            convertToTeX();
            Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
            setCursor(normalCursor);
        }

        private void convertToTeX() {
            mGTXParser = new GTXParser(mEditArea.getText());
            mGTXParser.convertToTeX();
            mShowTeXArea.setText(mGTXParser.getMyTeXFile());
            mShowTeXArea.setEnabled(true);
            mShowTeXArea.setVisible(true);
            mShowTeXArea.setCaretPosition(0);
            tabbedPane.setEnabledAt(1, true);

			// Wird spaeter wieder einkommentiert
            //mLatexButton.setEnabled(true);
        }
    }

    public class latexActionClass extends AbstractAction {

        private static final long serialVersionUID = -1834935499825164201L;

        public latexActionClass(String text) {
            super(text);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (mShowTeXArea.isEnabled()) {
                CmdExec latex = new CmdExec();
                File g = new File("/tmp/gtx2/lied1.tex");

                try {
                    try (FileWriter writer = new FileWriter(g)) {
                        mShowTeXArea.write(writer);
                    }
                } catch (Exception exc) {
                    System.out.println("ERR: Konnte die Datei nicht schreiben!" + exc);
                }
                System.out.println("Starte DVI-Anzeigeprogramm");
//				String actDir = System.getProperty("user.dir");
                latex.execute("/usr/bin/latex /tmp/gtx2/lied1.tex");
                latex.execute("/usr/bin/xdvi lied1.dvi");

				//latex.execute("cd /tmp/guitartex2/");
                //File tmpDir = new File(mPrefResbundle.getString("tmpPath"));
				//latex.execute("cd " + mPrefResbundle.getString("tmpPath"));
                //latex.execute(mPrefResbundle.getString("latex") + " /tmp/guitartex2/lied1.tex");
                //latex.execute("cd " + actDir);
            }
        }
    }

    /*
     public class dvi2psActionClass extends AbstractAction {
     private static final long serialVersionUID = -157869873861617802L;

     public dvi2psActionClass (String text) {
     super(text);			
     }
		
     public void actionPerformed (ActionEvent e) {
     System.out.println("dvi2ps");
     }
     }

     public class dvi2pdfActionClass extends AbstractAction {
     private static final long serialVersionUID = 3203633393288039244L;

     public dvi2pdfActionClass (String text) {
     super(text);
     }
		
     public void actionPerformed (ActionEvent e) {
     System.out.println("dvi2pdf");
     }
     }
     */
    public class tex2pdfActionClass extends AbstractAction {

        private static final long serialVersionUID = 1L;

        public tex2pdfActionClass(String text) {
            super(text);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String tmpDir = myConf.getTmpDir();
            String tmpDirPrefix = myConf.getTmpDirPrefix();
            String texFileName = "gtx2pdf" + id + ".tex";
            String pdfFileName = "gtx2pdf" + id + ".pdf";

            File fd = new File(tmpDir + tmpDirPrefix);
            if (fd.isDirectory() == false) {
                fd.mkdirs();
            }
            File f = new File(tmpDir + tmpDirPrefix + texFileName);

			//myStatusBox.setStatus("waiting...");
            try {
                mGTX2TeXAction.actionPerformed(e);
                FileOutputStream fos
                        = new FileOutputStream(f);
                try (OutputStreamWriter out = new OutputStreamWriter(fos, Charset.forName("UTF-8"))) {
                    out.write(mShowTeXArea.getText());
                    //if ( mShowTeXArea.isEnabled() ) {
                    String showPdf = myConf.quoteString(myConf.getPdfViewer()) + " " + myConf.quoteString(tmpDir + tmpDirPrefix + pdfFileName);
                    GTXClient gtxClient = new GTXClient(myConf.getGtxServer(), myConf.getGtxServerPort(), myStatusBox, tmpDir + tmpDirPrefix + texFileName, id, showPdf);
                    gtxClient.setGTXConsole(consoleBox);
                    int openResult = gtxClient.openConnection();
                    if (openResult == 0) {
                        gtxClient.start();
                    }
                    //}
                }
            } catch (Exception g) {
                consoleBox.addText("Can't open tmp-tex file: " + g);
            }
        }
    }

    public class prefActionClass extends AbstractAction {

        private static final long serialVersionUID = -8836756782588688400L;

        public prefActionClass(String text) {
            super(text);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (preferencesBox == null) {
                preferencesBox = new PreferencesBox(myConf, mTeX2PdfButton, mTeX2PdfAction);
            } else {
                preferencesBox.setEnabled(false);
                preferencesBox = new PreferencesBox(myConf, mTeX2PdfButton, mTeX2PdfAction);
            }
        }
    }

    public class consoleActionClass extends AbstractAction {

        private static final long serialVersionUID = -416103244597033152L;

        public consoleActionClass(String title) {
            super(title);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (consoleBox == null) {
                consoleBox = new GTXConsole();
                consoleBox.setGTXClient(myConf);
            } else {
                if (!consoleBox.isVisible()) {
                    consoleBox.setVisible(true);
                }
            }
        }
    }

    public class aboutActionClass extends AbstractAction {

        private static final long serialVersionUID = -262614383498345150L;

        public aboutActionClass(String text) {
            super(text);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (aboutBox == null) {
                aboutBox = new AboutBox();
            } else {
                if (!aboutBox.isVisible()) {
                    aboutBox.setVisible(true);
                }
            }
        }
    }

    public class faqActionClass extends AbstractAction {

        private static final long serialVersionUID = -4076201863269382621L;

        public faqActionClass(String text) {
            super(text);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            BareBonesBrowserLaunch.openURL(resbundle.getString("faqURL"));
        }
    }

    public class shortcutActionClass extends AbstractAction {

        private static final long serialVersionUID = 6535888027666006093L;

        public shortcutActionClass(String text) {
            super(text);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            BareBonesBrowserLaunch.openURL(resbundle.getString("shortcutsURL"));
        }
    }

    public class exitActionClass extends AbstractAction {

        private static final long serialVersionUID = -895438436156815591L;

        public exitActionClass(String text, KeyStroke shortcut) {
            super(text);
            putValue(ACCELERATOR_KEY, shortcut);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (mFileChanged == true) {
                int n = JOptionPane.showConfirmDialog(
                        GuitarTeX2.this,
                        resbundle.getString("mSaveQuestion"),
                        resbundle.getString("mSaveQuestionHead"),
                        JOptionPane.YES_NO_OPTION);
				// n: 0 == yes
                //    1 == no
                //   -1 == window closed
                if (n == 0) {
                    if (mActFileName.equalsIgnoreCase(resbundle.getString("mNewFile"))) {
                        mSaveAsAction.actionPerformed(e);
                        exitApplication();
                    } else {
                        mSaveAction.actionPerformed(e);
                        exitApplication();
                    }
                } else if (n == 1) {
                    exitApplication();
                }
            } else {
                exitApplication();
            }
        }

        private void exitApplication() {
            cleanTmpDirectory();
            System.exit(0);
        }

        private void cleanTmpDirectory() {
            String tmpDir = myConf.getTmpDir();
            String tmpDirPrefix = myConf.getTmpDirPrefix();
            try {
                File[] tempFiles = new File(tmpDir + tmpDirPrefix).listFiles();
                for (File tempFile : tempFiles) {
                    tempFile.deleteOnExit();
                }
            } catch (Exception e) {
                consoleBox.addText("Clean temp directory failed: " + e);
                System.err.println("clean temp directory failed: " + e);
            }
        }
    }

    // Button-Class
    public class mStructButtonClass extends AbstractAction {

        private static final long serialVersionUID = -2160838467602037321L;

        private final String mAction;

        public mStructButtonClass(String text) {
            super(text);
            mAction = text;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String mText = "";
            int orderLength = 0;
            int placeHolderLength = 4;

            int nextLine = getNextLine();

            if (resbundle.getString("mTitle").equals(mAction)) {
                mText = "{title: text}";
                orderLength = 8;
            }
            if (resbundle.getString("mSubtitle").equals(mAction)) {
                mText = "{subtitle: text}";
                orderLength = 11;
            }
            if (resbundle.getString("mBridge").equals(mAction)) {
                mText = "{bridge}\ntext\n{/bridge}";
                orderLength = 9;
            }
            if (resbundle.getString("mChorus").equals(mAction)) {
                mText = "{chorus}\ntext\n{/chorus}";
                orderLength = 9;
            }
            if (resbundle.getString("mBold").equals(mAction)) {
                mText = "{a: text}";
                orderLength = 4;
            }
            if (resbundle.getString("mGuitarTab").equals(mAction)) {
                mText = "{guitartab: }";
                orderLength = 12;
                placeHolderLength = 0;
            }
            if (resbundle.getString("mLanguage").equals(mAction)) {
                mText = "{language: english }";
                orderLength = 11;
                placeHolderLength = 7;
            }
            try {
                mEditArea.getDocument().insertString(nextLine, mText, new SimpleAttributeSet());
            } catch (Exception f) {
                consoleBox.addText("ERR: Insert " + mText + "failed" + f);
            }
            mEditArea.setSelectionStart(nextLine + orderLength);
            mEditArea.setSelectionEnd(nextLine + orderLength + placeHolderLength);
            mEditArea.requestFocus();
        }
    }

    public class mChordButtonClass extends AbstractAction {

        private static final long serialVersionUID = 2329576716672960443L;

        private final String mChord;

        public mChordButtonClass(String text) {
            super(text);
            mChord = "[" + text + "]";
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int pos = mEditArea.getCaretPosition();
            try {
                mEditArea.getDocument().insertString(pos, mChord, new SimpleAttributeSet());
                int lastPos = mEditArea.getCaretPosition();
                mEditArea.setCaretPosition(lastPos - 1);
            } catch (Exception f) {
                consoleBox.addText("ERR: Insert " + mChord + " failed:" + f);
            }
            mEditArea.requestFocus();
        }
    }

    public class mPMHarpActionClass extends AbstractAction {

        private static final long serialVersionUID = 6922439217506544126L;

        private final String button;

        public mPMHarpActionClass(String text) {
            super(text);
            button = text;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (button.equals("up")) {
                mHarpUp.setSelected(true);
                mHarpDown.setSelected(false);
            }
            if (button.equals("down")) {
                mHarpDown.setSelected(true);
                mHarpUp.setSelected(false);
            }
        }
    }

    public class mHarpButtonClass extends AbstractAction {

        private static final long serialVersionUID = -6793976993294567920L;

        private final String mHarp;

        public mHarpButtonClass(String text) {
            super(text);
            mHarp = text;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            char add = '+';
            if (mHarpUp.isSelected()) {
                add = '+';
            }
            if (mHarpDown.isSelected()) {
                add = '-';
            }
            String fullHarp = "<" + add + mHarp + ">";

            int pos = mEditArea.getCaretPosition();
            try {
                mEditArea.getDocument().insertString(pos, fullHarp, new SimpleAttributeSet());
                int lastPos = mEditArea.getCaretPosition();
                //mEditArea.setCaretPosition(lastPos - 1 );
                mEditArea.setCaretPosition(lastPos);
            } catch (Exception f) {
                consoleBox.addText("ERR: Insert " + fullHarp + " failed:" + f);
            }
            mEditArea.requestFocus();
        }
    }

    public class mBookToolBarClass extends AbstractAction {

        private static final long serialVersionUID = -2105845246317719134L;

        String mAction;

        public mBookToolBarClass(String text) {
            super(text);
            mAction = text;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String mText = "";
            int orderLength = 0;
            int placeHolderLength = 0;
            int nextLine = getNextLine();

            if (resbundle.getString("mSetBook").equals(mAction)) {
                mText = "{document_class:book}";
            }

            if (resbundle.getString("mBookTitle").equals(mAction)) {
                mText = "{book_title:title}";
                orderLength = 12;
                placeHolderLength = 5;
            }

            if (resbundle.getString("mBookAuthor").equals(mAction)) {
                mText = "{book_author:author}";
                orderLength = 13;
                placeHolderLength = 6;
            }

            if (resbundle.getString("mBookDate").equals(mAction)) {
                MessageFormat timeMf = new MessageFormat("{0,date,medium}");
                Object[] timeObjs = {new Date(System.currentTimeMillis())};
                mText = "{book_date:" + timeMf.format(timeObjs) + "}";
            }

            if (resbundle.getString("mBookInclude").equals(mAction)) {
                int retval = mFileChooser.showOpenDialog(GuitarTeX2.this);
                if (retval == JFileChooser.APPROVE_OPTION) {
                    File f = mFileChooser.getSelectedFile();
                    mText = "{include:" + f.getAbsolutePath() + "}";
                    orderLength = 9;
                }
            }
            try {
                if (orderLength != 0 && placeHolderLength != 0) {
                    mEditArea.getDocument().insertString(nextLine, mText, new SimpleAttributeSet());
                    mEditArea.setSelectionStart(nextLine + orderLength);
                    mEditArea.setSelectionEnd(nextLine + orderLength + placeHolderLength);
                } else {
                    mEditArea.getDocument().insertString(nextLine, mText, new SimpleAttributeSet());
                }
            } catch (Exception f) {
                consoleBox.addText("ERR: Insert BookToolBar " + mText + " failed: " + f);
            }
            mEditArea.requestFocus();
        }
    }

    public class mNoteButtonClass extends AbstractAction {

        private static final long serialVersionUID = -2909154433868053689L;

        private String mNote;
        private String mFret;

        public mNoteButtonClass(String text) {
            super(text);
            if (resbundle.getString("mToneE").equals(text)) {
                mNote = "1";
            }
            if (resbundle.getString("mToneA").equals(text)) {
                mNote = "2";
            }
            if (resbundle.getString("mToneD").equals(text)) {
                mNote = "3";
            }
            if (resbundle.getString("mToneG").equals(text)) {
                mNote = "4";
            }
            if (resbundle.getString("mToneH").equals(text)) {
                mNote = "5";
            }
            if (resbundle.getString("mTonee").equals(text)) {
                mNote = "6";
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Fret holen
            mFret = mFretBar.getText();

            if (!mFret.equalsIgnoreCase("")) {
                if (mNote != null) {
                    setNote(mNote, mFret);
                }
            }
            mFretBar.requestFocus();
        }

        private void setNote(String note, String fret) {
            // An der aktuellen Position einfuegen
            int pos = mEditArea.getCaretPosition();
            try {
                mEditArea.getDocument().insertString(pos, "[" + note + ";" + fret + "]", new SimpleAttributeSet());
            } catch (Exception f) {
                consoleBox.addText("ERR: Insert Note " + note + " failed: " + f);
            }
            mFretBar.setText("");
            mEditArea.requestFocus();
        }
    }

    public class mNoteStringButtonClass extends AbstractAction {

        private static final long serialVersionUID = 3976941938622369381L;

        private final String mNoteString;

        public mNoteStringButtonClass(String text) {
            super(text);
            mNoteString = text;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int pos = mEditArea.getCaretPosition();
            try {
                mEditArea.getDocument().insertString(pos, mNoteString, new SimpleAttributeSet());
            } catch (Exception f) {
                consoleBox.addText("ERR: Insert mNoteString " + mNoteString + " failed: " + f);
            }
            mFretBar.requestFocus();
            //mEditArea.requestFocus();
        }
    }

    public class mTestButtonClass extends AbstractAction {

        private static final long serialVersionUID = -1928126465736047335L;

        public mTestButtonClass(String text) {
            super(text);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //highlight(mEditArea, "a");
        }
    }

    private int getNextLine() {
        int pos;
        if (mEditArea.getDocument().getLength() == 0) {
            return 0;
        } else {
            try {
                for (pos = mEditArea.getCaretPosition(); pos <= mEditArea.getDocument().getLength(); pos++) {
                    if (mEditArea.getText(pos, 1).equals("\n")) {
                        if (pos == 0 || mEditArea.getText(pos - 1, 1).equals("\n")) {
                            return pos;
                        } else if (pos == mEditArea.getDocument().getLength()) {
                            // Eine neue Zeile einf�gen
                            mEditArea.getDocument().insertString(pos, "\n", new SimpleAttributeSet());
                            return pos + 1;
                        } else {
                            //Eine neue Zeile einf�gen
                            mEditArea.getDocument().insertString(pos, "\n", new SimpleAttributeSet());
                            return pos + 1;
                        }
                    }
                }
            } catch (Exception f) {
                consoleBox.addText("ERR: getNextLine failed: " + f);
            }
        }
        return 0;
    }

	// ========================= Highlight - BEGIN  ==============================
    //	 Creates highlights around all occurrences of pattern in textComp
    public void highlight(JTextComponent textComp, String pattern) {
        // First remove all old highlights
        removeHighlights(textComp);

        try {
            Highlighter hilite = textComp.getHighlighter();
            Document doc = textComp.getDocument();
            String text = doc.getText(0, doc.getLength());
            int pos = 0;

            // Search for pattern
            while ((pos = text.indexOf(pattern, pos)) >= 0) {
                // Create highlighter using private painter and apply around pattern
                hilite.addHighlight(pos, pos + pattern.length(), myHighlightPainter);
                pos += pattern.length();
            }
        } catch (BadLocationException e) {
        }
    }

    // Removes only our private highlights
    public void removeHighlights(JTextComponent textComp) {
        Highlighter hilite = textComp.getHighlighter();
        Highlighter.Highlight[] hilites = hilite.getHighlights();

        for (Highlighter.Highlight hilite1 : hilites) {
            if (hilite1.getPainter() instanceof MyHighlightPainter) {
                hilite.removeHighlight(hilite1);
            }
        }
    }

    // An instance of the private subclass of the default highlight painter
    Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(Color.yellow);

    // A private subclass of the default highlight painter
    class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {

        public MyHighlightPainter(Color color) {
            super(color);
        }
    }
	// ================================ Highlight - END ================================ //

    class MyDocumentListener implements DocumentListener {

        String newline = System.getProperty("line.separator");

        @Override
        public void insertUpdate(DocumentEvent e) {
            generalUpdate(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            generalUpdate(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            //generalUpdate(e);
            //Plain text components don't fire these events
        }

        private void generalUpdate(DocumentEvent e) {
            //highlight(mEditArea, "{chorus}");
            tabbedPane.setTitleAt(0, mActFileName + " *");
            mFileChanged = true;
        }
    }

    class SymWindow extends java.awt.event.WindowAdapter {

        @Override
        public void windowClosing(java.awt.event.WindowEvent event) {
            mExitAction.actionPerformed(null);
        }
    }
}//end class GuitarTeX2
