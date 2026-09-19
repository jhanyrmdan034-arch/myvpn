package com.qkvpn.vpn;


import android.app.Application;


public class MyApplication extends Application  {

    @Override
    public void onCreate() {
        super.onCreate();

    }

    static
    {
        System.loadLibrary("opvpnutil");

    }
}