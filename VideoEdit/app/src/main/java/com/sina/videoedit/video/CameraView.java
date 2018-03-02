package com.sina.videoedit.video;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sina.videoedit.util.CommonDialog;
import com.sina.videoedit.util.FileOperateUtil;
import com.sina.videoedit.util.SinaLog;
import com.sina.videoedit.util.ToastUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LinJ
 * @ClassName: CameraView
 * @Description: 和相机绑定的SurfaceView 封装了拍照方法
 * @date 2014-12-31 上午9:44:56
 */
public class CameraView extends SurfaceView implements CameraOperation, MediaRecorder.OnErrorListener {

    public final static String TAG = "CameraView";
    private final static String TIP_ERROR_EXCEPTION = "相机异常请检查";
    private final static String TIP_ERROR_CAMERA = "请检查相机权限";
    private final static String TIP_ERROR_AUDIO = "请检查录音权限";

    /**
     * 和该View绑定的Camera对象
     */
    private Camera mCamera;

    /**
     * 当为前置摄像头时记录cameraID
     **/
    private int mCameraId;

    /**
     * 当前闪光灯类型，默认为关闭
     */
    private FlashMode mFlashMode = FlashMode.OFF;

    /**
     * 当前缩放级别 默认为0
     */
    private int mZoom = 0;

    /**
     * 当前屏幕旋转角度
     */
    private int mOrientation = 0;
    /**
     * 是否打开前置相机,true为前置,false为后置
     */
    private boolean mIsFrontCamera;
    /**
     * 录像类
     */
    private MediaRecorder mMediaRecorder;
    /**
     * 相机配置，在录像前记录，用以录像结束后恢复原配置
     */
    private Camera.Parameters mParameters;
    private SurfaceHolder mSurfaceHolder;
    private boolean mIsRecording;
    private boolean isOpenCamera = false;

    private CommonDialog mDialog;

    /**
     * 录像存放路径 ，用以生成缩略图
     */
    private String mRecordPath = null;

    private List<String> mPaths;

    private Size mBestPreviewSize;

    public List<String> getPaths() {
        return mPaths;
    }

    protected boolean isRecording() {
        return mIsRecording;
    }


    public CameraView(Context context) {
        super(context);
        // 初始化容器
        getHolder().addCallback(mCallback);
        mPaths = new ArrayList<String>();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 初始化容器
        getHolder().addCallback(mCallback);
        mPaths = new ArrayList<String>();
    }

    private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            if (mCamera == null) {
                isOpenCamera = openCamera();
            }

            if (isOpenCamera) {
                try {
                    mCamera.setPreviewDisplay(holder);
                    mCamera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //ToastUtils.showToast(getContext(), "摄像权限未打开");
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            SinaLog.e(TAG, "SurfaceHolder.Callback surfaceChanged");
            if (mCamera != null) {
                mSurfaceHolder = holder;
//                setCameraParameters();
                try {
                    mCamera.autoFocus(autoFocusCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            SinaLog.e(TAG, "SurfaceHolder.Callback surfaceDestroyed");
            // 停止录像
            stopRecord(true);
            freeCameraResource();
        }
    };

    /**
     * 根据当前照相机状态(前置或后置)，打开对应相机
     */
    private boolean openCamera() {
        freeCameraResource();

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (mIsFrontCamera) {
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    mCameraId = i;
                    break;
                }
            } else {
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    mCameraId = i;
                    break;
                }
            }
        }

        try {
            mCamera = Camera.open(mCameraId);
            if (mCamera == null) {
                return false;
            }
            setCameraParameters();
        } catch (Exception e) {
            freeCameraResource();
            showDialog(TIP_ERROR_CAMERA);
            SinaLog.e(TAG, "open camera failed: " + e.getMessage());
            return false;
        }

        isOpenCamera = true;

        return true;
    }

    /**
     * 设置照相机参数
     */
    private void setCameraParameters() {
        if (mCamera == null) {
            return;
        }
        mParameters = mCamera.getParameters();
//		// 设置适合的尺寸
//        Size bestPreviewSize = determineBestPreviewSize(mParameters);
        Size bestPreviewSize = getOptimalPreviewSize(mParameters.getSupportedPreviewSizes(), getWidth(), getHeight());
        System.out.println("zjt bestPreviewSize = "+bestPreviewSize.width +" , "+bestPreviewSize.height);

        mBestPreviewSize = bestPreviewSize;
        mParameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
//        mParameters.setPreviewSize(1440, 1080);
        SinaLog.d(TAG, "bestPreviewSize width = " + bestPreviewSize.width + " , bestPreviewSize height = " + bestPreviewSize.height);

//		// 选择合适的预览尺寸
//		List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
//		Log.e(TAG, "设置照相机参数 = " + (sizeList == null ? "" : sizeList.size()));
//		if (sizeList.size() > 0) {
//			Size cameraSize = sizeList.get(sizeList.size() - 1);
//			// 预览图片大小
//			Log.e(TAG, "width = " + cameraSize.width + " , height = " + cameraSize.height);
//			parameters.setPreviewSize(cameraSize.width, cameraSize.height);
//		}

//        if (!mIsFrontCamera) {
//            // 自动聚焦模式
//            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//        }
        mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//        mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦

        mCamera.setParameters(mParameters);
        // 设置闪光灯模式。此处主要是用于在相机摧毁后又重建，保持之前的状态
        setFlashMode(mFlashMode);
        // 设置缩放级别
        setZoom(mZoom);
        // 开启屏幕朝向监听
        startOrientationChangeListener();
        // 当没有发现屏幕改变时 也需要调用一次
        updateCameraOrientation();
    }

    private void freeCameraResource() {
        isOpenCamera = false;

        try {
            if (mCamera != null) {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
            mCamera = null;
            SinaLog.e(TAG, "freeCameraResource error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 停止相机预览
     */
    public void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    /**
     * 开始录像
     *
     * @return 开始录像是否成功
     */
    @Override
    public boolean startRecord() {
        if (!isOpenCamera) {
            //相机没有打开
            showDialog(TIP_ERROR_EXCEPTION);
            return false;
        }

        //检查录音权限
        if (!getAudioPermission()) {
            showDialog(TIP_ERROR_AUDIO);
            return false;
        }

        long t1 = System.currentTimeMillis();
        if (mCamera == null) {
            openCamera();
        }
        if (mCamera == null) {
            return false;
        }
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        } else {
            mMediaRecorder.reset();
        }
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

//         mMediaRecorder.setVideoSize(mWidth, mHeight);// 设置分辨率：
//         mMediaRecorder.setVideoEncodingBitRate(1 * getWidth() * getHeight());// 设置帧频率，然后就清晰了

        // 前置摄像头需要对垂直方向做变换，否则照片是颠倒的
        int rotation = 90 + mOrientation == 360 ? 0 : 90 + mOrientation;
        if (mIsFrontCamera) {
            if (rotation == 90)
                rotation = 270;
            else if (rotation == 270)
                rotation = 90;
            mMediaRecorder.setOrientationHint(rotation);// 保持竖屏录制
        } else {
            mMediaRecorder.setOrientationHint(rotation);// 保持竖屏录制
        }

        // 设置录像参数，由于应用需要此处取一个较小格式的视频
        CamcorderProfile profile = getCameraProfile();
        if (profile != null) {
            profile.videoCodec = MediaRecorder.VideoEncoder.H264;
            profile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
            profile.audioCodec = AudioEncoder.AAC;
            mMediaRecorder.setProfile(profile);
        } else {
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setAudioEncoder(AudioEncoder.AAC);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        }

        String path = FileOperateUtil.getFolderPath(getContext(), FileOperateUtil.TYPE_VIDEO);
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdirs();
        try {
            String name = "video_" + FileOperateUtil.createFileNmae(".mp4");
            mRecordPath = path + File.separator + name;
            SinaLog.d(TAG, "Record video path: --> " + mRecordPath);
            mPaths.add(mRecordPath);

            File mRecAudioFile = new File(mRecordPath);
            mMediaRecorder.setOutputFile(mRecAudioFile.getAbsolutePath());
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mIsRecording = true;
        } catch (Exception e) {
            SinaLog.d(TAG, "startRecord error : " + e.getMessage());
            e.printStackTrace();
            mIsRecording = false;
            return false;
        }
        long t2 = System.currentTimeMillis();
        SinaLog.e(TAG, "Record prepare time: --> " + (t2 - t1));

        return true;
    }

    /**
     * 获取录音权限是否打开
     *
     * @return true权限打开，false未打开
     */
    private boolean getAudioPermission() {
        boolean audio_permission = false;
        try {
            SinaLog.e(TAG, "audio start");
            int frequency = 44100;
            int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
            int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
            int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration,
                    audioEncoding, bufferSize);
            audioRecord.startRecording();
            int state_init = audioRecord.getState();
            int state_recording = audioRecord.getRecordingState();
            SinaLog.e(TAG, "audio state init : " + state_init);
            SinaLog.e(TAG, "audio state recording: " + state_recording);
            audio_permission = state_init == AudioRecord.STATE_INITIALIZED && state_recording == AudioRecord.RECORDSTATE_RECORDING;
            audioRecord.stop();
            audioRecord.release();
        } catch (Exception e) {
            SinaLog.e(TAG, "audio error: " + e.getMessage());
            e.printStackTrace();
        }

        return audio_permission;
    }

    private CamcorderProfile getCameraProfile() {
        CamcorderProfile profile = null;
        Size size = mBestPreviewSize;
        int width = size.width;
        int height = size.height;
        System.out.println("zjt getCameraProfile size(" + width + "," + height + ")");
        if ((width == 1080 || width == 1088) && height == 1920) {//1080p
            if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_1080P)) {
                profile = CamcorderProfile.get(mCameraId, CamcorderProfile.QUALITY_1080P);
            }
        } else if (width == 720 && height == 1280) {//720p
            if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
                profile = CamcorderProfile.get(mCameraId, CamcorderProfile.QUALITY_720P);
            }
        } else if (width == 480 && height == 720) {//480p
            if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
                profile = CamcorderProfile.get(mCameraId, CamcorderProfile.QUALITY_480P);
            }
        } else if (width == 2160 && height == 3840) {//2160p
            if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_2160P)) {
                profile = CamcorderProfile.get(mCameraId, CamcorderProfile.QUALITY_2160P);
            }
        }
        boolean needChangeVideoSize = profile == null;
        if (profile == null) {
            if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
                profile = CamcorderProfile.get(mCameraId, CamcorderProfile.QUALITY_480P);
            } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_HIGH)) {
                profile = CamcorderProfile.get(mCameraId, CamcorderProfile.QUALITY_HIGH);
            }
        }
        if (needChangeVideoSize && profile != null) {
            double targetRatio = width > height ? ((double) height / width) : ((double) width / height);
            System.out.println("zjt getCameraProfile needChangeVideoSize  targetRatio = " + targetRatio);
            if (Math.abs(targetRatio - ((double) 240 / 320)) <= 0.01) {
                if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_QVGA)) {
                    profile.videoFrameWidth = 640;
                    profile.videoFrameHeight = 480;
                    needChangeVideoSize = false;
                }
            }
//            if (needChangeVideoSize && Math.abs(targetRatio - ((double) 240 / 320)) <= 0.01) {
//                if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_QVGA)) {
//                    profile.videoFrameWidth = 640;
//                    profile.videoFrameHeight = 480;
//                }
//            }
        }

        System.out.println("zjt getCameraProfile videoSize profile = "+profile);
        if (profile == null) {
            if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
                profile = CamcorderProfile.get(mCameraId, CamcorderProfile.QUALITY_480P);
            } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
                profile = CamcorderProfile.get(mCameraId, CamcorderProfile.QUALITY_720P);
            } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_LOW)) {
                profile = CamcorderProfile.get(mCameraId, CamcorderProfile.QUALITY_LOW);
            } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_HIGH)) {
                profile = CamcorderProfile.get(mCameraId, CamcorderProfile.QUALITY_HIGH);
            } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_CIF)) {
                profile = CamcorderProfile.get(mCameraId, CamcorderProfile.QUALITY_CIF);
            }
        }
        return profile;
    }

    public boolean pause() {
        return stopRecord(false)/* != null*/;
    }

    public boolean resume() {
        return startRecord();
    }

    @Override
    public boolean stopRecord(boolean complete) {
//		Bitmap bitmap = null;
        try {
            if (mMediaRecorder != null) {
                try {
                    mMediaRecorder.setOnErrorListener(null);
                    mMediaRecorder.setOnInfoListener(null);
                    mMediaRecorder.setPreviewDisplay(null);
                    mMediaRecorder.stop();
                    mIsRecording = false;
                } catch (IllegalStateException e) {
                    SinaLog.e(TAG, "stopRecord error1: " + e.getMessage());
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    SinaLog.e(TAG, "stopRecord error2: " + e.getMessage());
                    e.printStackTrace();
                    //TODO 当发生错误的时候应当吧 该文件删掉
                    //mp4文件异常，需删除
                    if (!TextUtils.isEmpty(mRecordPath)) {
                        delFile(mRecordPath);
                    }
                    mPaths.remove(mRecordPath);
                    mRecordPath = null;
                } catch (Exception e) {
                    SinaLog.e(TAG, "stopRecord error3: " + e.getMessage());
                    e.printStackTrace();
                }
                if (complete) {
                    mMediaRecorder.reset();
                    mMediaRecorder.release();
                    mMediaRecorder = null;
                }
            }
            /*由于体验不好，暂时取消
            if (mParameters != null && mCamera != null && !complete) {
                // 重新连接相机
                mCamera.reconnect();
                // 停止预览，注意这里必须先调用停止预览再设置参数才有效
                mCamera.stopPreview();
                // 设置参数为录像前的参数，不然如果录像是低配，结束录制后预览效果还是低配画面
                mCamera.setParameters(mParameters);
                // 重新打开
                mCamera.startPreview();
                //mParameters = null;
            }
            */
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean delFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        boolean result = false;
        File file = new File(path);
        if (file.exists()) {
            result = file.delete();
        }

        return result;
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        ToastUtils.showToast(getContext(), "拍摄异常：" + what + "," + extra);
        SinaLog.e(TAG, "on error: what=" + what + ", extra=" + extra);

        try {
            if (mr != null) {
                mr.reset();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //当前视频文件异常因此删除
        if (mRecordPath != null) {
            File file = new File(mRecordPath);
            if (file.exists()) {
                file.delete();
            }
        }

        if (mPaths != null && !mPaths.isEmpty()) {
            mPaths.remove(mRecordPath);
        }
    }

    public void clear() {
        File tempFile;
        for (String tempPath : mPaths) {
            tempFile = new File(tempPath);
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
        mPaths.clear();
    }

    private Bitmap saveThumbnail() throws IOException {
        if (mRecordPath != null) {
            // 创建缩略图,该方法只能获取384X512的缩略图，舍弃，使用源码中的获取缩略图方法
            // Bitmap bitmap=ThumbnailUtils.createVideoThumbnail(mRecordPath,
            // Thumbnails.MINI_KIND);
            Bitmap bitmap = getVideoThumbnail(mRecordPath);

            if (bitmap != null) {
                String mThumbnailFolder = FileOperateUtil.getFolderPath(getContext(), FileOperateUtil.TYPE_THUMBNAIL);
                File folder = new File(mThumbnailFolder);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                File file = new File(mRecordPath);
                file = new File(folder + File.separator + file.getName().replace("3gp", "jpg"));
                // 存图片小图
                BufferedOutputStream bufferos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferos);
                bufferos.flush();
                bufferos.close();
                return bitmap;
            }
            mRecordPath = null;
        }
        return null;
    }

    /**
     * 获取帧缩略图，根据容器的高宽进行缩放
     *
     * @param filePath
     * @return
     */
    public Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(-1);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        if (bitmap == null)
            return null;
        // Scale down the bitmap if it's too large.
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.i(TAG, "bitmap:" + width + " " + height);
        int pWidth = getWidth();// 容器宽度
        int pHeight = getHeight();// 容器高度
//		Log.i(TAG, "parent:" + pWidth + " " + pHeight);
        // 获取宽高跟容器宽高相比较小的倍数，以此为标准进行缩放
        float scale = Math.min((float) width / pWidth, (float) height / pHeight);
//		Log.i(TAG, scale + "");
        int w = Math.round(scale * pWidth);
        int h = Math.round(scale * pHeight);
//		Log.i(TAG, "parent:" + w + " " + h);
        bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
        return bitmap;
    }

    /**
     * 转换前置和后置照相机
     */
    @Override
    public void switchCamera() {
        mIsFrontCamera = !mIsFrontCamera;
//        mSurfaceHolder.removeCallback(mCallback);
//        mSurfaceHolder.addCallback(mCallback);
        isOpenCamera = openCamera();
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取当前闪光灯类型
     *
     * @return
     */
    @Override
    public FlashMode getFlashMode() {
        return mFlashMode;
    }

    /**
     * 设置闪光灯类型
     *
     * @param flashMode
     */
    @Override
    public void setFlashMode(FlashMode flashMode) {
        if (mCamera == null && !isOpenCamera) {
            showDialog(TIP_ERROR_EXCEPTION);
            return;
        }

        try {
            List<String> flashModes = mParameters.getSupportedFlashModes();
            if (flashModes != null) {
                switch (flashMode) {
                    case ON:
                        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        break;
                    case AUTO:
                        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                        break;
                    case TORCH:
                        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        break;
                    default:
                        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        break;
                }
            }
            // Lock in the changes
            mCamera.setParameters(mParameters);

            mFlashMode = flashMode;
        } catch (Exception e) {
            SinaLog.e(TAG, "camera set flash mode error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Size determineBestPreviewSize(Camera.Parameters parameters) {
        List<Size> sizes = parameters.getSupportedPreviewSizes();

        float DEFAULT_RATIO1 = 720f / 480f;
        float DEFAULT_RATIO2 = 1280f / 720f;
        float desireRatio = 1f * getWidth() / getHeight();//期望分辨率比例
//        CamcorderProfile profile = getCameraProfile();
//        if (profile != null) {
//            desireRatio = (float) profile.videoFrameWidth / profile.videoFrameHeight;
//        }

        Size bestSize = null;
        int numOfSizes = sizes.size();
        for (int i = 0; i < numOfSizes; i++) {
            Size size = sizes.get(i);

            boolean isDesireRatio;
//            if (desireRatio == 0) {
//                isDesireRatio = (float) size.width / size.height == DEFAULT_RATIO1 || (float) size.width / size.height == DEFAULT_RATIO2;
//            } else {
            float tagDes = (desireRatio > 1 ? (1f * size.width / size.height) : (1f * size.height / size.width));
                isDesireRatio = tagDes == desireRatio;
//            }
            boolean isBetterSize = bestSize == null || size.width > bestSize.width;
            System.out.println("zjt (" + size.width + "," + size.height + ") , tagDes = " + tagDes + " , desireRatio = " + desireRatio + " , isDesireRatio = " + isDesireRatio +" , isBetterSize = "+isBetterSize);

            if (isDesireRatio && isBetterSize) {
                bestSize = size;
            }
        }

        if (bestSize == null) {
            Log.d(TAG, "cannot find the best camera size");
            bestSize = sizes.get(sizes.size() - 1);
        }

        return bestSize;
    }

    /**
     * 手动聚焦
     **/
    protected void onFocus(int x, int y) {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        // 不支持设置自定义聚焦，则使用自动聚焦，返回
        if (parameters.getMaxNumFocusAreas() <= 0) {
            try {
                mCamera.autoFocus(autoFocusCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        List<Area> areas = new ArrayList<Area>();
        int left = x - 300;
        int top = y - 300;
        int right = x + 300;
        int bottom = y + 300;
        left = left < -1000 ? -1000 : left;
        top = top < -1000 ? -1000 : top;
        right = right > 1000 ? 1000 : right;
        bottom = bottom > 1000 ? 1000 : bottom;
        areas.add(new Area(new Rect(left, top, right, bottom), 100));
        parameters.setFocusAreas(areas);
        try {
            // 本人使用的小米手机在设置聚焦区域的时候经常会出异常，看日志发现是框架层的字符串转int的时候出错了，
            // 目测是小米修改了框架层代码导致，在此try掉，对实际聚焦效果没影响
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mCamera.autoFocus(autoFocusCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取最大缩放级别，最大为40
     *
     * @return
     */
    @Override
    public int getMaxZoom() {
        if (mCamera == null) {
            return -1;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (!parameters.isZoomSupported())
            return -1;
        return parameters.getMaxZoom() > 40 ? 40 : parameters.getMaxZoom();
    }

    /**
     * 设置相机缩放级别
     *
     * @param zoom
     */
    @Override
    public void setZoom(int zoom) {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameters;
        // 注意此处为录像模式下的setZoom方式。在Camera.unlock之后，调用getParameters方法会引起android框架底层的异常
        // stackoverflow上看到的解释是由于多线程同时访问Camera导致的冲突，所以在此使用录像前保存的mParameters。
        if (mParameters != null)
            parameters = mParameters;
        else {
            parameters = mCamera.getParameters();
        }

        if (!parameters.isZoomSupported())
            return;
        parameters.setZoom(zoom);
        mCamera.setParameters(parameters);
        mZoom = zoom;
    }

    @Override
    public int getZoom() {
        return mZoom;
    }

    /**
     * 启动屏幕朝向改变监听函数 用于在屏幕横竖屏切换时改变保存的图片的方向
     */
    private void startOrientationChangeListener() {
        OrientationEventListener mOrEventListener = new OrientationEventListener(getContext()) {
            @Override
            public void onOrientationChanged(int rotation) {

                if (((rotation >= 0) && (rotation <= 45)) || (rotation > 315)) {
                    rotation = 0;
                } else if ((rotation > 45) && (rotation <= 135)) {
                    rotation = 90;
                } else if ((rotation > 135) && (rotation <= 225)) {
                    rotation = 180;
                } else if ((rotation > 225) && (rotation <= 315)) {
                    rotation = 270;
                } else {
                    rotation = 0;
                }
                if (rotation == mOrientation) {
                    return;
                }
                mOrientation = rotation;
                SinaLog.d("CameraView", "当前屏幕旋转角度：" + mOrientation);
//                updateCameraOrientation();
            }
        };
        mOrEventListener.enable();
    }

    /**
     * 根据当前朝向修改保存图片的旋转角度
     */
    private void updateCameraOrientation() {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            // rotation参数为 0、90、180、270。水平方向为0。
            int rotation = 90 + mOrientation == 360 ? 0 : 90 + mOrientation;
            if (mIsFrontCamera) {
                if (rotation == 90) {
                    rotation = 270;
                } else if (rotation == 270) {
                    rotation = 90;
                }
            } else {
                rotation = 90;
            }
            parameters.setRotation(rotation);// 生成的图片转90°
            mCamera.setParameters(parameters);
            // 预览图片旋转90°
            mCamera.setDisplayOrientation(90);
        }
    }

    public String getVideoPath() {
        return mRecordPath;
    }

    /**
     * @Description: 闪光灯类型枚举 默认为关闭
     */
    public enum FlashMode {
        /**
         * ON:拍照时打开闪光灯
         */
        ON,
        /**
         * OFF：不打开闪光灯
         */
        OFF,
        /**
         * AUTO：系统决定是否打开闪光灯
         */
        AUTO,
        /**
         * TORCH：一直打开闪光灯
         */
        TORCH
    }

    /**
     * Measure the view and its content to determine the measured width and the
     * measured height
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        final boolean isPortrait =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        if (isPortrait) {
            System.out.println("onMeasure  width = "+width+" , height = "+height);
            if (width > height) {
                width = height;
            } else {
//                height = width;
            }
        }

        setMeasuredDimension(width, height);
    }

    private void showDialog(String msg) {
        if (mDialog == null) {
            mDialog = new CommonDialog(getContext(), true, 0);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setClickCallbackListener(new CommonDialog.ClickCallbackListener() {
                @Override
                public void fromSure(CommonDialog dialog) {
                    dialog.dismiss();

                }

                @Override
                public void fromCancel(CommonDialog dialog) {

                }
            });
        }

        if (!TextUtils.isEmpty(msg)) {
            mDialog.setMessage(String.valueOf(msg));
        }

        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    /**
     * Iterate over supported camera preview sizes to see which one best fits the
     * dimensions of the given view while maintaining the aspect ratio. If none can,
     * be lenient with the aspect ratio.
     *
     * @param sizes Supported camera preview sizes.
     * @param w The width of the view.
     * @param h The height of the view.
     * @return Best match camera preview size to fit in the view.
     */
    public static  Camera.Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        // Use a very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = w > h ? ((double) w / h) : ((double) h / w);
        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;

        // Start with max value and refine as we iterate over available preview sizes. This is the
        // minimum difference between view and camera height.
        double minDiff = Double.MAX_VALUE;

        // Target view height
        int targetHeight = h;

        // Try to find a preview size that matches aspect ratio and the target view size.
        // Iterate over all available sizes and pick the largest size that can fit in the view and
        // still maintain the aspect ratio.
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find preview size that matches the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onFocus((int) event.getX(), (int) event.getY());
                break;
        }
        return super.onTouchEvent(event);
    }

    private final AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            // 聚焦之后根据结果修改图片
            // if (success) {
            // mFocusImageView.onFocusSuccess();
            // }else {
            // //聚焦失败显示的图片，由于未找到合适的资源，这里仍显示同一张图片
            // mFocusImageView.onFocusFailed();
            //
            // }
            System.out.println("zjt onAutoFocus = success = "+success);
            if (success) {
                camera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。
            }
        }
    };

}
