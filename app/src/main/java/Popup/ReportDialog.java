package Popup;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import com.ibaax.com.ibaax.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapter.ReportListAdapter;
import DataAccessLayer.ReportTypeLayer;
import Entity.AppGlobal;
import Entity.Dictunary;
import Event.IEvent;
import Event.IHttpResponse;
import Plugins.TextBoxHandler;
import ServiceInvoke.HttpRequest;
import UI.MessageBox;

/**
 * Created by iBaax on 3/20/16.
 */
public class ReportDialog {

    Context context;
    IEvent listener;
    int PropertyID;
    static List<Dictunary>ReportList=new ArrayList<>();
    String ReportID;
    ReportListAdapter adapter;
    public ReportDialog(Context context,int PropertyID,IEvent listener){

        this.context=context;
        this.PropertyID=PropertyID;
        this.listener=listener;
    }

    public void PopupReport(){
        try {
            final Dialog dialog = new Dialog(context, R.style.MyCustomDialog);

            WindowManager.LayoutParams lp = new android.view.WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_report);
            dialog.show();
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().setDimAmount(0.5f);
            dialog.getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            ListView list=(ListView)dialog.findViewById(R.id.list_report);
            Button btnSubmit=(Button)dialog.findViewById(R.id.btn_submit_report);

            if(ReportList.size()>0){
                adapter=new ReportListAdapter(context, ReportList, new IEvent() {
                    @Override
                    public void onClick(Object obj) {
                        Dictunary dictunary=(Dictunary)obj;
                        for(int i=0;i<ReportList.size();i++){
                            ReportList.get(i).IsSelected=false;
                        }
                        dictunary.IsSelected=true;
                        ReportID=String.valueOf(dictunary.ID);

                    }
                });
                list.setAdapter(adapter);
            }else{
                ReportList=new ReportTypeLayer(context).getReportType();
                adapter=new ReportListAdapter(context, ReportList, new IEvent() {
                    @Override
                    public void onClick(Object obj) {
                        Dictunary dictunary=(Dictunary)obj;
                        for(int i=0;i<ReportList.size();i++){
                            ReportList.get(i).IsSelected=false;
                        }
                        dictunary.IsSelected=true;
                        ReportID=String.valueOf(dictunary.ID);
                    }
                });
                list.setAdapter(adapter);
            }
            for(int i=0;i<ReportList.size();i++){
                ReportList.get(i).IsSelected=false;
            }
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextBoxHandler.IsNullOrEmpty(ReportID).trim().length() > 0) {
                        String URL = AppGlobal.localHost + "/api/MProperty/ReportSave";
                        HashMap<String, String> jsonparams = new HashMap<String, String>();
                        jsonparams.put("PropertyID", String.valueOf(PropertyID));
                        jsonparams.put("PropertyReportTypeID", ReportID);
                        jsonparams.put("PropertyReportID", "0");
                        new HttpRequest(context, new IHttpResponse() {
                            @Override
                            public void RequestSuccess(Object response) {
                                try {
                                    JSONObject jo = new JSONObject((String) response);
                                    if (jo.getBoolean("success")) {
                                        MessageBox.Show(context, "Thank you for helping us.\n" +
                                                "\n" +
                                                "We will be taking appropriate measures.");
                                    }
                                    dialog.dismiss();
                                } catch (JSONException e) {

                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void RequestFailed(String response) {
                                MessageBox.Show(context, response);
                            }
                        }).HttpPost(jsonparams, URL);
                    }
                    else{
                        MessageBox.Show(context, "Please select an item you want to report.");
                    }
                }
            });

        }catch (Exception e){

        }

    }


}
