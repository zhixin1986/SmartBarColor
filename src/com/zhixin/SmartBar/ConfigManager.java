package com.zhixin.SmartBar;
import android.graphics.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by ashqal on 14-8-9.
 */
public class ConfigManager
{
    private static ConfigManager instance;
    public static  String DEF_MODE_NAME="zx.smartbar.mode";
    public static ConfigManager Instance()
    {
        if ( instance == null )
            instance = new ConfigManager();
        return instance;
    }
    private Properties mProp;
    private File mFile;
    private ConfigManager()
    {
        mProp = new Properties();
        File path = new File("/mnt/sdcard/smartcolor");
        if (!path.exists() )
        {
            path.mkdir();
        }
        mFile = new File(path.getAbsoluteFile() + "/config.xml");
        if (!mFile.exists())
        {
            try {
                mFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean load()
    {
        try
        {
            mProp.clear();
            mProp.load(new FileInputStream(mFile));
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    public boolean save() {
        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream(mFile, false);
            mProp.store(fileOutputStream,"");
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    public String  getString(String name,String defaultValue){
        return   mProp.getProperty(name,defaultValue);
    }
    public void clear(){
        mProp.clear();
    }
    public  boolean exist(String name){
        return  mProp.getProperty(name)!=null;
    }
    public int getColor(String name,int defaultValue){
        String value=mProp.getProperty(name);
        if(value!=null){
            if(value.indexOf("0x")==0 || value.indexOf("0X")==0){
                value=value.substring(2);
                value="#"+value;
            }
            try {
                return Color.parseColor(value);
            }
            catch (Exception ex){

            }
        }
        return  defaultValue;
    }
    public void put(String name,Object value){
        mProp.setProperty(name,String.valueOf(value));
    }
    public int  getInt(String name,int defaultValue){
        String value=  mProp.getProperty(name);
        if (value!=null){
            try {
                return Integer.parseInt(value);
            }
            catch (Exception ex){
            }
        }
        return  defaultValue;
    }
    public float getFloat(String name,float defaultValue){
        String value=  mProp.getProperty(name);
        if (value!=null){
            try {
                return Float.parseFloat(value );
            }
            catch (Exception ex){
            }
        }
        return  defaultValue;
    }
}