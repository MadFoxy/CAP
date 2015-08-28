package org.pin.cap.handle;

import org.pin.CapDocument;
import org.pin.cap.cmdui.ProgressBar;

/**
 * ExecuteHandle
 */
public interface ExecuteHandle {
    void execute(long starTime,CapDocument.Cap cap,ProgressBar bar) throws Exception;

    void exc(String args[]) throws Exception;
}