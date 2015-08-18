package org.pin.cap.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;


public class CapUitls {

    private static final Log logger  = LogFactory.getLog(CapUitls.class);

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
        switch (tempArr[2]) {
            case "integer":
                rtValue = Integer.parseInt(value);
                break;
            case "string":
                rtValue = String.valueOf(value);
                break;
            case "float":
                rtValue = Float.parseFloat(value);
                break;
            case "double":
                rtValue = Double.parseDouble(value);
                break;
            case "datetime":
                SimpleDateFormat sdf = new SimpleDateFormat(tempArr[3]);
                try {
                    //java.sql.Date dateTime = new java.sql.Date(timeDate.getTime());//sql类型
                    //java.sql.Timestamp
                    rtValue = new java.sql.Timestamp(sdf.parse(value).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }
        return rtValue;
    }

    public static Properties loadCapConf(String confFile) {
        Properties prop =null;
        InputStream fis = null;
        try {
            File file = new File("../conf/"+confFile+".properties");
            fis = new FileInputStream(file);
            prop = new Properties();
            prop.load(fis);
            fis.close();
            logger.info("CAP load conf/"+confFile+".properties complete.");
        } catch (Exception e) {
            logger.error("CAP load conf/"+confFile+".properties Error:"+e);
            //e.printStackTrace();
        }
        finally{
            IOUtils.closeQuietly(fis);
        }
        return prop;
    }

    public static Properties loadDBConf() {
        Properties prop = new Properties();
        InputStream fis = null;
        try {
            File file = new File("../conf/db_conf.properties");
            fis = new FileInputStream(file);
            prop.load(fis);
            fis.close();
            logger.info("CAP load conf/db_conf.properties complete.");
        } catch (Exception e) {
            logger.error("CAP load conf/db_conf.properties Error:"+e);
            //e.printStackTrace();
        }
        finally{
            IOUtils.closeQuietly(fis);
        }
        return prop;
    }
}
