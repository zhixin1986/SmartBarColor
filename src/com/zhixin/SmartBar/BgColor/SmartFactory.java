package com.zhixin.SmartBar.BgColor;

import android.graphics.Color;
import com.zhixin.SmartBar.R;

/**
 * Created by zhixin on 2014/8/16.
 */
public class SmartFactory {
    public static  CopyActionBarMode copyActionBarMode=new CopyActionBarMode();
    public static NoChangeMode noChangeMode=new NoChangeMode();
    public  static  SingleColorMode whiteColor=new SingleColorMode(0xFFFFFFFF);
    public  static  SingleColorMode garyColor=new SingleColorMode(Color.GRAY);
    public  static  IntellMode intellMode=new IntellMode();
    public  static ISmartBarMode createBarColorMode(SmartOptions mode){
        switch (mode){
            case NOChange:return  noChangeMode;
            case CopyActionBar:return  copyActionBarMode;
            case White:return  whiteColor;
            case Gray:return  garyColor;
            case Intell:return  intellMode;
            default: return  noChangeMode;
        }
    }
    public  static int getValueByMode(SmartOptions mode)
    {
        switch (mode){
            case NOChange:return  0;
            case CopyActionBar:return  1;
            case White:return  2;
            case Gray:return  3;
            case Intell:return  4;
            default: return  0;
        }
    }
    public  static SmartOptions getModeByValue(int mode){
        switch (mode){
            case 0:return SmartOptions.NOChange;
            case 1:return  SmartOptions.CopyActionBar;
            case 2:return SmartOptions.White;
            case 3:return  SmartOptions.Gray;
            case 4:return  SmartOptions.Intell;
            default: return SmartOptions.NOChange;
        }
    }
    public  static int getModeDisplayName(SmartOptions mode){
        switch (mode){
            case NOChange:return R.string.NoChange;
            case CopyActionBar:return R.string.Copy;
            case White:return R.string.White;
            case Gray:return   R.string.Gray;
            case Intell:return R.string.Intell;
            default: return  R.string.NoChange;
        }
    }
}
