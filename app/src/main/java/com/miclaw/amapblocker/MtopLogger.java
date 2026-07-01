package com.miclaw.amapblocker;

import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * MTOP API 日志记录器
 * 
 * 功能：
 * 1. 记录所有 MTOP API 调用
 * 2. 异步写入文件避免阻塞主线程
 * 3. 支持按日期分文件
 */
public class MtopLogger {

    private static final String TAG = "AMapBlocker";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS", Locale.CHINA);
    
    private static String sDataDir;
    private static File sLogFile;
    private static final ConcurrentLinkedQueue<String> sLogQueue = new ConcurrentLinkedQueue<>();
    private static volatile boolean sRunning = false;
    private static Thread sWriterThread;

    /**
     * 初始化日志系统
     */
    public static void init(String dataDir) {
        sDataDir = dataDir;
        
        // 创建日志目录
        File logDir = new File(dataDir, "amap_blocker_logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        
        // 创建今天的日志文件
        String dateStr = DATE_FORMAT.format(new Date());
        sLogFile = new File(logDir, "mtop_log_" + dateStr + ".txt");
        
        Log.i(TAG, "日志文件: " + sLogFile.getAbsolutePath());
        
        // 启动写入线程
        startWriterThread();
    }

    /**
     * 记录 MTOP API 调用
     */
    public static void logMtopCall(String apiName, String version, String data, String response) {
        String time = TIME_FORMAT.format(new Date());
        String logEntry = String.format(
            "[%s] MTOP API: %s\n" +
            "  Version: %s\n" +
            "  Request: %s\n" +
            "  Response: %s\n" +
            "---\n",
            time, apiName, version, 
            truncate(data, 500), 
            truncate(response, 500)
        );
        
        sLogQueue.offer(logEntry);
        Log.d(TAG, "MTOP: " + apiName);
    }

    /**
     * 记录推广内容拦截
     */
    public static void logAdBlocked(String type, String detail) {
        String time = TIME_FORMAT.format(new Date());
        String logEntry = String.format(
            "[%s] AD BLOCKED: %s\n" +
            "  Detail: %s\n" +
            "---\n",
            time, type, truncate(detail, 300)
        );
        
        sLogQueue.offer(logEntry);
        Log.d(TAG, "AD BLOCKED: " + type);
    }

    /**
     * 记录首页数据加载
     */
    public static void logHomePageData(String stage, String info) {
        String time = TIME_FORMAT.format(new Date());
        String logEntry = String.format(
            "[%s] HOME PAGE: %s\n" +
            "  Info: %s\n" +
            "---\n",
            time, stage, truncate(info, 300)
        );
        
        sLogQueue.offer(logEntry);
    }

    /**
     * 记录通用信息
     */
    public static void log(String level, String message) {
        String time = TIME_FORMAT.format(new Date());
        String logEntry = String.format("[%s] %s: %s\n", time, level, message);
        sLogQueue.offer(logEntry);
    }

    /**
     * 启动写入线程
     */
    private static void startWriterThread() {
        if (sRunning) return;
        
        sRunning = true;
        sWriterThread = new Thread(() -> {
            while (sRunning) {
                try {
                    String entry = sLogQueue.poll();
                    if (entry != null) {
                        writeToFile(entry);
                    } else {
                        Thread.sleep(1000); // 没有日志时休眠 1 秒
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    Log.e(TAG, "写入日志失败", e);
                }
            }
        }, "AMapBlocker-Logger");
        sWriterThread.setDaemon(true);
        sWriterThread.start();
    }

    /**
     * 写入文件
     */
    private static synchronized void writeToFile(String content) {
        if (sLogFile == null) return;
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sLogFile, true))) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            Log.e(TAG, "写入日志文件失败", e);
        }
    }

    /**
     * 截断字符串
     */
    private static String truncate(String s, int maxLen) {
        if (s == null) return "null";
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen) + "...[" + s.length() + " chars]";
    }

    /**
     * 停止日志系统
     */
    public static void stop() {
        sRunning = false;
        if (sWriterThread != null) {
            sWriterThread.interrupt();
        }
    }
}
