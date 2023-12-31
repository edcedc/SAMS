package com.yyc.smas.util;

import android.util.Log;

import com.blankj.utilcode.util.LogUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author nike
 * @Date 2023/6/14 13:51
 * @Description
 */
public class FileUtils {

    private static String filePath = "/sdcard/NIke测试日记/";

    // 将字符串写入到文本文件中
    public static void writeTxtToFile(String strcontent) {
        String fileName = getFileName() + ".txt";
        // 生成文件夹
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + "/" + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        BufferedWriter bufferedWriter = null;
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file: " + strFilePath);
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file, true);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(strContent);
            bufferedWriter.flush();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File: " + e);
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    Log.e("TestFile", "Error on close BufferedWriter: " + e);
                }
            }
        }
    }

    // 生成文件
    private static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(e);
        }
        return file;
    }

    // 生成文件夹
    private static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    //删除指定txt文件   通过路径
    public void deleteFile(String filePath, String fileName) {
        File f = new File(filePath + fileName);  // 输入要删除的文件位置
        if (f.exists()) {
            f.delete();
        }

    }

    /**
     * 确定文件名的函数，通过时间来产生不同的名字
     * @return
     */
    public static String getFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(new Date(System.currentTimeMillis()));
        return date;
    }
}
