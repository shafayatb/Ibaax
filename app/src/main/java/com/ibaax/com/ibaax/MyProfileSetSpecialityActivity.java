package com.ibaax.com.ibaax;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapter.PropertyTypeGridAdapter2;
import Entity.AppGlobal;
import Entity.Dictunary;
import Event.IHttpResponse;
import Event.IPropertyType;
import Plugins.JSONParser;
import Plugins.TextBoxHandler;
import ServiceInvoke.HttpRequest;

public class MyProfileSetSpecialityActivity extends AppCompatActivity {

    List<Dictunary> UserSpecialityList = new ArrayList<>();
    UI.ExpandableHeightGridView gridUserSpeciality;
    ProgressDialog progressDialog;
    AppPrefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile_set_speciality);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gridUserSpeciality = (UI.ExpandableHeightGridView) findViewById(R.id.grid_user_speciality);

        progressDialog = new ProgressDialog(this);
        prefs = new AppPrefs(this);

        //String URL = AppGlobal.localHost + "/api/MContacts/GetAllSpeciality?lang=en";
        if (prefs.getUserCategory().equals("2") || prefs.getUserCategory().equals("4")) {
            String URL = AppGlobal.localHost + "/api/MContacts/GetCompanySpecialities?companyID=" + prefs.getCompanyID() + "&lang='en'";

            new HttpRequest(this, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    try {
                        JSONArray jsonArray = new JSONArray((String) response);
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject specialitiesObject = jsonArray.getJSONObject(i);
                            Dictunary d = new Dictunary();
                            d.ID = JSONParser.parseInt(specialitiesObject, "CompanySkillLibraryID");
                            d.Title = JSONParser.parseString(specialitiesObject, "Name");
                            d.Name = JSONParser.parseString(specialitiesObject, "CompanySkillID");
                            if (JSONParser.parseString(specialitiesObject, "IsSelected").equals("True")) {
                                d.IsSelected = true;
                            } else {
                                d.IsSelected = false;
                            }
                            UserSpecialityList.add(d);
                        }
                        Bind(UserSpecialityList);

                    } catch (JSONException e) {

                    }

                }

                @Override
                public void RequestFailed(String response) {

                }
            }).MakeJsonArrayReq(URL);
        } else {
            String URL = AppGlobal.localHost + "/api/Mcontacts/GetContactSpecialities?contactID=" + prefs.getContactID() + "&lang='en'";

            new HttpRequest(this, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    try {
                        JSONArray jsonArray = new JSONArray((String) response);
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject specialitiesObject = jsonArray.getJSONObject(i);
                            Dictunary d = new Dictunary();
                            d.ID = JSONParser.parseInt(specialitiesObject, "CompetencyLibraryID");
                            d.Title = JSONParser.parseString(specialitiesObject, "Name");
                            d.Name = JSONParser.parseString(specialitiesObject, "ContactCompetencyID");
                            if (JSONParser.parseString(specialitiesObject, "IsSelected").equals("True")) {
                                d.IsSelected = true;
                            } else {
                                d.IsSelected = false;
                            }
                            UserSpecialityList.add(d);
                        }
                        Bind(UserSpecialityList);

                    } catch (JSONException e) {

                    }

                }

                @Override
                public void RequestFailed(String response) {

                }
            }).MakeJsonArrayReq(URL);

        }


    }

    public void btn_Save_User_Speciality(View view) {
        StringBuilder AddID = new StringBuilder();

        for (int i = 0; i < UserSpecialityList.size(); i++) {
            if (UserSpecialityList.get(i).IsSelected) {
                AddID.append(UserSpecialityList.get(i).ID).append(",");
            }
        }
        if (AddID.toString().length() > 0) {
            AddID.delete(AddID.length() - 1, AddID.length());
        }
        String SpecialitiesID = TextBoxHandler.IsNullOrEmpty(AddID.toString());
        AddID.delete(0, AddID.length());

        String URL;

        HashMap<String, String> jsonParams = new HashMap<>();

        if (prefs.getUserCategory().equals("2") || prefs.getUserCategory().equals("4")) {
            jsonParams.put("CompanyId", prefs.getCompanyID());
            jsonParams.put("CompanySkillLibraryIDs", TextBoxHandler.IsNullOrEmpty(SpecialitiesID));

            URL = AppGlobal.localHost + "/api/MCompany/AddOrUpdateCompanySpeciality";
            new HttpRequest(this, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    try {
                        JSONArray jsonArray = new JSONArray((String) response);

                        if (jsonArray.length() > 0) {
                            progressDialog.hide();
                            Toast.makeText(MyProfileSetSpecialityActivity.this, "Specialities Added", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            progressDialog.hide();
                            Toast.makeText(MyProfileSetSpecialityActivity.this, "Failed to add specialities", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException j) {

                    }

                }

                @Override
                public void RequestFailed(String response) {
                    progressDialog.hide();
                    Toast.makeText(MyProfileSetSpecialityActivity.this, "Failed to add specialities", Toast.LENGTH_SHORT).show();
                }
            }).HttpPostProperty(jsonParams, URL);
        } else {

            jsonParams.put("ContactID", prefs.getContactID());
            jsonParams.put("CompetencyLibraryIDs", TextBoxHandler.IsNullOrEmpty(SpecialitiesID));
            jsonParams.put("ContactCompetencyID", "0");
            jsonParams.put("CreatedByCompanyID", prefs.getCompanyID());

            URL = AppGlobal.localHost + "/api/MContacts/AddOrUpdateContactSpeciality";

            new HttpRequest(this, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    try {
                        JSONObject jo = new JSONObject((String) response);

                        if (jo.getString("SuccessMessage") != null) {
                            progressDialog.hide();
                            Toast.makeText(MyProfileSetSpecialityActivity.this, "Specialities Added", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            progressDialog.hide();
                            Toast.makeText(MyProfileSetSpecialityActivity.this, "Failed to add specialities", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException j) {

                    }

                }

                @Override
                public void RequestFailed(String response) {
                    progressDialog.hide();
                    Toast.makeText(MyProfileSetSpecialityActivity.this, "Failed to add specialities", Toast.LENGTH_SHORT).show();
                }
            }).HttpPostProperty(jsonParams, URL);
        }




    }


    void Bind(List<Dictunary> list) {

        PropertyTypeGridAdapter2 adapter = new PropertyTypeGridAdapter2(this, list, new IPropertyType() {
            @Override
            public void OnClick(Object obj) {

            }
        });

        gridUserSpeciality.setAdapter(adapter);
        gridUserSpeciality.setExpanded(true);

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
