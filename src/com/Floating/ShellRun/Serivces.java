package com.Floating.ShellRun;

import com.Floating.ShellRun.ShellUtils.CommandResult;
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
	private static final int DOUBLE_CLICK_TIME = 300;// �趨˫���ӳ�

	// ���帡�����ڲ���
	LinearLayout mFloatLayout;
	WindowManager.LayoutParams wmParams;
	// ���������������ò��ֲ����Ķ���
	WindowManager mWindowManager;
	Button mFloatView;
	String string;
	boolean root, out, doubleclick, move;
	int x, y;

	@Override
	public void onCreate() {
		super.onCreate();
		SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
		// ���ļ���ȡ�趨����,��֪����ô���κ��鷳��
		string = pref.getString("cmd", "");
		root = pref.getBoolean("root", false);
		out = pref.getBoolean("out", false);
		doubleclick = pref.getBoolean("doubleclick", false);
		move = pref.getBoolean("move", false);
		x = pref.getInt("x", 0);
		y = pref.getInt("y", 0);

		createFloatView();// ����������

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private void createFloatView() {// ����������
		wmParams = new WindowManager.LayoutParams();
		getApplication();
		// ��ȡWindowManagerImpl.CompatModeWrapper
		mWindowManager = (WindowManager) getApplication().getSystemService(
				Context.WINDOW_SERVICE);
		// ����window type
		wmParams.type = LayoutParams.TYPE_PHONE;
		// ����ͼƬ��ʽ��Ч��Ϊ����͸��
		wmParams.format = PixelFormat.RGBA_8888;
		// ���ø������ڲ��ɾ۽���ʵ�ֲ���������������������ɼ����ڵĲ�����
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
		// ������������ʾ��ͣ��λ��Ϊ����ö�
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;

		// ����Ļ���Ͻ�Ϊԭ�㣬����x��y��ʼֵ
		wmParams.x = x;
		wmParams.y = y;

		// �����������ڳ�������
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		LayoutInflater inflater = LayoutInflater.from(getApplication());
		// ��ȡ����������ͼ���ڲ���
		mFloatLayout = (LinearLayout) inflater.inflate(R.layout.serivces, null);
		// ���mFloatLayout
		mWindowManager.addView(mFloatLayout, wmParams);// �趨������λ��
		// �������ڰ�ť
		mFloatView = (Button) mFloatLayout.findViewById(R.id.float_id);
		mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

		// ���ü����������ڵĴ����ƶ�
		mFloatView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// getRawX�Ǵ���λ���������Ļ�����꣬getX������ڰ�ť������
				wmParams.x = (int) event.getRawX()
						- mFloatView.getMeasuredWidth() / 2;
				// 25Ϊ״̬���ĸ߶�
				wmParams.y = (int) event.getRawY()
						- mFloatView.getMeasuredHeight() / 2 - 25;
				// ˢ��
				if (move) {
					mWindowManager.updateViewLayout(mFloatLayout, wmParams);
				}
				return false;
			}
		});

		// ����¼�
		mFloatView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (doubleclick) {// �ж�˫��ִ��,�ø��ӵ��߼�,���Ѿ���������
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
		// ����������λ��
		SharedPreferences.Editor editor = getSharedPreferences("data",
				MODE_PRIVATE).edit();
		editor.putInt("x", wmParams.x);
		editor.putInt("y", wmParams.y);
		editor.commit();

		if (mFloatLayout != null) {
			mWindowManager.removeView(mFloatLayout);
		}
	}

	private void run() {// ִ������
		String[] commands = new String[] { string };
		CommandResult result = ShellUtils.execCommand(commands, root);
		if (out) {// �Ƿ����ִ�з�������
			if (!result.successMsg.equals("")) {// �ɹ�����ʾ
				Toast.makeText(Serivces.this, result.successMsg, 0).show();

			}
			if (!result.errorMsg.equals("")) {// �������ʾ
				Toast.makeText(Serivces.this, result.errorMsg, 0).show();

			}
			if (result.successMsg.equals("") && result.errorMsg.equals("")) {
				// ����ֵΪ�յ���ʾ
				Toast.makeText(Serivces.this, "û�з���ֵ", 0).show();
			}
		}
	}
}
