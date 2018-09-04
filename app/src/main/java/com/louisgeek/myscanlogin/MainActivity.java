package com.louisgeek.myscanlogin;

import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.louisgeek.myscanlogin.adpter.NSDAdapter;
import com.louisgeek.myscanlogin.nsd.NSDClient;
import com.louisgeek.myscanlogin.nsd.NSDInfo;
import com.louisgeek.myscanlogin.socket.TcpClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Context mContext;
    NSDClient mNSDClient;
    ListView id_lv;
    TextView id_tv_log;
    Button id_tv_btn;
    NSDAdapter mNSDAdapter;
    TcpClient tcpClient;
//    IpManager mIpManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        setTitle("先点击列表中设备，再使用扫码");

        id_lv = findViewById(R.id.id_lv);
        id_tv_log = findViewById(R.id.id_tv_log);
        id_tv_btn = findViewById(R.id.id_tv_btn);
        id_tv_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ScanActivity.class);
                startActivity(intent);
            }
        });

        mNSDAdapter = new NSDAdapter();
        id_lv.setAdapter(mNSDAdapter);
        id_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final NSDInfo nsdInfo = mNSDAdapter.getItem(position);
                //
                final TcpClient tcpClient = new TcpClient(mContext, nsdInfo.ip, nsdInfo.port);
                tcpClient.setOnConnectedListener(new TcpClient.OnConnectedListener() {
                    @Override
                    public void onConnected() {
                        MyApplication.getInstance().setTcpClient(tcpClient);
                        MyApplication.getInstance().getTcpClient().send("to_login");
                    }
                });
                //
            }
        });

        mNSDClient = new NSDClient(this);
        mNSDClient.init();
        mNSDClient.setOnNsdServiceInfoStateListener(new NSDClient.OnNsdServiceInfoStateListener() {

            @Override
            public void onStateChange(final Map<String, NsdServiceInfo> nsdServiceInfoMap) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (nsdServiceInfoMap == null) {
                            Toast.makeText(mContext, "扫描没有结果", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        List<NSDInfo> nsdInfoList = new ArrayList<>();
                        for (String key : nsdServiceInfoMap.keySet()) {
                            NsdServiceInfo nsdServiceInfo = nsdServiceInfoMap.get(key);
                            NSDInfo nsdInfo = new NSDInfo();
                            nsdInfo.name = nsdServiceInfo.getServiceName();
                            nsdInfo.ip = nsdServiceInfo.getHost().getHostAddress();
                            nsdInfo.port = nsdServiceInfo.getPort();
                            nsdInfoList.add(nsdInfo);
                        }
                        mNSDAdapter.refreshData(nsdInfoList);
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
    }

    private static final String TAG = "MainActivity";


    @Override
    protected void onPause() {
        mNSDClient.stopServiceDiscovery();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        id_tv_log.setText("start_" + simpleDateFormat.format(new Date()));
        mNSDClient.discoverServices();
    }


}
