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

public class SaveVideoActivity extends AppCompatActivity {

    EditText VideoLink;
    TextView InvalidLink;
    AppPrefs prefs;
    UI.ExpandableHeightGridView gridVideo;
    List<Dictunary> VideoList = new ArrayList<>();
    VideoAdapter adapter;
    String VideoURL, SaveURL, RemoveURL;
    String ParamID, ParamValue, IDKey;

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
        if (prefs.getUserCategory().equals("2") || prefs.getUserCategory().equals("4")) {
            VideoURL = AppGlobal.localHost + "/api/MContacts/GetCompanyVideo?companyId="
                    + prefs.getCompanyID() + "&lang=en";
            SaveURL = AppGlobal.localHost + "/api/MCompany/SaveCompanyVideo";
            RemoveURL = AppGlobal.localHost + "/api/MCompany/RemoveCompanyVideo/";
            ParamID = "CompanyID";
            ParamValue = prefs.getCompanyID();
            IDKey = "CompanyVideoID";
        } else {
            VideoURL = AppGlobal.localHost + "/api/MContacts/GetContactVideo?contactId="
                    + prefs.getContactID() + "&lang=en";
            SaveURL = AppGlobal.localHost + "/api/MContacts/SaveContactVideo";
            RemoveURL = AppGlobal.localHost + "/api/MContacts/RemoveContactVideo/";
            ParamID = "ContactID";
            ParamValue = prefs.getContactID();
            IDKey = "ContactVideoID";
        }
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
                        video.ID = JSONParser.parseInt(jo, IDKey);

                        VideoList.add(video);
                    }
                    BindVideo();

                } catch (JSONException e) {

                }
            }

            @Override
            public void RequestFailed(String response) {

            }
        }).MakeJsonArrayReq(VideoURL);

    }

    void BindVideo() {

        adapter = new VideoAdapter(this, VideoList, new IProperty() {
            @Override
            public void btnFavorite_click(Object obj) {

            }

            @Override
            public void onClick(Object obj) {
                final int id = (int) obj;
                new HttpRequest(SaveVideoActivity.this, new IHttpResponse() {
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
                            Toast.makeText(SaveVideoActivity.this, jo.getString("SuccessMessage"),
                                    Toast.LENGTH_SHORT).show();


                        } catch (JSONException e) {

                        }
                    }

                    @Override
                    public void RequestFailed(String response) {

                    }
                }).HttpPostProperty(null, RemoveURL + id);
            }
        });

        gridVideo.setAdapter(adapter);

    }

    public void btn_Upload_Video(View view) {
        if (ValidateLink()) {
            InvalidLink.setText("");

            HashMap<String, String> jsonParams = new HashMap<>();
            jsonParams.put("YouTubeVideoLink", VideoLink.getText().toString());
            jsonParams.put(ParamID, ParamValue);

            //Regex(@"youtu(?:\.be|be\.com)/(?:.*v(?:/|=)|(?:.*/)?)([a-zA-Z0-9-_]+)", RegexOptions.IgnoreCase);
            new HttpRequest(this, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    try {
                        JSONObject jo = new JSONObject((String) response);
                        Toast.makeText(SaveVideoActivity.this, jo.getString("SuccessMessage"), Toast.LENGTH_SHORT).show();
                        VideoLink.setText("");
                        VideoList.clear();
                        HttpGetVideos();
                    } catch (JSONException e) {

                    }
                }

                @Override
                public void RequestFailed(String response) {

                }
            }).HttpPostProperty(jsonParams, SaveURL);
        } else {
            InvalidLink.setText(R.string.content_error_valid_link);
        }

    }

    boolean ValidateLink() {

        /*Pattern pattern = Pattern.compile("youtu(?:\\.be|be\\.com)");
        Matcher m = pattern.matcher(VideoLink.getText().toString());
        return m.matches();*/
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
