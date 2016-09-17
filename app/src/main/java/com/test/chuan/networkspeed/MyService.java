package com.test.chuan.networkspeed;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class MyService extends Service {
    TextView tx;
    WindowManager windowManager;
    WindowManager.LayoutParams params;
    boolean isAdded;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0&&isAdded&&tx!=null){
                StringBuilder s = (StringBuilder) msg.obj;
                tx.setText(s);
            }
        }
    };

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createWindowsView();
        return super.onStartCommand(intent, flags, startId);
    }

    public void createWindowsView(){
        tx = new TextView(getApplicationContext());
        tx.setText("0.0\n0.0");
        tx.setTextColor(Color.BLUE);
//        tx.setBackgroundColor(Color.WHITE);
        windowManager = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        // 设置悬浮框不可触摸
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应
        params.format = PixelFormat.RGBA_8888;
        // 设置悬浮框的宽高
        params.width = 100;
        params.height = 100;
//        params.gravity = Gravity.RIGHT;
        int[] ints = MyApplication.getScreenHW(this);
        params.x = ints[0]/2-50;
        params.y = ints[1]/4;
        tx.setOnTouchListener(new View.OnTouchListener() {
            //保存悬浮框最后位置的变量
            int lastX, lastY;
            int paramX, paramY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        paramX = params.x;
                        paramY = params.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        params.x = paramX + dx;
                        params.y = paramY + dy;
                        // 更新悬浮窗位置
                        windowManager.updateViewLayout(tx, params);
                        break;
                }
                return true;
            }
        });
        windowManager.addView(tx, params);
        isAdded = true;
        getNetworkSpeed();
    }
    public void getNetworkSpeed(){
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long down = TrafficStats.getTotalRxBytes();
                    long up = TrafficStats.getTotalTxBytes();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    double speedDown = (TrafficStats.getTotalRxBytes() - down) / 3/1024;
                    double speedUp = (TrafficStats.getTotalTxBytes() - up) / 3/1024;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(speedDown).append("\n").append(speedUp);
                    Message message = new Message();
                    message.what = 0;
                    message.obj = stringBuilder;
                    handler.sendMessage(message);
                }
            }
        });
        thread.start();
    }

    @Override
    public void onDestroy() {
        windowManager.removeView(tx);
        isAdded = false;
        super.onDestroy();
    }

}
