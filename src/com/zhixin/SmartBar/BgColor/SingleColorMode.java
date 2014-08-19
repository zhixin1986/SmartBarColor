package com.zhixin.SmartBar.BgColor;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.*;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.zhixin.SmartBar.SmartBarUtils;

/**
 * Created by zhixin on 2014/8/16.
 */
public class SingleColorMode implements ISmartBarMode {
    private Drawable colorDrawable;
    private int iconColor=Color.BLACK;
    private int backColor;
    private boolean mustChange=false;
    public SingleColorMode(int color){
        backColor=color;
        mustChange=isWhite(color);
        if(mustChange){
            double num=220.0/Color.red(color);
            num=Math.max(num,220.0/Color.green(color));
            num=Math.max(num,220.0/Color.blue(color));
            color=Color.argb(255,(int)(Color.red(color)*num),(int)(Color.green(color)*num),(int)(Color.blue(color)*num));
            colorDrawable=new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[] { backColor,color});
        }
        else
        {
            colorDrawable=new ColorDrawable(backColor);
        }
        iconColor=Color.argb(100,255-Color.red(color),255-Color.green(color),255-Color.green(color));
    }
    @Override
    public boolean mustChnageIcon() {
        return mustChange;
    }
    public static boolean isWhite(int color) {
        return  Color.red(color)>224&&Color.green(color)>224 && Color.blue(color)>224;
    }
    public static void setIconColor(Bitmap bitmap,int backColor)
    {
        int bitW=bitmap.getWidth();
        int bitH=bitmap.getHeight();
        for (int i=0;i<bitW;i++){
            for (int j=0;j<bitH;j++){
                int color=bitmap.getPixel(i,j);
                if (Color.alpha(color)!=0 && isWhite(color)){
                    bitmap.setPixel(i,j,backColor);
                }
            }
        }
    }
    public  static Bitmap HandleBitMap(StateListDrawable stateListDrawable,int backColor)
    {
        Drawable drawable= stateListDrawable.getCurrent();
        if(drawable instanceof  BitmapDrawable ){
            return HandleBitMap(((BitmapDrawable)drawable).getBitmap(),backColor);
        }
       return  null;
    }
    public  static Bitmap HandleBitMap(Bitmap bitmap,int backColor)
    {
        if (bitmap!=null){
            Matrix matrix = new Matrix();
            matrix.postScale(2,2); //长和宽放大缩小的比例
            bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            setIconColor(bitmap,backColor);
        }
        return  bitmap;
    }
    public  static  void changeViewIcon(View view,int backColor){
        try {
            StateListDrawable drawable=(StateListDrawable)view.getBackground();
            Bitmap bitmap= HandleBitMap(drawable,backColor);
            if (bitmap!=null){
                view.setBackground(new BitmapDrawable(bitmap));
            }
        }
        catch (Exception ex){

        }
    }
    public  static  void  setSmartBarIconColor(ActionBar actionBar,ViewGroup mSplitView,int backColor){
        int count= mSplitView.getChildCount();
        boolean isFind=false;
        for(int i=0;i<count;i++){
            View view=  mSplitView.getChildAt(i);
            if(view.getClass().getName().equals("com.meizu.widget.KeyBackButton")){
                changeViewIcon(view,backColor);
                isFind=true;
            }
            if(view instanceof  ViewGroup)  {
                 setSmartBarIconColor(actionBar,(ViewGroup)view,backColor);
            }
            else {
                if(isFind){
                    changeViewIcon(view,backColor);
                }
            }
        }
    }
    public  static  void  setSmartBarIconColor(Activity activity,int backColor)
    {
        try{
            ActionBar actionBar = activity.getActionBar();
            if(actionBar!=null){
                FrameLayout mSplitView = (FrameLayout) SmartBarUtils.GetFieldValue(actionBar, "mSplitView");
                setSmartBarIconColor(actionBar,mSplitView,backColor);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public Drawable getSmartBarDrawable(Activity activity) {
        return colorDrawable;
    }
}
