package com.sina.videoedit.video;

//import com.sina.sinablog.BlogApplication;

import com.sina.videoedit.util.SinaLog;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by liuqun1 on 2015/12/22.
 */
public class VideoAlbumEngine {
    public static final String TAG = "VideoAlbumEngine";

    /** 发视频的最大数 */
    public static final int DEFAULT_MAX_SELECT_VIDEO = 5;

    /** 过滤大于200M的视频 */
    public static final long DEFAULT_VIDEO_SIZE_FILTER = 200 * 1024 * 1024;

    private static ArrayList<VideoItem> mSelectVideos = new ArrayList<VideoItem>();

    public static boolean add(VideoItem item) {
        File file = new File(item.path);
        if (!file.exists()) {
            //文件不存在
            SinaLog.e(TAG, "文件不存在：" + file.getAbsolutePath());
//            ToastUtils.showToast(BlogApplication.getContext(), "视频文件不存在:" + file.getName());
        } else if (file.length() <= 0) {
            //视频文件为空，可能是权限造成的(摄像权限允许、录音权限拒绝)
            SinaLog.e(TAG, "文件异常（可能是权限造成）：" + file.getAbsolutePath());
//            ToastUtils.showToast(BlogApplication.getContext(), "视频文件异常");

            //暂时先删除
            file.delete();
        } else {
            mSelectVideos.add(item);
            return true;
        }
        return false;
    }

    public static void remove(VideoItem item) {
        mSelectVideos.remove(item);
    }

    public static void removeItem(String videoLocalPath) {
        if (mSelectVideos != null) {
            for (int i = 0; i < mSelectVideos.size(); i++) {
                VideoItem item = mSelectVideos.get(i);
                if (item != null && item.path.equals(videoLocalPath)) {
                    mSelectVideos.remove(i);
                    break;
                }
            }
        }
    }

    public static ArrayList<VideoItem> getVideos(){
        return mSelectVideos;
    }

    public static void clear(){
        if (mSelectVideos != null){
            mSelectVideos.clear();
        }
    }

}
