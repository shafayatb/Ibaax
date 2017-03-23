package com.ibaax.com.ibaax;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Entity.AppGlobal;
import Event.IHttpResponse;
import Event.IValidation;
import ServiceInvoke.HttpRequest;
import UI.EdittextRequired;
import UI.MessageBox;

public class ResetPasswordActivity extends AppCompatActivity {

    com.iangclifton.android.floatlabel.FloatLabel ResetPassword;
    ImageView imgCross, imgLogo;
    TextView ValidationEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        getSupportActionBar().hide();

        ResetPassword = (com.iangclifton.android.floatlabel.FloatLabel) findViewById(R.id.txt_reset_email);
        imgCross = (ImageView) findViewById(R.id.img_reset_cross);
        imgLogo = (ImageView) findViewById(R.id.img_reset_logo);
        ValidationEmail = (TextView) findViewById(R.id.validate_reset_email);

        imgCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResetPasswordActivity.this, NavigationDrawerMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void btnReset_click(View view) {
        try {
            if (validatetextfields()) {

                String Url = AppGlobal.localHost + "/api/user/ForgetPassword";
                final Map<String, String> jsonParams = new HashMap<String, String>();
                jsonParams.put("EmailAddress", ResetPassword.getEditText().getText().toString());

                new HttpRequest(this, new IHttpResponse() {
                    @Override
                    public void RequestSuccess(Object response) {
                        try {

                            JSONObject jo = new JSONObject((String) response);
                            if (jo.getBoolean("success")) {
                                Toast.makeText(ResetPasswordActivity.this, jo.getString("data"), Toast.LENGTH_LONG).show();
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                                        INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                /*Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();*/
                            } else {
                                Toast.makeText(ResetPasswordActivity.this, jo.getString("data"), Toast.LENGTH_LONG).show();
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                                        INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                            }

                        } catch (Exception e) {
                            Log.e("esty", "ResetPasswordActivity/btnReset_click/HttpRequest/RequestSuccess " + e.getMessage());
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                                    INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }
                    }

                    @Override
                    public void RequestFailed(String response) {
                        MessageBox.Show(ResetPasswordActivity.this, "Sorry, there seems to be a problem right now. Please try again later.");
                    }
                }).HttpPost(jsonParams, Url);
            }


        } catch (Exception e) {


        }


    }

    private Boolean validatetextfields() {
        Boolean isValid = true;
        List<IValidation> validation = new ArrayList<IValidation>();
        validation.add(new EdittextRequired(ResetPassword.getEditText().getText().toString(),
                ValidationEmail, "Please Enter A Valid Email Address."));


        for (IValidation v : validation) {
            if (v.Invoke() == false) {
                isValid = false;
            }
        }

        return isValid;
    }

    public void btnBackToLogin_click(View view) {
        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
