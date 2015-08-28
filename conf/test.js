/*
    list = sourceData
    ranges = trend range
*/
function computeDataSet(list,ranges) {
    var rts = [];
    var object;
    var rangeCount=0;
    var NumH=0;
    var NumL=1;
    var MaxPrice=2;
    var MaxBars=3;
    var MinPrice=4;
    var MinBars=5;
    for(var i=0;i<list.size();i++){
        object = list.get(i);
        if(ranges[rangeCount]==(i+1)){
            //println(typeof parseFloat(rangeCount));
            rts.push({
                NumH:NumH,
                NumL:NumL,
                MaxPrice:MaxPrice,
                MaxBars:MaxBars,
                MinPrice:MinPrice,
                MinBars:MinBars
            });
            rangeCount++;
        }
    }
    return rts;
}
