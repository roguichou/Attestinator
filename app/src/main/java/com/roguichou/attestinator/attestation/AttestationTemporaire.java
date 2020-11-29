package com.roguichou.attestinator.attestation;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
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

//TODO gérer les profils
public class AttestationTemporaire extends Attestation {

    //taille du texte
    private static final float PDF_FONT_SZ = 11f;
    private static final float PDF_REASON_SZ = 12f;

    //position Nom Prénom
    private static final Point PDF_IDENTITY_POS = new Point (92, 702);

    //Position date de naissance
    private static final Point PDF_BIRTHDAY_POS = new Point (92, 684);

    //Position lieu de naissance
    private static final Point PDF_BIRTHPLACE_POS = new Point (214, 684);

    //adresse
    private static final Point PDF_ADDRESS_POS = new Point (104, 665);

    //position en X des croix
    private static final int PDF_REASON_X = 47;

    //fait à
    private static final Point PDF_LOCATION_POS = new Point (83, 76);

    //date de sortie
    private static final Point PDF_DATE_POS = new Point (63, 58);

    //heure de sortie
    private static final Point PDF_HEURE_POS = new Point (227, 58);



    //clé de Préférences
    private static final String KEY_RAISON = "raison";
    private static final String KEY_H = "heure";
    private static final String KEY_MIN = "min";

    private Calendar heureSortie = null;
    private Raison raison = null;

    private Bitmap qrBitmap = null;
    private Profil profil;

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
        raison = Raison.fromString(raison_s);
        if(null!= raison) {
            int h = settings.getInt(KEY_H, 0);
            int min = settings.getInt(KEY_MIN, 0);
            heureSortie = Calendar.getInstance();
            heureSortie.set(Calendar.HOUR_OF_DAY, h);
            heureSortie.set(Calendar.MINUTE, min);

            genererQRcode(null, qr_size);

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



    private Bitmap genererQRcode(View view, int sz)
    {
        Bitmap bmp = null;
        SimpleDateFormat frmt = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
        String today = frmt.format(heureSortie.getTime());
        String data = "Cree le: " + today + " a " + getTimeAsString() + ";\n" +
                "Nom: " + profil.getName() + "\n" + "Prenom: " + profil.getFirstName() + ";\n" +
                "Naissance: " + profil.getBirthDate() + " a " + profil.getBirthLocation() + ";\n" +
                "Adresse: " + profil.getAddress() + " " + profil.getPostCode() + " " + profil.getCity() + ";\n" +
                "Sortie: " + today + " a " + getTimeAsString() + ";\n" +
                "Motifs: " + raison.toString()+";\n";


        QRGEncoder qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, sz);

        try {
            bmp = qrgEncoder.encodeAsBitmap();
        } catch (WriterException e) {
            if(view!= null) {
                Snackbar mySnackbar = Snackbar.make(view, "Erreur à la génération de l'attestation", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
            e.printStackTrace();
        }

        return bmp;
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

        PDPage page = doc.getPage(0);

        try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, true, true, true)) {
            contentStream.beginText();
            contentStream.setFont(font, PDF_FONT_SZ);
            contentStream.setTextMatrix(Matrix.getTranslateInstance(PDF_IDENTITY_POS.x, PDF_IDENTITY_POS.y));
            contentStream.showText(profil.getName() + " " + profil.getFirstName());

            contentStream.setTextMatrix(Matrix.getTranslateInstance(PDF_BIRTHDAY_POS.x, PDF_BIRTHDAY_POS.y));
            contentStream.showText(profil.getBirthDate());

            contentStream.setTextMatrix(Matrix.getTranslateInstance(PDF_BIRTHPLACE_POS.x, PDF_BIRTHPLACE_POS.y));
            contentStream.showText(profil.getBirthLocation());

            contentStream.setTextMatrix(Matrix.getTranslateInstance(PDF_ADDRESS_POS.x, PDF_ADDRESS_POS.y));
            contentStream.showText(profil.getAddress() + " " + profil.getPostCode() + " " + profil.getCity());

            contentStream.setTextMatrix(Matrix.getTranslateInstance(PDF_LOCATION_POS.x, PDF_LOCATION_POS.y));
            contentStream.showText(profil.getCity());

            contentStream.setTextMatrix(Matrix.getTranslateInstance(PDF_DATE_POS.x, PDF_DATE_POS.y));
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat frmt = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
            String today = frmt.format(calendar.getTime());
            contentStream.showText(today);

            contentStream.setTextMatrix(Matrix.getTranslateInstance(PDF_HEURE_POS.x, PDF_HEURE_POS.y));
            contentStream.showText(getTimeAsString());


            contentStream.setFont(font, PDF_REASON_SZ);
            contentStream.setTextMatrix(Matrix.getTranslateInstance(PDF_REASON_X, raison.getPosition_y()));
            contentStream.showText("x");
            contentStream.endText();

            Bitmap bitmap = genererQRcode(view, 150);
            PDImageXObject image = LosslessFactory.createFromImage(doc, bitmap);
            PDRectangle pageSize = page.getMediaBox();
            contentStream.drawImage(image, pageSize.getWidth() - 156, 25);
            contentStream.close();

            Bitmap bitmap2  = genererQRcode(view, 300);
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

    public void genererAttestation(View view, Profil profil, Raison raison, int h, int min) {
        isValid = true;
        this.raison= raison;
        this.profil = profil;
        heureSortie = Calendar.getInstance();
        heureSortie.set(Calendar.HOUR_OF_DAY, h);
        heureSortie.set(Calendar.MINUTE, min);

        save_attestation_temporaire(h, min);

        genererPDF(view);
        qrBitmap = genererQRcode(view, qr_size);

        if(view!= null)
        {
            Snackbar mySnackbar = Snackbar.make(view, "Attestation générée", Snackbar.LENGTH_SHORT);
            mySnackbar.show();
        }
    }

    public Profil getProfil() { return profil; }

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
