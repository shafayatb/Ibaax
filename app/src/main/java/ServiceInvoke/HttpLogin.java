package ServiceInvoke;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ibaax.com.ibaax.AppController;
import com.ibaax.com.ibaax.AppPrefs;
import com.ibaax.com.ibaax.LoginActivity;
import com.ibaax.com.ibaax.NavigationDrawerMainActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Entity.AppGlobal;
import UI.MessageBox;

/**
 * Created by iBaax on 2/1/16.
 */
public class HttpLogin {

    Context context;

    public HttpLogin(Context context) {

        this.context = context;
    }

    public void MakeLogin(final String username, final String password) {


        String URL = AppGlobal.localHost + "/token";
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please Wait");
        pDialog.setCancelable(false);
        final Map<String, String> jsonParams = new HashMap<String, String>();
        // jsonParams.put("param1", youParameter);

        jsonParams.put("username", username);
        jsonParams.put("password", password);
        jsonParams.put("grant_type", "password");
        //jsonParams.put("rememberMe", "true");

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
                            prefs.setCompanyName(CompanyName);
                            prefs.setFirstName(FirstName);
                            prefs.setLastName(LastName);
                            prefs.setCompanyID(CompanyID);
                            prefs.setUserPhoneCode(PhoneCode);
                            prefs.setUserPhone(Phone);
                            String EmailAddress = user_obj.getString("EmailAddress");
                            Log.v("esty", FirstName);
                            prefs.setUserID(UserID);
                            prefs.setEmail(EmailAddress);
                            prefs.setContactID(String.valueOf(ContactID));
                            prefs.setUserCategory(UserCategoryID);
                            NavigationDrawerMainActivity.LastSelectedTab = 0;
                            Intent intent = new Intent(context, NavigationDrawerMainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                            ((LoginActivity) context).finish();


                        } catch (Exception e) {
                            Log.e("esty", "HttpLogin->Error:" + e.getMessage());
                            // MessageBox.Show(context, e.getMessage());
                        }
                        pDialog.hide();
                        // verificationSuccess(response);
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
                        //verificationFailed(error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return jsonParams;
            }


        };
        myRequest.setRetryPolicy(new DefaultRetryPolicy(30000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(myRequest, "tag_json_obj");
    }


}
