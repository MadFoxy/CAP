package org.pin.cap.js;

import org.apache.commons.io.IOUtils;
import sun.org.mozilla.javascript.internal.NativeArray;
import javax.script.*;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

public class CapJS {
    public NativeArray executeDataSetJS(String jspath,String method,Map<String,Object> tData,List<Map<String,Object>> sourceDataArray,int[] ranges){
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
            object = (NativeArray)invoke.invokeFunction(method, tData,sourceDataArray,ranges);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1102);
        }finally {
            IOUtils.closeQuietly(reader);
        }
        return object;
    }

//    public void swapParams(int i,int j,Object params[][],NativeArray nativeArray,CapDocument.Cap cap){
//        NativeObject object;
//        DataSetColumnType[] getDataSetColumnTypes = CapUitls.getDataSetColumnTypes(cap);
//        DBBase dbBase = DBBase.getInstance();
//        CategoryListColumnType[] columnArray = CapUitls.getCategoryListColumns(cap);
//        Object[] ps = new Object[columnArray.length+1];
////        int uuidIndex = cap.getDataSet().getConditionUUIDIndex().intValue();
////        for(int g=0;g<ps.length;g++){
////            ps[g] = params[i][uuidIndex++];
////        }
// //       System.out.println(j);
////        System.out.println(String.valueOf(ps[1]));
////        System.out.println(String.valueOf(ps[2]));
////        System.out.println(String.valueOf(ps[7]));
//        params[i][j++] = "Adj_K_Power";
//        params[i][j++] = "Adj_K_Ratio";
//        params[i][j++] = dbBase.queryUUID(new SelectOrderUUIDSQL(cap).generateSQL(),ps);
//        for(int x=0;x<nativeArray.size();x++){
//            object = (NativeObject)nativeArray.get(x);
//            for(int y=0;y<getDataSetColumnTypes.length;y++){
//                // NATIVE
//                //System.out.println((int)object.get(getDataSetColumnTypes[y].getStringValue()));
//                params[i][j] = CapUitls.getValue(getDataSetColumnTypes[y], object.get(getDataSetColumnTypes[y].getStringValue()).toString());
//                j++;
//            }
//        }
//    }

}
