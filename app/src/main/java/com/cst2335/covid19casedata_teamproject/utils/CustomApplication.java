package com.cst2335.covid19casedata_teamproject.utils;

import android.app.Application;

import com.yariksoffice.lingver.Lingver;

public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Lingver.init(this, "en");
    }
}
