package com.roguichou.attestinator;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.roguichou.attestinator.attestation.AttestationPermanente;
import com.roguichou.attestinator.iconspinner.CustomAdapter;
import com.roguichou.attestinator.iconspinner.SpinnerModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Vector;


//Profil
public class SecondFragment extends Fragment {

    static final int REQUEST_TAKE_PHOTO = 0x76;

    private  static final int PICK_FILE_GENERIC = 0xE0;

    public final ArrayList<SpinnerModel> CustomListViewValuesArr = new ArrayList<>();
    CustomAdapter adapter;
    Spinner spinner;

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
    public void onPause() {

        Profil profil = ((MainActivity)getActivity()).getProfil();

        Editable profil_name = ((EditText) fragmentView.findViewById(R.id.editTextTextPersonName)).getText();
        if (null != profil_name) {
            profil.setProfilName(profil_name.toString());
        }

        Editable profil_first_name = ((EditText) fragmentView.findViewById(R.id.editTextTextPersonName2)).getText();
        if (null != profil_first_name) {
            profil.setProfilFirstName(profil_first_name.toString());
        }

        Editable profil_birth_date = ((EditText) fragmentView.findViewById(R.id.editTextDate)).getText();
        if (null != profil_birth_date) {
            profil.setProfilBirthDate(profil_birth_date.toString());
        }

        Editable profil_birth_location = ((EditText) fragmentView.findViewById(R.id.editTextBirthPlace)).getText();
        if (null != profil_birth_location) {
            profil.setProfilBirthLocation(profil_birth_location.toString());
        }

        Editable profil_address = ((EditText) fragmentView.findViewById(R.id.editTextTextPostalAddress2)).getText();
        if (null != profil_address) {
            profil.setProfilAddress(profil_address.toString());
        }

        Editable profil_post_code = ((EditText) fragmentView.findViewById(R.id.editTextCP)).getText();
        if (null != profil_post_code) {
            profil.setProfilPostCode(profil_post_code.toString());
        }

        Editable profil_city = ((EditText) fragmentView.findViewById(R.id.editTextCity)).getText();
        if (null != profil_city) {
            profil.setProfilCity(profil_city.toString());
        }

        profil.saveProfil();

        super.onPause();
    }


    private void saveAttestationFile(Uri uri, String dest_fn)
    {


        File f = new File(getActivity().getFilesDir()+"/"+dest_fn);

        try {
            if (!f.exists())
            {
                f.createNewFile();
            }

            ContentResolver content = getContext().getContentResolver();
            InputStream source = content.openInputStream(uri);

            OutputStream destination = new FileOutputStream(f);
            if (source != null) {
                byte[] buffer = new byte[1000];
                int bytesRead;
                while ( ( bytesRead = source.read( buffer, 0, buffer.length ) ) >= 0 )
                {
                    destination.write( buffer, 0, bytesRead );
                }
            }
            if (source != null) {
                source.close();
            }
            destination.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public  void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        int fileType = AttestationPermanente.FILE_TYPE_JPG;
        Uri uri = null;

        if (resultCode == Activity.RESULT_OK)
        {
            if (PICK_FILE_GENERIC == requestCode && resultData != null)
            {
                 uri = Uri.parse(resultData.getDataString());
                String mime = getContext().getContentResolver().getType(uri);

                if (mime.contains("pdf"))
                {
                    fileType = AttestationPermanente.FILE_TYPE_PDF;
                }

            }
            else if(REQUEST_TAKE_PHOTO != requestCode)
            {
                return;
            }


            SpinnerModel model = (SpinnerModel)spinner.getSelectedItem();
            AttestationPermanente attestation = new AttestationPermanente(model.getType(),
                    fileType,
                    ((TextView)fragmentView.findViewById(R.id.label_att_perm)).getText().toString());
            if(null != uri) {
                saveAttestationFile(uri, attestation.getFilename());
            }
            ((MainActivity)getActivity()).addAttestation(attestation);
            rebuildTableAttestationPermanente();
        }
    }


    private void rebuildTableAttestationPermanente() {

        spinner.setSelection(0);
       ((TextView)fragmentView.findViewById(R.id.label_att_perm)).setText("");

        TableLayout table = (fragmentView.findViewById(R.id.tableAttesations));

        //1. clear all rows
        table.removeAllViews();

        //2. create rows
        Resources res = getResources();
        Vector<AttestationPermanente> attestations = ((MainActivity)getActivity()).getPermanentAttestations();
        for(int i=0;i<attestations.size();i++)
        {
            AttestationPermanente attestation = attestations.get(i);
            TableRow row = (TableRow)LayoutInflater.from(getContext()).inflate(R.layout.profil_att_row, null);

            //2.1 image
            int type = attestation.getAttestationType();
            String img = "ic_profile";
            switch(type)
            {
                case AttestationPermanente.ATTESTATION_TYPE_HOME :
                    img = "ic_noun_attestation_824051";
                    break;
                case AttestationPermanente.ATTESTATION_TYPE_WORK :
                    img = "ic_att_travail";
                    break;
                case AttestationPermanente.ATTESTATION_TYPE_ECOLE :
                    img = "ic_att_ecole";
                    break;
            }
            ImageView image = (row.findViewById(R.id.type_att_img));
            image.setImageResource(res.getIdentifier("com.roguichou.attestinator:drawable/"+ img,null,null));


            //2.2 Label
            TextView label = row.findViewById(R.id.label_att);
            label.setText(attestation.getLabel());

            //2.3 Delete Button
            ImageButton button = (row.findViewById(R.id.button_del_att));
            button.setTag(i);
            button.setOnClickListener(v -> {
                Integer idx = (Integer)v.getTag();
                ((MainActivity) getActivity()).removeAttestation(idx);
                rebuildTableAttestationPermanente();
            });

            table.addView(row);

        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity)getActivity()).setActionBarTitle("Profil");
        fragmentView = view;
        Profil profil = ((MainActivity)getActivity()).getProfil();

        String profil_name = profil.getProfilName();
        String profil_first_name = profil.getProfilFirstName();
        String profil_birth_date = profil.getProfilBirthDate();
        String profil_birth_location = profil.getProfilBirthLocation();
        String profil_address = profil.getProfilAddress();
        String profil_post_code = profil.getProfilPostCode();
        String profil_city = profil.getProfilCity();

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


        //peupler le spinner d'attestations  permanentes
        CustomListViewValuesArr.add(new SpinnerModel("ic_profile", AttestationPermanente.ATTESTATION_TYPE_UNKNOWN));
        CustomListViewValuesArr.add(new SpinnerModel("ic_home", AttestationPermanente.ATTESTATION_TYPE_HOME));
        CustomListViewValuesArr.add(new SpinnerModel("ic_work", AttestationPermanente.ATTESTATION_TYPE_WORK));
        CustomListViewValuesArr.add(new SpinnerModel("ic_school", AttestationPermanente.ATTESTATION_TYPE_ECOLE));

        // Create custom adapter object ( see below CustomAdapter.java )
        adapter = new CustomAdapter(getContext(), R.layout.spinner_rows, CustomListViewValuesArr,getResources());
        // Set adapter to spinner
        spinner = view.findViewById(R.id.spinner);
        spinner.setAdapter(adapter);


        view.findViewById(R.id.att_perm_browse).setOnClickListener(view1->{
            SpinnerModel model = (SpinnerModel)spinner.getSelectedItem();
            if (model.getType()==AttestationPermanente.ATTESTATION_TYPE_UNKNOWN ||
                    ((TextView)view.findViewById(R.id.label_att_perm)).length()<2  )
            {
                Snackbar mySnackbar = Snackbar.make(view, "Choisir type et label.", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
            else
            {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                String[] mimeTypes = {"image/*","application/pdf"};
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

                startActivityForResult(intent, PICK_FILE_GENERIC);
            }
        });


        view.findViewById(R.id.att_perm_photo).setOnClickListener(view2-> {

            SpinnerModel model = (SpinnerModel)spinner.getSelectedItem();
            if (model.getType()==AttestationPermanente.ATTESTATION_TYPE_UNKNOWN ||
                    ((TextView)view.findViewById(R.id.label_att_perm)).length()<2  )
            {
                Snackbar mySnackbar = Snackbar.make(view, "Choisir type et label.", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
            else {
                File photoFile = null;
                try {
                    photoFile = new File(getActivity().getFilesDir()+
                            "/att_" + ((TextView) view.findViewById(R.id.label_att_perm)).getText()+
                            ".jpg");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getContext(),
                                "com.roguichou.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }
                else
                {
                    Snackbar mySnackbar = Snackbar.make(view, "Erreur: pas d'autorisation d'utilisation de l'appareil photo", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
            }
        });

        rebuildTableAttestationPermanente();

    }
}