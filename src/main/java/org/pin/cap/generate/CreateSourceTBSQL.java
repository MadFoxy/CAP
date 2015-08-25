package org.pin.cap.generate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.CapDocument;
import org.pin.SourceDataColumnType;
import org.pin.cap.utils.CapUitls;

/**
 * Created by lee5hx on 15-8-10.
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
        sqlbuf = new StringBuffer("CREATE TABLE "+schemaName+"."+tableName+"(SDATA_UUID varchar(32),");
//        int i = 1;
//        String tempStr = cap_properties.getProperty("cap.load.data.source.column."+i);
//        String[] tempArr;
//        while (tempStr!=null){
//            //  System.out.println(tempStr);
//            tempArr = tempStr.split("\\|");
//            sqlbuf.append(tempArr[0]+" "+tempArr[1]+",");
//
//            i++;
//            tempStr = cap_properties.getProperty("cap.load.data.source.column."+i);
//        }
// cap.getSourceData().getTable().getColumns().getColumnArray();
        SourceDataColumnType[] sdcts = CapUitls.getSourceTableColumns(cap);
        //String[] tempArr;
        for(SourceDataColumnType sdct: sdcts){
           // tempArr = tempStr.split("\\|");
            sqlbuf.append(sdct.getStringValue()+" "+sdct.getType()+",");
        }
        sqlbuf.append("PRIMARY KEY(SDATA_UUID));");
        return sqlbuf.toString();
    }
}
