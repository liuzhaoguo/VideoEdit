package com.sina.videoedit.video;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.sina.videoedit.R;

public class CameraButton extends ImageButton {
	public static final int STATE_INIT = 0;
	public static final int STATE_RECORDING = 1;
	public static final int STATE_PAUSED = 2;
	private int mState = STATE_INIT;

	public CameraButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CameraButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CameraButton(Context context) {
		super(context);
		setBackgroundResource(R.drawable.btn_record_state_init_selector);
	}

	public void setState(int state) {
		if (mState != state) {
			mState = state;
			int res = R.drawable.btn_record_state_init_selector;
			switch (state) {
			case STATE_INIT:
				res = R.drawable.btn_record_state_init_selector;
				break;
			case STATE_RECORDING:
				res = R.drawable.btn_record_state_recording_selector;
				break;
			case STATE_PAUSED:
				res = R.drawable.btn_record_state_paused_selector;
				break;
			default:
				break;
			}
			setBackgroundResource(res);
		}
	}

	public int getState(){
		return mState;
	}

}
