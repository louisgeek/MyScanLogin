package com.louisgeek.myscanlogin.socket;

import android.content.Context;
import android.util.Log;

import com.louisgeek.myscanlogin.tool.UIToast;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by louisgeek on 2018/8/22.
 */
public class TcpClient {
    private static final String TAG = "TcpClient";
    Socket socket;
    Context mContext;
     ExecutorService mExecutorService = Executors.newFixedThreadPool(3);

    public TcpClient(Context context, final String ip, final int port) {
        mContext = context;

        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ip, port);
                    if (mOnConnectedListener != null) {
                        mOnConnectedListener.onConnected();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    UIToast.show(mContext, e.getMessage());
                }
            }
        });
    }

    private OnConnectedListener mOnConnectedListener;

    public void setOnConnectedListener(OnConnectedListener onConnectedListener) {
        mOnConnectedListener = onConnectedListener;
    }

    public interface OnConnectedListener {
        void onConnected();
    }

    public class SendRunnable implements Runnable {
        private String content;

        public SendRunnable(String content) {
            this.content = content;
        }

        @Override
        public void run() {
            OutputStream os = null;
            PrintWriter pw = null;
            try {
                if (socket.isConnected() && !socket.isClosed()) {
                    // 获取当前Socket输出流，输出数据
                    os = socket.getOutputStream();
                    pw = new PrintWriter(os);
//                    outputStream.write((mEdit.getText().toString()+"\n").getBytes("utf-8"));
                    pw.write(content + "\n");
                    pw.flush();
//                    socket.shutdownOutput();
//                    System.out.println("转发数据**********" + out);
                } else {
                    // 链接已关闭
//                    ChatManager.getInstance().remove(this);
                    Log.e(TAG, "run: 链接已关闭");
                    UIToast.show(mContext, "链接已关闭");
                }
            } catch (Exception e) {
                e.printStackTrace();
                UIToast.show(mContext, e.getMessage());
            }
        }
    }

    public void send(String text) {
        mExecutorService.execute(new SendRunnable(text));
    }
}
