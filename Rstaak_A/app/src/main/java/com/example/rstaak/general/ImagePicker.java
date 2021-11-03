package com.example.rstaak.general;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

import javax.inject.Inject;

public class ImagePicker
{
    @Inject
    public ImagePicker() {
    }

    public static Bitmap getBitmap(Context context, Uri uri)
    {

        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String filePath = cursor.getString(1);
        cursor.close();
        return BitmapFactory.decodeFile(filePath);
    }

    public static void pick(Context context, int request_code)
    {
        if (request_code == 1)
        {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            ((Activity) context).startActivityForResult(intent, 1);
        }

        if (request_code == 2)
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            ((Activity) context).startActivityForResult(intent,2);
        }
    }

    public String getStringImage(Bitmap bmp, int IMAGE_MAX_SIZE)
    {
        bmp = getResizedBitmap(bmp, IMAGE_MAX_SIZE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        baos.reset();
        return encodedImage;
    }

    public Bitmap getResizedBitmap(Bitmap image, int IMAGE_MAX_SIZE)
    {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1)
        {
            width = IMAGE_MAX_SIZE;
            height = (int) (width / bitmapRatio);
        }
        else
        {
            height = IMAGE_MAX_SIZE;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static Bitmap getBitmap(String encodedImage)
    {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return (BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
    }
}