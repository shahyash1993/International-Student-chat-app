package com.example.yps.assignment_5;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadWebpageTask extends AsyncTask<String, Void, String> {
    String TAG = "mTag";
    static String finalResult = null;

    @Override
    protected String doInBackground(String... urls) {
        try {
            return downloadUrl(urls[0]);
        } catch (IOException e) {
            Log.e(TAG, "Error accessing " + urls[0], e);
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG, "Result: "+result);
        finalResult = result;
        //Display result here
    }

    private String downloadUrl(String urlString) throws IOException {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            int contentLength = urlConnection.getContentLength();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode != 200) {
                // handle error here
                return "Server not happy";
            }
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return readIt(in, contentLength);
        } catch (MalformedURLException badURL) {
            Log.e(TAG, "Bad URL", badURL);
        } catch (IOException io) {
            Log.e(TAG, "network issue", io);
        } finally {
            urlConnection.disconnect();
        }
        return "";//error
    }//endOfDownloadURL

    public String readIt(InputStream stream, int len) throws IOException,
            UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }//End of readIt

   /*
   * Getter
   * And
   * Setter
   *
   * */

    public String getFinalResult() {
        return finalResult;
    }

    public void setFinalResult(String finalResult) {
        this.finalResult = finalResult;
    }
}//End of class