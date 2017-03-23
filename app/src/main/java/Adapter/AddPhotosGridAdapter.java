package Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.ibaax.com.ibaax.AppController;
import com.ibaax.com.ibaax.EditPhotoActivity;
import com.ibaax.com.ibaax.R;

import java.util.List;

import Entity.PropertyPhotoPortfolio;
import Event.IEvent;

/**
 * Created by iBaax on 2/22/16.
 */
public class AddPhotosGridAdapter extends BaseAdapter {

    Context context;
    List<PropertyPhotoPortfolio> PhotosList;
    IEvent listener;
    ImageLoader imageLoader;
    Bitmap bitmap;

    public AddPhotosGridAdapter(Context context, List<PropertyPhotoPortfolio> PhotosList, IEvent listener) {

        this.context = context;
        this.PhotosList = PhotosList;
        this.listener = listener;
        imageLoader = AppController.getInstance().getImageLoader();
    }

    @Override
    public int getCount() {
        return PhotosList.size();
    }

    @Override
    public Object getItem(int i) {
        return PhotosList.get(i);
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
        try {
            if (v == null) {

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.row_singleimage, null);
                holder = new ViewHolder();
                holder.imgPhotos = (UI.BlockImageView) v.findViewById(R.id.img_add_photos);
                holder.lnrBackground = (LinearLayout) v.findViewById(R.id.lnr_add_photo_activity);
                v.setTag(holder);

            } else {
                holder = (ViewHolder) v.getTag();
            }

            if (i == 0) {
                holder.imgPhotos.setImageBitmap(PhotosList.get(i).PhotoBitmap);
                holder.lnrBackground.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.background_border));
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onClick(PhotosList.get(index));
                    }
                });

            } else {
                holder.imgPhotos.setImageResource(R.drawable.photo_coming_soon);


                imageLoader.get(PhotosList.get(i).PropertyPhotoPortfolio, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response.getBitmap() != null) {
                            int nh = (int) (response.getBitmap().getHeight() * (512.0 / response.getBitmap().getWidth()));
                            Bitmap scaled = Bitmap.createScaledBitmap(response.getBitmap(), 512, nh, true);
                            holder.imgPhotos.setImageBitmap(scaled);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                holder.lnrBackground.setBackgroundDrawable(null);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, EditPhotoActivity.class);
                        Bitmap bm = ((BitmapDrawable) holder.imgPhotos.getDrawable()).getBitmap();
                        intent.putExtra("PhotoURL", PhotosList.get(index).PropertyPhotoPortfolio);
                        intent.putExtra("PhotoID", String.valueOf(PhotosList.get(index).PropertyPhotoPortfolioID));
                        intent.putExtra("PhotoName", PhotosList.get(index).PhotoTitle);
                        intent.putExtra("FileName", PhotosList.get(index).DocumentFileName);
                        context.startActivity(intent);
                    }
                });
            }

        } catch (Exception e) {

        }


        return v;
    }

    public static class ViewHolder {
        UI.BlockImageView imgPhotos;
        LinearLayout lnrBackground;
    }
}
