package com.example.dungit.gallery.presentation.Utils;

import java.io.File;
import java.text.DecimalFormat;

public class FileUtils {
    static public boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return( path.delete() );
    }

    private static String[] METRICS = {" B"," KB"," MB"," GB"};
    private static DecimalFormat NUMBER_FMT = new DecimalFormat("#.##");
    static public String calculateSize(long size){
        double size_ = size;
        int count = 0;
        while (size_ >= 1024 && count < METRICS.length){
            size_/=1024.0;
            count++;
        }
        return NUMBER_FMT.format(size_) + METRICS[count];
    }
}
