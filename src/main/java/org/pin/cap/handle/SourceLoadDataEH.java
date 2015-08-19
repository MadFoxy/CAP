package org.pin.cap.handle;

import org.pin.cap.cmdui.ProgressBar;
import org.pin.cap.core.SourceLoadData;
import java.util.Properties;

/**
 * Created by lee5hx on 15-8-17.
 */
public class SourceLoadDataEH extends CAPExecuteHandle {

    @Override
    public void execute(long starTime,Properties cap, ProgressBar bar) throws Exception {
        new SourceLoadData(cap, bar).start();
    }
}
