package com.tech.motjip.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.tech.motjip.R;

public class DialogUtil {

    // 클릭 이벤트를 전달받기 위한 인터페이스 정의
    public interface DialogCallback {
        void onConfirm();
    }

    public static void showCustomDialog(Context context, int iconRes, String title, String message, DialogCallback callback) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_common, null);

        ImageView ivIcon = view.findViewById(R.id.ivDialogIcon);
        TextView tvTitle = view.findViewById(R.id.tvDialogTitle);
        TextView tvMessage = view.findViewById(R.id.tvDialogMessage);
        Button btnConfirm = view.findViewById(R.id.btnDialogConfirm);

        ivIcon.setImageResource(iconRes);
        tvTitle.setText(title);
        tvMessage.setText(message);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false) // 외부 클릭으로 닫히지 않게 설정
                .create();

        // 배경을 투명하게 설정 (둥근 모서리 적용을 위해 필수)
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            if (callback != null) {
                callback.onConfirm(); // 확인을 누르면 콜백 실행
            }
        });

        dialog.show();
    }
}