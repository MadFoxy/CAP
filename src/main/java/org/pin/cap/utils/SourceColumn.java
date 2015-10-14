package org.pin.cap.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.CapDocument;
import org.pin.SourceDataColumnType;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * SourceColumn Created by lee5hx on 2015/10/13.
 */
public class SourceColumn {

    private static final Log logger  = LogFactory.getLog(SourceColumn.class);
    private SourceDataColumnType[] sdct;
    private String[] value;
    private int index;
    private File file;

    public SourceColumn(String sourceName,CapDocument.Cap cap){
        file = new File(cap.getSourceData().getErrorList().getPath()+sourceName+".errorList");
        if(file.exists()){
            try {
                FileUtils.forceDelete(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setColumn(SourceDataColumnType[] sdct,String[] value,int index){
        this.sdct = sdct;
        this.value = value;
        this.index = index;
    }

    private Object convert(){
        Object rtValue = null;
        switch (sdct[index].getToType()) {
            case "integer":
                rtValue = Integer.parseInt(value[index]);
                break;
            case "string":
                rtValue = String.valueOf(value[index]);
                break;
            case "float":
                rtValue = Float.parseFloat(value[index]);
                break;
            case "double":
                rtValue = Double.parseDouble(value[index]);
                break;
            case "datetime":
                SimpleDateFormat sdf = new SimpleDateFormat(sdct[index].getDateformat());
                try {
                    rtValue = new java.sql.Timestamp(sdf.parse(value[index]).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }
        return rtValue;
    }
    private boolean validation(){
        boolean rt = true;
        if(sdct[index].isSetRegex()){
            //logger.debug("Column:"+sdct[index].getStringValue()+" Regex:"+sdct[index].getRegex());
            rt = Pattern.compile(sdct[index].getRegex()).matcher(value[index]).find();
        }
        return  rt;
    }

    public Object getValue(){
        Object rtValue  = null;
        if(validation()){
            rtValue  = convert();
        }else{
            String errorStr = "Column:"+sdct[index].getStringValue()+" Regex:"+sdct[index].getRegex()+" Value:"+value[index]+" validation failure!";
            logger.debug(errorStr);
            try {
                FileUtils.write(file, errorStr + System.getProperty("line.separator"), "UTF-8", true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rtValue;
    }

}
