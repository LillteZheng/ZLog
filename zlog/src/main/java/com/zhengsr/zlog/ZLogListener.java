package com.zhengsr.zlog;

/**
 * @author by zhengshaorui 2021/6/7 16:08
 * describe：
 */
public abstract class ZLogListener {
    void onStart() { }

    /**
     * 安装成功，你应该使用命令行 sync 去同步，避免拔插U盘后没数据
     * 可以使用 com.jaredrummler:android-shell:1.0.0
     */
    abstract void onSuccess(String path);

    abstract void onFail(ZipError errorCode,String errorMsg);
}
