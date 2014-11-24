package com.zhixin.SmartBar.BgColor;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import com.zhixin.SmartBar.ConfigManager;
import com.zhixin.SmartBar.ShareRes.MZShareRes;
import com.zhixin.SmartBar.SmartBarUtils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by zhixin on 2014/8/18.
 */
public class SmartBarMethodHook extends XC_MethodHook {
    private boolean colorChange = false;

    public SmartBarMethodHook(boolean change) {
        colorChange = change;
    }

    @Override
    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
        Activity thisActivity = (Activity) param.thisObject;
        Object mSmartBarMode = XposedHelpers.getAdditionalInstanceField(thisActivity, "mSmartBarMode");
        Object mSmartBarColor = XposedHelpers.getAdditionalInstanceField(thisActivity, "mSmartBarColor");
        int smartBarMode = mSmartBarMode == null ? 0 : Integer.valueOf(mSmartBarMode.toString());
        int smartBarColor = mSmartBarColor == null ? 0 : Integer.valueOf(mSmartBarColor.toString());
        smartBarMode = colorChange ? 0 : smartBarMode;
        ConfigManager configManager = ConfigManager.Instance();
        configManager.load();
        String packageName = thisActivity.getPackageName();
        boolean mustChange = XposedHelpers.getAdditionalInstanceField(thisActivity, MZShareRes.mustChangeIcon) != null;
        if (configManager.exist(packageName + ".color")) {
            int color = configManager.getColor(packageName + ".color", 0);
            if (smartBarColor != color) {
                mustChange = SmartBarUtils.changeSmartBarColor(thisActivity, color);
                XposedBridge.log("ZX:" + packageName + ":Color->" + color);
                XposedHelpers.setAdditionalInstanceField(thisActivity, "mSmartBarColor", color);
            }
        } else {
            int def = configManager.getInt(ConfigManager.DEF_MODE_NAME, 0);
            int mode = configManager.getInt(thisActivity.getPackageName(), def);
            if (mode > 0 && mode != smartBarMode) {
                XposedHelpers.setAdditionalInstanceField(thisActivity, "mSmartBarMode", mode);
                mustChange = SmartBarUtils.changeSmartBarColor(thisActivity, SmartFactory.getModeByValue(mode));
            }
        }
        if (mustChange) {
            SmartBarUtils.changeSmartBarIcon(thisActivity,MZShareRes.getShareRes().shareActionBar);
            XposedHelpers.setAdditionalInstanceField(thisActivity, MZShareRes.mustChangeIcon, mustChange);
            MZShareRes res = MZShareRes.getShareRes();
            res.thisActivity = thisActivity;
            ImageButton mBackButton = res.shareBackButton;
            if (mBackButton != null) {
                if (res.mz_ic_tab_back_normal_dark != 0) {
                    mBackButton.setImageDrawable(thisActivity.getResources().getDrawable(res.mz_ic_tab_back_normal_dark));
                }
            }
            ImageButton mMenuView = res.shareMenuView;
            if (mMenuView != null && mMenuView instanceof ImageButton) {
                if (res.mz_ic_tab_more_normal_dark != 0) {
                    mMenuView.setImageDrawable(thisActivity.getResources().getDrawable(res.mz_ic_tab_more_normal_dark));
                }
            }
        }
    }
}
