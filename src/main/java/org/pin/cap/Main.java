package org.pin.cap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.pin.cap.cmdui.ProgressBar;
import org.pin.cap.cmdui.Type;
import org.pin.cap.core.CategoryListInit;
import org.pin.cap.core.SourceLoadData;
import org.pin.cap.handle.CAPExecuteHandle;
import org.pin.cap.handle.CategoryListInitEH;
import org.pin.cap.handle.ExecuteHandle;
import org.pin.cap.handle.SourceLoadDataEH;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.TimeZone;

/**
 * CAP Main
 * @author lee5hx
 * */
public class Main {

    private static final Log logger  = LogFactory.getLog(Main.class);

    public void start(String[] args, ClassLoader coreLoader) {
        try {
            processArgs(args);
        } catch (Throwable exc) {
            exc.printStackTrace();
        }
    }
    private void processArgs(String[] args)throws Exception{
        if(args.length == 0){
            noFun();
        }else{
            if (args[0].equals("-help")) {
                printUsage();
            }else {
                CAPExecuteHandle capEH;
                if (args.length > 1 && args[1].equals("-init")) {
                    BufferedReader strin=new BufferedReader(new InputStreamReader(System.in));
                    System.out.print("运行init,会清除Schema下所有的对象后，再次创建。您确认要运行吗?(yes/no) ");
                    String str = strin.readLine();
                    if(!"yes".equals(str)){
                        System.exit(-808);
                    }
                    capEH = new CategoryListInitEH();
                    capEH.exc(args);
                } else if (args.length > 1 && args[1].equals("-load")) {
                    capEH = new SourceLoadDataEH();
                    capEH.exc(args);
                } else if (args.length > 1 && args[1].equals("-genc")) {
                    long starTime = System.currentTimeMillis();
                    logger.info("开始执行:cap " + args[0] + " " + args[1]);
                    Properties cap_properties = loadCapConf(args[0]);
                    Properties db_properties = loadDBConf();
                    if (cap_properties != null) {
                        System.out.println("正在开发.");
                        System.exit(0);
                    } else {
                        System.out.println(args[0] + ".properties not found!pls check " + args[0] + ".properties weather it exist in this path:conf/");
                    }

                    System.out.println("待开发");
                }
                else {
                    printUsage();
                }
            }
        }
    }
    public static void run(String[] args,ClassLoader coreLoader) {
        Main m = new Main();
        m.start(args, coreLoader);
    }
    public static void main(String agrs[]){
        DOMConfigurator.configure("../conf/log4j.xml");
        run(agrs, null);
        System.exit(-1);
    }
    public static Properties loadCapConf(String confFile) {
        Properties prop =null;
        InputStream fis = null;
        try {
            File file = new File("../conf/"+confFile+".properties");
            fis = new FileInputStream(file);
            prop = new Properties();
            prop.load(fis);
            fis.close();
            logger.info("CAP load conf/"+confFile+".properties complete.");
        } catch (Exception e) {
            logger.error("CAP load conf/"+confFile+".properties Error:"+e);
            //e.printStackTrace();
        }
        finally{
            IOUtils.closeQuietly(fis);
        }
        return prop;
    }

    public static Properties loadDBConf() {
        Properties prop = new Properties();
        InputStream fis = null;
        try {
            File file = new File("../conf/db_conf.properties");
            fis = new FileInputStream(file);
            prop.load(fis);
            fis.close();
            logger.info("CAP load conf/db_conf.properties complete.");
        } catch (Exception e) {
            logger.error("CAP load conf/db_conf.properties Error:"+e);
            //e.printStackTrace();
        }
        finally{
            IOUtils.closeQuietly(fis);
        }
        return prop;
    }
    private static void printUsage() {
        String lSep = System.getProperty("line.separator");
        StringBuffer msg = new StringBuffer("");
        msg.append("cap [config_file] [action]  " + lSep);
        msg.append("[config_file]:" + lSep);
        msg.append("  [config_file].properties          " + lSep);
        msg.append("[action]:" + lSep);
        msg.append("  -init         初始化环境与categoryList." + lSep);
        msg.append("  -genc         重新生成categoryList." + lSep);
        msg.append("  -load         导入sourceData." + lSep);
        System.out.println(msg.toString());
    }
    private static void noFun() {
        StringBuffer msg = new StringBuffer("");
        msg.append("Command not found,Enter the command:cap -help");
        System.out.println(msg.toString());
    }

}
