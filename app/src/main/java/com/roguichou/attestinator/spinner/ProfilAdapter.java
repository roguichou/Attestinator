package com.roguichou.attestinator.spinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.roguichou.attestinator.Profil;

import java.util.ArrayList;

public class ProfilAdapter extends ArrayAdapter<Profil> {

    private final ArrayList<Profil> data;
    private final LayoutInflater inflater;

    /*************  CustomAdapter Constructor *****************/
    public ProfilAdapter(
            Context context,
            int textViewResourceId,
            ArrayList objects)
    {
        super(context, textViewResourceId, objects);

        data     = objects;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(int position, ViewGroup parent) {

        View row = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);

        Profil tempValue = data.get(position);

        TextView txt = row.findViewById(android.R.id.text1);

        txt.setText(tempValue==null ? "Tous" : tempValue.getLabel());
        return row;
    }
}
