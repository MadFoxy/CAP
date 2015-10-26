package org.pin.cap.handle;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.CapDocument;
import org.pin.CategoryListColumnType;
import org.pin.cap.cmdui.ProgressBar;
import org.pin.cap.cmdui.Type;
import org.pin.cap.db.DBBase;
import org.pin.cap.utils.CapNormList;
import org.pin.cap.utils.CapUitls;
import javax.sql.DataSource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

/**
 *  CapExecuteHandle lee5hx
 */
public abstract class CAPExecuteHandle implements ExecuteHandle {

    private static final Log logger  = LogFactory.getLog(CAPExecuteHandle.class);



    protected void cleanHistory(String schemaName,String tableName){
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



    protected void perm(List<String> bplist,char[] buf, int start, int end){
        if (start == end) {// 当只要求对数组中一个字母进行全排列时，只要就按该数组输出即可
            bplist.add(String.valueOf(buf));
        } else {// 多个字母全排列
            char temp;
            for (int i = start; i <= end; i++) {
                temp = buf[start];// 交换数组第一个元素与后续的元素
                buf[start] = buf[i];
                buf[i] = temp;
                perm(bplist,buf, start + 1, end);// 后续元素递归全排列
                temp = buf[start];// 将交换后的数组还原
                buf[start] = buf[i];
                buf[i] = temp;
            }
        }
    }

    protected void norm(List<String[]> normList,CapDocument.Cap cap){
        List<String[]> aNList = new ArrayList();
        String[] tempSlipStr;
        CategoryListColumnType[] columnArray = CapUitls.getCategoryListColumns(cap);
        for(CategoryListColumnType clct :columnArray){
            // = clct.getStringValue().split("\\|");
            tempSlipStr=clct.getStringValue().split(",");
            aNList.add(tempSlipStr);
        }
        CapNormList cnl = new CapNormList(aNList);
        String dd[];
        while (cnl.hasNext()){
            dd = cnl.next();
            ///System.out.println(dd[0] + "-" + dd[1]+"-"+dd[2] + "-" + dd[3]+"-"+dd[4]+"-"+dd[5]+"-"+dd[6]);
            normList.add(dd.clone());
        }

    }



    public void exc(String args[]) throws Exception{
        logger.info("开始执行:cap " + args[0] + " " + args[1]);
        CapDocument.Cap cap = CapUitls.loadCapConf(args[0]);
        Properties db_properties = CapUitls.loadDBConf();
        ProgressBar bar = new ProgressBar(50, 100, Type.BOTH);
        if (cap != null) {
            long starTime = System.currentTimeMillis();
            this.initDBbase(db_properties);
            this.execute(starTime, cap, bar);
            while (true) {
                Thread.sleep(Long.parseLong(cap.getEnvironment().getThreadSleep()));
                if (bar._currentTick >= 100d) {
                    logger.info("Cap "+args[1]+" 运行成功!(" + getHMS(starTime) + ")");
                    break;
                } else if (bar._currentTick >= 99d||bar._currentTick >= 98d) {
                    String hms = getHMS(starTime);
                    bar.tick(100d - bar._currentTick, "Run Success!(" + hms + ")");
                    logger.info("Cap "+args[1]+" 运行成功!(" + hms + ")");
                    break;
                }
            }
            System.exit(0);
        } else {
            System.out.println(args[0] + " not found!pls check " + args[0] + " weather it exist in this path:conf/");
        }

    }
    private String getHMS(long starTime){
        long endTime = System.currentTimeMillis();
        long diff = (endTime - starTime);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");//初始化Formatter的转换格式。
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return formatter.format(diff);
    }
    private void initDBbase(Properties dbConf){
        logger.info("正在初始化DataSource.");
        DataSource ds = DBBase.setupDataSource(dbConf);
        DBBase db = new DBBase();
        db.setDataSource(ds);
        db.init();
        logger.info("初始化DataSource完成.");
    }
}