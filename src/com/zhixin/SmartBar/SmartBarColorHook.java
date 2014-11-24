package com.zhixin.SmartBar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import com.zhixin.SmartBar.BgColor.SmartBarMethodHook;
import com.zhixin.SmartBar.ShareRes.MZShareRes;
import de.robv.android.xposed.*;

/**
 * Created by zhixin on 2014/8/16.
 */
public class SmartBarColorHook implements IXposedHookZygoteInit {
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        XposedHelpers.findAndHookMethod(Activity.class, "onStart", new SmartBarMethodHook(false));
        //XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new SmartBarMethodHook(false));
        //XposedHelpers.findAndHookMethod(ActionBar.class,"setBackgroundDrawable",Drawable.class,new SmartBarMethodHook(true));
        Class keyBackClass = Class.forName("com.meizu.widget.KeyBackButton");
        XposedHelpers.findAndHookMethod(keyBackClass, "init", Context.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                ImageButton mBackButton = (ImageButton) param.thisObject;
                if (mBackButton != null && mBackButton.getContext() instanceof Activity) {
                    Activity activity = (Activity) mBackButton.getContext();
                    XposedBridge.log("ZX:" + activity.getPackageName() + ":init");
                    MZShareRes.getShareRes().shareBackButton = mBackButton;
                    if (XposedHelpers.getAdditionalInstanceField(activity, MZShareRes.mustChangeIcon) != null) {
                        MZShareRes res = MZShareRes.getShareRes();
                        if (res.mz_ic_tab_back_normal_dark != 0) {
                            mBackButton.setImageDrawable(activity.getResources().getDrawable(res.mz_ic_tab_back_normal_dark));
                        }
                    }
                }
            }
        });
        Class mzActionBarClass = Class.forName("com.android.internal.widget.MzActionBarContainer");
        XposedHelpers.findAndHookMethod(mzActionBarClass, "setMenu", View.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                if (param.args[0] != null && param.args[0] instanceof ImageButton) {
                    ImageButton mMenuView = (ImageButton) param.args[0];
                    Activity activity = (Activity) mMenuView.getContext();
                    XposedBridge.log("ZX:" + activity.getPackageName() + ":setMenu->");
                    MZShareRes.getShareRes().shareMenuView = mMenuView;
                    if (XposedHelpers.getAdditionalInstanceField(activity, MZShareRes.mustChangeIcon) != null) {
                        MZShareRes res = MZShareRes.getShareRes();
                        if (res.mz_ic_tab_more_normal_dark != 0) {
                            mMenuView.setImageDrawable(activity.getResources().getDrawable(res.mz_ic_tab_more_normal_dark));
                        }
                    }
                }
            }
        });
        XposedHelpers.findAndHookMethod(mzActionBarClass, "onLayout", boolean.class,int.class,int.class,int.class,int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                Activity activity = MZShareRes.getShareRes().thisActivity;
                MZShareRes.getShareRes().shareActionBar=(FrameLayout)param.thisObject;
                if(activity!=null){
                    SmartBarUtils.changeSmartBarIcon(activity,MZShareRes.getShareRes().shareActionBar);
                    String log="ZX:" + activity.getPackageName() + ":onLayout->";
                    for (int i=0;i<param.args.length;i++){
                        log+=","+param.args[i].toString();
                    }
                    XposedBridge.log(log);
                }
            }
        });
        Class actionMenuClass = Class.forName("com.android.internal.view.menu.ActionMenuPresenter");
        XposedHelpers.findAndHookMethod(actionMenuClass, "updateSmartBarConfiguration", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                Object mOverflowButton = XposedHelpers.getObjectField(param.thisObject, "mOverflowButton");
                if (mOverflowButton != null && mOverflowButton instanceof ImageButton) {
                    ImageButton mMenuView = (ImageButton) mOverflowButton;
                    MZShareRes.getShareRes().shareMenuView = mMenuView;
                    Activity activity = MZShareRes.getShareRes().thisActivity;
                    if (activity != null) {
                        if (XposedHelpers.getAdditionalInstanceField(activity, MZShareRes.mustChangeIcon) != null) {
                            MZShareRes res = MZShareRes.getShareRes();
                            if (res.mz_ic_tab_more_normal_dark != 0) {
                                XposedBridge.log("ZX:" + activity.getPackageName() + ":updateSmartBarConfiguration->0");
                                mMenuView.setImageDrawable(activity.getResources().getDrawable(res.mz_ic_tab_more_normal_dark));
                            }
                        }
                    }
                }
                Object mzBackButton = XposedHelpers.getObjectField(param.thisObject, "mBackButton");
                if (mzBackButton != null && mzBackButton instanceof ImageButton) {
                    ImageButton mBackButton = (ImageButton) mzBackButton;
                    MZShareRes.getShareRes().shareBackButton = mBackButton;
                    Activity activity = MZShareRes.getShareRes().thisActivity;
                    if (activity != null) {
                        XposedBridge.log("ZX:" + activity.getPackageName() + ":updateSmartBarConfiguration->1");
                        if (XposedHelpers.getAdditionalInstanceField(activity, MZShareRes.mustChangeIcon) != null) {
                            MZShareRes res = MZShareRes.getShareRes();
                            if (res.mz_ic_tab_back_normal_dark != 0) {
                                mBackButton.setImageDrawable(activity.getResources().getDrawable(res.mz_ic_tab_back_normal_dark));
                            }
                        }
                    }
                }
            }
        });

        Class menuButtonClass = Class.forName("com.android.internal.view.menu.ActionMenuPresenter$OverflowMenuButton");
        XposedHelpers.findAndHookMethod(menuButtonClass, "restoreDrawable", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                ImageButton menuView = (ImageButton) param.thisObject;
                MZShareRes.getShareRes().shareMenuView = menuView;
                Activity activity = MZShareRes.getShareRes().thisActivity;
                if (activity != null) {
                    if (XposedHelpers.getAdditionalInstanceField(activity, MZShareRes.mustChangeIcon) != null) {
                        MZShareRes res = MZShareRes.getShareRes();
                        if (res.mz_ic_tab_more_normal_dark != 0) {
                            menuView.setImageDrawable(activity.getResources().getDrawable(res.mz_ic_tab_more_normal_dark));
                        }
                    }
                }
            }
        });
    }
}
