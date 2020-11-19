package com.roguichou.attestinator.iconspinner;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import com.roguichou.attestinator.R;

public class CustomAdapter extends ArrayAdapter<String> {

    private final ArrayList<SpinnerModel> data;
    private final Resources res;
    private final LayoutInflater inflater;

    /*************  CustomAdapter Constructor *****************/
    public CustomAdapter(
            Context context,
            int textViewResourceId,
            ArrayList objects,
            Resources resLocal
    )
    {
        super(context, textViewResourceId, objects);

        data     = objects;
        res      = resLocal;

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

        View row = inflater.inflate(R.layout.spinner_rows, parent, false);

        SpinnerModel tempValues = data.get(position);

        ImageView icon = row.findViewById(R.id.image);


        icon.setImageResource(res.getIdentifier
                    ("com.roguichou.attestinator:drawable/"
                            + tempValues.getImage(),null,null));

        return row;
    }
}
