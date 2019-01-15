import com.alibaba.fastjson.JSON;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

import java.io.IOException;

public class FFmpeg {
    public static void main(String[] args) throws IOException {
        String path = "/Users/Biao/Documents/workspace/new-ebag-web/ebag-web-app/src/test/resources/videos/test.swf";
        FFprobe ffprobe = new FFprobe("/usr/local/Cellar/ffmpeg/4.1/bin/ffprobe");
        FFmpegProbeResult in = ffprobe.probe(path);

        for (FFmpegStream stream : in.getStreams()) {
            System.out.println(JSON.toJSONString(stream));
            if (stream.codec_type == FFmpegStream.CodecType.VIDEO) {
                System.out.println(stream.duration);
            }
        }
    }
}
