package org.pin.cap.generate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.cap.cmdui.ProgressBar;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by lee5hx on 15-6-3.
 */
public class InsertCategorySQL implements IGenerate {


    private static final Log logger  = LogFactory.getLog(InsertCategorySQL.class);
    private double count = 0;
    private List<String[]> aNList = new ArrayList();
    private char[] order;
    private String table;
    private File file;
    private double sumcount = 1;
    private double _lastCount = 0;
    private double _currentCount = 0;

    private ProgressBar bar;

    private void init(Properties cap_properties){
        aNList = new ArrayList();
        order = cap_properties.getProperty("cap.order").toCharArray();
        for(int j = 1; j<=order.length;j++){
            sumcount = sumcount*j;
        }
        int i = 1;
        String tempStr = cap_properties.getProperty("cap.category."+i+".col");
        String[] tempArr;
        String[] tempSlipStr;
        while (tempStr!=null){
            //  System.out.println(tempStr);
            tempArr = tempStr.split("\\|");
            tempSlipStr=tempArr[2].split(",");
            aNList.add(tempSlipStr);
            sumcount = sumcount*tempSlipStr.length;
            i++;
            tempStr = cap_properties.getProperty("cap.category."+i+".col");
        }

        table = cap_properties.getProperty("cap.targetName")+".CategoryList";

        //File outfile = new File(conf.getProperty("leads.insert.savepath") + insertfile);
        file = new File("../tmp/"+cap_properties.getProperty("cap.targetName")+"."+cap_properties.getProperty("cap.category.table.name")+".cvs");

        logger.info("category.count:"+sumcount);
        logger.info("cvs.path:"+file.getPath());

    }
    public InsertCategorySQL(Properties cap_properties,ProgressBar bar){
        this.bar = bar;
        this.init(cap_properties);
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
    private boolean next(int ct[],int gz[]){
        int x = ct.length-1;//数组长度
        int y;//归零位数
        int sum;
        while (x!=-1){
            sum = ct[x]+1;
            if(sum<=gz[x]){//如果下标+1后，小于等于上限值
                y = x;
                ct[y] = sum;
                while(y!=ct.length-1){//如果归零位数，不等于最大位数
                    y=y+1;
                    ct[y] = 0;//位数归零操作
                }
                return true;
            }
            x--;
        }
        return false;
    }
    private void anfunc(String order,List<String[]> aNList) throws IOException{
        int ct[] = new int[aNList.size()];//初始下标，如:[0,0,0,-1]
        int gz[] = new int[aNList.size()];//下标上限，如:[1,2,3,2]
        StringBuffer sb = new StringBuffer();
        String[] An;
        for(int i = 0 ; i < aNList.size() ; i++){
            An = aNList.get(i);
            ct[i]=0;
            if(i==aNList.size()-1){
                ct[i]=-1;
            }
            gz[i]=An.length-1;
        }
        while (next(ct,gz)){
//            sb.append("INSERT INTO ");
//            sb.append(table);
//            sb.append("(Condition_UUID,Comb_Order");
            //System.out.print(order + "INSERT INTO(Condition_Number,Order,) VALUES('" + UUID.randomUUID()+"','"+order+"');");
//            for(int i = 0 ; i < aNList.size() ; i++){
//                sb.append(",A");
//                sb.append(i + 1);
//            }
//            sb.append(")");
//            sb.append(" VALUES (");
//            sb.append("'");
            sb.append(UUID.randomUUID().toString().replaceAll("-", ""));
            //sb.append("'");
            sb.append(',');
            sb.append(order);
            for(int i = 0 ; i < aNList.size() ; i++){
                An = aNList.get(i);
                //System.out.println(An[0]);
                sb.append(",");
                sb.append(An[ct[i]]);

            }
            //sb.append(");");
            FileUtils.write(file, sb.toString() + System.getProperty("line.separator"), "UTF-8", true);
            //System.out.println(sb.toString());
            sb.delete(0, sb.length());

            count = count+1;
            _lastCount = _currentCount;
            _currentCount = count/sumcount*73;

            bar.tick(_currentCount - _lastCount,"");
            //System.out.println();

        }
    }

    private void perm(char[] buf, int start, int end) throws IOException{
        if (start == end) {// 当只要求对数组中一个字母进行全排列时，只要就按该数组输出即可
            anfunc(String.valueOf(buf), aNList);
        } else {// 多个字母全排列
            for (int i = start; i <= end; i++) {
                char temp = buf[start];// 交换数组第一个元素与后续的元素
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