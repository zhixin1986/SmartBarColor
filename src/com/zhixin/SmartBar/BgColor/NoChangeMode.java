package com.zhixin.SmartBar.BgColor;

import android.app.Activity;
import android.graphics.drawable.Drawable;

/**
 * Created by zhixin on 2014/8/16.
 */
public class NoChangeMode implements ISmartBarMode {
    @Override
    public Drawable getSmartBarDrawable(Activity activity) {
        return null;
    }

    @Override
    public boolean mustChnageIcon() {
        return false;
    }
}
