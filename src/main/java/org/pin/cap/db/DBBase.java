package org.pin.cap.db;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.DelegatingConnection;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import javax.sql.DataSource;
import java.io.FileInputStream;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;


/**
 * DBBase lee5hx on 2015/6/8.
 */
public class DBBase {

    private static final Log logger  = LogFactory.getLog(DBBase.class);

    private static DBBase dbBase;

    private static QueryRunner run;

    private DataSource dataSource;

    public void init() {
        dbBase = this;
        run = new QueryRunner(dataSource);
    }

    public void setDataSource(DataSource ds){
        this.dataSource = ds;
    }

    public DataSource getDataSource(){
        return this.dataSource;
    }

    public static DBBase getInstance() {
        return dbBase;
    }

    public void createSchema(String schemaName){
        logger.info("正在创建schema[" + schemaName+"]");
        String sql = "CREATE SCHEMA "+schemaName+";";
        try {
            logger.debug(sql);
            run.update(sql);

        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            System.exit(109);
        }
        logger.info("创建schema["+schemaName+"]完成!");
    }

    public void dropSchema(String schemaName){
        logger.info("正在删除schema["+schemaName+"]及schema下所有的对象");
        String sql = "DROP SCHEMA "+schemaName+" CASCADE;";
        try {
            logger.debug(sql);
            run.update(sql);

        } catch (SQLException e) {
            //e.printStackTrace();
            logger.error(e.getMessage());
        }
        logger.info("删除schema["+schemaName+"]完成!");
    }
    public void createCategoryListTable(String sql){
        logger.info("正在创建CategoryListTable.");
        //logger.debug("rt["+i+"]:"+rt[i]);
        try {

            run.update(sql);
        } catch (SQLException e) {
            //e.printStackTrace();
            System.exit(110);
            logger.error(e.getMessage());

        }
        logger.info("创建CategoryListTable完成!");
    }

    public void createCategoryListTableIndex(String sql){
        logger.info("正在创建CategoryListTable-Index.");
        //logger.debug("rt["+i+"]:"+rt[i]);
        try {
            //int [] rt =run.batch("CREATE SCHEMA1 "+schemaName+";",n);
            logger.debug("CategoryList CreateIndex:"+sql);
            run.update(sql);
        } catch (SQLException e) {
            //e.printStackTrace();
            logger.error(e.getMessage());
            System.exit(111);

        }
        logger.info("创建CategoryListTable-Index完成.");
    }
    public void copyInsertCategoryListTable(String schemaName, String tableName) {
        String insertFile = "../tmp/" + schemaName + "." + tableName+".cvs";
        logger.info("正在将"+insertFile+",以Copy_Insert方式批量插入.");
        FileInputStream fileInputStream = null;
        String sql = "COPY " + schemaName + "." + tableName + " FROM STDIN WITH DELIMITER ','";
        try {
            DataSource ds1 = dataSource;
            DelegatingConnection del = new DelegatingConnection(ds1.getConnection());
            PGConnection con = (PGConnection)del.getInnermostDelegate();
            CopyManager copyManager = new CopyManager((BaseConnection)con);
            fileInputStream = new FileInputStream(insertFile);
            logger.debug(sql);
            copyManager.copyIn(sql, fileInputStream);
        }catch (Exception e){
            System.exit(112);
            e.printStackTrace();
        }finally {
            IOUtils.closeQuietly(fileInputStream);
        }
        logger.info(insertFile+",以Copy_Insert批量插入完成!");
    }
    public static DataSource setupDataSource(Properties dbConf) {
        BasicDataSource ds = new BasicDataSource();
        //驱动
        ds.setDriverClassName(dbConf.getProperty("cap.ds.driverClassName"));
        logger.debug("cap.ds.driverClassName:"+dbConf.getProperty("cap.ds.driverClassName"));
        //用户名
        ds.setUsername(dbConf.getProperty("cap.ds.username"));
        logger.debug("cap.ds.username:"+dbConf.getProperty("cap.ds.username"));
        //密码
        ds.setPassword(dbConf.getProperty("cap.ds.password"));
        logger.debug("cap.ds.password:************");
        //地址
        ds.setUrl(dbConf.getProperty("cap.ds.url"));
        logger.debug("cap.ds.url:"+dbConf.getProperty("cap.ds.url"));

        ds.setInitialSize(Integer.parseInt(dbConf.getProperty("cap.ds.initialSize")));
        logger.debug("cap.ds.initialSize:"+dbConf.getProperty("cap.ds.initialSize"));

        ds.setMaxTotal(Integer.parseInt(dbConf.getProperty("cap.ds.maxtotal")));
        logger.debug("cap.ds.maxtotal:"+dbConf.getProperty("cap.ds.maxtotal"));

        ds.setMaxIdle(Integer.parseInt(dbConf.getProperty("cap.ds.maxIdle")));
        logger.debug("cap.ds.maxIdle:"+dbConf.getProperty("cap.ds.maxIdle"));

        ds.setMaxWaitMillis(Integer.parseInt(dbConf.getProperty("cap.ds.maxWaitMillis")));
        logger.debug("cap.ds.maxWaitMillis:"+dbConf.getProperty("cap.ds.maxWaitMillis"));

        ds.setMinIdle(Integer.parseInt(dbConf.getProperty("cap.ds.minIdle")));
        logger.debug("cap.ds.minIdle:"+dbConf.getProperty("cap.ds.minIdle"));

        return ds;
    }
}
