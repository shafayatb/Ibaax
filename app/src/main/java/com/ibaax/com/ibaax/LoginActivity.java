package com.ibaax.com.ibaax;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import Entity.AppGlobal;
import Entity.User;
import Event.IHttpResponse;
import ServiceInvoke.HttpLogin;
import ServiceInvoke.HttpRequest;
import UI.MessageBox;

public class LoginActivity extends AppCompatActivity {

    EditText txtUserName, txtPassword;
    TextView lblUser, lblPassword;
    LinearLayout btnFacebookLogin;
    CallbackManager callbackManager;
    ImageView imgCross, imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        txtUserName = (EditText) findViewById(R.id.txtLoginUserName);
        txtPassword = (EditText) findViewById(R.id.txtLoginPassword);
        lblUser = (TextView) findViewById(R.id.lblLoginUsereName);
        lblPassword = (TextView) findViewById(R.id.lblLoginPassword);
        btnFacebookLogin = (LinearLayout) findViewById(R.id.btn_facebook_login);
        imgCross = (ImageView) findViewById(R.id.img_cross);
        imgLogo = (ImageView) findViewById(R.id.img_sign_in_logo);

        btnFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                        Arrays.asList("public_profile", "email"));
            }
        });

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object,
                                                    GraphResponse response) {
                                // TODO Auto-generated method stub
                                Log.v("esty",
                                        "Response:"
                                                + response.toString());
                                try {
                                    object = response.getJSONObject();
                                    User user = new User();
                                    String ID = object.getString("id");
                                    String Fname = object.getString("name").
                                            substring(0, object.getString("name").indexOf(" "));
                                    String Lname = object
                                            .getString("name").substring(object.getString("name")
                                                    .lastIndexOf(" ") + 1, object.getString("name")
                                                    .length());
                                    String email;

                                    email = object.getString("email");

                                    user.FirstName = Fname;
                                    user.LastName = Lname;
                                    user.EmailAddress = email;

                                    ExternalLogin(user);


                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    MessageBox
                                            .Show(LoginActivity.this,
                                                    "Please provide a valid email address in your facebook account");
                                    Log.v("esty", "Error: " + e.getMessage());
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields",
                        "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();

            }


            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.v("esty", "Error: " + error.getMessage());
            }
        });

        imgCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(LoginActivity.this, NavigationDrawerMainActivity.class);
                NavigationDrawerMainActivity.LastSelectedTab=0;
                startActivity(intent);*/
                finish();
            }
        });

        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, NavigationDrawerMainActivity.class);
                NavigationDrawerMainActivity.LastSelectedTab = 0;
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

    }


    void ExternalLogin(final User user) {

        Map<String, String> params = new HashMap<String, String>();

        params.put("EmailAddress", user.EmailAddress);
        params.put("UserID", "0");

        new HttpRequest(LoginActivity.this, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                try {
                    JSONObject jobj = new JSONObject((String) response);
                    String Message = jobj.getString("Message");
                    if (Message.equals("User Info")) {

                        JSONObject resobj = new JSONObject(jobj.getString("response"));
                        String User = resobj.getString("User");
                        String UserDefaultSetting = resobj.getString("UserDefaultSetting");

                        AppPrefs prefs = new AppPrefs(LoginActivity.this);

                        JSONObject user_obj = new JSONObject(User);
                        String FirstName = user_obj.getString("FirstName");
                        String LastName = user_obj.getString("LastName");
                        String CompanyID = user_obj.getString("CreatedByCompanyID");
                        prefs.setFirstName(FirstName);
                        prefs.setLastName(LastName);
                        prefs.setCompanyID(CompanyID);
                        prefs.setName(FirstName + " " + LastName);

                        String EmailAddress = user_obj.getString("EmailAddress");
                        Log.v("esty", FirstName);

                        JSONArray userarr = new JSONArray(UserDefaultSetting);

                        for (int i = 0; i < userarr.length(); i++) {

                            JSONObject defaultObj = userarr.getJSONObject(i);
                            prefs.setUserID(defaultObj.getString("UserID"));
                            prefs.setOAuthToken(defaultObj.getString("UserID"));
                        }

                        prefs.setEmail(EmailAddress);

                        Intent intent = new Intent(LoginActivity.this, NavigationDrawerMainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        ExternalRegister(user);
                    }

                } catch (JSONException e) {
                    Log.v("esty", "Error: " + e.getMessage());
                }

            }

            @Override
            public void RequestFailed(String response) {
                Log.v("esty", "Error: " + response);
            }
        }).HttpPost(params, AppGlobal.localHost + "/api/user/ExternalLoginCallback");

    }

    void ExternalRegister(final User user) {

        Map<String, String> params = new HashMap<>();

        params.put("FirstName", user.FirstName);
        params.put("LastName", user.LastName);
        params.put("EmailAddress", user.EmailAddress);
        params.put("Password", "");
        params.put("Phone", "");
        params.put("CountryID", AppGlobal.CurrentCountryID);
        params.put("UserID", "0");

        new HttpRequest(LoginActivity.this, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                try {
                    JSONObject jobj = new JSONObject((String) response);
                    String Message = jobj.getString("Message");
                    if (Message.equals("User registration completed.")) {
                        ExternalLogin(user);
                    } else {
                        MessageBox.Show(LoginActivity.this, "Cannot Login With Facebook.");
                    }

                } catch (JSONException e) {
                    Log.v("esty", "Error: " + e.getMessage());
                }

            }

            @Override
            public void RequestFailed(String response) {
                Log.v("esty", "Error: " + response);
            }
        }).HttpPost(params, AppGlobal.localHost + "/api/user/ExternalLoginCallback");

    }

    public void btnLogin_click(View view) {


        if (Validation()) {
            new HttpLogin(this).MakeLogin(txtUserName.getText().toString(), txtPassword.getText().toString());
        }


    }


    private Boolean Validation() {
        boolean IsValid = true;
        if (txtUserName.getText().length() == 0) {
            lblUser.setVisibility(View.VISIBLE);
            IsValid = false;
        } else {

            lblUser.setVisibility(View.GONE);

        }

        if (txtPassword.getText().length() == 0) {
            lblPassword.setVisibility(View.VISIBLE);
            IsValid = false;
        } else {
            lblPassword.setVisibility(View.GONE);
        }
        return IsValid;

    }

    public void btnSignUp_click(View view) {
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void btnResetPass_click(View view) {

        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*Intent intent = new Intent(LoginActivity.this, NavigationDrawerMainActivity.class);
        NavigationDrawerMainActivity.LastSelectedTab=0;
        startActivity(intent);*/
        finish();
    }
}
