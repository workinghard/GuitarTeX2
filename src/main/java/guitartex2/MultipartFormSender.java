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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class MultipartFormSender {

    public static ServerResponse sendMultipartForm(String requestURL, String filePath, String fileFormFieldName, String fieldName, String fieldValue) throws IOException {
        String boundary =  "*****"+Long.toString(System.currentTimeMillis())+"*****";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        ServerResponse myResponse = new ServerResponse();

        URL url = new URL(requestURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setUseCaches(false);
        connection.setDoOutput(true); // Indicates POST method
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("User-Agent", "GuitarTex2 v3.5");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            // Write form fields
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"" + lineEnd + lineEnd);
            outputStream.writeBytes(fieldValue + lineEnd);


            // Write file
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + fileFormFieldName + "\"; filename=\"" + new File(filePath).getName() + "\"\r\n");
            // Ensure the file part has an appropriate Content-Type, if necessary
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            try (BufferedInputStream fileStream = new BufferedInputStream(new FileInputStream(filePath))) {
                byte[] buffer = new byte[8192]; // Consider adjusting the buffer size
                int bytesRead;
                while ((bytesRead = fileStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.writeBytes("\r\n");
            }

           // End of multipart/form-data.
           outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
        }

        // Handle the response from the server
        int responseCode = connection.getResponseCode();
        myResponse.setUrlRC(responseCode);
        //System.out.println("Response Code: " + responseCode);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
                response.append(System.lineSeparator());
            }
            // Try to decode the response
            //JsonObject jsonObject = Json.createReader(new InputStream()).readObject();
             
            JSONObject myJsonObject = new JSONObject(response.toString());
            myResponse.setCmdRC(myJsonObject.getInt("cmd_rc"));
            myResponse.setMsg(myJsonObject.getString("msg"));
            myResponse.setDownloadURL(myJsonObject.getString("download_url"));
            
            //myResponse.setMsg();
            //System.out.println("Response: " + response.toString());
        } catch (Exception e) {
            System.out.println("Json failed: " + e.getMessage());
        } finally {
            connection.disconnect();
        }
        return myResponse;
    }
}
