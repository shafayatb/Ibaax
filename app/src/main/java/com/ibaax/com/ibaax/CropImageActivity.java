package com.ibaax.com.ibaax;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.isseiaoki.simplecropview.CropImageView;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import Entity.AppGlobal;
import Plugins.AndroidMultiPartEntity;
import Plugins.JSONParser;
import Plugins.getPath;
import UI.MessageBox;

public class CropImageActivity extends AppCompatActivity {

    public static String filename = "";
    String LocalFileName;
    AppPrefs pref;
    CropImageView ProfilePic;
    long totalSize = 0;
    File finalFile;
    Bitmap bitmap1;
    Uri iamgeuri = null;
    ContentValues values;
    FloatingActionButton fabDone;
    String URL, paramID, ConID;
    private ProgressDialog progressBar;
    private int PICK_IMAGE_REQUEST = 1;
    private int REQUEST_CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Crop Image");
        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Changing..");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setCancelable(false);
        ProfilePic = (CropImageView) findViewById(R.id.cropImageView);
        values = new ContentValues();
        pref = new AppPrefs(this);
        fabDone = (FloatingActionButton) findViewById(R.id.fab_upload);
        if (getIntent().getStringExtra("Type").equals("Gallery")) {
            //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }

        if (getIntent().getStringExtra("Type").equals("Camera")) {
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            iamgeuri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, iamgeuri);
            startActivityForResult(intent, REQUEST_CAMERA);
        }

        if (pref.getUserCategory().equals("2") || pref.getUserCategory().equals("4")) {
            URL = AppGlobal.localHost + "/api/MCompany/UploadCompanyLogo";
            paramID = "CompanyID";
            ConID = pref.getCompanyID();
        } else {
            URL = AppGlobal.localHost + "/api/User/UploadPictureAjax";
            paramID = "ContactID";
            ConID = pref.getContactID();
        }

        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CropImageActivity.this);
                builder.setTitle("Upload Image").setMessage("Do you want to change your profile picture?")
                        .setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final Bitmap croppedImage = ProfilePic.getCroppedBitmap();
                                saveToFile(croppedImage);
                                new UploadFileToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });
    }

    void saveToFile(Bitmap crooped) {

        OutputStream fOut = null;
        Uri outputFileUri;
        String selectedImagePath = null;
        try {
            File root = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "ibaax_cache" + File.separator);
            root.mkdirs();
            File sdImageMainDirectory = new File(root, LocalFileName);
            outputFileUri = Uri.fromFile(sdImageMainDirectory);
            selectedImagePath = getPath.getPath(outputFileUri, this);
            Log.v("esty", "Cropped URI: " + selectedImagePath);
            finalFile = new File(selectedImagePath);
            fOut = new FileOutputStream(sdImageMainDirectory);
        } catch (Exception e) {
            progressBar.hide();
            Log.e("error", e.getMessage());
            Toast.makeText(this, "Error occured. Please try again later.",
                    Toast.LENGTH_SHORT).show();
        }

        try {
            crooped.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int ID = item.getItemId();
        if (ID == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {

                try {
                    String selectedImagePath = null;
                    Uri selectedImageUri = data.getData();

                    if (selectedImageUri.toString().contains("%")) {
                        if (selectedImageUri.toString().startsWith("content://com.google.android.apps.photos.content")) {
                            Log.v("esty", "URI: " + selectedImageUri.toString());
                            selectedImagePath = getPath.getImageUrlWithAuthority(this, selectedImageUri);

                        } else {


                            Log.v("esty", "URI: " + selectedImageUri.toString());
                            String[] UriArray = selectedImageUri.toString().split("%3A");

                            String newURIString = "content://media/external/images/media/" + UriArray[1];
                            Uri newURI = Uri.parse(newURIString);
                            Log.v("esty", "URI: " + newURI);
                            selectedImagePath = getPath.getPath(newURI, this);
                        }
                    } else {
                        selectedImagePath = getPath.getPath(selectedImageUri, this);
                    }

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bitmap1 = BitmapFactory.decodeFile(selectedImagePath, options);
                    int nh = (int) (bitmap1.getHeight() * (512.0 / bitmap1.getWidth()));
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap1, 512, nh, true);
                    finalFile = new File(selectedImagePath);
                    ProfilePic.setImageBitmap(scaled);
                    LocalFileName = selectedImagePath.substring(selectedImagePath.lastIndexOf("/") + 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == REQUEST_CAMERA) {
                try {
                    Bitmap bitmap = null;
                    try {
                        bitmap = getPath.decodeUri(iamgeuri, this);
                    } catch (FileNotFoundException e) {
                        MessageBox.Show(this, "File not found");
                    }
                    Uri tempUri = getPath.getImageUri(this, bitmap);
                    finalFile = new File(getPath.getRealPathFromURI(this, tempUri));
                    ProfilePic.setImageBitmap(bitmap);
                    LocalFileName = finalFile.getName();
                } catch (Exception e) {

                }
            }
        }
    }


    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
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
                entity.addPart("photo", new FileBody(sourceFile, ContentType.create("image/gif"), sourceFile.getName()));
                // Extra parameters if you want to pass to server
                entity.addPart(paramID, new StringBody(ConID));

                totalSize = entity.getContentLength();
                //String Header = "Bearer " + Base64.encodeToString(pref.gettOAuthToken().getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);

                httppost.setHeader("Authorization", "Bearer " + pref.gettOAuthToken());


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
                    progressBar.hide();
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
                if (!JSONParser.parseString(jo, "PhotoFileName").equals("")) {
                    progressBar.hide();
                    Toast.makeText(CropImageActivity.this, "Profile Photo Changed", Toast.LENGTH_SHORT).show();
                    CompleteProfileUploadPictureFragment.isProfileResumed = true;
                    filename = LocalFileName;
                    finish();
                } else if (!JSONParser.parseString(jo, "LogoFileName").equals("")) {
                    progressBar.hide();
                    Toast.makeText(CropImageActivity.this, "Profile Photo Changed", Toast.LENGTH_SHORT).show();
                    CompleteProfileUploadPictureFragment.isProfileResumed = true;
                    filename = LocalFileName;
                    finish();
                } else {
                    MessageBox.Show(CropImageActivity.this, "Failed to change profile picture.");
                    progressBar.hide();
                }
            } catch (JSONException e) {

            }
            super.onPostExecute(result);
        }
    }
}
