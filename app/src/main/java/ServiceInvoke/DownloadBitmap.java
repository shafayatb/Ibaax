package ServiceInvoke;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.ibaax.com.ibaax.AppController;

import Event.IHttpResponse;

/**
 * Created by S.R Rain on 1/19/2016.
 */
public class DownloadBitmap
{    Context context;
    IHttpResponse listener;
    public DownloadBitmap(Context context,   IHttpResponse listener)
    {

        this.context=context;
        this.listener=listener;
    }
    public  void Download(String url)
    {

       // String imageUrl="http://www.ibaax.com/property/ShowListImage/5230?PhotoFileName=IS5ems91lqf9qv1000000000.jpg";
        ImageRequest ir = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {

                /// iv.setImageBitmap(response);
                if (response == null) {


                    listener.RequestFailed("No image found ");

                    Log.v("esty", "bitmap is null");

                } else {

                    listener.RequestSuccess(response);
                    Log.v("esty", "bitmap is  not null");
                }

            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("esty", "error: "+error.getMessage());
            }
        });


        ir.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(ir, "");
    }


}
