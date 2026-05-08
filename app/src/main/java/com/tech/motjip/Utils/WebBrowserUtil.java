package com.tech.motjip.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class WebBrowserUtil {
    public static void openWebBrowser(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "웹 브라우저를 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}