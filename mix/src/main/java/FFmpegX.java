import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
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

    /**
     * 提取 MP4 视频的封面
     *
     * @param mp4 MP4 文件
     * @throws IOException
     */
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

    /**
     * 音频文件转换为 mp3
     *
     * @param audio 音频文件
     * @throws IOException
     */
    public static void convertMp3(File audio) throws IOException {
        File mp3 = new File(audio.getParent(), "out.mp3");
        FFmpegProbeResult in = ffprobe.probe(audio.getAbsolutePath());

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(audio.getAbsolutePath())
                .overrideOutputFiles(true)
                .addOutput(mp3.getAbsolutePath())
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        FFmpegJob job = executor.createJob(builder, new ProgressListener() {
            // 使用 FFmpegProbeResult 得到视频的长度 (单位为纳秒)
            final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

            @Override
            public void progress(Progress progress) {
                // 转换进度 [0, 100]
                // [Fix] No duration for FLV, SWF file, 所以获取进度无效时都假装转换到了 99%
                int percentage = (duration_ns > 0) ? (int)(progress.out_time_ns / duration_ns * 100) : 99;

                // 日志中输出转换进度信息
                log.info("[{}%] status: {}, frame: {}, time: {} ms, fps: {}, speed: {}x",
                        percentage,
                        progress.status,
                        progress.frame,
                        FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
                        progress.fps.doubleValue(),
                        progress.speed
                );
            }
        });
        job.run();
    }

    public static void main(String[] args) throws IOException {
        // probe();
        // extractCover(new File("/Users/Biao/Desktop/238855614619451392.mp4"));
        // extractCover(new File("/Users/Biao/Desktop/Tears in Heaven.mp4"));
        // extractCover(new File("/Users/Biao/Desktop/Tears in Heaven.mp4"));
        // extractCover(new File("/Users/Biao/Desktop/Tears in Heaven.mp4"));
        // extractCover(new File("/Users/Biao/Desktop/Tears in Heaven.mp4"));
        // extractCover(new File("/Users/Biao/Desktop/Tears in Heaven.mp4"));
        // extractCover(new File("/Users/Biao/Desktop/Tears in Heaven.mp4"));
        // extractCover(new File("/Users/Biao/Desktop/Tears in Heaven.mp4"));
        convertMp3(new File("/Users/Biao/Desktop/error-cx.mp3"));
    }
}
