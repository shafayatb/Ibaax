package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ibaax.com.ibaax.R;

import java.util.List;

import Entity.AgentReview;

/**
 * Created by iBaax on 4/18/16.
 */
public class AgentReviewAdapter extends BaseAdapter {

    List<AgentReview> list;
    Context context;

    public AgentReviewAdapter(Context context, List<AgentReview> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ViewHolder holder;
        try {

            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.row_list_agent_review, null);
                holder = new ViewHolder();
                holder.lblReviewDate = (TextView) v.findViewById(R.id.lbl_review_date);
                holder.lblReviewer = (TextView) v.findViewById(R.id.lbl_review_name);
                holder.lblReviewDesc = (TextView) v.findViewById(R.id.lbl_review_text);
                holder.ReviewRate = (RatingBar) v.findViewById(R.id.rate_review);
                v.setTag(holder);

            } else {
                holder = (ViewHolder) v.getTag();
            }

            String []Date=list.get(i).CreateDate.split("T");
            holder.lblReviewDate.setText(Date[0]);
            holder.lblReviewer.setText("Recommendation by "+list.get(i).FirstName+" "+list.get(i).LastName);
            holder.lblReviewDesc.setText(list.get(i).RecommendationInfo);
            holder.ReviewRate.setRating(Float.valueOf(list.get(i).RatingScale));

        }catch (Exception e){


        }

        return v;
    }

    class ViewHolder {

        public TextView lblReviewDate;
        public TextView lblReviewer;
        public TextView lblReviewDesc;
        public RatingBar ReviewRate;

    }
}
