package com.louisgeek.myscanloginserver;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.louisgeek.myscanloginserver.nsd.NSDServer;
import com.louisgeek.myscanloginserver.socket.TcpServer;
import com.louisgeek.myscanloginserver.tool.QRCodeTool;
import com.louisgeek.myscanloginserver.tool.UUIDTool;

import java.io.IOException;
import java.net.ServerSocket;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Context mContext;
    NSDServer mNSDServer;
    int mPort;
    ServerSocket mServerSocket;

    TextView id_tv;
    TextView id_tv_log;
    ImageView qrcode;
    TcpServer tcpServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;


        //
        id_tv = findViewById(R.id.id_tv);
        id_tv_log = findViewById(R.id.id_tv_log);
        qrcode = findViewById(R.id.qrcode);
        qrcode.setVisibility(View.GONE);
        //
        mNSDServer = new NSDServer(this);
        mNSDServer.init();
        mNSDServer.setOnNsdServiceInfoStateListener(new NSDServer.OnNsdServiceInfoStateListener() {
            @Override
            public void onServiceLost(NsdServiceInfo nsdServiceInfo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //
                        Toast.makeText(mContext, "服务丢失", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onServiceResolved(final NsdServiceInfo nsdServiceInfo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //
                        id_tv.setText(nsdServiceInfo.getServiceName()
                                + "服务已启动："
                                + nsdServiceInfo.getHost().getHostAddress() + ":"
                                + nsdServiceInfo.getPort());
//                        Log.e(TAG, "服务启动成功 run: " + mNetNSDManager.getNsdServiceInfo().toString());


                    }
                });
            }

            @Override
            public void onShowLog(final String log) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        id_tv_log.setText(id_tv_log.getText() + "\n" + log);
                    }
                });
            }
        });

        try {
            //auto set port
            mServerSocket = new ServerSocket(0);
            mPort = mServerSocket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mNSDServer.registerService(mPort);
        //
        tcpServer = new TcpServer(mContext, mServerSocket);
        tcpServer.addOnMsgReceiveListener(new TcpServer.OnMsgReceiveListener() {
            @Override
            public void onMsgReceive(String info) {
                if ("to_login".equals(info)) {
                    Toast.makeText(mContext, "客户端请求登录", Toast.LENGTH_SHORT).show();
                    String str = "test_qr_code_uuid:" + UUIDTool.getUUID();
                    QRCodeTool.builder(str).
                            backColor(0xFFFFFFFF).
                            codeColor(0xFF000000).
                            codeSide(600).
                            into(qrcode);
                    qrcode.setVisibility(View.VISIBLE);
                } else if ("scan_success".equals(info)) {
                    Toast.makeText(mContext, "扫码成功，等待客户端确认", Toast.LENGTH_LONG).show();
                    qrcode.setVisibility(View.GONE);
                } else if ("login_loading".equals(info)) {
                    Toast.makeText(mContext, "登录中...", Toast.LENGTH_LONG).show();
                } else if ("login_success".equals(info)) {
                    Toast.makeText(mContext, "登录成功", Toast.LENGTH_LONG).show();
                } else if("relogin".equals(info)){
                    Toast.makeText(mContext, "客户端需要重新扫码...", Toast.LENGTH_SHORT).show();
                    String str = "test_qr_code_uuid:" + UUIDTool.getUUID();
                    QRCodeTool.builder(str).
                            backColor(0xFFFFFFFF).
                            codeColor(0xFF000000).
                            codeSide(600).
                            into(qrcode);
                    qrcode.setVisibility(View.VISIBLE);
                }else {
                    Toast.makeText(mContext, "收到客户端信息:" + info, Toast.LENGTH_LONG).show();
                }

            }

        });

        MySingleton.getInstance().setTcpServer(tcpServer);
    }


    @Override
    protected void onPause() {
//        mNSDServer.stopServiceDiscovery();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
      /*  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        id_tv_log.setText("start_" + simpleDateFormat.format(new Date()));
        mNSDServer.discoverServices();*/
    }

    @Override
    protected void onDestroy() {
        mNSDServer.unregisterService();
        super.onDestroy();

    }
}
