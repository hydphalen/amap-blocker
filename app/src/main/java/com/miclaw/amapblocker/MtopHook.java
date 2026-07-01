package com.miclaw.amapblocker;

import android.util.Log;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * MTOP 网关 Hook — 精准拦截推广 API
 *
 * 策略：
 * 1. 所有包含 recommend / relationrecommend 的 API → 全量记录请求+响应
 * 2. 命中屏蔽列表的 API → 直接伪造空响应，不发网络请求
 * 3. 其他 API → 不碰
 */
public class MtopHook {

    private static final String TAG = "AMapBlocker";

    // ═══════════════════════════════════════════════════════════════
    //  需要记录的 API 关键词（包含这些字符串的 API 名都会被记录）
    // ═══════════════════════════════════════════════════════════════
    private static final String[] LOG_KEYWORDS = {
        "recommend",
        "relationrecommend",
        "recommendstream",
        "listRecommendMiniApps",
    };

    // ═══════════════════════════════════════════════════════════════
    //  需要屏蔽的 API（命中即拦截，返回空 JSON）
    //  先跑日志确认具体字符串后再往里加
    // ═══════════════════════════════════════════════════════════════
        private static final Set<String> BLOCKED_APIS = new HashSet<>(Arrays.asList(
        // 小程序推荐
        "mtop.relationrecommend.mtoprecommend.listRecommendMiniApps"
        // 以下先留占位，等日志跑出来再补具体 API 名
        // "mtop.xxx.xxx.xxx"
    ));

    // ═══════════════════════════════════════════════════════════════
    //  需要屏蔽的 API 关键词（模糊匹配，命中即拦截）
    //  先跑日志确认后再往里加
    // ═══════════════════════════════════════════════════════════════
    private static final String[] BLOCK_KEYWORDS = {
        // 金融/钱包/车主服务相关 —— 等日志确认后补充具体关键词
        // "wallet",
        // "finance",
        // "carservice",
    };

    // ═══════════════════════════════════════════════════════════════
    //  伪造的空响应
    // ═══════════════════════════════════════════════════════════════
    private static final String FAKE_EMPTY_RESPONSE = "{}";

    /**
     * 入口
     */
    public static void hook(ClassLoader classLoader) {
        Log.i(TAG, "开始 Hook MTOP 网关...");

        hookMtopRequest(classLoader);

        Log.i(TAG, "MTOP 网关 Hook 完成");
    }

    // ═══════════════════════════════════════════════════════════════
    //  核心 Hook：拦截 MtopRequest 的发送流程
    // ═══════════════════════════════════════════════════════════════

    private static void hookMtopRequest(ClassLoader classLoader) {
        try {
            // MtopRequest 是请求载体，持有 apiName / version / data 等字段
            Class<?> mtopRequestClass = XposedHelpers.findClass(
                "mtopsdk.mtop.domain.MtopRequest", classLoader);

            // ── Hook 1: 拦截 MtopRequest 构造 / setApiName ──
            // 在 apiName 被设置的那一刻就记录下来
            hookSetApiName(mtopRequestClass);

            // ── Hook 2: 拦截实际网络请求入口 ──
            // 高德走的是 mtopsdk 内部的 filter chain
            // 关键类: mtopsdk.mtop.intf.MtopBuilder / MtopImpl
            hookMtopBuilder(classLoader);

        } catch (Throwable t) {
            Log.e(TAG, "Hook MtopRequest 失败", t);
        }
    }

    /**
     * Hook setApiName — 在 API 名被设置时就打标记
     */
    private static void hookSetApiName(Class<?> mtopRequestClass) {
        try {
            // 找 setApiName 方法
            Method setApiName = null;
            for (Method m : mtopRequestClass.getDeclaredMethods()) {
                if (m.getName().equals("setApiName") && m.getParameterTypes().length == 1) {
                    setApiName = m;
                    break;
                }
            }
            if (setApiName == null) {
                Log.w(TAG, "未找到 MtopRequest.setApiName");
                return;
            }

            XposedBridge.hookMethod(setApiName, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String apiName = (String) param.args[0];
                    if (apiName == null) return;

                    Object request = param.thisObject;

                    // 判断是否需要记录
                    if (shouldLog(apiName)) {
                        String version = safeCallStringMethod(request, "getVersion");
                        MtopLogger.logMtopCall(apiName, version, "(setting apiName)", null);
                        Log.i(TAG, "[LOG] API: " + apiName);
                    }

                                        // 判断是否需要屏蔽
                    if (shouldBlock(apiName)) {
                        // 标记这个 request 对象为"待拦截"
                        XposedHelpers.setAdditionalInstanceField(request, "amap_blocker_blocked", true);
                        Log.i(TAG, "[BLOCK] 标记拦截: " + apiName);
                        MtopLogger.logAdBlocked("MTOP_MARK", "标记拦截: " + apiName);
                    }
                }
            });

            Log.i(TAG, "Hook setApiName 成功");
        } catch (Throwable t) {
            Log.w(TAG, "Hook setApiName 失败: " + t.getMessage());
        }
    }

    /**
     * Hook MtopBuilder / MtopImpl — 在请求真正发出前拦截
     * 
     * 这是关键：在请求进入网络层之前，检查是否被标记为待拦截，
     * 如果是，直接伪造返回值，不走网络。
     */
    private static void hookMtopBuilder(ClassLoader classLoader) {
        // 尝试多个可能的入口类
        String[] builderClasses = {
            "mtopsdk.mtop.intf.MtopBuilder",
            "mtopsdk.mtop.intf.MtopImpl",
            "mtopsdk.mtop.MtopImpl",
            "mtopsdk.mtop.intf.Mtop",
            "mtopsdk.mtop.Mtop",
        };

        for (String className : builderClasses) {
            try {
                Class<?> builderClass = XposedHelpers.findClass(className, classLoader);
                hookBuilderMethods(builderClass);
                Log.i(TAG, "Hook " + className + " 成功");
                break; // 成功一个就够了
            } catch (Throwable t) {
                // 继续尝试下一个
            }
        }

        // 也尝试 hook 高德自己的 MTOP 服务封装
        try {
            Class<?> aMapMtopClass = XposedHelpers.findClass(
                "com.autonavi.minimap.AMapMTopService", classLoader);
            hookAMapMtopService(aMapMtopClass);
            Log.i(TAG, "Hook AMapMTopService 成功");
        } catch (Throwable t) {
            // 高德可能没这个类，没关系
        }

        try {
            Class<?> mtopServiceImpl = XposedHelpers.findClass(
                "com.autonavi.minimap.mtop.services.MtopServiceImpl", classLoader);
            hookAMapMtopService(mtopServiceImpl);
            Log.i(TAG, "Hook MtopServiceImpl 成功");
        } catch (Throwable t) {
            // 同上
        }
    }

    /**
     * Hook Builder/Impl 的方法，拦截实际发送
     */
    private static void hookBuilderMethods(Class<?> builderClass) {
        for (Method method : builderClass.getDeclaredMethods()) {
            // 找返回 MtopResponse 或 void 的方法，参数包含 listener/callback
            // 典型签名: reqAsync(listener) / build().req()
            String name = method.getName();
            Class<?>[] paramTypes = method.getParameterTypes();

            // 匹配 reqAsync / req / build 等方法
            if (name.equals("reqAsync") || name.equals("req") || 
                name.equals("syncRequest") || name.equals("build")) {

                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        // 从 this 或参数中拿到 MtopRequest
                        Object request = extractRequest(param);
                        if (request == null) return;

                                                // 检查是否被标记为待拦截
                        Boolean blocked = null;
                        try {
                            blocked = (Boolean) XposedHelpers.getAdditionalInstanceField(
                                    request, "amap_blocker_blocked");
                        } catch (Throwable ignored) {}

                        if (blocked != null && blocked) {
                            String apiName = safeCallStringMethod(request, "getApiName");
                            Log.i(TAG, "[BLOCKED] 拦截请求: " + apiName);

                            // 方案 A: 伪造空 JSON 响应
                            fakeEmptyResponse(param);

                            // 方案 B: 如果伪造不行，就直接取消
                            // param.setResult(null);
                        }
                    }
                });
            }
        }
    }

    /**
     * Hook 高德自己的 MTOP 服务封装
     */
    private static void hookAMapMtopService(Class<?> serviceClass) {
        for (Method method : serviceClass.getDeclaredMethods()) {
            String name = method.getName();
            Class<?>[] paramTypes = method.getParameterTypes();

            // 找包含 "mtop" / "request" / "send" / "call" 的方法名
            if (name.toLowerCase().contains("mtop") || 
                name.toLowerCase().contains("request") ||
                name.toLowerCase().contains("send") ||
                name.toLowerCase().contains("call")) {

                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        // 记录高德自己的 MTOP 调用
                        StringBuilder argInfo = new StringBuilder();
                        for (int i = 0; i < param.args.length && i < 3; i++) {
                            if (param.args[i] != null) {
                                argInfo.append("arg").append(i).append("=")
                                       .append(truncate(param.args[i].toString(), 100))
                                       .append("; ");
                            }
                        }
                        MtopLogger.log("AMAP_MTOP", name + "(" + argInfo + ")");
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (param.getResult() != null) {
                            String resultStr = param.getResult().toString();
                            MtopLogger.log("AMAP_MTOP_RESULT", 
                                name + " -> " + truncate(resultStr, 200));
                        }
                    }
                });
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  工具方法
    // ═══════════════════════════════════════════════════════════════

    /**
     * 判断是否需要记录日志
     */
    private static boolean shouldLog(String apiName) {
        String lower = apiName.toLowerCase();
        for (String keyword : LOG_KEYWORDS) {
            if (lower.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否需要屏蔽
     */
    private static boolean shouldBlock(String apiName) {
        // 精确匹配
        if (BLOCKED_APIS.contains(apiName)) {
            return true;
        }
        // 关键词模糊匹配
        String lower = apiName.toLowerCase();
        for (String keyword : BLOCK_KEYWORDS) {
            if (lower.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从 Hook 参数中提取 MtopRequest 对象
     */
    private static Object extractRequest(MethodHookParam param) {
        // 先检查 this
        if (param.thisObject != null) {
            try {
                Object req = XposedHelpers.callMethod(param.thisObject, "getMtopRequest");
                if (req != null) return req;
            } catch (Throwable ignored) {}

            try {
                Object req = XposedHelpers.getObjectField(param.thisObject, "mtopRequest");
                if (req != null) return req;
            } catch (Throwable ignored) {}

            try {
                Object req = XposedHelpers.getObjectField(param.thisObject, "request");
                if (req != null) return req;
            } catch (Throwable ignored) {}
        }

        // 检查参数
        for (Object arg : param.args) {
            if (arg != null) {
                String className = arg.getClass().getName();
                if (className.contains("MtopRequest")) {
                    return arg;
                }
            }
        }

        return null;
    }

    /**
     * 伪造空响应 — 让调用方收到一个正常的空 JSON
     */
    private static void fakeEmptyResponse(MethodHookParam param) {
        try {
            Class<?> responseClass = XposedHelpers.findClass(
                "mtopsdk.mtop.domain.MtopResponse", param.thisObject.getClass().getClassLoader());

            // 尝试构造一个空响应
            Object fakeResponse = XposedHelpers.newInstance(responseClass);

            // 设置 retCode 为成功
            try {
                XposedHelpers.callMethod(fakeResponse, "setRetCode", "SUCCESS");
            } catch (Throwable ignored) {}

            try {
                XposedHelpers.callMethod(fakeResponse, "setResponseCode", 200);
            } catch (Throwable ignored) {}

            // 设置空 body
            try {
                XposedHelpers.callMethod(fakeResponse, "setBytedData", 
                    FAKE_EMPTY_RESPONSE.getBytes("UTF-8"));
            } catch (Throwable ignored) {}

            // 如果方法返回值是 MtopResponse，直接替换
            if (param.getMethod().getReturnType().getName().contains("MtopResponse")) {
                param.setResult(fakeResponse);
                Log.i(TAG, "[BLOCKED] 已伪造空响应");
            } else {
                // 否则尝试通过回调传递
                // 这种情况比较复杂，先标记
                Log.w(TAG, "[BLOCKED] 无法直接伪造响应（方法返回类型: " + 
                    param.getMethod().getReturnType().getName() + "）");
            }
        } catch (Throwable t) {
            Log.e(TAG, "[BLOCKED] 伪造响应失败", t);
            // 最后手段：直接取消请求
            param.setResult(null);
        }
    }

    /**
     * 安全调用字符串方法
     */
    private static String safeCallStringMethod(Object obj, String methodName) {
        try {
            Object result = XposedHelpers.callMethod(obj, methodName);
            return result != null ? result.toString() : "null";
        } catch (Throwable t) {
            return "(error)";
        }
    }

    /**
     * 截断字符串
     */
    private static String truncate(String s, int maxLen) {
        if (s == null) return "null";
        return s.length() > maxLen ? s.substring(0, maxLen) + "..." : s;
    }
}
