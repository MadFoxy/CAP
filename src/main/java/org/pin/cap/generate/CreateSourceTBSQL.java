package org.pin.cap.generate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.CapDocument;
import org.pin.SourceDataColumnType;
import org.pin.cap.utils.CapUitls;

/**
 * CreateSourceTBSQL Created by lee5hx on 15-8-10.
 */
public class CreateSourceTBSQL implements IGenerate {

    private static final Log logger  = LogFactory.getLog(CreateSourceTBSQL.class);

    private CapDocument.Cap cap;
    private StringBuffer sqlbuf;
    private String tableName;
    private String schemaName;


    private void init(CapDocument.Cap cap){
        this.cap = cap;
        schemaName = cap.getTargetName();
    }

    public CreateSourceTBSQL(CapDocument.Cap cap,String tableName){
        this.tableName = tableName;
        this.init(cap);
    }


    @Override
    public String generateSQL() {

        String pkgs = cap.getPrimaryKeyGenStrategy();

        if(pkgs.toLowerCase().equals("UUID".toLowerCase())){
            sqlbuf = new StringBuffer("CREATE TABLE "+schemaName+"."+tableName+"(SDATA_ID varchar(32),");
        }else{
            sqlbuf = new StringBuffer("CREATE TABLE "+schemaName+"."+tableName+"(SDATA_ID serial,");
        }
        SourceDataColumnType[] sdcts = CapUitls.getSourceTableColumns(cap);
        for(SourceDataColumnType sdct: sdcts){
            sqlbuf.append(sdct.getStringValue()+" "+sdct.getType()+",");
        }
        sqlbuf.append("PRIMARY KEY(SDATA_ID));");
        logger.info(sqlbuf.toString());
        return sqlbuf.toString();
    }
}
