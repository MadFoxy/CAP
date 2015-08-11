package org.pin.cap.utils;

import java.util.Properties;

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
        String tempStr = cap_properties.getProperty("cap.load.data.source.column."+i);
        while (tempStr!=null){
            i++;
            tempStr = cap_properties.getProperty("cap.load.data.source.column."+i);
        }
        return i-1;
    }


}
