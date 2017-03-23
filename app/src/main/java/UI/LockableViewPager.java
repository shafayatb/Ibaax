package UI;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by iBaax on 5/5/16.
 */
public class LockableViewPager extends ViewPager {

    private boolean swippable;

    public LockableViewPager(Context context) {
        super(context);
    }

    public LockableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.swippable = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (this.swippable) {
            return super.onTouchEvent(ev);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.swippable) {
            return super.onInterceptTouchEvent(ev);
        }

        return false;
    }

    public void setSwippable(boolean swippable) {
        this.swippable = swippable;
    }
}
