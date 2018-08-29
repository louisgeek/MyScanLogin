package com.louisgeek.myscanloginserver;

import com.louisgeek.myscanloginserver.socket.TcpServer;

/**
 * Created by louisgeek on 2018/8/28.
 */

public class MySingleton {
    public static MySingleton getInstance() {
        return Inner.INSTANCE;
    }

    private static class Inner {
        private static final MySingleton INSTANCE = new MySingleton();
    }

    private TcpServer mTcpServer;
    public TcpServer getTcpServer() {
        return mTcpServer;
    }
    public void setTcpServer(TcpServer tcpServer) {
        mTcpServer = tcpServer;
    }
}
