package org.pin.cap.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.CapDocument;
import org.pin.CategoryListColumnType;
import org.pin.SourceDataColumnType;

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
    public static SourceDataColumnType[]  getSourceTableColumns(CapDocument.Cap cap){
        return cap.getSourceData().getTable().getColumns().getColumnArray();
    }

    public static CategoryListColumnType[] getCategoryListColumns(CapDocument.Cap cap){
        return cap.getCategoryList().getTable().getColumns().getColumnArray();
    }



    public static Object getValue(SourceDataColumnType sdct,String value) {
        Object rtValue = null;
       // String[] tempArr = sourceColumn.split("\\|",-1);
        switch (sdct.getToType()) {
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
                SimpleDateFormat sdf = new SimpleDateFormat(sdct.getDateformat());
                try {
                    rtValue = new java.sql.Timestamp(sdf.parse(value).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }
        return rtValue;
    }

    public static CapDocument.Cap loadCapConf(String confFile) {
        CapDocument.Cap cap =null;
        InputStream fis = null;
        try {
            File file = new File("../conf/"+confFile+".xml");
            cap = CapDocument.Factory.parse(file).getCap();
            logger.info("CAP load conf/"+confFile+".xml to tasks complete.");
        } catch (Exception e) {
            logger.error("CAP load conf/"+confFile+".xml to tasks Error:"+e);
        }
        finally{
            IOUtils.closeQuietly(fis);
        }
        return cap;
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
