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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.ResourceBundle;



public class GTXClient extends Thread{

	private static String quit = "CMD:123_QUIT_123";
	private static String transfer = "CMD:123_TRANSFER_123";
	//private static String ok = "CMD:123_OK_123";
	private static String failed = "CMD:123_FAILED_123";
	private static String ping = "CMD:123_PING_123";
    private static String pong = "CMD:123_PONG_123";
    
    private int id;
    private StatusBox myStatusBox;
    private String fileName;
    private String showPdf;

    private boolean hostExist = false;
    
    private ResourceBundle resbundle;
    protected Socket serverConn;
    DataOutputStream dout;
    DataInputStream din;
    
    private GTXConsole myConsole;
    private String logCache = "";
    
    // Konstruktor
    public GTXClient(String host, int port) {
    	resbundle = ResourceBundle.getBundle ("GuitarTeX2strings", Locale.getDefault());
    	try {
    		logToConsole("Trying to connect to " + host + " " + port);
			serverConn = new Socket(host, port);
		}catch (UnknownHostException e) {
			logToConsole("Bad host name given.");
		}catch (IOException e) {
			logToConsole("GtxClient: " + e);
		}
		hostExist = true;
		logToConsole("Made server connection");
    }
	
    public GTXClient(String host, int port, StatusBox sBox, String file, int myId, String sPdf) {
    	resbundle = ResourceBundle.getBundle ("GuitarTeX2strings", Locale.getDefault());
    	try {
    		logToConsole("Trying to connect to " + host + " " + port);
			serverConn = new Socket(host, port);
		}catch (UnknownHostException e) {
			logToConsole("Bad host name given.");
		}catch (IOException e) {
			logToConsole("GtxClient: " + e);
		}
		
		hostExist = true;
		showPdf = sPdf;
		id = myId;
		myStatusBox = sBox;
		fileName = file;
		
		logToConsole("Made server connection");
    }
    
    private void logToConsole(String text) {
    	if ( myConsole == null ) {
    		if ( logCache.equals("") ) {
    			logCache = text;
    		}else{
    			logCache = logCache + "\n" + text;
    		}
    	}else{
    		if ( ! logCache.equals("") ) {
        		myConsole.addText(logCache);
        		logCache = "";
    			myConsole.addText(text);
    		}else{
    			myConsole.addText(text);
    		}
    	}
    }
    
    void setGTXConsole(GTXConsole mConsole) {
    	myConsole = mConsole;
    	if ( ! logCache.equals("") ) {
    		myConsole.addText(logCache);
    		logCache = "";
    	}
    }
    
    void forceLogCache() {
    	if ( myConsole != null ) {
    		myConsole.addText(logCache);
    		logCache = null;
    	}
    }
    
    @Override
    public void run () {
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
    }
    
    public int openConnection() {
    	if ( hostExist) {
    		try {
    			dout = new DataOutputStream(serverConn.getOutputStream());
    			din = new DataInputStream(serverConn.getInputStream());
    			logToConsole("connection open");
    			return 0;
    		}catch (Exception e) {
    			logToConsole("open connection failed: " + e);
    			return 1;
    		}
    	}else{
    		logToConsole("Host not exists!");
    		return 1;
    	}
    }
    
    public int closeConnection() {
    	if ( hostExist ) {
    		try {
    			dout.writeUTF(quit);
    			din.close();
    			dout.close();
    			logToConsole("connection closed");
    			return 0;
    		}catch (Exception e) {
    			logToConsole("close connection failed: " + e);
    			return 1;
    		}
    	}else{
    		logToConsole("Host not exists!");
    		return 1;
    	}
    }
    
    public int checkServerConnection() {
    	if ( hostExist ) {
    		try {
    			logToConsole("sending ping ...");
    			dout.writeUTF(ping);
    			logToConsole("awaiting pong ...");
    			String pingResult = din.readUTF();
    			logToConsole(pingResult);
    			if ( pingResult.equals(pong)) {
    				logToConsole("pong received.");
    				return 0;
    			}else {
    				logToConsole("server doesn't working.");
    				return 1;
    			}
    		}catch (Exception e) {
    			logToConsole("ping failed " + e);
    			return 1;
    		}
    	}else{
    		logToConsole("Host not exists!");
    		return 1;
    	}
    }
    
    public String sendText(String text) {
    	String result = "unknown command!";
    	if ( text.equals("ping") ){
    		try {
    			dout.writeUTF(ping);
    			result = din.readUTF();
    			if ( result.equals(pong) ) {
    				result = "pong";
    			}
    			dout.writeUTF(quit);
    		}catch (Exception e) {
    			logToConsole("Sending failed!");
    			return "Sending failed!";
    		}
    	}
    	return result;
    }
/*    public int tex2pdf(String fileName, int id) {
    	
    }*/
    
}