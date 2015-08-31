package org.pin.cap;

import org.apache.log4j.xml.DOMConfigurator;
import org.pin.cap.handle.*;
import java.io.*;

/**
 * CAP Main
 * @author lee5hx
 * */
public class Main {

    public void start(String[] args, ClassLoader coreLoader) {
        try {
            processArgs(args,coreLoader);
        } catch (Throwable exc) {
            exc.printStackTrace();
        }
    }
    private void processArgs(String[] args,ClassLoader coreLoader)throws Exception{
        if(args.length == 0){
            noFun(coreLoader);
        }else{
            switch (args[0]) {
                case "-help":
                    printUsage();
                    break;
                case "-version":
                    printVersion();
                    break;
                default:
                    ExecuteHandle eh = null;
                    if (args.length > 1 && args[1].equals("-init")) {
                        BufferedReader strin = new BufferedReader(new InputStreamReader(System.in));
                        System.out.print("After running init, it will remove all objects under the schema, create again. You sure you want to run it?(yes/no) ");
                        String str = strin.readLine();
                        if (!"yes".equals(str)) {
                            System.exit(-808);
                        }
                        eh = new CategoryListInitEH();
                    } else if (args.length > 1 && args[1].equals("-load")) {
                        eh = new SourceLoadDataEH();
                    } else if (args.length > 1 && args[1].equals("-genc")) {
                        eh = new CategoryListGencEH();
                    } else if (args.length > 1 && args[1].equals("-cset")) {
                        eh = new ComputeDataSetEH();
                    } else {
                        printUsage();
                    }
                    if (eh != null) eh.exc(args);
                    break;
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

    private void printUsage() {
        String lSep = System.getProperty("line.separator");
        StringBuffer msg;
        msg = new StringBuffer();
        msg.append("cap [config_file] [action]  ").append(lSep);
        msg.append("[config_file]:").append(lSep);
        msg.append("  [config_file].xml          ").append(lSep);
        msg.append("[action]:").append(lSep);
        msg.append("  -init        initialize the environment and generate CategoryList.").append(lSep);
        msg.append("  -genc        regenerate CategoryList.").append(lSep);
        msg.append("  -load        load SourceData.").append(lSep);
        msg.append("  -cset        compute DataSet.").append(lSep);
        System.out.println(msg.toString());
    }
    private void printVersion() {
        System.out.println("1.0-SNAPSHOT");
    }

    private static void noFun(ClassLoader coreLoader) {
        if(coreLoader==null) System.out.println("Command not found,Enter the command:cap -help");
    }

}
