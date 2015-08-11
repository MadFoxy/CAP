package org.pin.cap.generate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Properties;

/**
 * Created by lee5hx on 15-8-10.
 */
public class InsertBatchSourceSQL implements IGenerate {

    private static final Log logger  = LogFactory.getLog(InsertBatchSourceSQL.class);

    private Properties cap_properties;
    private StringBuffer sqlbuf;
    private String tableName;
    private String schemaName;


    private void init(Properties cap_properties){
        this.cap_properties = cap_properties;
        schemaName = cap_properties.getProperty("cap.targetName");
    }

    public InsertBatchSourceSQL(Properties cap_properties, String tableName){
        this.tableName = tableName;
        this.init(cap_properties);
    }


    @Override
    public String generateSQL() {
        sqlbuf = new StringBuffer("INSERT INTO "+schemaName+"."+tableName+"(");
        int i = 1;
        String tempStr = cap_properties.getProperty("cap.load.data.source.column."+i);
        String[] tempArr;
        while (tempStr!=null){
            //  System.out.println(tempStr);
            tempArr = tempStr.split("\\|",-1);
            sqlbuf.append(tempArr[0]+",");

            i++;
            tempStr = cap_properties.getProperty("cap.load.data.source.column."+i);
        }
        sqlbuf = new StringBuffer(sqlbuf.substring(0, sqlbuf.length()-1)+") VALUES (");
        for(int j=0;j<i-1;j++){
            sqlbuf.append("?,");
        }
        return sqlbuf.substring(0, sqlbuf.length()-1)+");";
    }
}
