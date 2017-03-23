package Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibaax.com.ibaax.AppPrefs;
import com.ibaax.com.ibaax.R;
import com.ibaax.com.ibaax.SaveSearchResultActivity;

import org.json.JSONObject;

import java.util.List;

import Entity.AppGlobal;
import Entity.Dictunary;
import Event.IEvent;
import Event.IHttpResponse;
import ServiceInvoke.HttpRequest;
import UI.MessageBox;

/**
 * Created by iBaax on 6/26/16.
 */
public class SaveSearchListAdapter extends RecyclerView.Adapter<SaveSearchListAdapter.ViewHolder> {

    AppPrefs prefs;
    private Context mContext;
    private List<Dictunary> list;

    public SaveSearchListAdapter(Context mContext, List<Dictunary> list) {
        this.mContext = mContext;
        this.list = list;
        prefs = new AppPrefs(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_save_seach_list, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Dictunary Save = list.get(position);
        holder.lblSaveName.setText(Save.Name);
        holder.lblKeywords.setText(Save.Title);
        holder.lblCreateDate.setText(Save.ShortCuts);
        final int index = position;

        holder.imgOverflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.imgOverflowMenu, index);
            }
        });

        holder.cardSaveSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SaveSearchResultActivity.class);
                intent.putExtra("ID", String.valueOf(Save.ID));
                intent.putExtra("Name", Save.Name);
                intent.putExtra("URL", Save.URL);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void showPopupMenu(View view, int index) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_save_search, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(index));
        popup.show();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView lblSaveName;
        TextView lblKeywords;
        TextView lblCreateDate;
        ImageView imgOverflowMenu;
        CardView cardSaveSearch;

        public ViewHolder(View itemView) {
            super(itemView);
            lblSaveName = (TextView) itemView.findViewById(R.id.lbl_save_name);
            lblKeywords = (TextView) itemView.findViewById(R.id.lbl_save_keywords);
            lblCreateDate = (TextView) itemView.findViewById(R.id.lbl_save_create);
            imgOverflowMenu = (ImageView) itemView.findViewById(R.id.img_overflow_menu);
            cardSaveSearch = (CardView) itemView.findViewById(R.id.card_view_save_search);
        }
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        int position;

        public MyMenuItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_savesearch_rename:

                    String RenURL = AppGlobal.localHost + "/api/MSavedSearch/RenameSavedSearch?id=" + list.get(position).ID
                            + "&name=";

                    MessageBox.SaveSearchRename(mContext, RenURL, list.get(position).Name, new IEvent() {
                        @Override
                        public void onClick(Object obj) {
                            list.get(position).Name = (String) obj;
                            notifyDataSetChanged();
                        }
                    });

                    return true;
                case R.id.action_savesearch_delete:
                    String DelURL = AppGlobal.localHost + "/api/MSavedSearch/DeleteSavedSearch?id=" + list.get(position).ID;
                    new HttpRequest(mContext, new IHttpResponse() {
                        @Override
                        public void RequestSuccess(Object response) {
                            try {
                                JSONObject jo = new JSONObject((String) response);
                                if (jo.getInt("flag") == 1) {
                                    list.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, list.size());
                                }
                            } catch (Exception e) {

                            }
                        }

                        @Override
                        public void RequestFailed(String response) {

                        }
                    }).HttpPostProperty(null, DelURL);

                    return true;
            }

            return false;
        }


    }

}
