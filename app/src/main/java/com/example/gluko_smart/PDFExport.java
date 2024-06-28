package com.example.gluko_smart;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PDFExport {

    private Context context;

    public PDFExport(Context context) {
        this.context = context;
    }

    public void createPdf(List<GlucoseValues> glucoseValuesList, String userName) {
        // Sortiere die GlucoseValues-Liste nach dem Zeitstempel absteigend
        Collections.sort(glucoseValuesList, (v1, v2) -> v2.getTimestamp().compareTo(v1.getTimestamp()));

        // Erstelle ein neues Dokument
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        int entriesPerPage = 26;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());

                // Benötigte Seitenzahl berechnen
                int pageNumber = 1;
                int totalEntries = glucoseValuesList.size();
                int totalPages = (int) Math.ceil((double) totalEntries / entriesPerPage);

                // Schriftfarben
                Paint paint_text = new Paint();
                Paint paint_title = new Paint();
                Paint headerBackgroundPaint = new Paint();
                headerBackgroundPaint.setColor(ContextCompat.getColor(context,R.color.Tiara_light));// Hellblauer Hintergrund
                // Set Text Size of the Title
                paint_title.setColor(ContextCompat.getColor(context, R.color.Nepal));


                for(int i=0; i < totalEntries; i+= entriesPerPage) {

                    // Text Standardwerte
                    paint_title.setTextSize(20);
                    paint_text.setTextSize(14);
                    paint_text.setColor(Color.BLACK);
                    paint_text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

                    // Starte eine neue Seite
                    PdfDocument.Page page = document.startPage(pageInfo);

                    // Header-Bereich
                    page.getCanvas().drawRect(0, 0, page.getCanvas().getWidth(), 100, headerBackgroundPaint); // Hellblauer Hintergrund

                    // Header Bereich
                    // Logo
                    Bitmap gs_logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_ohne);
                    Bitmap gs_logo_scaled = Bitmap.createScaledBitmap(gs_logo, 90, 130, true);

                    // Blooddrop icon
                    Bitmap blooddrop = BitmapFactory.decodeResource(context.getResources(), R.drawable.bluttropfen);
                    Bitmap blooddrop_scaled = Bitmap.createScaledBitmap(blooddrop, 70, 80, true);

                    page.getCanvas().drawBitmap(gs_logo_scaled, 15, 5, null);
                    page.getCanvas().drawBitmap(blooddrop_scaled, 450, 10, null);

                    page.getCanvas().drawText("GlukoSmart - Glucose Report", 160, 30, paint_title);
                    page.getCanvas().drawText("Erstellt von: " + userName, 160, 50, paint_text);

                    // Erstellungsdatum
                    Log.d("PDFExport", "Erstellungsdatum: " + currentDateAndTime);
                    page.getCanvas().drawText("Erstellungsdatum: " + currentDateAndTime + " Uhr", 160, 70, paint_text);

                    // Legende unter dem Header
                    paint_text.setTextSize(10);
                    paint_text.setTypeface(Typeface.DEFAULT_BOLD);
                    page.getCanvas().drawText("Postprandiale Werte sind fettgedruckt", 180, 115, paint_text);
                    paint_text.setColor(Color.RED);
                    page.getCanvas().drawText("Werte über 130 oder unter 80 sind rot", 180, 130, paint_text);

                    // Tabelle mit den GlucoseValues
                    int startX = 15;
                    int startY = 160;
                    int cellHeight = 25;

                    // Tabellenkopf
                    paint_text.setTextSize(14);
                    paint_text.setColor(Color.GRAY);
                    paint_text.setTypeface(Typeface.DEFAULT);
                    page.getCanvas().drawText("Datum / Uhrzeit", startX, startY, paint_text);
                    page.getCanvas().drawText("Blutzuckerwert [in mg/dl]", startX + 200, startY, paint_text);
                    page.getCanvas().drawText("Kontext (Prandial)", startX + 400, startY, paint_text);

                    //Tabelleninhalt
                    paint_text.setTextSize(12);
                    paint_text.setColor(Color.BLACK);
                    startY += cellHeight;

                    for (int j= i; j < i + entriesPerPage && j < totalEntries; j++) {
                        GlucoseValues value = glucoseValuesList.get(j);
                        // Postprandiale Werte in Fett
                        if (value.getVorNachMahlzeit().equalsIgnoreCase("nach dem Essen")) {
                            paint_text.setTypeface(Typeface.DEFAULT_BOLD);
                        } else {
                            paint_text.setTypeface(Typeface.DEFAULT);
                        }
                        // Transformiere den Zeitstempel in ein lesbares Datum 2024-06-28T12:00 -> 28.06.2024 12:00 Uhr
                        String timestamp_simple = value.getTimestamp().replace("T", " / ").substring(0, 18)+" Uhr";
                        page.getCanvas().drawText(timestamp_simple, startX, startY, paint_text);
                        // Werte über 130 oder unter 80 in Rot
                        if (value.getBzWert() > 130 || value.getBzWert() < 80) {
                            paint_text.setColor(Color.RED); // Werte über 130 oder unter 80 in Rot
                        } else {
                            paint_text.setColor(Color.BLACK); // Normale Werte in Schwarz
                        }
                        page.getCanvas().drawText(String.valueOf(value.getBzWert()), startX + 200, startY, paint_text);
                        page.getCanvas().drawText(value.getVorNachMahlzeit(), startX + 400, startY, paint_text);
                        startY += cellHeight;
                    }

                    // Seitenzahl hinzufügen
                    paint_text.setTypeface(Typeface.DEFAULT);
                    paint_text.setColor(Color.GRAY);
                    paint_text.setTextSize(10);
                    page.getCanvas().drawText("Seite " + pageNumber + " von " + totalPages, 270, 820, paint_text);

                    document.finishPage(page);
                    pageNumber++;

                }



        // Dokumenten-Verzeichnis laden
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        Log.d("PDFExport", "Dokumentenverzeichnis: " + documentsDir.getAbsolutePath());

        // Ziel-Verzeichnis auf dem Gerät
        String directoryPath = documentsDir.getAbsolutePath() + "/GlukoSmart/PDF Berichte/";
        Log.d("PDFExport", "Verzeichnis: " + directoryPath);

        // Verzeichnis erstellen, falls es nicht existiert
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                Log.d("PDFExport", "Verzeichnis erstellt: " + directoryPath);
            } else {
                Log.e("PDFExport", "Fehler beim Erstellen des Verzeichnisses: " + directoryPath);
                return;
            }
        }else {
            Log.d("PDFExport", "GS Verzeichnis existiert bereits: " + directoryPath);
        }


        // Name der PDF-Datei
        // Erstellen Sie einen Dateinamen, der auf dem aktuellen Datum basiert (z. B. "GlucoseSmart Bericht 28062024")
        String dateForFileName = currentDateAndTime.substring(0, 16).replace(".", "").replace(":","");
        String fileName = "GlucoseSmart Bericht " + dateForFileName +".pdf";
        String filePath = directoryPath + fileName;
        File file = new File(filePath);

        try {
            document.writeTo(new FileOutputStream(file));
            Log.d("PDFExport", "PDF wurde erfolgreich in Dokumenten gespeichert: " + filePath);
            //openPdf(file);
        } catch (IOException e) {
            Log.e("PDFExport", "Fehler beim Schreiben des PDFs: " + e.toString());
        } finally {
            document.close();
        }
        if (file.exists() && file.canRead()) {
            Log.d("PDFExport", "File exists and is readable: " + file.getAbsolutePath());
            openPdf(file);
        } else {
            Log.e("PDFExport", "File does not exist or is not readable: " + file.getAbsolutePath());
        }
        openPdf(file);
    }

    private void openPdf(File file) {
        try {

            MyFileProvider myFileProvider = new MyFileProvider();
            Uri uri = myFileProvider.getUriForFile(context, "com.example.gluko_smart.provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);

            Log.d("PDFExport", "File path: " + file.getAbsolutePath());
            Log.d("PDFExport", "Uri: " + uri.toString());
            Log.d("PDFExport", "Intent: " + intent.getAction());

            //context.startActivity(intent);

            PackageManager pm = context.getPackageManager();
            Log.d("PDFExport", "PackageManager: " + pm.toString());
            List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
            for (ResolveInfo activity : activities) {
                Log.d("PDFExport", "Activity: " + activity.activityInfo.name);
            }
            boolean isIntentSafe = activities.size() > 0;

            if (isIntentSafe) {
                context.startActivity(intent);
            } else {
                Log.e("PDFExport", "No suitable app found to open the PDF file.");
            }
        } catch (IllegalArgumentException e) {
            Log.e("PDFExport", "Error getting URI for file: " + e.getMessage());
            // You might want to show a user-friendly error message here
        }
    }
}