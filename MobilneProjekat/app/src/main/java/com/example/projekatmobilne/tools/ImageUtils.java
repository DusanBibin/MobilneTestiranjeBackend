package com.example.projekatmobilne.tools;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import okhttp3.Headers;
import okhttp3.MultipartBody;
public class ImageUtils {

    public static String getFileNameFromPart(MultipartBody.Part part) {
        Headers headers = part.headers();

        String contentDisposition = headers.get("Content-Disposition");

        String[] parts = contentDisposition.split(";");

        String filename = null;
        for (String partStr : parts) {
            if (partStr.trim().startsWith("filename")) {
                filename = partStr.substring(partStr.indexOf('=') + 1).trim().replace("\"", "");
                break;
            }
        }

        return filename;
    }

    public static String getFileNameFromPart(Uri uri, ContentResolver contentResolver) {
        String result;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

}
