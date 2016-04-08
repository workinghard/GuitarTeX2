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
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.ResourceBundle;

public class GuitarTeX2Convert {

    public static void main(String[] args) {
        ResourceBundle resbundle = ResourceBundle.getBundle("GuitarTeX2strings", Locale.getDefault());
        String help = ""
                + resbundle.getString("appVersion") + "\n"
                + "Usage: \n"
                + "        GuitarTeX2Converter  -h               help (this screen)\n"
                + "        GuitarTeX2Converter  -f <file.gtx>    convert gtx file and print to std out\n"
                + "\n";

        if (args.length != 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("--help") || args[i].equals("-help") || args[i].equals("-h") || args[i].equals("--h")) {
                    System.out.println(help);
                    System.exit(0);
                }
                if (args[i].equals("-f") || args[i].equals("--f") || args[i].equals("-file") || args[i].equals("--file")) {
                    if (i + 1 < args.length) {
                        String fileName = args[i + 1];
                        if (!fileName.equals("")) {
                            File f = new File(fileName);
                            if (f.canRead()) {
                                GuitarTeX2Convert guitarTeX2Convert = new GuitarTeX2Convert(f);
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
            System.out.println(help);
            System.exit(0);
        }
    }

    public GuitarTeX2Convert(File f) {
        String mTextArea = "";
        try {
            /*InputStreamReader in = new InputStreamReader(fis, Charset.forName("UTF-8"));
            while ( in.read() != -1) {
            char c = (char)in.read();
            mTextArea = mTextArea + c;
            }*/
            try (FileInputStream fis = new FileInputStream(f); /*InputStreamReader in = new InputStreamReader(fis, Charset.forName("UTF-8"));
            while ( in.read() != -1) {
            char c = (char)in.read();
            mTextArea = mTextArea + c;
            }*/ BufferedReader in = new BufferedReader(
                    new InputStreamReader(new FileInputStream(f), Charset.forName("UTF-8")))) {
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

            try (OutputStreamWriter out = new OutputStreamWriter(System.out, Charset.forName("UTF-8"))) {
                out.write(mGTXParser.getMyTeXFile());
            }

        } catch (Exception e) {
            System.err.println("File input error:" + e);
        }
    }

}
