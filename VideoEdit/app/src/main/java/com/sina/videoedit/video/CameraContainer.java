package com.sina.videoedit.video;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.sina.videoedit.R;
import com.sina.videoedit.util.FileOperateUtil;
import com.sina.videoedit.util.Mp4Util;
import com.sina.videoedit.util.SinaLog;
import com.sina.videoedit.video.CameraView.FlashMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @ClassName: CameraContainer
 * @Description: 相机界面的容器 包含相机绑定的surfaceview、拍照后的临时图片View和聚焦View
 * @author LinJ
 * @date 2014-12-31 上午9:38:52
 *
 */
public class CameraContainer extends RelativeLayout implements CameraOperation {

	public final static String TAG = "CameraContainer";
	public static final int MAX_DURATION = 30 * 1000;

	/** 相机绑定的SurfaceView */
	private CameraView mCameraView;

	private TimeView mTimeView;

    /**拍摄预览**/
	private VideoView mVideoView;

	/** 用以执行定时任务的Handler对象 */
	private Handler mHandler;
	private long mRecordStartTime;
	private long mLastRecordTime;// 已经记录的时间, 毫秒数
	private long mDuration; //视频时长
	private SimpleDateFormat mTimeFormat;
	private boolean mIsBusy = false;
	private int mCurPosition = -1;//预览时记录当前播放位置
	private String mCurPath;//预览时记录视频地址

	public long getDuration(){
		return mDuration;
	}

    public interface RecordActionListener {
        void record1sCall();
        void recordPause(boolean notify);
        void recordFinish();
		void recording();
    }

    /**
     * 预览回调接口
     */
    public interface PreviewActionListener {
		/**
		 *准备工作：目前主要是合成视频
		 */
		void doPrepare();
		/**
         * 开始预览回调
         */
        void startPreview();

        /**
         * 视频播放完毕回调
         */
        void completePreview();

        /**
         * 预览发生错误时回调
         */
        void errorPreview(int what, int extra);
    }

    public PreviewActionListener mPreviewActionListener;
    public void setPreviewActionListener(PreviewActionListener previewActionListener) {
        this.mPreviewActionListener = previewActionListener;
    }

    public RecordActionListener mRecordActionListener;
    public void setRecordActionListener(RecordActionListener recordActionListener) {
        this.mRecordActionListener = recordActionListener;
    }

	public CameraContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
        initView(context);
        mHandler = new Handler();
        mTimeFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
    }

	/**
	 * 初始化子控件
	 *
	 * @param context
	 */
	private void initView(Context context) {
		inflate(context, R.layout.layout_camera_container, this);
		mCameraView = (CameraView) findViewById(R.id.cameraView);
		mTimeView = (TimeView) findViewById(R.id.camera_time_view);
		mTimeView.setVisibility(View.GONE);
        mVideoView = (VideoView) findViewById(R.id.videoView);

        showCameraView(true);

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mCurPosition = -1;
				mCurPath = null;

				if (getAllFilePath() == null || getAllFilePath().isEmpty()) {
					return;
				}

				if (mPreviewActionListener != null) {
					mPreviewActionListener.completePreview();
				}
			}
		});

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mPreviewActionListener != null) {
                    mPreviewActionListener.startPreview();
                }
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
				mCurPosition = -1;
				mCurPath = null;

				if (mPreviewActionListener != null) {
					mPreviewActionListener.errorPreview(what, extra);
				}
				return true;
			}
		});
    }

	@Override
	public boolean startRecord() {
		mIsBusy = true;
		if (mCameraView.startRecord()) {
			mTimeView.setVisibility(View.VISIBLE);
			mTimeView.setTextString("00:00");
			mTimeView.setMaxProgress(MAX_DURATION);
			mTimeView.setProgress(0);

			mRecordStartTime = SystemClock.uptimeMillis();
			SinaLog.d(TAG, "mCameraView.startRecord() 开始录制");
			// 时间并不准确
			mHandler.postAtTime(recordRunnable, mRecordStartTime);
			mIsBusy = false;
			return true;
		} else {
			mIsBusy = false;
			return false;
		}
	}

    /**
     * 暂停
     * @param notify 是否通知
     * @return
     */
	public boolean pauseRecord(boolean notify) {
		mIsBusy = true;
		boolean pauseResult = mCameraView.pause();
        mLastRecordTime += SystemClock.uptimeMillis() - mRecordStartTime;
        if (pauseResult) {
			mHandler.removeCallbacks(recordRunnable);
            if (mRecordActionListener != null) {
                mRecordActionListener.recordPause(notify);
            }
		} else {
			SinaLog.e(TAG, "暂停失败了！");
		}
		mIsBusy = false;
		return pauseResult;
	}

	// 从暂停状态再次开始录制
	public boolean resumeRecord() {
		mIsBusy = true;
		mTimeView.setVisibility(View.VISIBLE);

        if (mCameraView.startRecord()) {
            mRecordStartTime = SystemClock.uptimeMillis();
            SinaLog.d(TAG, "mCameraView.resumeRecord() 暂停后再次录制");
			mHandler.postAtTime(recordRunnable, mRecordStartTime + 500);
			mIsBusy = false;
			return true;
		} else {
			mIsBusy = false;
			return false;
		}
	}

	Runnable recordRunnable = new Runnable() {
		@Override
		public void run() {
			if (mRecordActionListener != null) {
				mRecordActionListener.recording();
			}
			if (mCameraView.isRecording()) {
				// 时间并不准确
				long recordTime = mLastRecordTime + SystemClock.uptimeMillis() - mRecordStartTime;
				if (recordTime > 1000) {
                    // 大于1秒时，才显示录制和预览
                    if (mRecordActionListener != null) {
                        mRecordActionListener.record1sCall();
                    }
                }
				if (recordTime >= MAX_DURATION) {
                    // 先UI显示停止录制了
                    removeCallbacks(this);
                    mCameraView.stopRecord(true);
					mCameraView.stopPreview();//停止相机预览
					if (mRecordActionListener != null) {
                        mRecordActionListener.recordFinish();
                    }
                }
				mDuration = recordTime;
                mTimeView.setTextString(mTimeFormat.format(new Date(recordTime)));
				mTimeView.setProgress((int) recordTime);
				mHandler.postAtTime(this, SystemClock.uptimeMillis());
			}
		}
	};

	@Override
	public boolean stopRecord(boolean complete) {
//        mRecordingInfoTextView.setVisibility(View.GONE);
//		mPBRecordTime.setVisibility(View.GONE);

//		Bitmap thumbnailBitmap = mCameraView.stopRecord(complete);
//		if (thumbnailBitmap != null) {
//			// mTempImageView.setListener(mListener);
//			// mTempImageView.isVideo(true);
//			// mTempImageView.setImageBitmap(thumbnailBitmap);
//			// mTempImageView.startAnimation(R.anim.tempview_show);
//		}
//		return thumbnailBitmap;

        //停止预览
        stopPreview();

        if (mCameraView.isRecording()) {
            return mCameraView.stopRecord(complete);
        }
        return false;
    }

	/**
	 * 改变相机模式 在拍照模式和录像模式间切换 两个模式的初始缩放级别不同
	 */
//	public void switchMode(int zoom) {
	public void switchMode() {
		mCameraView.setZoom(mCameraView.getZoom());
		// 自动对焦
		mCameraView.onFocus(getWidth() / 2, getHeight() / 2);
	}

	/**
	 * 前置、后置摄像头转换
	 */
	@Override
	public void switchCamera() {
		mIsBusy = true;
        if (mCameraView.isRecording()) {
            pauseRecord(true);
        }
		mCameraView.switchCamera();
		mIsBusy = false;
	}

	/**
	 * 获取当前闪光灯类型
	 *
	 * @return
	 */
	@Override
	public FlashMode getFlashMode() {
		return mCameraView.getFlashMode();
	}

	/**
	 * 设置闪光灯类型
	 *
	 * @param flashMode
	 */
	@Override
	public void setFlashMode(FlashMode flashMode) {
		mCameraView.setFlashMode(flashMode);
	}

	@Override
	public int getMaxZoom() {
		return mCameraView.getMaxZoom();
	}

	@Override
	public void setZoom(int zoom) {
		mCameraView.setZoom(zoom);
	}

	@Override
	public int getZoom() {
		return mCameraView.getZoom();
	}

	public String getVideoPath() {
		return mCameraView.getVideoPath();
	}

	public void showCameraView(boolean show) {
		mCameraView.setVisibility(show ? View.VISIBLE : View.GONE);
        mVideoView.setVisibility(show ? View.GONE : View.VISIBLE);

        mTimeView.setVisibility(show && mTimeView.getProgress() > 0 ? View.VISIBLE : View.GONE);
    }

	public void startPreview() {
		mCurPosition = -1;
		mCurPath = null;

		if (getAllFilePath() == null || getAllFilePath().isEmpty()) {
			return;
		}

		if (mPreviewActionListener != null) {
			mPreviewActionListener.doPrepare();
		}

		if (getAllFilePath().size() == 1) {
			doPreview(getAllFilePath().get(0));
		} else {
			//多个视频预览时先合并再播放，预防卡顿
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					String path = FileOperateUtil.getFolderPath(getContext(), FileOperateUtil.TYPE_VIDEO);
					String name = "video_" + FileOperateUtil.createFileNmae(".mp4");
					String newPath = path + File.separator + name;

					boolean succ = Mp4Util.append(getAllFilePath(), newPath);
					if (succ) {
						for (String string : getAllFilePath()) {
							File file = new File(string);
							if (file.exists()) {
								try {
									file.delete();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}

						getAllFilePath().clear();
						getAllFilePath().add(newPath);

						doPreview(getAllFilePath().get(0));
					}
				}
			});
		}

	}

	private void doPreview(String path) {
		mCurPath = path;
		showCameraView(false);
		mVideoView.setVideoPath(new File(path).getAbsolutePath());
		mVideoView.start();
	}

    public void stopPreview() {
		mCurPosition = -1;
		mCurPath = null;
        showCameraView(true);
        if (mVideoView.isPlaying()) {
            mVideoView.stopPlayback();
        }
    }

	/**
	 * 暂停预览
	 */
	public void pausePreview() {
		if (mVideoView.isPlaying() && mVideoView.canPause()) {
			mVideoView.pause();
			mCurPosition = mVideoView.getCurrentPosition();
		} else {
			mCurPosition = -1;
		}
	}

	/**
	 * 继续开始预览
	 */
	public void reStartPreview() {
		if (mCurPosition < 0 || TextUtils.isEmpty(mCurPath)) {
			return;
		}
		mVideoView.setVideoPath(new File(mCurPath).getAbsolutePath());
		mVideoView.start();
		mVideoView.seekTo(mCurPosition);

		mCurPosition = -1;
	}

	public List<String> getAllFilePath() {
		return mCameraView.getPaths();
	}

	public void clear() {
		//TODO 是否需要清理其他？
        mCameraView.stopRecord(true);
        stopPreview();
        mCameraView.clear();

        mVideoView.pause();
	}

    public boolean isRecording() {
        return mCameraView.isRecording();
    }

	public boolean isBusy() {
		return mIsBusy;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
//		int height = width * 4 / 3;
		int height = MeasureSpec.getSize(heightMeasureSpec);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
