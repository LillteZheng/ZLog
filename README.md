# ZLog
快速压缩日志工具到U盘，支持压缩其他文件到指定路径

```
repositories {
        ...
        maven { url 'https://jitpack.io' }
}
```

```
implementation 'com.github.LillteZheng:ZLog:v1.3'
```

```
String netLog = "/sdcard/tem.txt";
ZLog.with(this)
        .logPaths(Arrays.asList(netLog))
        .zipPath("/sdcard/")
        .zipName("传屏No.1.zip")
        .isHasSystemPermission(true)
        .listener(new ZLogListener() {
            @Override
            void onStart() {
                super.onStart();
                Log.d(TAG, "zsr onStart: ");
            }

            @Override
            void onSuccess(String path) {
                Log.d(TAG, "zsr onSuccess: "+path);
            }

            @Override
            void onFail(ZipError errorCode, String errorMsg) {
                Log.d(TAG, "zsr onFail: "+errorCode+" "+errorMsg);
            }
        }).zip();

```
