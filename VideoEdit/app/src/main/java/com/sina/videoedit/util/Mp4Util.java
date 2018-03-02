package com.sina.videoedit.util;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * mp4 操作类
 * 本类的方法都属于耗时操作
 *
 * https://github.com/tqnst/MP4ParserMergeAudioVideo
 * http://m.blog.csdn.net/blog/ab6326795/42390525
 * @author justin
 */
public class Mp4Util {

	/**
	 * mp4，音频合并
	 * @param sources 可以是mp4路径 或者音频路径
	 * @param target 输出路劲
	 * @return
	 */
	public static boolean append(List<String> sources, String target) {
		try {
			List<Track> videoTracks = new LinkedList<Track>();
			List<Track> audioTracks = new LinkedList<Track>();
			Movie m = null;
			for (String s : sources) {
				//读取视频
				File file = new File(s);
				if (!file.exists() || file.length() <= 0) {
					continue;
				}
				m = MovieCreator.build(s);
				for (Track t : m.getTracks()) {
					if (t.getHandler().equals("soun")) {
						audioTracks.add(t);
					}
					if (t.getHandler().equals("vide")) {
						videoTracks.add(t);
					}
				}
			}
			Movie result = new Movie();
			if (audioTracks.size() > 0) {
				result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
			}
			if (videoTracks.size() > 0) {
				result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
			}

			// 输出
			Container out = new DefaultMp4Builder().build(result);
			String outputFilePath = target;
			FileOutputStream fos = new FileOutputStream(new File(outputFilePath));
			out.writeContainer(fos.getChannel());
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 视频裁剪
	 * @param source
	 * @param target
	 * @return
	 */
	public static boolean crop(String source, String target) {
		try {
			//读取原始视频
			String filePath = source;
			Movie originalMovie = MovieCreator.build(filePath);

			// 分割
			Track track = originalMovie.getTracks().get(0);
			Movie movie = new Movie();
			movie.addTrack(new AppendTrack(new CroppedTrack(track, 200, 400)));

			// 输出
			Container out = new DefaultMp4Builder().build(movie);
			String outputFilePath = target;
			FileOutputStream fos = new FileOutputStream(new File(outputFilePath));
			out.writeContainer(fos.getChannel());
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
