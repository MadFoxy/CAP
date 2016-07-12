/*
    data = 当根sourceData
    list = 最大范围的 sourceData List
    ranges = trend range
*/


function OutcomeDataSet(NumUp, NumDn,MaxUpGap,MaxUpPos,MaxDnGap,MaxDnPos) {
    this.NumUp = NumUp;
    this.NumDn = NumDn;
    this.MaxUpGap = MaxUpGap;
    this.MaxUpPos = MaxUpPos;
    this.MaxDnGap = MaxDnGap;
    this.MaxDnPos = MaxDnPos;

    this.getNumUp = function() {
        return this.NumUp;
    };
    this.getNumDn = function() {
        return this.NumDn;
    };
    this.getMaxUpGap = function() {
        return this.MaxUpGap;
    };
    this.getMaxUpPos = function() {
        return this.MaxUpPos;
    };
    this.getMaxDnGap = function() {
        return this.MaxDnGap;
    };
    this.getMaxDnPos = function() {
        return this.MaxDnPos;
    }
}


function computeDataSet(data,list,ranges) {
    var ArrayList = Java.type('java.util.ArrayList');
    //var ArrayList = Java.type('java.util.ArrayList');
    var rts = new ArrayList();
    var object;
    var rangeCount=0;
    var NumUp=0;
    var NumDn=0;
    var MaxUpGapAndPos=[0.0000,0];
    var MaxDnGapAndPos=[0.0000,0];
    //println(data[0]+'='+list.get(0)[0]+':'+list.size());
    var ods;
    for(var i=0;i<list.size();i++){
        object = list.get(i);
        NumUp = xNumUp(NumUp,data,object);
        NumDn = xNumDn(NumDn,data,object);
        MaxUpGapAndPos = xMaxUpGapAndPos(MaxUpGapAndPos,data,object,i);
        //MaxUpPos = xMaxUpPos(MaxUpPos,data,object,i);
        MaxDnGapAndPos = xMaxDnGapAndPos(MaxDnGapAndPos,data,object,i);
        //MaxDnPos = xMaxDnPos(MaxDnPos,data,object);
        if(ranges[rangeCount]==(i+1)){
            ods = new OutcomeDataSet(
                NumUp,
                NumDn,
                MaxUpGapAndPos[0],
                MaxUpGapAndPos[1],
                MaxDnGapAndPos[0],
                MaxDnGapAndPos[1]
            );
            rts.add(ods);
            //rts.push();
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
* */
function xNumUp(value,a,b){
    if(parseFloat(a.get("high"))<parseFloat(b.get("close"))){
       value = value+1;
    }
    return value;
}
function xNumDn(value,a,b){
    if(parseFloat(a.get("low"))>parseFloat(b.get("close"))){
        value = value+1;
    }
    return value;
}
function xMaxUpGapAndPos(value,a,b,i){
    if(parseFloat(b.get("close")) - parseFloat(a.get("high"))>value[0]){
        value[0] = parseFloat(b.get("close")) - parseFloat(a.get("high"));
        value[1] = i+1;
    }
    return value;
}
function xMaxDnGapAndPos(value,a,b,i){
    if(parseFloat(a.get("low")) - parseFloat(b.get("close"))>value[0]){
        value[0] = parseFloat(b.get("low")) - parseFloat(a.get("close"));
        value[1] = i+1;
    }
    return value;
}
