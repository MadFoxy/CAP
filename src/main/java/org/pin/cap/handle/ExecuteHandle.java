package org.pin.cap.handle;

import org.pin.cap.cmdui.ProgressBar;

import java.util.Properties;

/**
 * Created by lee5hx on 15-8-17.
 */
public interface ExecuteHandle {
    void execute(long starTime,Properties cap,Properties db,ProgressBar bar) throws Exception;
}