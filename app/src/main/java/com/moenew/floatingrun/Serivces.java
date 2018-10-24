package com.moenew.floatingrun;

import com.moenew.floatingrun.ShellUtils.CommandResult;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Serivces extends Service {

    private boolean waitDouble = true;
    private static final int DOUBLE_CLICK_TIME = 300;// 设定双击延迟

    // 定义浮动窗口布局
    LinearLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    // 创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;
    Button mFloatView;
    String string;
    boolean root, out, doubleclick, move;
    int x, y;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        // 从文件读取设定内容,不知道怎么传参好麻烦啊
        string = pref.getString("cmd", "");
        root = pref.getBoolean("root", false);
        out = pref.getBoolean("out", false);
        doubleclick = pref.getBoolean("doubleclick", false);
        move = pref.getBoolean("move", false);
        x = pref.getInt("x", 0);
        y = pref.getInt("y", 0);

        createFloatView();// 创建悬浮窗

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private void createFloatView() {// 创建悬浮窗
        wmParams = new WindowManager.LayoutParams();
        getApplication();
        // 获取WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) getApplication().getSystemService(
                Context.WINDOW_SERVICE);
        // 设置window type
        wmParams.type = LayoutParams.TYPE_PHONE;
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
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        // 获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.serivces,null);
        // 添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);// 设定悬浮窗位置
        // 浮动窗口按钮
        mFloatView = (Button) mFloatLayout.findViewById(R.id.float_id);
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        // 设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                wmParams.x = (int) event.getRawX()
                        - mFloatView.getMeasuredWidth() / 2;
                // 25为状态栏的高度
                wmParams.y = (int) event.getRawY()
                        - mFloatView.getMeasuredHeight() / 2 - 25;
                // 刷新
                if (move) {
                    mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                }
                return false;
            }
        });

        // 点击事件
        mFloatView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doubleclick) {// 判断双击执行,好复杂的逻辑,我已经看不懂了
                    if (waitDouble == true) {
                        waitDouble = false;
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    sleep(DOUBLE_CLICK_TIME);
                                    if (waitDouble == false) {
                                        waitDouble = true;
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();
                    } else {
                        waitDouble = true;
                        run();
                    }
                } else {
                    run();
                }

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 保存悬浮窗位置
        SharedPreferences.Editor editor = getSharedPreferences("data",
                MODE_PRIVATE).edit();
        editor.putInt("x", wmParams.x);
        editor.putInt("y", wmParams.y);
        editor.apply();

        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
    }

    private void run() {// 执行命令
        String[] commands = new String[] { string };
        CommandResult result = ShellUtils.execCommand(commands, root);
        if (out) {// 是否输出执行返回内容
            if (!result.successMsg.equals("")) {// 成功的提示
                Toast.makeText(Serivces.this, result.successMsg, Toast.LENGTH_SHORT).show();

            }
            if (!result.errorMsg.equals("")) {// 错误的提示
                Toast.makeText(Serivces.this, result.errorMsg, Toast.LENGTH_SHORT).show();

            }
            if (result.successMsg.equals("") && result.errorMsg.equals("")) {
                // 返回值为空的提示
                Toast.makeText(Serivces.this, "没有返回值", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
