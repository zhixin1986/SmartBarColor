package com.zhixin.SmartBar.BgColor;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
/**
 * Created by zhixin on 2014/8/18.
 */
public class SmartIconMethodHook extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
        View view=(View)param.thisObject;
        if (view.getContext() instanceof Activity){
            Activity activity=(Activity) view.getContext();
            Object mustChangeIcon= XposedHelpers.getAdditionalInstanceField(activity,"mustChangeIcon");
            if(mustChangeIcon!=null){
                StateListDrawable drawable=(StateListDrawable)view.getBackground();
                Bitmap bitmap= SingleColorMode.HandleBitMap(drawable, Color.GRAY);
                if (bitmap!=null){
                    param.args[0]=new BitmapDrawable(bitmap);
                }
            }
        }
    }
}
