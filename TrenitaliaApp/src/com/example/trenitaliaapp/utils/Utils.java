package com.example.trenitaliaapp.utils;

import java.io.File;

import android.os.Environment;
import android.util.Log;

public class Utils
{
    public static final String APP_PATH = "/TrenitaliaAPP";
    
    public static final String TEMP_IMG_PATH = "/temp_images";
    
    public static final String TEMP_IMG_VISO = "/viso_temp.png";
    
    public static final String TEMP_IMG_DOCUMENTO = "/documento_temp.png";
    
    public static void resetTempFolder()
    {
        String path = Environment.getExternalStorageDirectory().toString() + Utils.APP_PATH + Utils.TEMP_IMG_PATH;
        try
        {            
            File visoImg = new File(path + Utils.TEMP_IMG_VISO);            
            if (visoImg.delete())
            {
                Log.v("Delete", visoImg.getName() + " is deleted!");
            }
            else
            {
                Log.v("Delete", "Delete operation is failed.");
            }
            File documentiImg = new File(path + Utils.TEMP_IMG_DOCUMENTO);            
            if (documentiImg.delete())
            {
                Log.v("Delete", documentiImg.getName() + " is deleted!");
            }
            else
            {
                Log.v("Delete", "Delete operation is failed.");
            }
            
        }
        catch (Exception e)
        {            
            e.printStackTrace();            
        }
    }
}
