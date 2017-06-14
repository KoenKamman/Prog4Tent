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

/**
 * Created by Whrabbit on 6/13/2017.
 */

public class FilmAdapter extends ArrayAdapter<Film> {

    public FilmAdapter(Context context, ArrayList<Film> cities){
        super(context, 0, cities);
    }
    @Override
    public View getView(int position, View convertview, ViewGroup parent) {

        Film film= getItem(position);

        if (convertview == null){
            convertview = LayoutInflater.from(getContext()).inflate(R.layout.listview_film_item_row, parent, false);
        }

        TextView filmName = (TextView) convertview.findViewById(R.id.film_item_TextView);
        filmName.setText(film.getTitle());

        return convertview;

    }

}
