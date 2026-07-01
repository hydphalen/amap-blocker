package com.miclaw.amapblocker;

import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 高德地图去推广 LSPosed 模块入口
 * 
 * 功能：
 * 1. 拦截 MTOP 网关请求，记录所有 API 调用
 * 2. 过滤推广/广告内容
 * 3. 记录首页数据加载过程
 */
public class HookEntry implements IXposedHookLoadPackage {

    private static final String TAG = "AMapBlocker";
    private static final String TARGET_PACKAGE = "com.autonavi.minimap";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals(TARGET_PACKAGE)) {
            return;
        }

        Log.i(TAG, "=== 高德地图去推广模块已加载 ===");
        Log.i(TAG, "目标包名: " + lpparam.packageName);
        Log.i(TAG, "ClassLoader: " + lpparam.classLoader.getClass().getName());

        // 初始化日志
        MtopLogger.init(lpparam.appInfo.dataDir);

        // Hook 各个模块
        try {
            MtopHook.hook(lpparam.classLoader);
            Log.i(TAG, "MTOP Hook 成功");
        } catch (Throwable t) {
            Log.e(TAG, "MTOP Hook 失败", t);
        }

        try {
            ContentHook.hook(lpparam.classLoader);
            Log.i(TAG, "Content Hook 成功");
        } catch (Throwable t) {
            Log.e(TAG, "Content Hook 失败", t);
        }

        try {
            AdFilterHook.hook(lpparam.classLoader);
            Log.i(TAG, "AdFilter Hook 成功");
        } catch (Throwable t) {
            Log.e(TAG, "AdFilter Hook 失败", t);
        }

        Log.i(TAG, "=== 所有 Hook 已完成 ===");
    }
}
