package org.pin.cap.core;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.cap.cmdui.ProgressBar;
import org.pin.cap.db.DBBase;
import org.pin.cap.generate.*;
import org.pin.cap.utils.CapUitls;
import javax.sql.DataSource;
import java.io.File;
import java.util.*;


/**
 * Created by lee5hx on 2015/6/9.
 */
public class SourceLoadData extends Thread {

    private static final Log logger  = LogFactory.getLog(SourceLoadData.class);

    private Properties dbConf;
    private Properties capConf;
    private ProgressBar bar;
    private Long startTime;

    public SourceLoadData(Long startTime, Properties dbConf, Properties capConf, ProgressBar bar){
        this.dbConf = dbConf;
        this.capConf = capConf;
        this.bar = bar;
        this.startTime = startTime;
    }

    private void initDBbase(){
        logger.info("正在初始化DataSource.");
        DataSource ds = DBBase.setupDataSource(dbConf);
        DBBase db = new DBBase();
        db.setDataSource(ds);
        db.init();
        logger.info("初始化DataSource完成.");
    }

    private Collection<File> getSourceFiles(String sourceFile,String extension){
        return FileUtils.listFiles(new File(sourceFile),new String[]{extension},false);
    }


    private void cleanHistory(String schemaName,String tableName){
        logger.info("正在清除历史.");
        File file  = new File("../tmp/"+schemaName+"."+tableName+".cvs");
        if(file.exists()){
            try {
                logger.info("force delete:"+file.getPath());
                FileUtils.forceDelete(file);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        logger.info("正在清除历史完成!");
    }

    public void run() {
        String schemaName = capConf.getProperty("cap.targetName");
        String sfPath = capConf.getProperty("cap.load.data.source.file.path");
        String sfExtension =  capConf.getProperty("cap.load.data.source.file.extension");

        bar.tick(1d, "正在初始化DataSource.");
        initDBbase();
        bar.tick(3d, "初始化DataSource完成.");
        DBBase dbBase = DBBase.getInstance();
        Collection<File> sourceFiles =  getSourceFiles(sfPath, sfExtension);
        File sourceFile;
        double tickCount = 95d/sourceFiles.size();
        String tableName;
        IGenerate igenerate;
        double loadTick =  tickCount/5;
        String insertSql;
        for (Iterator iterator = sourceFiles.iterator(); iterator.hasNext();) {
            sourceFile  = (File) iterator.next();
            tableName = CapUitls.getSourceTableName(sourceFile.getName())+"_sdata";
            if(!dbBase.existsTable(tableName,schemaName)){
                igenerate = new CreateSourceTBSQL(capConf,tableName);
                //System.out.println(igenerate.generateSQL());
                dbBase.createSourceTable(igenerate.generateSQL());
            }
            igenerate = new InsertBatchSourceSQL(capConf,tableName);
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
            int columnCounnt = CapUitls.getSourceTableColumnCount(capConf);
            logger.info("linesCount:"+linesCount);
            logger.info("columnCounnt:"+columnCounnt);
            Object params[][] = new Object[linesCount][columnCounnt+1];
            String[] values;
            for(int i=0;i<linesCount;i++){
                values = lines.get(i).split(capConf.getProperty("cap.load.data.source.file.del"), -1);
                for(int j=0;j<columnCounnt+1;j++){
                    if(j==0){
                        params[i][j] = UUID.randomUUID().toString().replaceAll("-", "");
                    }else {
                        params[i][j] = CapUitls.getValue(capConf.getProperty("cap.load.data.source.column."+j),values[j-1]);
                    }
                }
                bar.tick((loadTick*4)/linesCount,null);

            }
            dbBase.insertBatchSourceTable(insertSql,params);
           // bar.tick(tickCount, "");
        }
    }
}
