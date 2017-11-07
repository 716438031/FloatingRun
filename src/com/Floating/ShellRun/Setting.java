package com.Floating.ShellRun;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class Setting extends Activity {
	EditText edit;
	CheckBox box1, box2, box3, box4;
	TextView text;
	boolean mode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		// ��ʼ��
		edit = (EditText) findViewById(R.id.cmd_in);
		box1 = (CheckBox) findViewById(R.id.root);
		box2 = (CheckBox) findViewById(R.id.out);
		box3 = (CheckBox) findViewById(R.id.move);
		box4 = (CheckBox) findViewById(R.id.doubleclick);
		text = (TextView) findViewById(R.id.setting_out);
		loading();// ������Ϣ
	}

	private void loading() {// ������Ϣ
		SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);// ָ����ȡ���ļ�
		String cmd = pref.getString("cmd", "");// ִ�е�����
		String root = "" + pref.getBoolean("root", false);// �Ƿ���Ҫroot
		String out = "" + pref.getBoolean("out", false);// ���
		String move = "" + pref.getBoolean("move", false);// �ƶ�
		String doubleclick = "" + pref.getBoolean("doubleclick", false);// ˫��ģʽ
		text.setText("ָ�" + cmd + "\n" + "Root��" + root + "\n" + "�����" + out
				+ "\n" + "�ƶ�ģʽ" + move + "\n" + "˫��ģʽ" + doubleclick);// ����趨����
		// �����ļ���¼��Ϣ,�趨״̬
		edit.setText(cmd);
		box1.setChecked(pref.getBoolean("root", false));
		box2.setChecked(pref.getBoolean("out", false));
		box3.setChecked(pref.getBoolean("move", false));
		mode = pref.getBoolean("move", false);
		box4.setChecked(pref.getBoolean("doubleclick", false));

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start:
			SharedPreferences.Editor editor = getSharedPreferences("data",
					MODE_PRIVATE).edit();
			editor.putString("cmd", edit.getText().toString());
			editor.putBoolean("root", box1.isChecked());
			editor.putBoolean("out", box2.isChecked());
			editor.putBoolean("move", box3.isChecked());
			editor.putBoolean("doubleclick", box4.isChecked());
			editor.commit();
			loading();
			run();
			finish();
			break;
		case R.id.stop:
			stopService(new Intent(this, Serivces.class));
			break;
		}
	}

	private void run() {

		stopService(new Intent(this, Serivces.class));
		startService(new Intent(this, Serivces.class));

//		}
	}
}
