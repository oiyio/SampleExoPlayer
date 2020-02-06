package com.example.aliazaz.exoplayer_sample.ui;

import android.os.Environment;

import java.io.File;

public class MyUtil {

    /**
     * returns path of the video files saved in device
     * */
    public static String getVideoFilesPathStoredInDevice(){
        return Environment.getExternalStorageDirectory() + File.separator + "VideoFiles";
    }

}
