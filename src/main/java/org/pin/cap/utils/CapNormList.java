package org.pin.cap.utils;

import java.util.Iterator;
import java.util.List;

/**
 * CapNormList Created by lee5hx on 15-9-29.
 */
public class CapNormList implements Iterator<String[]> {


    private List<String[]> aNList;
    private int ct[];
    private int gz[];
    private boolean hasNext = false;
    private int anListSize;
    private int sum;
    private int x;
    private int y;
    private String[] rt;

    public CapNormList(List<String[]> aNList){
        this.aNList = aNList;
        this.anListSize = aNList.size();
        this.init();
    }
    private void init(){
        ct = new int[anListSize];//初始下标，如:[0,0,0,-1]
        gz = new int[anListSize];//下标上限，如:[1,2,3,2]
        rt = new String[anListSize];
        x = ct.length - 1;
        String[] An;
        for(int i = 0 ; i < anListSize ; i++){
            An = aNList.get(i);
            ct[i]=0;
            if(i==aNList.size()-1){
                ct[i]=-1;
            }
            gz[i]=An.length-1;
        }
        if(anListSize>0&&gz[anListSize-1]>-1){
            hasNext = true;
        }
    }
    @Override
    public boolean hasNext() {
        while (x!=-1){
            sum = ct[x]+1;
            if(sum<=gz[x]){
                hasNext = true;
                break;
            }
            x--;
        }
        if(x == -1){
            hasNext = false;
        }
        return hasNext;
    }

    @Override
    public String[] next() {
        if(hasNext){
            y=x;
            ct[y] = sum;
            while(y!=ct.length-1){//如果归零位数，不等于最大位数
                y=y+1;
                ct[y] = 0;//位数归零操作
            }
            x=ct.length - 1;//回归初始位数，才能重新++
            String[] tempAn;
            for(int i = 0 ; i < aNList.size() ; i++){
                tempAn = aNList.get(i);
                rt[i] =tempAn[ct[i]];
            }
        }
        return rt;
    }

    @Override
    public void remove() {

    }
//    public static void main(String[] agrs){
//
//        List<String[]> aNList = new ArrayList<>();
//        String[] tempSlipStr;
//        //,"C1,C2,C3","D1,D2,D3","E1,E2,E3","F1,F2,F3","G1,G2,G3","H1,H2,H3"
//        String[] xx={"A1,A2,A3,A5,A6,S7","B1,B2,B3","C1,C2,C3","D1,D2,D3","E1,E2,E3","F1,F2,F3","G1,G2,G3","H1,H2,H3"};
//        for(String clct :xx){
//            // = clct.getStringValue().split("\\|");
//            tempSlipStr=clct.split(",");
//            aNList.add(tempSlipStr);
//            //sumcount = sumcount*tempSlipStr.length;
//        }
//        CapNormList cnl = new CapNormList(aNList);
//        while (cnl.hasNext()){
//            for(String s :cnl.next()){
//                System.out.print(s+",");
//            }
//            System.out.println();
//        }
//    }
}
