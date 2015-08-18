package org.pin.cap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.pin.cap.handle.CAPExecuteHandle;
import org.pin.cap.handle.CategoryListGencEH;
import org.pin.cap.handle.CategoryListInitEH;
import org.pin.cap.handle.SourceLoadDataEH;
import java.io.*;

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
                CAPExecuteHandle capEH = null;
                if (args.length > 1 && args[1].equals("-init")) {
                    BufferedReader strin=new BufferedReader(new InputStreamReader(System.in));
                    System.out.print("运行init,会清除Schema下所有的对象后，再次创建。您确认要运行吗?(yes/no) ");
                    String str = strin.readLine();
                    if(!"yes".equals(str)){
                        System.exit(-808);
                    }
                    capEH = new CategoryListInitEH();
                } else if (args.length > 1 && args[1].equals("-load")) {
                    capEH = new SourceLoadDataEH();
                } else if (args.length > 1 && args[1].equals("-genc")) {
                    capEH = new CategoryListGencEH();
                } else {
                    printUsage();
                }
                if(capEH!=null){
                    capEH.exc(args);
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
