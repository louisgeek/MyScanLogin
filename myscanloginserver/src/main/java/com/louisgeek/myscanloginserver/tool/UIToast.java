package com.louisgeek.myscanloginserver.tool;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by louisgeek on 2018/8/24.
 */
public class UIToast {
    public static void show(final Context context, final String text) {
        ThreadTool.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
