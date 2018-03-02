package com.sina.videoedit;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sina.videoedit.video.CameraButton;
import com.sina.videoedit.video.CameraContainer;
import com.sina.videoedit.video.CameraView;
import com.sina.videoedit.util.Const;
import com.sina.videoedit.util.FileOperateUtil;
import com.sina.videoedit.util.Mp4Util;
import com.sina.videoedit.util.SinaLog;
//import com.sina.videoedit.video.SinaProgressDialog;
import com.sina.videoedit.util.ToastUtils;
import com.sina.videoedit.video.VideoAlbumEngine;
import com.sina.videoedit.video.VideoChooseEvent;
import com.sina.videoedit.video.VideoItem;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "MainActivity";
    private CameraContainer mContainer;
    private CameraButton mRecordShutterButton;
    private ImageView mFlashView;
    private ImageView mSwitchCameraView;
    private View mTVCancel;
    private View mPreviewCancel;
    private TextView mTVPreView;
    private TextView mTVComplete;
    private Dialog mProgressDialog;

    private boolean isRecording = false;
    private boolean isFinish = false;
    private boolean isModule = false;

    private TextView textView;
    private Button button;

    @Override
    public void onCreate(Bundle savedInstance) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstance);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_camera);

        mContainer = (CameraContainer) findViewById(R.id.container);
        mRecordShutterButton = (CameraButton) findViewById(R.id.btn_shutter_record);
        mSwitchCameraView = (ImageView) findViewById(R.id.btn_switch_camera);
        mFlashView = (ImageView) findViewById(R.id.btn_flash_mode);
        mTVCancel = findViewById(R.id.cancel);
        mPreviewCancel = findViewById(R.id.preview_cancel);
        mTVPreView = (TextView) findViewById(R.id.preview);
        mTVComplete = (TextView) findViewById(R.id.complete);

        mRecordShutterButton.setOnClickListener(this);
        mFlashView.setOnClickListener(this);
        mSwitchCameraView.setOnClickListener(this);
        mTVCancel.setOnClickListener(this);
        mTVComplete.setOnClickListener(this);
        mTVPreView.setOnClickListener(this);
        mPreviewCancel.setOnClickListener(this);

        mContainer.setRecordActionListener(new CameraContainer.RecordActionListener() {
            @Override
            public void record1sCall() {
                // 显示录制和预览
                mTVPreView.setVisibility(View.VISIBLE);
                mTVComplete.setVisibility(View.VISIBLE);
            }

            @Override
            public void recordPause(boolean notify) {
                isRecording = false;
                mRecordShutterButton.setClickable(true);
                mRecordShutterButton.setState(CameraButton.STATE_PAUSED);
                if (notify) {
                    ToastUtils.showToast(MainActivity.this, "视频录制已暂停");
                }
            }

            @Override
            public void recordFinish() {
                isFinish = true;
                isRecording = false;
                mRecordShutterButton.setState(CameraButton.STATE_INIT);
                mRecordShutterButton.setClickable(false);

                if (mFlashView != null) {
                    if (mContainer.getFlashMode() == CameraView.FlashMode.TORCH) {
                        onClick(mFlashView);//关闭闪光灯
                    }

                    //禁用闪光灯按钮事件
                    mFlashView.setClickable(false);
                }
                if (mSwitchCameraView != null) {
                    //禁用切换摄像头按钮
                    mSwitchCameraView.setClickable(false);
                }
            }

            @Override
            public void recording() {
                if (!isFinish) {
                    mRecordShutterButton.setClickable(true);
                }
            }
        });

        mContainer.setPreviewActionListener(new CameraContainer.PreviewActionListener() {
            @Override
            public void doPrepare() {
//                showProgressDialog();
            }

            @Override
            public void startPreview() {
//                hideProgressDialog();
                showPreviewMode(true);
            }

            @Override
            public void completePreview() {
                showPreviewMode(false);
            }

            @Override
            public void errorPreview(int what, int extra) {
                SinaLog.e(TAG, "errorPreview: " + what + ", " + extra);
                ToastUtils.showToast(MainActivity.this, "预览异常，请重新录制");
//                hideProgressDialog();
                showPreviewMode(false);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_flash_mode:    // 闪光灯模式
                if (mContainer.isBusy()) {
                    return;
                }
                int record_state = mRecordShutterButton.getState();
                if (record_state == CameraButton.STATE_RECORDING) {
                    ToastUtils.showToast(MainActivity.this, "拍摄中禁止使用闪光灯");
                    return;
                }
                if (mContainer.getFlashMode() == CameraView.FlashMode.TORCH) {
                    mContainer.setFlashMode(CameraView.FlashMode.OFF);
                    mFlashView.setImageResource(R.mipmap.btn_flash_off);
                } else if (mContainer.getFlashMode() == CameraView.FlashMode.OFF) {
                    mContainer.setFlashMode(CameraView.FlashMode.TORCH);
                    mFlashView.setImageResource(R.mipmap.btn_flash_on);
                }
                break;

            case R.id.btn_shutter_record:    // 录制
                if (mContainer.isBusy()) {
                    return;
                }

                mRecordShutterButton.setClickable(false);

                int state = mRecordShutterButton.getState();
                if (state == CameraButton.STATE_INIT) {
                    isRecording = mContainer.startRecord();
                    if (isRecording) {
                        mRecordShutterButton.setState(CameraButton.STATE_RECORDING);
                    }
                } else if (state == CameraButton.STATE_RECORDING) {
                    // TODO 点击后应该将按钮置灰，等待返回结果
                    mRecordShutterButton.setClickable(false);
                    if (isRecording) {
                        mRecordShutterButton.setState(CameraButton.STATE_PAUSED);
                    }
                    isRecording = false;
                    boolean isPause = mContainer.pauseRecord(false);
                    SinaLog.d(TAG, "暂停结果 : " + isPause);
                    mRecordShutterButton.setClickable(true);
                } else if (state == CameraButton.STATE_PAUSED) {
                    mRecordShutterButton.setState(CameraButton.STATE_RECORDING);
                    boolean isResume = mContainer.resumeRecord();
                    SinaLog.d(TAG, "暂停后再次录制结果 : " + isResume);
                    isRecording = true;
                }
                break;
            case R.id.btn_switch_camera:    //前后摄像头切换
                if (mContainer.isBusy()) {
                    return;
                }

                // 正在录制的时候切换
                if (mContainer.isRecording()) {
                    mRecordShutterButton.setClickable(false);
                }
                mContainer.switchCamera();
                break;
            case R.id.cancel:
                mContainer.clear();    // 清理工作
                finish();
                break;
            case R.id.preview_cancel:
                showPreviewMode(false);
                stopPreviewVideo();
                break;
            case R.id.preview:
                if (isRecording) {
                    ToastUtils.showToast(this, "拍摄中禁止预览");
                    break;
                }

                if (mContainer.getAllFilePath() == null || mContainer.getAllFilePath().isEmpty()) {
                    break;
                }
                showPreviewMode(true);
                previewViewVideo();
                break;
            case R.id.complete:    //完成
                mContainer.stopRecord(true);
                List<String> paths = mContainer.getAllFilePath();
                if (paths != null) {
                    if (paths.isEmpty()) {
                        finish();
                    } else {
//                        showProgressDialog();
                        EditMovieTask task = new EditMovieTask();
                        task.execute(paths);
                    }
                }
                break;
            default:
                break;
        }
    }


    /**
     * 录像模式或者预览模式
     *
     * @param isShow true显示预览模式、false显示录像模式
     */
    private void showPreviewMode(boolean isShow) {
        mFlashView.setVisibility(isShow ? View.GONE : View.VISIBLE);
        mSwitchCameraView.setVisibility(isShow ? View.GONE : View.VISIBLE);
        mRecordShutterButton.setVisibility(isShow ? View.GONE : View.VISIBLE);
        mTVCancel.setVisibility(isShow ? View.GONE : View.VISIBLE);
        mTVComplete.setVisibility(isShow ? View.GONE : View.VISIBLE);
        mTVPreView.setVisibility(isShow ? View.GONE : View.VISIBLE);

        mPreviewCancel.setVisibility(isShow ? View.VISIBLE : View.GONE);
        mContainer.showCameraView(!isShow);

    }

    @Override
    public void onBackPressed() {
        // 清理工作
        mContainer.clear();
        super.onBackPressed();
    }

    private void stopPreviewVideo() {
        mContainer.stopPreview();
    }

    private void previewViewVideo() {
        mContainer.startPreview();
    }

    public class EditMovieTask extends AsyncTask<List<String>, Void, String> {
        @Override
        protected String doInBackground(List<String>... params) {
            List<String> pathList = params[0];

            if (pathList == null) {
                return null;
            }

            String newPath = null;
            if (pathList.size() == 1) {
                //只有一个视频时，直接返回，不需要任何操作
                newPath = pathList.get(0);
            } else if (pathList.size() > 1) {
                //多个视频需要合成一个mp4 到 newPath下
                String path = FileOperateUtil.getFolderPath(getApplicationContext(), FileOperateUtil.TYPE_VIDEO);
                String name = "video_" + FileOperateUtil.createFileNmae(".mp4");
                newPath = path + File.separator + name;

                boolean succ = Mp4Util.append(pathList, newPath);
                SinaLog.d(TAG, "merge video result: " + succ);
                if (!succ) {
                    newPath = null;
                }

                //暂时忽略合成结果，直接删除所有视频
                delAllFile(pathList);
            }
            return newPath;
        }

        @Override
        protected void onPostExecute(String result) {
//            hideProgressDialog();
            if (TextUtils.isEmpty(result)) {
                ToastUtils.showToast(MainActivity.this, "操作失败，请重试");
//                finish();
            } else {
                finishCamera(result);
            }
        }
    }

//    private void showProgressDialog() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (mProgressDialog == null) {
//                    mProgressDialog = SinaProgressDialog.create(MainActivity.this, "", false, null);
//                }
//                if (!mProgressDialog.isShowing()) {
//                    mProgressDialog.show();
//                }
//            }
//        });
//    }

//    private void hideProgressDialog() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (mProgressDialog != null) {
//                    mProgressDialog.dismiss();
//                }
//            }
//        });
//    }

    private void delAllFile(List<String> listPaths) {
        if (listPaths == null || listPaths.isEmpty()) {
            return;
        }

        File file = null;
        for (String path : listPaths) {
            file = new File(path);
            delFile(file);
            SinaLog.d(TAG, "delete video: " + path);
        }
    }

    private void delFile(File file) {
        if (file != null && file.exists()) {
            try {
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void finishCamera(String videoPath) {
        SinaLog.d(TAG, "finish camera video: " + videoPath);

        //通知媒体服务及时更新文件
        MediaScannerConnection.scanFile(this, new String[]{videoPath}, null, null);

//        if (isModule) {
//            Intent intent = new Intent();
//            intent.putExtra(Const.BundleKey.DATA, videoPath);
//            setResult(RESULT_OK, intent);
//            finish();
//            return;
//        }

        //把videoPath 带到result参数里面去
        MediaPlayer mp = new MediaPlayer();
        long duration = 0;
        try {
            mp.setDataSource(videoPath);
            duration = mp.getDuration();
            mp.release();
        } catch (IOException e) {
            e.printStackTrace();
        }

        VideoItem item = new VideoItem();
        item.path = videoPath;
        item.duration = duration;
        item.displayName = item.title;
        File f = new File(videoPath);
        item.title = f.getName();
        item.size = f.length();

        VideoAlbumEngine.add(item);
        SinaLog.d(TAG, "finish camera VideoAlbumEngine videoSize: " + VideoAlbumEngine.getVideos().size());

        Intent intent = new Intent();
        intent.putExtra(Const.BundleKey.DATA, true);
        setResult(RESULT_OK, intent);
        ToastUtils.showToast(MainActivity.this, "视频已保存：" + videoPath);
//        finish();

        EventBus.getDefault().post(new VideoChooseEvent());
    }

    @Override
    protected void onDestroy() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        super.onDestroy();
    }
}