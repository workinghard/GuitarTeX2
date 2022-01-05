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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

class FileTransfer {

    // Debug 0/1
    int debug = 0;

	//	 Define commands
    //private static String quit = "CMD:123_QUIT_123";
    //private static String transfer = "CMD:123_TRANSFER_123";
    private static String ok = "CMD:123_OK_123";
    private static String failed = "CMD:123_FAILED_123";

    // Father ID
    int fId;

    // Streams
    DataInputStream inStream;
    DataOutputStream outStream;

    // Main - Constructor
    public FileTransfer(int id, DataInputStream inputStream, DataOutputStream outputStream) {
        fId = id;
        inStream = inputStream;
        outStream = outputStream;
    }

    public FileTransfer(int id, int mDebug, DataInputStream inputStream, DataOutputStream outputStream) {
        debug = mDebug;
        fId = id;
        inStream = inputStream;
        outStream = outputStream;
    }

    @SuppressWarnings("UnusedAssignment")
    public int sendFile(String myFile) {
        RandomAccessFile in;

        long length;
        String fileLengthResult;

        int zipResult = gzipFile(myFile, false);
        if (zipResult == 0) {
            myFile = myFile + ".gz";
        } else {
            sendMsg("Filetransfer[" + fId + "]: can't gzip file");
            try {
                outStream.writeInt(0);
                fileLengthResult = inStream.readUTF();
            } catch (Exception e) {
                sendMsg("Filetransfer[" + fId + "]: stream error" + e);
                cleanTmp(myFile);
                return 1;
            }
            cleanTmp(myFile);
            return 1;
        }

        try {
            in = new RandomAccessFile(myFile, "r");
            length = in.length();
        } catch (Exception g) {
            sendMsg("Filetransfer[" + fId + "]: can't read file" + g);
            try {
                outStream.writeInt(0);
                fileLengthResult = inStream.readUTF();
            } catch (Exception h) {
                sendMsg("Filetransfer[" + fId + "]: stream error" + h);
                cleanTmp(myFile);
                return 1;
            }
            cleanTmp(myFile);
            return 1;
        }
        if (length > Integer.MAX_VALUE) {
            // File is too large
            sendMsg("Filetransfer[" + fId + "]: File is too large");
            cleanTmp(myFile, in);
            return 1;
        } else {
            int myFileLength = (int) length;
            sendMsg("Filetransfer[" + fId + "]: sending fille length: " + myFileLength);
            try {
                outStream.writeInt(myFileLength);
                fileLengthResult = inStream.readUTF();
            } catch (Exception i) {
                sendMsg("Filetransfer[" + fId + "]: stream error" + i);
                cleanTmp(myFile, in);
                return 1;
            }
            if (fileLengthResult.equals(ok)) {
                try {
                    sendMsg("Filetransfer[" + fId + "]: send a file...");
                    byte b;
                    for (int i = 0; i < myFileLength; i++) {
                        b = in.readByte();
                        outStream.writeByte(b);
                    }
                    in.close();
                    File f = new File(myFile);
                    f.delete();
                    String fileTransferResult = inStream.readUTF();
                    if (fileTransferResult.equals(failed)) {
                        sendMsg("Filetransfer[" + fId + "]: sending file failed: " + fileTransferResult);
                        cleanTmp(myFile);
                        return 1;
                    }
                } catch (Exception j) {
                    sendMsg("Filetransfer[" + fId + "]: stream error" + j);
                    cleanTmp(myFile);
                    return 1;
                }
            } else {
                sendMsg("Filetransfer[" + fId + "]: sending file length failed: " + fileLengthResult);
                cleanTmp(myFile, in);
                return 1;
            }
        }
        return 0;
    }

    public int receiveFile(String myFile) {
        int fileLength;
        RandomAccessFile out;

        sendMsg("Filetransfer[" + fId + "]: awaiting filelength ...");
        try {
            fileLength = inStream.readInt();
        } catch (IOException e) {
            sendMsg("Filetransfer[" + fId + "]: stream error " + e);
            cleanTmp(myFile);
            return 1;
        }
        sendMsg("Filetransfer[" + fId + "]: file length = " + fileLength);
        if (fileLength > 0) {
            try {
                out = new RandomAccessFile(myFile + ".gz", "rw");
            } catch (IOException g) {
                sendMsg("Filetransfer[" + fId + "]: can't write to a file " + g);
                try {
                    outStream.writeUTF(failed);
                } catch (Exception h) {
                    sendMsg("Filetransfer[" + fId + "]: stream error " + h);
                }
                cleanTmp(myFile);
                return 1;
            }
            try {
                outStream.writeUTF(ok);
            } catch (Exception i) {
                sendMsg("Filetransfer[" + fId + "]: stream error " + i);
                cleanTmp(myFile, out);
                return 1;
            }
            sendMsg("Filetransfer[" + fId + "]: awaiting file...");
            try {
                byte b;
                for (int i = 0; i < fileLength; i++) {
                    b = inStream.readByte();
                    out.write(b);
                }
                out.close();
            } catch (Exception j) {
                sendMsg("Filetransfer[" + fId + "]: file transfer failed " + j);
                try {
                    outStream.writeUTF(failed);
                } catch (Exception k) {
                    sendMsg("Filetransfer[" + fId + "]: stream error " + k);
                }
                cleanTmp(myFile);
                return 1;
            }
            try {
                outStream.writeUTF(ok);
                int gunzipResult = gunzipFile(myFile + ".gz", true);
                if (gunzipResult != 0) {
                    sendMsg("Filetransfer[" + fId + "]: gunzip failed");
                    cleanTmp(myFile);
                    return 1;
                }
            } catch (Exception l) {
                sendMsg("Filetransfer[" + fId + "]: stream error " + l);
                cleanTmp(myFile);
                return 1;
            }
            sendMsg("Filetransfer[" + fId + "]: file transfer complete.");
        } else {
            try {
                outStream.writeUTF(failed);
            } catch (Exception f) {
                sendMsg("Filetransfer[" + fId + "]: file length invalid");
                cleanTmp(myFile);
                return 1;
            }
        }
        return 0;
    }

    private int gzipFile(String myFile, boolean delSource) {
        int read;
        byte[] data = new byte[1024];
        sendMsg("Filetransfer[" + fId + "]: gzip file");
        try {
            File f = new File(myFile);
            GZIPOutputStream out;
            try (FileInputStream in = new FileInputStream(f)) {
                out = new GZIPOutputStream(new FileOutputStream(myFile + ".gz"));
                while ((read = in.read(data, 0, 1024)) != -1) {
                    out.write(data, 0, read);
                }
            }
            out.close();
            if (delSource == true) {
                f.delete();
            }
        } catch (Exception e) {
            sendMsg("Filetransfer[" + fId + "]: gzipping file failed " + e);
            return 1;
        }

        sendMsg("Filetransfer[" + fId + "]: gzip file");
        return 0;
    }

    private int gunzipFile(String myGZFile, boolean delSource) {
        int read;
        byte[] data = new byte[1024];

        sendMsg("Filetransfer[" + fId + "]: gunzip file");

        try {
            File f = new File(myGZFile);
            FileOutputStream out;
            try (GZIPInputStream in = new GZIPInputStream(new FileInputStream(f))) {
                String myFile;
                if (myGZFile.endsWith(".gz")) {
                    myFile = myGZFile.substring(0, myGZFile.length() - 3);
                } else {
                    myFile = myGZFile;
                }   out = new FileOutputStream(myFile);
                while ((read = in.read(data, 0, 1024)) != -1) {
                    out.write(data, 0, read);
            }
            }
            out.close();
            if (delSource == true) {
                f.delete();
            }
        } catch (Exception e) {
            sendMsg("Filetransfer[" + fId + "]: gunzipping file failed " + e);
            return 1;
        }
        return 0;
    }

    private void sendMsg(String msg) {
        if (debug == 1) {
            System.out.println(msg);
        }
    }

    private void cleanTmp(String fileName, RandomAccessFile file) {
        try {
            new File(fileName).delete();
            file.close();
        } catch (Exception e) {
            sendMsg("can't clean tmp file: " + e);
        }
    }

    private void cleanTmp(String fileName) {
        try {
            new File(fileName).delete();
        } catch (Exception e) {
            sendMsg("can't clean tmp file: " + e);
        }
    }
}
