package com.ni3.ag.navigator.client.util;

import com.ni3.ag.navigator.client.gui.Ni3;

public class Ni3UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Ni3.showClientError(thread, throwable);
    }
}
