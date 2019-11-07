package com.example.thiago.testeparse.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.thiago.testeparse.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostImageActivity extends AppCompatActivity {

    private ImageView photo;
    private Button btPostar;
    private String nomeImagem = null;
    private String photoPath = null;
    private ExifInterface exif = null;
    SimpleDateFormat dateFormat = new SimpleDateFormat("ddmmaaahhmmss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_image);

        int requestCode = getIntent().getExtras().getInt("requestCode");
        int resultCode = getIntent().getExtras().getInt("resultCode");
        photoPath = getIntent().getExtras().getString("photoPath");

        photo = findViewById(R.id.foto_postagem);
        btPostar = findViewById(R.id.bt_postar);
        nomeImagem= dateFormat.format(new Date());

        takeImageFromDevice(requestCode, resultCode);

    }

    public void takeImageFromDevice(int requestCode, int resultCode) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = Uri.parse(getIntent().getExtras().getString("uri"));

            Bitmap imgRotate = getBitmapFromUri(uri);

            final byte[] byteArray = getBytes(imgRotate);

            final ParseObject parseObject = new ParseObject("Imagem");
            final ParseFile arquivoParse = new ParseFile(nomeImagem + "imagem.png", byteArray);
            parseObject.put("username", ParseUser.getCurrentUser().getUsername());
            parseObject.put("imagem", arquivoParse);

            postarImagem(parseObject);
        } else if (requestCode == 2){

            Bitmap imgRotate = getBitmapFromString(photoPath);

            final byte[] byteArray = getBytes(imgRotate);

            final ParseObject parseObject = new ParseObject("Imagem");
            final ParseFile arquivoParse = new ParseFile(nomeImagem + "imagem.png", byteArray);
            parseObject.put("username", ParseUser.getCurrentUser().getUsername());
            parseObject.put("imagem", arquivoParse);

            postarImagem(parseObject);
        }
    }

    private void postarImagem(final ParseObject parseObject) {
        final Intent returnIntent = new Intent(this, MainActivity.class);
        btPostar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            startActivityForResult(returnIntent, 3);
                        } else {
                            Toast.makeText(getApplicationContext(), "Erro ao postar imagem - Tente Novamente!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });
    }

    private byte[] getBytes(Bitmap imgRotate) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imgRotate.compress(Bitmap.CompressFormat.PNG, 70, stream);
        return stream.toByteArray();
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
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
