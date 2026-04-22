package com.tech.motjip.Handler;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;


/**
 * 앱 실행 종료 메인이동등을 제어하기위한 클래스
 */
public class AppHandler {

    /**
     * 인트로 액티비티를 재실행함
     */
    public static void restart(Activity activity){
        PackageManager packageManager = activity.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(activity.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        activity.startActivity(mainIntent);
        System.exit(0);
    }

    /**
     * 모든 액티비티를 종료함
     */
    public static void off(Activity activity){
        activity.finishAffinity();
        System.exit(0);
    }
}
