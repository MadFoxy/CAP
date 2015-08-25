package org.pin.cap.handle;

import org.pin.CapDocument;
import org.pin.cap.cmdui.ProgressBar;
import org.pin.cap.core.ComputeDataSet;

/**
 * Created by lee5hx on 15-8-17.
 */
public class ComputeDataSetEH extends CAPExecuteHandle {

    @Override
    public void execute(long starTime,CapDocument.Cap cap, ProgressBar bar) throws Exception {
        new ComputeDataSet(cap, bar).start();
    }
}
