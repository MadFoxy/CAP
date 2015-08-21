package org.pin.cap.core;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.cap.cmdui.ProgressBar;
import org.pin.cap.db.DBBase;
import org.pin.cap.generate.CreateSourceTBSQL;
import org.pin.cap.generate.IGenerate;
import org.pin.cap.generate.InsertBatchSourceSQL;
import org.pin.cap.utils.CapUitls;

import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * Created by lee5hx on 2015/6/9.
 */
public class ComputeDataSet extends Thread {

    private static final Log logger  = LogFactory.getLog(ComputeDataSet.class);


    private Properties capConf;
    private ProgressBar bar;

    public ComputeDataSet(Properties capConf, ProgressBar bar){

        this.capConf = capConf;
        this.bar = bar;

    }

    private Collection<File> getSourceFiles(String sourceFile,String extension){
        return FileUtils.listFiles(new File(sourceFile),new String[]{extension},false);
    }


    public void run() {
       bar.tick(99d,null);
    }
}
