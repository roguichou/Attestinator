package com.roguichou.attestinator.iconspinner;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import com.roguichou.attestinator.R;

public class CustomAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<SpinnerModel> data;
    public Resources res;
    SpinnerModel tempValues=null;
    LayoutInflater inflater;

    /*************  CustomAdapter Constructor *****************/
    public CustomAdapter(
            Context _context,
            int textViewResourceId,
            ArrayList objects,
            Resources resLocal
    )
    {
        super(_context, textViewResourceId, objects);

        context = _context;
        data     = objects;
        res      = resLocal;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(R.layout.spinner_rows, parent, false);

        tempValues = null;
        tempValues = data.get(position);

        ImageView icon = row.findViewById(R.id.image);


        icon.setImageResource(res.getIdentifier
                    ("com.roguichou.attestinator:drawable/"
                            + tempValues.getImage(),null,null));

        return row;
    }
}
