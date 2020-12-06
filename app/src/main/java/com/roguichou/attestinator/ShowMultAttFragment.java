package com.roguichou.attestinator;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ortiz.touchview.TouchImageView;
import com.roguichou.attestinator.attestation.AttestationTemporaire;

import java.io.File;
import java.util.List;
import java.util.Vector;

public class ShowMultAttFragment extends Fragment {

    public ShowMultAttFragment() {
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
        return inflater.inflate(R.layout.fragment_show_mult_att, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).setActionBarTitle("Attestation");

        ViewPager pager  = view.findViewById(R.id.att_view_pager);
        pager.setAdapter(new TouchImageAdapter(
                ((MainActivity) getActivity()).getTemporaireAttestations(),
                getActivity().getFilesDir().getPath()));
    }


    public static class TouchImageAdapter extends PagerAdapter {

        private final List<Bitmap> attBmp = new Vector<>();

        public TouchImageAdapter(List<AttestationTemporaire> attTemp, String filesDir)
        {
            for (AttestationTemporaire att : attTemp) {
                Bitmap bitmap;

                try {
                    File att_fn = new File(filesDir + "/" + att.getFilename());
                    ParcelFileDescriptor fd = ParcelFileDescriptor.open(att_fn, ParcelFileDescriptor.MODE_READ_ONLY);
                    PdfRenderer renderer = new PdfRenderer(fd);
                    PdfRenderer.Page page = renderer.openPage(0);
                    bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    attBmp.add(bitmap);
                    page.close();
                    fd.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }

        @Override
        public int getCount()
        {
            return attBmp.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o==view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TouchImageView image = new TouchImageView(container.getContext());
            image.setImageBitmap(attBmp.get(position));

            container.addView(image, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            return image;
        }

    }
}