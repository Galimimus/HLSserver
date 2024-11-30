package galimimus.org.hlsserver.helpers;

import galimimus.org.hlsserver.HelloApplication;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

import java.io.File;
import java.io.IOException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ProcessMP4 {
    static final Logger log = Logger.getLogger(HelloApplication.class.getName());
    static final String PATH_TO_FFMPEG = "/bin/ffmpeg";

    private static boolean createDir(String dirName){
        boolean mkdir_res = new File(dirName).mkdir();
        if(!mkdir_res){
            log.logp(Level.SEVERE, "ProcessMP4", "createDir", "Could not create new directory \"" + dirName + "\", directory already exist.");
            return false ;
        }
        return true;
    }

    private static boolean createM3U8File(String fileName){
        File file = new File(fileName);
        try {
            boolean file_res = file.createNewFile();
            if(!file_res) {
                log.logp(Level.SEVERE, "ProcessMP4", "createM3U8File", "Could not create new M3U8 file \"" + fileName + "\", file already exist.");
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private static boolean createTsFiles(String fileMP4, String resolutionPath){
        createDir(resolutionPath);
        //String PATH_TO_FFMPEG = "ffmpeg";
        //exec('ffmpeg -i abc.mp4 -c:v libx264 -c:a aac -b:a 160k -bsf:v h264_mp4toannexb -f mpegts -crf 32 pqr.ts');
        //ffmpeg -y -i sample.avi -ss 00:00:10 -to 00:00:20 -vcodec libx264 -acodec aac -vf scale=426:-1 -muxdelay 10 out1.ts

        //ffmpeg -y -i /home/galimimus/IdeaProjects/HLSserver/video1.mp4 -c:v libx264 -c:a aac -bsf:v h264_mp4toannexb
        // -bsf:a aac_adtstoasc -map 0 -f segment -segment_time 10 -segment_format mpegts
        // -segment_list /home/galimimus/IdeaProjects/HLSserver/server/video1/240/video.m3u8 -segment_list_type m3u8
        // /home/galimimus/IdeaProjects/HLSserver/server/video1/240/video%03d.ts
        ProcessBuilder pb = new ProcessBuilder(
          PATH_TO_FFMPEG, "-y",
          "-i", fileMP4,
          "-c:v", "libx264",
          "-c:a", "aac",
          "-bsf:v", "h264_mp4toannexb",
          "-bsf:a", "aac_adtstoasc",
          "-map", "0",
          "-f", "segment",
          "-segment_time", "10",
          "-segment_format", "mpegts",
          "-segment_list", resolutionPath + "/video.m3u8",
          "-segment_list_type", "m3u8",
          resolutionPath + "/%04d.ts"
        );
        /*ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-y",
                "-i", fileMP4,
                "-c:v", "libx264",
                "-c:a", "aac",
                "-bsf", "h264_mp4toannexb",
                "-map", "0",
                "-f", "segment",
                "-segment-time", "10",
                "-segment-list", resolutionPath + "/video.m3u8",
                "-segment_list_type", "m3u8",
                resolutionPath + "/part%03d.ts"
        );
        /*ffmpeg -y \
 -i sample.mov \
 -codec copy \
 -bsf h264_mp4toannexb \
 -map 0 \
 -f segment \
 -segment_time 10 \
 -segment_format mpegts \
 -segment_list "/Library/WebServer/Documents/vod/prog_index.m3u8" \
 -segment_list_type m3u8 \
 "/Library/WebServer/Documents/vod/fileSequence%d.ts"*/

        Process process = null;
        try {
            process = pb.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        /*
        FFmpeg ffmpeg = null;
        try {
            ffmpeg = new FFmpeg(PATH_TO_FFMPEG); // Путь к исполнимому файлу ffmpeg
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (ffmpeg==null) return false;

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(fileMP4) // Исходное видео
                .addOutput(String.format(resolutionPath + "/%04d.ts")) // Формат имени выходных файлов
                .setFormat("mpegts") // Формат TS
                .setVideoCodec("libx264") // Видео кодек (H.264)
                .setAudioCodec("aac") // Аудио кодек (AAC)
                .setVideoBitRate(1500_000) // Устанавливаем битрейт видео
                .setAudioBitRate(128_000)
                //.set// Устанавливаем битрейт аудио
                //.setSegmentTime(10) // Разделение на сегменты по 10 секунд
                //.setHlsPlaylist(true)
                //.setOutput// Включаем создание HLS плейлиста
                //.addExtraArgs("-hls_time", "10")
                //.addExtraArgs("-hls_list_size", "0")
                //.addExtraArgs("-hls_segment_type", "mpegts")
                .addExtraArgs("-f", "mpegts")
                //.addOutput(resolutionPath+"playlist.m3u8")
                .done(); // Создаем команду

        // Запуск конвертации
        try {
            ffmpeg.run(builder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        return true;
    }



    public static void processMP4(File mp4_file){
        //TODO: Создается папка с именем файла, неэффиктивно, нужно добавить id, делая папку с подобным именем.
        // В пост запросе помимо имен передавать названия файлов
        // (мб сделать заранее подготовленный json и обновлять его только при добавлении нового видео файла.)

        String baseFileName = mp4_file.getName().replaceFirst("[.][^.]+$", "");
        String newFileDirName = "/home/galimimus/IdeaProjects/HLSserver/server/" + baseFileName;
        if (!createDir(newFileDirName)) return;

        createTsFiles(mp4_file.getAbsolutePath(), newFileDirName+"/240");


/*
        boolean create_m3u8_res = createPlaylistM3U8(newFileDirName, mp4_file);
        if(!create_m3u8_res){
            log.logp(Level.SEVERE, "ProcessMP4", "processMP4", "Could not create new playlist file. Sharing file failed. Exiting.");
            return;
        }
*/
    }


}
