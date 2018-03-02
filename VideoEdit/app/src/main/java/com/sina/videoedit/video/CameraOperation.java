package com.sina.videoedit.video;

/**
 * @ClassName: CameraOperation
 * @Description:相机操作接口，用以统一CameraContainer和CameraView的功能
 * @author LinJ
 * @date 2015-1-26 下午2:41:31
 *
 */
public interface CameraOperation {
	/**
	 *  开始录像
	 *  @return  是否成功开始录像
	 */
	boolean startRecord();

	/**
	 *  停止录像
	 *  @return 录像缩略图
	 */
	boolean stopRecord(boolean complete);
	/**
	 *   切换前置和后置相机
	 */
	void switchCamera();
	/**
	 *  获取当前闪光灯模式
	 *  @return
	 */
	CameraView.FlashMode getFlashMode();
	/**
	 *  设置闪光灯模式
	 *  @param flashMode
	 */
	void setFlashMode(CameraView.FlashMode flashMode);
	/**
	 *  相机最大缩放级别
	 *  @return
	 */
	int getMaxZoom();
	/**
	 *  设置当前缩放级别
	 *  @param zoom
	 */
	void setZoom(int zoom);
	/**
	 *  获取当前缩放级别
	 *  @return
	 */
	int getZoom();
}
