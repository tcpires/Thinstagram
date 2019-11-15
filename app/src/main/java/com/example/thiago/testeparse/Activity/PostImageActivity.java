package com.example.thiago.testeparse.Activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.thiago.testeparse.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostImageActivity extends AppCompatActivity {

    private static final int PHOTO_CAMERA = 5;
    private static final int PHOTO_GALERY = 6;
    private static int LOCAL_REQUEST = 0;
    private ImageView photo;
    private Button btPostar;
    private String mCurrentPhotoPathFirst;
    private String mTempPhotoPathFirst;
    private ExifInterface exif = null;
    private ProgressBar pbPostagem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_image);

        photo = findViewById(R.id.foto_postagem);
        btPostar = findViewById(R.id.bt_postar);
        pbPostagem = findViewById(R.id.pbPostagem);
        pbPostagem.setVisibility(View.GONE);

        if(LOCAL_REQUEST==0 & getIntent().getExtras() != null){
            LOCAL_REQUEST = getIntent().getExtras().getInt("localRequest");
    }
        if (LOCAL_REQUEST == 1) {
            getTakenImageFirst();
        } else if (LOCAL_REQUEST == 2) {
            compartilharFotos();
        }
    }

    private void getTakenImageFirst() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile;
            try {
                photoFile = ImageHelper.createImageFile();
            } catch (IOException e) {
                return;
            }
            if (photoFile == null) {
                return;
            }
            mTempPhotoPathFirst = photoFile.getAbsolutePath();
            Uri photoUri =
                    FileProvider.getUriForFile(
                            this, getApplicationContext().getPackageName() + ".provider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

            startActivityForResult(takePictureIntent, PHOTO_CAMERA);
        }
    }

    private void compartilharFotos() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PHOTO_GALERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_GALERY && data != null) {
            Uri localImagemSelecionada = data.getData();

            Bitmap imagem = getBitmapFromUri(localImagemSelecionada);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imagem.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            SimpleDateFormat dateFormat = new SimpleDateFormat("ddmmaaahhmmss");
            String nomeImagem = dateFormat.format(new Date());
            ParseFile arquivoParse = new ParseFile(nomeImagem + "imagem.png", byteArray);

            ParseObject parseObject = new ParseObject("Imagem");
            parseObject.put("username", ParseUser.getCurrentUser().getUsername());
            parseObject.put("imagem", arquivoParse);
            postarImagem(parseObject);


        } else if (requestCode == PHOTO_CAMERA && mCurrentPhotoPathFirst==null) {

            mCurrentPhotoPathFirst = mTempPhotoPathFirst;

            SimpleDateFormat dateFormat = new SimpleDateFormat("ddmmaaahhmmss");
            String nomeImagem = dateFormat.format(new Date());
            Bitmap imagem = getBitmapFromString(mTempPhotoPathFirst);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imagem.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            ParseFile arquivoParse = new ParseFile(nomeImagem + "imagem.png", byteArray);

            ParseObject parseObject = new ParseObject("Imagem");
            parseObject.put("username", ParseUser.getCurrentUser().getUsername());
            parseObject.put("imagem", arquivoParse);
            postarImagem(parseObject);
        }
        else {
            returnToMain();
        }
    }

    private void returnToMain() {
        final Intent returnIntent = new Intent(this, MainActivity.class);
        startActivityForResult(returnIntent, 0);
    }

    private void postarImagem(final ParseObject parseObject) {
        final Intent returnIntent = new Intent(this, MainActivity.class);
        btPostar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbPostagem.setVisibility(View.VISIBLE);
                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            startActivityForResult(returnIntent, 3);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Erro ao postar imagem - Tente Novamente!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        String imageFile = getRealPathFromURI(uri);
        try {
            exif = new ExifInterface(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap imagem = BitmapFactory.decodeFile(imageFile, bmOptions);

        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        Bitmap imgRotate = ImageHelper.rotateBitmap(imagem, rotation);

        photo.setImageBitmap(imgRotate);
        return imgRotate;
    }

    private Bitmap getBitmapFromString(String photoPath) {
        try {
            exif = new ExifInterface(photoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap imagem = BitmapFactory.decodeFile(photoPath, bmOptions);

        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        Bitmap imgRotate = ImageHelper.rotateBitmap(imagem, rotation);

        photo.setImageBitmap(imgRotate);
        return imgRotate;
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
}
