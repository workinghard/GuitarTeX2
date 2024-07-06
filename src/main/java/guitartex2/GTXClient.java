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

import java.util.Locale;
import java.util.ResourceBundle;

import java.net.URL;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;


public class GTXClient extends Thread{
    
    private StatusBox _myStatusBox;
    private String _texFileName;
	private String _pdfFileName;
    private String _showPdf;

    private boolean _hostExist = false;
    
    private ResourceBundle _resbundle;
    
    private GTXConsole _myConsole;
    private String logCache = "";
    private String _urlPingEndpoint = "/ping";
	private String _urlLatexEndpoint = "/latex";
	private String _httpsURLPing = "";
	private String _httpsURLLatex = "";

    // Konstruktor
    public GTXClient(String host, int port) {
    	_resbundle = ResourceBundle.getBundle ("GuitarTeX2strings", Locale.getDefault());
		if ( port > 0) {
			_httpsURLPing = host + ":" + port + _urlPingEndpoint;
			_httpsURLLatex = host + ":" + port + _urlLatexEndpoint;	
		}else{
			_httpsURLPing = host + _urlPingEndpoint;
			_httpsURLLatex = host + _urlLatexEndpoint;				
		}
		logToConsole("PingURL: " + _httpsURLPing);
		logToConsole("LatexURL: " + _httpsURLLatex);
		int available = this.checkServerConnection();
		if ( available == 0 ) {
			_hostExist = true;
			logToConsole("Made server connection");
		}else{
			_hostExist = false;
			logToConsole("No server connection");
		}
    }
	
    public GTXClient(String host, int port, StatusBox sBox, String texFile, String pdfFile, String sPdf) {
		this(host, port);
		
		_showPdf = sPdf;
		_myStatusBox = sBox;
		_texFileName = texFile;
		_pdfFileName = pdfFile;
		
		logToConsole("Made server connection");
    }
    
    private void logToConsole(String text) {
    	if ( _myConsole == null ) {
    		if ( logCache.equals("") ) {
    			logCache = text;
    		}else{
    			logCache = logCache + "\n" + text;
    		}
    	}else{
    		if ( ! logCache.equals("") ) {
        		_myConsole.addText(logCache);
        		logCache = "";
    			_myConsole.addText(text);
    		}else{
    			_myConsole.addText(text);
    		}
    	}
    }
    
    void setGTXConsole(GTXConsole mConsole) {
    	_myConsole = mConsole;
    	if ( ! logCache.equals("") ) {
    		_myConsole.addText(logCache);
    		logCache = "";
    	}
    }
    
    void forceLogCache() {
    	if ( _myConsole != null ) {
    		_myConsole.addText(logCache);
    		logCache = null;
    	}
    }
    
    @Override
    public void run () {
		// Do something only if the connection exist
		if ( _hostExist == true ) {
			if ( _myStatusBox != null ) {
    			_myStatusBox.setStatus(_resbundle.getString("sendTexFile"));	
    		}
			logToConsole("Sending tex file...");
			try {
				ServerResponse myResponse = MultipartFormSender.sendMultipartForm(_httpsURLLatex, _texFileName, 
					"file", "text", "{\"version\":\"1.0\"}");
				logToConsole(myResponse.toString());
				if ( myResponse.isInitial() == false ) {
					if ( myResponse.getCmdRC() == 0 ) {
						if ( myResponse.getDownloadURL() != "" ) {
							_myStatusBox.setStatus(_resbundle.getString("receivePdfFile"));
							// Download file
							BufferedInputStream in = new BufferedInputStream(new URL(myResponse.getDownloadURL()).openStream());
  							FileOutputStream fileOutputStream = new FileOutputStream(_pdfFileName);
    						byte dataBuffer[] = new byte[1024];
    						int bytesRead;
    						while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
        						fileOutputStream.write(dataBuffer, 0, bytesRead);
    						}
							fileOutputStream.close();
							in.close();

							logToConsole("pdf file received.");
							// Show PDF
							_myStatusBox.setStatus("try to show PDF file ...");
							logToConsole("try to show pdf file");
							try{ 
								Runtime.getRuntime().exec(_showPdf);
							}catch (Exception h) {
								logToConsole("ERR: " + h);
							}
						}else{
							logToConsole("Download URL is missing");
						}
					}else{
						logToConsole("Tex command failed");
						new InfoBox(_resbundle.getString("texFailed"));
					}
				}else{
					logToConsole("Got no response from server");
				}
			} catch (IOException e) {
				logToConsole("tex file send failed: " + e.getMessage());
			}
		}else{
			logToConsole("We're offline. Please check internet connection");
		}

		_myStatusBox.setVisible(false);

		/* 
    	try {
    		if ( myStatusBox != null ) {
    			myStatusBox.setStatus(resbundle.getString("sendTexFile"));	
    		}
    		dout.writeUTF(transfer);
    		logToConsole("Client ready for transfer:");
    		FileTransfer fileTransfer = new FileTransfer(id,0,din, dout);
    		int texSendResult = fileTransfer.sendFile(fileName);
    		if ( texSendResult != 0) {
    			logToConsole("tex file send failed");
    		}else{
				myStatusBox.setStatus(resbundle.getString("wait4PdfFile"));
				logToConsole("tex file send.");
    		}
    		String texResult = din.readUTF();
    		if ( texResult.equals(failed)) {
                new InfoBox(resbundle.getString("texFailed"));
    			logToConsole("texin unsuccessfull");
    			//myStatusBox.setStatus("FEHLER!");
    			//myStatusBox.requestFocus();
    		}else{
    			myStatusBox.setStatus(resbundle.getString("receivePdfFile"));
    			String rawFileName = fileName.substring(0, fileName.length()-4);
    			int pdfResult = fileTransfer.receiveFile(rawFileName + ".pdf");
    			if ( pdfResult != 0) {
    				logToConsole("pdf unsuccesfull");
    			}else{
    				logToConsole("pdf file received.");
    			}
    			myStatusBox.setStatus(resbundle.getString("receiveLogFile"));
    			int logResult = fileTransfer.receiveFile(rawFileName + ".log");
    			if ( logResult != 0) {
    				logToConsole("log unsuccesfull");
    			}else{
    				logToConsole("log file received.");
    			}
        		myStatusBox.setStatus("try to show PDF file ...");
        		logToConsole("try to show pdf file");
    	        try{ 
    	        	Runtime.getRuntime().exec(showPdf);
    			}catch (Exception h) {
    				logToConsole("ERR: " + h);
    			}

    		}
    		closeConnection();
    		
			myStatusBox.setVisible(false);
    	}catch (Exception e) {
    		logToConsole("failed texin file: " + e);
    	}
		*/
    }
    
    public int checkServerConnection() {
		try{
			URL myUrl = new URL(_httpsURLPing);
			HttpsURLConnection conn = (HttpsURLConnection)myUrl.openConnection();
			if ( conn.getResponseCode() == 200 ) {
				return 0;
			}else{
				logToConsole("responseCode: " + conn.getResponseCode());
				InputStream is = conn.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String inputLine;
				String bodeString = "";
				while ((inputLine = br.readLine()) != null) {
					bodeString = bodeString + inputLine;
				}
				br.close();
				logToConsole("Error: " + bodeString);
				return 1;
			}
		}catch (Exception e) {
			logToConsole("Error: " + e.getMessage());
			return 1;
		}
    }
    
    public String sendText(String text) {
    	String result = "not implemented yet!";
    	return result;
    }
    
}