package com.zhengsr.zlog;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author by zhengshaorui 2021/6/7 15:57
 * describe：
 */
public class ZipRequest {
    private ExecutorService executors = Executors.newSingleThreadExecutor();
    //墓碑文件
    private static final String TOMBSTONES_PATH = "${Environment.getDataDirectory()}/tombstones";
    //anr文件
    private static final String ANR_PATH = "${Environment.getDataDirectory()}/anr";

    private ZipRequest(Builder builder) {
        try {

            if ((builder.logPaths == null || builder.logPaths.isEmpty())
                    && (builder.logFiles == null || builder.logFiles.isEmpty())) {
                if (builder.listener != null) {
                    builder.listener.onFail(ZipError.NO_LOG_FILE,"无log路径或文件，请输入 logPath 或 logFiles");
                }
                return;
            }


            String usbPath = ZipUtil.getUsbPath(builder.context);

            if (TextUtils.isEmpty(builder.zipPath) && TextUtils.isEmpty(usbPath)) {
                if (builder.listener != null) {
                    builder.listener.onFail(ZipError.NO_OUT_PATH,"找不到U盘或没有输出路径");
                }
                return;
            }

            String path = !TextUtils.isEmpty(builder.zipPath) ? builder.zipPath : usbPath;

            File dir = new File(path);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            zipTask(builder,path);

        } catch (Exception e) {
            e.printStackTrace();
            if (builder.listener != null) {
                builder.listener.onFail(ZipError.UNKNOWN,e.toString());
            }
        }
    }

    private void zipTask(Builder builder,String path) {
        executors.execute(() -> {
            ZLogListener listener = builder.listener;
            try {
                if (builder.listener != null) {
                    builder.listener.onStart();
                }
                String name = !TextUtils.isEmpty(builder.zipName) ? builder.zipName
                        : builder.context.getPackageName() + ".zip";
                File outFile = new File(path,name);
                if (outFile.exists()) {
                    outFile.delete();
                }
                List<File> logFiles = getLogFiles(builder);
                if (logFiles.isEmpty()) {
                    if (listener != null) {
                        listener.onFail(ZipError.NO_LOG_FILE,"没有需要压缩的文件");
                    }
                }
                ZipUtil.zipFolder(logFiles,outFile.getAbsolutePath());
                if (outFile.length() <= 0){
                    if (listener != null) {
                        listener.onFail(ZipError.ZIP_FILE_EMPTY,"压缩失败,"+name+" 大小为0");
                    }
                }else{
                    if (listener != null) {
                        listener.onSuccess(outFile.getAbsolutePath());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.onFail(ZipError.UNKNOWN,"zipTask fail: "+e.toString());
                }
            }
        });
    }

    private List<File> getLogFiles(Builder builder){
        List<File> files = new ArrayList<>();
        if (builder.isHasSystemPermission){
            File anrFile = new File(ANR_PATH);
            if (anrFile.exists()) {
                files.add(anrFile);
            }
            File tomsFile = new File(TOMBSTONES_PATH);
            if (tomsFile.exists()) {
                files.add(tomsFile);
            }
            if (builder.logPaths != null) {
                for (String logPath : builder.logPaths) {
                    File file = new File(logPath);
                    if (file.exists()) {
                        files.add(file);
                    }
                }
            }
            if (builder.logFiles != null) {
                for (File logFile : builder.logFiles) {
                    if (logFile.exists()) {
                        files.add(logFile);
                    }
                }
            }
        }
        return files;
    }

    public static class Builder {
        private Context context;
        private List<String> logPaths = new ArrayList<>();
        private List<File> logFiles;
        private ZLogListener listener;
        private boolean isHasSystemPermission;
        private String zipPath;
        private String zipName;

        public Builder (Context Context) {
            this.context = Context;
        }
        /**
         * 需要一起压缩的log路径
         */
        public Builder logPath(String path) {
            if (logPaths == null) {
                logPaths = new ArrayList<>();
            }
            logPaths.add(path);
            return this;
        }
        /**
         * 需要一起压缩的log路径
         */
        public Builder logPaths(List<String> paths) {
            if (logPaths == null) {
                logPaths = new ArrayList<>();
            }
            logPaths.addAll(paths);
            return this;
        }
        /**
         * 需要一起压缩的log文件,可以是其他log的文件
         */
        public Builder logFile(File file) {
            if (logFiles == null) {
                logFiles = new ArrayList<>();
            }
            logFiles.add(file);
            return this;
        }
        /**
         * 需要一起压缩的log文件,可以是其他log的文件
         */
        public Builder logFiles(List<File> files) {
            if (logFiles == null) {
                logFiles = new ArrayList<File>();
            }
            this.logFiles.addAll(files);
            return this;
        }
        /**
         * 是否是系统应用，默认false，是则拷贝anr和墓碑文件
         */
        public Builder isHasSystemPermission(boolean isHasSystemPermission) {
            this.isHasSystemPermission = isHasSystemPermission;
            return this;
        }
        /**
         * 需要解压的路径
         * @param zipPath 默认放到U盘下
         */
        public Builder zipPath(String zipPath) {
            this.zipPath = zipPath;
            return this;
        }
        /**
         * 压缩的名字
         * @param zipName 默认包名.zip
         */
        public Builder zipName(String zipName) {
            this.zipName = zipName;
            return this;
        }

        public Builder listener(ZLogListener listener) {
            this.listener = listener;
            return this;
        }

        public ZipRequest zip() {
            return new ZipRequest(this);
        }
    }

}
