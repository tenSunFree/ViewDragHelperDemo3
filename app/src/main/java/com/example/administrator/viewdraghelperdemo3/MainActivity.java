package com.example.administrator.viewdraghelperdemo3;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private DragLayout dl;
    private ListView lvLeft;
    private ListView lvMain;
    private ImageView ivMain2;
    private ImageView lvLeft2;
    private MyLinearLayout myMainLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();
        init();

        dl.setDragStatusListener(new DragLayout.onDragStatusChangeListener() {

            /** 關掉左面板時, 想執行哪些行為 */
            @Override
            public void onClose() {
                Util.showToast(getApplicationContext(), "onClose");

                /** 让图表晃动 */
                ObjectAnimator mAinm = ObjectAnimator.ofFloat(
                        ivMain2, "translationX", 20.0f);                     // ObjectAnimator的初始化, 沿X轴平移的效果, values的值 是Float類型 表示平移的距離
                mAinm.setDuration(500);                                                             // 動畫持續時間
                mAinm.setInterpolator(new CycleInterpolator(2));                             // CycleInterpolator 周期运动, 来回晃动四圈
                mAinm.start();
            }

            /** 打開左面板時, 想執行哪些行為 */
            @Override
            public void onOpen() {
                Util.showToast(getApplicationContext(), "onOpen");

                /** 让图表晃动 */
                ObjectAnimator mAinm = ObjectAnimator.ofFloat(
                        lvLeft2, "translationX", 20.0f);                     // ObjectAnimator的初始化, 沿X轴平移的效果, values的值 是Float類型 表示平移的距離
                mAinm.setDuration(500);                                                             // 動畫持續時間
                mAinm.setInterpolator(new CycleInterpolator(2));                             // CycleInterpolator 周期运动, 来回晃动四圈
                mAinm.start();

                /** 左面版listView随机设置一个条目 */
                /*
                Random random = new Random();
                int nextInt = random.nextInt();
                lvLeft.smoothScrollToPosition(nextInt);                                             // 平滑移动
                 */
            }

            /** 打開左面板的過程, 想實現哪些行為 */
            @Override
            public void onDragging(float percent) {
                Util.showToast(getApplicationContext(), "onDragging");

                ivMain2.setAlpha(1 - percent);                                                       // ivMain 會隨著打開左面板的過程, 逐漸隱藏
            }
        });
    }

    private void init() {

        /** 快速设置List字符串, 重写方法 设置字体顏色 */
        lvLeft.setAdapter(new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, Cheeses.areas) {
            @SuppressLint("ResourceAsColor")
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextSize(22);
                textView.setTextColor(0xffF4F3F4);                                                  // 關於顏色的轉換, #0080FF → 0xff0080FF, 0x: 代表颜色整数的标记, ff: 代表透明度, 0080FF：表示颜色
                return textView;
            }
        });

        /** 快速设置List字符串, 重写方法 设置字体顏色 */
        lvMain.setAdapter(new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, Cheeses.Taipei) {
            @SuppressLint("ResourceAsColor")
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextSize(18);
                textView.setTextColor(0xffF4F3F4);                                                  // 關於顏色的轉換, #0080FF → 0xff0080FF, 0x: 代表颜色整数的标记, ff: 代表透明度, 0080FF：表示颜色
                return textView;
            }
        });

        lvLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    /** 快速设置List字符串, 重写方法 设置字体顏色 */
                    lvMain.setAdapter(new ArrayAdapter<String>(
                            MainActivity.this, android.R.layout.simple_list_item_1, Cheeses.Taipei) {
                        @SuppressLint("ResourceAsColor")
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            TextView textView = (TextView) view;
                            textView.setTextSize(18);
                            textView.setTextColor(0xffF4F3F4);                                      // 關於顏色的轉換, #0080FF → 0xff0080FF, 0x: 代表颜色整数的标记, ff: 代表透明度, 0080FF：表示颜色
                            return textView;
                        }
                    });
                    dl.close();
                }

                if (i == 4) {
                    /** 快速设置List字符串, 重写方法 设置字体顏色 */
                    lvMain.setAdapter(new ArrayAdapter<String>(
                            MainActivity.this, android.R.layout.simple_list_item_1, Cheeses.Taichung) {
                        @SuppressLint("ResourceAsColor")
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            TextView textView = (TextView) view;
                            textView.setTextSize(18);
                            textView.setTextColor(0xffF4F3F4);                                      // 關於顏色的轉換, #0080FF → 0xff0080FF, 0x: 代表颜色整数的标记, ff: 代表透明度, 0080FF：表示颜色
                            return textView;
                        }
                    });
                    dl.close();
                }
            }
        });

        myMainLl.setDragLayout(dl);
    }

    private void findView() {
        dl = (DragLayout) findViewById(R.id.dl);
        lvLeft = (ListView) findViewById(R.id.lv_left);
        lvMain = (ListView) findViewById(R.id.lv_main);
        ivMain2 = (ImageView) findViewById(R.id.iv_main);
        lvLeft2 = (ImageView) findViewById(R.id.lvLeft2);
        myMainLl = (MyLinearLayout) findViewById(R.id.my_main_ll);
    }
}
