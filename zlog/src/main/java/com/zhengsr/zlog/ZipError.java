package com.zhengsr.zlog;

/**
 * @author by zhengshaorui 2021/6/7 16:54
 * describe：
 */
public enum ZipError {
    /**
     * 未找到log文件
     */
    NO_LOG_FILE,
    /**
     * 没有输出路径
     */
    NO_OUT_PATH,
    /**
     * 压缩失败
     */
    ZIP_FILE_EMPTY,
    UNKNOWN
}
