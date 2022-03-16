package com.example.angebots_app.ui.shopSearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.angebots_app.IProductCRUDOperations;
import com.example.angebots_app.MainActivity;
import com.example.angebots_app.Offer;
import com.example.angebots_app.R;
import com.example.angebots_app.RoomProductCRUDOperations;
import com.example.angebots_app.tasks.ReadAllOffersTask;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse ist für die Darstellung der Marktsuche zuständig
 */
public class ShopSearchFragment extends Fragment {

    private ListView lv;
    private SearchView sv;
    private Spinner locationDropDown;
    private List<String> shops = new ArrayList<>();
    private ArrayAdapter<String> adapter, dropdownAdapter;
    private String[] cities={"39108 Magdeburg","10115 Berlin","20095 Hamburg","21337 Lüneburg","29345 Uelzen","29439 Lüchow"};
    private MainActivity mainActivity;
    private String location;
    private String searchShop;
    private IProductCRUDOperations crudOperations;
    private MaterialButton button;

    // Initialisierung der Seite
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Seite aufbauen
        View root = inflater.inflate(R.layout.fragment_shopsearch, container, false);

        // Datenbankzugriff erstellen und Elemente verbinden
        this.crudOperations = new RoomProductCRUDOperations(this.getActivity());
        this.lv = root.findViewById(R.id.locationListView);
        this.sv = root.findViewById(R.id.locationSearchView);
        this.locationDropDown = root.findViewById(R.id.shopNameLocationDropDown);
        this.button = root.findViewById(R.id.shopSearchSubmitButton);
        this.mainActivity = (MainActivity) this.getActivity();
        this.location = this.mainActivity.getLocation();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Marktsuche");

        // Adapter für die Standortauswahl erstellen und setzen
        this.adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1,cities);
        this.lv.setAdapter(this.adapter);

        // Aktion beim Klicken auf Suchvorschläge aus der Standortauswal setzen
        this.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String loc = adapter.getItem(position);
                mainActivity.setLocation(loc);
                location = loc;
                sv.setQuery(loc, false);
            }
        });

        // Sichtbarkeit der Suchvorschläge setzen
        this.sv.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, final boolean hasFocus)
            {
                if (!hasFocus)
                {
                    // Collapse the searchbox on ActionBar
                    lv.setVisibility(View.GONE);
                } else if (hasFocus) {
                    lv.setVisibility(View.VISIBLE);
                }
            }
        });

        // Aktion zum Filtern der Standortauswahl-Suchvorschläge setzen
        this.sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String text) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {

                adapter.getFilter().filter(text);

                return false;
            }
        });

        this.sv.setQuery(this.location, false);

        // Alle Angebote lesen
        new ReadAllOffersTask(this.crudOperations, this.getActivity())
                .execute()
                .thenAccept(offers -> {
                    List<String> shopNames = this.getAllShopNames(offers);
                    dropdownAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.support_simple_spinner_dropdown_item, shopNames);
                    dropdownAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    this.locationDropDown.setAdapter(dropdownAdapter);
                    this.dropdownAdapter.notifyDataSetChanged();
                });
        // Aktion für das Auswählen eines Marktes aus dem DropDown setzen
        this.locationDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchShop = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Aktion für das Klicken des Such-Buttons setzen
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + searchShop + "," + location);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        return root;
    }

    // Aus den Angeboten alle Märkte filtern
    private List<String> getAllShopNames(List<Offer> offers){
        List<String> shopNames = new ArrayList<>();

        for(Offer o: offers){
            if(shopNames.indexOf(o.getShopName()) == -1){
                shopNames.add(o.getShopName());
            }
        }

        return shopNames;
    }
}
