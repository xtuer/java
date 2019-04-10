import com.alibaba.fastjson.JSON;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

public class FFmpegX {
    static FFmpeg ffmpeg;

    static {
        try {
            ffmpeg = new FFmpeg("/usr/local/Cellar/ffmpeg/4.1/bin/ffmpeg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static FFprobe ffprobe;

    static {
        try {
            ffprobe = new FFprobe("/usr/local/Cellar/ffmpeg/4.1/bin/ffprobe");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void probe() throws IOException {
        String path = "/Users/Biao/Documents/workspace/new-ebag-web/ebag-web-app/src/test/resources/videos/test.swf";
        FFmpegProbeResult in = ffprobe.probe(path);

        for (FFmpegStream stream : in.getStreams()) {
            System.out.println(JSON.toJSONString(stream));
            if (stream.codec_type == FFmpegStream.CodecType.VIDEO) {
                System.out.println(stream.duration);
            }
        }
    }

    public static void extractCover(File mp4) throws IOException {
        File cover = new File(mp4.getParent(), String.format("cover-%s.png", FilenameUtils.getBaseName(mp4.getName())));

        // [2] 设置从视频中提取第 5 秒的画面作为封面
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(mp4.getAbsolutePath())
                .overrideOutputFiles(true)
                .addOutput(cover.getAbsolutePath())
                .setFrames(1)
                .setVideoFilter("select='gte(t\\,5)'") // 第 5 秒的画面
                .done();


        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        executor.createJob(builder).run();
    }

    public static void main(String[] args) throws IOException {
        probe();
        extractCover(new File("/Users/Biao/Desktop/238855614619451392.mp4"));
        extractCover(new File("/Users/Biao/Desktop/Tears in Heaven.mp4"));
        extractCover(new File("/Users/Biao/Desktop/Tears in Heaven.mp4"));
        extractCover(new File("/Users/Biao/Desktop/Tears in Heaven.mp4"));
        extractCover(new File("/Users/Biao/Desktop/Tears in Heaven.mp4"));
        extractCover(new File("/Users/Biao/Desktop/Tears in Heaven.mp4"));
        extractCover(new File("/Users/Biao/Desktop/Tears in Heaven.mp4"));
        extractCover(new File("/Users/Biao/Desktop/Tears in Heaven.mp4"));
    }
}
