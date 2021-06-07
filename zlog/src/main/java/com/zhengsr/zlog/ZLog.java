package com.zhengsr.zlog;

import android.content.Context;

/**
 * @author by zhengshaorui 2021/6/7 15:56
 * describe：
 */
public class ZLog {
    public static ZipRequest.Builder with(Context context){
        return new ZipRequest.Builder(context.getApplicationContext());
    }
}
