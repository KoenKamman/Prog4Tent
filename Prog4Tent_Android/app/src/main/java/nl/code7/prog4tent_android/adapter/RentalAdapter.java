package nl.code7.prog4tent_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nl.code7.prog4tent_android.R;
import nl.code7.prog4tent_android.domain.Film;
import nl.code7.prog4tent_android.domain.Rental;

import static nl.code7.prog4tent_android.R.string.film;

/**
 * Created by Whrabbit on 6/15/2017.
 */

public class RentalAdapter extends ArrayAdapter<Rental> {
    public RentalAdapter(Context context, ArrayList<Rental> rentals){
        super(context, 0, rentals);
    }
    @Override
    public View getView(int position, View convertview, ViewGroup parent) {

        Rental rental= getItem(position);

        if (convertview == null){
            convertview = LayoutInflater.from(getContext()).inflate(R.layout.listview_film_item_row, parent, false);
        }

        TextView filmName = (TextView) convertview.findViewById(R.id.film_item_TextView);
        filmName.setText(rental.getRental_id());

        return convertview;

    }

}