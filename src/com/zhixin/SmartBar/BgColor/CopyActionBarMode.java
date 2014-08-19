package com.zhixin.SmartBar.BgColor;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.TypedValue;

/**
 * Created by zhixin on 2014/8/16.
 */
public class CopyActionBarMode implements ISmartBarMode {
    private  boolean mustChange;
    public static Drawable bitmap2Drawable(Bitmap bitmap){
        return new BitmapDrawable(bitmap) ;
    }
    public static Bitmap drawable2Bitmap(Drawable drawable){
        if(drawable instanceof BitmapDrawable){
            return ((BitmapDrawable)drawable).getBitmap() ;
        }else if(drawable instanceof NinePatchDrawable){
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        }else{
            return null ;
        }
    }
    /**获取ActionBar的背景*/
    public static Drawable getActionBarBackground(Context context)
    {
        int[] android_styleable_ActionBar = { android.R.attr.background };
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.actionBarStyle, outValue, true);
        TypedArray abStyle = context.getTheme().obtainStyledAttributes(outValue.resourceId, android_styleable_ActionBar);
        try
        {
            return abStyle.getDrawable(0);
        }
        finally
        {
            abStyle.recycle();
        }
    }
    @Override
    public boolean mustChnageIcon() {
        return mustChange;
    }
    @Override
    public Drawable getSmartBarDrawable(Activity activity) {
        Drawable bg= getActionBarBackground(activity);
        if(bg instanceof NinePatchDrawable){
            Bitmap bitmap= drawable2Bitmap(bg);
            SingleColorMode singleColorMode=new SingleColorMode(bitmap.getPixel(bitmap.getWidth()/2,bitmap.getHeight()/2));
            mustChange=singleColorMode.mustChnageIcon();
            return singleColorMode.getSmartBarDrawable(activity);
        }
        return  bg;
    }
}
