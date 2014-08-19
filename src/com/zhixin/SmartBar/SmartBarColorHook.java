package com.zhixin.SmartBar;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import com.zhixin.SmartBar.BgColor.SmartBarMethodHook;
import com.zhixin.SmartBar.BgColor.SmartIconMethodHook;
import de.robv.android.xposed.*;
/**
 * Created by zhixin on 2014/8/16.
 */
public class SmartBarColorHook implements IXposedHookZygoteInit {
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        XposedHelpers.findAndHookMethod(Activity.class, "onStart",new SmartBarMethodHook(false));
        XposedHelpers.findAndHookMethod(ActionBar.class,"setBackgroundDrawable",Drawable.class,new SmartBarMethodHook(true));
    }
}
