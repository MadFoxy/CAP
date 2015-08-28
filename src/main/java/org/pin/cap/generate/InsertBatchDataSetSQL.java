package org.pin.cap.generate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.CapDocument;
import org.pin.DataSetColumnType;
import org.pin.RangeType;
import org.pin.SourceDataColumnType;
import org.pin.cap.utils.CapUitls;

/**
 * Created by lee5hx on 15-8-10.
 */
public class InsertBatchDataSetSQL implements IGenerate {

    private static final Log logger  = LogFactory.getLog(InsertBatchDataSetSQL.class);

    private CapDocument.Cap cap;
    private StringBuffer sqlbuf;
    private String tableName;
    private String schemaName;


    private void init(CapDocument.Cap cap){
        this.cap = cap;
        schemaName = cap.getTargetName();
    }

    public InsertBatchDataSetSQL(CapDocument.Cap cap, String tableName){
        this.tableName = tableName;
        this.init(cap);
    }


    @Override
    public String generateSQL() {
        sqlbuf = new StringBuffer("INSERT INTO "+schemaName+"."+tableName+"(DS_UUID,");
        SourceDataColumnType[] sdcts = CapUitls.getSourceTableColumns(cap);
        for(SourceDataColumnType sdct: sdcts){
            sqlbuf.append(sdct.getStringValue()+",");
        }
        sqlbuf.append("Adj_K_Power,");
        sqlbuf.append("Adj_K_Ratio,");
        sqlbuf.append("Condition_Number,");
        RangeType[] rts = CapUitls.getRangeTypes(cap);
        DataSetColumnType[] getDataSetColumnTypes = CapUitls.getDataSetColumnTypes(cap);
        for(RangeType rt: rts){
            for(DataSetColumnType dsct :getDataSetColumnTypes){
                sqlbuf.append(rt.getId()+ "_"+dsct.getName()+ ",");
            }
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
        for(int j=0;j<(sdcts.length+3+rts.length*getDataSetColumnTypes.length);j++){
            sqlbuf.append("?,");
        }
        sqlbuf.append("?);");
        return sqlbuf.toString();
    }
}
