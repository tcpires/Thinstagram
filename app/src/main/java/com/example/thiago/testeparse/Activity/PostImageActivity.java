package com.example.thiago.testeparse.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.thiago.testeparse.R;
import com.example.thiago.testeparse.adapter.TabsAdapter;
import com.example.thiago.testeparse.fragments.HomeFragment;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostImageActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private String mCurrentPhotoPathFirst;
    private String mTempPhotoPathFirst;
    private Integer requestCode;
    private Integer resultiCode;
    private String intentData;
    private Uri uri;
    private ImageView photo;
    private Button btPostar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_image);

        this.requestCode = getIntent().getExtras().getInt("requestCode");
        this.resultiCode = getIntent().getExtras().getInt("resultCode");
        this.intentData = getIntent().getExtras().getString("intent");
        this.uri = Uri.parse(getIntent().getExtras().getString("uri"));
        this.photo = findViewById(R.id.foto_postagem);
        this.btPostar = findViewById(R.id.bt_postar);



        takeImageFromDevice(requestCode, resultiCode, uri);

    }

    public void takeImageFromDevice (int requestCode, int resultCode, Uri uri) {
        if (requestCode == 1 && resultCode == RESULT_OK && uri != null) {
            String imageFile = getRealPathFromURI(uri);

            ExifInterface exif = null;
            try {
                exif = exif = new ExifInterface(imageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap imagem = BitmapFactory.decodeFile(imageFile, bmOptions);

            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Bitmap imgRotate = ImageHelper.rotateBitmap(imagem, rotation);

            photo.setImageBitmap(imgRotate);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imgRotate.compress(Bitmap.CompressFormat.PNG, 70, stream);
            final byte[] byteArray = stream.toByteArray();
            final Intent returnIntent = new Intent(this, MainActivity.class);
            SimpleDateFormat dateFormat = new SimpleDateFormat("ddmmaaahhmmss");
            String nomeImagem = dateFormat.format(new Date());

            final ParseObject parseObject = new ParseObject("Imagem");
            final ParseFile arquivoParse = new ParseFile(nomeImagem + "imagem.png", byteArray);
            parseObject.put("username", ParseUser.getCurrentUser().getUsername());

            btPostar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    parseObject.put("imagem", arquivoParse);
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



//
//            ParseFile arquivoParse = new ParseFile(nomeImagem + "imagem.png", byteArray);
//            parseObject.put("username", ParseUser.getCurrentUser().getUsername());
//            parseObject.put("imagem", arquivoParse);


//            parseObject.saveInBackground(new SaveCallback() {
//                @Override
//                public void done(ParseException e) {
//                    if (e == null) {
//                        Toast.makeText(getApplicationContext(), "Sua Imagem foi postada",
//                                Toast.LENGTH_SHORT).show();
//                        TabsAdapter adapterNovo = (TabsAdapter) viewPager.getAdapter();
//                        HomeFragment homeFragmentNovo = (HomeFragment) adapterNovo.getFragment(0);
//                        homeFragmentNovo.atualizaPostagens();
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Erro ao postar imagem - Tente Novamente!",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
//            mCurrentPhotoPathFirst = mTempPhotoPathFirst;
//
//            Bitmap imageBmp = ImageHelper.decodeSampledBitmapFromResource
//                    (mCurrentPhotoPathFirst, 300, 300);
//            try {
//                ExifInterface exif = new ExifInterface(mCurrentPhotoPathFirst);
//                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//
//                Bitmap imgRotate = ImageHelper.rotateBitmap(imageBmp, rotation);
//
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                imgRotate.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] byteArray = stream.toByteArray();
//
//                SimpleDateFormat dateFormat = new SimpleDateFormat("ddmmaaahhmmss");
//                String nomeImagem = dateFormat.format(new Date());
//                ParseFile arquivoParse = new ParseFile(nomeImagem + "imagem.png", byteArray);
//
//                ParseObject parseObject = new ParseObject("Imagem");
//                parseObject.put("username", ParseUser.getCurrentUser().getUsername());
//                parseObject.put("imagem", arquivoParse);
//                parseObject.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(ParseException e) {
//                        if (e == null) {
//                            Toast.makeText(getApplicationContext(), "Sua Imagem foi postada",
//                                    Toast.LENGTH_SHORT).show();
//                            TabsAdapter adapterNovo = (TabsAdapter) viewPager.getAdapter();
//                            HomeFragment homeFragmentNovo = (HomeFragment) adapterNovo.getFragment(0);
//                            homeFragmentNovo.atualizaPostagens();
//                        } else {
//                            Toast.makeText(getApplicationContext(), "Erro ao postar imagem - Tente Novamente!",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            return;
        }
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
