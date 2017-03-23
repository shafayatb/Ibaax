package Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ibaax.com.ibaax.AppPrefs;
import com.ibaax.com.ibaax.NavigationDrawerMainActivity;
import com.ibaax.com.ibaax.R;
import com.ibaax.com.ibaax.TutorialActivity;

import java.util.List;

import Entity.Tutorial;

/**
 * Created by iBaax on 2/8/16.
 */
public class MyCustomPagerAdapter extends PagerAdapter {

    Context context;
    LayoutInflater inflater;
    List<Tutorial> list;
    TutorialActivity.OnClickEvent listener;

    public MyCustomPagerAdapter(Context context, List<Tutorial> list, TutorialActivity.OnClickEvent listener) {

        this.context = context;
        this.list = list;
        this.listener = listener;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.row_viewpageritem, container,
                false);
        try {
            final int index = position;
            ImageView ImgTutorial = (ImageView) itemView.findViewById(R.id.img_tutorial_Image);
            TextView lblTutorialTitle = (TextView) itemView.findViewById(R.id.lbl_tutorial_titledesc);
            TextView lblTutorialDesc = (TextView) itemView.findViewById(R.id.lbl_tutorial_quickdesc);
            TextView lblSkip = (TextView) itemView.findViewById(R.id.lbl_tutorial_skip);
            final Button btnNext = (Button) itemView.findViewById(R.id.btn_next);
            final Button btnGetStarted = (Button) itemView.findViewById(R.id.btn_get_started);

            ImgTutorial.setImageDrawable(context.getResources().getDrawable(list.get(position).TutorialImage));
            lblTutorialTitle.setText(list.get(position).TutorialTitle);
            lblTutorialDesc.setText(list.get(position).TutorialDesc);
            lblSkip.setPaintFlags(lblSkip.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            lblSkip.setText("Skip");
            ((ViewPager) container).addView(itemView);
            if (position == 3) {
                btnNext.setVisibility(View.GONE);
                btnGetStarted.setVisibility(View.VISIBLE);
            } else {
                btnNext.setVisibility(View.VISIBLE);
                btnGetStarted.setVisibility(View.GONE);
            }
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onNextClick(index);
                }
            });

            btnGetStarted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AppPrefs(context).setFirstInstall(false);
                    Intent intent = new Intent(context, NavigationDrawerMainActivity.class);
                    context.startActivity(intent);
                    ((TutorialActivity) context).finish();
                }
            });
            lblSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AppPrefs(context).setFirstInstall(false);
                    Intent intent = new Intent(context, NavigationDrawerMainActivity.class);
                    context.startActivity(intent);
                    ((TutorialActivity) context).finish();
                }
            });
        } catch (Exception e) {
            Log.v("esty", "MyCustomPagerAdater/intantiateItem/Error: " + e.getMessage());
        }
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}
