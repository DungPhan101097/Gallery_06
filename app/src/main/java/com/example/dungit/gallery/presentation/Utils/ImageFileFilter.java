package com.example.dungit.gallery.presentation.Utils;

import java.io.File;
import java.io.FileFilter;

public class ImageFileFilter implements FileFilter {
    private final String[] imgFileExtensions = new String[]{"jpg", "jpeg", "png", "gif", "ico"};

    @Override
    public boolean accept(File file) {
        for (String extension : imgFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
