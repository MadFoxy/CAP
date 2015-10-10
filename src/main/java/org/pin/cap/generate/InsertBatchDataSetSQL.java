package org.pin.cap.generate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.CapDocument;
import org.pin.DataSetColumnType;
import org.pin.RangeType;
import org.pin.SourceDataColumnType;
import org.pin.cap.utils.CapUitls;

/**
 * InsertBatchDataSetSQL Created by lee5hx on 15-8-10.
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
        String pkgs = cap.getPrimaryKeyGenStrategy();
        String endSQL;
        if(pkgs.toLowerCase().equals("UUID".toLowerCase())){
            sqlbuf = new StringBuffer("INSERT INTO "+schemaName+"."+tableName+"(DS_ID,");
            endSQL ="?);";
        }else{
            sqlbuf = new StringBuffer("INSERT INTO "+schemaName+"."+tableName+"(");
            endSQL=");";
        }
        SourceDataColumnType[] sdcts = CapUitls.getSourceTableColumns(cap);
        for(SourceDataColumnType sdct: sdcts){
            sqlbuf.append(sdct.getStringValue()+",");
        }
        sqlbuf.append("Adj_K_Power,");
        sqlbuf.append("Adj_K_Ratio,");
        sqlbuf.append("Condition_ID,");
        RangeType[] rts = CapUitls.getRangeTypes(cap);
        DataSetColumnType[] getDataSetColumnTypes = CapUitls.getDataSetColumnTypes(cap);
        for(RangeType rt: rts){
            for(DataSetColumnType dsct :getDataSetColumnTypes){
                sqlbuf.append(rt.getId()+ "_"+dsct.getName()+ ",");
            }
        }

        sqlbuf = new StringBuffer(sqlbuf.substring(0, sqlbuf.length()-1)+") VALUES (");
        for(int j=0;j<(sdcts.length+3+rts.length*getDataSetColumnTypes.length);j++){
            sqlbuf.append("?,");
        }

        if(endSQL.length()<3){
            sqlbuf = new StringBuffer(sqlbuf.substring(0, sqlbuf.length()-1));
        }

        sqlbuf.append(endSQL);

       // sqlbuf.append("?);");
        return sqlbuf.toString();
    }
}
