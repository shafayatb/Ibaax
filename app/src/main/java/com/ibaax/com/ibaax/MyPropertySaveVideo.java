package com.ibaax.com.ibaax;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapter.VideoAdapter;
import Entity.AppGlobal;
import Entity.Dictunary;
import Event.IHttpResponse;
import Event.IProperty;
import Plugins.JSONParser;
import ServiceInvoke.HttpRequest;

/**
 * Created by iBaax on 5/30/16.
 */
public class MyPropertySaveVideo extends AppCompatActivity {

    EditText VideoLink;
    TextView InvalidLink;
    AppPrefs prefs;
    UI.ExpandableHeightGridView gridVideo;
    List<Dictunary> VideoList = new ArrayList<>();
    VideoAdapter adapter;
    String PropertyID, CreateDate, LastUpdateDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_video);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        VideoLink = (EditText) findViewById(R.id.txt_video_link);
        InvalidLink = (TextView) findViewById(R.id.err_valid_link);
        gridVideo = (UI.ExpandableHeightGridView) findViewById(R.id.grid_video);
        prefs = new AppPrefs(this);

        PropertyID = getIntent().getStringExtra("PID");
        //CreateDate = getIntent().getStringExtra("CreateDate");
        //LastUpdateDate = getIntent().getStringExtra("LastDate");

        HttpGetVideos();
    }

    void HttpGetVideos() {

        new HttpRequest(this, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                try {
                    JSONArray jsonArray = new JSONArray((String) response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);
                        Dictunary video = new Dictunary();
                        video.Title = JSONParser.parseString(jo, "YouTubeVideoThumbnailLink");
                        video.Name = JSONParser.parseString(jo, "Name");
                        video.ID = JSONParser.parseInt(jo, "PropertyVideoID");

                        VideoList.add(video);
                    }
                    BindVideo();

                } catch (JSONException e) {

                }
            }

            @Override
            public void RequestFailed(String response) {

            }
        }).MakeJsonArrayReq(AppGlobal.localHost + "/api/MProperty/GetPropertyVideoLink?pid=" + PropertyID);

    }

    void BindVideo() {

        adapter = new VideoAdapter(this, VideoList, new IProperty() {
            @Override
            public void btnFavorite_click(Object obj) {

            }

            @Override
            public void onClick(Object obj) {
                final int id = (int) obj;
                new HttpRequest(MyPropertySaveVideo.this, new IHttpResponse() {
                    @Override
                    public void RequestSuccess(Object response) {
                        try {
                            JSONObject jo = new JSONObject((String) response);
                            for (int i = 0; i < VideoList.size(); i++) {
                                if (VideoList.get(i).ID == id) {
                                    VideoList.remove(i);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            Toast.makeText(MyPropertySaveVideo.this, jo.getString("SuccessMessage"),
                                    Toast.LENGTH_SHORT).show();


                        } catch (JSONException e) {

                        }
                    }

                    @Override
                    public void RequestFailed(String response) {

                    }
                }).HttpPostProperty(null, AppGlobal.localHost + "/api/Mproperty/RemovePropertyVideo?PropertyVideoIds=" +
                        id + "&PropertyId=" + PropertyID);
            }
        });

        gridVideo.setAdapter(adapter);

    }

    public void btn_Upload_Video(View view) {
        if (ValidateLink()) {
            InvalidLink.setText("");

            HashMap<String, String> jsonParams = new HashMap<>();
            jsonParams.put("YouTubeVideoLink", VideoLink.getText().toString());
            jsonParams.put("PropertyID", PropertyID);
            jsonParams.put("PropertyVideoID", "0");
            jsonParams.put("CreatedByUserID", prefs.getUserID());
            jsonParams.put("CreateDate", "26-12-2016");
            jsonParams.put("LastUpdateDate", "26-12-2016");
            jsonParams.put("LastUpdatedByUserID", prefs.getUserID());


            //Regex(@"youtu(?:\.be|be\.com)/(?:.*v(?:/|=)|(?:.*/)?)([a-zA-Z0-9-_]+)", RegexOptions.IgnoreCase);
            new HttpRequest(this, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    try {
                        JSONObject jo = new JSONObject((String) response);
                        Toast.makeText(MyPropertySaveVideo.this, jo.getString("Message"), Toast.LENGTH_SHORT).show();
                        VideoLink.setText("");
                        VideoList.clear();
                        HttpGetVideos();
                    } catch (JSONException e) {

                    }
                }

                @Override
                public void RequestFailed(String response) {

                }
            }).HttpPostProperty(jsonParams, AppGlobal.localHost + "/api/MProperty/SavePropertyVideo");
        } else {
            InvalidLink.setText(R.string.content_error_valid_link);
        }

    }

    boolean ValidateLink() {

        return VideoLink.getText().toString().contains("youtube.com");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


}
