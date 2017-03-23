package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.ibaax.com.ibaax.R;

import java.util.List;

import Entity.Dictunary;
import Event.IEvent;

/**
 * Created by iBaax on 3/20/16.
 */
public class ReportListAdapter extends BaseAdapter {
    Context context;
    List<Dictunary>list;
    IEvent listener;

    public ReportListAdapter(Context context,List<Dictunary>list, IEvent listener){
        this.context=context;
        this.list=list;
        this.listener=listener;
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
        View v=view;
        ViewHolder holder;
        final int index=i;
        if(v==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v=inflater.inflate(R.layout.row_reportdialog,null);
            holder=new ViewHolder();
            holder.rdReportType=(RadioButton)v.findViewById(R.id.radio_report_type);
            v.setTag(holder);

        }else{
            holder=(ViewHolder)v.getTag();
        }

        if(list.get(i).IsSelected){
            holder.rdReportType.setChecked(true);
        }else{
            holder.rdReportType.setChecked(false);
        }
        holder.rdReportType.setText(list.get(i).Title);
        holder.rdReportType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(compoundButton.isChecked()){
                        listener.onClick(list.get(index));
                    }else{
                        list.get(index).IsSelected=false;
                    }
                notifyDataSetChanged();
            }
        });

        return v;
    }

    class ViewHolder{
        RadioButton rdReportType;
    }
}
