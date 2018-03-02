package com.sina.videoedit.video;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by liuqun1 on 2015/12/16.
 */
public class VideoItem implements Parcelable, Serializable {
    public int id;
    public String title;//文件名 不带后缀
    public String album;
    public String artist;
    public String displayName;//文件名 带后缀
    public String mimeType;
    public String path;     //视频地址
//    public String newPath; // getExternalCacheDir()+"/" + System.currentTimeMillis()+".mp4"; 然后赋值给path
    public String fileUrl; // 视频网络地址，对应 MediaTable表中 fileUrl 字段
    public long size;
    public long duration;
    public String thumbnail;    // thumbnailPath
    public String mediaId;      //MediaTable表中 mediaId, 并且和html中video标签的 id属性值一样.
    public int width;           //缩略图的宽
    public int height;          //缩略图的高

    private long data_added;//视频创建时间,文件被添加到媒体
    private long data_modified;//视频修改时间
    private String resolution;
    private String description;
    private float latitude;//拍摄视频的纬度
    private float longitude;//拍摄视频的经度
    private long datetaken;//视频拍摄日期和时间
    private String bucket_display_name;//视频所在目录名称
    private int video_width;
    private int video_height;

    private boolean isHeader;
    private String formatDate;
    private boolean selectedAll;

    public VideoItem(){
        super();
        long time = System.currentTimeMillis();
        this.id = (int) (time / 1000);
        this.mediaId = String.valueOf(time);
    }

    public VideoItem(int id, String title, String album, String artist,
                     String displayName, String mimeType, String path, long size,
                     long duration) {
        super();
        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.displayName = displayName;
        this.mimeType = mimeType;
        this.path = path;
        this.size = size;
        this.duration = duration;
        this.mediaId = String.valueOf(System.currentTimeMillis() / 1000);
    }

    protected VideoItem(Parcel in) {
        id = in.readInt();
        title = in.readString();
        album = in.readString();
        artist = in.readString();
        displayName = in.readString();
        mimeType = in.readString();
        path = in.readString();
        fileUrl = in.readString();
        size = in.readLong();
        duration = in.readLong();
        thumbnail = in.readString();
        mediaId = in.readString();
        width = in.readInt();
        height = in.readInt();
    }

    @Override
    public String toString() {
        return "VideoItem{" +
                "album='" + album + '\'' +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", displayName='" + displayName + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", path='" + path + '\'' +
                ", size=" + size +
                ", duration=" + duration +
                ", thumbnail='" + thumbnail + '\'' +
                ", mediaId='" + mediaId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VideoItem item = (VideoItem) o;

        if (id != item.id) return false;
        if (size != item.size) return false;
        if (duration != item.duration) return false;
        if (width != item.width) return false;
        if (height != item.height) return false;
        if (!title.equals(item.title)) return false;
        if (album != null ? !album.equals(item.album) : item.album != null) return false;
        if (artist != null ? !artist.equals(item.artist) : item.artist != null) return false;
        if (!displayName.equals(item.displayName)) return false;
        if (mimeType != null ? !mimeType.equals(item.mimeType) : item.mimeType != null) return false;
        if (!path.equals(item.path)) return false;
//        if (newPath != null ? !newPath.equals(item.newPath) : item.newPath != null) return false;
        if (fileUrl != null ? !fileUrl.equals(item.fileUrl) : item.fileUrl != null) return false;
        if (thumbnail != null ? !thumbnail.equals(item.thumbnail) : item.thumbnail != null) return false;
        return mediaId.equals(item.mediaId);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + title.hashCode();
        result = 31 * result + (album != null ? album.hashCode() : 0);
        result = 31 * result + (artist != null ? artist.hashCode() : 0);
        result = 31 * result + displayName.hashCode();
        result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
        result = 31 * result + path.hashCode();
//        result = 31 * result + (newPath != null ? newPath.hashCode() : 0);
        result = 31 * result + (fileUrl != null ? fileUrl.hashCode() : 0);
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        result = 31 * result + (thumbnail != null ? thumbnail.hashCode() : 0);
        result = 31 * result + mediaId.hashCode();
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(album);
        dest.writeString(artist);
        dest.writeString(displayName);
        dest.writeString(mimeType);
        dest.writeString(path);
        dest.writeString(fileUrl);
        dest.writeString(thumbnail);
        dest.writeString(mediaId);
        dest.writeLong(size);
        dest.writeLong(duration);
        dest.writeInt(width);
        dest.writeInt(height);
    }

    public static final Creator<VideoItem> CREATOR = new Creator<VideoItem>() {
        @Override
        public VideoItem createFromParcel(Parcel in) {
            return new VideoItem(in);
        }

        @Override
        public VideoItem[] newArray(int size) {
            return new VideoItem[size];
        }
    };


    public long getData_added() {
        return data_added;
    }

    public void setData_added(long data_added) {
        this.data_added = data_added;
    }

    public long getData_modified() {
        return data_modified;
    }

    public void setData_modified(long data_modified) {
        this.data_modified = data_modified;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public long getDatetaken() {
        return datetaken;
    }

    public void setDatetaken(long datetaken) {
        this.datetaken = datetaken;
    }

    public String getBucket_display_name() {
        return bucket_display_name;
    }

    public void setBucket_display_name(String bucket_display_name) {
        this.bucket_display_name = bucket_display_name;
    }

    public int getVideo_width() {
        return video_width;
    }

    public void setVideo_width(int video_width) {
        this.video_width = video_width;
    }

    public int getVideo_height() {
        return video_height;
    }

    public void setVideo_height(int video_height) {
        this.video_height = video_height;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setIsHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }

    public String getFormatDate() {
        return formatDate;
    }

    public void setFormatDate(String formatDate) {
        this.formatDate = formatDate;
    }

    public boolean isSelectedAll() {
        return selectedAll;
    }

    public void setSelectedAll(boolean selectedAll) {
        this.selectedAll = selectedAll;
    }
}
