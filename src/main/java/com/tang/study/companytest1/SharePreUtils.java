package com.tang.study.companytest1;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Tangshuiming99 on 2016/4/26.
 */
public class SharePreUtils {

    private SharePreUtils(){

    }
    public static SharedPreferences.Editor getEditor(Context context, String saveFile){
        SharedPreferences sharedPreferences = context.getSharedPreferences(saveFile, Context.MODE_PRIVATE);

        return  sharedPreferences.edit();
    }
}
