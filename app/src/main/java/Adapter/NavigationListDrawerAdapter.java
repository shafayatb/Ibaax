package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ibaax.com.ibaax.R;

import java.util.Collections;
import java.util.List;

import Entity.Drawer;

/**
 * Created by iBaax on 3/19/16.
 */
public class NavigationListDrawerAdapter extends BaseAdapter {
    List<Drawer> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;


    public NavigationListDrawerAdapter(Context context, List<Drawer> data) {
        this.context = context;
        inflater = (LayoutInflater.from(context));
        this.data = data;

    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final DrawerItemHolder drawerHolder;
        View v = view;
        if (v == null) {
            drawerHolder = new DrawerItemHolder();
            v = inflater.inflate(R.layout.nav_drawer_row, viewGroup, false);
            drawerHolder.navTitle = (TextView) v.findViewById(R.id.nav_drawer_title);
            drawerHolder.navTitleSingle = (TextView) v.findViewById(R.id.nav_drawer_title_single);
            drawerHolder.navIcon = (ImageView) v.findViewById(R.id.img_drawericon);
            drawerHolder.OneItem = (RelativeLayout) v.findViewById(R.id.rel_one_item);
            drawerHolder.TwoItem = (RelativeLayout) v.findViewById(R.id.rel_two_item);
            v.setTag(drawerHolder);
        }else{
            drawerHolder = (DrawerItemHolder) v.getTag();
        }
        Drawer current=data.get(i);
        if(current.ItemType.equals("two")) {
            drawerHolder.TwoItem.setVisibility(View.VISIBLE);
            drawerHolder.navTitle.setText(current.Text);
            drawerHolder.navIcon.setImageResource(current.ImageID);
        }else{
            drawerHolder.TwoItem.setVisibility(View.GONE);
        }
        if(current.ItemType.equals("noitem")){
            drawerHolder.OneItem.setVisibility(View.VISIBLE);
            drawerHolder.navTitleSingle.setText(current.Text);
        }else{
            drawerHolder.OneItem.setVisibility(View.GONE);
        }

        return v;
    }

    static class DrawerItemHolder {

        TextView navTitle, navTitleSingle;
        ImageView navIcon;
        RelativeLayout OneItem, TwoItem;


    }

}
