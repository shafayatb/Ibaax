package com.ibaax.com.ibaax;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import Entity.AppGlobal;
import Event.IHttpResponse;
import ServiceInvoke.DownloadBitmap;
import ServiceInvoke.HttpRequest;
import UI.MessageBox;

public class EditPhotoActivity extends AppCompatActivity {

    public static String PhotoFileName = "";
    ImageView PropertyImage;
    Button btnSetCover;
    EditText txtPhotoTitle;
    String PhotoID, PhotoName, PhotoURL;
    String FileName = "";
    ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Changing..");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setCancelable(true);

        PropertyImage = (ImageView) findViewById(R.id.img_edit_photo_title);
        btnSetCover = (Button) findViewById(R.id.btn_set_cover);
        txtPhotoTitle = (EditText) findViewById(R.id.txt_propertyphoto_title);
        PhotoURL = getIntent().getStringExtra("PhotoURL");
        PhotoID = getIntent().getStringExtra("PhotoID");
        PhotoName = getIntent().getStringExtra("PhotoName");
        FileName = getIntent().getStringExtra("FileName");
        txtPhotoTitle.setText(PhotoName);

        new DownloadBitmap(this, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                Bitmap bitmap = (Bitmap) response;
                int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                PropertyImage.setImageBitmap(scaled);
            }

            @Override
            public void RequestFailed(String response) {

            }
        }).Download(PhotoURL);

    }

    public void btnChangeTitleSave(View view) {

        try {

            String URL = AppGlobal.localHost + "/api/MProperty/UpdatePhotoTitle";
            HashMap<String, String> jsonParams = new HashMap<String, String>();
            jsonParams.put("Title", txtPhotoTitle.getText().toString());
            jsonParams.put("Id", PhotoID);
            // String jsonParams="Title="+txtPhotoTitle.getText().toString()+"&Id="+PhotoID;
            new HttpRequest(this, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    try {
                        JSONObject jo = new JSONObject((String) response);

                        if (jo.getBoolean("success")) {
                            MessageBox.Show(EditPhotoActivity.this, "Photo Title has been updated");
                        }

                    } catch (JSONException e) {


                    }
                }

                @Override
                public void RequestFailed(String response) {

                }
            }).HttpPost(jsonParams, URL);

        } catch (Exception e) {


        }

    }

    public void btn_Change_Cover(View view) {

        try {
            String URL = AppGlobal.localHost + "/api/MProperty/MakePropertyCoverPhoto?photoPortfolioId=" + PhotoID
                    + "&propertyId=" + AddPhotoActivity.PropertyID;

            new HttpRequest(this, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    try {
                        JSONObject jo = new JSONObject((String) response);

                        if (jo.getBoolean("success")) {
                            MessageBox.Show(EditPhotoActivity.this, "This Image Has Been Set As Cover Photo");
                            PhotoFileName = jo.getString("data");
                        }

                    } catch (JSONException e) {


                    }
                }

                @Override
                public void RequestFailed(String response) {

                }
            }).HttpPostProperty(null, URL);
        } catch (Exception e) {

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                finish();
                return true;
            case R.id.action_delete:
                final String URL = AppGlobal.localHost + "/api/MProperty/BulkDeletePhoto";

                final HashMap<String, String> jsonParams = new HashMap<>();
                jsonParams.put("Ids", PhotoID);
                jsonParams.put("PropertyId", AddPhotoActivity.PropertyID);
                AlertDialog.Builder builder = new AlertDialog.Builder(EditPhotoActivity.this);
                builder.setMessage("Are you sure you want to delete this photo?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do some thing here which you need
                                new HttpRequest(EditPhotoActivity.this, new IHttpResponse() {
                                    @Override
                                    public void RequestSuccess(Object response) {
                                        try {
                                            JSONObject jo = new JSONObject((String) response);

                                            if (jo.getBoolean("success")) {
                                                Toast.makeText(EditPhotoActivity.this, jo.getString("Message"), Toast.LENGTH_LONG).show();
                                                //NavigationDrawerMainActivity.LastSelectedTab=0;
                                                finish();
                                            }
                                        } catch (Exception e) {
                                            Log.e("esty", "Delete Success Error: " + e.getMessage());
                                        }
                                    }

                                    @Override
                                    public void RequestFailed(String response) {
                                        Log.e("esty", "Delete Failed: " + response);
                                    }
                                }).HttpPostProperty(jsonParams, URL);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog alert = builder.create();
                alert.show();


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
