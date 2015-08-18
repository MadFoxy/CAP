package org.pin.cap.handle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pin.cap.Main;
import org.pin.cap.cmdui.ProgressBar;
import org.pin.cap.cmdui.Type;
import org.pin.cap.core.SourceLoadData;

import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.TimeZone;

/**
 * Created by lee5hx on 15-8-17.
 */
public abstract class CAPExecuteHandle implements ExecuteHandle {
    //public void

    private static final Log logger  = LogFactory.getLog(CAPExecuteHandle.class);

    public void exc(String args[]) throws Exception{
        logger.info("开始执行:cap " + args[0] + " " + args[1]);
        Properties cap_properties = Main.loadCapConf(args[0]);
        Properties db_properties = Main.loadDBConf();
        ProgressBar bar = new ProgressBar(50, 100, Type.BOTH);
        if (cap_properties != null) {
            long starTime = System.currentTimeMillis();
            this.execute(starTime, cap_properties, db_properties, bar);
            while (true) {
                    Thread.sleep(500);
                    if (bar._currentTick >= 100d) {
                        long endTime = System.currentTimeMillis();
                        long diff = (endTime - starTime);
                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");//初始化Formatter的转换格式。
                        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                        String hms = formatter.format(diff);
                        logger.info("Cap "+args[1]+" 运行成功!(" + hms + ")");
                        break;
                    } else if (bar._currentTick >= 99d||bar._currentTick >= 98d) {
                        long endTime = System.currentTimeMillis();
                        long diff = (endTime - starTime);
                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");//初始化Formatter的转换格式。
                        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                        String hms = formatter.format(diff);
                        bar.tick(100d - bar._currentTick, "Run Success!(" + hms + ")");
                        logger.info("Cap "+args[1]+" 运行成功!(" + hms + ")");
                        break;
                    }
               //
            }
            System.exit(0);
        } else {
            System.out.println(args[0] + ".properties not found!pls check " + args[0] + ".properties weather it exist in this path:conf/");
        }

    }

}
