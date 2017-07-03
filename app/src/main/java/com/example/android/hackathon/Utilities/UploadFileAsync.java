package com.example.android.hackathon.Utilities;

/**
 * Created by Tommy on 7/2/17.
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class UploadFileAsync extends AsyncTask<String, Void, String> {
    private final String PHP_URL = "http://jagpal-development.com/food_truck/php/upload_file.php";

    @Override
    protected String doInBackground(String... params) {
        try {
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;

            String fileSource = params[0];
            String fileDest   = params[1];
            String fileName   = params[2];

            HttpURLConnection connection = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int maxBufferSize = 1 * 1024 * 1024;

            File sourceFile = new File(fileSource);

            if (sourceFile.isFile()) {
                Log.e("ISFILE", "Inside this");
                try {
                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(PHP_URL);

                    // Open a HTTP connection to the URL
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true); // Allow Inputs
                    connection.setDoOutput(true); // Allow Outputs
                    connection.setUseCaches(false); // Don't use a Cached Copy

                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                    connection.setRequestProperty("Content-Type",
                            "multipart/form-data;boundary=" + boundary);
                    connection.setRequestProperty("uploaded_file", fileSource);

                    dos = new DataOutputStream(connection.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    /* POST the actual File to the PHP script */
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileSource + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    // create a buffer of maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    /* End POST actual file */

                    /* POST the File Name to the PHP script */
                    dos.writeBytes("Content-Disposition: form-data; name=\"filename\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(fileName + lineEnd);
                    dos.writeBytes(twoHyphens + boundary +  lineEnd);
                    /* End POST File Name */

                    /* POST the File Destination to the PHP script */
                    dos.writeBytes("Content-Disposition: form-data; name=\"dest\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(fileDest + lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    /* End POST File Destination */

                    // Responses from the server (code and message)
                    int serverResponseCode = connection.getResponseCode();
                    String serverResponseMessage = connection.getResponseMessage();

                    if (serverResponseCode == 200) {
                        Log.v("RESPONSE", serverResponseMessage);
                    }

                    // close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "Executed";
    }
}
