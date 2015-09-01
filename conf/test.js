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
    var MaxUpGap=0.0000;
    var MaxUpPos=0;
    var MaxDnGap=0.0000;
    var MaxDnPos=0;
    //println(data[0]+'='+list.get(0)[0]+':'+list.size());
    for(var i=0;i<list.size();i++){
        object = list.get(i);
        NumUp = xNumUp(NumUp,data,object);
        NumDn = xNumDn(NumDn,data,object);
        MaxUpGap = xMaxUpGap(MaxUpGap,data,object);
        MaxUpPos = xMaxUpPos(MaxUpPos,data,object);
        MaxDnGap = xMaxDnGap(MaxDnGap,data,object);
        MaxDnPos = xMaxDnPos(MaxDnPos,data,object);
        if(ranges[rangeCount]==(i+1)){
            rts.push({
                NumUp:NumUp,
                NumDn:NumDn,
                MaxUpGap:MaxUpGap,
                MaxUpPos:MaxUpPos,
                MaxDnGap:MaxDnGap,
                MaxDnPos:MaxDnPos
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
* */
function xNumUp(value,a,b){
    return value+1;
}
function xNumDn(value,a,b){
    return value+1;
}
function xMaxUpGap(value,a,b){
    return value + parseFloat(a[6])+parseFloat(b[6]);
}
function xMaxUpPos(value,a,b){
    return 2;
}
function xMaxDnGap(value,a,b){
    return value + parseFloat(a[7])+parseFloat(b[7]);
}
function xMaxDnPos(value,a,b){
    return 2;
}
