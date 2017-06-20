package nl.code7.prog4tent_android.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import nl.code7.prog4tent_android.R;
import nl.code7.prog4tent_android.domain.Film;
import nl.code7.prog4tent_android.domain.Rental;

import static nl.code7.prog4tent_android.R.string.film;

/**
 * Created by Whrabbit on 6/15/2017.
 */

public class RentalAdapter extends ArrayAdapter<Rental> {
    private String returnOn;

    public RentalAdapter(Context context, ArrayList<Rental> rentals){
        super(context, 0, rentals);
    }
    @Override
    public View getView(int position, View convertview, ViewGroup parent) {

        Rental rental = getItem(position);
        if (convertview == null){
            convertview = LayoutInflater.from(getContext()).inflate(R.layout.listview_rental_item_row, parent, false);
        }

        String rentalDate = rental.getRental_date();

        int spaceIndex = rentalDate.indexOf("T");
        if (spaceIndex != -1)
        {
            rentalDate = rentalDate.substring(0, spaceIndex);
        }

        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
            Date date = parser.parse(rentalDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE, Integer.parseInt(rental.getRental_duration()));
            Date newDate = c.getTime();
            returnOn = parser.format(newDate);
        } catch (ParseException e){
            e.printStackTrace();
        }

        TextView filmName = (TextView) convertview.findViewById(R.id.rental_item_TextView);
        filmName.setText(rental.getTitle());
        TextView filmRentalDate = (TextView) convertview.findViewById(R.id.rental_date_TV);
        filmRentalDate.setText(rentalDate);
        TextView filmPeriod = (TextView) convertview.findViewById(R.id.rental_period_TV);
        filmPeriod.setText(returnOn);


        return convertview;

    }

}
