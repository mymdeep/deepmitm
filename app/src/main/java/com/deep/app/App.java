package com.deep.app;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import deep.com.deepproxy.log.Logger;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import util.FileUtil;
import util.MyQueue;

/**
 * Created by wangfei on 2018/1/10.
 */

public class App extends MultiDexApplication {
    public static int proxyPort = 8888;
    public static Boolean isInitProxy = false;
    public BrowserMobProxy proxy;
    @Override
    public void onCreate() {
        super.onCreate();
        initProxy();

    }
    public void initProxy() {
        try {
            FileUtil.forceMkdir(new File(Environment.getExternalStorageDirectory() + "/har"));

        } catch (IOException e) {
            // test.har文件不存在
        }
        MyQueue.runInBack(new Runnable() {
            @Override
            public void run() {
                startProxy();

                Intent intent = new Intent();
                intent.setAction("proxyfinished");
                sendBroadcast(intent);
            }
        });

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        MyQueue.runInBack(new Runnable() {
            @Override
            public void run() {
                Log.e("~~~","onTerminate");
                proxy.stop();
            }
        });

    }

    public void startProxy(){
        try {
            proxy = new BrowserMobProxyServer();
            proxy.setTrustAllServers(true);
            proxy.start(8888);
        } catch (Exception e) {
            // 防止8888已被占用
            Random rand = new Random();
            int randNum = rand.nextInt(1000) + 8000;
            proxyPort = randNum;
            Logger.single(0,"port="+proxyPort);
            proxy = new BrowserMobProxyServer();
            proxy.setTrustAllServers(true);
            proxy.start(randNum);
        }



        proxy.enableHarCaptureTypes(
            net.lightbody.bmp.proxy.CaptureType.REQUEST_HEADERS, net.lightbody.bmp.proxy.CaptureType.REQUEST_COOKIES,
            net.lightbody.bmp.proxy.CaptureType.REQUEST_CONTENT, net.lightbody.bmp.proxy.CaptureType
                .RESPONSE_HEADERS, net.lightbody.bmp.proxy.CaptureType.REQUEST_COOKIES,
            net.lightbody.bmp.proxy.CaptureType.RESPONSE_CONTENT);

        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
            .format(new Date(System.currentTimeMillis()));
        proxy.newHar(time);


        isInitProxy = true;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    public void stopProxy(){
        if(proxy!=null){
            proxy.stop();
        }
    }
}
