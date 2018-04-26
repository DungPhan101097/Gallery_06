package com.example.dungit.gallery.presentation.Utils;

import java.io.File;
import java.io.FileFilter;

public class EmptyFolderFileFilter implements FileFilter {
    @Override
    public boolean accept(File file) {
        return file.isDirectory() && file.list().length <=0;
    }
}
