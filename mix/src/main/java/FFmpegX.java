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
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FFmpegX {
    static FFmpeg  ffmpeg;
    static FFprobe ffprobe;

    static {
        try {
            ffmpeg  = new FFmpeg("/usr/local/Cellar/ffmpeg/4.1/bin/ffmpeg");
            ffprobe = new FFprobe("/usr/local/Cellar/ffmpeg/4.1/bin/ffprobe");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 输出视频信息
     * @throws IOException
     */
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
     * 提取视频中传入的 time 处的画面
     *
     * @param video 视频文件
     * @throws IOException
     */
    public static void extractImage(File video, File image, int time) throws IOException {
        FFmpeg  ffmpeg  = new FFmpeg("/usr/local/Cellar/ffmpeg/4.1/bin/ffmpeg");
        FFprobe ffprobe = new FFprobe("/usr/local/Cellar/ffmpeg/4.1/bin/ffprobe");
        FFmpegBuilder builder = new FFmpegBuilder()
                .overrideOutputFiles(true)
                .setInput(video.getAbsolutePath())
                .addOutput(image.getAbsolutePath())
                .setFrames(1)
                .setVideoFilter(String.format("select='gte(t\\,%d)'", time)) // 第 time 秒处的画面
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        executor.createJob(builder).run();
    }

    /**
     * 转换任意格式的视频为 mp4 格式的视频
     *
     * @param src  源视频文件
     * @param dest 保存 mp4 视频的文件
     * @throws IOException
     */
    public static void convertVideoToMp4(File src, File dest) throws IOException {
        FFmpeg  ffmpeg  = new FFmpeg("/usr/local/Cellar/ffmpeg/4.1/bin/ffmpeg");
        FFprobe ffprobe = new FFprobe("/usr/local/Cellar/ffmpeg/4.1/bin/ffprobe");
        FFmpegProbeResult in = ffprobe.probe(src.getAbsolutePath());

        FFmpegBuilder builder = new FFmpegBuilder()
                .overrideOutputFiles(true) // Override the output if it exists
                .setInput(in)
                .addOutput(dest.getAbsolutePath())
                .setFormat("mp4")                  // Format is inferred from filename, or can be set
                .setVideoCodec("libx264")          // Video using x264
                .setVideoFrameRate(24, 1)          // At 24 frames per second
                // .setVideoResolution(width, height) // At 1280x720 resolution (宽高必须都能被 2 整除)
                .setAudioCodec("aac")              // Using the aac codec
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs (ex. aac)
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
                log.debug("[{}%] status: {}, frame: {}, time: {} ms, fps: {}, speed: {}x",
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

    /**
     * 转换任意格式的音频为 mp3 格式的音频
     *
     * @param src  源音频文件
     * @param dest 保存 mp3 音频的文件
     * @throws IOException
     */
    public static void convertAudioToMp3(File src, File dest) throws IOException {
        FFmpeg  ffmpeg  = new FFmpeg("/usr/local/Cellar/ffmpeg/4.1/bin/ffmpeg");
        FFprobe ffprobe = new FFprobe("/usr/local/Cellar/ffmpeg/4.1/bin/ffprobe");
        FFmpegProbeResult in = ffprobe.probe(src.getAbsolutePath());

        FFmpegBuilder builder = new FFmpegBuilder()
                .overrideOutputFiles(true)
                .setInput(src.getAbsolutePath())
                .addOutput(dest.getAbsolutePath())
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        FFmpegJob job = executor.createJob(builder, new ProgressListener() {
            // 使用 FFmpegProbeResult 得到视频的长度 (单位为纳秒)
            final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

            @Override
            public void progress(Progress progress) {
                // 转换进度 [0, 100]
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
        // convertVideoToMp4(new File("/Users/Biao/Desktop/test.mkv"), new File("/Users/Biao/Desktop/test.mp4"));
        // convertAudioToMp3(new File("/Users/Biao/Desktop/cx.mp3"), new File("/Users/Biao/Desktop/cx-x.mp3"));
        // extractImage(new File("/Users/Biao/Desktop/test.mkv"), new File("/Users/Biao/Desktop/ano.png"), 8);

        ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-y", "-i" , "test.avi", "-vcodec", "h264" , "test.mp4");
        pb.directory(new File("/Users/Biao/Desktop")); // pb 的工作目录，设置为 test.avi 所在目录
        pb.redirectError(new File("/Users/Biao/Desktop/error.txt"));
        pb.redirectOutput(new File("/Users/Biao/Desktop/log.txt"));
        pb.start();
    }
}
