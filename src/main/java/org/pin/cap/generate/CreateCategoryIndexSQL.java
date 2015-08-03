package org.pin.cap.generate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.cap.cmdui.ProgressBar;

import java.util.Properties;

/**
 * Created by lee5hx on 15-6-10.
 */
public class CreateCategoryIndexSQL implements IGenerate {


    private static final Log logger  = LogFactory.getLog(CreateCategoryIndexSQL.class);

    private StringBuffer sqlbuf;
    private String schemaName;
    private String tableName;



    private void init(Properties cap_properties){
        schemaName = cap_properties.getProperty("cap.targetName");
        tableName = cap_properties.getProperty("cap.category.table.name");
    }

    public CreateCategoryIndexSQL(Properties cap_properties){

        this.init(cap_properties);
    }



    @Override
    public String generateSQL() {
        sqlbuf = new StringBuffer("CREATE INDEX "+tableName+"_CombOrder_index ON "+schemaName+"."+tableName+"(Comb_Order);");
        sqlbuf.append("alter table "+schemaName+"."+tableName+" add PRIMARY KEY(Condition_UUID);");
        return sqlbuf.toString();
    }
}
