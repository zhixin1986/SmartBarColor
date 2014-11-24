package com.zhixin.SmartBar;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import com.zhixin.SmartBar.BgColor.ISmartBarMode;
import com.zhixin.SmartBar.BgColor.SingleColorMode;
import com.zhixin.SmartBar.BgColor.SmartFactory;
import com.zhixin.SmartBar.BgColor.SmartOptions;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by zhixin on 2014/8/16.
 */
public class SmartBarUtils {
    public  static  Bitmap SmartBarBackIcon;
    public  static boolean changeSmartBarColor(Activity thisActivity)
    {
        ConfigManager configManager=ConfigManager.Instance();
        configManager.load();
        String packageName=thisActivity.getPackageName();
        String log="";
        if (configManager.exist(packageName+".color")){
            int color=configManager.getColor(packageName + ".color", 0);
            return SmartBarUtils.changeSmartBarColor(thisActivity, color);
        }else
        {
            int def=configManager.getInt(ConfigManager.DEF_MODE_NAME,0);
            int mode= configManager.getInt(thisActivity.getPackageName(),def);
            if(mode>0){
                return  SmartBarUtils.changeSmartBarColor(thisActivity, SmartFactory.getModeByValue(mode));
            }
        }
        return  false;
    }
    public static void changeSmartBarIcon(Activity activity, ViewGroup mzSmartBar) {
        if(mzSmartBar!=null){
            int count=mzSmartBar.getChildCount();
            for(int i=0;i<count;i++){
                View view=mzSmartBar.getChildAt(i);
                if (view instanceof ViewGroup){
                    changeSmartBarIcon(activity,(ViewGroup)view);
                }else
                {
                    String className=view.getClass().getName();
                    if(className.equals("com.meizu.widget.KeyBackButton"))
                        break;
                    if(className.equals("com.android.internal.view.menu.ActionMenuPresenter$OverflowMenuButton"))
                        break;
                    XposedBridge.log(activity.getPackageName()+":"+className);
                }
            }
        }
    }
    public static boolean changeSmartBarColor(Activity activity, int color) {
        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            ISmartBarMode barColorMode = new SingleColorMode(color);
            Drawable bg = barColorMode.getSmartBarDrawable(activity);
            if (bg != null) {
                actionBar.setSplitBackgroundDrawable(bg);
            }
            return  barColorMode.mustChnageIcon();
        }
        return  false;
    }
    public static boolean changeSmartBarColor(Activity activity, SmartOptions mode) {
        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            ISmartBarMode barColorMode = SmartFactory.createBarColorMode(mode);
            Drawable bg = barColorMode.getSmartBarDrawable(activity);
            if (bg != null) {
                actionBar.setSplitBackgroundDrawable(bg);
            }
            return  barColorMode.mustChnageIcon();
        }
        return  false;
    }
    /**
     * 获取ActionBar的高度
     */
    protected static int getActionBarSize(Context context) {
        int[] attrs = {android.R.attr.actionBarSize};
        TypedArray values = context.getTheme().obtainStyledAttributes(attrs);
        try {
            return values.getDimensionPixelSize(0, 0);//第一个参数数组索引，第二个参数 默认值
        } finally {
            values.recycle();
        }
    }
    private static BitmapDrawable FindSmartBarBackBtn(ViewGroup mSplitView)
    {
        int count= mSplitView.getChildCount();
        for(int i=0;i<count;i++){
            View view=  mSplitView.getChildAt(i);
            if(view.getClass().getName().equals("com.meizu.widget.KeyBackButton")){
                StateListDrawable drawable=(StateListDrawable) GetFieldValue(view,"mBackIcon");
                return (BitmapDrawable)drawable.getCurrent();
            }
            if(view instanceof  ViewGroup)  {
                BitmapDrawable drawable = FindSmartBarBackBtn((ViewGroup) view);
                if(drawable!=null)return  drawable;
            }
        }
        return  null;
    }
    public static void setBackIcon(ActionBar actionbar, Drawable backIcon) {
        try {
            Method method = Class.forName("android.app.ActionBar").getMethod(
                    "setBackButtonDrawable", new Class[] { Drawable.class });
            try {
                method.invoke(actionbar, backIcon);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static Bitmap FindSmartBarBackBtn(final Activity context)
    {
        if(SmartBarBackIcon!=null) return  SmartBarBackIcon;
        try{
            ActionBar actionBar = context.getActionBar();
            if(actionBar!=null){
                FrameLayout mSplitView = (FrameLayout) SmartBarUtils.GetFieldValue(actionBar, "mSplitView");
                BitmapDrawable drawable = FindSmartBarBackBtn(mSplitView);
                Bitmap bitmap= drawable.getBitmap();
                SmartBarBackIcon=bitmap;
                return  bitmap;
            }
        }catch(Exception e) {
        }
        return  null;
    }
    public static  int FindSmartBarBtnNum(ViewGroup mSplitView)
    {
        int count= mSplitView.getChildCount();
        for(int i=0;i<count;i++){
           View view=  mSplitView.getChildAt(i);
           if(view.getClass().getName().equals("com.meizu.widget.KeyBackButton")){
               view =mSplitView.getChildAt(count-1);
               if(view.getClass().getName().equals("com.android.internal.view.menu.ActionMenuPresenter$OverflowMenuButton")){
                   return  count-2;
               }
               return  count-1;
           }
           if(view instanceof  ViewGroup)  {
               int  num = FindSmartBarBtnNum((ViewGroup)view);
               if(num>=0) return  num;
           }
        }
        return  -1;
    }
    public static int HasActionBarHook(final Activity context, Menu menu) {
        try{
            ActionBar actionBar = context.getActionBar();
            if(actionBar!=null){
                FrameLayout mSplitView = (FrameLayout) SmartBarUtils.GetFieldValue(actionBar, "mSplitView");
                int count= FindSmartBarBtnNum(mSplitView);
                return count;
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return  -1;
    }
    public static void SetFieldValue(Object aObject, String aFieldName, Object value) {
        Field field = GetClassField(aObject.getClass(), aFieldName);// get the field in this object
        if (field != null) {
            field.setAccessible(true);
            try {
                field.set(aObject, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static Object GetFieldValue(Object aObject, String aFieldName) {
        Field field = GetClassField(aObject.getClass(), aFieldName);// get the field in this object
        if (field != null) {
            field.setAccessible(true);
            try {
                return field.get(aObject);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static Field GetClassField(Class aClazz, String aFieldName) {
        Field[] declaredFields = aClazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getName().equals(aFieldName)) {
                return field;// define in this class
            }
        }
        Class superclass = aClazz.getSuperclass();
        if (superclass != null) {// 简单的递归一下
            return GetClassField(superclass, aFieldName);
        }
        return null;
    }

    private static void testIfNull(Object obj, String error) throws NullPointerException {
        if (obj == null) throw new NullPointerException(error);
    }

    public static void HideV2(FrameLayout mSplitView) {

        mSplitView.setVisibility(View.GONE);

        for (int i = 0; i < mSplitView.getChildCount(); i++) {
            mSplitView.getChildAt(i).setVisibility(View.GONE);
        }
    }

    public static void ShowV2(ActionBar actionBar) {
        Object mActionView = SmartBarUtils.GetFieldValue(actionBar, "mActionView");
        testIfNull(mActionView, "get mActionView failed");

        FrameLayout mSplitView = (FrameLayout) SmartBarUtils.GetFieldValue(mActionView, "mSplitView");
        testIfNull(mSplitView, "get mSplitView failed");
        mSplitView.setVisibility(View.VISIBLE);

        for (int i = 0; i < mSplitView.getChildCount(); i++) {
            mSplitView.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }
    public static void setActionBarTabsShowAtBottom(ActionBar actionbar, boolean showAtBottom) {
        try {
            Method method = Class.forName("android.app.ActionBar").getMethod(
                    "setTabsShowAtBottom", new Class[] { boolean.class });
            try {
                method.invoke(actionbar, showAtBottom);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
