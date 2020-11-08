package com.roguichou.attestinator;

import android.app.Activity;
import android.app.Application;

public class MyApp extends Application {

    private Activity mCurrentActivity = null;
    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }
}
