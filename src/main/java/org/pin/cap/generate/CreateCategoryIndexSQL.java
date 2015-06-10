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



    private void init(Properties cap_properties){
        schemaName = cap_properties.getProperty("cap.targetName");
    }

    public CreateCategoryIndexSQL(Properties cap_properties){

        this.init(cap_properties);
    }



    @Override
    public String generateSQL() {
        sqlbuf = new StringBuffer("CREATE INDEX categoryList_CombOrder_index ON "+schemaName+".CategoryList(Comb_Order);");

        logger.debug("CategoryList CreateIndex:"+sqlbuf.toString());
        return sqlbuf.toString();
    }
}
