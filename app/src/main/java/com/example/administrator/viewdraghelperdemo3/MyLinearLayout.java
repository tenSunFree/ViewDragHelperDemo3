package com.example.administrator.viewdraghelperdemo3;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 在打开状态, 控制左侧不能滑动, 点击或者滑动直接关闭
 */
public class MyLinearLayout extends LinearLayout {

    private DragLayout mDraglayout;

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDragLayout(DragLayout mDraglayout) {
        this.mDraglayout = mDraglayout;
    }

    /**
     * 用于进行点击事件的拦截
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        /** 如果当前左面版是关闭状态，按之前方法判断 */
        if (mDraglayout.getStatus() == DragLayout.Status.Close) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return true;                                                                            // 其实这里只有打开状态才会被ll拦截, 因为拖拽状态被DragLayout拦截了
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mDraglayout.getStatus() == DragLayout.Status.Close) {                                    // 如果当前状态是关闭, 按之前方法处理
            return super.onTouchEvent(event);
        } else {
            if (event.getAction() == MotionEvent.ACTION_UP) {                                      // //用up来判断是因为down事件一开始就会被执行, up事件需要返回true才会执行
                mDraglayout.close();
            }
            return true;
        }
    }
}
