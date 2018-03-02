package com.sina.videoedit.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.sina.videoedit.R;

public class CommonDialog extends Dialog implements View.OnClickListener {

    private TextView mTitle, mMsgTextView;
    private Button mRightBtn, mLeftBtn;
    private View mMsgLine;
    private ViewGroup mLayoutContent;
    private ClickCallbackListener mCallbackListener;
    private View container;
    private View divider;
    private View line;

    public CommonDialog(Context context, int themeMode) {
        this(context, false, themeMode);
    }

    public CommonDialog(Context context, boolean hideCancel, int themeMode) {
        super(context, R.style.CommonDialogStyle);
        View parent = LayoutInflater.from(context).inflate(R.layout.dialog_common_layout, null);
        container = parent.findViewById(R.id.container);
        divider = parent.findViewById(R.id.divider);
        mRightBtn = (Button) parent.findViewById(R.id.tips_dialog_btn_ok);
        mRightBtn.setOnClickListener(this);
        line = parent.findViewById(R.id.tips_dialog_line);
        if (hideCancel) {
            if (line != null) {
                line.setVisibility(View.GONE);
            }
            mRightBtn.setBackgroundResource(R.drawable.selector_dialog_single_button);
        }
        mLeftBtn = (Button) parent.findViewById(R.id.tips_dialog_btn_cancel);
        mLeftBtn.setOnClickListener(this);
        mLeftBtn.setVisibility(hideCancel ? View.GONE : View.VISIBLE);
        mTitle = (TextView) parent.findViewById(R.id.tips_dialog_title);
        mLayoutContent = (ViewGroup) parent.findViewById(R.id.layout_content);
        mMsgTextView = (TextView) parent.findViewById(R.id.tips_dialog_content);
        mMsgLine = parent.findViewById(R.id.tips_dialog_content_line);
        applyTheme(context, themeMode);
        setContentView(parent);
    }

    private void applyTheme(Context context, int themeMode) {
        switch (themeMode) {
            case Const.APP_THEME_DAY:
                break;
            case Const.APP_THEME_NIGHT:
                container.setBackgroundResource(R.drawable.dialog_bg_night);
                mTitle.setTextColor(context.getResources().getColor(R.color.color_text1_night));
                mMsgTextView.setTextColor(context.getResources().getColor(R.color.color_text1_night));
                mRightBtn.setTextColor(context.getResources().getColor(R.color.color_accent_night));
                mRightBtn.setBackgroundColor(Color.TRANSPARENT);
                mLeftBtn.setTextColor(context.getResources().getColor(R.color.color_accent_night));
                mLeftBtn.setBackgroundColor(Color.TRANSPARENT);
                divider.setBackgroundColor(context.getResources().getColor(R.color.color_divider_night));
                mMsgLine.setBackgroundColor(context.getResources().getColor(R.color.color_divider_night));
                if (line != null) {
                    line.setBackgroundColor(context.getResources().getColor(R.color.color_divider_night));
                }
                break;
        }
    }

//    @Override
//    protected View obtainView(Context context) {
//        View parent = LayoutInflater.from(context).inflate(R.layout.dialog_common_layout, null);
//        parent.findViewById(R.id.layout_sure).setOnClickListener(this);
//        parent.findViewById(R.id.layout_cancel).setOnClickListener(this);
//        mTitle = (TextView) parent.findViewById(R.id.title);
//        return parent;
//    }

    public void setTitle(String title) {
        if (title == null || title.isEmpty()) {
            mTitle.setVisibility(View.GONE);
        } else {
            mTitle.setVisibility(View.VISIBLE);
            mTitle.setText(title);
        }
    }

    public void setTitle(int titleResId) {
        if (titleResId == 0) {
            mTitle.setVisibility(View.GONE);
        } else {
            mTitle.setVisibility(View.VISIBLE);
            mTitle.setText(titleResId);
        }
    }

    public void setMessage(String message) {
        if (message == null || message.isEmpty()) {
            mMsgTextView.setVisibility(View.GONE);
        } else {
            mMsgTextView.setVisibility(View.VISIBLE);
            mMsgTextView.setText(message);
        }
    }

    public void setMessage(int msgResId) {
        if (msgResId != 0) {
            mMsgTextView.setVisibility(View.VISIBLE);
            mMsgTextView.setText(msgResId);
        } else {
            mMsgTextView.setVisibility(View.GONE);
        }
    }

    public void setMessageGravity(int gravity) {
        mMsgTextView.setGravity(gravity);
    }

    public void setMessageLineVisibility(int visibility) {
        mMsgLine.setVisibility(visibility);
    }

    public void setLeftButtonText(int resId) {
        if (resId != 0) {
            mLeftBtn.setText(resId);
        }
    }

    public void setLeftButtonText(String text) {
        if (!TextUtils.isEmpty(text)) {
            mLeftBtn.setText(text);
        }
    }

    public void setRightButtonText(int resId) {
        if (resId != 0) {
            mRightBtn.setText(resId);
        }
    }

    public void setRightButtonText(String text) {
        if (!TextUtils.isEmpty(text)) {
            mRightBtn.setText(text);
        }
    }

    public void setRightButtonTextColor(int color) {
        mRightBtn.setTextColor(color);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tips_dialog_btn_ok :
                fromSure();
                break;
            case R.id.tips_dialog_btn_cancel :
                fromCancel();
                break;
            default :
                break;
        }
    }

    protected void fromSure() {
        if (mCallbackListener != null) {
            mCallbackListener.fromSure(this);
        }
    }

    protected void fromCancel() {
        if (mCallbackListener != null) {
            mCallbackListener.fromCancel(this);
        }
    }

    public void setClickCallbackListener(ClickCallbackListener listener) {
        mCallbackListener = listener;
    }
    public interface ClickCallbackListener {

        void fromSure(CommonDialog dialog);

        void fromCancel(CommonDialog dialog);
    }

    public void setView(View view) {
        if (mLayoutContent != null) {
            mLayoutContent.removeAllViews();
            mLayoutContent.addView(view);
        }
    }

    public void requestInputMethod() {
        Window window = this.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void hideInputMethod() {
        Window window = this.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
}