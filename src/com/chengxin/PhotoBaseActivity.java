package com.chengxin;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.chengxin.R;
import com.chengxin.dialog.MMAlert;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.GlobalParam;

import java.io.File;
import java.io.IOException;

/**
 * Created by Chony on 2015/4/23.
 */
public class PhotoBaseActivity extends BaseActivity{
    public static final String TEMP_FILE_NAME = "header.jpg";

    /*
	 * 选择图片对话框
	 */
    public void selectImg(){
        MMAlert.showAlert(mContext, "", mContext.getResources().
                        getStringArray(R.array.camer_item),
                null, new MMAlert.OnAlertSelectId() {

                    @Override
                    public void onClick(int whichButton) {
                        switch (whichButton) {
                            case 0:
                                getImageFromGallery();
                                break;
                            case 1:
                                getImageFromCamera();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    public void doChoose(boolean isGallery, final Intent data){
        doChoose(isGallery, data, true, "", 0, 0);
    }

    /*
	 * 处理选择的图片
	 */
    public void doChoose(boolean isGallery, final Intent data, boolean isScale, String tempFile, int width, int height) {
        if(isGallery){
            originalImage(data, isScale, width, height);
        }else {
            if(data != null){
                originalImage(data, isScale, width, height);
            }else{
                // Here if we give the uri, we need to read it
                String tempUrl =tempFile;
                if (TextUtils.isEmpty(tempUrl)){
                    tempUrl = TEMP_FILE_NAME;
                }
                String path = Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY+tempUrl;
                String extension = path.substring(path.indexOf("."), path.length());
                if(FeatureFunction.isPic(extension)){
                    if (isScale){
                        startPhotoZoom(Uri.fromFile(new File(path)), width, height);
                    }
                    else {
                        Intent intent = new Intent(mContext, RotateImageActivity.class);
                        intent.putExtra("path", path);
                        startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);
                    }
                }else {
                    //Toast.makeText(mContext, R.string.please_choose_pic, Toast.LENGTH_SHORT).show();
                }
                //mImageFilePath = FeatureFunction.PUB_TEMP_DIRECTORY+TEMP_FILE_NAME;
                //ShowBitmap(false);
            }
        }
    }

    /**
     * 根据图片路径进入裁剪页面
     *
     * @param data
     */
    private void originalImage(Intent data, boolean isScale, int width, int height) {
        Uri uri = data.getData();

        if (!TextUtils.isEmpty(uri.getAuthority())) {
            ContentResolver resolver = this.getContentResolver();
            String[] projection = { MediaStore.Images.Media.DATA };
            String localImagePath = "";
            if (Build.VERSION.SDK_INT > 18) {
                String lastPathSegment = uri.getLastPathSegment();
                if (!lastPathSegment.matches("\\d+"))
                    lastPathSegment = lastPathSegment.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = { lastPathSegment };
                Cursor cursor = resolver.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                        selection, selectionArgs, null);
                int columnIndex = cursor.getColumnIndex(projection[0]);
                if (cursor.moveToFirst())
                    localImagePath = cursor.getString(columnIndex);
                cursor.close();
            } else {
                Cursor cursor = resolver.query(uri, projection, null, null,
                        null);
                int columnIndex = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                localImagePath = cursor.getString(columnIndex);
                cursor.close();
            }

            String extension = localImagePath.substring(
                    localImagePath.lastIndexOf("."), localImagePath.length());
            if (FeatureFunction.isPic(extension)) {
                if (isScale){
                    startPhotoZoom(uri, width, height);
                }
                else {
                    Intent intent = new Intent(mContext, RotateImageActivity.class);
                    intent.putExtra("path", localImagePath);
                    startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);
                }
            } else {
            }
        } else {
            String path = uri.getPath();
            String extension = path.substring(path.lastIndexOf("."),
                    path.length());
            if (FeatureFunction.isPic(extension)) {
                if (isScale){
                    startPhotoZoom(Uri.fromFile(new File(path)), width, height);
                }
                else {
                    Intent intent = new Intent(mContext, RotateImageActivity.class);
                    intent.putExtra("path", path);
                    startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);
                }
            } else {
            }
        }
    }

    public void getImageFromCamera(){
        getImageFromCamera("");
    }
    /*
     * 拍一张
     */
    public void getImageFromCamera(String file) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(FeatureFunction.newFolder(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY)){
            String tempPath = file;
            if (TextUtils.isEmpty(tempPath)){
                tempPath = TEMP_FILE_NAME;
            }
            File out = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY, tempPath);
            Uri uri = Uri.fromFile(out);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            startActivityForResult(intent, GlobalParam.REQUEST_GET_IMAGE_BY_CAMERA);
        }
    }

    /*
     * 从相册中选择
     */
    public void getImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");

        startActivityForResult(intent, GlobalParam.REQUEST_GET_URI);
    }

    /**
     * 调用系统裁剪功能
     *
     * @param uri
     *            文件URI
     */
    public void startPhotoZoom(Uri uri, int width, int height) {
        Intent intent = new Intent("com.android.camera.action.CROP");

        if (android.os.Build.VERSION.SDK_INT > 18) {
            String url=getPath(mContext, uri);
            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        }else{
            intent.setDataAndType(uri, "image/*");
        }

        //intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例

        if (width == 0 || height == 0) {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            // outputX outputY 是裁剪图片宽高
            intent.putExtra("outputX", 180);
            intent.putExtra("outputY", 180);
            intent.putExtra("return-data", true);
        }
        else{
            intent.putExtra("scale", true);
            intent.putExtra("scaleUpIfNeeded", true);

            intent.putExtra("aspectX", 3);
            intent.putExtra("aspectY", 2);
            // outputX outputY 是裁剪图片宽高
            intent.putExtra("outputX", width);
            intent.putExtra("outputY", height);

            String imagePath = Environment.getExternalStorageDirectory() +  FeatureFunction.PUB_TEMP_DIRECTORY + "album.jpg";
            File file = new File(imagePath);
            try {
                file.createNewFile();
            } catch (IOException e) {
            }

            Uri imageUri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true);
            intent.putExtra("return-data", false);
        }

        startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);
    }

    @SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT > 18;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
