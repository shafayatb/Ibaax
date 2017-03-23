package UI;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;
/**
 * Created by S.R Rain on 1/11/2016.
 */
public class CustomExpandedListView extends ListView
{
    //private android.view.ViewGroup.LayoutParams params;
    private int old_count = 0;

    /*public ExpandedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getCount() != old_count) {
            old_count = getCount();
            params = getLayoutParams();
            params.height = getCount()
                    * (old_count > 0 ? getChildAt(0).getHeight() : 0);
            setLayoutParams(params);
        }

        super.onDraw(canvas);
    }*/
    public CustomExpandedListView(Context context) {
        super(context);
    }
    public CustomExpandedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CustomExpandedListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    /* @Override
     public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
             int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                     Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
             super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
             ViewGroup.LayoutParams params = getLayoutParams();
             params.height = getMeasuredHeight();
     }*/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Do not use the highest two bits of Integer.MAX_VALUE because they are
        // reserved for the MeasureSpec mode
        int heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightSpec);
        getLayoutParams().height = getMeasuredHeight();
    }
}
