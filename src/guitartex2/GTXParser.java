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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GTXParser {

    private final String mEditArea;
    private String[] mEditAreaLines;
    private String myTeXFile = "";

    // Debug
    private final boolean debugModus = false;

    // geparste Variablen;
    private boolean mEven = false;
    private final String[] mGeometry = new String[50];
    private int mGeometry_counter = 0;
    private String mFontSize = "12";
    private String mLanguage = "english";
    private String mColorChorus = "0,0,0";
    private String mColorBridge = "0,0,1";
    private String mColorInstr = ".3,.3,.3";
    private String mColorTab = ".3,.3,.3";
    private String mColorSecond = "1,1,1";
    private String mColorSecondBack = ".7,.7,.7";
    private boolean bookModus = false;
    private final boolean pdfModus = true;
    private String mBridgeSection = "";
    private String mChorusSection = "";
    private String mTexSection = "";
    private String mTabSection = "";

    private String mTitle = "";
    private String mSubtitle = "";
    private int mDefine_counter = 0;
    private final int mDefine_size = 9;
    private final String[][] mDefine = new String[256][mDefine_size];
    private String mSong = "";
    private String mTab = "";
    private String mBookAuthor = "";
    private String mBookTitle = "";
    private String mBookDate = "";
    private String mMultipleSongs = "";
    private boolean somethingMatched;
    private boolean blockBatch;
    private boolean noPageNum = false;

    /**
     * true when harp tones get used, if true \renewcommand{\baselinestretch}{2}
     * is added to tex
     */
    private boolean harpUsed = false;
    private boolean chordUsed = false;

    /*
     * 

     Output related directives

     {textfont:fontname}

     Sets the font used to print text to fontname. This must be the name of a known PostScript font. 
     Chordii: Default font for text is Times-Roman.


     {chordfont:fontname}

     Sets the font used to print chords to fontname. This must be the name of a known PostScript font. 
     Chordii: Default font for chords is Helvetica-Oblique.

     {chordsize:fontsize}

     Sets the size of the font used for chords. 
     Chordii: Default font size for chords is 9.

     {no_grid}
     {ng}

     Suppresses printing of the list of chords at the end of the current song.

     {grid}
     {g}

     Enables printing of the list of chords at the end of the current song. 
     Chordii: The -g and -G command line options can be used to control what chords are printed.

     {titles: flush}

     Chordii: If flush is left, song titles are printed to the left of the page. If flush is center, or if this directive is missing, titles are printed centered on top of the page.


     {columns number}
     {col: number}

     Specifies the number of columns to print the current song in.

     {column_break}
     {colb}

     Forces a column break.

     // Comments als Box mit einer Hintergrundfarbe hinterlegen
     // \fcolorbox{Rahmenfarbe}{white}{Text, der in einer Box steht, die einen Rahmen in einer von dir definierten Farbe hat}.
     * 
     */
    // Direktiven
    private final String dGeometry = "\\{geometry:";
    private final String dFontSize = "\\{font_size:";
    private final String dFontSize2 = "\\{textsize:";
    private final String dEven = "\\{even\\}";
    private final String dNoPageNum = "\\{nopagenum\\}";
    private final String dColorChorus = "\\{color_chorus:";
    private final String dColorBridge = "\\{color_bridge:";
    private final String dColorInstr = "\\{color_instr:";
    private final String dColorTab = "\\{color_tab:";
    private final String dColorSecond = "\\{color_second:";
    private final String dColorSecondBack = "\\{color_second_back:";
    private final String dTitle = "\\{title:";
    private final String dTitle2 = "\\{t:";
    private final String dSubtitle = "\\{subtitle:";
    private final String dSubtitle2 = "\\{st:";
    private final String dDefine = "\\{define:";
    private final String dHead = "\\{a:";
    private final String dHead2 = "\\{comment:";
    private final String dHead3 = "\\{c:";
    private final String dHead4 = "\\{comment_italic:";
    private final String dHead5 = "\\{ci:";
    private final String dBridgeStart = "\\{bridge\\}";
    private final String dBridgeStart2 = "\\{start_of_bridge\\}";
    private final String dBridgeStart3 = "\\{sob\\}";
    private final String dBridgeEnd = "\\{/bridge\\}";
    private final String dBridgeEnd2 = "\\{end_of_bridge\\}";
    private final String dBridgeEnd3 = "\\{eob\\}";
    private final String dChorusStart = "\\{chorus\\}";
    private final String dChorusStart2 = "\\{start_of_chorus\\}";
    private final String dChorusStart3 = "\\{soc\\}";
    private final String dChorusEnd = "\\{/chorus\\}";
    private final String dChorusEnd2 = "\\{end_of_chorus\\}";
    private final String dChorusEnd3 = "\\{eoc\\}";
    private final String dTabStart = "\\{tab\\}";
    private final String dTabStart2 = "\\{start_of_tab\\}";
    private final String dTabStart3 = "\\{sot\\}";
    private final String dTabEnd = "\\{/tab\\}";
    private final String dTabEnd2 = "\\{end_of_tab\\}";
    private final String dTabEnd3 = "\\{eot\\}";
    private final String dNewPage = "\\{np\\}";
    private final String dNewPage2 = "\\{new_page\\}";
    private final String dGuitarTab = "\\{guitartab:";
    private final String dTexStart = "\\{tex\\}";
    private final String dTexEnd = "\\{/tex\\}";
    private final String dDocumentClass = "\\{document_class:";
    private final String dBookAuthor = "\\{book_author:";
    private final String dBookTitle = "\\{book_title:";
    private final String dBookDate = "\\{book_date:";
    private final String dInclude = "\\{include:";
    private final String dComment = "^#";
    private final String dLangauge = "\\{language:";
    private final String dHarpUsed = ".*<.*>.*";
    private final String dChordUsed = ".*\\[.*\\].*";

    private final GTXTextConsole myConsole;

    // --------------------Constructor
    public GTXParser(String editArea) {
        // http://www.bastie.de/index.html?/java/mjregular/index.html
        mEditArea = editArea;
        myConsole = new GTXTextConsole();
    }// Constructor

    public int convertToTeX() {
        int bridgeStartZeile = 0;
        int chorusStartZeile = 0;
        int tabStartZeile = 0;
        int texStartZeile = 0;

        // All patterns
        Pattern geoPattern = Pattern.compile(dGeometry + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern fontSizePattern = Pattern.compile("(" + dFontSize + ".*)|(" + dFontSize2 + ".*)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern evenPattern = Pattern.compile(dEven + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern cChPattern = Pattern.compile(dColorChorus + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern cBrPattern = Pattern.compile(dColorBridge + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern cInstrPattern = Pattern.compile(dColorInstr + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern cTabPattern = Pattern.compile(dColorTab + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern cSecondPattern = Pattern.compile(dColorSecond + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern cSecondBackPattern = Pattern.compile(dColorSecondBack + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern titlePattern = Pattern.compile("(" + dTitle + ".*)|(" + dTitle2 + ".*)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern subtitlePattern = Pattern.compile("(" + dSubtitle + ".*)|(" + dSubtitle2 + ".*)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern definePattern = Pattern.compile(dDefine + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern headPattern = Pattern.compile("(" + dHead + ".*)|(" + dHead2 + ".*)|(" + dHead3 + ".*)|(" + dHead4 + ".*)|(" + dHead5 + ".*)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern bridgeStartPattern = Pattern.compile("(" + dBridgeStart + ".*)|(" + dBridgeStart2 + ".*)|(" + dBridgeStart3 + ".*)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern bridgeEndPattern = Pattern.compile("(" + dBridgeEnd + ".*)|(" + dBridgeEnd2 + ".*)|(" + dBridgeEnd3 + ".*)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern chorusStartPattern = Pattern.compile("(" + dChorusStart + ".*)|(" + dChorusStart2 + ".*)|(" + dChorusStart3 + ".*)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern chorusEndPattern = Pattern.compile("(" + dChorusEnd + ".*)|(" + dChorusEnd2 + ".*)|(" + dChorusEnd3 + ".*)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern tabStartPattern = Pattern.compile("(" + dTabStart + ".*)|(" + dTabStart2 + ".*)|(" + dTabStart3 + ".*)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern tabEndPattern = Pattern.compile("(" + dTabEnd + ".*)|(" + dTabEnd2 + ".*)|(" + dTabEnd3 + ".*)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern newPagePattern = Pattern.compile("(" + dNewPage + ".*)|(" + dNewPage2 + ".*)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern guitarTabPattern = Pattern.compile(dGuitarTab + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern texStartPattern = Pattern.compile(dTexStart + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern texEndPattern = Pattern.compile(dTexEnd + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern documentClassPattern = Pattern.compile(dDocumentClass + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern bookAuthorPattern = Pattern.compile(dBookAuthor + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern bookTitlePattern = Pattern.compile(dBookTitle + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern bookDatePattern = Pattern.compile(dBookDate + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern includePattern = Pattern.compile(dInclude + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern commentPattern = Pattern.compile(dComment + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern noPageNumPattern = Pattern.compile(dNoPageNum + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern languagePattern = Pattern.compile(dLangauge + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern harpUsedPattern = Pattern.compile(dHarpUsed + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern chordUsedPattern = Pattern.compile(dChordUsed + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

        mEditAreaLines = mEditArea.split("\n");
        blockBatch = false;
        for (int i = 0; i < mEditAreaLines.length; i++) {
            try {
                if (debugModus == true) {
                    System.out.println(mEditAreaLines[i]);
                    printChars(mEditAreaLines[i]);
                }
                Matcher geoMatch = geoPattern.matcher(mEditAreaLines[i]);
                Matcher fontSizeMatch = fontSizePattern.matcher(mEditAreaLines[i]);
                Matcher evenMatch = evenPattern.matcher(mEditAreaLines[i]);
                Matcher cChMatch = cChPattern.matcher(mEditAreaLines[i]);
                Matcher cBrMatch = cBrPattern.matcher(mEditAreaLines[i]);
                Matcher cInstrMatch = cInstrPattern.matcher(mEditAreaLines[i]);
                Matcher cTabMatch = cTabPattern.matcher(mEditAreaLines[i]);
                Matcher cSecondMatch = cSecondPattern.matcher(mEditAreaLines[i]);
                Matcher cSecondBackMatch = cSecondBackPattern.matcher(mEditAreaLines[i]);
                Matcher titleMatch = titlePattern.matcher(mEditAreaLines[i]);
                Matcher subtitleMatch = subtitlePattern.matcher(mEditAreaLines[i]);
                Matcher defineMatch = definePattern.matcher(mEditAreaLines[i]);
                Matcher headMatch = headPattern.matcher(mEditAreaLines[i]);
                Matcher bridgeStartMatch = bridgeStartPattern.matcher(mEditAreaLines[i]);
                Matcher bridgeEndMatch = bridgeEndPattern.matcher(mEditAreaLines[i]);
                Matcher chorusStartMatch = chorusStartPattern.matcher(mEditAreaLines[i]);
                Matcher chorusEndMatch = chorusEndPattern.matcher(mEditAreaLines[i]);
                Matcher tabStartMatch = tabStartPattern.matcher(mEditAreaLines[i]);
                Matcher tabEndMatch = tabEndPattern.matcher(mEditAreaLines[i]);
                Matcher newPageMatch = newPagePattern.matcher(mEditAreaLines[i]);
                Matcher guitarTabMatch = guitarTabPattern.matcher(mEditAreaLines[i]);
                Matcher texStartMatch = texStartPattern.matcher(mEditAreaLines[i]);
                Matcher texEndMatch = texEndPattern.matcher(mEditAreaLines[i]);
                Matcher documentClassMatch = documentClassPattern.matcher(mEditAreaLines[i]);
                Matcher bookAuthorMatch = bookAuthorPattern.matcher(mEditAreaLines[i]);
                Matcher bookTitleMatch = bookTitlePattern.matcher(mEditAreaLines[i]);
                Matcher bookDateMatch = bookDatePattern.matcher(mEditAreaLines[i]);
                Matcher includeMatch = includePattern.matcher(mEditAreaLines[i]);
                Matcher commentMatch = commentPattern.matcher(mEditAreaLines[i]);
                Matcher noPageNumMatch = noPageNumPattern.matcher(mEditAreaLines[i]);
                Matcher languageMatch = languagePattern.matcher(mEditAreaLines[i]);
                Matcher harpUsedMatch = harpUsedPattern.matcher(mEditAreaLines[i]);
                Matcher chordUsedMatch = chordUsedPattern.matcher(mEditAreaLines[i]);

                if (!commentMatch.matches()) {
                    somethingMatched = false;

                    if (languageMatch.matches()) {
                        setLanguage(languageMatch.group());
                        somethingMatched = true;
                    }

                    if (harpUsedMatch.matches()) {
                        setHarpUsed(true);
                        /**
                         * an dieser Stelle wird KEIN somethingMatched gesetzt,
                         * weil wir entsprechende Zeilen ja nicht wegwerfen
                         * möchten sondern nur wissen wollen ob überhaupt welche
                         * vorhanden sind.
                         */
                    }

                    if (chordUsedMatch.matches()) {
                        setChordUsed(true);
                        /**
                         * an dieser Stelle wird KEIN somethingMatched gesetzt,
                         * weil wir entsprechende Zeilen ja nicht wegwerfen
                         * möchten sondern nur wissen wollen ob überhaupt welche
                         * vorhanden sind.
                         */
                    }

                    if (noPageNumMatch.matches()) {
                        setNoPageNum(noPageNumMatch.group());
                        somethingMatched = true;
                    }
                    if (geoMatch.matches()) {
                        setGeometry(geoMatch.group());
                        somethingMatched = true;
                    }
                    if (fontSizeMatch.matches()) {
                        setFontSize(fontSizeMatch.group());
                        somethingMatched = true;
                    }
                    if (cChMatch.matches()) {
                        setColorChorus(cChMatch.group());
                        somethingMatched = true;
                    }
                    if (cBrMatch.matches()) {
                        setColorBridge(cBrMatch.group());
                        somethingMatched = true;
                    }
                    if (cInstrMatch.matches()) {
                        setColorInstr(cInstrMatch.group());
                        somethingMatched = true;
                    }
                    if (cTabMatch.matches()) {
                        setColorTab(cTabMatch.group());
                        somethingMatched = true;
                    }
                    if (cSecondMatch.matches()) {
                        setColorSecond(cSecondMatch.group());
                        somethingMatched = true;
                    }
                    if (cSecondBackMatch.matches()) {
                        setColorSecondBack(cSecondBackMatch.group());
                        somethingMatched = true;
                    }
                    if (evenMatch.matches()) {
                        setEven();
                        somethingMatched = true;
                    }
                    if (titleMatch.matches()) {
                        setTitle(titleMatch.group());
                        somethingMatched = true;
                    }
                    if (subtitleMatch.matches()) {
                        setSubtitle(subtitleMatch.group());
                        somethingMatched = true;
                    }
                    if (defineMatch.matches()) {
                        setDefine(defineMatch.group());
                        somethingMatched = true;
                    }

                    if (headMatch.matches()) {
                        setHead(headMatch.group());
                        somethingMatched = true;
                    }

                    if (bridgeStartMatch.matches()) {
                        somethingMatched = true;
                        blockBatch = true;
                        if (debugModus == true) {
                            System.out.println("Bridge Start matched");
                        }
                        bridgeStartZeile = i + 1;
                    }
                    if (bridgeEndMatch.matches()) {
                        somethingMatched = true;
                        blockBatch = false;
                        if (debugModus == true) {
                            System.out.println("Bridge End matched");
                        }
                        if (bridgeStartZeile != 0) {
                            for (int j = bridgeStartZeile; j < i; j++) {
                                mBridgeSection = mBridgeSection + mEditAreaLines[j] + "\n";
                            }
                            setBridge(mBridgeSection);
                            bridgeStartZeile = 0;
                            mBridgeSection = "";
                        } else {
                            myConsole.addText("Ende von Bridge ohne den zugehoerigen Anfang!");
                        }
                    }

                    if (chorusStartMatch.matches()) {
                        somethingMatched = true;
                        blockBatch = true;
                        chorusStartZeile = i + 1;
                    }
                    if (chorusEndMatch.matches()) {
                        blockBatch = false;
                        somethingMatched = true;
                        if (chorusStartZeile != 0) {
                            for (int j = chorusStartZeile; j < i; j++) {
                                mChorusSection = mChorusSection + mEditAreaLines[j] + "\n";
                            }
                            setChorus(mChorusSection);
                            chorusStartZeile = 0;
                            mChorusSection = "";
                        } else {
                            myConsole.addText("Ende von Chorus ohne den zugehoerigen Anfang!");
                        }
                    }

                    if (newPageMatch.matches()) {
                        somethingMatched = true;
                        setNewPage();
                    }

                    if (tabStartMatch.matches()) {
                        blockBatch = true;
                        somethingMatched = true;
                        tabStartZeile = i + 1;
                    }
                    if (tabEndMatch.matches()) {
                        blockBatch = false;
                        somethingMatched = true;
                        if (tabStartZeile != 0) {
                            for (int j = tabStartZeile; j < i; j++) {
                                mTabSection = mTabSection + mEditAreaLines[j] + "\n";
                            }
                            setTab(mTabSection);
                            tabStartZeile = 0;
                            mTabSection = "";
                        } else {
                            myConsole.addText("Ende vom Tab ohne den zugehoerigen Anfang!");
                        }
                    }

                    if (guitarTabMatch.matches()) {
                        somethingMatched = true;
                        setGuitarTab(guitarTabMatch.group());
                    }

                    if (texStartMatch.matches()) {
                        blockBatch = true;
                        somethingMatched = true;
                        texStartZeile = i + 1;
                    }
                    if (texEndMatch.matches()) {
                        blockBatch = false;
                        somethingMatched = true;
                        if (texStartZeile != 0) {
                            for (int j = texStartZeile; j < i; j++) {
                                mTexSection = mTexSection + mEditAreaLines[j] + "\n";
                            }
                            setTex(mTexSection);
                            texStartZeile = 0;
                            mTexSection = "";
                        } else {
                            myConsole.addText("Ende von Tex ohen den zugehoerigen Anfang!");
                        }
                    }

                    if (documentClassMatch.matches()) {
                        somethingMatched = true;
                        setDocumentClass(documentClassMatch.group());
                    }
                    if (bookAuthorMatch.matches()) {
                        somethingMatched = true;
                        setBookAuthor(bookAuthorMatch.group());
                    }
                    if (bookTitleMatch.matches()) {
                        somethingMatched = true;
                        setBookTitle(bookTitleMatch.group());
                    }
                    if (bookDateMatch.matches()) {
                        somethingMatched = true;
                        setBookDate(bookDateMatch.group());
                    }
                    if (includeMatch.matches()) {
                        somethingMatched = true;
                        setInclude(includeMatch.group());
                    }

                    // Falls keine bekannte Zeile, dann wird sie durch den Parser gejagt und angedruckt
                    if (somethingMatched == false && blockBatch == false) {
                        nothingMatched(mEditAreaLines[i]);
                    }
                }

            } catch (Exception e) {
                myConsole.addText("Problem beim Konvertieren" + e);
            }
        }

		//mEditArea.setCursor(0);
        /*		System.out.println("mGeometry: " + getGeometry());
         System.out.println("mFontSize: " + getFontSize());
         System.out.println("mColorChorus: " + getColorChorus());
         System.out.println("mEven: " + mEven); */
        /*		for ( int i = 0; i<mDefine_counter; i++) {
         System.out.println("Akkord: " + mDefine[i][0]);
         System.out.print("Noten: ");
         for ( int j = 1; j<mDefine_size; j++) {
         System.out.print(mDefine[i][j]);
         }
         System.out.println("");
         } */
//		System.out.println("mTitle: " + mTitle);
// 		System.out.println("mSubtitle: " + mSubtitle);
//		System.out.println("mHead: " + mSong);
//		System.out.println(mSong);
//		System.out.println(mTab);
//		System.out.println(mGuitarTab);
        return 0;
    }

    private void nothingMatched(String inputText) {
        if (!inputText.equals("")) {
            //mSong = mSong + parseChordSection(inputText + "\n", true);
            mSong = mSong + inputText + "\n";
        }
    }

    private void setLanguage(String language) {
        language = language.replaceAll(dLangauge, "");
        mLanguage = toTeXString(language, 1);
    }

    private void setNoPageNum(String inputText) {
        noPageNum = true;
    }

    private String toTeXString(String chString, int type) {
		// Typen:
        //   1 => delete closed Element
        //     => delete spaces
        //     => delete carage return (windows)
        //   2 => delete closed Element
        //     => delete TAB
        //     => replace # Element
        //   3 => delete TAB        (chord and bdrige blocks)
        //     => replace # Element
        if (type == 1) {
            chString = chString.replaceAll("\\}", "");
            chString = chString.replaceAll(" ", "");
            chString = chString.replaceAll("\r", "");
        }
        if (type == 2) {
            chString = chString.replaceAll("\\}", "");
            chString = chString.replaceAll("#", "\\$\\\\sharp\\$");
            chString = chString.replaceAll("\t", "");
        }
        if (type == 3) {
            chString = chString.replaceAll(" ", "~");
            chString = chString.replaceAll("#", "\\$\\\\sharp\\$");
            chString = chString.replaceAll("\t", "");
			//chString = chString.replaceAll("\n", "\\\\\\\\");
            //chString = chString.replaceAll("\n", "\\\\ \n");
            //chString = chString.replaceAll("\n", "\n\\\n");
            chString = chString.replaceAll("ö", "\"o");
            chString = chString.replaceAll("Ö", "\"O");
            chString = chString.replaceAll("ä", "\"a");
            chString = chString.replaceAll("Ä", "\"A");
            chString = chString.replaceAll("ü", "\"u");
            chString = chString.replaceAll("Ü", "\"U");
            chString = chString.replaceAll("ß", "\"s");
        }

        return chString;
    }

    private int setGeometry(String geoString) {
        geoString = geoString.replaceAll(dGeometry, "");
        geoString = toTeXString(geoString, 1);
        mGeometry[mGeometry_counter] = geoString;
        mGeometry_counter = mGeometry_counter + 1;
        return 0;
    }

    /*	private String[] getGeometry() {
     return mGeometry;
     } */
    private int setFontSize(String fontSize) {
        fontSize = fontSize.replaceAll(dFontSize, "");
        fontSize = fontSize.replaceAll(dFontSize2, "");
        fontSize = toTeXString(fontSize, 1);
        mFontSize = fontSize;

        return 0;
    }

    /*	private String getFontSize() {
     return mFontSize;
     } */
    private int setColorChorus(String colorChorus) {
        colorChorus = colorChorus.replaceAll(dColorChorus, "");
        colorChorus = toTeXString(colorChorus, 1);
        mColorChorus = colorChorus;
        return 0;
    }

    /*	private String getColorChorus() {
     return mColorChorus;
     } */
    private int setColorBridge(String colorBridge) {
        colorBridge = colorBridge.replaceAll(dColorBridge, "");
        colorBridge = toTeXString(colorBridge, 1);
        mColorBridge = colorBridge;
        return 0;
    }

    /*	private String getColorBridge() {
     return mColorBridge;
     } */
    private int setColorInstr(String colorInstr) {
        colorInstr = colorInstr.replaceAll(dColorInstr, "");
        colorInstr = toTeXString(colorInstr, 1);
        mColorInstr = colorInstr;
        return 0;
    }

    /*	private String getColorInstr() {
     return mColorInstr;
     } */
    private int setColorTab(String colorTab) {
        colorTab = colorTab.replaceAll(dColorTab, "");
        colorTab = toTeXString(colorTab, 1);
        mColorTab = colorTab;
        return 0;
    }

    /*	private String getColorTab() {
     return mColorTab;
     } */
    private int setColorSecond(String colorSecond) {
        colorSecond = colorSecond.replaceAll(dColorSecond, "");
        colorSecond = toTeXString(colorSecond, 1);
        mColorSecond = colorSecond;
        return 0;
    }

    /*	private String getColorSecond() {
     return mColorSecond;
     } */
    private int setColorSecondBack(String colorSecondBack) {
        colorSecondBack = colorSecondBack.replaceAll(dColorSecondBack, "");
        colorSecondBack = toTeXString(colorSecondBack, 1);
        mColorSecondBack = colorSecondBack;
        return 0;
    }

    /*	private String getColorSecondBack() {
     return mColorSecondBack;
     } */
    private int setTitle(String title) {
        title = title.replaceAll(dTitle, "");
        title = title.replaceAll(dTitle2, "");
        title = toTeXString(title, 2);
        mTitle = title;

        return 0;
    }

    /*	private String getTitle() {
     return mTitle;
     } */
    private int setSubtitle(String subTitle) {
        subTitle = subTitle.replaceAll(dSubtitle, "");
        subTitle = subTitle.replaceAll(dSubtitle2, "");
        subTitle = toTeXString(subTitle, 2);
        mSubtitle = subTitle;

        return 0;
    }

    /*	private String getSubtitle() {
     return mSubtitle;
     } */
    private int setDefine(String define) {
        define = define.replaceAll(dDefine, " ");
        define = toTeXString(define, 2);
        //String[] oneDefine = new String[mDefine_size];
        String[] oneDefine = (define.split("[ ]+", mDefine_size));

        System.arraycopy(oneDefine, 0, mDefine[mDefine_counter], 0, oneDefine.length);

        mDefine_counter = mDefine_counter + 1;

        return 0;
    }

    /*	private String[][] getDefine () {
     return mDefine;
     } */
    private void setEven() {
        mEven = true;
    }

    public boolean getEven() {
        return mEven;
    }

    /**
     * @param harpUsed the harpUsed to set
     */
    public void setHarpUsed(boolean harpUsed) {
        this.harpUsed = harpUsed;
    }

    /**
     * @return the harpUsed
     */
    public boolean isHarpUsed() {
        return harpUsed;
    }

    /**
     * @param chordUsed the chordUsed to set
     */
    public void setChordUsed(boolean chordUsed) {
        this.chordUsed = chordUsed;
    }

    /**
     * @return the chordUsed
     */
    public boolean isChordUsed() {
        return chordUsed;
    }

    private int setHead(String head) {
        head = head.replaceAll(dHead, "");
        head = head.replaceAll(dHead2, "");
        head = head.replaceAll(dHead3, "");
        head = head.replaceAll(dHead4, "");
        head = head.replaceAll(dHead5, "");
        head = toTeXString(head, 2);

        mSong = mSong + "\\textbf{\\textit{" + head + "}} \\\\\n\\\\\n";
        //mSong = mSong + "\\textbf{\\textit{" + head + "}}\\\\\n";
        return 0;
    }

    /*	private String getSong () {
     return mSong;
     } */
    private String parseChordSection(String chordSection, boolean skipEmptyLines) {
        String convertedText = "";
        chordSection = chordSection.replaceAll("\r", "");
        String[] chordSectionLinewise = chordSection.split("\n");

        // Pro Zeile parseChordSectionSingle aufrufen
        for (String chordSectionLinewise1 : chordSectionLinewise) {
            String line = parseChordSectionSingle(chordSectionLinewise1, skipEmptyLines);
            if ("".equals(line)) {
                line = toTeXString(chordSectionLinewise1, 3);
                convertedText = convertedText + "\\combichord{}{}{" + line + "}\\\\ \n";
            } else {
                //System.out.println("> " + line);
                convertedText = convertedText + line + "\\\\ \n";
            }
        }

        return convertedText;
    }

    private String parseChordSectionSingle(String chordSection, boolean skipEmptyLines) {
        String startText = "";
        int startIndex = 0;
        String myTextChord = "";
        String myTextSong = "";
        String myTextHarp = "";
        boolean unchanged = true;

        String convertedText = chordSection;

        Pattern parsePattern = Pattern.compile("([\\[<].*?[\\]>])([\\[<].*?[\\]>])?([.[^\\n\\[<\\]>\\\\]]*)");

        Matcher mat = parsePattern.matcher(convertedText);

        //System.out.print("ChordSection:"+chordSection);
        while (mat.find()) {
            // Anfang soll auch in ein combichord gepackt werden
            if ("".equals(myTextChord) && "".equals(myTextSong) && "".equals(myTextHarp)) {
                startIndex = mat.start();
                if (startIndex != 0) {
                    startText = "\\combichord{}{}{" + chordSection.substring(0, startIndex) + "}";
                }
            }

            unchanged = false;
            myTextChord = "";
            myTextHarp = "";

            myTextSong = mat.group(3);

			//System.out.println("(1): " + mat.group(1) + " (2): " + mat.group(2) + " (3): " + mat.group(3) );
            if (mat.group(1) != null && mat.group(1).charAt(0) == '[') {
                //its an guitar chord
                myTextChord = mat.group(1).substring(1, mat.group(1).length() - 1);
            } else if (mat.group(1) != null) { //it must be harp
                myTextHarp = mat.group(1).substring(1, mat.group(1).length() - 1);
            }

            if (mat.group(2) != null && mat.group(2).charAt(0) == '[') {
                //its an guitar chord
                myTextChord = mat.group(2).substring(1, mat.group(2).length() - 1);
            } else if (mat.group(2) != null) { //it must be harp
                myTextHarp = mat.group(2).substring(1, mat.group(2).length() - 1);
            }

            myTextHarp = harpToTex(myTextHarp);

            convertedText = mat.replaceFirst("\\\\combichord{" + myTextChord + "}{" + myTextHarp + "}{" + myTextSong + "}");
            mat.reset(convertedText);

        }
        if (unchanged) {
            return "";
        } else {
			//System.out.println(startText + convertedText.substring(startIndex, convertedText.length()));
            //return "";
            return toTeXString(startText + convertedText.substring(startIndex, convertedText.length()), 3);
        }
    }

    private String harpToTex(String myTextHarp) {
        if (myTextHarp == null || myTextHarp.length() < 2) {
            return "";
        }
        String out;
        String channel = myTextHarp.substring(1, myTextHarp.length());
        if (myTextHarp.contains("-")) {
            if (myTextHarp.contains("\'\'\'")) {
                out = channel + "\\\\hdrawhhh";
            } else if (myTextHarp.contains("\'\'")) {
                out = channel + "\\\\hdrawhh";
            } else if (myTextHarp.contains("\'")) {
                out = channel + "\\\\hdrawh";
            } else {
                out = channel + "\\\\hdraw";
            }
        } else {
            // TODO Specify blow-bends
            out = channel + "\\\\hblow";
        }
        out = out.replace("\'", "");
        return out;
    }

    private int setBridge(String myBridge) {
        mSong = mSong + "\\begin{bridge}\n";
        mSong = mSong + parseChordSection(myBridge, false);
        mSong = mSong + "\\end{bridge}" + "\n\n";
        return 0;
    }

    private int setChorus(String myChorus) {
        mSong = mSong + "\\begin{chorus}\n";
        mSong = mSong + parseChordSection(myChorus, false);
        mSong = mSong + "\\end{chorus}" + "\n\n";
        return 0;
    }

    private int setTab(String myTeXTab) {
        String[] myTeXTabLine = myTeXTab.split("\n");
        mTab = "\\begin{gtxtab}" + "\n";
        try {
            for (String myTeXTabLine1 : myTeXTabLine) {
                mTab = mTab + "\\footnotesize \\verb!" + myTeXTabLine1 + "! \\normalsize \\newline \n";
            }
        } catch (Exception e) {
            myConsole.addText("ERR: " + e);
        }
        mTab = mTab + "\\end{gtxtab}" + "\n";
        mSong = mSong + mTab + "\n";
        return 0;
    }

    private int setTex(String myTex) {
        try {
            // zum Song hinzufuegen
            mSong = mSong + "\n" + myTex;
        } catch (Exception e) {
            myConsole.addText("ERR: " + e);
        }

        return 0;
    }

    private int setNewPage() {
        mSong = mSong + "\n" + "\\newpage" + "\n";

        return 0;
    }

    private int setGuitarTab(String guitarTab) {
        String texGuitarTab = "";

        String string1Pos = "2.5ex";
        String string2Pos = "4.5ex";
        String string3Pos = "6.5ex";
        String string4Pos = "8.5ex";
        String string5Pos = "10.5ex";
        String string6Pos = "12.5ex";
        String string9Pos = "14.5ex";
        String tabSpace = "~~~";
        String tabBreak = tabSpace
                + "\\gtxtabs{3.7ex}{|}"
                + "\\gtxtabs{5.7ex}{|}"
                + "\\gtxtabs{7.7ex}{|}"
                + "\\gtxtabs{9.7ex}{|}"
                + "\\gtxtabs{11.3ex}{|}" + tabSpace;

        guitarTab = guitarTab.replaceAll(dGuitarTab, "");
        guitarTab = toTeXString(guitarTab, 2);

        guitarTab = guitarTab.replaceAll("\\[1;", "\\[" + string1Pos + ";");
        guitarTab = guitarTab.replaceAll("\\[2;", "\\[" + string2Pos + ";");
        guitarTab = guitarTab.replaceAll("\\[3;", "\\[" + string3Pos + ";");
        guitarTab = guitarTab.replaceAll("\\[4;", "\\[" + string4Pos + ";");
        guitarTab = guitarTab.replaceAll("\\[5;", "\\[" + string5Pos + ";");
        guitarTab = guitarTab.replaceAll("\\[6;", "\\[" + string6Pos + ";");
        guitarTab = guitarTab.replaceAll("\\[9;", "\\[" + string9Pos + ";");

        texGuitarTab = texGuitarTab + "\n" + "\\guitartab" + "\n";
        String gtxTab = "";
        String oneNote;
        String onePlace;
        boolean noteMode = false;
        for (int i = 0; i < guitarTab.length(); i++) {
            if (guitarTab.charAt(i) == ']') {
                String [] tmp = gtxTab.split(";");
                onePlace = tmp[0];
                oneNote = tmp[1];

                texGuitarTab = texGuitarTab + "\\gtxtabs{" + onePlace + "}{" + oneNote + "}";

                if (i + 1 < guitarTab.length()) {
                    if (guitarTab.charAt(i + 1) == '[') {
                        texGuitarTab = texGuitarTab + tabSpace;
                    }
                }

                gtxTab = "";
                noteMode = false;
            }
            if (noteMode == true) {
                gtxTab = gtxTab + guitarTab.charAt(i);
            }
            if (guitarTab.charAt(i) == '[') {
                noteMode = true;
            }
            if (guitarTab.charAt(i) == ' ') {
                texGuitarTab = texGuitarTab + "~";
            }
            if (guitarTab.charAt(i) == '_') {
                texGuitarTab = texGuitarTab + tabSpace;
            }
            if (guitarTab.charAt(i) == '|') {
                texGuitarTab = texGuitarTab + tabBreak;
            }

        }

        mSong = mSong + "\\footnotesize";
        mSong = mSong + texGuitarTab + "\n";
        mSong = mSong + "\\normalsize" + "\n\n";

        return 0;
    }

    public String getMyTeXFile() {
        if (bookModus == true) {
            createTeXBook();
        } else {
            createTeXFile();
        }
        return myTeXFile;
    }

    public String getMyTeXSong() {
        createTeX();
        return myTeXFile;
    }

    private void setDocumentClass(String documentClass) {
        documentClass = documentClass.replaceAll(dDocumentClass, "");
        documentClass = toTeXString(documentClass, 1);

        if (documentClass.equalsIgnoreCase("book")) {
            bookModus = true;
        }
    }

    private void setBookAuthor(String bookAuthor) {
        bookAuthor = bookAuthor.replaceAll(dBookAuthor, "");
        bookAuthor = toTeXString(bookAuthor, 2);

        mBookAuthor = bookAuthor;
    }

    private void setBookTitle(String bookTitle) {
        bookTitle = bookTitle.replaceAll(dBookTitle, "");
        bookTitle = toTeXString(bookTitle, 2);

        mBookTitle = bookTitle;
    }

    private void setBookDate(String bookDate) {
        bookDate = bookDate.replaceAll(dBookDate, "");
        bookDate = toTeXString(bookDate, 2);

        mBookDate = bookDate;
    }

    private void setInclude(String fileName) {
        String mTextArea = "";
        fileName = fileName.replaceAll(dInclude, "");
        fileName = fileName.replaceAll("\\}", "");
        fileName = fileName.replaceAll("\n", "");
        fileName = fileName.replaceAll("\r", "");

        File f = new File(fileName);
        try {
            try (FileInputStream fis = new FileInputStream(f); BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f), Charset.forName("UTF-8")))) {
                boolean eof = false;
                while (eof == false) {
                    String line = in.readLine();
                    if (line == null) {
                        eof = true;
                    } else {
                        mTextArea = mTextArea + line + "\n";
                    }
                }
            }

            GTXParser mGTXParser = new GTXParser(mTextArea);
            mGTXParser.convertToTeX();
            if (mGTXParser.getEven() == true) {
                mMultipleSongs = mMultipleSongs + "\\ifthenelse{\\isodd{\\thepage}}{}{\\newpage ~}\n";
            }
            mMultipleSongs = mMultipleSongs + mGTXParser.getMyTeXSong();
        } catch (Exception e) {
            myConsole.addText("File input error:" + e);
        }

    }

    public int createTeXFile() {

        myTeXFile = "\\documentclass[" + mLanguage + "," + mFontSize + "pt]{article} \n";
        //myTeXFile = myTeXFile + "\\documentstyle[times]{article}";
        myTeXFile = myTeXFile + "\\usepackage[";
        if (mGeometry_counter != 0) {
            for (int i = 0; i < mGeometry_counter - 1; i++) {
                myTeXFile = myTeXFile + mGeometry[i] + ",";
            }
            myTeXFile = myTeXFile + mGeometry[mGeometry_counter - 1] + "]{geometry}\n";
        } else {
            myTeXFile = myTeXFile + "a4paper,margin=2.5cm" + "]{geometry}\n";
        }
        myTeXFile = myTeXFile
                + "\\usepackage[T1]{fontenc}\n"
                + "\\usepackage{lmodern}\n"
                + "\\usepackage[utf8]{inputenc}\n"
                + //"\\usepackage[latin1]{inputenc}\n" +
                "\\usepackage{babel}\n"
                + "\\usepackage{color}\n"
                + //"\\usepackage{graphicx}\n" +
                "\\usepackage{ifthen}\n"
                + "\\usepackage{calc}\n"
                + "\\usepackage{gchords}\n"
                + "\\usepackage{calligra}\n";

        if (noPageNum) {
            myTeXFile = myTeXFile + "\\pagestyle{empty}\n";
        }

        if (mEven) {
            myTeXFile = myTeXFile + "\\pagestyle{empty}\n";
        }

        // Fontfarben definieren
        myTeXFile = myTeXFile + "\n"
                + "%------definition of font colors-----------\n"
                + "\\definecolor{chorus}{rgb}{" + mColorChorus + "}\n"
                + "\\definecolor{bridge}{rgb}{" + mColorBridge + "}\n"
                + "\\definecolor{instr}{rgb}{" + mColorInstr + "}\n"
                + "\\definecolor{tab}{rgb}{" + mColorTab + "}\n"
                + "\\definecolor{second_text}{rgb}{" + mColorSecond + "}\n"
                + "\\definecolor{second_back}{rgb}{" + mColorSecondBack + "}\n";

        if (isHarpUsed() && isChordUsed()) {
            myTeXFile = myTeXFile + "\\renewcommand{\\baselinestretch}{2}\n";
        }
        // Umgebungen definieren
        myTeXFile = myTeXFile + "\\newenvironment{chorus}{\\color{chorus}}{\\normalcolor}\n"
                + "\\newenvironment{bridge}{\\color{bridge}}{\\normalcolor}\n"
                + "\\newenvironment{instr}{\\color{instr}}{\\normalcolor}\n"
                + "\\newenvironment{gtxtab}{\\color{tab}}{\\normalcolor}\n";

        myTeXFile = myTeXFile
                + "\\newcommand{\\gtxchordsize}{\\footnotesize}\n"
                + "\\setlength{\\parindent}{0pt}\n"
                + "\\sloppy\n"
                + "\\setlength{\\unitlength}{1mm}\n"
                + "\\newcommand{\\gtxchord}[2]{\\sbox{\\gtxchordbox}{#1}\\sbox{\\textbox}{#2}%\n"
                + "\\ifthenelse{\\lengthtest{\\wd\\textbox>\\wd\\gtxchordbox}}%\n"
                + "{%\n"
                + "\\raisebox{2ex}[2ex][2.5ex]{\\makebox[0pt][l]{\\scriptsize\\bf#1}}#2%\n"
                + "}{%\n"
                + "\\raisebox{2ex}[2ex][2.5ex]{\\makebox[0pt][l]{\\scriptsize\\bf#1}}\\makebox[\\wd\\gtxchordbox+0.5em][l]{#2}%\n"
                + "}%\n"
                + "}%\n"
                + "\\newcommand{\\guitartab}{%\n"
                + "\\makebox[0cm][l]{\\raisebox{12.5ex}{\\footnotesize{e}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{10.5ex}{\\footnotesize{H}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{8.5ex}{\\footnotesize{G}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{6.5ex}{\\footnotesize{D}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{4.5ex}{\\footnotesize{A}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{2.5ex}{\\footnotesize{E}}}%\n"
                + "~~\n"
                + "\\makebox[0cm][l]{\\raisebox{13ex}{\\line(1,0){130}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{11ex}{\\line(1,0){130}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{9ex}{\\line(1,0){130}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{7ex}{\\line(1,0){130}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{5ex}{\\line(1,0){130}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{3ex}{\\line(1,0){130}}}%\n"
                + "~}\n\n"
                + "\\newcommand{\\basstab}{%\n"
                + "\\makebox[0cm][l]{\\raisebox{8.5ex}{\\footnotesize{G}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{6.5ex}{\\footnotesize{D}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{4.5ex}{\\footnotesize{A}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{2.5ex}{\\footnotesize{E}}}%\n"
                + "~~\n"
                + "\\makebox[0cm][l]{\\raisebox{9ex}{\\line(1,0){130}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{7ex}{\\line(1,0){130}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{5ex}{\\line(1,0){130}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{3ex}{\\line(1,0){130}}}%\n"
                + "~}\n\n"
                + "\\newcommand{\\gtxtabs}[2]{\\makebox[0cm][l]{\\raisebox{#1}{#2}}}\n"
                + //	define arrow symbols for harp tunes
                "\\newcommand{\\hdraw}{\n"
                + "  \\begin{picture}(0,0)\n"
                + "    \\put(0,2.5){\\vector(0,-1){3}}\n"
                + "  \\end{picture}\n"
                + "}\n"
                + "\n"
                + "\\newcommand{\\hblow}{\n"
                + "  \\begin{picture}(0,0)\n"
                + "    \\put(0,-0.5){\\vector(0,1){3}}\n"
                + "  \\end{picture}\n"
                + "}\n"
                + "\n"
                + "\\newcommand{\\hdrawh}{\n"
                + "  \\begin{picture}(0,0)\n"
                + "    \\put(0,2.5){\\line(0,-1){1.5}}\n"
                + "%    \\put(0,1){\\line(-1,-1){1.5}}\n"
                + "%    \\put(0,1){\\vector(-1,-1){2}}\n"
                + "    \\put(0,1){\\vector(-1,-1){1}}\n"
                + "  \\end{picture}\n"
                + "}\n"
                + "\n"
                + "\\newcommand{\\hdrawhh}{\n"
                + "  \\begin{picture}(0,0)\n"
                + "    \\put(0,2.5){\\line(0,-1){1.5}}\n"
                + "    \\put(0,1){\\vector(-1,0){1.5}}\n"
                + "  \\end{picture}\n"
                + "}\n"
                + "\n"
                + "\\newcommand{\\hdrawhhh}{\n"
                + "  \\begin{picture}(0,0)\n"
                + "    \\put(0,2.5){\\line(0,-1){2}}\n"
                + "    \\put(0,0.5){\\vector(-1,1){1}}\n"
                + "  \\end{picture}\n"
                + "}\n"
                + "\n"
                + "\\newcommand{\\combichord}[3]{\\sbox{\\gtxchordbox}{#1}\\sbox{\\textbox}{#3}\\sbox{\\harpbox}{#2}%\n"
                + "\\ifthenelse{\\lengthtest{\\wd\\textbox>\\wd\\gtxchordbox}\\and\\lengthtest{\\wd\\textbox>\\wd\\harpbox}}{%\n"
                + "\\raisebox{2ex}[2ex][2.5ex]{\\makebox[0pt][l]{\\scriptsize\\bf#1}}\\raisebox{-2ex}[2ex][2.5ex]{\\makebox[0pt][l]{\\scriptsize\\bf#2}}#3%\n"
                + "}{%\n"
                + "\\ifthenelse{\\lengthtest{\\wd\\harpbox>\\wd\\textbox}\\and\\lengthtest{\\wd\\harpbox>\\wd\\gtxchordbox}}{%\n"
                + "\\raisebox{2ex}[2ex][2.5ex]{\\makebox[0pt][l]{\\scriptsize\\bf#1}}\\raisebox{-2ex}[2ex][2.5ex]{\\makebox[0pt][l]{\\scriptsize\\bf#2}}\\makebox[\\wd\\harpbox]{#3}%\n"
                + "}{%\n"
                + "\\raisebox{2ex}[2ex][2.5ex]{\\makebox[0pt][l]{\\scriptsize\\bf#1}}\\raisebox{-2ex}[2ex][2.5ex]{\\makebox[0pt][l]{\\scriptsize\\bf#2}}\\makebox[\\wd\\gtxchordbox]{#3}%\n"
                + "}}}%";

		// Dokument - Start
        myTeXFile = myTeXFile + "\n"
                + "\\begin{document}\n"
                + "\\newsavebox{\\gtxchordbox}\n"
                + "\\newsavebox{\\textbox}\n"
                + "\\newsavebox{\\harpbox}\n"
                + "\\ifthenelse{\\isodd{\\thepage}}{}{\\newpage ~}%\n";

        myTeXFile = myTeXFile + "\n%----- New Song -----\n"
                + "\\newpage\\addcontentsline{toc}{section}{" + mTitle + "}\n"
                + "\\begin{center} \\section*{" + mTitle + "}\\end{center}\n"
                + "\\index{" + mTitle + "}\\typeout{" + mTitle + "}\\index{" + mSubtitle
                + "!" + mTitle + "}\\begin{center}" + mSubtitle + "\\end{center}\n";

        // Dokument - Song
        myTeXFile = myTeXFile + mSong;

		// Tabs
/*		myTeXFile = myTeXFile + "\\footnotesize" + "\n";
         myTeXFile = myTeXFile + mTab + "\n";
         myTeXFile = myTeXFile + mGuitarTab + "\n\n";
         myTeXFile = myTeXFile + "\\normalsize" + "\n"; 
         */
        // Definiere - Akkorde
        String mChord = "";
        for (int i = 0; i < mDefine_counter; i++) {
            mChord = mChord + "~\\chord {{" + mDefine[i][2] + "}}{";
            for (int j = 3; j < mDefine_size - 1; j++) {
                mChord = mChord + mDefine[i][j] + ",";
            }
            mChord = mChord + mDefine[i][mDefine_size - 1];
            mChord = mChord + "}{" + mDefine[i][1] + "}\n";
        }
        mChord = mChord.replaceAll(" ", "");
        myTeXFile = myTeXFile + mChord;

		// Dokement - Ende
        myTeXFile = myTeXFile + "\\end{document}\n";

        return 0;
    }

    public int createTeX() {
		// Dokument - Start

        myTeXFile = myTeXFile + "\n" + "%----- New Song -----\n"
                + "\\newpage\\addcontentsline{toc}{section}{" + mTitle + "}\n";
        if (pdfModus == true) {
            myTeXFile = myTeXFile + "\\pdfdest name {"
                    + mTitle + "} fith \\pdfoutline goto name {"
                    + mTitle + "} {" + mTitle + "}";
        }
        myTeXFile = myTeXFile + "\\begin{center} \\section*{" + mTitle + "}\\end{center}\n"
                + "\\index{" + mTitle + "}\\typeout{" + mTitle + "}\\index{" + mSubtitle
                + "!" + mTitle + "}\\begin{center}" + mSubtitle + "\\end{center}\n";

        // Dokument - Song
        myTeXFile = myTeXFile + mSong;

		// Tabs
/*		myTeXFile = myTeXFile + "\\footnotesize" + "\n";
         myTeXFile = myTeXFile + mTab + "\n";
         myTeXFile = myTeXFile + mGuitarTab + "\n\n";
         myTeXFile = myTeXFile + "\\normalsize" + "\n";
         */
        // Definiere - Akkorde
        String mChord = "";
        for (int i = 0; i < mDefine_counter; i++) {
            mChord = mChord + "~\\chord {{" + mDefine[i][2] + "}}{";
            for (int j = 3; j < mDefine_size - 1; j++) {
                mChord = mChord + mDefine[i][j] + ",";
            }
            mChord = mChord + mDefine[i][mDefine_size - 1];
            mChord = mChord + "}{" + mDefine[i][1] + "}\n";
        }
        mChord = mChord.replaceAll(" ", "");
        myTeXFile = myTeXFile + mChord;

        return 0;
    }

    public int createTeXBook() {
        // PDF - Info
        myTeXFile = myTeXFile + "\\pdfoutput=1\n"
                + "\\pdfcompresslevel=9\n"
                + "\\pdfpageheight=29.7cm\n"
                + "\\pdfpagewidth=21cm\n"
                + "\\pdfcatalog  {/PageMode /UseOutlines}\n"
                + "\\pdfinfo {\n"
                + "  /Title (" + mBookTitle + ")\n"
                + "  /Author (" + mBookAuthor + ")\n"
                + "  /CreationDate (" + mBookDate + ")\n"
                + "  /Producer (GuitarTeX2 by Nikolai Rinas)\n"
                + "}\n";

        myTeXFile = myTeXFile + "\\documentclass[" + mLanguage + ",pdftex," + mFontSize + "pt]{book} \n"
                + "\\usepackage[";
        if (mGeometry_counter != 0) {
            for (int i = 0; i < mGeometry_counter - 1; i++) {
                myTeXFile = myTeXFile + mGeometry[i] + ",";
            }
            myTeXFile = myTeXFile + mGeometry[mGeometry_counter - 1] + "]{geometry}\n";
        } else {
            myTeXFile = myTeXFile + "a4paper,margin=2.5cm" + "]{geometry}\n";
        }

        myTeXFile = myTeXFile
                + "\\usepackage[T1]{fontenc}\n"
                + "\\usepackage{lmodern}\n"
                + "\\usepackage[utf8]{inputenc}\n"
                + //"\\usepackage[latin1]{inputenc}\n" +
                "\\usepackage{babel}\n"
                + "\\usepackage{color}\n"
                + //"\\usepackage{graphicx}\n" +
                "\\usepackage{ifthen}\n"
                + "\\usepackage{calc}\n"
                + "\\usepackage{gchords}\n"
                + "\\usepackage{makeidx}\n"
                + "\\usepackage{fancyhdr}\n"
                + "\\usepackage{calligra}\n"
                + "\\usepackage[pdftex]{thumbpdf}\n";

        myTeXFile = myTeXFile + "\\title{\\calligra{" + mBookTitle + "}}\n"
                + "\\author{\\calligra{" + mBookAuthor + "}}\n"
                + "\\date{" + mBookDate + "}\n"
                + "\\makeindex\n"
                + "\\pagestyle{fancy}\n";

        // Fontfarben definieren
        myTeXFile = myTeXFile + "\n"
                + "%------definition of font colors-----------\n"
                + "\\definecolor{chorus}{rgb}{" + mColorChorus + "}\n"
                + "\\definecolor{bridge}{rgb}{" + mColorBridge + "}\n"
                + "\\definecolor{instr}{rgb}{" + mColorInstr + "}\n"
                + "\\definecolor{tab}{rgb}{" + mColorTab + "}\n"
                + "\\definecolor{second_text}{rgb}{" + mColorSecond + "}\n"
                + "\\definecolor{second_back}{rgb}{" + mColorSecondBack + "}\n";

        // Umgebungen definieren
        myTeXFile = myTeXFile + "\\newenvironment{chorus}{\\color{chorus}}{\\normalcolor}\n"
                + "\\newenvironment{bridge}{\\color{bridge}}{\\normalcolor}\n"
                + "\\newenvironment{instr}{\\color{instr}}{\\normalcolor}\n"
                + "\\newenvironment{gtxtab}{\\color{tab}}{\\normalcolor}\n";

        myTeXFile = myTeXFile + "\\newcommand{\\gtxchordsize}{\\footnotesize}\n"
                + "\\setlength{\\parindent}{0pt}\n"
                + "\\sloppy\n"
                + "\\setlength{\\unitlength}{1mm}\n"
                + "\\newcommand{\\gtxchord}[2]{\\sbox{\\gtxchordbox}{#1}\\sbox{\\textbox}{#2}%\n"
                + "\\ifthenelse{\\lengthtest{\\wd\\textbox>\\wd\\gtxchordbox}}%\n"
                + "{%\n"
                + "\\raisebox{2ex}[2ex][2.5ex]{\\makebox[0pt][l]{\\scriptsize\\bf#1}}#2%\n"
                + "}{%\n"
                + "\\raisebox{2ex}[2ex][2.5ex]{\\makebox[0pt][l]{\\scriptsize\\bf#1}}\\makebox[\\wd\\gtxchordbox+0.5em][l]{#2}%\n"
                + "}%\n"
                + "}%\n"
                + "\\newcommand{\\guitartab}{%\n"
                + "\\makebox[0cm][l]{\\raisebox{12.5ex}{\\footnotesize{e}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{10.5ex}{\\footnotesize{H}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{8.5ex}{\\footnotesize{G}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{6.5ex}{\\footnotesize{D}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{4.5ex}{\\footnotesize{A}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{2.5ex}{\\footnotesize{E}}}%\n"
                + "~~\n"
                + "\\makebox[0cm][l]{\\raisebox{13ex}{\\line(1,0){130}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{11ex}{\\line(1,0){130}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{9ex}{\\line(1,0){130}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{7ex}{\\line(1,0){130}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{5ex}{\\line(1,0){130}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{3ex}{\\line(1,0){130}}}%\n"
                + "~}\n\n"
                + "\\newcommand{\\basstab}{%\n"
                + "\\makebox[0cm][l]{\\raisebox{8.5ex}{\\footnotesize{G}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{6.5ex}{\\footnotesize{D}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{4.5ex}{\\footnotesize{A}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{2.5ex}{\\footnotesize{E}}}%\n"
                + "~~\n"
                + "\\makebox[0cm][l]{\\raisebox{9ex}{\\line(1,0){130}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{7ex}{\\line(1,0){130}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{5ex}{\\line(1,0){130}}}%\n"
                + "\\makebox[0cm][l]{\\raisebox{3ex}{\\line(1,0){130}}}%\n"
                + "~}\n\n"
                + "\\newcommand{\\gtxtabs}[2]{\\makebox[0cm][l]{\\raisebox{#1}{#2}}}\n"
                + //								define arrow symbols for harp tunes
                "\\newcommand{\\hdraw}{\n"
                + "  \\begin{picture}(0,0)\n"
                + "    \\put(0,2.5){\\vector(0,-1){3}}\n"
                + "  \\end{picture}\n"
                + "}\n"
                + "\n"
                + "\\newcommand{\\hblow}{\n"
                + "  \\begin{picture}(0,0)\n"
                + "    \\put(0,-0.5){\\vector(0,1){3}}\n"
                + "  \\end{picture}\n"
                + "}\n"
                + "\n"
                + "\\newcommand{\\hdrawh}{\n"
                + "  \\begin{picture}(0,0)\n"
                + "    \\put(0,2.5){\\line(0,-1){1.5}}\n"
                + "%    \\put(0,1){\\line(-1,-1){1.5}}\n"
                + "%    \\put(0,1){\\vector(-1,-1){2}}\n"
                + "    \\put(0,1){\\vector(-1,-1){1}}\n"
                + "  \\end{picture}\n"
                + "}\n"
                + "\n"
                + "\\newcommand{\\hdrawhh}{\n"
                + "  \\begin{picture}(0,0)\n"
                + "    \\put(0,2.5){\\line(0,-1){1.5}}\n"
                + "    \\put(0,1){\\vector(-1,0){1.5}}\n"
                + "  \\end{picture}\n"
                + "}\n"
                + "\n"
                + "\\newcommand{\\hdrawhhh}{\n"
                + "  \\begin{picture}(0,0)\n"
                + "    \\put(0,2.5){\\line(0,-1){2}}\n"
                + "    \\put(0,0.5){\\vector(-1,1){1}}\n"
                + "  \\end{picture}\n"
                + "}\n"
                + "\n"
                + "\\newcommand{\\combichord}[3]{\\sbox{\\gtxchordbox}{#1}\\sbox{\\textbox}{#3}\\sbox{\\harpbox}{#2}%\n"
                + "\\ifthenelse{\\lengthtest{\\wd\\textbox>\\wd\\gtxchordbox}\\and\\lengthtest{\\wd\\textbox>\\wd\\harpbox}}{%\n"
                + "\\raisebox{2ex}[2ex][2.5ex]{\\makebox[0pt][l]{\\scriptsize\\bf#1}}\\raisebox{-2ex}[2ex][2.5ex]{\\makebox[0pt][l]{\\scriptsize\\bf#2}}#3%\n"
                + "}{%\n"
                + "\\ifthenelse{\\lengthtest{\\wd\\harpbox>\\wd\\textbox}\\and\\lengthtest{\\wd\\harpbox>\\wd\\gtxchordbox}}{%\n"
                + "\\raisebox{2ex}[2ex][2.5ex]{\\makebox[0pt][l]{\\scriptsize\\bf#1}}\\raisebox{-2ex}[2ex][2.5ex]{\\makebox[0pt][l]{\\scriptsize\\bf#2}}\\makebox[\\wd\\harpbox]{#3}%\n"
                + "}{%\n"
                + "\\raisebox{2ex}[2ex][2.5ex]{\\makebox[0pt][l]{\\scriptsize\\bf#1}}\\raisebox{-2ex}[2ex][2.5ex]{\\makebox[0pt][l]{\\scriptsize\\bf#2}}\\makebox[\\wd\\gtxchordbox]{#3}%\n"
                + "}}}%\n";

        // Dokument - Start
        myTeXFile = myTeXFile + "\\begin{document}\n"
                + "\\newsavebox{\\gtxchordbox}\n"
                + "\\newsavebox{\\textbox}\n"
                + "\\newsavebox{\\harpbox}\n"
                + "\\fancyhead\n"
                + "\\fancyfoot\n"
                + "\\maketitle\n"
                + "\\tableofcontents\n"
                + "\\fancyhead[CE]{\\calligra{" + mBookTitle + "}}\n"
                + "\\fancyhead[LE,RO]{\\thepage}";

        // Dokument - Songs
        myTeXFile = myTeXFile + mMultipleSongs;

        // Dokument - End
        myTeXFile = myTeXFile + "\\backmatter\n"
                + "\\printindex\n"
                + "\\end{document}\n";

        return 0;
    }

    private void printChars(String inString) {
        int actChar;
        for (int i = 0; i < inString.length(); i++) {
            actChar = inString.charAt(i);
            System.out.print(actChar + " ");
        }
        System.out.println("\n");
    }
}
