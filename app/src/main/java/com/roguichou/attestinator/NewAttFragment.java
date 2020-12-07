package com.roguichou.attestinator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.roguichou.attestinator.attestation.AttestationTemporaire;
import com.roguichou.attestinator.attestation.Raison;
import com.roguichou.attestinator.spinner.ProfilAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NewAttFragment extends Fragment {

    private Raison raison = null;
    private View fragmentView = null;
    private TimePicker picker = null;
    private Profil profil = null;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_att, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity)getActivity()).setActionBarTitle("Générer attestation");

        picker=view.findViewById(R.id.timePicker1);
        picker.setIs24HourView(true);

        fragmentView = view;

        List<Profil> profils= ((MainActivity)getActivity()).getProfils();
        ArrayList<Profil> CustomListViewValuesArr = new ArrayList<>();
        CustomListViewValuesArr.add(null);
        CustomListViewValuesArr.addAll(profils);
        ProfilAdapter adapter = new ProfilAdapter(getContext(), android.R.layout.simple_spinner_item, CustomListViewValuesArr);


        AutoCompleteTextView spinner = view.findViewById(R.id.profilDropDown);
        spinner.setAdapter(adapter);

        spinner.setOnItemClickListener((parent, arg1, pos, id) -> profil = (Profil) parent.getItemAtPosition(pos));
        spinner.setText("Tous", false);



        view.findViewById(R.id.button_generer).setOnClickListener(view1 -> {
            //1. vérifier qu'on a bien une raison
            int chip_id = ((ChipGroup)fragmentView.findViewById(R.id.raison_group)).getCheckedChipId();

            if (ChipGroup.NO_ID == chip_id )
            {
                String msg = "Pas de raison sélectionnée";
                Snackbar mySnackbar = Snackbar.make(fragmentView, msg, Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
            else
            {
                String msg = "raison sélectionnée : "+chip_id;
                Snackbar mySnackbar = Snackbar.make(fragmentView, msg, Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }


        });


        ((Chip)view.findViewById(R.id.raison_courses)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                raison = Raison.ACHATS;
            }
        });
        ((Chip)view.findViewById(R.id.raison_sante)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                raison = Raison.SANTE;
            }
        });
        ((Chip)view.findViewById(R.id.raison_prommenade)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                raison = Raison.SPORT_ANIMAUX;
            }
        });
        ((Chip)view.findViewById(R.id.raison_enfants)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                raison = Raison.ENFANTS;
            }
        });


        view.findViewById(R.id.button_generer).setOnClickListener(view12 -> {

            int chip_id = ((ChipGroup)fragmentView.findViewById(R.id.raison_group)).getCheckedChipId();

            //vérifier qu'on a bien une raison
            if (ChipGroup.NO_ID == chip_id  || null == raison)
            {
                String msg = "Pas de raison sélectionnée";
                Snackbar mySnackbar = Snackbar.make(fragmentView, msg, Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
            else
            {
                if(null == profil)
                {
                    Iterator<Profil> profilIt = profils.iterator();
                    while (profilIt.hasNext()) {
                        profil = profilIt.next();
                        AttestationTemporaire att = new AttestationTemporaire(getContext(), fragmentView,
                                ((MainActivity) getActivity()).getScreenWidth(),
                                profil, raison, picker.getHour(), picker.getMinute());

                        ((MainActivity) getActivity()).addTempAtt(att);
                    }
                    profil = null;
                }
                else {
                    AttestationTemporaire att = new AttestationTemporaire(getContext(), fragmentView,
                            ((MainActivity) getActivity()).getScreenWidth(),
                            profil, raison, picker.getHour(), picker.getMinute());

                    ((MainActivity) getActivity()).addTempAtt(att);
                }

                if (raison == Raison.SPORT_ANIMAUX) {
                    ((MainActivity)getActivity()).debuterSortie (fragmentView, profil, picker.getHour(), picker.getMinute());
                }


                NavHostFragment.findNavController(NewAttFragment.this)
                        .navigate(R.id.action_newAttFragment_to_MenuFragment);

            }


        });


    }
}
