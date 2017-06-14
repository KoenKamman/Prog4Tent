package nl.code7.prog4tent_android.presentation.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import nl.code7.prog4tent_android.R;
import nl.code7.prog4tent_android.adapter.FilmAdapter;
import nl.code7.prog4tent_android.domain.Film;
import nl.code7.prog4tent_android.presentation.activities.FilmDetailActivity;
import nl.code7.prog4tent_android.presentation.activities.RentalDetailActivity;

public class FilmFragment extends Fragment {
    private ListView filmListView;
    private ArrayList<Film> films;
    private FilmAdapter filmAdapter;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_film);
//    }

    public FilmFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_film, container, false);

        films = new ArrayList<Film>();

        filmListView = (ListView) rootView.findViewById(R.id.film_ListView);
        filmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getContext(), FilmDetailActivity.class);
                // getrentalslist
                //i.putExtra("RENTALS", emailView.getEditableText().toString());
                startActivity(i);
            }
        });

        filmAdapter = new FilmAdapter(getContext(), films);
        filmListView.setAdapter(filmAdapter);

        return rootView;
    }
}

