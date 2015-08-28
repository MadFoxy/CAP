package org.pin.cap.handle;

import org.pin.CapDocument;
import org.pin.cap.cmdui.ProgressBar;
import org.pin.cap.core.CategoryListGenc;
import java.util.Properties;

/**
 * CategoryListGencEH.
 */
public class CategoryListGencEH extends CAPExecuteHandle {

    @Override
    public void execute(long starTime,CapDocument.Cap cap, ProgressBar bar) throws Exception {
        new CategoryListGenc(starTime, cap, bar).start();
    }
}
