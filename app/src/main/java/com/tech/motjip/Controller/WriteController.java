package com.tech.motjip.Controller;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.tech.motjip.API.ApiService;
import com.tech.motjip.API.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.Callback;

public class WriteController {

    private final Context context;
    private final ApiService apiService;

    public WriteController(Context context) {
        this.context = context;
        this.apiService = RetrofitClient.getApiService(context);
    }

    public void uploadCommunityPost(
            String tag,
            String region,
            String title,
            String location,
            String date,
            String content,
            Uri imageUri,
            Callback<Void> callback
    ) {

        RequestBody tagBody = createTextRequestBody(tag);
        RequestBody regionBody = createTextRequestBody(region);
        RequestBody titleBody = createTextRequestBody(title);
        RequestBody locationBody = createTextRequestBody(location);
        RequestBody dateBody = createTextRequestBody(date);
        RequestBody contentBody = createTextRequestBody(content);

        MultipartBody.Part imagePart = createImagePart(imageUri);

        if (imagePart == null) {
            callback.onFailure(
                    null,
                    new RuntimeException("이미지 변환에 실패했습니다")
            );
            return;
        }

        apiService.createCommunityPost(
                tagBody,
                regionBody,
                titleBody,
                locationBody,
                dateBody,
                contentBody,
                imagePart
        ).enqueue(callback);
    }

    private RequestBody createTextRequestBody(String value) {
        return RequestBody.create(
                value,
                MediaType.parse("text/plain")
        );
    }

    private MultipartBody.Part createImagePart(Uri imageUri) {
        try {
            String fileName = getFileName(imageUri);

            if (fileName == null || fileName.trim().isEmpty()) {
                fileName = "community_image.jpg";
            }

            File file = new File(
                    context.getCacheDir(),
                    fileName
            );

            InputStream inputStream =
                    context.getContentResolver().openInputStream(imageUri);

            if (inputStream == null) {
                return null;
            }

            FileOutputStream outputStream =
                    new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int read;

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            RequestBody requestFile =
                    RequestBody.create(
                            file,
                            MediaType.parse("image/*")
                    );

            return MultipartBody.Part.createFormData(
                    "image",
                    file.getName(),
                    requestFile
            );

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getFileName(Uri uri) {
        String result = "선택된 이미지";

        Cursor cursor = context.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            try {
                int nameIndex =
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

                if (nameIndex >= 0 && cursor.moveToFirst()) {
                    result = cursor.getString(nameIndex);
                }

            } finally {
                cursor.close();
            }
        }

        return result;
    }
}