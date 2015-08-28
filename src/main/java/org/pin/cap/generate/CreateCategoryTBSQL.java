package org.pin.cap.generate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.CapDocument;
import org.pin.CategoryListColumnType;
import org.pin.cap.utils.CapUitls;

/**
 * CreateCategoryTBSQL Created by lee5hx on 15-6-10.
 */
public class CreateCategoryTBSQL implements IGenerate {


    private static final Log logger  = LogFactory.getLog(CreateCategoryTBSQL.class);

    private StringBuffer sqlbuf;
    private String schemaName;
    private String tableName;
    private char[] capOrder;
    private CapDocument.Cap cap;

    private void init(CapDocument.Cap cap){
        this.cap = cap;
        schemaName = cap.getTargetName();
        // cap_properties.getProperty("cap.order").toCharArray()
        capOrder = cap.getCategoryList().getOrder().toCharArray();
        //cap_properties.getProperty("cap.category.table.name")
        tableName = cap.getCategoryList().getTable().getName();

    }

    public CreateCategoryTBSQL(CapDocument.Cap cap){
        this.init(cap);
    }

    @Override
    public String generateSQL() {
        sqlbuf = new StringBuffer("CREATE TABLE "+schemaName+"."+tableName+"(Condition_UUID varchar(32),Comb_Order varchar("+capOrder.length+")");
        CategoryListColumnType[] columnArray = CapUitls.getCategoryListColumns(cap);
        for(CategoryListColumnType clct :columnArray){
            sqlbuf.append(","+clct.getName()+" varchar("+clct.getLength()+")");
        }

//        int i = 1;
//        String tempStr = cap_properties.getProperty("cap.category."+i+".col");
//        String[] tempArr;
//        while (tempStr!=null){
//            //  System.out.println(tempStr);
//            tempArr = tempStr.split("\\|");
//            sqlbuf.append(","+tempArr[0]+" varchar("+tempArr[1]+")");
//            i++;
//            tempStr = cap_properties.getProperty("cap.category."+i+".col");
//        }
        //sqlbuf.append(",PRIMARY KEY(Condition_UUID));");
        sqlbuf.append(");");
        logger.info("CategoryList CreateTable:"+sqlbuf.toString());
        return sqlbuf.toString();
    }
}
