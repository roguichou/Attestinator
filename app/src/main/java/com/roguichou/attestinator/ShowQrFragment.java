package com.roguichou.attestinator;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ortiz.touchview.TouchImageView;
import com.roguichou.attestinator.attestation.AttestationTemporaire;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ShowQrFragment extends Fragment {

    public ShowQrFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_qr, container, false);
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).setActionBarTitle("Attestation");

        ViewPager pager  = view.findViewById(R.id.qr_view_pager);
        pager.setAdapter(new QRAdapter(
                ((MainActivity) getActivity()).getTemporaireAttestations()));
    }



    public static class QRAdapter extends PagerAdapter {

        private final List<AttestationTemporaire> attTemp;

        public QRAdapter(List<AttestationTemporaire> attTemp)
        {
            this.attTemp = attTemp;
        }

        @Override
        public int getCount() {
            return attTemp.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return object==view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = (LayoutInflater) container.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            AttestationTemporaire att = attTemp.get(position);

            View layout = inflater.inflate(R.layout.show_qr, null);

            if (null!= att) {
                ((ImageView) layout.findViewById(R.id.qr_img)).setImageBitmap(att.getQrBitmap());

                ((TextView) layout.findViewById(R.id.show_qr_profil)).setText(att.getProfil().getLabel());

                Calendar heureSortie = att.getHeureSortie();
                String time = String.format(Locale.FRENCH, "%02d/%02d/%04d %02d:%02d",
                        heureSortie.get(Calendar.DAY_OF_MONTH),
                        heureSortie.get(Calendar.MONTH),
                        heureSortie.get(Calendar.YEAR),
                        heureSortie.get(Calendar.HOUR_OF_DAY),
                        heureSortie.get(Calendar.MINUTE));

                ((TextView) layout.findViewById(R.id.show_qr_time)).setText(time);

                ((TextView) layout.findViewById(R.id.show_qr_raison)).setText(att.getRaison().toString());
            }
            container.addView(layout);

            return layout;
        }
    }


}