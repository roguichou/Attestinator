package com.roguichou.attestinator;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

public class AproposFragment extends Fragment {

    private View fragmentView;
    private boolean showLogs = false;
    private boolean showCredits = false;
    private TextView txtView;

    public AproposFragment() {
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
        return  inflater.inflate(R.layout.fragment_apropos, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity activity = ((MainActivity) getActivity());
        activity.setActionBarTitle("A propos");

        fragmentView = view;
        txtView = view.findViewById(R.id.txt_apropos);

        try {
            PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            String version = pInfo.versionName;
            ((TextView)view.findViewById(R.id.version_txt)).setText("v"+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        txtView.setText("");

        view.findViewById(R.id.file_size_txt).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.clear_log_but).setVisibility(View.INVISIBLE);

        view.findViewById(R.id.clear_log_but).setOnClickListener(view0 -> {
            activity.getLog().clearLog();
            txtView.setText(Html.fromHtml(activity.getLog().toString(),Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE|Html.FROM_HTML_OPTION_USE_CSS_COLORS));
            showLogSize();
        });

        view.findViewById(R.id.logs_but).setOnClickListener(view1 -> {
            txtView.setText("");
            if (showLogs) {
                fragmentView.findViewById(R.id.file_size_txt).setVisibility(View.INVISIBLE);
                fragmentView.findViewById(R.id.clear_log_but).setVisibility(View.INVISIBLE);
            }
            else
            {
                fragmentView.findViewById(R.id.file_size_txt).setVisibility(View.VISIBLE);
                fragmentView.findViewById(R.id.clear_log_but).setVisibility(View.VISIBLE);

                txtView.setText(Html.fromHtml(activity.getLog().toString(),Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE|Html.FROM_HTML_OPTION_USE_CSS_COLORS));
            }
            showLogs = !showLogs;
            showCredits = false;
        });

        view.findViewById(R.id.credits_but).setOnClickListener(view2 -> {
            txtView.setText("");
            if (showLogs) {
                fragmentView.findViewById(R.id.file_size_txt).setVisibility(View.INVISIBLE);
                fragmentView.findViewById(R.id.clear_log_but).setVisibility(View.INVISIBLE);
            }

            if (!showCredits)
            {
                txtView.setText(Html.fromHtml(getString(R.string.credits), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE|Html.FROM_HTML_OPTION_USE_CSS_COLORS));
            }
            showCredits = !showCredits;
            showLogs = false;
        });

        showLogSize();

    }


    private void showLogSize()
    {
        MainActivity activity = ((MainActivity) getActivity());
        Logger log = activity.getLog();
        double sz = log.getLogFileSize();
        String fsz;
        if(sz>1024)
        {
            sz /= 1024;
            if(sz>1024)
            {
                //en Mo
                sz /= 1024;
                fsz=String.format(Locale.FRENCH,"%.2f Mo", sz);
            }
            else
            {
                //en ko
                fsz=String.format(Locale.FRENCH,"%.2f ko", sz);
            }
        }
        else
        {
            //en octet
            fsz=String.format(Locale.FRENCH,"%.0f o", sz);
        }
        ((TextView)fragmentView.findViewById(R.id.file_size_txt)).setText(""+fsz);

    }

}