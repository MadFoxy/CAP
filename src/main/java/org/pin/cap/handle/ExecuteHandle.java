package org.pin.cap.handle;

import org.pin.CapDocument;
import org.pin.cap.cmdui.ProgressBar;

/**
 * Created by lee5hx on 15-8-17.
 */
public interface ExecuteHandle {
    void execute(long starTime,CapDocument.Cap cap,ProgressBar bar) throws Exception;

    void exc(String args[]) throws Exception;
}