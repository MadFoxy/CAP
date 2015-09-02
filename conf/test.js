/*
    data = 当根sourceData
    list = 最大范围的 sourceData List
    ranges = trend range
*/
function computeDataSet(data,list,ranges) {
    var rts = [];
    var object;
    var rangeCount=0;
    var NumUp=0;
    var NumDn=0;
    var MaxUpGapAndPos=[0.0000,0];
    var MaxDnGapAndPos=[0.0000,0];
    //println(data[0]+'='+list.get(0)[0]+':'+list.size());
    for(var i=0;i<list.size();i++){
        object = list.get(i);
        NumUp = xNumUp(NumUp,data,object);
        NumDn = xNumDn(NumDn,data,object);
        MaxUpGapAndPos = xMaxUpGapAndPos(MaxUpGapAndPos,data,object,i);
        //MaxUpPos = xMaxUpPos(MaxUpPos,data,object,i);
        MaxDnGapAndPos = xMaxDnGapAndPos(MaxDnGapAndPos,data,object,i);
        //MaxDnPos = xMaxDnPos(MaxDnPos,data,object);
        if(ranges[rangeCount]==(i+1)){
            rts.push({
                NumUp:NumUp,
                NumDn:NumDn,
                MaxUpGap:MaxUpGapAndPos[0],
                MaxUpPos:MaxUpGapAndPos[1],
                MaxDnGap:MaxDnGapAndPos[0],
                MaxDnPos:MaxDnGapAndPos[1]
            });
            rangeCount++;
        }
    }
    return rts;
}
/*
* value = 需要返回的值
* a = 当根行数据
* b = 比较的行数据
* i = 第几根
* Open  4
  High  5
  Low   6
  Close 7
* */
function xNumUp(value,a,b){
    if(parseFloat(a[5])<parseFloat(b[7])){
       value = value+1;
    }
    return value;
}
function xNumDn(value,a,b){
    if(parseFloat(a[6])>parseFloat(b[7])){
        value = value+1;
    }
    return value;
}
function xMaxUpGapAndPos(value,a,b,i){
    if(parseFloat(b[7]) - parseFloat(a[5])>value[0]){
        value[0] = parseFloat(b[7]) - parseFloat(a[5]);
        value[1] = i+1;
    }
    return value;
}
function xMaxDnGapAndPos(value,a,b,i){
    if(parseFloat(a[6]) - parseFloat(b[7])>value[0]){
        value[0] = parseFloat(b[7]) - parseFloat(a[5]);
        value[1] = i+1;
    }
    return value;
}
