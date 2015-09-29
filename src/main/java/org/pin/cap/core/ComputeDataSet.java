package org.pin.cap.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.*;
import org.pin.cap.cmdui.ProgressBar;
import org.pin.cap.db.DBBase;
import org.pin.cap.generate.CreateDataSetTBSQL;
import org.pin.cap.generate.IGenerate;
import org.pin.cap.generate.InsertBatchDataSetSQL;
import org.pin.cap.generate.SelectOrderUUIDSQL;
import org.pin.cap.js.CapJS;
import org.pin.cap.utils.CapUitls;
import sun.org.mozilla.javascript.internal.NativeArray;
import sun.org.mozilla.javascript.internal.NativeObject;

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
        //int orderColumnindex = cap.getDataSet().getDatetime().getIndex().intValue();

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
        List<Map<String,Object>> arraySourceList =  dbBase.queryMap(sql);
        List<Map<String,Object>> arrayBGSourceList;
        List<Map<String,Object>> arraySetSourceList;
        bar.tick(3d, null);
        double onetick = 90d/arraySourceList.size();
        CapJS capjs = new CapJS();
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
        Map<String,Object> sourceData;
        int xn=0;
        NativeObject object;
        for(int i=0;i<arraySourceListSize;i++){
            sourceData = arraySourceList.get(i);
            arraySetSourceList = new ArrayList<>();
            for(int j=1;j<=maxRange;j++){
                if(i+j>arraySourceListSize-1){
                    break;
                }else {
                    arraySetSourceList.add(arraySourceList.get(i+j));
                }
            }
            endcount = maxRange - arraySetSourceList.size();
            //System.out.println(arraySetSourceList.size());
            if(endcount>0){
                if(endcount==maxRange){
                    //endDT = sourceData[orderColumnindex].toString();
                    endDT = sourceData.get(orderColumn).toString();
                }else{
                    endDT = arraySetSourceList.get(arraySetSourceList.size()-1).get(orderColumn).toString();
                }
                arrayBGSourceList = dbBase.queryMap("select * from "+schemaName+"."+sourceTableName+" where "+orderColumn+"> timestamp '"+endDT+"' order by " + orderColumn+" asc LIMIT "+endcount+"");
                arraySetSourceList.addAll(arrayBGSourceList);
            }
            NativeArray nativeArray = capjs.executeDataSetJS(
                    cap.getDataSet().getComputeJsPath(),
                    cap.getDataSet().getComputeJsMethod(),
                    sourceData,
                    arraySetSourceList,
                    ranges
            );
            //赋值params
            params[i][xn++] = UUID.randomUUID().toString().replaceAll("-", "");//替换UUID
            Set<Map.Entry<String, Object>> set = sourceData.entrySet();
            Iterator it = set.iterator();
            Map.Entry<String, Object> entry;
            //将map对象里面的属性循环遍历出来
            while(it.hasNext()){//赋值DataSorce
                //it.next();
                if(xn==1){
                    it.next();
                }
                entry = (Map.Entry<String, Object>) it.next();
                params[i][xn++] = entry.getValue();
            }

            if(xn>SourceDataColumnTypes.length){

                CategoryListColumnType[] columnArray = CapUitls.getCategoryListColumns(cap);
                Object[] ps = new Object[columnArray.length+1];
                ps[0] = sourceData.get("comb_order");
                //capjs.swapParams(i, xn, params, nativeArray, cap);
                for(int k=1;k<ps.length;k++){
                    ps[k] = sourceData.get(columnArray[k-1].getName());
                    System.out.println(ps[k]);
                }
                params[i][xn++] = "Adj_K_Power";
                params[i][xn++] = "Adj_K_Ratio";
                params[i][xn++] = dbBase.queryUUID(new SelectOrderUUIDSQL(cap).generateSQL(),ps);
                for(int x=0;x<nativeArray.size();x++){
                    object = (NativeObject)nativeArray.get(x);
                    for(int y=0;y<getDataSetColumnTypes.length;y++){
                        // NATIVE
                        //System.out.println((int)object.get(getDataSetColumnTypes[y].getStringValue()));
                        params[i][xn++] = CapUitls.getValue(getDataSetColumnTypes[y], object.get(getDataSetColumnTypes[y].getStringValue()).toString());
                       // xn++;
                    }
                }
                xn=0;
            }
            bar.tick(onetick, null);
        }
        InsertBatchDataSetSQL ibdss = new InsertBatchDataSetSQL(cap,tableName);
        dbBase.insertBatchDataSet(ibdss.generateSQL(),params);
        bar.tick(3d,null);
    }
}