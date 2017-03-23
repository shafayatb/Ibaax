package ServiceInvoke;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.ibaax.com.ibaax.AppPrefs;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import Event.IEvent;
import Plugins.AndroidMultiPartEntity;
import UI.MessageBox;

/**
 * Created by iBaax on 4/12/16.
 */
public class UploadFileToServer extends AsyncTask<Void, Integer, String> {

    Context context;
    ProgressDialog progressBar;
    AppPrefs pref;
    File finalFile;
    long totalSize;
    IEvent delegate;
    String URL;
    String ID;
    String paramID;

    public UploadFileToServer(Context context, ProgressDialog progressBar, File finalFile, String URL, String paramID, String ID,
                              IEvent delegate) {
        this.context = context;
        this.progressBar = progressBar;
        this.finalFile = finalFile;
        this.URL = URL;
        this.paramID = paramID;
        this.ID = ID;
        this.delegate = delegate;
        pref = new AppPrefs(context);
        totalSize = 0;
    }

    @Override
    protected void onPreExecute() {
        // setting progress bar to zero
        progressBar.setProgress(0);
        progressBar.show();
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        progressBar.setProgress(progress[0]);
    }

    @Override
    protected String doInBackground(Void... params) {
        return uploadFile();
    }

    @SuppressWarnings("deprecation")
    private String uploadFile() {
        String responseString = null;

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(URL);

        try {
            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                    new AndroidMultiPartEntity.ProgressListener() {

                        @Override
                        public void transferred(long num) {
                            publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });

            File sourceFile = new File(finalFile.getPath());

            // Adding file data to http body
            // entity.addPart("Photo", new FileBody(sourceFile));
            entity.addPart("Photo", new FileBody(sourceFile, ContentType.create("image/gif"), sourceFile.getName()));
            // Extra parameters if you want to pass to server
            entity.addPart(paramID, new StringBody(ID));
            totalSize = entity.getContentLength();
            String Header = "Bearer " + Base64.encodeToString(pref.gettOAuthToken().getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);

            httppost.setHeader("Authorization", "Bearer " + pref.gettOAuthToken());
            //httppost.setHeader("Authorization",Header);
            //httppost.setHeader("Content-Type", "multipart/form-data; boundary=****;charset=utf-8");


            httppost.setEntity(entity);

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(r_entity);
            } else {
                responseString = "Error occurred! Http Status Code: "
                        + statusCode;
            }

        } catch (ClientProtocolException e) {
            responseString = e.toString();
        } catch (IOException e) {
            responseString = e.toString();
        }

        return responseString;

    }

    @Override
    protected void onPostExecute(String result) {
        Log.v("esty", "Response from server: " + result);

        try {
            JSONObject jo = new JSONObject(result);
            if (jo.getString("CoverPhotoFileName") != null) {
                progressBar.hide();
                delegate.onClick(result);

            } else {
                MessageBox.Show(context, "Failed to change Property Picture");
            }
        } catch (JSONException e) {

        }
        super.onPostExecute(result);
    }
}
