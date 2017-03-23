package ServiceInvoke;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.ibaax.com.ibaax.AppController;
import com.ibaax.com.ibaax.AppPrefs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Event.IHttpResponse;
import UI.MessageBox;

/**
 * Created by S.R Rain on 1/13/2016.
 */
public class HttpRequest {
    Context context;
    IHttpResponse listener;
    AppPrefs prefs;

    public HttpRequest(Context context, IHttpResponse listener) {

        this.context = context;
        this.listener = listener;
        prefs = new AppPrefs(context);
    }


    public void MakeJsonArrayReq(String url) {


        JsonArrayRequest jreq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {


                        listener.RequestSuccess(response.toString());


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                listener.RequestFailed(error.getMessage());
            }
        });
        jreq.setRetryPolicy(new DefaultRetryPolicy(30000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(jreq, "tag_json_obj");
    }


    public void HttpPost(final Map<String, String> jsonParams, String url) {

        String URL = url;
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please Wait");

        pDialog.show();
        StringRequest myRequest = new StringRequest(
                Request.Method.POST,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Log.v("esty", "success: " + response.toString());
                        listener.RequestSuccess(response.toString());
                        pDialog.dismiss();
                        // verificationSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        AppController.getInstance().getRequestQueue().cancelAll("tag_json_obj");
                        if (error instanceof NoConnectionError) {
                            Log.e("esty", "HttpPost->Error:" + error.getMessage());
                            MessageBox.Show(context, "Please Check Your Internet Connection");
                            pDialog.dismiss();
                        }
                        if (error instanceof TimeoutError) {
                            Log.e("esty", "HttpPost->Error:" + error.getMessage());
                            MessageBox.Show(context, "Please Check Your Internet Connection");
                            pDialog.dismiss();
                        }
                        if (error instanceof ServerError) {
                            try {
                                Log.e("esty", "HttpPost->Error:" + String.valueOf(error.networkResponse.statusCode));
                                pDialog.dismiss();
                                listener.RequestFailed(String.valueOf(error.networkResponse.statusCode));
                            } catch (Exception e) {
                                Log.e("error", "HttpPost->ServerError:" + e.getMessage());
                            }
                        } else {
                            Log.e("esty", "HttpPost->Error:" + error.getMessage());
                            listener.RequestFailed(error.toString());

                            pDialog.dismiss();
                        }
                        //verificationFailed(error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return jsonParams;
            }


        };
        myRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(myRequest, "tag_json_obj");
    }


    public void JSONObjectRequest(JSONObject js, String url) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, url, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("esty", "JSONObjectRequest/onResponse-> " + response.toString());
                        listener.RequestSuccess(response);
                        // msgResponse.setText(response.toString());
                        //  hideProgressDialog();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("esty", "JSONObjectRequest/OnErrorResponse-> " + error.toString());

                listener.RequestFailed(error.toString());


            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
    }


    public void JSONObjectGetRequest(JSONObject js, String url) {

        final Map<String, String> mHeaders = new ArrayMap<String, String>();
        mHeaders.put("Authorization", "Bearer " + prefs.gettOAuthToken());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET, url, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("esty", response.toString());
                        listener.RequestSuccess(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    // MessageBox.Show(context, "Please Check Your Internet Connection");
                }
                if (error instanceof TimeoutError) {
                    //MessageBox.Show(context, "Please Check Your Internet Connection");
                    Log.d("esty", error.toString());
                } else {

                    //MessageBox.Show(context, "Bad Request");
                    listener.RequestFailed(error.toString());
                    Log.d("esty", error.toString());
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return mHeaders;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
    }

    public void HttpPostProperty(final Map<String, String> jsonParams, String url) {


        String URL = url;
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please Wait");
        final Map<String, String> mHeaders = new ArrayMap<String, String>();
        mHeaders.put("Authorization", "Bearer " + prefs.gettOAuthToken());
        pDialog.show();
        StringRequest myRequest = new StringRequest(
                Request.Method.POST,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Log.v("esty", "success: " + response.toString());
                        listener.RequestSuccess(response.toString());
                        pDialog.dismiss();
                        // verificationSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        AppController.getInstance().cancelPendingRequests("tag_json_obj");
                        if (error instanceof NoConnectionError) {
                            Log.e("esty", "HttpPost->Error:" + error.getMessage());
                            MessageBox.Show(context, "Please Check Your Internet Connection");
                            pDialog.dismiss();
                        }
                        if (error instanceof TimeoutError) {
                            Log.e("esty", "HttpPost->Error:" + error.getMessage());
                            MessageBox.Show(context, "Please Check Your Internet Connection");
                            pDialog.dismiss();
                        } else {
                            Log.e("esty", "HttpPost->Error:" + error.getMessage());
                            listener.RequestFailed(error.toString());

                            pDialog.dismiss();
                        }
                        //verificationFailed(error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return jsonParams;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return mHeaders;
            }
        };
        myRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(myRequest, "tag_json_obj");
    }

    public void HttpSavePropertySpecification(final String body, String url) {

        String URL = url;
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please Wait");
        final Map<String, String> mHeaders = new ArrayMap<String, String>();

        //mHeaders.put("Content-Type", "multipart/form-data; boundary=*****; charset=utf-8");
        mHeaders.put("Authorization", "Bearer " + prefs.gettOAuthToken());
        pDialog.show();
        //region StringRequest
        StringRequest myRequest = new StringRequest(
                Request.Method.POST,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Log.v("esty", "success: " + response.toString());
                        listener.RequestSuccess(response.toString());
                        pDialog.dismiss();
                        // verificationSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (error instanceof NoConnectionError) {
                            Log.e("esty", "HttpPost->Error:" + error.getMessage());
                            MessageBox.Show(context, "Please Check Your Internet Connection");
                            pDialog.dismiss();
                        }
                        if (error instanceof TimeoutError) {
                            Log.e("esty", "HttpPost->Error:" + error.getMessage());
                            MessageBox.Show(context, "Please Check Your Internet Connection");
                            pDialog.dismiss();
                        } else {
                            Log.e("esty", "HttpPost->Error:" + error.getMessage());
                            listener.RequestFailed(error.toString());

                            pDialog.dismiss();
                        }
                        //verificationFailed(error);
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // mHeaders.put("Content-Type", "application/x-www-form-urlencoded");
                return mHeaders;
            }

            @Override
            public String getBodyContentType() {
                // TODO Auto-generated method stub
                return "application/x-www-form-urlencoded";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                // TODO Auto-generated method stub
                return body.getBytes();
            }

            @Override
            protected String getParamsEncoding() {
                return "utf-8";
            }
        };
        //endregion


        AppController.getInstance().addToRequestQueue(myRequest, "tag_json_obj");
    }


    public void HttpPostAddToFavorite(final Map<String, String> jsonParams, String url) {

        String URL = url;
        final ProgressDialog pDialog = new ProgressDialog(context);

        final Map<String, String> mHeaders = new ArrayMap<String, String>();


        mHeaders.put("Authorization", "Bearer " + prefs.gettOAuthToken());

        StringRequest myRequest = new StringRequest(
                Request.Method.POST,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("esty", "success: " + response.toString());
                        listener.RequestSuccess(response.toString());

                        // verificationSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (error instanceof NoConnectionError) {
                            Log.e("esty", "HttpPost->Error:" + error.getMessage());
                            //MessageBox.Show(context, "Please Check Your Internet Connection");
                            listener.RequestFailed(error.toString());
                        }
                        if (error instanceof TimeoutError) {
                            Log.e("esty", "HttpPost->Error:" + error.getMessage());
                            //MessageBox.Show(context, "Please Check Your Internet Connection");
                            listener.RequestFailed(error.toString());

                        } else {
                            Log.e("esty", "HttpPost->Error:" + error.getMessage());
                            listener.RequestFailed(error.toString());
                        }
                        //verificationFailed(error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return jsonParams;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return mHeaders;
            }

        };
        AppController.getInstance().addToRequestQueue(myRequest, "tag_json_obj");
    }

}
