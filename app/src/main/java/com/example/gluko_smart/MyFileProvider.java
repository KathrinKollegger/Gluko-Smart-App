package com.example.gluko_smart;

import androidx.core.content.FileProvider;


public class MyFileProvider extends FileProvider {

    public MyFileProvider() {
    super(R.xml.file_paths);
    }


}
