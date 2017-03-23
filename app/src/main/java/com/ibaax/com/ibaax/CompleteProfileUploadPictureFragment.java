package com.ibaax.com.ibaax;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.io.File;

import Entity.AppGlobal;
import Plugins.TextBoxHandler;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompleteProfileUploadPictureFragment extends Fragment {

    public static boolean isProfileResumed = false;
    public static boolean isCoverResumed = false;
    Context context;
    AppPrefs pref;
    ImageView imgCoverimg;
    RelativeLayout relChngcvr;
    RelativeLayout relChngimg;
    ImageView imgMyProfileImage;
    Button btnSavePhoto;
    ImageLoader loader = AppController.getInstance().getImageLoader();

    public CompleteProfileUploadPictureFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_complete_profile_upload_picture, container, false);
        context = getActivity();
        pref = new AppPrefs(context);
        isProfileResumed = false;
        isCoverResumed = false;
        findViews(rootView);


        return rootView;
    }


    private void findViews(View rootView) {

        imgCoverimg = (ImageView) rootView.findViewById(R.id.img_coverimg);
        relChngcvr = (RelativeLayout) rootView.findViewById(R.id.rel_chngcvr);
        relChngimg = (RelativeLayout) rootView.findViewById(R.id.rel_chngimg);
        imgMyProfileImage = (ImageView) rootView.findViewById(R.id.img_profile_image);
        btnSavePhoto = (Button) rootView.findViewById(R.id.btn_save_photo);

        btnSavePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CompleteProfileActivity) context).changePager();
            }
        });
    }

    void getMyImage() {
        if (isProfileResumed) {
            if (pref.getUserCategory().equals("2") || pref.getUserCategory().equals("4")) {
                String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath() + "/" + "ibaax_cache" + "/" + TextBoxHandler.IsNullOrEmpty(CropImageActivity.filename);
                final File f = new File(SDCARD_PATH);
                imgMyProfileImage.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
                loader.get(AppGlobal.ImageHost + "/agency/showcompanylogo/" + pref.getCompanyID() + "?logofilename="
                                + TextBoxHandler.IsNullOrEmpty(CropImageActivity.filename).replace(" ", "%20"),
                        new ImageLoader.ImageListener() {
                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                f.delete();
                                if (response.getBitmap() != null) {
                                    imgMyProfileImage.setImageBitmap(response.getBitmap());
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
            } else {
                String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath() + "/" + "ibaax_cache" + "/" + TextBoxHandler.IsNullOrEmpty(CropImageActivity.filename);
                final File f = new File(SDCARD_PATH);
                imgMyProfileImage.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
                loader.get(AppGlobal.ImageHost + "/AppAccess/User/ShowUserPhoto?Id=" + pref.getUserID() + "&PhotoFileName="
                                + TextBoxHandler.IsNullOrEmpty(CropImageActivity.filename).replace(" ", "%20"),
                        new ImageLoader.ImageListener() {
                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                f.delete();
                                if (response.getBitmap() != null) {
                                    imgMyProfileImage.setImageBitmap(response.getBitmap());
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
            }
        }
        if (isCoverResumed) {
            if (pref.getUserCategory().equals("2") || pref.getUserCategory().equals("4")) {
                String SDCARD_COVERPATH = Environment.getExternalStorageDirectory().getPath() + "/" + "ibaax_cache" + "/" + TextBoxHandler.IsNullOrEmpty(CropCoverImageActivity.COVERNAME);
                final File Cover_f = new File(SDCARD_COVERPATH);
                imgCoverimg.setImageBitmap(BitmapFactory.decodeFile(Cover_f.getAbsolutePath()));
                loader.get(AppGlobal.ImageHost + "/CRM/Company/ShowCompanyCoverPhoto/" + pref.getCompanyID() + "?CoverPhotoFileName="
                                + TextBoxHandler.IsNullOrEmpty(CropCoverImageActivity.COVERNAME).replace(" ", "%20"),
                        new ImageLoader.ImageListener() {
                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                Cover_f.delete();
                                if (response.getBitmap() != null) {
                                    imgCoverimg.setImageBitmap(response.getBitmap());
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
            } else {
                String SDCARD_COVERPATH = Environment.getExternalStorageDirectory().getPath() + "/" + "ibaax_cache" + "/" + TextBoxHandler.IsNullOrEmpty(CropCoverImageActivity.COVERNAME);
                final File Cover_f = new File(SDCARD_COVERPATH);
                imgCoverimg.setImageBitmap(BitmapFactory.decodeFile(Cover_f.getAbsolutePath()));
                loader.get(AppGlobal.ImageHost + "/CRM/Contact/ShowContactCoverPhoto/" + pref.getContactID() + "?CoverPhotoFileName="
                                + TextBoxHandler.IsNullOrEmpty(CropCoverImageActivity.COVERNAME).replace(" ", "%20"),
                        new ImageLoader.ImageListener() {
                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                Cover_f.delete();
                                if (response.getBitmap() != null) {

                                    imgCoverimg.setImageBitmap(response.getBitmap());

                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
            }


        }

        imgMyProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder photodialog = new AlertDialog.Builder(context);

                photodialog.setMessage("Take a new photo or select one from gallery.")
                        .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(context, CropImageActivity.class);
                                intent.putExtra("Type", "Camera");
                                startActivity(intent);
                            }
                        }).setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isProfileResumed = true;
                        Intent intent = new Intent(context, CropImageActivity.class);
                        intent.putExtra("Type", "Gallery");
                        startActivity(intent);
                    }
                });
                AlertDialog alert = photodialog.create();
                alert.show();
            }
        });
        relChngcvr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder coverdialog = new AlertDialog.Builder(context);

                coverdialog.setTitle("Change Cover Photo").setMessage("Take a new photo or select one from gallery.")
                        .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(context, CropCoverImageActivity.class);
                                intent.putExtra("Type", "Camera");
                                startActivity(intent);
                            }
                        }).setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isCoverResumed = true;
                        Intent intent = new Intent(context, CropCoverImageActivity.class);
                        intent.putExtra("Type", "Gallery");
                        startActivity(intent);
                    }
                });
                AlertDialog alert = coverdialog.create();
                alert.show();
            }
        });

    }

    @Override
    public void onResume() {
        try {
            super.onResume();
            getMyImage();
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
    }

}
