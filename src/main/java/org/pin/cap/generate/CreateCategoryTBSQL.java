package org.pin.cap.generate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Properties;

/**
 * Created by lee5hx on 15-6-10.
 */
public class CreateCategoryTBSQL implements IGenerate {


    private static final Log logger  = LogFactory.getLog(CreateCategoryTBSQL.class);

    private StringBuffer sqlbuf;
    private String schemaName;
    private String tableName;
    private char[] capOrder;
    private Properties cap_properties;

    private void init(Properties cap_properties){
        this.cap_properties = cap_properties;
        schemaName = cap_properties.getProperty("cap.targetName");
        capOrder = cap_properties.getProperty("cap.order").toCharArray();
        tableName = cap_properties.getProperty("cap.category.table.name");

    }

    public CreateCategoryTBSQL(Properties cap_properties){
        this.init(cap_properties);
    }



    @Override
    public String generateSQL() {
        sqlbuf = new StringBuffer("CREATE TABLE "+schemaName+"."+tableName+"(Condition_UUID varchar(32),Comb_Order varchar("+capOrder.length+")");
        int i = 1;
        String tempStr = cap_properties.getProperty("cap.category."+i+".col");
        String[] tempArr;
        while (tempStr!=null){
            //  System.out.println(tempStr);
            tempArr = tempStr.split("\\|");
            ;
            sqlbuf.append(","+tempArr[0]+" varchar("+tempArr[1]+")");

            i++;
            tempStr = cap_properties.getProperty("cap.category."+i+".col");
        }
        sqlbuf.append(",PRIMARY KEY(Condition_UUID));");
        logger.debug("CategoryList CreateTable:"+sqlbuf.toString());
        return sqlbuf.toString();
    }
}
