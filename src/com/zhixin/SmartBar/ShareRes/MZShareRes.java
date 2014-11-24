package com.zhixin.SmartBar.ShareRes;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.lang.reflect.Field;
import java.net.PortUnreachableException;

/**
 * Created by zhixin on 2014/8/20.
 */
public class MZShareRes {
    private  static MZShareRes shareRes;
    public  static String mustChangeIcon="mustChangeIcon";
    public  static  String mBackButton="mBackButton";
    public static  String mMenuView="mMenuView";
    public  static MZShareRes  getShareRes(){
        synchronized (mustChangeIcon){
            if(shareRes==null){
                shareRes=new MZShareRes();
            }
            return shareRes;
        }
    }
    public int mz_ic_tab_back_normal_dark=0;
    public int mz_ic_tab_back_normal=0;
    public int mz_ic_tab_more_normal_dark=0;
    public  int mz_ic_tab_more_normal=0;
    public ImageButton shareBackButton;
    public ImageButton shareMenuView;
    public Activity thisActivity;
    public FrameLayout shareActionBar;
    private MZShareRes(){
          try {
              Class intelR=Class.forName("com.android.internal.R$drawable");
              Field[] fs =intelR.getFields();
              for(int i = 0 ; i < fs.length; i++) {
                  String fieldName=fs[i].getName();
                  if(fieldName.equals("mz_ic_tab_back_normal")){
                      mz_ic_tab_back_normal_dark= intelR.getField(fieldName).getInt(null);
                  }
                  if(fieldName.equals("mz_ic_tab_back_dark")){
                      mz_ic_tab_back_normal= intelR.getField(fieldName).getInt(null);
                  }
                  if(fieldName.equals("mz_ic_tab_more_normal")){
                      mz_ic_tab_more_normal_dark= intelR.getField(fieldName).getInt(null);
                  }
                  if(fieldName.equals("mz_ic_tab_more_dark")){
                      mz_ic_tab_more_normal= intelR.getField(fieldName).getInt(null);
                  }
              }
          } catch (ClassNotFoundException e) {
              e.printStackTrace();
          } catch (NoSuchFieldException e) {
              e.printStackTrace();
          } catch (IllegalAccessException e) {
              e.printStackTrace();
          }
      }
}
