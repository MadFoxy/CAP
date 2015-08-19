package org.pin.cap.handle;

import org.pin.cap.cmdui.ProgressBar;
import org.pin.cap.core.CategoryListGenc;
import java.util.Properties;

/**
 * Created by lee5hx on 15-8-17.
 */
public class CategoryListGencEH extends CAPExecuteHandle {

    @Override
    public void execute(long starTime,Properties cap, ProgressBar bar) throws Exception {
        new CategoryListGenc(starTime, cap, bar).start();
    }
}
