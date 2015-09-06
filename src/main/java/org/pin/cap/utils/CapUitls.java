package org.pin.cap.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.CapDocument;
import org.pin.SourceDataColumnType;
import org.pin.RangeType;
import org.pin.DataSetColumnType;
import org.pin.CategoryListColumnType;
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

    public static RangeType[]  getRangeTypes(CapDocument.Cap cap){
        return cap.getDataSet().getTrend().getRanges().getRangeArray();
    }

    public static DataSetColumnType[] getDataSetColumnTypes(CapDocument.Cap cap){
        return cap.getDataSet().getTrend().getTable().getColumns().getColumnArray();
    }

    public static CategoryListColumnType[] getCategoryListColumns(CapDocument.Cap cap){
        return cap.getCategoryList().getTable().getColumns().getColumnArray();
    }

//    public static void toValue(int i,int j,Object params[][],NativeArray nativeArray,CapDocument.Cap cap){
//        NativeObject object;
//        DataSetColumnType[] getDataSetColumnTypes = CapUitls.getDataSetColumnTypes(cap);
//        params[i][j++] = "Adj_K_Power";
//        params[i][j++] = "Adj_K_Ratio";
//        params[i][j++] = DBBase.getInstance().getCategoryUUID("");
//        for(int x=0;x<nativeArray.size();x++){
//            object = (NativeObject)nativeArray.get(x);
//            for(int y=0;y<getDataSetColumnTypes.length;y++){
//               // NATIVE
//                //System.out.println((int)object.get(getDataSetColumnTypes[y].getStringValue()));
//                params[i][j] = getValue(getDataSetColumnTypes[y], object.get(getDataSetColumnTypes[y].getStringValue()).toString());
//                j++;
//            }
//        }
//    }

    public static Object getValue(DataSetColumnType dsct,String value) {
        Object rtValue = null;
        // String[] tempArr = sourceColumn.split("\\|",-1);
        //System.out.println(dsct.getName());
        switch (dsct.getToType()) {
            case "integer":
                Double d = Double.parseDouble(value);
                rtValue = d.intValue();
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
                SimpleDateFormat sdf = new SimpleDateFormat(dsct.getDateformat());
                try {
                    rtValue = new java.sql.Timestamp(sdf.parse(value).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }
        return rtValue;
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
        //InputStream fis = null;
        try {
            File file = new File("../conf/"+confFile);
            cap = CapDocument.Factory.parse(file).getCap();
            logger.info("CAP load conf/"+confFile+" to tasks complete.");
        } catch (Exception e) {
            logger.error("CAP load conf/"+confFile+" to tasks Error:"+e);
        }
//        finally{
//            IOUtils.closeQuietly(fis);
//        }
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
