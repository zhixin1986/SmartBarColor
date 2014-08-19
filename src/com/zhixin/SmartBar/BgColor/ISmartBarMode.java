package com.zhixin.SmartBar.BgColor;

import android.app.Activity;
import android.graphics.drawable.Drawable;

/**
 * Created by zhixin on 2014/8/16.
 */
public interface ISmartBarMode {
    Drawable getSmartBarDrawable(Activity activity);
    boolean mustChnageIcon();
}
