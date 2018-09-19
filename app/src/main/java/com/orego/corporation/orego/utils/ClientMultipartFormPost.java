package com.orego.corporation.orego.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class ClientMultipartFormPost {
    private static String TAG = "ClientMultipartFormPost";

    public static void sendPictureAndReplace(Bitmap bitmap, String mPath, Activity activity) {
        Log.d(TAG, bitmap.toString());

        File file = new File(mPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            Log.e(TAG, "save picture error", e);
        }

        if (file.exists()) {
            Intent data = new Intent();
            data.setData(Uri.parse(mPath));
            activity.setResult(Activity.RESULT_OK, data);
        }
        activity.finish();

//        Intent intent = new Intent(activity, ModelActivity.class);
//        Bundle b = new Bundle();
//        b.putString("model", "null");
//        intent.putExtras(b);
//        activity.startActivity(intent);
    }

    public static File sendFile(File dir) throws IOException {
        URL url = new URL("https://face.spbpu.com/servletpost/");
        File file = new File(dir, "result.jpg");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setUseCaches(false);
        con.setDoOutput(true);
        con.setDoInput(true);
        con.addRequestProperty("3dfacePOST", "3dfacePOST");
        con.connect();
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        BufferedOutputStream bos = new BufferedOutputStream(con.getOutputStream());
        byte[] byteArray = new byte[1];
        while (bis.read(byteArray) != -1) {
            bos.write(byteArray, 0, 1);
        }

        bis.close();
        bos.close();
        file = new File(dir,
                "serverResponse.buf.zip");
        bis = new BufferedInputStream(con.getInputStream());
        bos = new BufferedOutputStream(new FileOutputStream(file));
        byteArray = new byte[1];
        while (con.getInputStream().read(byteArray, 0, 1) != -1) {
            bos.write(byteArray, 0, 1);
        }
        bis.close();
        bos.close();
        File resultFile = new File(dir,
                "resultObj.buf");
        return unzipFunction(resultFile, file);
    }


    private static File unzipFunction(File resultFile, File zipFile) {
        byte[] buffer = new byte[1024];
        ZipInputStream zis;
        try {
            zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                FileOutputStream fos = new FileOutputStream(resultFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultFile;

    }
}