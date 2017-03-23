package com.ibaax.com.ibaax;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapter.AddPhotosGridAdapter;
import Entity.AppGlobal;
import Entity.PropertyPhotoPortfolio;
import Event.IEvent;
import Event.IHttpResponse;
import Plugins.AndroidMultiPartEntity;
import Plugins.JSONParser;
import Plugins.MuliPartReqeust;
import Plugins.TextBoxHandler;
import Plugins.getPath;
import ServiceInvoke.HttpRequest;
import UI.MessageBox;

public class AddPhotoActivity extends AppCompatActivity {
    public static String PropertyID;
    List<PropertyPhotoPortfolio> PhotosList = new ArrayList<>();
    GridView gridPhotos;
    long totalSize = 0;
    AddPhotosGridAdapter Photoadp;
    Uri fileImagePath;
    File finalFile;
    String filename = "";
    Bitmap bitmap1;
    AppPrefs prefs;
    ////////////////Camera Code//////////////////////////
    Uri iamgeuri = null;
    ContentValues values;
    private ProgressDialog progressBar;
    private int PICK_IMAGE_REQUEST = 1;
    private int REQUEST_CAMERA = 2;

    public static String getImageUrlWithAuthority(Context context, Uri uri) {
        InputStream is = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                return writeToTempImageAndGetPathUri(context, bmp).toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Uri writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Uploading..");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setCancelable(true);
        prefs = new AppPrefs(this);
        PropertyID = getIntent().getStringExtra("ID");
        gridPhotos = (GridView) findViewById(R.id.grid_add_photos);
        values = new ContentValues();

    }

    private void HttpRequestPhoto() {
        PropertyPhotoPortfolio po = new PropertyPhotoPortfolio();
        po.PropertyPhotoPortfolioID = 0;
        po.DocumentFileName = "";
        po.PhotoTitle = "";
        po.PropertyPhotoPortfolio = "";
        po.PhotoBitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.add_property_img));

        PhotosList.add(po);

        String URL = AppGlobal.localHost + "/api/MProperty/GetPropertyPhotoPortfolio?pid=" + PropertyID;

        new HttpRequest(this, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                try {
                    JSONArray jsonArray = new JSONArray((String) response);
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jo = jsonArray.getJSONObject(i);
                        final PropertyPhotoPortfolio p = new PropertyPhotoPortfolio();

                        p.PropertyPhotoPortfolioID = JSONParser.parseInt(jo, "PropertyPhotoPortfolioID");
                        p.PhotoTitle = JSONParser.parseString(jo, "PhotoTitle");
                        p.DocumentFileName = JSONParser.parseString(jo, "DocumentFileName");
                        //String PhotoURL = "http://www.rebaax.com" + JSONParser.parseString(jo, "PropertyPhotoPortfolio");
                        String PhotoURL = AppGlobal.ImageHost + "/" + JSONParser.parseString(jo, "PropertyPhotoPortfolio");
                        p.PropertyPhotoPortfolio = PhotoURL.replaceAll(" ", "%20");

                        PhotosList.add(p);
                    }
                    BindPhoto();
                } catch (JSONException e) {

                }

            }

            @Override
            public void RequestFailed(String response) {

            }
        }).MakeJsonArrayReq(URL);
    }

    void BindPhoto() {
        Photoadp = new AddPhotosGridAdapter(this, PhotosList, new IEvent() {
            @Override
            public void onClick(Object obj) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        gridPhotos.setAdapter(Photoadp);

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
                            selectedImagePath = getImageUrlWithAuthority(this, selectedImageUri);
                        } else {


                            Log.v("esty", "URI: " + selectedImageUri.toString());
                            String[] UriArray = selectedImageUri.toString().split("%3A");

                            String newURIString = "content://media/external/images/media/" + UriArray[1];
                            Uri newURI = Uri.parse(newURIString);
                            Log.v("esty", "URI: " + newURI);
                            selectedImagePath = getPath(newURI);
                        }
                    } else {
                        selectedImagePath = getPath(selectedImageUri);
                    }

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bitmap1 = BitmapFactory.decodeFile(selectedImagePath, options);
                    int nh = (int) (bitmap1.getHeight() * (512.0 / bitmap1.getWidth()));
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap1, 512, nh, true);
                    //finalFile = new File(selectedImagePath);
                    filename = selectedImagePath.substring(selectedImagePath.lastIndexOf("/") + 1);
                    saveToFile(scaled);
                    new UploadFileToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == REQUEST_CAMERA) {
                try {
                    Bitmap bitmap = null;
                    try {
                        bitmap = decodeUri(iamgeuri);
                    } catch (FileNotFoundException e) {
                        MessageBox.Show(this, "File not found");
                    }
                    Uri tempUri = getImageUri(this, bitmap);
                    finalFile = new File(getRealPathFromURI(tempUri));
                    new UploadFileToServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } catch (Exception e) {

                }
            }

        }
    }

    void saveToFile(Bitmap crooped) {

        OutputStream fOut = null;
        Uri outputFileUri;
        String selectedImagePath = null;
        try {
            File root = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "ibaax_cache" + File.separator);
            root.mkdirs();
            File sdImageMainDirectory = new File(root, filename);
            outputFileUri = Uri.fromFile(sdImageMainDirectory);
            selectedImagePath = getPath.getPath(outputFileUri, this);
            Log.v("esty", "Cropped URI: " + selectedImagePath);
            finalFile = new File(selectedImagePath);

            fOut = new FileOutputStream(sdImageMainDirectory);
        } catch (Exception e) {
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


    @SuppressWarnings("deprecation")
    public String getPath(Uri uri) {

        if (uri == null) {
            // TODO perform some logging or show user feedback
            return null;
        }

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                finish();
                return true;
            case R.id.action_camera:
                OpenCamera();
                return true;
            //break;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //region FailedMultipart
    public void HttpUploadFile(File f, String leadid) {
        try {

            // progressBar.setMax(100);
            progressBar.setProgress(1);
            progressBar.show();
            //String url="http://192.168.2.8:8181/test/uploadfile.php";
            String url = "http://api.ibaax.com/api/MProperty/SavePropertyPhoto/";

            ///File f = new File(getRealPathFromURI(path));
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", "Bearer " + prefs.gettOAuthToken());

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("Photos", f.getPath());
            params.put("ID", leadid);


            MuliPartReqeust.MultipartProgressListener listener = new MuliPartReqeust.MultipartProgressListener() {
                @Override
                public void transferred(long transfered, int progress) {
                    Log.v("Success", "" + transfered + " Progress" + progress);

                    progressBar.setProgress(progress);
                }
            };


            MuliPartReqeust mr2 = new MuliPartReqeust(this, url, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError arg0) {
                    // TODO Auto-generated method stub
                    if (arg0 instanceof ServerError) {
                        Toast.makeText(AddPhotoActivity.this, "error " + arg0.toString(), Toast.LENGTH_LONG).show();
                        progressBar.hide();
                    }
                    if (arg0 instanceof TimeoutError) {
                        MessageBox.Show(AddPhotoActivity.this, "error: " + arg0.getMessage());
                        progressBar.hide();
                    } else {
                        Toast.makeText(AddPhotoActivity.this, "error " + arg0.toString(), Toast.LENGTH_LONG).show();
                        progressBar.hide();
                    }
                }
            },
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //JSONObject obj = new JSONObject(response);
                                //String msg = obj.getString("Result");
                                MessageBox.Show(AddPhotoActivity.this, response.toString());
                                progressBar.hide();
                                //PhotosList.add(bitmap1);
                                //Photoadp.notifyDataSetChanged();
                            } catch (Exception ex) {
                                MessageBox.Show(AddPhotoActivity.this, "JSON parse exception");
                            }
                            //Clear();
                            //MessageBox.Show(context,response);

                        }
                    }, f, f.length(), params, headers,
                    "Photo", listener);


            AppController.getInstance().addToRequestQueue(mr2, "tag_json_obj");

        } catch (Exception ex) {
            MessageBox.Show(AddPhotoActivity.this, ex.getMessage());
        }
    }
//endregion


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_photo, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PhotosList.size() > 0) {
            PhotosList.clear();
        }
        HttpRequestPhoto();

    }

    private void OpenCamera() {

        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        iamgeuri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, iamgeuri);
        startActivityForResult(intent, REQUEST_CAMERA);

    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 600;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    /**
     * Uploading the file to server
     */
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
            HttpPost httppost = new HttpPost(AppGlobal.localHost + "/api/MProperty/SavePropertyPhoto/");

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
                entity.addPart("ID", new StringBody(PropertyID));
                totalSize = entity.getContentLength();
                String Header = "Bearer " + Base64.encodeToString(prefs.gettOAuthToken().getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);

                httppost.setHeader("Authorization", "Bearer " + prefs.gettOAuthToken());
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
                    progressBar.dismiss();
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
                progressBar.dismiss();
            } catch (IOException e) {
                responseString = e.toString();
                progressBar.dismiss();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.v("esty", "Response from server: " + result);

            try {
                JSONObject jo = new JSONObject(result);
                if (jo.getBoolean("success")) {
                    //PhotosList.add(bitmap1);
                    //Photoadp.notifyDataSetChanged();

                    progressBar.hide();
                    //MessageBox.Show(AddPhotoActivity.this, TextBoxHandler.IsNullOrEmpty(jo.getString("Message")));
                    Toast.makeText(AddPhotoActivity.this, TextBoxHandler.IsNullOrEmpty(jo.getString("Message")), Toast.LENGTH_LONG).show();
                    PhotosList.clear();
                    finalFile.delete();
                    HttpRequestPhoto();
                } else {
                    //MessageBox.Show(AddPhotoActivity.this, "Failed to upload photo.");
                    Toast.makeText(AddPhotoActivity.this, "Failed to upload photo.", Toast.LENGTH_LONG).show();
                    progressBar.dismiss();
                }
            } catch (JSONException e) {
                progressBar.dismiss();

            }
            super.onPostExecute(result);
        }
    }
}
