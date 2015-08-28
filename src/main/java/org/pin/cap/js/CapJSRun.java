package org.pin.cap.js;

import org.apache.commons.io.IOUtils;
import sun.org.mozilla.javascript.internal.NativeArray;

import javax.script.*;
import java.io.FileReader;
import java.util.List;

public class CapJSRun {
    public NativeArray executeDataSetJS(String jspath,String method,List<Object[]> sourceDataArray,int[] ranges){
        NativeArray object  = null;
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine engine = sem.getEngineByName("javascript");
        Compilable compEngine = (Compilable) engine;
        FileReader reader = null;
        try {
            reader = new FileReader(jspath);
            CompiledScript script = compEngine.compile(reader);
            script.eval();
            Invocable invoke = (Invocable) engine;
            object = (NativeArray)invoke.invokeFunction(method, sourceDataArray,ranges);
           // NativeObject jj = (NativeObject) object.get(0);
            //System.out.println(object.size());

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1102);
        }finally {
            IOUtils.closeQuietly(reader);
        }
        return object;
    }

   // public void

}
