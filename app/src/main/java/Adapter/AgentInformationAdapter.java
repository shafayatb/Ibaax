package Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ibaax.com.ibaax.R;

import java.util.List;

import Entity.AgentInformation;

/**
 * Created by iBaax on 4/7/16.
 */
public class AgentInformationAdapter extends BaseAdapter {
    List<AgentInformation> list;
    Context context;

    public AgentInformationAdapter(Context context, List<AgentInformation> list) {
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
        final int index = i;
        ViewHolder holder;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.row_agent_information, null);
            holder = new ViewHolder();
            holder.Title = (TextView) v.findViewById(R.id.lbl_agentinfo_title);
            holder.Value = (TextView) v.findViewById(R.id.lbl_agentinfo_value);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.Title.setText(list.get(i).Title);
        holder.Value.setText(list.get(i).Value);

        if (list.get(i).Type.equals("Call")) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Call " + list.get(index).Value + " ?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do some thing here which you need
                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + list.get(index).Value));
                                    context.startActivity(intent);
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();

                }
            });
        } else {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        return v;
    }

    class ViewHolder {

        LinearLayout lnrBig, lnrSmall;
        TextView Title, Value;

    }
}
