package galimimus.org.hlsserver.helpers;

import com.google.gson.Gson;
import galimimus.org.hlsserver.HelloApplication;
import galimimus.org.hlsserver.models.FoldersPaths;
import galimimus.org.hlsserver.models.Video;

import java.io.File;
import java.io.IOException;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import static galimimus.org.hlsserver.helpers.Helper.readFromResourceStream;


public class ProcessMP4 {
    static final Logger log = Logger.getLogger(HelloApplication.class.getName());
    static Gson g = new Gson();
    static FoldersPaths settings_paths = g.fromJson(readFromResourceStream(Paths.get("settings_paths.json")), FoldersPaths.class);

    static Video[] settings_videos = g.fromJson(readFromResourceStream(Paths.get("videos.json")), Video[].class);


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

    private static void createTsFiles(String fileMP4, String resolutionPath, String seg_time){
        createDir(resolutionPath);
        //String PATH_TO_FFMPEG = "ffmpeg";

        ProcessBuilder pb = new ProcessBuilder(
                settings_paths.SETTINGS_FFMPEG_PATH, "-y",
          "-i", fileMP4,
          "-c:v", "libx264",
          "-c:a", "aac",
          "-bsf:v", "h264_mp4toannexb",
          "-bsf:a", "aac_adtstoasc",
          "-map", "0",
          "-f", "segment",
          //"-t", seg_time,
          "-segment_time", seg_time, //Обрезается только на keyframes(переход от кадра к кадру)
          "-segment_format", "mpegts",
          "-segment_list", resolutionPath + "/video.m3u8",
          "-segment_list_type", "m3u8",
          resolutionPath + "/%04d.ts"
        );

        Process process = null;
        try {
            process = pb.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



    public static void processMP4(File mp4_file, String ts_len){

        String baseFileName = mp4_file.getName().replaceFirst("[.][^.]+$", "");
        Video newVideo = new Video();
        newVideo.SETTINGS_VIDEO_FOLDER = Integer.toHexString(mp4_file.hashCode());
        newVideo.SETTINGS_VIDEO_NAME = baseFileName;
        String newFileDirName = settings_paths.SETTINGS_SERVER_PATH + "/" + newVideo.SETTINGS_VIDEO_FOLDER;
        if (!createDir(newFileDirName)){
            log.logp(Level.SEVERE, "ProcessMP4", "ProcessMP4", "Could not create new directory \"" + newFileDirName + "\", directory already exist.");
            return;
        }
        // TODO: НУЖНО ЧТО-ТО СДЕЛАТЬ С РАЗРЕШЕНИЕМ. ПОПРОБОВАТЬ ПЕРЕГОНЯТЬ ЧЕРЕЗ FFMPEG.
        createTsFiles(mp4_file.getAbsolutePath(), newFileDirName+"/240", ts_len);
        ArrayList<Video> tmpList;
        if (settings_videos != null) {
            tmpList = new ArrayList(Arrays.asList(settings_videos));
        }else {
            tmpList = new ArrayList<Video>();
        }
        if(!tmpList.contains(newVideo)) {
            tmpList.add(newVideo);


            try {

                FileOutputStream os = new FileOutputStream(String.valueOf(Paths.get("videos.json")));
                byte[] tmp = g.toJson(tmpList.toArray()).getBytes();
                os.write(tmp);
                os.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }


}
