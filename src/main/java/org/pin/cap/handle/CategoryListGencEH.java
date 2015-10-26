package org.pin.cap.handle;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.CapDocument;
import org.pin.cap.cmdui.ProgressBar;
import org.pin.cap.db.DBBase;
import org.pin.cap.fork.task.GenInsertCategoryCVS;
import org.pin.cap.generate.CreateCategoryIndexSQL;
import org.pin.cap.generate.CreateCategoryTBSQL;
import org.pin.cap.generate.IGenerate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * CategoryListGencEH.
 */
public class CategoryListGencEH extends CAPExecuteHandle {

    private static final Log logger  = LogFactory.getLog(CategoryListInitEH.class);


    @Override
    public void execute(long starTime,CapDocument.Cap cap, ProgressBar bar) throws Exception {
       // new CategoryListGenc(starTime, cap, bar).start();

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

//        bar.tick(1d, "正在生成InsertCategory.cvs.");
//        ig = new InsertCategorySQL(cap,bar,80d);
//        ig.generateSQL();
//        bar.tick(2d, "生成InsertCategory.cvs完成.");



        bar.tick(1d, "正在生成所有组合Order and norm.");
        char[] order = cap.getCategoryList().getOrder().toCharArray();
        List<String> bplist = new ArrayList<>();
        List<String[]> normList = new ArrayList<>();
        perm(bplist, order, 0, order.length - 1);
        norm(normList, cap);
        //System.out.println(bplist.size());
        bar.tick(6d, "正在生成所有组合Order And norm success.");

        Double tickCount = 76d/(bplist.size()*normList.size());
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
        dbBase.copyInsertCategoryListTable(schemaName, tableName,cap);
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

        bar.tick(2d, "Run Success!("+hms+")");



    }
}
