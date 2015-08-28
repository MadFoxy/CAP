package org.pin.cap.generate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.CapDocument;
import org.pin.DataSetColumnType;
import org.pin.RangeType;
import org.pin.SourceDataColumnType;
import org.pin.cap.utils.CapUitls;

/**
 * 82575360
 */
public class CreateDataSetTBSQL implements IGenerate {

    private static final Log logger  = LogFactory.getLog(CreateDataSetTBSQL.class);

    private CapDocument.Cap cap;
    private String tableName;
    private String schemaName;


    private void init(CapDocument.Cap cap){
        this.cap = cap;
        schemaName = cap.getTargetName();
    }

    public CreateDataSetTBSQL(CapDocument.Cap cap, String tableName){
        this.tableName = tableName;
        this.init(cap);
    }


    @Override
    public String generateSQL() {
        StringBuffer sqlbuf = new StringBuffer("CREATE TABLE " + schemaName + "." + tableName + "(DS_UUID varchar(32),");
        SourceDataColumnType[] sdcts = CapUitls.getSourceTableColumns(cap);
        for(SourceDataColumnType sdct: sdcts){
            sqlbuf.append(sdct.getStringValue() + " " + sdct.getType() + ",");
        }
        sqlbuf.append("Adj_K_Power varchar(32),");
        sqlbuf.append("Adj_K_Ratio varchar(32),");
        sqlbuf.append("Condition_Number varchar(32),");
        RangeType[] rts = CapUitls.getRangeTypes(cap);
        DataSetColumnType[] getDataSetColumnTypes = CapUitls.getDataSetColumnTypes(cap);
        for(RangeType rt: rts){
            for(DataSetColumnType dsct :getDataSetColumnTypes){
                sqlbuf.append(rt.getId() + "_" + dsct.getName() + " " + dsct.getType() + ",");
            }
        }
        sqlbuf.append("PRIMARY KEY(DS_UUID));");
        logger.info(sqlbuf.toString());
        return sqlbuf.toString();
    }
}
