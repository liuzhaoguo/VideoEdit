package com.sina.videoedit.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @ClassName: FileOperateUtil
 * @Description:  文件操作工具类
 * @author LinJ
 * @date 2014-12-31 上午9:44:38
 *
 */
public class FileOperateUtil {
	public final static String TAG="FileOperateUtil";

	public final static int TYPE_IMAGE=1;//图片
	public final static int TYPE_THUMBNAIL=2;//缩略图
	public final static int TYPE_VIDEO=3;//视频
	//   /data/data/com.example.camera/files/Files/test/Video/video20151218142129.mp4

	/**
	 *获取文件夹路径
	 * @param type 文件夹类别
	 * @return
	 */
	public static String getFolderPath(Context context, int type) {
		//本业务文件主目录
		StringBuilder pathBuilder=new StringBuilder();
		//添加应用存储路径
		pathBuilder.append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
		pathBuilder.append(File.separator);
		pathBuilder.append("SinaBlog");
		pathBuilder.append(File.separator);
		switch (type) {
			case TYPE_IMAGE:
				pathBuilder.append("Image");
				break;
			case TYPE_VIDEO:
				pathBuilder.append("Video");
				break;
			case TYPE_THUMBNAIL:
				pathBuilder.append("Thumbnail");
				break;
			default:
				break;
		}
		String absPath = pathBuilder.toString();
		File file = new File(absPath);
		file.mkdirs();
		return absPath;
	}

	/**
	 * 获取目标文件夹内指定后缀名的文件数组,按照修改日期排序
	 * @param file 目标文件夹路径
	 * @param format
	 * @param content 包含的内容,用以查找视频缩略图
	 * @return
	 */
	public static List<File> listFiles(String file, final String format, String content){
		return listFiles(new File(file), format, content);
	}

	public static List<File> listFiles(String file, final String format){
		return listFiles(new File(file), format,null);
	}

	/**
	 * 获取目标文件夹内指定后缀名的文件数组,按照修改日期排序
	 * @param file 目标文件夹
	 * @param extension 指定后缀名
	 * @param content 包含的内容,用以查找视频缩略图
	 * @return
	 */
	public static List<File> listFiles(File file, final String extension, final String content){
		File[] files = null;
		if (file == null || !file.exists() || !file.isDirectory())
			return null;
		files = file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
				// TODO Auto-generated method stub
				if (content == null || content.equals(""))
					return arg1.endsWith(extension);
				else {
					return arg1.contains(content) && arg1.endsWith(extension);
				}
			}
		});
		if (files != null) {
			List<File> list = new ArrayList<File>(Arrays.asList(files));
			sortList(list, false);
			return list;
		}
		return null;
	}

	/**
	 *  根据修改时间为文件列表排序
	 *  @param list 排序的文件列表
	 *  @param asc  是否升序排序 true为升序 false为降序
	 */
	public static void sortList(List<File> list, final boolean asc){
		//按修改日期排序
		Collections.sort(list, new Comparator<File>() {
			public int compare(File file, File newFile) {
				if (file.lastModified() > newFile.lastModified()) {
					if (asc) {
						return 1;
					} else {
						return -1;
					}
				} else if (file.lastModified() == newFile.lastModified()) {
					return 0;
				} else {
					if (asc) {
						return -1;
					} else {
						return 1;
					}
				}

			}
		});
	}

	/**
	 *
	 * @param extension 后缀名 如".jpg"
	 * @return
	 */
	public static String createFileNmae(String extension){
		DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
		// 转换为字符串
		String formatDate = format.format(new Date());
		//查看是否带"."
		if(!extension.startsWith("."))
			extension="."+extension;
		return formatDate+extension;
	}

	/**
	 *  删除缩略图 同时删除源图或源视频
	 *  @param thumbPath 缩略图路径
	 *  @return
	 */
	public static boolean deleteThumbFile(String thumbPath, Context context) {
		boolean flag = false;

		File file = new File(thumbPath);
		if (!file.exists()) { // 文件不存在直接返回
			return flag;
		}

		flag = file.delete();
		//源文件路径
		String sourcePath=thumbPath.replace("Thumbnail", "Image");
		file = new File(sourcePath);
		if (!file.exists()) { // 文件不存在直接返回
			return flag;
		}
		flag = file.delete();
		return flag;
	}

	/**
	 *  删除源图或源视频 同时删除缩略图
	 *  @param sourcePath 缩略图路径
	 *  @return
	 */
	public static boolean deleteSourceFile(String sourcePath, Context context) {
		boolean flag = false;

		File file = new File(sourcePath);
		if (!file.exists()) { // 文件不存在直接返回
			return flag;
		}

		flag = file.delete();
		//缩略图文件路径
		String thumbPath=sourcePath.replace("Image", "Thumbnail");
		file = new File(thumbPath);
		if (!file.exists()) { // 文件不存在直接返回
			return flag;
		}
		flag = file.delete();
		return flag;
	}

	/**
	 * 复制单个文件
	 *
	 * @param oldPath String 原文件路径 如：c:/fqf.txt
	 * @param newPath String 复制后路径 如：f:/fqf.txt
	 * @return boolean true复制成功，false复制失败
	 */
	public static boolean copyFile(String oldPath, String newPath) {
		boolean isSuccess = false;

		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { //文件存在时
				InputStream inStream = new FileInputStream(oldPath); //读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[4096];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; //字节数 文件大小
//					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}

			isSuccess = true;
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		}

		return isSuccess;
	}
}
