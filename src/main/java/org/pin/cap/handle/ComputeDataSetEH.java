package org.pin.cap.handle;

import org.pin.cap.cmdui.ProgressBar;
import org.pin.cap.core.ComputeDataSet;
import org.pin.cap.core.SourceLoadData;

import java.util.Properties;

/**
 * Created by lee5hx on 15-8-17.
 */
public class ComputeDataSetEH extends CAPExecuteHandle {

    @Override
    public void execute(long starTime,Properties cap, ProgressBar bar) throws Exception {
        new ComputeDataSet(cap, bar).start();
    }
}
