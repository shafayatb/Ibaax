package ServiceInvoke;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ibaax.com.ibaax.AppController;
import com.ibaax.com.ibaax.AppPrefs;
import com.ibaax.com.ibaax.CompleteProfileActivity;
import com.ibaax.com.ibaax.NavigationDrawerMainActivity;
import com.ibaax.com.ibaax.RegistrationActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Entity.AppGlobal;
import Plugins.TextBoxHandler;
import UI.MessageBox;

/**
 * Created by iBaax on 5/5/16.
 */
public class HttpLoginFromRegister {

    Context context;

    public HttpLoginFromRegister(Context context) {
        this.context = context;
    }

    public void LoginFromRegister(final String username, final String password) {
        String URL = AppGlobal.localHost + "/token";
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please Wait");
        pDialog.setCancelable(false);
        final Map<String, String> jsonParams = new HashMap<String, String>();

        jsonParams.put("username", username);
        jsonParams.put("password", password);
        jsonParams.put("grant_type", "password");

        pDialog.show();

        StringRequest myRequest = new StringRequest(
                Request.Method.POST,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("esty", "success: " + response);

                        try {


                            JSONObject jobj = new JSONObject(response);

                            String access_token = jobj.getString("access_token");
                            String User = jobj.getString("User");
                            String Contact = jobj.getString("Contact");
                            String CategoryUser = jobj.getString("UserCategoryUser");
                            AppPrefs prefs = new AppPrefs(context);
                            prefs.setOAuthToken(access_token);


                            JSONObject user_obj = new JSONObject(User);
                            String FirstName = user_obj.getString("FirstName");
                            String LastName = user_obj.getString("LastName");
                            String UserID = user_obj.getString("UserID");
                            String CompanyID = user_obj.getString("CreatedByCompanyID");
                            String Phone = user_obj.getString("Phone");
                            String PhoneCode = user_obj.getString("PhoneCode");
                            String CompanyName = user_obj.getString("CompanyName");

                            JSONObject contact_obj = new JSONObject(Contact);
                            int ContactID = contact_obj.getInt("ContactID");

                            JSONObject catObj = new JSONObject(CategoryUser);
                            String UserCategoryID = catObj.getString("UserCategoryID");

                            prefs.setName(FirstName + " " + LastName);
                            prefs.setCompanyName(TextBoxHandler.IsNullOrEmpty(CompanyName));
                            prefs.setFirstName(FirstName);
                            prefs.setLastName(LastName);
                            prefs.setCompanyID(CompanyID);
                            prefs.setUserPhone(PhoneCode + "" + Phone);
                            String EmailAddress = user_obj.getString("EmailAddress");
                            prefs.setUserID(UserID);
                            prefs.setEmail(EmailAddress);
                            prefs.setContactID(String.valueOf(ContactID));
                            prefs.setUserCategory(UserCategoryID);
                            if (UserCategoryID.equals("3")) {
                                Intent intent = new Intent(context, NavigationDrawerMainActivity.class);
                                NavigationDrawerMainActivity.LastSelectedTab = 0;
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(intent);
                                ((RegistrationActivity) context).finish();
                            } else {
                                Intent intent = new Intent(context, CompleteProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(intent);
                                ((RegistrationActivity) context).finish();
                            }


                        } catch (Exception e) {
                            Log.e("esty", "HttpLogin->Error:" + e.getMessage());
                        }
                        pDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("esty", "Error: " + error);
                        pDialog.hide();
                        if (error instanceof NoConnectionError) {
                            Log.e("esty", "HttpLogin->Error:" + error.getMessage());
                            MessageBox.Show(context, "Please Check Your Internet Connection");
                        }
                        if (error instanceof TimeoutError) {
                            Log.e("esty", "HttpLogin->Error:" + error.getMessage());
                            MessageBox.Show(context, "Connection Timed Out");
                        } else {

                            Log.e("esty", "HttpLogin->Error:" + error.getMessage());
                            MessageBox.Show(context, "Invalid username and password");
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return jsonParams;
            }


        };
        AppController.getInstance().addToRequestQueue(myRequest, "tag_json_obj");
    }

}
