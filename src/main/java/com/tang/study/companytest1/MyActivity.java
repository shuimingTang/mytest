package com.tang.study.companytest1;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Tangshuiming99 on 2016/4/28.
 */
public class MyActivity extends Activity{
    private static int index;
    private int count = 5;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

//            Toast.makeText(MyActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
            return true;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        Button button =
                (Button)findViewById(R.id.button5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true){
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                            List<ActivityManager.RunningTaskInfo> list = manager.getRunningTasks(1);
                            if(list.size() > 0){
                                String name = list.get(0).topActivity.getPackageName();

                                Log.e("Cat", "启动应用的包名：" + name);
//                                handler.obtainMessage(1, name).sendToTarget();
                            }
                            /*Log.e("Cat", "before...");
                            if(count != -1){

                                if(index == 5){
                                    continue;
                                }
                                ++index;
                            }
                            Log.e("Cat", "index :" + index);
                            Log.e("Cat", "sssssssss");*/
                        }
                    }
                }).start();

            }
        });
    }
}
