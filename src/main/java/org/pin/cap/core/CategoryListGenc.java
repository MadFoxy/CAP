package org.pin.cap.core;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.CapDocument;
import org.pin.cap.cmdui.ProgressBar;
import org.pin.cap.db.DBBase;
import org.pin.cap.generate.CreateCategoryIndexSQL;
import org.pin.cap.generate.CreateCategoryTBSQL;
import org.pin.cap.generate.IGenerate;
import org.pin.cap.generate.InsertCategorySQL;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


/**
 * CategoryListGenc Created by lee5hx on 2015/6/9.
 */
public class CategoryListGenc extends Thread {

    private static final Log logger  = LogFactory.getLog(CategoryListGenc.class);

    private CapDocument.Cap cap;
    private ProgressBar bar;
    private Long startTime;

    public CategoryListGenc(Long startTime, CapDocument.Cap cap, ProgressBar bar){
        this.cap = cap;
        this.bar = bar;
        this.startTime = startTime;
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
        String schemaName = cap.getTargetName();
        //capConf.getProperty("cap.category.table.name")
        String tableName = cap.getCategoryList().getTable().getName();

        bar.tick(1d, "正在清除历史.");
        cleanHistory(schemaName, tableName);
        bar.tick(2d, "正在清除历史完成!");

        DBBase dbBase = DBBase.getInstance();

        bar.tick(1d, "正在删除CategoryListTable");
        dbBase.dropCategoryListTable("DROP TABLE " + schemaName + "." + tableName + ";");
        bar.tick(2d, "删除CategoryListTable成功!");

        bar.tick(1d, "正在创建CategoryListTable");
        IGenerate ig = new CreateCategoryTBSQL(cap);
        dbBase.createCategoryListTable(ig.generateSQL());
        bar.tick(2d, "创建CategoryListTable成功!");

        bar.tick(1d, "正在生成InsertCategory.cvs.");
        ig = new InsertCategorySQL(cap,bar,80d);
        ig.generateSQL();
        bar.tick(2d, "生成InsertCategory.cvs完成.");



        bar.tick(1d, "正在进行CopyInsert CategoryList.");
        dbBase.copyInsertCategoryListTable(schemaName, tableName);
        bar.tick(2d, "CopyInsert CategoryList 完成.");


        bar.tick(1d, "正在创建CategoryListTable-Index.");
        ig = new CreateCategoryIndexSQL(cap);
        dbBase.createCategoryListTableIndex(ig.generateSQL());
        bar.tick(2d, "创建CategoryListTable-Index完成.");


        long endTime = System.currentTimeMillis();
        long diff =  (endTime - startTime);
        logger.info("diff-time:" + diff);

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");//初始化Formatter的转换格式。
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(diff);

        bar.tick(2d, "Run Success!("+hms+")");

    }
}
