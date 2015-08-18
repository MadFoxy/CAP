package org.pin.cap.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.cap.cmdui.ProgressBar;
import org.pin.cap.db.DBBase;
import javax.sql.DataSource;
import java.util.Properties;



/**
 * Created by lee5hx on 2015/6/9.
 */
public class CategoryListGenc extends Thread {

    private static final Log logger  = LogFactory.getLog(CategoryListGenc.class);

    private Properties dbConf;
    private Properties capConf;
    private ProgressBar bar;
    private Long startTime;

    public CategoryListGenc(Long startTime, Properties dbConf, Properties capConf, ProgressBar bar){
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


    public void run() {
//        String schemaName = capConf.getProperty("cap.targetName");
//        String tableName = capConf.getProperty("cap.category.table.name");

        bar.tick(1d, "正在初始化DataSource.");
        initDBbase();
        bar.tick(3d, "初始化DataSource完成.");
//        DBBase dbBase = DBBase.getInstance();



        bar.tick(95d, null);
    }
}
