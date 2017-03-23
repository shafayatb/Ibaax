package Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.ibaax.com.ibaax.AppController;
import com.ibaax.com.ibaax.R;
import com.ibaax.com.ibaax.TutorialActivity;

import java.util.List;

import Entity.PropertyGallary;
import Event.IHttpResponse;
import ServiceInvoke.DownloadBitmap;

/**
 * Created by iBaax on 2/25/16.
 */
public class PropertyImageSwipeAdapter extends PagerAdapter {

    Context context;
    int[] flag;
    LayoutInflater inflater;
    List<PropertyGallary> list;
    TutorialActivity.OnClickEvent listener;
    ImageLoader imageLoader= AppController.getInstance().getImageLoader();
    public PropertyImageSwipeAdapter(Context context, List<PropertyGallary> list, TutorialActivity.OnClickEvent listener) {

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
        View itemView = inflater.inflate(R.layout.popup_property_gallary, container,
                false);
        try {
            final int index = position;
            final ImageView ImgGalleray = (ImageView) itemView.findViewById(R.id.img_gallary);

            imageLoader.get(list.get(position).PropertyPhotoPortfolio.replace(" ","%20"), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if(response.getBitmap()!=null){
                        ImgGalleray.setImageBitmap(response.getBitmap());
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            ((ViewPager) container).addView(itemView);

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