package com.example.gluko_smart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class UserManual2 extends AppCompatActivity {

    private static final String FILE_NAME = "handbuch.pdf";
    //private static final int PAGE_INDEX = 0;
    Button button_homeUser;
    ImageView imageView;
    ImageButton button_next, button_previous;

    private int currentPage = 0;
    private int pageCount = 0;
    //private PdfRenderer renderer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usermanual2);

        imageView = findViewById(R.id.pdf_image);
        button_next = findViewById(R.id.pdfbutton_next);
        button_previous = findViewById(R.id.pdfbutton_back);

        button_homeUser = findViewById(R.id.button_homeUser);
        button_homeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent9 = new Intent(UserManual2.this, Home.class);
                startActivity(intent9);
            }
        });

        pageCount = getPageCount(this);
        if (pageCount > 0) {
            displayPdf(this, imageView, currentPage);

            // OnClickListener for previous Page Button
            button_previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentPage > 0) {
                        currentPage--;
                        displayPdf(UserManual2.this, imageView, currentPage);
                    }
                }
            });

            // OnClickListener for next Page Button
            button_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentPage < pageCount - 1) {
                        currentPage++;
                        displayPdf(UserManual2.this, imageView, currentPage);
                    }
                }
            });
        }
    }

    // Open PDF-File and receive PageCount
    public static int getPageCount(Context context) {
        try{
            File file = new File(context.getFilesDir(), FILE_NAME);

            if(!file.exists()){
                copyFileFromAssets(context, FILE_NAME, file);
            }


            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer renderer = new PdfRenderer(fileDescriptor);
            final int pageCount = renderer.getPageCount();

            //Clean up
            renderer.close();
            fileDescriptor.close();

            return pageCount;
        } catch (IOException ex){
            ex.printStackTrace();
            return 0;
        }
    }

    public static void displayPdf (Context context, ImageView imageView, int pageIndex){
        try{
            File file = new File(context.getFilesDir(), FILE_NAME);

            if(!file.exists()){
                copyFileFromAssets(context, FILE_NAME, file);
            }

            // Open PDF-File and receive PageCount
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer renderer = new PdfRenderer(fileDescriptor);

            //create Bitmap and Render Pages
            PdfRenderer.Page page = renderer.openPage(pageIndex);
            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            // Show Bitmap i ImageView
            imageView.setImageBitmap(bitmap);
            page.close();

            // clean up
            renderer.close();
            fileDescriptor.close();

        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    // This method copies a file from the app's assets folder to a specified file path
    private static void copyFileFromAssets(Context context, String fileName, File file) throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(fileName);
        FileOutputStream outputStream = new FileOutputStream(file);

        //A byte array buffer is used to read and write the file
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }
}
