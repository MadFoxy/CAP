package org.pin.cap.generate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.CapDocument;
import org.pin.CategoryListColumnType;
import org.pin.cap.cmdui.ProgressBar;
import org.pin.cap.utils.CapNormList;
import org.pin.cap.utils.CapUitls;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * InsertCategorySQL by lee5hx on 15-6-3.
 */
public class InsertCategorySQL implements IGenerate {


    private static final Log logger  = LogFactory.getLog(InsertCategorySQL.class);
    private double count = 0;
    private List<String[]> aNList = new ArrayList();
    private char[] order;
   // private String table;
    private File file;
    private double sumcount = 1;
    private double _lastCount = 0;
    private double _currentCount = 0;
    private double sCount=0;
    private ProgressBar bar;
    private String pkgs;

    private void init(CapDocument.Cap cap){
        pkgs = cap.getPrimaryKeyGenStrategy();
        aNList = new ArrayList();
        //cap_properties.getProperty("cap.order").toCharArray();
        order = cap.getCategoryList().getOrder().toCharArray();
        for(int j = 1; j<=order.length;j++){
            sumcount = sumcount*j;
        }
        String[] tempSlipStr;

        CategoryListColumnType[] columnArray = CapUitls.getCategoryListColumns(cap);
        for(CategoryListColumnType clct :columnArray){
            // = clct.getStringValue().split("\\|");
            tempSlipStr=clct.getStringValue().split(",");
            aNList.add(tempSlipStr);
            sumcount = sumcount*tempSlipStr.length;
        }
        //table = cap_properties.getProperty("cap.targetName")+".CategoryList";

        //File outfile = new File(conf.getProperty("leads.insert.savepath") + insertfile);
        //cap_properties.getProperty("cap.category.table.name")
        file = new File("../tmp/"+cap.getTargetName()+"."+cap.getCategoryList().getTable().getName()+".cvs");

        logger.info("category.count:"+sumcount);
        logger.info("cvs.path:"+file.getPath());

    }
    public InsertCategorySQL(CapDocument.Cap cap,ProgressBar bar,double sCount){
        this.bar = bar;
        this.sCount = sCount;
        this.init(cap);
    }

    public String generateSQL(){
        logger.info("正在生成InsertCategory.cvs.");
        try {
            perm(order, 0, order.length - 1);

        }catch (Exception e){
            e.printStackTrace();
        }
        logger.info("生成InsertCategory.cvs完成.");
        return "";
    }
//    private boolean next(int ct[],int gz[]){
//        int x = ct.length-1;//数组长度
//        int y;//归零位数
//        int sum;
//        while (x!=-1){
//            sum = ct[x]+1;
//            if(sum<=gz[x]){//如果下标+1后，小于等于上限值
//                y = x;
//                ct[y] = sum;
//                while(y!=ct.length-1){//如果归零位数，不等于最大位数
//                    y=y+1;
//                    ct[y] = 0;//位数归零操作
//                }
//                return true;
//            }
//            x--;
//        }
//        return false;
//    }
    private void anfunc(String order,List<String[]> aNList) throws IOException{
        CapNormList cnl = new CapNormList(aNList);
        StringBuffer sb = new StringBuffer();
        String[] An;

        while (cnl.hasNext()){
            if(pkgs.toLowerCase().equals("UUID".toLowerCase())){
                sb.append(UUID.randomUUID().toString().replaceAll("-", ""));
                //sb.append("'");
                sb.append(',');
            }
            sb.append(order);
            An = cnl.next();
            for(int i = 0 ; i < aNList.size() ; i++){
                //An = aNList.get(i);
                //System.out.println(An[0]);
                sb.append(",");
                sb.append(An[i]);
            }
            FileUtils.write(file, sb.toString() + System.getProperty("line.separator"), "UTF-8", true);
            //System.out.println(sb.toString());
            sb.delete(0, sb.length());
            count = count+1;
            _lastCount = _currentCount;
            _currentCount = count/sumcount*sCount;
            bar.tick(_currentCount - _lastCount,"");
        }
    }
    private void perm(char[] buf, int start, int end) throws IOException{
        if (start == end) {// 当只要求对数组中一个字母进行全排列时，只要就按该数组输出即可
            anfunc(String.valueOf(buf), aNList);
        } else {// 多个字母全排列
            char temp;
            for (int i = start; i <= end; i++) {
                temp = buf[start];// 交换数组第一个元素与后续的元素
                buf[start] = buf[i];
                buf[i] = temp;
                perm(buf, start + 1, end);// 后续元素递归全排列
                temp = buf[start];// 将交换后的数组还原
                buf[start] = buf[i];
                buf[i] = temp;
            }
        }
    }
}