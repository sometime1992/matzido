package com.tech.motjip.Controller;

import android.app.Activity;

import javax.inject.Inject;

public class TestController {

    private final Activity testActivity;

    @Inject
    TestController(Activity testActivity)
    {
        this.testActivity = testActivity;
    }

}
