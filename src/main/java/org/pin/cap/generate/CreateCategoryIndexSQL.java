package org.pin.cap.generate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.CapDocument;

/**
 *  CreateCategoryIndexSQL Created by lee5hx on 15-6-10.
 */
public class CreateCategoryIndexSQL implements IGenerate {


    private static final Log logger  = LogFactory.getLog(CreateCategoryIndexSQL.class);

    private StringBuffer sqlbuf;
    private String schemaName;
    private String tableName;



    private void init(CapDocument.Cap cap){
        schemaName = cap.getTargetName();
        tableName = cap.getCategoryList().getTable().getName();
    }

    public CreateCategoryIndexSQL(CapDocument.Cap cap){
        this.init(cap);
    }



    @Override
    public String generateSQL() {
        sqlbuf = new StringBuffer("CREATE INDEX "+tableName+"_CombOrder_index ON "+schemaName+"."+tableName+"(Comb_Order);");
        sqlbuf.append("alter table "+schemaName+"."+tableName+" add PRIMARY KEY(Condition_UUID);");
        return sqlbuf.toString();
    }
}
