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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

final class Configurations {
	
	private String myName = "GuitarTeX2";
	private String propertiesFileName = myName + ".properties";

	// get systeminfo
	private String osName = System.getProperty( "os.name" );
	private String fileSeparator = System.getProperty("file.separator");
	private String userPath = System.getProperty("user.home");
	private String runPath = System.getProperty("user.dir");
	
	private String pdfViewer = "";
	private String tmpDir = "";
	private String tmpDirPrefix = "gtxtmp" +  fileSeparator;
	private Properties mProperties;
	private String propertiesFile;
	
	private String confProblems = "";
	
	private GTXConsole mConsole;

	// Constructor
	public Configurations(GTXConsole consoleBox) {
		mProperties = new Properties();
		mConsole = consoleBox;
 
		if ( osName.matches("Windows.*")) {
			mConsole.addText(osName);
			propertiesFile = windowsConfiguration();
		}
		if ( osName.matches("Linux.*")) {
			mConsole.addText("osName");
			propertiesFile = linuxConfiguration();
		}
		if ( osName.matches("Mac OS.*")) {
			mConsole.addText(osName);
			propertiesFile = macosConfiguration();
		}
		if ( osName.equals("")) {
			mConsole.addText("Unknown OS:" + osName);
			propertiesFile = defaultConfiguration();
		}
		try {
			FileInputStream propsFIS;
			if ( propertiesFile.equals("")) {
				mConsole.addText("taking default config");
				propsFIS = new FileInputStream(propertiesFile);
				mConsole.addText("loading...");
				mProperties.load( propsFIS );
				propsFIS.close();
			}else{
				mConsole.addText("taking prefered config: " + propertiesFile);
				propsFIS = new FileInputStream(propertiesFile);
				mConsole.addText("checking new entries ...");
				// Defaultwerte laden
				//File tmpFile = new File(GuitarTeX2.class.getResourceAsStream(propertiesFileName).);
				//System.out.println(tmpFile.getAbsoluteFile());
				//FileInputStream defaultPropFIS = new FileInputStream(tmpFile);
				
				Properties defProps = new Properties();
				//defProps.load(defaultPropFIS);
				defProps.load(GuitarTeX2.class.getResourceAsStream("/"+propertiesFileName));
				//defaultPropFIS.close();
				Enumeration<Object> defElements = defProps.keys();
				mProperties.load( propsFIS );
				propsFIS.close();
				// Auf neue Eintrage pruefen
				boolean mustSafe = false;
				while (defElements.hasMoreElements()) {
					String key = (String)defElements.nextElement();
					if ( mProperties.getProperty(key) == null ) {
						mProperties.setProperty(key, defProps.getProperty(key));
						mustSafe = true;
					}
				}
				if ( mustSafe == true) {
					saveSettings();
				}
			}
		}catch (Exception e) {
			mConsole.addText("failed to load config file: " + e);
			confProblems = confProblems + "Failed to load config file: " + e;
		}
	}
	
	private String windowsConfiguration() {
		String userFileFullPath = userPath + fileSeparator + myName + fileSeparator + propertiesFileName;
		String userFileDirectory = userPath + fileSeparator + myName;
		boolean userConfigFileResult = checkFile(userFileFullPath);
		if ( userConfigFileResult == false ) {
			boolean systemConfigFileResult = checkFile(runPath + fileSeparator + myName + fileSeparator + propertiesFileName);
			if ( systemConfigFileResult == false ) {
				mConsole.addText("creating user config file...");
				try {
					if ( ! new File(userFileDirectory).exists() ) {
						new File(userFileDirectory).mkdir();
					}
					copyFile(GuitarTeX2.class.getResource(propertiesFileName), new File(userFileFullPath));
					return userFileFullPath;
				}catch (Exception e) {
					mConsole.addText("creating user config file failed: " + e);
					return "";
				} 
			}else{
				return runPath + fileSeparator + myName + fileSeparator + propertiesFileName;
			}
		}else{
			return userFileFullPath;
		}
	}
	
	private String linuxConfiguration() {
		String userFileFullPath = userPath + fileSeparator + "." + myName + fileSeparator + propertiesFileName;
		String userFileDirectory = userPath + fileSeparator + "." + myName;
		boolean userConfigFileResult = checkFile(userFileFullPath);
		if ( userConfigFileResult == false ) {
			boolean systemConfigFileResult = checkFile("/etc/GuitarTeX2" + fileSeparator + propertiesFileName);
			if ( systemConfigFileResult == false ) {
				mConsole.addText("creating user config file...");
				try {
					if ( ! new File(userFileDirectory).exists() ) {
						new File(userFileDirectory).mkdir();
					}
					copyFile(GuitarTeX2.class.getResource(propertiesFileName), new File(userFileFullPath));
					return userFileFullPath;
				}catch (Exception e) {
					mConsole.addText("creating user config file failed: " + e);
					return "";
				} 
			}else{
				return runPath + fileSeparator + myName + fileSeparator + propertiesFileName;
			}
		}else{
			return userFileFullPath;
		}
	}
	
	private String macosConfiguration() {
		String userFileFullPath = userPath + fileSeparator + "." + myName + fileSeparator + propertiesFileName;
		String userFileDirectory = userPath + fileSeparator + "." + myName;
		boolean userConfigFileResult = checkFile(userFileFullPath);
		if ( userConfigFileResult == false ) {
			boolean systemConfigFileResult = checkFile("/etc/GuitarTeX2" + fileSeparator + propertiesFileName);
			if ( systemConfigFileResult == false ) {
				mConsole.addText("creating user config file...");
				try {
					if ( ! new File(userFileDirectory).exists() ) {
						new File(userFileDirectory).mkdir();
					}
					copyFile(GuitarTeX2.class.getResource(propertiesFileName), new File(userFileFullPath));
					return userFileFullPath;
				}catch (Exception e) {
					mConsole.addText("creating user config file failed: " + e);
					return "";
				} 
			}else{
				return runPath + fileSeparator + myName + fileSeparator + propertiesFileName;
			}
		}else{
			return userFileFullPath;
		}
	}
	
	private String defaultConfiguration() {
		String userFileFullPath = userPath + fileSeparator + myName + fileSeparator + propertiesFileName;
		String userFileDirectory = userPath + fileSeparator + myName;
		boolean userConfigFileResult = checkFile(userFileFullPath);
		if ( userConfigFileResult == false ) {
			mConsole.addText("creating user config file...");
			try {
				if ( ! new File(userFileDirectory).exists() ) {
					new File(userFileDirectory).mkdir();
				}
				copyFile(GuitarTeX2.class.getResource(propertiesFileName), new File(userFileFullPath));
				return userFileFullPath;
			}catch (Exception e) {
				mConsole.addText("creating user config file failed: " + e);
				return "";
			} 
		}else{
			return userFileFullPath;
		}
	}
	
	
	private void copyFile(URL inURL, File out) throws Exception {
		mConsole.addText("CopyFile: " + inURL);
            try (InputStream inStream = inURL.openStream(); FileOutputStream fos = new FileOutputStream(out)) {
                
                byte[] buf = new byte[1024];
                int i;
                while((i=inStream.read(buf))!=-1) {
                    fos.write(buf, 0, i);
                }
                
            }
	}
	
	private boolean checkFile(String fileName) {
		try {
                    File myFileName = new File(fileName);
                    return myFileName.canRead() == true;
		}catch (Exception e) {
                    return false;
		}
	}
	
	private boolean checkDirectory(String directory) {
		try {
			File dirName = new File(directory);
			if ( dirName.isDirectory() ) {
				File testFile = new File(directory+"testFileName.tst");
				if ( testFile.createNewFile() ) {
					testFile.delete();
					return true;
				}else{
					mConsole.addText("tmpDirectory is not writable");
					confProblems = confProblems + "temp directory is not writable!\n";
					return false;
				}
			}else{
				mConsole.addText("tmpDirectory doesn't exist");
				confProblems = confProblems + "temp directory doesn't exist\n";
				return false;
			}
		}catch (Exception e) {
			return false;
		}
	}

	private boolean checkWindowsSettings() {
		boolean checkResult;
		String path = userPath + fileSeparator + myName + fileSeparator + "tmp" + fileSeparator;
		String tmpPath = userPath + fileSeparator + myName + fileSeparator + "tmp" + 
		                 fileSeparator + tmpDirPrefix + fileSeparator;
		if ( mProperties.getProperty("windowsTmpPath").equals("") ) {
			// Create default tmp-Dir
			File defTmpDir = new File(tmpPath);
			if ( defTmpDir.isDirectory() == false ) {
				defTmpDir.mkdirs();
			}
			mProperties.setProperty("windowsTmpPath", path);
		}
		checkResult = checkDirectory(mProperties.getProperty("windowsTmpPath"));
		checkResult = checkResult & checkFile(mProperties.getProperty("windowsPdfViewer"));
		if ( checkResult == false ) {
			mConsole.addText("windowsPdfViewer not found");
			confProblems = confProblems + "windowsPdfViewer not found\n";
		}
		pdfViewer = mProperties.getProperty("windowsPdfViewer");
		tmpDir = mProperties.getProperty("windowsTmpPath");
		return checkResult;
	}
	
	private boolean checkLinuxSettings() {
		boolean checkResult;
		String path = userPath + fileSeparator + "." + myName + fileSeparator + "tmp" + fileSeparator;
		String tmpPath = userPath + fileSeparator + "." + myName + fileSeparator + "tmp" + 
						 fileSeparator + tmpDirPrefix + fileSeparator;
		if ( mProperties.getProperty("tmpPath").equals("") ) {
			// Create default tmp-Dir
			File defTmpDir = new File(tmpPath);
			if ( defTmpDir.isDirectory() == false ) {
				defTmpDir.mkdirs();
			}
			mProperties.setProperty("tmpPath", path);
		}
		checkResult = checkDirectory(mProperties.getProperty("tmpPath"));
		checkResult = checkResult & checkFile(mProperties.getProperty("linuxPdfViewer"));
		if ( checkResult == false ) {
			mConsole.addText("linuxPdfViewer not found");
			confProblems = confProblems + "linuxPdfViewer not found!\n";
		}
		pdfViewer = mProperties.getProperty("linuxPdfViewer");
		tmpDir = mProperties.getProperty("tmpPath");
		return checkResult;
	}
	
	private boolean checkMacOSSettings() {
		boolean checkResult;
		String path = userPath + fileSeparator + "." + myName + fileSeparator + "tmp" + fileSeparator;
		String tmpPath = userPath + fileSeparator + "." + myName + fileSeparator + "tmp" + fileSeparator +
		                 tmpDirPrefix + fileSeparator;
		if ( mProperties.getProperty("tmpPath").equals("") ) {
			// Create default tmp-Dir
			File defTmpDir = new File(tmpPath);
			if ( defTmpDir.isDirectory() == false ) {
				defTmpDir.mkdirs();
			}
			mProperties.setProperty("tmpPath", path);
		}
		checkResult = checkDirectory(mProperties.getProperty("tmpPath"));
		pdfViewer = "open";
		tmpDir = mProperties.getProperty("tmpPath");
		return checkResult;
	}
	
	private boolean checkOsIndependent() {
		boolean checkResult = true;
	
		return checkResult;
	}
	
	private void setOsUnknown() {
		pdfViewer = "open";
		tmpDir = userPath + fileSeparator;
	}
	
	public Properties getProperties () {
		return mProperties;
	}
	
	public String getTmpDir() {
		return tmpDir;
	}
	
	public void setTmpDir(String mValue) {
		if ( osName.equals("Windows XP")) {
			mProperties.setProperty("windowsTmpPath", mValue);
		}
		if ( osName.equals("Linux")) {
			mProperties.setProperty("tmpPath", mValue);
		}
		if ( osName.equals("Mac OS X")) {
			mProperties.setProperty("tmpPath", mValue);
		}
		tmpDir = mValue;
	}
	
	public boolean checkConfig() {
		boolean testResult = true;
		boolean knownSystem = false;
		if ( osName.matches("Windows.*")) {
			testResult = checkWindowsSettings();
			knownSystem = true;
		}
		if ( osName.matches("Linux.*")) {
			testResult = checkLinuxSettings();
			knownSystem = true;
		}
		if ( osName.matches("Mac OS.*")) {
			testResult = checkMacOSSettings();
			knownSystem = true;
		}
		
		if ( knownSystem == false ) {
			setOsUnknown();
		}
		
		testResult = testResult & checkOsIndependent();
		
		return testResult;
	}
	
	public void saveSettings() {
		try {
			FileOutputStream propsFOS;
			mConsole.addText("taking prefered config: " + propertiesFile);
			propsFOS = new FileOutputStream(propertiesFile);
			mConsole.addText("saving config file ...");
			mProperties.store( propsFOS, " ");
			propsFOS.close();
		}catch (Exception e) {
			mConsole.addText("failed to save config file: " + e);
			confProblems = confProblems + "failed to save config file: " + e;
		}
	}
	
	public String getGtxServer() {
		return mProperties.getProperty("gtxServer");
	}
	public void setGtxServer(String mValue) {
		mProperties.setProperty("gtxServer", mValue);
	}
	
	public int getGtxServerPort() {
		return Integer.parseInt(mProperties.getProperty("gtxServerPort"));
	}
	
	public void setGtxServerPort(String mValue) {
		mProperties.setProperty("gtxServerPort", mValue);
	}
	
	public String getPdfViewer() {
		return pdfViewer;
	}
	public void setPdfViewer(String mValue) {
		if ( osName.matches("Windows.*")) {
			mProperties.setProperty("windowsPdfViewer", mValue);
			pdfViewer = mValue;
		}
		if ( osName.matches("Linux.*")) {
			mProperties.setProperty("linuxPdfViewer", mValue);
			pdfViewer = mValue;
		}
	}
	
	public String getConfFile() {
		return propertiesFile;
	}
	
	public String getLatex() {
		return mProperties.getProperty("latex");
	}
	public void setLatex(String mValue) {
		mProperties.setProperty("latex", mValue);
	}
	
	public String getXDvi() {
		return mProperties.getProperty("xdvi");
	}
	public void setXDvi(String mValue) {
		mProperties.setProperty("xdvi", mValue);
	}
	
	public String getPdfLatex() {
		return mProperties.getProperty("pdflatex");
	}
	
	public void setPdfLatex(String mValue) {
		mProperties.setProperty("pdflatex", mValue);
	}
	
	public String getConfProblems() {
		return confProblems;
	}
	
	public String getTmpDirPrefix() {
		return tmpDirPrefix;
	}
	
	public void setTmpDirPrefix(String mValue) {
		tmpDirPrefix = mValue;
	}
	
	public String quoteString (String input) {
		if ( osName.matches("Windows.*")) {
			return "\""  + input + "\"";
		}else{
			return input;
		}
		
	}
	
	public String getFileSeparator() {
		return fileSeparator;
	}
	
	public String getRunPath() {
		return runPath;
	}
	
	public String getSongTemplate() {
		return runPath + fileSeparator + "examples" + fileSeparator + mProperties.getProperty("exSongFile");
	}
	
	public String getBookTemplate() {
		return runPath + fileSeparator + "examples" + fileSeparator + mProperties.getProperty("exBookFile");
	}
	
	public void loadDefaults() {
		
		try {
                    Properties defProps;
                    try (FileInputStream defaultPropFIS = new FileInputStream(propertiesFileName)) {
                        defProps = new Properties();
                        defProps.load(defaultPropFIS);
                    }
			mProperties.setProperty("gtxServer", defProps.getProperty("gtxServer"));
			mProperties.setProperty("gtxServerPort", defProps.getProperty("gtxServerPort"));
		} catch (Exception e) {
        	new InfoBox(e + "");
		}
		/*
		mPdfViewerField.setText(myConfiguration.getPdfViewer());
		mTmpPathField.setText(myConfiguration.getTmpDir());
		*/
	}
	
	public GTXConsole getConsole() {
		return mConsole; 
	}
}