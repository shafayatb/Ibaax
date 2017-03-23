package ServiceInvoke;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.github.mrengineer13.snackbar.SnackBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Entity.AppGlobal;
import Event.IHttpResponse;
import Event.IProperty;

/**
 * Created by iBaax on 2/6/16.
 */
public class AddToFavorite
{

    Context context;
    IProperty listener;
    Short duration=5000;
    public  AddToFavorite(Context context,IProperty listener)
    {

        this.context=context;
        this.listener=listener;

    }
    public void Post(final String PropertyID,String UserID,String CompanyID,String FirstName,String LastName){

        final Map<String, String> jsonParams = new HashMap<String, String>();
        //  String URL = "http://api.ibaax.com/api/MProperty/AddToFavouriteAjax?id=5231&UserID=1&CompanyID=1&firstName=IBaax&lastName=Administrator";
        String FName=FirstName.replace(" ","%20");
        String URL = AppGlobal.localHost+"/api/MProperty/AddToFavouriteAjax?id="+PropertyID+"&UserID="
                +UserID+"&CompanyID="+CompanyID+"&firstName="+Uri.encode(FName)+"&lastName="+ Uri.encode(LastName)+"";
       // new SnackBar.Builder((Activity)context).withMessage("Will be added to shortlist after sometime")
           //     .withDuration(duration).show();
        new HttpRequest(context, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                try {
                    Log.v("esty", (String) response);
                    JSONObject jobj = new JSONObject((String) response);
                    if(jobj.getBoolean("success")==true){
                        if(jobj.getBoolean("IsLiked")==true){
                            //Toast.makeText(context,"Added to your shortlist",Toast.LENGTH_SHORT).show();
                            //new SnackBar.Builder((Activity)context).withMessage("Property added to your shortlist")
                                 //   .withDuration(duration).show();
                        }else{
                            //Toast.makeText(context,"Removed from your shortlist",Toast.LENGTH_LONG).show();
                            /*new SnackBar.Builder((Activity)context).withMessage("Property removed from your shortlist")
                                    .withDuration(duration).show();*/
                            listener.onClick(PropertyID);
                        }
                    }else{
                        //Toast.makeText(context,"Sorry, there was a problem while adding to shortlist",Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    Log.e("esty","AddToFavorites/Post/IHttpResponse/RequestSuccess->Error: "+e.getMessage());
                }
            }
            @Override
            public void RequestFailed(String response) {
                // IsAlreadyHttpRwquestSend=false;
                /// lnrBusy.setVisibility(View.GONE);

            }
        }).HttpPostAddToFavorite(jsonParams,URL);
    }
}
