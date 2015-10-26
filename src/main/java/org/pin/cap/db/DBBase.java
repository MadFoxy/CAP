package org.pin.cap.db;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.DelegatingConnection;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.CapDocument;
import org.pin.CategoryListColumnType;
import org.pin.cap.utils.CapUitls;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import javax.sql.DataSource;
import java.io.FileInputStream;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
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


    public static DBBase getInstance() {
        return dbBase;
    }

    public void createSchema(String schemaName){
        logger.info("正在创建schema[" + schemaName+"]");
        String sql = "CREATE SCHEMA "+schemaName+";";
        try {
            logger.info(sql);
            run.update(sql);

        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            System.exit(109);
        }
        logger.info("创建schema["+schemaName+"]完成!");
    }

    public boolean existsTable(String table,String schema){
        boolean rt = false;
        try {
            DatabaseMetaData metaData = run.getDataSource().getConnection().getMetaData();
            //table.toLowerCase()
            //System.out.println(table);

            // System.out.println(schema);
            //System.out.println(table);
            ResultSet rs = metaData.getTables(run.getDataSource().getConnection().getCatalog(), schema.toLowerCase(), table.toLowerCase(), new String[]{"TABLE"});
            while(rs.next()) {
               // System.out.println(rs.getString(3));
                rt = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return rt;
    }
    public void dropSchema(String schemaName){
        logger.info("正在删除schema["+schemaName+"]及schema下所有的对象");
        String sql = "DROP SCHEMA "+schemaName+" CASCADE;";
        try {
            logger.info(sql);
            run.update(sql);

        } catch (SQLException e) {
            //e.printStackTrace();
            logger.error(e.getMessage());
        }
        logger.info("删除schema["+schemaName+"]完成!");
    }
    public void dropCategoryListTable(String sql){
        logger.info("正在执行dropCategoryListTable.");
        //logger.info("rt["+i+"]:"+rt[i]);
        try {

            run.update(sql);
        } catch (SQLException e) {
            //e.printStackTrace();
            System.exit(610);
            logger.error(e.getMessage());

        }
        logger.info("执行dropCategoryListTable完成!");
    }

    public void deleteDataSetTable(String sql){
        logger.info("正在执行deleteDataSetTable.");
        //logger.info("rt["+i+"]:"+rt[i]);
        try {
            //System.out.println("sql:"+sql);
            logger.info("sql:"+sql);
            run.update(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(610);
            logger.error(e.getMessage());

        }
        logger.info("执行deleteDataSetTable完成!");
    }


    public void createCategoryListTable(String sql){
        logger.info("正在创建CategoryListTable.");
        //logger.info("rt["+i+"]:"+rt[i]);
        try {

            run.update(sql);
        } catch (SQLException e) {
            //e.printStackTrace();
            System.exit(110);
            logger.error(e.getMessage());

        }
        logger.info("创建CategoryListTable完成!");
    }

    public void createSourceTable(String sql){
        logger.info("正在创建SourceTable.");
        //logger.info("rt["+i+"]:"+rt[i]);
        try {
            run.update(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(210);
            logger.error(e.getMessage());

        }
        logger.info("创建SourceTable完成!");
    }

    public void createDataSetTable(String sql){
        logger.info("正在创建DataSetTable.");
        //logger.info("rt["+i+"]:"+rt[i]);
        try {
            run.update(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(610);
            logger.error(e.getMessage());

        }
        logger.info("创建DataSetTable完成!");
    }

    public void insertBatchSourceTable(String sql,Object[][] parms){
        logger.info("正在执行 insert SourceTable.");
        try {

            run.batch(sql, parms);
        } catch (SQLException e) {
            //e.printStackTrace();
            e.getNextException().getNextException().printStackTrace();
            //System.out.println();
            logger.error(e.getMessage());
            System.exit(220);
        }
        logger.info("执行 insert SourceTable完成!");
    }

    public void insertBatchDataSet(String sql,Object[][] parms){
        logger.info("正在执行 insert DataSet.");
        try {
            run.batch(sql, parms);
        } catch (SQLException e) {
            e.printStackTrace();
            e.getNextException().printStackTrace();
            e.getNextException().getNextException().printStackTrace();
            //System.out.println();
            logger.error(e.getMessage());
            System.exit(280);
        }
        logger.info("执行 insert DataSet完成!");
    }


    public String queryCategoryID(String sql,Object[] parms){
        String uuid = null;
        try {
            logger.debug("正在查询" + sql);
            uuid = run.query(sql,
                    new ResultSetHandler<String>() {
                        public String handle(ResultSet rs) throws SQLException {
                        String result = null;
                        if (rs.next()) {
                            result = rs.getString(1);
                        }
                        return result;
                        }
                    },
                    parms);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            System.exit(266);
        }
        return uuid;
    }
        //MapListHandler
    public List<Map<String,Object>> queryMap(String sql){
        List<Map<String,Object>> arrayList = null;
        try {
            logger.info("正在查询"+sql);
            arrayList = run.query(sql, new MapListHandler());
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            System.exit(291);
        }
        return  arrayList;
    }
    public List<Object[]> query(String sql){
        List<Object[]> arrayList = null;
        try {
            logger.info("正在查询"+sql);
            arrayList = run.query(sql, new ArrayListHandler());
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            System.exit(260);
        }
        return  arrayList;
    }
    public void createCategoryListTableIndex(String sql){
        logger.info("正在创建CategoryListTable-Index.");
        //logger.info("rt["+i+"]:"+rt[i]);
        try {
            //int [] rt =run.batch("CREATE SCHEMA1 "+schemaName+";",n);
            logger.info("CategoryList CreateIndex:"+sql);
            run.update(sql);
        } catch (SQLException e) {
            //e.printStackTrace();
            logger.error(e.getMessage());
            System.exit(111);

        }
        logger.info("创建CategoryListTable-Index完成.");
    }
    public void copyInsertCategoryListTable(String schemaName, String tableName,CapDocument.Cap cap) {
        String insertFile = "../tmp/" + schemaName + "." + tableName+".cvs";
        String pkType = cap.getPrimaryKeyGenStrategy();
        logger.info("正在将"+insertFile+",以Copy_Insert方式批量插入.");
        FileInputStream fileInputStream = null;
        String sql;
        if(pkType.toLowerCase().equals("UUID".toLowerCase())){
            sql = "COPY " + schemaName + "." + tableName + "  FROM STDIN WITH DELIMITER ','";
        }else {
            sql = "COPY " + schemaName + "." + tableName +"(";
            CategoryListColumnType[] columnArray = CapUitls.getCategoryListColumns(cap);

            sql = sql+cap.getCategoryList().getOrderColumn();

           // CategoryListColumnType clct;
            for(int i=0;i<columnArray.length;i++){
                sql = sql+","+columnArray[i].getName();

            }
            sql = sql+") FROM STDIN WITH DELIMITER ','";

        }
        try {
            DataSource ds1 = dataSource;
            DelegatingConnection del = new DelegatingConnection(ds1.getConnection());
            PGConnection con = (PGConnection)del.getInnermostDelegate();
            CopyManager copyManager = new CopyManager((BaseConnection)con);
            fileInputStream = new FileInputStream(insertFile);
            logger.info(sql);
            copyManager.copyIn(sql, fileInputStream);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(112);

        }finally {
            IOUtils.closeQuietly(fileInputStream);
        }
        logger.info(insertFile+",以Copy_Insert批量插入完成!");
    }
    public static DataSource setupDataSource(Properties dbConf) {
        BasicDataSource ds = new BasicDataSource();
        //驱动
        ds.setDriverClassName(dbConf.getProperty("cap.ds.driverClassName"));
        logger.info("cap.ds.driverClassName:"+dbConf.getProperty("cap.ds.driverClassName"));
        //用户名
        ds.setUsername(dbConf.getProperty("cap.ds.username"));
        logger.info("cap.ds.username:"+dbConf.getProperty("cap.ds.username"));
        //密码
        ds.setPassword(dbConf.getProperty("cap.ds.password"));
        logger.info("cap.ds.password:************");
        //地址
        ds.setUrl(dbConf.getProperty("cap.ds.url"));
        logger.info("cap.ds.url:"+dbConf.getProperty("cap.ds.url"));

        ds.setInitialSize(Integer.parseInt(dbConf.getProperty("cap.ds.initialSize")));
        logger.info("cap.ds.initialSize:"+dbConf.getProperty("cap.ds.initialSize"));

        ds.setMaxTotal(Integer.parseInt(dbConf.getProperty("cap.ds.maxtotal")));
        logger.info("cap.ds.maxtotal:"+dbConf.getProperty("cap.ds.maxtotal"));

        ds.setMaxIdle(Integer.parseInt(dbConf.getProperty("cap.ds.maxIdle")));
        logger.info("cap.ds.maxIdle:"+dbConf.getProperty("cap.ds.maxIdle"));

        ds.setMaxWaitMillis(Integer.parseInt(dbConf.getProperty("cap.ds.maxWaitMillis")));
        logger.info("cap.ds.maxWaitMillis:"+dbConf.getProperty("cap.ds.maxWaitMillis"));

        ds.setMinIdle(Integer.parseInt(dbConf.getProperty("cap.ds.minIdle")));
        logger.info("cap.ds.minIdle:"+dbConf.getProperty("cap.ds.minIdle"));

        return ds;
    }
}
