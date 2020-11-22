package com.roguichou.attestinator.attestation;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.WriterException;
import com.roguichou.attestinator.Profil;
import com.roguichou.attestinator.R;
import com.tom_roush.pdfbox.io.RandomAccessBufferedFileInputStream;
import com.tom_roush.pdfbox.pdfparser.PDFParser;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.tom_roush.pdfbox.util.Matrix;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class AttestationTemporaire extends Attestation {

    //clé de Préférences
    private static final String KEY_RAISON = "raison";
    private static final String KEY_H = "heure";
    private static final String KEY_MIN = "min";

    private Calendar heureSortie = null;
    private Raison raison = null;

    private Bitmap qrBitmap = null;
    private final Profil profil;

    private boolean isValid = false;
    private SharedPreferences settings;
    private final Context context;
    private final int qr_size;

    public AttestationTemporaire(Context context, Profil profil, int qr_size)
    {
        this.qr_size = qr_size;
        this.context = context;
        this.profil = profil;
        fileType = FILE_TYPE_NONE;
        filename ="attestation.pdf";
    }

    public void parseSettings(SharedPreferences settings)
    {
        this.settings = settings;
        String raison_s = settings.getString(KEY_RAISON, null);
        if(null!= raison_s) {
            raison = Raison.fromString(raison_s);

            int h = settings.getInt(KEY_H, 0);
            int min = settings.getInt(KEY_MIN, 0);
            heureSortie = Calendar.getInstance();
            heureSortie.set(Calendar.HOUR_OF_DAY, h);
            heureSortie.set(Calendar.MINUTE, min);

            genererQRcode(null);

            isValid = true;
        }
    }

    public void setFileType(int fileType) {this.fileType = fileType;}



    public Bitmap getQrBitmap() {
        return qrBitmap;
    }


    private String getTimeAsString()
    {
        return String.format(Locale.FRENCH, "%02d:%02d",  heureSortie.get(Calendar.HOUR_OF_DAY), heureSortie.get(Calendar.MINUTE));
    }


    private void genererQRcode(View view)
    {
        SimpleDateFormat frmt = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
        String today = frmt.format(heureSortie.getTime());
        String data = "Cree le: " + today + " a " + getTimeAsString() + ";\n" +
                "Nom: " + profil.getProfilName() + "\n" + "Prenom: " + profil.getProfilFirstName() + ";\n" +
                "Naissance: " + profil.getProfilBirthDate() + " a " + profil.getProfilBirthLocation() + ";\n" +
                "Adresse: " + profil.getProfilAddress() + " " + profil.getProfilPostCode() + " " + profil.getProfilCity() + ";\n" +
                "Sortie: " + today + " a " + getTimeAsString() + ";\n" +
                "Motifs: " + raison.toString()+";\n";


        QRGEncoder qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, qr_size);

        try {
            qrBitmap = qrgEncoder.encodeAsBitmap();
        } catch (WriterException e) {
            if(view!= null) {
                Snackbar mySnackbar = Snackbar.make(view, "Erreur à la génération de l'attestation", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
            e.printStackTrace();
        }


        if(view!= null)
        {
            Snackbar mySnackbar = Snackbar.make(view, "Attestation générée", Snackbar.LENGTH_SHORT);
            mySnackbar.show();
        }
    }

    private void genererPDF(View view) {
        InputStream input;
        PDDocument doc;
        try {
            input = context.getResources().openRawResource(R.raw.certificate);
            RandomAccessBufferedFileInputStream istream = new RandomAccessBufferedFileInputStream(input);
            PDFParser parser = new PDFParser(istream);
            parser.parse();
            doc = parser.getPDDocument();
        } catch (Exception e) {
            Snackbar mySnackbar = Snackbar.make(view, "Erreur à l'ouverture du modèle " + e.toString(), Snackbar.LENGTH_SHORT);
            mySnackbar.show();
            return;
        }

        PDFont font = PDType1Font.HELVETICA;
        float fontSize = 11.0f;

        PDPage page = doc.getPage(0);

        try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, true, true, true)) {
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.setTextMatrix(Matrix.getTranslateInstance(119, 696));
            contentStream.showText(profil.getProfilName() + " " + profil.getProfilFirstName());

            contentStream.setTextMatrix(Matrix.getTranslateInstance(119, 674));
            contentStream.showText(profil.getProfilBirthDate());

            contentStream.setTextMatrix(Matrix.getTranslateInstance(297, 674));
            contentStream.showText(profil.getProfilBirthLocation());

            contentStream.setTextMatrix(Matrix.getTranslateInstance(133, 652));
            contentStream.showText(profil.getProfilAddress() + " " + profil.getProfilPostCode() + " " + profil.getProfilCity());

            contentStream.setTextMatrix(Matrix.getTranslateInstance(105, 177));
            contentStream.showText(profil.getProfilCity());

            contentStream.setTextMatrix(Matrix.getTranslateInstance(91, 153));
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat frmt = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
            String today = frmt.format(calendar.getTime());
            contentStream.showText(today);

            contentStream.setTextMatrix(Matrix.getTranslateInstance(264, 153));
            contentStream.showText(getTimeAsString());


            contentStream.setFont(font, 18.0f);
            contentStream.setTextMatrix(Matrix.getTranslateInstance(78, raison.getPosition_y()));
            contentStream.showText("x");
            contentStream.endText();

            String data = "Cree le: " + today + " a " + getTimeAsString() + "\n" +
                    "Nom: " + profil.getProfilName() + "\n" + "Prenom: " + profil.getProfilFirstName() + "\n" +
                    "Naissance: " + profil.getProfilBirthDate() + " a " + profil.getProfilBirthLocation() + "\n" +
                    "Adresse: " + profil.getProfilAddress() + " " + profil.getProfilPostCode() + " " + profil.getProfilCity() + "\n" +
                    "Sortie: " + today + " a " + getTimeAsString() + "\n" +
                    "Motifs: " + raison.toString();

            QRGEncoder qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, 96);
            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            PDImageXObject image = LosslessFactory.createFromImage(doc, bitmap);
            PDRectangle pageSize = page.getMediaBox();
            contentStream.drawImage(image, pageSize.getWidth() - 156, 100);
            contentStream.close();

            QRGEncoder qrgEncoder2 = new QRGEncoder(data, null, QRGContents.Type.TEXT, 300);
            Bitmap bitmap2 = qrgEncoder2.encodeAsBitmap();
            PDImageXObject image2 = LosslessFactory.createFromImage(doc, bitmap2);

            doc.addPage(new PDPage(PDRectangle.A4));
            PDPage page2 = doc.getPage(1);
            PDPageContentStream contentStream2 = new PDPageContentStream(doc, page2, false, true, true);
            pageSize = page2.getMediaBox();
            contentStream2.drawImage(image2, 50, pageSize.getHeight() - 350);
            contentStream2.close();
        } catch (Exception e) {
            Snackbar mySnackbar = Snackbar.make(view, "Erreur à l'édition du PDF " + e.toString(), Snackbar.LENGTH_SHORT);
            Log.e("Atestation-inator", e.toString());
            mySnackbar.show();
            return;
        }


        try (FileOutputStream fos = context.getApplicationContext().openFileOutput("attestation.pdf", Context.MODE_PRIVATE)) {
            doc.save(fos);
            doc.close();
            input.close();
        } catch (Exception e) {
            Snackbar mySnackbar = Snackbar.make(view, "Erreur à l'écriture du PDF " + e.toString(), Snackbar.LENGTH_SHORT);
            mySnackbar.show();
            return;
        }

        Snackbar mySnackbar = Snackbar.make(view, "Attestation générée", Snackbar.LENGTH_SHORT);
        mySnackbar.show();
    }

    public void genererAttestation(View view, Raison raison, int h, int min) {
        isValid = true;
        this.raison= raison;
        heureSortie = Calendar.getInstance();
        heureSortie.set(Calendar.HOUR_OF_DAY, h);
        heureSortie.set(Calendar.MINUTE, min);

        save_attestation_temporaire(h, min);

        genererQRcode(view);
        genererPDF(view);
    }

    private void save_attestation_temporaire(int h, int min) {
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putString(KEY_RAISON, raison.toString());
        editor.putInt(KEY_H, h);
        editor.putInt(KEY_MIN, min);
        editor.apply();
    }


    public boolean isValid(){return isValid;}

    public Calendar getHeureSortie() {
        return heureSortie;
    }

}
