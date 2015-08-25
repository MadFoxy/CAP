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
import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * Created by lee5hx on 2015/6/9.
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
        double loadTick =  tickCount/10;
        String insertSql;
        for (Iterator iterator = sourceFiles.iterator(); iterator.hasNext();) {
            sourceFile  = (File) iterator.next();
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
            }finally {

            }

            bar.tick(loadTick,null);

            int linesCount =lines.size();
            SourceDataColumnType[] sdcts = CapUitls.getSourceTableColumns(cap);
            int columnCounnt = sdcts.length;
            logger.info("linesCount:"+linesCount);
            logger.info("columnCounnt:"+columnCounnt);
            Object params[][] = new Object[linesCount][columnCounnt+1];
            String[] values;
            for(int i=0;i<linesCount;i++){
                //capConf.getProperty("cap.load.data.source.file.del")
                values = lines.get(i).split(cap.getSourceData().getImportFile().getDel(), -1);
                for(int j=0;j<columnCounnt+1;j++){
                    if(j==0){
                        params[i][j] = UUID.randomUUID().toString().replaceAll("-", "");
                    }else {
                        //capConf.getProperty("cap.load.data.source.column."+j)
                        params[i][j] = CapUitls.getValue(sdcts[j-1],values[j-1]);
                    }
                }
                bar.tick((loadTick*7)/linesCount,null);

            }
            dbBase.insertBatchSourceTable(insertSql, params);
            bar.tick(loadTick, null);
            try {
                FileUtils.copyFileToDirectory(sourceFile, sbPath);
                FileUtils.forceDelete(sourceFile);
            }catch (IOException e){
                e.printStackTrace();
            }

            bar.tick(loadTick,null);

           // bar.tick(tickCount, "");
        }
    }
}
