package com.example.dungit.gallery;

import ly.img.android.PESDK;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        PESDK.init(this,"LICENSE");
    }
}
