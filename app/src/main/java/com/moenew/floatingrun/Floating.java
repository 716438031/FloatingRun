package com.moenew.floatingrun;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

public class Floating extends Service {

    // 定义浮动窗口布局
    LinearLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    // 创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;
    Button mFloatView;
    String string;
    boolean use_root, info, move_mode;
    int x, y;

    @Override
    public void onCreate() {
        super.onCreate();
        createFloatView();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stop();
        createFloatView();// 创建悬浮窗
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @SuppressLint({"ClickableViewAccessibility", "InflateParams"})
    //屏蔽报错
    private void createFloatView() {// 创建悬浮窗

        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        // 从文件读取设定内容
        string = pref.getString("cmd", "");
        use_root = pref.getBoolean("use_root", false);
        info = pref.getBoolean("info", false);
        move_mode = pref.getBoolean("move_mode", false);
        x = pref.getInt("x", 0);
        y = pref.getInt("y", 0);

        wmParams = new WindowManager.LayoutParams();

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= 26) {//8.0新特性
            wmParams.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
        }

        // 设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        // 调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.START | Gravity.TOP;

        // 以屏幕左上角为原点，设置x、y初始值
        wmParams.x = x;
        wmParams.y = y;
        // 设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // 设定浮动窗口视图布局
        mFloatLayout = (LinearLayout) LayoutInflater.from(getApplication()).inflate(R.layout.floating, null);
        // 设定悬浮窗位置
        mWindowManager.addView(mFloatLayout, wmParams);
        // 浮动窗口按钮
        mFloatView = mFloatLayout.findViewById(R.id.button);

        // 设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new OnTouchListener() {

            float mInScreenX;
            float mInScreenY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //按下
                        break;
                    case MotionEvent.ACTION_MOVE://移动
                        //获取移动到的坐标,减去控件本身的大小(纠偏)
                        if (move_mode) {
                            mInScreenX = event.getRawX() - mFloatView.getMeasuredWidth() / 2;
                            mInScreenY = event.getRawY() - mFloatView.getMeasuredHeight();
                            //获取坐标,每次移动都会更新

                            wmParams.x = (int) (mInScreenX);
                            wmParams.y = (int) (mInScreenY);
                            //写入算好的坐标

                            mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                            //设置悬浮窗位置
                        }

                        break;
                    case MotionEvent.ACTION_UP://松开
                        Log.e(TAG, event.getRawX() + "+" + event.getRawY());
                        Log.e(TAG, event.getX() + "+" + event.getY());
                        if (mInScreenX + mInScreenY != 0) {
                            //Log.e(TAG, mInScreenX + mInScreenY + "");
                            mInScreenX = 0;
                            mInScreenY = 0;
                        } else {
                            //如果没有移动,则判断为点击
                            run();
                            Log.e(TAG, "已执行");
                        }

                        break;
                }

                return false;
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
    }

    private void run() {
        // 执行命令
        String[] commands = new String[]{string};
        ShellUtils.CommandResult result = ShellUtils.execCommand(commands, use_root);
        if (info) {
            // 是否输出执行返回内容
            if (!result.successMsg.equals("")) {
                // 成功的提示
                Toast.makeText(Floating.this, result.successMsg, Toast.LENGTH_SHORT).show();

            }
            if (!result.errorMsg.equals("")) {
                // 错误的提示
                Toast.makeText(Floating.this, result.errorMsg, Toast.LENGTH_SHORT).show();

            }
            if (result.successMsg.equals("") && result.errorMsg.equals("")) {
                // 返回值为空的提示
                Toast.makeText(Floating.this, "没有返回值", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void stop() {
        //保存当前坐标位置
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putInt("x", wmParams.x);
        editor.putInt("y", wmParams.y);
        editor.apply();

        mWindowManager.removeView(mFloatLayout);
    }
}
