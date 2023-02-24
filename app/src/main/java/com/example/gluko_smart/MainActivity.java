package com.example.gluko_smart;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    // The duration of the splash screen in milliseconds
    public final int LOAD_TIME = 3000;
    // The request code for the READ_EXTERNAL_STORAGE permission
    public static final int REQUEST_READ_EXTERNAL_STORAGE = 5;

    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = findViewById(R.id.mainLayout);
        showAppPreview();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            // Request for Storage permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start AppPreview Activity.
                Snackbar.make(mLayout, R.string.StorageAccessGranted,
                                Snackbar.LENGTH_SHORT)
                        .show();
                startApplication();
            } else {
                // Permission request was denied.
                Snackbar.make(mLayout, R.string.AccessWasDenied,
                                Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void showAppPreview() {
        // Check if the ReadStorage permission has been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start App preview
            Snackbar.make(mLayout, R.string.StorageAccessAlreadyGranted,
                    Snackbar.LENGTH_SHORT).show();
            startApplication();
        } else {
            // Permission is missing and must be requested.
            requestReadExtStoragePermission();
        }
    }

    private void requestReadExtStoragePermission() {

        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(mLayout, R.string.StorageAccessIsRequired,
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_READ_EXTERNAL_STORAGE);
                }
            }).show();

        } else {
            Snackbar.make(mLayout, R.string.AccessNotPossible, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    // Start the application after a delay
    public void startApplication() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        }, LOAD_TIME);
    }

}