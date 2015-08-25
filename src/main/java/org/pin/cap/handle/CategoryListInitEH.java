package org.pin.cap.handle;

import org.pin.CapDocument;
import org.pin.cap.cmdui.ProgressBar;
import org.pin.cap.core.CategoryListInit;

/**
 * Created by lee5hx on 15-8-17.
 */
public class CategoryListInitEH extends CAPExecuteHandle {

    @Override
    public void execute(long starTime,CapDocument.Cap cap, ProgressBar bar) throws Exception {
        new CategoryListInit(starTime, cap, bar).start();
    }
}
