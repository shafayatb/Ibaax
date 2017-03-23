package UI;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by iBaax on 6/8/16.
 */
public class WorkAroundNestedScrollView extends NestedScrollView {

    public WorkAroundNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // Explicitly call computeScroll() to make the Scroller compute itself
            computeScroll();
        }
        return super.onInterceptTouchEvent(ev);
    }
}
