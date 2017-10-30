package com.example.minnie.rec_web;
import java.io.File;
import android.os.Environment;

public class AudioFileFunc {
    /*錄音输出文件*/
    private final static String AUDIO_RAW_FILENAME = "RawAudio.raw";
    private final static String AUDIO_WAV_FILENAME = "FinalAudio.wav";

    /*判斷是否有SD卡*/
    public static boolean isSdcardExit(){
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /*原始音頻路徑*/
    public static String getRawFilePath(){
        String mAudioRawPath = "";
        if(isSdcardExit()){
            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mAudioRawPath = fileBasePath+"/"+AUDIO_RAW_FILENAME;
        }
        return mAudioRawPath;
    }

    /*編碼後WAV路徑*/
    public static String getWavFilePath(){
        String mAudioWavPath = "";
        if(isSdcardExit()){
            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mAudioWavPath = fileBasePath+"/"+AUDIO_WAV_FILENAME;
        }
        return mAudioWavPath;
    }

    /*文件大小*/
    public static long getFileSize(String path){
        File mFile = new File(path);
        if(!mFile.exists())
            return -1;
        return mFile.length();
    }

}