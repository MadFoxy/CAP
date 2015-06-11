package org.pin.cap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.pin.cap.cmdui.ProgressBar;
import org.pin.cap.cmdui.Type;
import org.pin.cap.core.CategoryListInit;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;


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
    private void processArgs(String[] args) {
        if(args.length == 0){
            noFun();
        }else{
            if (args[0].equals("-help")) {
                printUsage();
            }else if(args.length>1&&args[1].equals("-init")){
                logger.info("开始执行:cap "+args[0]+" "+args[1]);
                Properties cap_properties = loadCapConf(args[0]);
                Properties db_properties = loadDBConf();
                ProgressBar bar = new ProgressBar( 50,100, Type.BOTH );
                if(cap_properties!=null){
                    new CategoryListInit(db_properties,cap_properties,bar).start();
                    while (true){
                        try{
                            Thread.sleep(500);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        if(bar._currentTick>=99d){
                            bar.tick(1d, "Cap Init CategoryList 运行成功!");
                            break;
                        }

                    }
                    System.exit(0);
                    //bar.tick(1d, "Cap initCategory 运行成功!");
                }else{
                    System.out.println(args[0]+".properties不存在!请检查"+args[0]+".properties是否存在于conf/目录下.");
                }

            }else{
                printUsage();
            }
        }
    }
    public static void run(String[] args,
                           ClassLoader coreLoader) {
        Main m = new Main();
        m.start(args, coreLoader);
    }
    public static void main(String agrs[]){
        DOMConfigurator.configure("../conf/log4j.xml");
        run(agrs, null);
        System.exit(-1);
    }
    private static Properties loadCapConf(String confFile) {
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

    private static Properties loadDBConf() {
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
        msg.append("  [config_file].properties          配置文件" + lSep);
        msg.append("[action]:" + lSep);
        msg.append("  -init                              初始化" + lSep);
      //  msg.append("  -start                             开始" + lSep); logger.debug("111");
        System.out.println(msg.toString());
    }
    private static void noFun() {
        StringBuffer msg = new StringBuffer("");
        msg.append("没有找到命令,请输入命令查看帮助:cap -help");
        System.out.println(msg.toString());
    }

}
