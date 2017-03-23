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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.isseiaoki.simplecropview.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import Entity.AppGlobal;
import Event.IEvent;
import Plugins.getPath;
import ServiceInvoke.UploadFileToServer;
import UI.MessageBox;

public class CropCoverImageActivity extends AppCompatActivity {

    public static String COVERNAME = "";
    String FileName;
    AppPrefs pref;
    CropImageView CoverPic;
    long totalSize = 0;
    File finalFile;
    Bitmap bitmap1;
    Uri iamgeuri = null;
    ContentValues values;
    FloatingActionButton fabDone;
    ProgressDialog progressBar;
    int PICK_IMAGE_REQUEST = 1;
    int REQUEST_CAMERA = 2;
    String URl, paramID, ConID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_cover_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Crop Image");
        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Changing...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setCancelable(false);
        CoverPic = (CropImageView) findViewById(R.id.cropCoverImageView);
        values = new ContentValues();
        pref = new AppPrefs(this);
        fabDone = (FloatingActionButton) findViewById(R.id.fab_cover_upload);
        if (getIntent().getStringExtra("Type").equals("Gallery")) {
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
            URl = AppGlobal.localHost + "/api/MCompany/UploadCompanyCoverPhoto";
            paramID = "CompanyID";
            ConID = pref.getCompanyID();
        } else {
            URl = AppGlobal.localHost + "/api/User/UploadUserCoverPhoto";
            paramID = "ContactID";
            ConID = pref.getContactID();
        }

        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CropCoverImageActivity.this);
                builder.setTitle("Upload Image").setMessage("Do you want to change your Cover Picture?")
                        .setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final Bitmap croppedImage = CoverPic.getCroppedBitmap();
                                saveToFile(croppedImage);
                                new UploadFileToServer(CropCoverImageActivity.this, progressBar, finalFile,
                                        URl, paramID, ConID,
                                        new IEvent() {
                                            @Override
                                            public void onClick(Object obj) {
                                                CompleteProfileUploadPictureFragment.isCoverResumed = true;
                                                COVERNAME = FileName;
                                                //finalFile.delete();
                                                finish();
                                            }
                                        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
            File sdImageMainDirectory = new File(root, FileName);
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
                    CoverPic.setImageBitmap(scaled);
                    FileName = selectedImagePath.substring(selectedImagePath.lastIndexOf("/") + 1);
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
                    CoverPic.setImageBitmap(bitmap);
                    FileName = finalFile.getName();
                } catch (Exception e) {

                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
