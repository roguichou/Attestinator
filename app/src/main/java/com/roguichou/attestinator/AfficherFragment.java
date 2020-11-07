package com.roguichou.attestinator;

import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.tom_roush.pdfbox.io.RandomAccessBufferedFileInputStream;
import com.tom_roush.pdfbox.pdfparser.PDFParser;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AfficherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AfficherFragment extends Fragment {

    ImageView image;
    View fragmentView = null;

    public AfficherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment AfficherFragment.
     */
    public static AfficherFragment newInstance() {
        return new AfficherFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_afficher, container, false);

        fragmentView = view;

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        image = view.findViewById(R.id.pdf_renderer);

        String fichier = getArguments().getString("fichier");

        if( null == fichier)
        {
            view.setBackgroundColor(Color.BLACK);
            image.setImageBitmap(((MainActivity)getActivity()).getQrBitmap());
        }
        else {
            view.setBackgroundColor(Color.WHITE);
            InputStream input;
            PDDocument doc;
            try {
                File attestation = new File(getActivity().getFilesDir() + "/" + fichier);
                input = new FileInputStream(attestation);
                RandomAccessBufferedFileInputStream istream = new RandomAccessBufferedFileInputStream(input);
                PDFParser parser = new PDFParser(istream);
                parser.parse();
                doc = parser.getPDDocument();
            } catch (Exception e) {
                Snackbar mySnackbar = Snackbar.make(view, "Erreur à l'ouverture du fichier " + e.toString(), Snackbar.LENGTH_SHORT);
                mySnackbar.show();
                return;
            }

            PDFRenderer renderer = new PDFRenderer(doc);

            try {
                image.setImageBitmap(renderer.renderImage(0));
            } catch (IOException e) {
                Snackbar mySnackbar = Snackbar.make(view, "Erreur au rendu du fichier " + e.toString(), Snackbar.LENGTH_SHORT);
                mySnackbar.show();
                e.printStackTrace();
            }
        }



    }

}