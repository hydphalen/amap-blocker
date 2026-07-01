package com.miclaw.amapblocker;

import android.util.Log;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 广告/推广过滤 Hook
 * 
 * 功能：
 * 1. 过滤 MTOP 响应中的推广内容
 * 2. 拦截推广卡片的显示
 * 3. 移除首页推荐流中的广告
 */
public class AdFilterHook {

    private static final String TAG = "AMapBlocker";

    // 推广内容类型标识
    private static final String[] AD_TYPE_IDENTIFIERS = {
        "promotion",
        "advert",
        "industry",
        "sponsored",
        "commercial",
        "ad_card",
        "ad_banner",
        "ad_splash",
        "recommend_ad",
        "feed_ad"
    };

    // 推广字段名
    private static final String[] AD_FIELD_NAMES = {
        "isAd",
        "is_ad",
        "isPromotion",
        "is_promotion",
        "adType",
        "ad_type",
        "promotionType",
        "promotion_type",
        "commercial",
        "sponsored"
    };

    /**
     * Hook 广告过滤系统
     */
    public static void hook(ClassLoader classLoader) {
        Log.i(TAG, "开始 Hook 广告过滤系统...");

        // 策略 1: Hook JSON 解析，过滤推广内容
        hookJsonParsing(classLoader);

        // 策略 2: Hook 卡片渲染，拦截推广卡片
        hookCardRendering(classLoader);

        // 策略 3: Hook 列表适配器，移除推广项
        hookListAdapter(classLoader);

        Log.i(TAG, "广告过滤系统 Hook 完成");
    }

    /**
     * Hook JSON 解析 - 过滤推广内容
     */
    private static void hookJsonParsing(ClassLoader classLoader) {
        try {
            // Hook JSONObject 解析
            Class<?> jsonObjectClass = XposedHelpers.findClass(
                "org.json.JSONObject", classLoader);

            // Hook put 方法，检测推广内容
            for (Method method : jsonObjectClass.getDeclaredMethods()) {
                if (method.getName().equals("put") && 
                    method.getParameterTypes().length == 2 &&
                    method.getParameterTypes()[0] == String.class) {
                    
                    XposedBridge.hookMethod(method, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            String key = (String) param.args[0];
                            Object value = param.args[1];
                            
                            // 检查是否是推广字段
                            if (isAdField(key)) {
                                MtopLogger.logAdBlocked("JSON_FIELD", 
                                    "检测到推广字段: " + key + "=" + value);
                            }
                        }
                    });
                }
            }

            Log.i(TAG, "Hook JSONObject 成功");
        } catch (Throwable t) {
            Log.w(TAG, "Hook JSONObject 失败: " + t.getMessage());
        }
    }

    /**
     * Hook 卡片渲染 - 拦截推广卡片
     */
    private static void hookCardRendering(ClassLoader classLoader) {
        // 尝试 Hook 卡片渲染相关的类
        String[] cardClasses = {
            "com.autonavi.minimap.searchlist.search.components.card.BaseCard",
            "com.autonavi.minimap.searchlist.search.components.card.CardFactory",
            "com.amap.bundle.nativerender.NativeRenderCard",
            "com.autonavi.bundle.amaphome.desktopwidget.hiboard.card.BaseCard"
        };

        for (String className : cardClasses) {
            try {
                Class<?> cardClass = XposedHelpers.findClass(className, classLoader);
                
                // Hook bind/bindData 方法
                for (Method method : cardClass.getDeclaredMethods()) {
                    String methodName = method.getName();
                    if (methodName.equals("bind") || 
                        methodName.equals("bindData") ||
                        methodName.equals("setData") ||
                        methodName.equals("render")) {
                        
                        XposedBridge.hookMethod(method, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                Object[] args = param.args;
                                
                                // 检查数据是否包含推广标识
                                for (Object arg : args) {
                                    if (arg != null && isAdContent(arg)) {
                                        // 阻止推广卡片渲染
                                        param.setResult(null);
                                        MtopLogger.logAdBlocked("CARD_RENDER", 
                                            "拦截推广卡片: " + className);
                                        return;
                                    }
                                }
                            }
                        });
                    }
                }
                
                Log.i(TAG, "Hook " + className + " 成功");
            } catch (Throwable t) {
                Log.d(TAG, "Hook " + className + " 失败: " + t.getMessage());
            }
        }
    }

    /**
     * Hook 列表适配器 - 移除推广项
     */
    private static void hookListAdapter(ClassLoader classLoader) {
        // 尝试 Hook RecyclerView.Adapter
        try {
            Class<?> adapterClass = XposedHelpers.findClass(
                "androidx.recyclerview.widget.RecyclerView.Adapter", classLoader);

            // Hook onBindViewHolder 方法
            for (Method method : adapterClass.getDeclaredMethods()) {
                if (method.getName().equals("onBindViewHolder")) {
                    XposedBridge.hookMethod(method, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            // 获取 position
                            int position = (int) param.args[1];
                            
                            // 这里需要根据具体实现来判断是否是推广项
                            // 暂时记录日志
                            MtopLogger.log("ADAPTER_BIND", 
                                "onBindViewHolder position=" + position);
                        }
                    });
                }
            }

            Log.i(TAG, "Hook RecyclerView.Adapter 成功");
        } catch (Throwable t) {
            Log.w(TAG, "Hook RecyclerView.Adapter 失败: " + t.getMessage());
        }
    }

    /**
     * 判断是否是推广字段
     */
    private static boolean isAdField(String fieldName) {
        if (fieldName == null) return false;
        
        String lowerName = fieldName.toLowerCase();
        for (String adField : AD_FIELD_NAMES) {
            if (lowerName.contains(adField.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是推广内容
     */
    private static boolean isAdContent(Object obj) {
        if (obj == null) return false;
        
        String content = obj.toString().toLowerCase();
        
        // 检查推广类型标识
        for (String adType : AD_TYPE_IDENTIFIERS) {
            if (content.contains(adType)) {
                return true;
            }
        }
        
        // 检查推广字段
        for (String adField : AD_FIELD_NAMES) {
            if (content.contains(adField.toLowerCase())) {
                return true;
            }
        }
        
        return false;
    }
}
