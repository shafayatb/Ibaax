package UI;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by iBaax on 2/22/16.
 */
public class BlockImageView extends ImageView {
    public BlockImageView(Context context) {
        super(context);
    }

    public BlockImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BlockImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }
}
