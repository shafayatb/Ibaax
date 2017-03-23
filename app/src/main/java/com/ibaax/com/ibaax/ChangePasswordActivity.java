package com.ibaax.com.ibaax;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Entity.AppGlobal;
import Event.IHttpResponse;
import Event.IValidation;
import Plugins.PasswordMatch;
import ServiceInvoke.HttpRequest;
import UI.EdittextRequired;
import UI.MessageBox;

public class ChangePasswordActivity extends AppCompatActivity {

    TextView ValidationOld, ValidationNew, ValidationConfirm;
    EditText txtOld, txtNew, txtConfirm;
    AppPrefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prefs = new AppPrefs(this);
        init();
    }

    void init() {

        ValidationOld = (TextView) findViewById(R.id.validate_oldpassword);
        ValidationNew = (TextView) findViewById(R.id.validate_newpassword);
        ValidationConfirm = (TextView) findViewById(R.id.validate_confirmpassword);

        txtOld = (EditText) findViewById(R.id.txt_edit_OldPassword);
        txtNew = (EditText) findViewById(R.id.txt_edit_newPassword);
        txtConfirm = (EditText) findViewById(R.id.txt_edit_comfirmPassword);

    }


    public void btn_ChangePassword(View v) {
        if (validatetextfields()) {
            if (matchfields()) {

                String URL = AppGlobal.localHost + "/api/user/ChangePassword";
                HashMap<String, String> jsonparams = new HashMap<>();

                jsonparams.put("UserId", prefs.getUserID());
                jsonparams.put("OldPassword", txtOld.getText().toString());
                jsonparams.put("NewPassword", txtNew.getText().toString());

                new HttpRequest(this, new IHttpResponse() {
                    @Override
                    public void RequestSuccess(Object response) {

                        try {
                            JSONObject jo = new JSONObject((String) response);
                            if (jo.getBoolean("success")) {
                                MessageBox.Show(ChangePasswordActivity.this, jo.getString("Message"));
                            } else {
                                MessageBox.Show(ChangePasswordActivity.this, "Old Password is wrong.");
                            }

                        } catch (JSONException e) {

                        }
                    }

                    @Override
                    public void RequestFailed(String response) {

                    }
                }).HttpPost(jsonparams, URL);
            }
        }


    }

    private Boolean validatetextfields() {
        Boolean isValid = true;
        List<IValidation> validation = new ArrayList<IValidation>();
        validation.add(new EdittextRequired(txtOld.getText().toString(),
                ValidationOld, "Old Password Field Required."));
        validation.add(new EdittextRequired(txtNew.getText().toString(),
                ValidationNew, "New Password Field Required."));
        validation.add(new EdittextRequired(txtConfirm.getText().toString(),
                ValidationConfirm, "Confirm Password Field Required."));

        for (IValidation v : validation) {
            if (v.Invoke() == false) {
                isValid = false;
            }
        }

        return isValid;
    }

    private Boolean matchfields() {
        Boolean isValid = true;
        List<IValidation> validation = new ArrayList<IValidation>();
        validation.add(new PasswordMatch(txtNew.getText().toString(),txtConfirm.getText().toString()
                ,ValidationConfirm));

        for (IValidation v : validation) {
            if (v.Invoke() == false) {
                isValid = false;
            }
        }

        return isValid;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
