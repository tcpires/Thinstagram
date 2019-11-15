package com.example.thiago.testeparse.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

public class MainActivity extends AppCompatActivity {


    private Toolbar toolbarPrincipal;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbarPrincipal = findViewById(R.id.toolbar_principal);

        toolbarPrincipal.setTitle("Fabio & Isabely");
        toolbarPrincipal.setTitleTextColor(R.color.preto);

        setSupportActionBar(toolbarPrincipal);

        slidingTabLayout = findViewById(R.id.sliding_tab_main);
        viewPager = findViewById(R.id.view_pager_main);
        TabsAdapter tabsAdapter = new TabsAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(tabsAdapter);
        verifyStoragePermissions(this);
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
            case R.id.action_compartilhar:
                initIntentTakenPicture();
                return true;
            case R.id.action_compartilhar_camera:
                initIntentTakenPhoto();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initIntentTakenPhoto() {
        Intent goToTakePhoto = new Intent(this, PostImageActivity.class);
        goToTakePhoto.putExtra("localRequest", 1);
        startActivity(goToTakePhoto);
    }

    private void initIntentTakenPicture() {
        Intent goToTakePhoto = new Intent(this, PostImageActivity.class);
        goToTakePhoto.putExtra("localRequest", 2);
        startActivity(goToTakePhoto);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1
                && resultCode == RESULT_OK
                && data != null) {
            Uri localImagemSelecionada = data.getData();
            String imageFile = getRealPathFromURI(localImagemSelecionada);
            Intent intent = getIntent(requestCode, resultCode, imageFile);
            intent.putExtra("uri", localImagemSelecionada.toString());

            startActivity(intent);

        } else if (requestCode == 2) {
            String mTempPhotoPathFirst = null;
            Intent intent = getIntent(requestCode, resultCode, mTempPhotoPathFirst);
            startActivity(intent);

        } else if (requestCode == 3 && requestCode == 0) {
            TabsAdapter adapterNovo = (TabsAdapter) viewPager.getAdapter();
            HomeFragment homeFragmentNovo = (HomeFragment) adapterNovo.getFragment(0);
            homeFragmentNovo.atualizaPostagens();
            Toast.makeText(getApplicationContext(), "Sua Imagem foi postada",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    private Intent getIntent(int requestCode, int resultCode, String imageFile) {
        Intent intent = new Intent(this, PostImageActivity.class);
        intent.putExtra("requestCode", requestCode);
        intent.putExtra("resultCode", resultCode);
        intent.putExtra("photoPath", imageFile);
        return intent;
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
        int permissionExternalStorage = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionExternalStorage != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}

