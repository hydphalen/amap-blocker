package com.miclaw.amapblocker;

import android.util.Log;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Method;

/**
 * 内容/卡片系统 Hook
 * 
 * 目标类：
 * - ContentWidgetRepository (内容卡片仓库)
 * - IndustryPromotionCardDataParser (推广卡片解析器)
 * - FetchCardDataUseCase (卡片数据获取)
 * - MapHomePagePresenter (首页 Presenter)
 * - MapHomePageViewModel (首页 ViewModel)
 */
public class ContentHook {

    private static final String TAG = "AMapBlocker";

    // 推广卡片类型关键词
    private static final String[] AD_CARD_KEYWORDS = {
        "promotion",
        "advert",
        "industry",
        "recommend",
        "splash",
        "banner",
        "sponsored",
        "ad_",
        "_ad",
        "commercial"
    };

    /**
     * Hook 内容/卡片系统
     */
    public static void hook(ClassLoader classLoader) {
        Log.i(TAG, "开始 Hook 内容/卡片系统...");

        // 策略 1: Hook ContentWidgetRepository
        hookContentWidgetRepository(classLoader);

        // 策略 2: Hook IndustryPromotionCardDataParser
        hookIndustryPromotionCardParser(classLoader);

        // 策略 3: Hook FetchCardDataUseCase
        hookFetchCardDataUseCase(classLoader);

        // 策略 4: Hook 首页 Presenter/ViewModel
        hookHomePagePresenter(classLoader);

        Log.i(TAG, "内容/卡片系统 Hook 完成");
    }

    /**
     * Hook ContentWidgetRepository - 内容卡片仓库
     */
    private static void hookContentWidgetRepository(ClassLoader classLoader) {
        try {
            Class<?> repositoryClass = XposedHelpers.findClass(
                "com.autonavi.bundle.amaphome.desktopwidget.hiboard.contentrecommend.ContentWidgetRepository", 
                classLoader);

            // Hook 所有方法
            for (Method method : repositoryClass.getDeclaredMethods()) {
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String methodName = method.getName();
                        Object[] args = param.args;
                        
                        // 记录方法调用
                        StringBuilder argInfo = new StringBuilder();
                        for (int i = 0; i < args.length; i++) {
                            if (args[i] != null) {
                                String argStr = args[i].toString();
                                argInfo.append("arg").append(i).append("=")
                                       .append(argStr.substring(0, Math.min(100, argStr.length())))
                                       .append("; ");
                            }
                        }
                        
                        MtopLogger.log("CONTENT_REPO", 
                            methodName + "(" + argInfo + ")");
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String methodName = method.getName();
                        Object result = param.getResult();
                        
                        if (result != null) {
                            String resultStr = result.toString();
                            MtopLogger.log("CONTENT_REPO_RESULT", 
                                methodName + " -> " + resultStr.substring(0, Math.min(200, resultStr.length())));
                        }
                    }
                });
            }

            Log.i(TAG, "Hook ContentWidgetRepository 成功");
        } catch (Throwable t) {
            Log.w(TAG, "Hook ContentWidgetRepository 失败: " + t.getMessage());
        }
    }

    /**
     * Hook IndustryPromotionCardDataParser - 推广卡片解析器
     */
    private static void hookIndustryPromotionCardParser(ClassLoader classLoader) {
        try {
            Class<?> parserClass = XposedHelpers.findClass(
                "com.autonavi.minimap.searchlist.search.components.card.parser.IndustryPromotionCardDataParser", 
                classLoader);

            // Hook parse 方法
            for (Method method : parserClass.getDeclaredMethods()) {
                if (method.getName().equals("parse") || 
                    method.getName().contains("Data") ||
                    method.getName().contains("Card")) {
                    
                    XposedBridge.hookMethod(method, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            String methodName = method.getName();
                            Object[] args = param.args;
                            
                            StringBuilder argInfo = new StringBuilder();
                            for (int i = 0; i < args.length; i++) {
                                if (args[i] != null) {
                                    String argStr = args[i].toString();
                                    argInfo.append("arg").append(i).append("=")
                                           .append(argStr.substring(0, Math.min(150, argStr.length())))
                                           .append("; ");
                                }
                            }
                            
                            MtopLogger.log("PROMO_PARSER", 
                                methodName + "(" + argInfo + ")");
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            String methodName = method.getName();
                            Object result = param.getResult();
                            
                            if (result != null) {
                                String resultStr = result.toString();
                                MtopLogger.log("PROMO_PARSER_RESULT", 
                                    methodName + " -> " + resultStr.substring(0, Math.min(300, resultStr.length())));
                            }
                        }
                    });
                }
            }

            Log.i(TAG, "Hook IndustryPromotionCardDataParser 成功");
        } catch (Throwable t) {
            Log.w(TAG, "Hook IndustryPromotionCardDataParser 失败: " + t.getMessage());
        }
    }

    /**
     * Hook FetchCardDataUseCase - 卡片数据获取
     */
    private static void hookFetchCardDataUseCase(ClassLoader classLoader) {
        try {
            Class<?> useCaseClass = XposedHelpers.findClass(
                "com.amap.bundle.nativerender.event.usecases.FetchCardDataUseCase", 
                classLoader);

            // Hook execute 方法
            for (Method method : useCaseClass.getDeclaredMethods()) {
                if (method.getName().equals("execute") || 
                    method.getName().equals("run") ||
                    method.getName().contains("Fetch")) {
                    
                    XposedBridge.hookMethod(method, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            String methodName = method.getName();
                            Object[] args = param.args;
                            
                            StringBuilder argInfo = new StringBuilder();
                            for (int i = 0; i < args.length; i++) {
                                if (args[i] != null) {
                                    String argStr = args[i].toString();
                                    argInfo.append("arg").append(i).append("=")
                                           .append(argStr.substring(0, Math.min(150, argStr.length())))
                                           .append("; ");
                                }
                            }
                            
                            MtopLogger.log("CARD_FETCH", 
                                methodName + "(" + argInfo + ")");
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            String methodName = method.getName();
                            Object result = param.getResult();
                            
                            if (result != null) {
                                String resultStr = result.toString();
                                MtopLogger.log("CARD_FETCH_RESULT", 
                                    methodName + " -> " + resultStr.substring(0, Math.min(300, resultStr.length())));
                            }
                        }
                    });
                }
            }

            Log.i(TAG, "Hook FetchCardDataUseCase 成功");
        } catch (Throwable t) {
            Log.w(TAG, "Hook FetchCardDataUseCase 失败: " + t.getMessage());
        }
    }

    /**
     * Hook 首页 Presenter
     */
    private static void hookHomePagePresenter(ClassLoader classLoader) {
        // 尝试 Hook 首页相关的 Presenter/ViewModel
        String[] presenterClasses = {
            "com.autonavi.minimap.amap.AMapHomePagePresenter",
            "com.autonavi.minimap.amap.AMapHomePageViewModel",
            "com.autonavi.minimap.amap.homepage.MapHomePagePresenter",
            "com.autonavi.minimap.amap.homepage.MapHomePageViewModel"
        };

        for (String className : presenterClasses) {
            try {
                Class<?> presenterClass = XposedHelpers.findClass(className, classLoader);
                
                // Hook 数据加载相关方法
                for (Method method : presenterClass.getDeclaredMethods()) {
                    String methodName = method.getName();
                    if (methodName.contains("load") || 
                        methodName.contains("Load") ||
                        methodName.contains("fetch") ||
                        methodName.contains("Fetch") ||
                        methodName.contains("refresh") ||
                        methodName.contains("Refresh") ||
                        methodName.contains("data") ||
                        methodName.contains("Data")) {
                        
                        XposedBridge.hookMethod(method, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                Object[] args = param.args;
                                
                                StringBuilder argInfo = new StringBuilder();
                                for (int i = 0; i < args.length; i++) {
                                    if (args[i] != null) {
                                        String argStr = args[i].toString();
                                        argInfo.append("arg").append(i).append("=")
                                               .append(argStr.substring(0, Math.min(100, argStr.length())))
                                               .append("; ");
                                    }
                                }
                                
                                MtopLogger.log("HOME_PAGE", 
                                    className + "." + methodName + "(" + argInfo + ")");
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
}
