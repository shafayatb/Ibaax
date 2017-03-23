package Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibaax.com.ibaax.AppPrefs;
import com.ibaax.com.ibaax.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import Entity.AppGlobal;
import Entity.Dictunary;
import Event.IHttpResponse;
import ServiceInvoke.HttpRequest;

/**
 * Created by iBaax on 5/29/16.
 */
public class ServiceAreaAdapter extends RecyclerView.Adapter<ServiceAreaAdapter.ViewHolder> {

    AppPrefs prefs;
    private Context mContext;
    private List<Dictunary> list;

    public ServiceAreaAdapter(Context mContext, List<Dictunary> list) {
        this.mContext = mContext;
        this.list = list;
        prefs = new AppPrefs(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout_service_areas, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Dictunary Areas = list.get(position);
        holder.lblServiceArea.setText(Areas.Title);
        final int index = position;
        holder.imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String URL;
                if (prefs.getUserCategory().equals("2") || prefs.getUserCategory().equals("4")) {
                    URL = AppGlobal.localHost + "/api/MCompany/RemoveCompanyServiceArea?companyPreferredWorkLocationIds="
                            + Areas.ID + "&companyId=" + prefs.getCompanyID();
                } else {
                    URL = AppGlobal.localHost + "/api/MContacts/RemoveContactServiceArea?contactPreferredWorkLocationIds="
                            + Areas.ID + "&contactId=" + prefs.getContactID();
                }

                new HttpRequest(mContext, new IHttpResponse() {
                    @Override
                    public void RequestSuccess(Object response) {
                        try {
                            JSONArray jsonArray = new JSONArray((String) response);
                            if (jsonArray.length() < list.size()) {
                                list.remove(index);
                                notifyItemRemoved(index);
                                notifyItemRangeChanged(index, list.size());
                            }
                        } catch (JSONException e) {

                        }
                    }

                    @Override
                    public void RequestFailed(String response) {

                    }
                }).HttpPostProperty(null, URL);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView lblServiceArea;
        ImageView imgRemove;

        public ViewHolder(View itemView) {
            super(itemView);
            lblServiceArea = (TextView) itemView.findViewById(R.id.txt_service_area);
            imgRemove = (ImageView) itemView.findViewById(R.id.img_remove_service_area);
        }
    }

}
