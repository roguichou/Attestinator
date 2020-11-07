package com.roguichou.attestinator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.tom_roush.pdfbox.io.RandomAccessBufferedFileInputStream;
import com.tom_roush.pdfbox.pdfparser.PDFParser;
import com.tom_roush.pdfbox.pdmodel.PDDocument;

import java.io.FileOutputStream;
import java.io.InputStream;


//Profil
public class SecondFragment extends Fragment {


    private  static final int PICK_FILE = 0x12;

    private View fragmentView = null;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }


    @Override
    public  void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == PICK_FILE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.

            if (resultData != null) {
                Uri attestation_dom = resultData.getData();
                try {
                    InputStream inputStream = getContext().getContentResolver().openInputStream(attestation_dom);
                    RandomAccessBufferedFileInputStream istream = new RandomAccessBufferedFileInputStream(inputStream);
                    PDFParser parser = new PDFParser(istream);
                    parser.parse();
                    PDDocument doc = parser.getPDDocument();

                    FileOutputStream fos = getContext().openFileOutput("att_dom.pdf", Context.MODE_PRIVATE);
                    doc.save(fos);
                    inputStream.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentView = view;

        String profil_name = ((MainActivity)getActivity()).getProfilName();
        String profil_first_name = ((MainActivity)getActivity()).getProfilFirstName();
        String profil_birth_date = ((MainActivity)getActivity()).getProfilBirthDate();
        String profil_birth_location = ((MainActivity)getActivity()).getProfilBirthLocation();
        String profil_address = ((MainActivity)getActivity()).getProfilAddress();
        String profil_post_code = ((MainActivity)getActivity()).getProfilPostCode();
        String profil_city = ((MainActivity)getActivity()).getProfilCity();

        if (null != profil_name) {
            ((EditText) view.findViewById(R.id.editTextTextPersonName)).setText(profil_name);
        }
        if (null != profil_first_name) {
            ((EditText) view.findViewById(R.id.editTextTextPersonName2)).setText(profil_first_name);
        }
        if (null != profil_birth_date) {
            ((EditText) view.findViewById(R.id.editTextDate)).setText(profil_birth_date);
        }
        if (null != profil_birth_location) {
            ((EditText) view.findViewById(R.id.editTextBirthPlace)).setText(profil_birth_location);
        }
        if (null != profil_address) {
            ((EditText) view.findViewById(R.id.editTextTextPostalAddress2)).setText(profil_address);
        }
        if (null != profil_post_code) {
            ((EditText) view.findViewById(R.id.editTextCP)).setText(profil_post_code);
        }
        if (null != profil_city) {
            ((EditText) view.findViewById(R.id.editTextCity)).setText(profil_city);
        }


        view.findViewById(R.id.attestation_dom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf");

                startActivityForResult(intent, PICK_FILE);
            }
        });

        view.findViewById(R.id.button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

        view.findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Editable profil_name = ((EditText) fragmentView.findViewById(R.id.editTextTextPersonName)).getText();
                if(null != profil_name){ ((MainActivity)getActivity()).setProfilName(profil_name.toString());}

                Editable profil_first_name = ((EditText) fragmentView.findViewById(R.id.editTextTextPersonName2)).getText();
                if(null != profil_first_name){ ((MainActivity)getActivity()).setProfilFirstName(profil_first_name.toString());}

                Editable profil_birth_date = ((EditText) fragmentView.findViewById(R.id.editTextDate)).getText();
                if(null != profil_birth_date){ ((MainActivity)getActivity()).setProfilBirthDate(profil_birth_date.toString());}

                Editable profil_birth_location = ((EditText) fragmentView.findViewById(R.id.editTextBirthPlace)).getText();
                if(null != profil_birth_location){ ((MainActivity)getActivity()).setProfilBirthLocation(profil_birth_location.toString());}

                Editable profil_address = ((EditText) fragmentView.findViewById(R.id.editTextTextPostalAddress2)).getText();
                if(null != profil_address){ ((MainActivity)getActivity()).setProfilAddress(profil_address.toString());}

                Editable profil_post_code = ((EditText) fragmentView.findViewById(R.id.editTextCP)).getText();
                if(null != profil_post_code){ ((MainActivity)getActivity()).setProfilPostCode(profil_post_code.toString());}

                Editable profil_city = ((EditText) fragmentView.findViewById(R.id.editTextCity)).getText();
                if(null != profil_city){ ((MainActivity)getActivity()).setProfilCity(profil_city.toString());}


                ((MainActivity)getActivity()).saveProfil();


            }
        });

    }
}