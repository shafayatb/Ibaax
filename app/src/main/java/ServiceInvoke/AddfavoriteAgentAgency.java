package ServiceInvoke;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.github.mrengineer13.snackbar.SnackBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Event.IHttpResponse;
import Event.IProperty;

/**
 * Created by iBaax on 4/18/16.
 */
public class AddfavoriteAgentAgency {

    Context context;
    IProperty listener;
    Short duration = 3000;

    public AddfavoriteAgentAgency(Context context, IProperty listener) {
        this.context = context;
        this.listener = listener;
    }

    public void Post(final String ID, String userType, String URL) {

        final Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("id", ID);
        jsonParams.put("userCategory", userType);
        new HttpRequest(context, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                try {
                    Log.v("esty", (String) response);
                    JSONObject jobj = new JSONObject((String) response);
                    if (jobj.getBoolean("success")) {
                        if (jobj.getBoolean("IsLiked")) {

                        } else {
                            listener.onClick(ID);
                        }
                    } else {
                        new SnackBar.Builder((Activity) context).withMessage("Action can't be performed at this moment !!")
                                .withDuration(duration).show();
                        listener.onClick(jobj.getString("success"));
                    }
                } catch (JSONException e) {
                    Log.e("esty", "AddToFavorites/Post/IHttpResponse/RequestSuccess->Error: " + e.getMessage());
                }
            }

            @Override
            public void RequestFailed(String response) {


            }
        }).HttpPostAddToFavorite(jsonParams, URL);
    }
}
