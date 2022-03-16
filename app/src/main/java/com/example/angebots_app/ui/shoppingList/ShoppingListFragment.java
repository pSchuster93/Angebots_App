package com.example.angebots_app.ui.shoppingList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.angebots_app.IProductCRUDOperations;
import com.example.angebots_app.MainActivity;
import com.example.angebots_app.Offer;
import com.example.angebots_app.ProductAndOffer;
import com.example.angebots_app.ProductWithOffers;
import com.example.angebots_app.R;
import com.example.angebots_app.RoomProductCRUDOperations;
import com.example.angebots_app.ShoppingItem;
import com.example.angebots_app.ShoppingListAdapter;
import com.example.angebots_app.databinding.FragmentShoppinglistListelementBinding;
import com.example.angebots_app.tasks.DeleteShoppingItemTask;
import com.example.angebots_app.tasks.GetAllProductsAndOffersTask;
import com.example.angebots_app.tasks.ReadAllShoppingItemsTask;
import com.example.angebots_app.tasks.UpdateShoppingItemTask;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse ist für die Darstellung der "Einkaufsliste"-Seite zuständig
 */
public class ShoppingListFragment extends Fragment {

    private IProductCRUDOperations crudOperations;
    private List<ProductAndOffer> lidlProductsWithOffers = new ArrayList<>();
    private List<ProductAndOffer> reweProductsWithOffers = new ArrayList<>();
    private List<ProductAndOffer> doneProductsWithOffers = new ArrayList<>();
    private List<ProductWithOffers> allProductsWithOffers = new ArrayList<>();
    private List<ShoppingItem> allShoppingItems = new ArrayList<>();
    private TextView lidlDivider, reweDivider, doneDivider;
    private RecyclerView lidlList, reweList, doneList;
    private ShoppingListAdapter adapterLidl, adapterRewe, adapterDone;
    private String location;
    private ChipGroup chipGroup;

    // Initialisierung der Seite
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Aufbauen der Seite
        View root = inflater.inflate(R.layout.fragment_shoppinglist, container, false);

        // Standort holen
        MainActivity mainActivity = (MainActivity) this.getActivity();
        this.location = mainActivity.getLocation();

        // Datenbankzugriff erstellen und Verbindungen mit Elemente herstellen
        this.crudOperations = new RoomProductCRUDOperations(this.getActivity());
        this.doneList = root.findViewById(R.id.doneList);
        this.lidlList = root.findViewById(R.id.lidlList);
        this.reweList = root.findViewById(R.id.reweList);
        chipGroup = root.findViewById(R.id.filterChipGroupShoppingList);

        this.doneDivider = root.findViewById(R.id.divider3ShoppingList);
        this.lidlDivider = root.findViewById(R.id.divider1ShoppingList);
        this.reweDivider = root.findViewById(R.id.divider2ShoppingList);

        // Standort-Icon in Trennelementen setzen
        this.lidlDivider.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_location_on_black_24dp, 0);
        this.reweDivider.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_location_on_black_24dp, 0);

        // Alle Produkte und deren Angebote aus der Datenbank lesen
        new GetAllProductsAndOffersTask(this.crudOperations, this.getActivity())
                .execute()
                .thenAccept(productWithOffers -> {
                   this.allProductsWithOffers.addAll(productWithOffers);
                   new ReadAllShoppingItemsTask(this.crudOperations, this.getActivity())
                           .execute()
                           .thenAccept(shoppingItems -> {
                               this.allShoppingItems.addAll(shoppingItems);
                              for(ShoppingItem s : shoppingItems){
                                  ProductAndOffer productAndOffer = findProductAndOffer(s.getProductId(), s.getOfferId(), s.getId());
                                  addToList(productAndOffer, s.isDone());
                              }
                               this.setupRecyclerView();
                               this.adapterDone.notifyDataSetChanged();
                               this.adapterLidl.notifyDataSetChanged();
                               this.adapterRewe.notifyDataSetChanged();
                               setVisibility(R.id.allChip);
                           });
                });


        // Aktion beim Klicken des Standort-Icons definieren
        this.lidlDivider.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= lidlDivider.getRight() - lidlDivider.getTotalPaddingRight()) {
                        Uri gmmIntentUri = Uri.parse("geo:0,0?q=Lidl," + location);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);

                        return true;
                    }
                }
                return true;
            }
        });

        // Aktion beim Klicken des Standort-Icons definieren
        this.reweDivider.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= reweDivider.getRight() - reweDivider.getTotalPaddingRight()) {
                        Uri gmmIntentUri = Uri.parse("geo:0,0?q=Rewe," + location);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);

                        return true;
                    }
                }
                return true;
            }
        });


        // Aktion zum Filtern setzen
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = root.findViewById(checkedId);
            if(chip != null) {
                chip.setChecked(true);
            }
            this.setVisibility(checkedId);
        });

        return root;
    }

    // Methdoe, die Produkt und Angebot für ein ShoppingItem sucht
    private ProductAndOffer findProductAndOffer(long productId, long offerId, long shoppingItemId){
        ProductAndOffer productAndOffer = new ProductAndOffer(null, null, shoppingItemId);
        for(ProductWithOffers p : this.allProductsWithOffers){
            if(p.product.getId() == productId){
                productAndOffer.setProduct(p.product);
                for(Offer o: p.offers){
                    if(o.getId() == offerId){
                        productAndOffer.setOffer(o);
                    }
                }
            }
        }
        return productAndOffer;
    }

    // Sichtbarkeit der Trennelemente setzen
    private void setVisibility(int checkedId){
        if(lidlProductsWithOffers.size() == 0 || checkedId == R.id.reweChip){
            this.lidlDivider.setVisibility(View.GONE);
            this.lidlList.setVisibility(View.GONE);
        } else {
            this.lidlDivider.setVisibility(View.VISIBLE);
            this.lidlList.setVisibility(View.VISIBLE);
        }

        if(reweProductsWithOffers.size() == 0 || checkedId == R.id.lidlChip){
            this.reweDivider.setVisibility(View.GONE);
            this.reweList.setVisibility(View.GONE);
        } else {
            this.reweDivider.setVisibility(View.VISIBLE);
            this.reweList.setVisibility(View.VISIBLE);
        }

        if(doneProductsWithOffers.size() > 0 && checkedId == R.id.allChip){
            this.doneDivider.setVisibility(View.VISIBLE);
            this.doneList.setVisibility(View.VISIBLE);
        } else {
            this.doneDivider.setVisibility(View.GONE);
            this.doneList.setVisibility(View.GONE);
        }
    }

    // Produkt und Angebot in die richtige Liste setzen
    private void addToList(ProductAndOffer productAndOffer, boolean isDone){
        if(isDone){
            this.doneProductsWithOffers.add(productAndOffer);
        } else{
            switch (productAndOffer.getOffer().getShopName()) {
                case "Rewe":
                    this.reweProductsWithOffers.add(productAndOffer);
                    break;
                case "Lidl":
                    this.lidlProductsWithOffers.add(productAndOffer);
                    break;

            }
        }
    }


    // Listen-View für die Listen erstellen
    private void setupRecyclerView() {

        this.adapterLidl = new ShoppingListAdapter(this.lidlProductsWithOffers, this.crudOperations, this.getActivity(), this.allShoppingItems, this.doneProductsWithOffers, null , this.reweProductsWithOffers, this.lidlList, this.reweList, this.doneList);
        this.adapterRewe = new ShoppingListAdapter(this.reweProductsWithOffers, this.crudOperations, this.getActivity(), this.allShoppingItems, this.doneProductsWithOffers, this.lidlProductsWithOffers, null, this.lidlList, this.reweList, this.doneList);
        this.adapterDone = new ShoppingListAdapter(this.doneProductsWithOffers, this.crudOperations, this.getActivity(), this.allShoppingItems, null, this.lidlProductsWithOffers, this.reweProductsWithOffers, this.lidlList, this.reweList, this.doneList);

        this.lidlList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        this.lidlList.setNestedScrollingEnabled(false);
        this.lidlList.setHasFixedSize(true);
        this.lidlList.setAdapter(this.adapterLidl);

        this.reweList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        this.reweList.setNestedScrollingEnabled(false);
        this.reweList.setHasFixedSize(true);
        this.reweList.setAdapter(this.adapterRewe);

        this.doneList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        this.doneList.setHasFixedSize(true);
        this.doneList.setNestedScrollingEnabled(false);
        this.doneList.setAdapter(this.adapterDone);
    }

    // Versuch, auf die TopBar zuzugreifen
   /* @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        NavController navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
        Toolbar toolbar = view.findViewById(R.id.toolbar_shoppingList);

        NavigationUI.setupWithNavController(
                toolbar, navController, appBarConfiguration);

    }

    */
}
