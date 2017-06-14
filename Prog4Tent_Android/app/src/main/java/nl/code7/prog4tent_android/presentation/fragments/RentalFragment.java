package nl.code7.prog4tent_android.presentation.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import nl.code7.prog4tent_android.R;
import nl.code7.prog4tent_android.presentation.activities.RentalDetailActivity;

public class RentalFragment extends Fragment {

    private ListView rentalListView;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_rental_fragment);
//    }
    public RentalFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_rental, container, false);


        rentalListView= (ListView) rootView.findViewById(R.id.rental_ListView);
        rentalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getContext(), RentalDetailActivity.class);
                // getrentalslist
                //i.putExtra("RENTALS", emailView.getEditableText().toString());
                startActivity(i);
            }
        });

        return rootView;
    }
}
