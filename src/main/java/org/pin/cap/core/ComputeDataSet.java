package org.pin.cap.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.CapDocument;
import org.pin.DataSetColumnType;
import org.pin.RangeType;
import org.pin.SourceDataColumnType;
import org.pin.cap.cmdui.ProgressBar;
import org.pin.cap.db.DBBase;
import org.pin.cap.generate.CreateDataSetTBSQL;
import org.pin.cap.generate.IGenerate;
import org.pin.cap.generate.InsertBatchDataSetSQL;
import org.pin.cap.js.CapJSRun;
import org.pin.cap.utils.CapUitls;
import sun.org.mozilla.javascript.internal.NativeArray;
import java.util.*;


/**
 * ComputeDataSet.
 */
public class ComputeDataSet extends Thread {

    private static final Log logger  = LogFactory.getLog(ComputeDataSet.class);


    private CapDocument.Cap cap;
    private ProgressBar bar;

    public ComputeDataSet(CapDocument.Cap cap, ProgressBar bar){
        this.cap = cap;
        this.bar = bar;
    }

    public void run() {
        String schemaName = cap.getTargetName();
        String tableName = cap.getDataSet().getSourceName()+"_"+cap.getDataSet().getTrend().getTable().getExtension();
        String sourceTableName =cap.getDataSet().getSourceName()+"_"+cap.getSourceData().getTable().getExtension();
        String orderColumn = cap.getDataSet().getDatetime().getColumn();
        int orderColumnindex = cap.getDataSet().getDatetime().getIndex().intValue();

        String from = cap.getDataSet().getDatetime().getFrom();
        String to = cap.getDataSet().getDatetime().getTo();
        IGenerate igenerate;
        DBBase dbBase = DBBase.getInstance();
        //建表
        bar.tick(1d, null);
        if(!dbBase.existsTable(tableName,schemaName)){
            igenerate = new CreateDataSetTBSQL(cap,tableName);
            dbBase.createDataSetTable(igenerate.generateSQL());
        }else{
            dbBase.deleteDataSetTable("delete from "+schemaName+"."+tableName+" where "+orderColumn+">= timestamp '"+from+"'  and "+orderColumn+" <= timestamp '"+to+"'");
        }
        bar.tick(2d, null);
        String sql = "select * from "+schemaName+"."+sourceTableName+" where "+orderColumn+">= timestamp '"+from+"'  and "+orderColumn+" <= timestamp '"+to+"'  order by "+orderColumn+" asc";
        //String tsql = ;

        logger.info("sql:" + sql);
        List<Object[]> arraySourceList =  dbBase.query(sql);
        List<Object[]> arrayBGSourceList;
        List<Object[]> arraySetSourceList;
        bar.tick(3d, null);
        double onetick = 90d/arraySourceList.size();
        CapJSRun capJSRun = new CapJSRun();
        RangeType maxRangeType = cap.getDataSet().getTrend().getRanges().getRangeArray(cap.getDataSet().getTrend().getRanges().sizeOfRangeArray() - 1);
        int maxRange = Integer.parseInt(maxRangeType.getStringValue());
        int[] ranges = new int[cap.getDataSet().getTrend().getRanges().sizeOfRangeArray()];
        for(int i=0;i<ranges.length;i++){//compute ranges
            ranges[i] = Integer.parseInt(cap.getDataSet().getTrend().getRanges().getRangeArray(i).getStringValue());
        }
        int arraySourceListSize = arraySourceList.size();
        String endDT;
        int endcount;

        RangeType[] rts = CapUitls.getRangeTypes(cap);
        DataSetColumnType[] getDataSetColumnTypes = CapUitls.getDataSetColumnTypes(cap);

        SourceDataColumnType[] SourceDataColumnTypes = CapUitls.getSourceTableColumns(cap);

        Object params[][] = new Object[arraySourceListSize][SourceDataColumnTypes.length+3+rts.length*getDataSetColumnTypes.length+1];
        Object[] sourceData;
        for(int i=0;i<arraySourceListSize;i++){
            sourceData = arraySourceList.get(i);
            arraySetSourceList = new ArrayList<>();
            for(int j=0;j<maxRange;j++){
                if(i+j>arraySourceListSize-1){
                    break;
                }else {
                    arraySetSourceList.add(arraySourceList.get(i+j));
                }
            }
            endcount = maxRange - arraySetSourceList.size();
            if(endcount>0){
                endDT = arraySetSourceList.get(arraySetSourceList.size()-1)[orderColumnindex].toString();
                arrayBGSourceList = dbBase.query("select * from "+schemaName+"."+sourceTableName+" where "+orderColumn+"> timestamp '"+endDT+"' order by "+orderColumn+" asc LIMIT "+endcount+"");
                arraySetSourceList.addAll(arrayBGSourceList);
            }
            NativeArray nativeArray = capJSRun.executeDataSetJS(
                    cap.getDataSet().getComputeJsPath(),
                    cap.getDataSet().getComputeJsMethod(),
                    arraySetSourceList,
                    ranges
            );

            for(int j=0;j<params[i].length+1;j++){
                if(j==0){
                    params[i][j] = UUID.randomUUID().toString().replaceAll("-", "");
                }else {
                    if(j>SourceDataColumnTypes.length){
                       CapUitls.toValue(i,j,params,nativeArray,cap);
                       break;
                    }else{
                        params[i][j] = sourceData[j];
                    }
                }
            }
            bar.tick(onetick, null);
        }
        InsertBatchDataSetSQL ibdss = new InsertBatchDataSetSQL(cap,tableName);

        dbBase.insertBatchDataSet(ibdss.generateSQL(),params);

        bar.tick(3d,null);
    }
}
