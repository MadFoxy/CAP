package org.pin.cap.generate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.CapDocument;
import org.pin.SourceDataColumnType;
import org.pin.cap.utils.CapUitls;

/**
 * InsertBatchSourceSQL Created by lee5hx on 15-8-10.
 */
public class InsertBatchSourceSQL implements IGenerate {

    private static final Log logger  = LogFactory.getLog(InsertBatchSourceSQL.class);

    private CapDocument.Cap cap;
    private StringBuffer sqlbuf;
    private String tableName;
    private String schemaName;


    private void init(CapDocument.Cap cap){
        this.cap = cap;
        schemaName = cap.getTargetName();
    }

    public InsertBatchSourceSQL(CapDocument.Cap cap, String tableName){
        this.tableName = tableName;
        this.init(cap);
    }


    @Override
    public String generateSQL() {
        String pkgs = cap.getPrimaryKeyGenStrategy();
        String endSQL;
        if(pkgs.toLowerCase().equals("UUID".toLowerCase())){
            sqlbuf = new StringBuffer("INSERT INTO "+schemaName+"."+tableName+"(SDATA_ID,");
            endSQL ="?);";
        }else{
            sqlbuf = new StringBuffer("INSERT INTO "+schemaName+"."+tableName+"(");
            endSQL=");";
        }
        //sqlbuf = new StringBuffer("INSERT INTO "+schemaName+"."+tableName+"(SDATA_ID,");
        //int i = 0;

        SourceDataColumnType[] sdcts = CapUitls.getSourceTableColumns(cap);

        for(SourceDataColumnType sdct: sdcts){
            // tempArr = tempStr.split("\\|");
            sqlbuf.append(sdct.getStringValue()+",");
        }


//        String tempStr = cap_properties.getProperty("cap.load.data.source.column."+i);
//        String[] tempArr;
//        while (tempStr!=null){
//            //  System.out.println(tempStr);
//            tempArr = tempStr.split("\\|",-1);
//            sqlbuf.append(tempArr[0]+",");
//
//            i++;
//            tempStr = cap_properties.getProperty("cap.load.data.source.column."+i);
//        }



        sqlbuf = new StringBuffer(sqlbuf.substring(0, sqlbuf.length()-1)+") VALUES (");
        for(int j=0;j<sdcts.length;j++){
            sqlbuf.append("?,");
        }
        if(endSQL.length()<3){
            sqlbuf = new StringBuffer(sqlbuf.substring(0, sqlbuf.length()-1));
        }

        sqlbuf.append(endSQL);
        //System.out.println(sqlbuf.toString());
        return sqlbuf.toString();
    }
}
