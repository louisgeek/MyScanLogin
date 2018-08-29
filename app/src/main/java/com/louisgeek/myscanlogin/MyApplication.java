package com.louisgeek.myscanlogin;

import android.app.Application;

import com.louisgeek.myscanlogin.socket.TcpClient;

/**
 * Created by louisgeek on 2018/8/24.
 */
public class MyApplication extends Application {
    private static MyApplication sInstance;
    private TcpClient mTcpClient;

    public void setTcpClient(TcpClient tcpClient) {
        mTcpClient = tcpClient;
    }

    public TcpClient getTcpClient() {
        return mTcpClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    /**
     * 通过Android底层实现关闭当前进程
     */
    public static void killProcess() {
        int pid = android.os.Process.myPid();
        if (pid != 0) {
            System.exit(0);
            android.os.Process.killProcess(pid);
        }
    }

}
