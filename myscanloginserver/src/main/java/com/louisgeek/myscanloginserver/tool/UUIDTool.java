package com.louisgeek.myscanloginserver.tool;

import java.util.UUID;

/**
 * Created by louisgeek on 2018/8/24.
 */
public class UUIDTool {

    public static String getRawUUID() {
        return UUID.randomUUID().toString();
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
