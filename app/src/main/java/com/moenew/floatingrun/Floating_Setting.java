package com.moenew.floatingrun;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Floating_Setting extends Activity {

    EditText edit;
    CheckBox box1, box2, box3, box4;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.floating_setting);
        // 初始化
        edit = findViewById(R.id.cmd);

        box1 = findViewById(R.id.use_root);
        box2 = findViewById(R.id.info);
        box3 = findViewById(R.id.move_mode);
        box4 = findViewById(R.id.double_click);
        textView = findViewById(R.id.textview);
        loading();// 载入信息
    }

    private void loading() {// 载入信息
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);// 指定读取的文件
        String cmd = pref.getString("cmd", "");// 执行的命令
        Boolean use_root = pref.getBoolean("use_root", false);// 是否需要root
        Boolean info = pref.getBoolean("info", false);// 输出
        Boolean move_mode =  pref.getBoolean("move_mode", false);// 移动
        Boolean double_click = pref.getBoolean("double_click", false);// 双击模式

        String str = String.format(getResources().getString(R.string.textview),cmd,use_root,info,move_mode,double_click);

        textView.setText(str);// 输出设定内容
        // 根据文件记录信息,设定状态
        edit.setText(cmd);
        box1.setChecked(pref.getBoolean("use_root", false));
        box2.setChecked(pref.getBoolean("info", false));
        box3.setChecked(pref.getBoolean("move_mode", false));
        box4.setChecked(pref.getBoolean("double_click", false));

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putString("cmd", edit.getText().toString());
                editor.putBoolean("use_root", box1.isChecked());
                editor.putBoolean("info", box2.isChecked());
                editor.putBoolean("move_mode", box3.isChecked());
                editor.putBoolean("double_click", box4.isChecked());
                editor.apply();
                loading();
                run();
                //finish();
                break;
            case R.id.stop:
                stopService(new Intent(this, Floating.class));
                break;
        }
    }

    private void run() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(this)) {
                startService(new Intent(this, Floating.class));
            } else {
                Toast.makeText(this, "必须允许悬浮窗权限", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
            }
        } else {
            startService(new Intent(this, Floating.class));
        }
    }

}
