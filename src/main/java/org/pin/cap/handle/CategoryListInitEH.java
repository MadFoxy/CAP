package org.pin.cap.handle;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.CapDocument;
import org.pin.CategoryListColumnType;
import org.pin.cap.cmdui.ProgressBar;
import org.pin.cap.core.CategoryListInit;
import org.pin.cap.db.DBBase;
import org.pin.cap.fork.task.GenInsertCategoryCVS;
import org.pin.cap.generate.CreateCategoryIndexSQL;
import org.pin.cap.generate.CreateCategoryTBSQL;
import org.pin.cap.generate.IGenerate;
import org.pin.cap.generate.InsertCategorySQL;
import org.pin.cap.utils.CapNormList;
import org.pin.cap.utils.CapUitls;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * CategoryListInitEH.
 */
public class CategoryListInitEH extends CAPExecuteHandle {


    private static final Log logger  = LogFactory.getLog(CategoryListInitEH.class);

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


    @Override
    public void execute(long starTime,CapDocument.Cap cap, ProgressBar bar) throws Exception {

        String schemaName = cap.getTargetName();
        String tableName = cap.getCategoryList().getTable().getName();

        bar.tick(1d, "正在清除历史.");
        cleanHistory(schemaName, tableName);
        bar.tick(2d, "正在清除历史完成!");

        DBBase dbBase = DBBase.getInstance();

        bar.tick(1d, "正在删除schema[" + schemaName + "]及schema下所有的对象");
        dbBase.dropSchema(schemaName);
        bar.tick(2d, "删除schema[" + schemaName + "]完成!");

        bar.tick(1d, "正在创建schema[" + schemaName + "]");
        dbBase.createSchema(schemaName);
        bar.tick(2d, "创建schema[" + schemaName + "]完成!");

        bar.tick(1d, "正在创建CategoryListTable");
        IGenerate ig = new CreateCategoryTBSQL(cap);
        dbBase.createCategoryListTable(ig.generateSQL());
        bar.tick(2d, "创建CategoryListTable成功!");


        bar.tick(1d, "正在生成所有组合Order and norm.");
        char[] order = cap.getCategoryList().getOrder().toCharArray();
        List<String> bplist = new ArrayList<>();
        List<String[]> normList = new ArrayList<>();
        perm(bplist, order, 0, order.length - 1);
        norm(normList, cap);
        //System.out.println(bplist.size());
        bar.tick(6d, "正在生成所有组合Order And norm success.");

        Double tickCount = 73d/(bplist.size()*normList.size());
        //System.out.println(tickCount);

        GenInsertCategoryCVS task = new GenInsertCategoryCVS(cap,normList,bplist, 0, bplist.size(),bar,tickCount);
        //task.setScount(76d);

        ForkJoinPool pool = new ForkJoinPool();
        pool.execute(task);
       // pool.awaitTermination(1000, TimeUnit.SECONDS);


        do {
//            System.out.printf("Main: Thread Count: %d\n",
//                    pool.getActiveThreadCount());
//            System.out.printf("Main: Thread Steal: %d\n", pool.getStealCount());
//            System.out.printf("Main: Parallelism: %d\n", pool.getParallelism());
            try {
                TimeUnit.MILLISECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!task.isDone());

        pool.shutdown();

        bar.tick(1d, "正在进行CopyInsert CategoryList.");
        dbBase.copyInsertCategoryListTable(schemaName, tableName);
        bar.tick(2d, "CopyInsert CategoryList 完成.");




        bar.tick(1d, "正在创建CategoryListTable-Index.");
        ig = new CreateCategoryIndexSQL(cap);
        dbBase.createCategoryListTableIndex(ig.generateSQL());
        bar.tick(2d, "创建CategoryListTable-Index完成.");


        long endTime = System.currentTimeMillis();
        long diff =  (endTime - starTime);
        logger.info("diff-time:" + diff);

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");//初始化Formatter的转换格式。
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(diff);
        // System.out.println("111");

        bar.tick(2d, "Run Success!("+hms+")");

    }

    private void perm(List<String> bplist,char[] buf, int start, int end){
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

    private void norm(List<String[]> normList,CapDocument.Cap cap){

        List<String[]> aNList = new ArrayList();
        String[] tempSlipStr;
        CategoryListColumnType[] columnArray = CapUitls.getCategoryListColumns(cap);
        for(CategoryListColumnType clct :columnArray){
            // = clct.getStringValue().split("\\|");
            tempSlipStr=clct.getStringValue().split(",");
            aNList.add(tempSlipStr);
        }

        CapNormList cnl = new CapNormList(aNList);
        while (cnl.hasNext()){
            normList.add(cnl.next());
        }

    }


}
