package com.example.thiago.testeparse.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.thiago.testeparse.R;
import com.example.thiago.testeparse.adapter.TabsAdapter;
import com.example.thiago.testeparse.fragments.HomeFragment;
import com.example.thiago.testeparse.util.SlidingTabLayout;
import com.parse.ParseUser;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    private Toolbar toolbarPrincipal;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private String mCurrentPhotoPathFirst;
    private String mTempPhotoPathFirst;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbarPrincipal = (Toolbar) findViewById(R.id.toolbar_principal);
        toolbarPrincipal.setLogo(R.drawable.instagramlogo);
        setSupportActionBar(toolbarPrincipal);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tab_main);
        viewPager = (ViewPager) findViewById(R.id.view_pager_main);

        TabsAdapter tabsAdapter = new TabsAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(tabsAdapter);
        slidingTabLayout.setCustomTabView(R.layout.tab_view, R.id.text_item_tab);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.cinzaEscuro));
        slidingTabLayout.setViewPager(viewPager);

        verifyStoragePermissions(this);

    }

    private void retunPostActivity() {



//        byteArray =  getIntent().getExtras().getByteArray("byteArray");
//        nomeImagem = getIntent().getExtras().getString("nomeImagem");
//
//        if(byteArray != null & nomeImagem != null ) {
//
//            ParseObject parseObject = new ParseObject("Imagem");
//            ParseFile arquivoParse = new ParseFile(nomeImagem + "imagem.png", byteArray);
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
//
//        } else return;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_sair:
                deslogarUsuario();
                return true;
            case R.id.action_configuracoes:
                return true;
            case R.id.action_compartilhar:
                compartilharFotos();
                return true;
            case R.id.action_compartilhar_camera:
                getTakenImageFirst();
            default:
                return super.onOptionsItemSelected(item);
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

            startActivityForResult(takePictureIntent, 2);
        }
    }

    private void compartilharFotos() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri localImagemSelecionada = data.getData();
            String imageFile = getRealPathFromURI(localImagemSelecionada);

            Intent intent = new Intent(this, PostImageActivity.class);
            intent.putExtra("requestCode", requestCode);
            intent.putExtra("resultCode", resultCode);
            intent.putExtra("intent", imageFile);
            intent.putExtra("uri", localImagemSelecionada.toString());

            startActivity(intent);
        } else if (requestCode == 3) {

            TabsAdapter adapterNovo = (TabsAdapter) viewPager.getAdapter();
            HomeFragment homeFragmentNovo = (HomeFragment) adapterNovo.getFragment(0);
            homeFragmentNovo.atualizaPostagens();
            Toast.makeText(getApplicationContext(), "Sua Imagem foi postada",
                    Toast.LENGTH_SHORT).show();
        }


//            SimpleDateFormat dateFormat = new SimpleDateFormat("ddmmaaahhmmss");
//            String nomeImagem = dateFormat.format(new Date());
//            ParseObject parseObject = new ParseObject("Imagem");
//            ExifInterface exif = null;
//            Bitmap imagem = null;
//            try {
//                imagem = MediaStore.Images.Media
//                        .getBitmap(getContentResolver(), localImagemSelecionada);
//                exif = new ExifInterface(imageFile);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//                Bitmap imgRotate = ImageHelper.rotateBitmap(imagem, rotation);
//
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                imgRotate.compress(Bitmap.CompressFormat.PNG, 70, stream);
//
//                byte[] byteArray = stream.toByteArray();
//
//                ParseFile arquivoParse = new ParseFile(nomeImagem + "imagem.png", byteArray);
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
//        } else if(requestCode == 2 && resultCode == RESULT_OK && data != null){
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
//        } else{
//            return;
//        }
    }

    private void deslogarUsuario() {
        ParseUser.logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
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

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permissionExternalStorage
        int permissionExternalStorage = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionExternalStorage != PackageManager.PERMISSION_GRANTED) {
            // We don't have permissionExternalStorage so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}

