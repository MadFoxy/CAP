package org.pin.cap.fork.task;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.CapDocument;
import org.pin.cap.cmdui.ProgressBar;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.RecursiveAction;

public class GenInsertCategoryCVS extends RecursiveAction {


    private static final Log logger  = LogFactory.getLog(GenInsertCategoryCVS.class);
    private static final long serialVersionUID = 1L;  
    // These attributes will determine the block of products this task has to  
    // process.  
    private List<String> bplist;
    private int first;  
    private int last;

    private CapDocument.Cap cap;
    // store the increment of the price of the products

    private double count = 0;
   // private List<String[]> aNList = new ArrayList();
    private char[] order;
    // private String table;
    private File file;
    private ProgressBar bar;
    private List<String[]> normList;
    private Double tickCount;


  
    public GenInsertCategoryCVS(CapDocument.Cap cap,List<String[]> normList,List<String> bplist, int first, int last,ProgressBar bar,Double tickCount) {
        super();  
        this.bplist = bplist;
        this.normList = normList;
        this.first = first;  
        this.last = last;
        this.cap = cap;
        this.init(cap);
        this.bar = bar;
        this.tickCount = tickCount;
    }

    private void init(CapDocument.Cap cap){
        file = new File("../tmp/"+cap.getTargetName()+"."+cap.getCategoryList().getTable().getName()+".cvs");
       // logger.info("category.count:"+sumcount);
        logger.info("cvs.path:"+file.getPath());
    }


    /**
     * If the difference between the last and first attributes is greater than
     * or equal to 10, create two new GenInsertCategoryCVS objects, one to process the first
     * half of products and the other to process the second half and execute
     * them in ForkJoinPool using the invokeAll() method.
     */
    @Override  
    protected void compute() {  
        if (last - first < 10) {
            try{
                gen();
            }catch (IOException e){
                e.printStackTrace();
            }
        } else {  
            int middle = (first + last) / 2;  
            //System.out.printf("GenInsertCategoryCVS: Pending tasks:%s\n", getQueuedTaskCount());
            GenInsertCategoryCVS t1 = new GenInsertCategoryCVS(cap,normList,bplist, first, middle + 1,bar,tickCount);
            GenInsertCategoryCVS t2 = new GenInsertCategoryCVS(cap,normList,bplist, middle + 1, last,bar,tickCount);
            invokeAll(t1, t2);  
        }  
    }  
  
    private void gen()throws IOException{
        String order;
        for (int i = first; i < last; i++) {
            order = bplist.get(i);
            anfunc(order,normList);
        }
    }

    private void anfunc(String order,List<String[]> normList) throws IOException {

        StringBuffer sb = new StringBuffer();
        String[] An;
        String pkgs;
        for(int i=0;i<normList.size();i++){
            pkgs = cap.getPrimaryKeyGenStrategy();
            if(pkgs.toLowerCase().equals("UUID".toLowerCase())){
                sb.append(UUID.randomUUID().toString().replaceAll("-", ""));
                sb.append(',');
            }
            sb.append(order);
            An = normList.get(i);
            for(String s:An){
                sb.append(",");
                sb.append(s);
                //System.out.print(s+"-");
            }
            //System.out.println(sb);
            FileUtils.write(file, sb.toString() + System.getProperty("line.separator"), "UTF-8", true);
            //System.out.println(sb.toString());
            sb.delete(0, sb.length());
//            count = count+1;
//            _lastCount = _currentCount;
//            _currentCount = count/sumcount*sCount;
            bar.tick(tickCount,"");

        }
    }

  
}  