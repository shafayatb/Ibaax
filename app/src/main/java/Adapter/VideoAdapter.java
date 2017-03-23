package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.ibaax.com.ibaax.AppController;
import com.ibaax.com.ibaax.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import Entity.Dictunary;
import Event.IHttpResponse;
import Event.IProperty;
import ServiceInvoke.HttpRequest;

/**
 * Created by iBaax on 5/26/16.
 */
public class VideoAdapter extends BaseAdapter {

    List<Dictunary> VideoList;
    Context context;
    IProperty listener;

    public VideoAdapter(Context context, List<Dictunary> VideoList, IProperty listener) {

        this.context = context;
        this.VideoList = VideoList;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return VideoList.size();
    }

    @Override
    public Object getItem(int i) {
        return VideoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v = view;
        final ViewHolder holder;
        final int index = i;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.row_videos, null);
            holder = new ViewHolder();
            holder.imgVideoThumbnail = (NetworkImageView) v.findViewById(R.id.img_youtube_video);
            holder.imgDelete = (ImageView) v.findViewById(R.id.img_delete_video);
            holder.YoutubeLink = (TextView) v.findViewById(R.id.lbl_youtube_link);
            v.setTag(holder);

        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.imgVideoThumbnail.setImageUrl(VideoList.get(i).Title, AppController.getInstance().getImageLoader());
        holder.YoutubeLink.setText(VideoList.get(i).Name);
        /*new HttpRequest(context, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                try {
                    JSONObject jo = new JSONObject(response.toString());
                    holder.YoutubeLink.setText(jo.getString("title"));
                } catch (JSONException e) {

                }
            }

            @Override
            public void RequestFailed(String response) {

            }
        }).JSONObjectRequest(null, "https://www.youtube.com/oembed?url=" +
                VideoList.get(i).Name + "&format=json");*/
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(VideoList.get(index).ID);
            }
        });


        return v;
    }

    class ViewHolder {

        NetworkImageView imgVideoThumbnail;
        TextView YoutubeLink;
        ImageView imgDelete;

    }

}
