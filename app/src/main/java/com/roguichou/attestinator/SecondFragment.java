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
import com.google.android.material.tabs.TabLayout;
import com.roguichou.attestinator.attestation.AttestationPermanente;
import com.roguichou.attestinator.spinner.IconAdapter;
import com.roguichou.attestinator.spinner.IconSpinnerModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


//Profil
public class SecondFragment extends Fragment {

    static final int REQUEST_TAKE_PHOTO = 0x76;

    private  static final int PICK_FILE_GENERIC = 0xE0;

    public final ArrayList<IconSpinnerModel> CustomListViewValuesArr = new ArrayList<>();
    private IconAdapter adapter;
    private Spinner spinner;

    private View fragmentView = null;
    private TabLayout tabProfils = null;

    private EditText labelEdit;
    private EditText nameEdit;
    private EditText firstNameEdit;
    private EditText birthDateEdit;
    private EditText birthPlaceEdit;
    private EditText addressEdit;
    private EditText postCodeEdit;
    private EditText cityEdit;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
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


            IconSpinnerModel model = (IconSpinnerModel)spinner.getSelectedItem();
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
        List<AttestationPermanente> attestations = ((MainActivity)getActivity()).getPermanentAttestations();
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
            button.setTag(attestation);
            button.setOnClickListener(v -> {
                AttestationPermanente att = (AttestationPermanente) v.getTag();
                ((MainActivity) getActivity()).removeAttestation(att);
                rebuildTableAttestationPermanente();
            });

            table.addView(row);

        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity)getActivity()).setActionBarTitle("Profil");
        fragmentView = view;

        labelEdit = view.findViewById(R.id.profil_label);
        nameEdit = view.findViewById(R.id.editTextTextPersonName);
        firstNameEdit = view.findViewById(R.id.editTextTextPersonName2);
        birthDateEdit = view.findViewById(R.id.editTextDate);
        birthPlaceEdit = view.findViewById(R.id.editTextBirthPlace);
        addressEdit = view.findViewById(R.id.editTextTextPostalAddress2);
        postCodeEdit = view.findViewById(R.id.editTextCP);
        cityEdit = view.findViewById(R.id.editTextCity);

        List<Profil> profils = ((MainActivity)getActivity()).getProfils();

        tabProfils = view.findViewById(R.id.tab_profils);

        tabProfils.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Profil profil = (Profil)tab.getTag();
                if(profil != null) {
                    labelEdit.setText(profil.getLabel());
                    nameEdit.setText(profil.getName());
                    firstNameEdit.setText(profil.getFirstName());
                    birthDateEdit.setText(profil.getBirthDate());
                    birthPlaceEdit.setText(profil.getBirthLocation());
                    addressEdit.setText(profil.getAddress());
                    postCodeEdit.setText(profil.getPostCode());
                    cityEdit.setText(profil.getCity());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                labelEdit.setText("");
                nameEdit.setText("");
                firstNameEdit.setText("");
                birthDateEdit.setText("");
                birthPlaceEdit.setText("");
                addressEdit.setText("");
                postCodeEdit.setText("");
                cityEdit.setText("");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        view.findViewById(R.id.buttonSave).setOnClickListener(view100->{
            boolean isNew = false;
            Profil profil = (Profil)tabProfils.getTabAt(tabProfils.getSelectedTabPosition()).getTag();
            //si c'est null, alors on est dans l'onglet de création de profil. Sinon c'est une màj de profil
            if (null == profil)
            {
                isNew = true;
                profil = new Profil();
            }

            if(null!= labelEdit.getText()) {
                profil.setLabel(labelEdit.getText().toString());
            }
            if(null!= nameEdit.getText()) {
                profil.setName(nameEdit.getText().toString());
            }
            if(null!= firstNameEdit.getText()) {
                profil.setFirstName(firstNameEdit.getText().toString());
            }
            if(null!= birthDateEdit.getText()) {
                profil.setBirthDate(birthDateEdit.getText().toString());
            }
            if(null!= birthPlaceEdit.getText()) {
                profil.setBirthLocation(birthPlaceEdit.getText().toString());
            }
            if(null!= addressEdit.getText()) {
                profil.setAddress(addressEdit.getText().toString());
            }
            if(null!= postCodeEdit.getText()) {
                profil.setPostCode(postCodeEdit.getText().toString());
            }
            if(null!= cityEdit.getText()) {
                profil.setCity(cityEdit.getText().toString());
            }

            //on vérifie que tous les champs sont bien remplis
            if(profil.isValid())
            {
                if (isNew)
                {
                    ((MainActivity)getActivity()).addProfil(profil);
                    TabLayout.Tab tab = tabProfils.newTab();
                    tab.setText(profil.getLabel());
                    tab.setTag(profil);
                    //on insère juste à gauche du "+"
                    tabProfils.addTab(tab, tabProfils.getTabCount()-1);
                    tabProfils.selectTab(tab);
                }
                else
                {
                    ((MainActivity)getActivity()).updateProfil(profil);
                }
            }
            else
            {
                Snackbar mySnackbar = Snackbar.make(view, "Veuillez compléter le profil", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
        });

        view.findViewById(R.id.buttonDelete).setOnClickListener(view101-> {
            int idx = tabProfils.getSelectedTabPosition();
            Profil profil = (Profil)tabProfils.getTabAt(idx).getTag();
            if(null != profil)
            {
                tabProfils.removeTabAt(idx);
                ((MainActivity)getActivity()).deleteProfil(profil);
            }
            else
            {
                labelEdit.setText("");
                nameEdit.setText("");
                firstNameEdit.setText("");
                birthDateEdit.setText("");
                birthPlaceEdit.setText("");
                addressEdit.setText("");
                postCodeEdit.setText("");
                cityEdit.setText("");
            }
        });

        for(int i=0; i<profils.size();i++)
        {
            Profil profil = profils.get(i);
            TabLayout.Tab tab = tabProfils.newTab();
            tab.setText(profil.getLabel());
            tab.setTag(profil);
            tabProfils.addTab(tab,i);
        }
        tabProfils.selectTab(tabProfils.getTabAt(0));

        //peupler le spinner d'attestations  permanentes
        CustomListViewValuesArr.add(new IconSpinnerModel("ic_profile", AttestationPermanente.ATTESTATION_TYPE_UNKNOWN));
        CustomListViewValuesArr.add(new IconSpinnerModel("ic_home", AttestationPermanente.ATTESTATION_TYPE_HOME));
        CustomListViewValuesArr.add(new IconSpinnerModel("ic_work", AttestationPermanente.ATTESTATION_TYPE_WORK));
        CustomListViewValuesArr.add(new IconSpinnerModel("ic_school", AttestationPermanente.ATTESTATION_TYPE_ECOLE));

        // Create custom adapter object ( see below CustomAdapter.java )
        adapter = new IconAdapter(getContext(), R.layout.spinner_rows, CustomListViewValuesArr,getResources());
        // Set adapter to spinner
        spinner = view.findViewById(R.id.spinner);
        spinner.setAdapter(adapter);


        view.findViewById(R.id.att_perm_browse).setOnClickListener(view1->{
            IconSpinnerModel model = (IconSpinnerModel)spinner.getSelectedItem();
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

            IconSpinnerModel model = (IconSpinnerModel)spinner.getSelectedItem();
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