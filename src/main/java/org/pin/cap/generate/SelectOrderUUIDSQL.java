package org.pin.cap.generate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.*;
import org.pin.cap.utils.CapUitls;

/**
 * SelectOrderUUIDSQL
 */
public class SelectOrderUUIDSQL implements IGenerate {

    private static final Log logger  = LogFactory.getLog(SelectOrderUUIDSQL.class);

    private CapDocument.Cap cap;
    private String schemaName;
    private String cl_tableName;


    private void init(CapDocument.Cap cap){
        this.cap = cap;
        schemaName = cap.getTargetName();
    }

    public SelectOrderUUIDSQL(CapDocument.Cap cap){
        cl_tableName = cap.getCategoryList().getTable().getName();
        this.init(cap);
    }

    @Override
    public String generateSQL() {
        StringBuffer sqlbuf = new StringBuffer("SELECT a.condition_uuid from "+schemaName+"."+cl_tableName+" a where a.comb_order = ?");
        CategoryListColumnType[] columnArray = CapUitls.getCategoryListColumns(cap);
        for(CategoryListColumnType clct :columnArray){
           // sqlbuf.append(","+clct.getName()+" varchar("+clct.getLength()+")");
            sqlbuf.append(" and a."+clct.getName()+" = ? ");

        }
       // sqlbuf.append(" where a.DS_UUID = ? ");
        logger.info(sqlbuf.toString());
        return sqlbuf.toString();
    }
}
