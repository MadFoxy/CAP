package org.pin.cap.utils;

import com.sun.deploy.util.StringUtils;

import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Created by lee5hx on 15-8-8.
 */
public class CapUitls {
    public static String getSourceTableName(String fileName){
        String[] tempStr = fileName.split("_");
        return tempStr[0];
    }
    public static int getSourceTableColumnCount(Properties cap_properties){
        int i = 1;
        String tempStr = cap_properties.getProperty("cap.load.data.source.column." + i);
        while (tempStr!=null){
            i++;
            tempStr = cap_properties.getProperty("cap.load.data.source.column."+i);
        }
        return i-1;
    }


    public static Object getValue(String sourceColumn,String value) {
        Object rtValue = null;
        String[] tempArr = sourceColumn.split("\\|",-1);
        if(tempArr[2].equals("integer")){
            rtValue =  Integer.parseInt(value);
        }else if(tempArr[2].equals("string")){
            rtValue = String.valueOf(value);
        }else if(tempArr[2].equals("float")){
            rtValue = Float.parseFloat(value);
        }else if(tempArr[2].equals("double")){
            rtValue = Double.parseDouble(value);
        }
        return rtValue;
    }

    public static void main(String agrs[]){
        //Integer.
        //System.out.println(CapUitls.isInteger("12333.00000"));
    }
}
