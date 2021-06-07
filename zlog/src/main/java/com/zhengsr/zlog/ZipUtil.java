package com.zhengsr.zlog;

import android.content.Context;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class ZipUtil {

    /**
     * Compress file and folder
     *
     * @param files       file  to be Compress
     * @param zipFilePath the path name of result ZIP
     * @throws Exception
     */
    public static void zipFolder(List<File> files, String zipFilePath) throws Exception {
        ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFilePath));
        for (File file : files) {
            //compress
            zipFiles(file.getParent() + File.separator, file.getName(), outZip);
        }
        outZip.finish();
        outZip.close();


    }

    /**
     * 复制文件
     *
     * @param srcFile
     * @param destFile
     * @return
     */
    public static void copyToFile(File srcFile, File destFile) throws Exception {

        FileInputStream inputStream = new FileInputStream(srcFile);
        if (destFile.exists()) {
            destFile.delete();
        }
        FileOutputStream e = new FileOutputStream(destFile);
        try {
            byte[] bytes = new byte[4096];

            int bytesRead;
            while ((bytesRead = inputStream.read(bytes)) >= 0) {
                e.write(bytes, 0, bytesRead);
            }
        } finally {
            e.flush();
            e.close();
        }
    }

    /**
     * compress files
     *
     * @param folderString
     * @param fileString
     * @param zipOutputSteam
     * @throws Exception
     */
    private static void zipFiles(String folderString, String fileString, ZipOutputStream zipOutputSteam) throws Exception {
        if (zipOutputSteam == null)
            return;
        File file = new File(folderString + fileString);
        if (file.isFile()) {
            ZipEntry zipEntry = new ZipEntry(fileString);
            FileInputStream inputStream = new FileInputStream(file);
            zipOutputSteam.putNextEntry(zipEntry);
            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }
            zipOutputSteam.closeEntry();
        } else {
            //folder
            String fileList[] = file.list();
            //no child file and compress  
            if (fileList.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            }
            //child files and recursion  
            for (int i = 0; i < fileList.length; i++) {
                zipFiles(folderString, fileString + File.separator + fileList[i], zipOutputSteam);
            }//end of for  
        }
    }

    /**
     * 获取usb路径
     * @param context
     * @return
     * @throws Exception
     */
    public static String getUsbPath(Context context)throws Exception {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        Class storeManagerClazz = Class.forName("android.os.storage.StorageManager");
        Method getVolumesMethod = storeManagerClazz.getMethod("getVolumes");
        List<?> volumeInfos  = (List<?>)getVolumesMethod.invoke(storageManager);
        Class volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");
        Field udiskPath = volumeInfoClazz.getDeclaredField("internalPath");
        Method getFsUuidMethod = volumeInfoClazz.getMethod("getFsUuid");

        if(volumeInfos != null){
            for(Object volumeInfo:volumeInfos){
                String uuid = (String)getFsUuidMethod.invoke(volumeInfo);
                if(uuid != null){
                    //只获取第一个
                    String sUdiskPath = (String)udiskPath.get(volumeInfo);  // 结果是 /mnt/media_rw/28BA-794A
                    Log.d("TAG", "zsr getUsbPath: "+sUdiskPath);
                    return sUdiskPath;
                }
            }
        }
        return null;

    }


}