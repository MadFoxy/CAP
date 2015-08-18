package org.pin.cap.handle;

import org.pin.cap.cmdui.ProgressBar;
import org.pin.cap.core.CategoryListInit;

import java.util.Properties;

/**
 * Created by lee5hx on 15-8-17.
 */
public class CategoryListInitEH extends CAPExecuteHandle {

    @Override
    public void execute(long starTime,Properties cap, Properties db, ProgressBar bar) throws Exception {
        new CategoryListInit(starTime, db, cap, bar).start();
    }
}
