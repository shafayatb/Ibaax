package com.ibaax.com.ibaax;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import Entity.AppGlobal;
import Entity.PropertySerialize;
import Event.IHttpResponse;
import ServiceInvoke.HttpRequest;

public class PostAPropertyActivity extends AppCompatActivity {
    PropertySerialize property;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_aproperty);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        property = (PropertySerialize) getIntent().getSerializableExtra("Property");
        NavigationDrawerMainActivity.LastSelectedTab = 8;
    }


    public void btn_Post_Property_Photo(View view) {
        Intent intent = new Intent(this, AddPhotoActivity.class);
        intent.putExtra("ID", String.valueOf(property.PropertyID));
        startActivity(intent);
        finish();

    }

    public void btn_Post_Property_Specification(View view) {
        Intent intent = new Intent(this, PropertySetSpecificationActivity.class);
        intent.putExtra("ID", String.valueOf(property.PropertyID));
        startActivity(intent);
        finish();

    }

    public void btn_Post_Property_Publish(View view) {


        String URL = AppGlobal.localHost + "/api/Mproperty/PropertyPublishStatusSave/";

        HashMap<String, String> jsonParams = new HashMap<>();
        jsonParams.put("propertyId", String.valueOf(property.PropertyID));
        jsonParams.put("isPublish", "true");

        new HttpRequest(this, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                try {
                    JSONObject jo = new JSONObject((String) response);
                    if (jo.getBoolean("success")) {
                        //PublishStatus.setText(jo.getString("SuccessMessage"));
                        Toast.makeText(PostAPropertyActivity.this, jo.getString("SuccessMessage"), Toast.LENGTH_SHORT).show();
                        NavigationDrawerMainActivity.LastSelectedTab = 8;
                        finish();
                    } else {
                        //PublishStatus.setText(jo.getString("SuccessMessage"));
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void RequestFailed(String response) {

            }
        }).HttpPostProperty(jsonParams, URL);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                NavigationDrawerMainActivity.LastSelectedTab = 8;
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
