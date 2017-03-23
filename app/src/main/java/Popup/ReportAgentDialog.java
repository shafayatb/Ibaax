package Popup;

import android.app.Dialog;
import android.content.Context;
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
import ServiceInvoke.HttpRequest;
import UI.MessageBox;

/**
 * Created by iBaax on 3/27/16.
 */
public class ReportAgentDialog {

    Context context;
    IEvent listener;
    int ContactID;
    static List<Dictunary> ReportList=new ArrayList<>();
    String ReportID;
    String KeyID,KeyReportTypeID,KeyReportID;
    String URL;
    ReportListAdapter adapter;
    public ReportAgentDialog(Context context,int ContactID,String KeyID,
                             String KeyReportTypeID,String KeyReportID, String URL,IEvent listener){

        this.context=context;
        this.ContactID=ContactID;
        this.KeyID=KeyID;
        this.KeyReportID=KeyReportID;
        this.KeyReportTypeID=KeyReportTypeID;
        this.URL=URL;
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

           /* if(ReportList.size()>0){
                adapter=new ReportListAdapter(context, ReportList, new IEvent() {
                    @Override
                    public void onClick(Object obj) {
                        Dictunary dictunary=(Dictunary)obj;
                        for(int i=0;i<ReportList.size();i++){
                            ReportList.get(i).IsSelected=false;
                        }
                        dictunary.IsSelected=true;
                        ReportID=String.valueOf(dictunary.ID);
                        //adapter.notifyDataSetChanged();

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
                        //adapter.notifyDataSetChanged();
                    }
                });
                list.setAdapter(adapter);
            }*/
            if(ReportList.size()>0) {
                adapter=new ReportListAdapter(context, ReportList, new IEvent() {
                    @Override
                    public void onClick(Object obj) {
                        Dictunary dictunary=(Dictunary)obj;
                        for(int i=0;i<ReportList.size();i++){
                            ReportList.get(i).IsSelected=false;
                        }
                        dictunary.IsSelected=true;
                        ReportID=String.valueOf(dictunary.ID);
                        //adapter.notifyDataSetChanged();
                    }
                });
                list.setAdapter(adapter);
            }else {
                Dictunary dictunary = new Dictunary();
                dictunary.ID = 1;
                dictunary.Title = "Wrong Information";
                dictunary.IsSelected = false;
                ReportList.add(dictunary);
                adapter=new ReportListAdapter(context, ReportList, new IEvent() {
                    @Override
                    public void onClick(Object obj) {
                        Dictunary dictunary=(Dictunary)obj;
                        for(int i=0;i<ReportList.size();i++){
                            ReportList.get(i).IsSelected=false;
                        }
                        dictunary.IsSelected=true;
                        ReportID=String.valueOf(dictunary.ID);
                        //adapter.notifyDataSetChanged();
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
                    //String URL= AppGlobal.localHost+"/api/MProperty/ReportSave";
                    HashMap<String,String> jsonparams=new HashMap<String, String>();
                    jsonparams.put(KeyID,String.valueOf(ContactID));
                    jsonparams.put(KeyReportTypeID,ReportID);
                    jsonparams.put(KeyReportID,"0");
                    new HttpRequest(context, new IHttpResponse() {
                        @Override
                        public void RequestSuccess(Object response) {
                            try {
                                JSONObject jo=new JSONObject((String)response);
                                if(jo.getBoolean("success")) {
                                    MessageBox.Show(context, jo.getString("Message"));
                                }
                                dialog.dismiss();
                            }catch (JSONException e) {

                                //MessageBox.Show(context, (String) response);
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void RequestFailed(String response) {
                            MessageBox.Show(context,response);
                        }
                    }).HttpPost(jsonparams,URL);
                }
            });

        }catch (Exception e){

        }

    }


}
