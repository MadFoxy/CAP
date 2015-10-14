package org.pin.cap.core;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.CapDocument;
import org.pin.SourceDataColumnType;
import org.pin.cap.cmdui.ProgressBar;
import org.pin.cap.db.DBBase;
import org.pin.cap.generate.*;
import org.pin.cap.utils.CapUitls;
import org.pin.cap.utils.SourceColumn;

import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * SourceLoadData Created by lee5hx on 2015/6/9.
 */
public class SourceLoadData extends Thread {

    private static final Log logger  = LogFactory.getLog(SourceLoadData.class);

    private CapDocument.Cap cap;
    private ProgressBar bar;

    public SourceLoadData(CapDocument.Cap cap, ProgressBar bar){
        this.cap = cap;
        this.bar = bar;
    }

    private Collection<File> getSourceFiles(String sourceFile,String extension){
        return FileUtils.listFiles(new File(sourceFile),new String[]{extension},false);
    }

    public void run() {
        String schemaName = cap.getTargetName();
        //capConf.getProperty("cap.load.data.source.file.path");
        String sfPath = cap.getSourceData().getImportFile().getPath();
        //capConf.getProperty("cap.load.data.source.file.extension");
        String sfExtension =  cap.getSourceData().getImportFile().getExtension();
        //capConf.getProperty("cap.load.data.source.file.bak.path")
        File sbPath = new File(cap.getSourceData().getImportFile().getBakPath());
        DBBase dbBase = DBBase.getInstance();
        Collection<File> sourceFiles =  getSourceFiles(sfPath, sfExtension);
        if(sourceFiles.size()==0){
            System.out.println("sourceData file not found!");
            System.exit(-909);
        }
        File sourceFile;
        double tickCount = 99d/sourceFiles.size();
        String tableName;
        IGenerate igenerate;
        double loadTick =  tickCount/20;//拆成20步

        //System.out.println(loadTick);
        String insertSql;
        Iterator iterator = sourceFiles.iterator();

        SourceColumn sc;

        while (iterator.hasNext()) {
            sourceFile  = (File) iterator.next();
            sc = new SourceColumn(sourceFile.getName(),cap);
            tableName = CapUitls.getSourceTableName(sourceFile.getName())+"_"+cap.getSourceData().getTable().getExtension();
            if(!dbBase.existsTable(tableName,schemaName)){
                igenerate = new CreateSourceTBSQL(cap,tableName);
                //System.out.println(igenerate.generateSQL());
                dbBase.createSourceTable(igenerate.generateSQL());
            }
            igenerate = new InsertBatchSourceSQL(cap,tableName);
            insertSql = igenerate.generateSQL();
            logger.info(insertSql);
            List<String> lines = null;
            try {
                 lines = FileUtils.readLines(sourceFile, "UTF-8");
            }catch (Exception e){
                e.printStackTrace();
            }
            //bar.tick(loadTick, null);


            int linesCount = lines.size();
            SourceDataColumnType[] sdcts = CapUitls.getSourceTableColumns(cap);
            int columnCounnt = sdcts.length;
            //判断主键生成方式
            String pkgs = cap.getPrimaryKeyGenStrategy();
            int xj = 0;
            if(pkgs.toLowerCase().equals("UUID".toLowerCase())){
                columnCounnt = columnCounnt+1;
                xj = 1;
            }
            logger.info("linesCount:"+linesCount);
            logger.info("columnCounnt:"+columnCounnt);
            Object params[][] = new Object[linesCount][columnCounnt];
            String[] values;
            for(int i=0;i<linesCount;i++){
                values = lines.get(i).split(cap.getSourceData().getImportFile().getDel(), -1);
                for(int j=0;j<columnCounnt;j++){
                    if(pkgs.toLowerCase().equals("UUID".toLowerCase())&&j==0){
                        params[i][j] = UUID.randomUUID().toString().replaceAll("-", "");
                    }else {
                        sc.setColumn(sdcts,values,j-xj);//字段属性，字段行数据，索引定位数据
                        //params[i][j] = CapUitls.getValue(sdcts[j-xj],values[j-xj]);
                        params[i][j] = sc.getValue();
                    }
                }
                bar.tick((loadTick*19)/linesCount,null);
            }
            dbBase.insertBatchSourceTable(insertSql, params);
           // bar.tick(loadTick, null);
            try {
                FileUtils.copyFileToDirectory(sourceFile, sbPath);
                FileUtils.forceDelete(sourceFile);
            }catch (IOException e){
                e.printStackTrace();
            }
            bar.tick(loadTick,null);
        }
    }

}