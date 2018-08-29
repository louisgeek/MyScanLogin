package com.louisgeek.myscanloginserver.socket;

import android.content.Context;
import android.util.Log;

import com.louisgeek.myscanloginserver.tool.ThreadTool;
import com.louisgeek.myscanloginserver.tool.UIToast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by louisgeek on 2018/8/22.
 */
public class TcpServer {

    private static final String TAG = "TcpServer";
    Socket socket;
    Context mContext;
    ExecutorService mExecutorService = Executors.newFixedThreadPool(3);

    public TcpServer(final Context context, final ServerSocket serverSocket) {
        mContext = context;
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "startServer: ");
                UIToast.show(mContext, "startServer");
                try {
                    while (true) {
                        //阻塞
                        socket = serverSocket.accept();
//                        UIToast.show(mContext, "有客户端连接到本机:" + socket.getLocalPort());
                        Log.e(TAG, "run: " + "有客户端连接到本机:" + socket.getLocalPort());
                        //
                        mExecutorService.execute(new ReceiveRunnable());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    UIToast.show(mContext, e.getMessage());
                }
            }
        });
    }

    private List<OnMsgReceiveListener> mOnMsgReceiveListeners = new ArrayList<>();

    public void addOnMsgReceiveListener(OnMsgReceiveListener onMsgReceiveListener) {
        mOnMsgReceiveListeners.add(onMsgReceiveListener);
    }

    public interface OnMsgReceiveListener {
        void onMsgReceive(String info);
    }


    public class ReceiveRunnable implements Runnable {

        @Override
        public void run() {
            Log.e(TAG, "ReceiveRunnable run: ");
            //
            InputStream is = null;
            InputStreamReader isr = null;
            BufferedReader br = null;
            try {
                if (socket.isConnected() && !socket.isClosed()) {
                    // 接受客户端数据
                    is = socket.getInputStream();
                    isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);
                    // 读取数据
                    String info = null;
                    Log.e(TAG, "ReceiveRunnable 读取数据 ");
                    while ((info = br.readLine()) != null) {
                        Log.e(TAG, "ReceiveRunnable readLine ");
                        //
                        final String finalInfo = info;
                        ThreadTool.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mOnMsgReceiveListeners != null && !mOnMsgReceiveListeners.isEmpty()) {
                                    for (OnMsgReceiveListener onMsgReceiveListener : mOnMsgReceiveListeners) {
                                        onMsgReceiveListener.onMsgReceive(finalInfo);
                                    }
                                }
                            }
                        });
                        System.out.println("ReceiveRunnable 接受数据******" + info);
                    }
                    br.close();
                    //
                } else {
                    // 链接已关闭
                    Log.e(TAG, "ReceiveRunnable run: 链接已关闭");
                    UIToast.show(mContext, "链接已关闭");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "ReceiveRunnable " + e.getMessage());
                UIToast.show(mContext, e.getMessage());
            }

        }
    }
}
