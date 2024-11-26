package com.example.theychat.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class BitmapUtil {
    private final static String TAG = "BitmapUtil";

    // 保存 Bitmap 图片到指定路径
    public static void saveImage(String path, Bitmap bitmap) {
        try (FileOutputStream os = new FileOutputStream(path)) {
            // 使用 JPEG 格式压缩图片，质量为 80，保存到文件输出流
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, os);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 保存 ByteBuffer 数据为图片到指定路径
    public static void saveImage(String path, ByteBuffer buffer) {
        try (FileOutputStream os = new FileOutputStream(path)) {
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            os.write(data, 0, data.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取旋转后的 Bitmap 图片
    public static Bitmap getRotateBitmap(Bitmap bitmap, float rotateDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateDegree);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, false);
    }

    // 获取缩放后的 Bitmap 图片
    public static Bitmap getScaleBitmap(Bitmap bitmap, double scaleRatio) {
        Matrix matrix = new Matrix();
        matrix.postScale((float)scaleRatio, (float)scaleRatio);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, false);
    }

    // 自动缩放图片（根据 Uri 获取）
    public static Bitmap getAutoZoomImage(Context ctx, Uri uri) {
        Log.d(TAG, "getAutoZoomImage uri="+uri.toString());
        Bitmap zoomBitmap = null;

        try (InputStream is = ctx.getContentResolver().openInputStream(uri)) {
            Bitmap originBitmap = BitmapFactory.decodeStream(is);
            int ratio = originBitmap.getWidth()/2000+1;
            zoomBitmap = getScaleBitmap(originBitmap, 1.0/ratio);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zoomBitmap;
    }

    // 自动缩放图片（传入 Bitmap 进行缩放）
    public static Bitmap getAutoZoomImage(Bitmap origin) {
        int ratio = origin.getWidth()/2000+1;
        return getScaleBitmap(origin, 1.0/ratio);
    }

    // 根据 Uri 获取图片路径，并自动缩放保存
    public static String getAutoZoomPath(Context ctx, Uri uri) {
        Log.d(TAG, "getAutoZoomPath uri="+uri.toString());

        // 获取下载目录下的保存路径，文件名为当前时间
        String imagePath = String.format("%s/%s.jpg",
                ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(),
                DateUtil.getNowDateTime());
        try (InputStream is = ctx.getContentResolver().openInputStream(uri)) {
            Bitmap originBitmap = BitmapFactory.decodeStream(is);
            int ratio = originBitmap.getWidth()/1000+1;
            Bitmap zoomBitmap = getScaleBitmap(originBitmap, 1.0/ratio);
            saveImage(imagePath, zoomBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }
}
