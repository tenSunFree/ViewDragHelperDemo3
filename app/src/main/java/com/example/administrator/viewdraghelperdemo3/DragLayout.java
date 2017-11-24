package com.example.administrator.viewdraghelperdemo3;

import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by YQ on 2016/7/27.
 */

/**
 * 侧滑面板
 */
public class DragLayout extends FrameLayout {

    private ViewDragHelper dragHelper;
    private ViewGroup mLeftContent;
    private ViewGroup mMainContent;
    private FloatEvaluator floatEvaluator = new FloatEvaluator();                                   // 通过百分比, 循序变换
    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();                                      // 颜色差值器
    private int mHeight;
    private int mWidth;
    private int mRange;
    private onDragStatusChangeListener mListener;
    private Status mStatus = Status.Close;

    /**
     * 自定义 枚举常量
     */
    public static enum Status {
        Close, Open, Dragging
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status mStatus) {
        this.mStatus = mStatus;
    }

    public interface onDragStatusChangeListener {

        void onClose();

        void onOpen();

        void onDragging(float percent);
    }

    public void setDragStatusListener(onDragStatusChangeListener listener) {
        this.mListener = listener;
    }

    /**
     * 将三个构造函数串起来
     */
    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dragHelper = ViewDragHelper.create(this, mCallBack);
    }


    /**
     * 处理事件
     */
    ViewDragHelper.Callback mCallBack = new ViewDragHelper.Callback() {

        /**
         * 根据当前返回结果是否可以拖拽
         * 尝试捕获, 只要没有执行onCaptureView, 就会一直回调这个方法
         * @param child, 当前被拖拽的View
         * @param pointerId, 区分多点触摸的id
         * @return
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;                                                                            // 如果返回true, 那么哪个面板都可以拖拽, child==mMainContent表示, 只想要主面板被拖拽
        }

        /** 当tryViewCaptured返回true时, 调用且仅一次 */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        /** 返回拖住的范围，但不对拖拽进行真正的限制，仅仅决定动画执行的速度 */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return mRange;
        }

        /**
         * 根据建议值修正正要移动的位置(重要)
         * 实现: 拖动主面板, 露出底部的左面版
         * 手指的移动和视图的移动之间有延迟, 根据手指移动的建议值来修正位置
         *通过View的dragTo方法中的offsetLeftAndRight方法实现,但2.3版本没有在此方法实现invalidate方法，考虑到兼容，在onViewPositionChanged加入invalidate重绘
         * @param child 当前拖拽的View
         * @param left 新的位置的建议值(View左上角离屏幕左上角的水平距离),child.getLeft()获得变化之前的left
         * @param dx 位置变化量(瞬间)
         * @return  //表示移动到那个位置，（当然是新的建议位置，即left）
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            /** 限定距离 */
            if (child == mMainContent) {
                left = fixLeft(left);                                                               // 修正左边值(限定范围)
            }
            return left;
        }

        /** 拖动主面板不能超过屏幕最左边, 也不能超过mRange */
        private int fixLeft(int left) {
            if (left < 0) {
                return 0;
            } else if (left > mRange) {
                return mRange;
            }
            return left;
        }

        /**
         * 当View位置改变的时候, 处理要做的事情(更新状态, 重绘界面,伴随动画)
         * @param changedView
         * @param left 当前View的位置
         * @param top
         * @param dx
         * @param dy
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            /**
             * 实现:拖动mLeftContent
             * mLeftContent保持不动，mMainContent移动
             */

            int newLeft = left;
            if (changedView == mLeftContent) {
                newLeft = mMainContent.getLeft() + dx;                                              // 把当前变化量传给mMainContent
            }

            newLeft = fixLeft(newLeft);                                                             // 修正
            if (changedView == mLeftContent) {
                /** 当左面移动之后, 再强制放回去 */
                mLeftContent.layout(0, 0, 0 + mWidth, 0 + mHeight);                       // 固定mLeftContent
                mMainContent.layout(newLeft, 0, newLeft + mWidth, 0 + mHeight);             // 修改mMainContent的位置
            }

            dispatchDragEvent(newLeft);                                                             // 更新状态执行动画
            invalidate();                                                                           // 兼容低版本
        }

        /**
         * 当View被释放的时候处理的事件(执行动画)
         *只要抓取了，不管视图是否移动都会有速度
         * @param releasedChild 被释放的View
         * @param xvel 水平方向的速度(往右为正)
         * @param yvel 往下为正
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            /** 判断执行关闭 或者开启左面版 */
            if (xvel == 0 && mMainContent.getLeft() > mRange / 2.0f) {
                open();                                                                             // 拖动停止且主面板的位置在一半屏幕以上
            } else if (xvel > 0) {
                open();                                                                             // 默认平滑
            } else {
                close();                                                                            // 其他都关闭
            }
        }
    };


    /** 補充
     * setTranslationX改变了view的位置, 但没有改变view的LayoutParams里的margin属性; 也即不改变getLeft等view的信息
     * 它改变的是android:translationX 属性, 也即这个参数级别是和margin平行的
     */

    /**
     * 更新状态和执行动画
     *
     * @param newLeft
     */
    private void dispatchDragEvent(int newLeft) {

        float percent = newLeft * 1.0f / mRange;
        Status preStatus = mStatus;                                                                 //上一次状态
        mStatus = updateStatus(percent);                                                            //实时修改状态

        if (mStatus != preStatus) {
            if (mStatus == Status.Close) {
                if (mListener != null) {                                                            //要判空
                    mListener.onClose();
                }
            } else if (mStatus == Status.Open) {
                if (mListener != null) {
                    mListener.onOpen();
                }
            }
        } else {
            if (mListener != null) {
                mListener.onDragging(percent);
            }
        }

        animViews(percent);
    }

    private Status updateStatus(float percent) {
        if (percent == 0) {
            return Status.Close;
        } else if (percent == 1.0f) {
            return Status.Open;
        } else {
            return Status.Dragging;
        }
    }

    private void animViews(float percent) {

        /** 1. 左面板: (缩放动画, 平移动画, 透明度动画) */
        /** 缩放动画: 0.5-1.0的大小变化动画 */
        mLeftContent.setScaleX(floatEvaluator.evaluate(percent, 0.5f, 1.0f));
        mLeftContent.setScaleY(floatEvaluator.evaluate(percent, 0.5f, 1.0f));

        /** 平移动画：-mWidth/2.0f-0.0f */
        mLeftContent.setTranslationX(floatEvaluator.evaluate(percent, -mWidth / 2.0f, 1.0f));

        /** 透明度：0.5->1.0f */
        mLeftContent.setAlpha(floatEvaluator.evaluate(percent, 0.5f, 1.0f));


        /** 2. 主面板: 缩放动画 */
        mMainContent.setScaleX(floatEvaluator.evaluate(percent, 1.0f, 0.8f));
        mMainContent.setScaleY(floatEvaluator.evaluate(percent, 1.0f, 0.8f));

        /** 3. 背景动画: 亮度变化(颜色变化) */
        getBackground().setColorFilter(
                (Integer) argbEvaluator.evaluate(percent, Color.BLACK, Color.TRANSPARENT),
                PorterDuff.Mode.SRC_OVER
        );
    }


    /**
     * 平滑持续动画的模板
     */
    @Override
    public void computeScroll() {
        super.computeScroll();

        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);                                            //post排列起来，延时操作
        }
    }

    /**
     * 自动开启，自动关闭
     */
    public void close() {
        close(true);
    }

    public void open() {
        open(true);
    }

    /**
     * isSmooth是否平滑, open/close的是左面版, 移动是主面板
     *
     * @param isSmooth
     */
    public void close(boolean isSmooth) {

        int finalLeft = 0;

        if (isSmooth) {

            /** 触发一个平滑移动 */
            if (dragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {                // smoothSlideViewTo内部会调用Scroller的startScroll方法，computeScroll，移动,参数三为finalTop

                /**
                 * 返回true 表示还没有移动到指定位置, 需要刷新界面
                 * 参数传this(child所在的ViewGroup)
                 */
                ViewCompat.postInvalidateOnAnimation(this);                                        // post将动画排列起来，延时操作
            }
        } else {
            mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
        }
    }

    public void open(boolean isSmooth) {
        int finalLeft = mRange;
        if (isSmooth) {
            if (dragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
        }

    }


    /**
     * 传递触摸事件, 拖拽时才会拦截事件并交给DragLayout处理
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return dragHelper.shouldInterceptTouchEvent(ev);                                            // 传递事件给dragHelper，让他决定是否拦截
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);                                                        // dragHelper处理
        return true;                                                                                // 持续接受事件
    }

    /**
     * 拿到XML中View的引用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        /** 容错性检查(至少有俩子View, 子View必须是ViewGroup) */
        if (getChildCount() < 2) {
            throw new IllegalStateException("布局至少两个孩子");                                    //非法参数异常
        }
        if (!(getChildAt(0) instanceof ViewGroup && getChildAt(1) instanceof ViewGroup)) {
            throw new IllegalStateException("子View必须是ViewGroup的子类");                         //非法参数异常
        }
        mLeftContent = (ViewGroup) getChildAt(0);
        mMainContent = (ViewGroup) getChildAt(1);
    }

    /**
     * 尺寸有变化的时候调用
     * 获得DragLayout的长宽测量值和滑动最大值
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
        mRange = (int) (mWidth * 0.3);
    }
}
