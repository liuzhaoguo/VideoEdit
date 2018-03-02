//package com.sina.videoedit.video;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.view.Gravity;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.TextView;
//
//import com.sina.videoedit.R;
//
//
//public class SinaProgressDialog extends Dialog {
//    private SinaProgressDialog(Context context) {
//        super(context);
//    }
//
//    private SinaProgressDialog(Context context, int theme) {
//        super(context, theme);
//    }
//
//    public static SinaProgressDialog create(Context context, int layoutId, CharSequence message, boolean cancelable,
//                                            OnCancelListener cancelListener) {
//        SinaProgressDialog dialog = new SinaProgressDialog(context, R.style.SinaDialogStyle);
//        dialog.setTitle("");
//        dialog.setContentView(layoutId);
//        if (message == null || message.length() == 0) {
//            dialog.findViewById(R.id.message).setVisibility(View.GONE);
//        } else {
//            TextView txt = (TextView) dialog.findViewById(R.id.message);
//            txt.setText(message);
//        }
//        dialog.findViewById(R.id.progressbar).setAlpha(1);
//        dialog.setCancelable(cancelable);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setOnCancelListener(cancelListener);
//        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
//        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//        lp.dimAmount = 0.2f;
//        dialog.getWindow().setAttributes(lp);
//        // dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
//        return dialog;
//    }
//
//    public static SinaProgressDialog create(Context context, CharSequence message, boolean cancelable,
//                                            OnCancelListener cancelListener) {
//        return create(context, R.layout.view_custom_progress, message, cancelable, cancelListener);
//    }
//
//    public static SinaProgressDialog show(Context context, CharSequence message, boolean cancelable,
//                                          OnCancelListener cancelListener) {
//        SinaProgressDialog dialog = create(context, message, cancelable, cancelListener);
//        dialog.show();
//        return dialog;
//    }
//}
