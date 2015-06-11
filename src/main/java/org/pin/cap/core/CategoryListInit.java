package org.pin.cap.core;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.cap.cmdui.ProgressBar;
import org.pin.cap.db.DBBase;
import org.pin.cap.generate.CreateCategoryIndexSQL;
import org.pin.cap.generate.CreateCategoryTBSQL;
import org.pin.cap.generate.IGenerate;
import org.pin.cap.generate.InsertCategorySQL;

import javax.sql.DataSource;
import java.io.File;
import java.util.Properties;

/**
 * Created by Jinny on 2015/6/9.
 */
public class CategoryListInit extends Thread {

    private static final Log logger  = LogFactory.getLog(CategoryListInit.class);

    private Properties dbConf;
    private Properties capConf;
    private ProgressBar bar;




    public CategoryListInit(Properties dbConf,Properties capConf,ProgressBar bar){
        this.dbConf = dbConf;
        this.capConf = capConf;
        this.bar = bar;
    }

    private void initDBbase(){
        logger.info("正在初始化DataSource.");
        DataSource ds = DBBase.setupDataSource(dbConf);
        DBBase db = new DBBase();
        db.setDataSource(ds);
        db.init();
        logger.info("初始化DataSource完成.");
    }
    private void cleanHistory(String schemaName,String tableName){
        logger.info("正在清除历史.");
        File file  = new File("../tmp/"+schemaName+"."+tableName+".cvs");
        if(file.exists()){
            try {

                FileUtils.forceDelete(file);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        logger.info("正在清除历史完成!");
    }

    public void run() {
        String schemaName = capConf.getProperty("cap.targetName");
        String tableName = capConf.getProperty("cap.category.table.name");

        bar.tick(1d, "正在清除历史.");
        cleanHistory(schemaName,tableName);
        bar.tick(2d, "正在清除历史完成!");

        bar.tick(1d, "正在初始化DataSource.");
        initDBbase();
        bar.tick(3d, "初始化DataSource完成.");
        DBBase dbBase = DBBase.getInstance();
        bar.tick(1d, "正在删除schema[" + schemaName + "]及schema下所有的对象");
        dbBase.dropSchema(schemaName);
        bar.tick(2d, "删除schema[" + schemaName + "]完成!");

        bar.tick(1d, "正在创建schema[" + schemaName + "]");
        dbBase.createSchema(schemaName);
        bar.tick(2d, "创建schema[" + schemaName + "]完成!");

        bar.tick(1d, "正在创建CategoryListTable");
        IGenerate ig = new CreateCategoryTBSQL(capConf);
        dbBase.createCategoryListTable(ig.generateSQL());
        bar.tick(2d, "创建CategoryListTable成功!");

        bar.tick(1d, "正在创建CategoryListTable-Index.");
        ig = new CreateCategoryIndexSQL(capConf);
        dbBase.createCategoryListTableIndex(ig.generateSQL());
        bar.tick(2d, "创建CategoryListTable-Index完成.");


        bar.tick(1d, "正在生成InsertCategorySQL.");
        ig = new InsertCategorySQL(capConf,bar);
        ig.generateSQL();
        bar.tick(2d, "生成InsertCategorySQL完成.");


        bar.tick(1d, "正在进行CopyInsert CategoryList.");
        dbBase.copyInsertCategoryListTable(schemaName + "." + tableName, "../tmp/" + schemaName + "." + tableName+".cvs");
        bar.tick(4d, "Cap Init CategoryList 运行成功!");
        logger.info("Cap Init CategoryList 运行成功!");

    }
}
