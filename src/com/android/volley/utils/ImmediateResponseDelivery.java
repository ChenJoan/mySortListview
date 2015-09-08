// Copyright 2011 Google Inc. All rights reserved.

package com.android.volley.utils;

import java.util.concurrent.Executor;

import com.android.volley.ExecutorDelivery;

/**
 * A ResponseDelivery for testing that immediately delivers responses
 * instead of posting back to the main thread.
 */
public class ImmediateResponseDelivery extends ExecutorDelivery {

    public ImmediateResponseDelivery() {
        super(new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        });
    }
}
