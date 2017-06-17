package nl.code7.prog4tent_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nl.code7.prog4tent_android.R;
import nl.code7.prog4tent_android.domain.Inventory;

/**
 * Created by Koen Kamman on 17-6-2017.
 */

public class InventoryAdapter extends ArrayAdapter<Inventory>{

    private static final String TAG = InventoryAdapter.class.getName();

    public InventoryAdapter(Context context, ArrayList<Inventory> films){
        super(context, 0, films);
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {

        Inventory inventory = getItem(position);

        if (convertview == null){
            convertview = LayoutInflater.from(getContext()).inflate(R.layout.listview_inventory_item_row, parent, false);
        }

        TextView inventoryId = (TextView) convertview.findViewById(R.id.inventory_idTV);
        inventoryId.setText("Inventory ID: " + inventory.getInventory_id());

        TextView storeId = (TextView) convertview.findViewById(R.id.store_idTV);
        storeId.setText("Store ID: " + inventory.getStore_id());

        return convertview;

    }
}
