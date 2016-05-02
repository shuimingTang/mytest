package com.tang.study.companytest1;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by Tangshuiming99 on 2016/4/25.
 */
public class BrowsableUtil {

    private BrowsableUtil(){

    }

    /**
     * 检测是不是浏览器方法
     * */
    private static boolean isBrowsable(Context context, String packName){

        List<String> packNameList = getBrowserName(context);
        if(packNameList != null && packNameList.contains(packName)){
            return true;
        }

        return false;
    }
    public static List<String> getBrowserName(Context context){

        //浏览器意图
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
//        intent.addCategory("android.intent.category.BROWSABLE");
        intent.setDataAndType(Uri.parse("http://"), null);
        //通过PackageManager，根据浏览器意图，查询所有浏览器
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities
                (intent, PackageManager.GET_ACTIVITIES);
        if(resolveInfoList.size() == 0){
            return null;
        }
        List<String> packNameList = new ArrayList<String>();
        for(ResolveInfo info : resolveInfoList){
            packNameList.add(info.activityInfo.packageName);//包名
        }
        return packNameList;
    }

    public static List<String> getLaucherPro(Context context){
        ActivityManager activityManager =
                (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = activityManager.getRunningAppProcesses();
        if(infos.size() == 0){
            return null;
        }
        List<String> processes = new ArrayList<String>();
//        String[] names = new String[infos.size()];
        LinkedList<String> linkedList = new LinkedList<>();
        for(ActivityManager.RunningAppProcessInfo info : infos){
            if(isBrowsable(context, info.processName)){
                String name = info.processName;
                if(!processes.contains(name)){
                    processes.add(name);
//                    Log.e("Cat", "检测到正在运行浏览器：" + info.processName);
                }
                /*if(!linkedList.contains(name)){
                    linkedList.add(name);
                    Log.e("Cat", "检测到正在运行浏览器：" + info.processName);
                }*/

            }
        }
        return processes;
    }
    public static Set<String> getLaucherPro2(Context context){
        ActivityManager activityManager =
                (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = activityManager.getRunningAppProcesses();
        if(infos.size() == 0){
            return null;
        }
        Set<String> processes = new HashSet<String>();
        for(ActivityManager.RunningAppProcessInfo info : infos){
            if(isBrowsable(context, info.processName)){
                String name = info.processName;
                    processes.add(name);
//                    Log.e("Cat", "检测到正在运行浏览器：" + info.processName);

            }
        }
        return processes;
    }
    public static LinkedList<String> getLaucherPro3(Context context){
        ActivityManager activityManager =
                (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = activityManager.getRunningAppProcesses();
        if(infos.size() == 0){
            return null;
        }
        LinkedList<String> processes = new LinkedList<>();
        for(ActivityManager.RunningAppProcessInfo info : infos){
            if(isBrowsable(context, info.processName)){
                String name = info.processName;
                if(!processes.contains(name)){
                    processes.add(name);
                }
//                Log.e("Cat", "检测到正在运行浏览器：" + info.processName);

            }
        }
        return processes;
    }
    /***/
    public boolean isContains(String pakName, String[] pakNames){
        for(String n : pakNames){
            if(pakName.equals(n)){
                return true;
            }
        }
        return false;
    }
    /**
     * 通过ActivityManager，检测最新启动的浏览器进程
     * */
    public static String  isLaucherPro(Context context){
        //获取所有正在运行的进程
        ActivityManager activityManager =
                (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = activityManager.getRunningAppProcesses();
        if(infos.size() == 0){
            return "";
        }
        String proName;
        for(ActivityManager.RunningAppProcessInfo info : infos){
            //判断是否是浏览器进程
            proName = info.processName;
            if(isBrowsable(context, proName)){
//                Log.e("info", "检测到的启动进程:" + proName);
                return proName;//只要检测到有浏览器启动，立刻返回
            }
        }
        return "";
    }

}
