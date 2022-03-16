package com.example.angebots_app.ui.favorite;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.angebots_app.IProductCRUDOperations;
import com.example.angebots_app.Offer;
import com.example.angebots_app.Product;
import com.example.angebots_app.ProductWithOffers;
import com.example.angebots_app.R;
import com.example.angebots_app.RoomProductCRUDOperations;
import com.example.angebots_app.databinding.FragmentFavoriteListelementBinding;
import com.example.angebots_app.databinding.FragmentSearchProductBinding;
import com.example.angebots_app.tasks.GetAllProductsAndOffersTask;
import com.example.angebots_app.tasks.UpdateProductTask;
import com.example.angebots_app.ui.productDetails.ProductDetailsFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Diese Klasse stellt die Seite "Favoriten" in der Anwendung dar.
 */
public class FavoriteFragment extends Fragment {

    private List<ProductWithOffers> productWithOffersList = new ArrayList<>();
    private List<ProductWithOffers> filterdProductWithOffersList = new ArrayList<>();
    private ListView lvFavorite;
    private ArrayAdapter<ProductWithOffers> adapter;
    private ArrayAdapter<String> dropdownAdapter;
    private IProductCRUDOperations crudOperations;
    private String[] categories = {"Alle", "Getränke" , "Tee", "Süssigkeiten"};

    // Wenn die Seite initialisiert wird
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Seite aufbauen und Verbindungen zu den Elementen aus dem Layout setzten
        View root = inflater.inflate(R.layout.fragment_favorite, container, false);
        this.lvFavorite = root.findViewById(R.id.favoriteList);
        // Verbindung zur Datenbank aufbauen
        this.crudOperations = new RoomProductCRUDOperations(this.getActivity());

        // Adapter für die Liste erstellen
        this.adapter=new ArrayAdapter<ProductWithOffers>(this.getActivity(), R.layout.fragment_favorite_listelement, filterdProductWithOffersList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View existingView, @NonNull ViewGroup parent) {
                // Binding für das Element der Liste setzen
                FragmentFavoriteListelementBinding binding = null;
                View currentView = null;

                if (existingView != null) {
                    currentView = existingView;
                    binding = (FragmentFavoriteListelementBinding) existingView.getTag();
                } else {
                    binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_favorite_listelement, null, false);
                    currentView = binding.getRoot();
                    currentView.setTag(binding);
                }


                ProductWithOffers productWithOffers = getItem(position);
                binding.setProductWithOffers(productWithOffers);

                // Prüfen, wie das Angebote-Textfeld dargestellt werden soll
                TextView countOffers = currentView.findViewById(R.id.countOffers);
                if(productWithOffers.getOfferCount() == 0){
                    countOffers.setBackground(getResources().getDrawable(R.drawable.rounded_corner_gray, null));
                }

                // Setzen der Aktion zum Löschen eines Produkts von der Favoritenliste
                ImageView favIcon = currentView.findViewById(R.id.favoriteIconList);
                favIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filterdProductWithOffersList.remove(productWithOffers);
                        productWithOffers.product.setFavorite(false);
                        new UpdateProductTask(crudOperations, getActivity())
                                .execute(productWithOffers.product)
                                .thenAccept(aBoolean -> {
                                    showFeedbackMessage(productWithOffers.product.getProductName() + " wurde aus den Favoriten entfernt");
                                    adapter.notifyDataSetChanged();
                                });

                    }
                });
                return currentView;
            }
        };
        this.lvFavorite.setAdapter(adapter);

        // Alle Produkte und deren dazugehörigen Angebote laden
        new GetAllProductsAndOffersTask(this.crudOperations, this.getActivity())
                .execute()
                .thenAccept(prodOffers -> {
                    this.productWithOffersList.addAll(prodOffers);
                    for(ProductWithOffers po : prodOffers){
                        if(po.product.isFavorite()){
                            this.filterdProductWithOffersList.add(po);
                        }
                    }
                    this.sortList(R.id.lastAddedChip);
                    this.adapter.notifyDataSetChanged();
                });

        // Aktion für das Klicken auf das Listenelement setzen
        lvFavorite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProductWithOffers productWithOffers = adapter.getItem(position);
                onProductSelected(productWithOffers);
            }
        });

        // Aktion für das Sortieren über die Chips setzen
        ChipGroup chipGroup = root.findViewById(R.id.filterChipGroup);
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = root.findViewById(checkedId);
            if(chip != null) {
                chip.setChecked(true);
            }
            this.sortList(checkedId);
        });

        // Dropdown für das Filtern nach Kategorien erstellen
        Spinner spinner = root.findViewById(R.id.favoriteCategoryDropDown);
        dropdownAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.spinner_item, categories);
        dropdownAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(dropdownAdapter);

        // Filterung nach Katergorien definieren
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = (String) parent.getItemAtPosition(position);

                switch (s) {
                    case "Getränke" :
                        filterdProductWithOffersList.clear();
                        for(ProductWithOffers po : productWithOffersList){
                            if(po.product.isFavorite() && po.product.getCategory().equals("Getränke")){
                                filterdProductWithOffersList.add(po);
                            }
                        }
                        sortList(chipGroup.getCheckedChipId());
                        adapter.notifyDataSetChanged();
                        break;
                    case "Tee" :
                        filterdProductWithOffersList.clear();
                        for(ProductWithOffers po : productWithOffersList){
                            if(po.product.isFavorite() && po.product.getCategory().equals("Tee")){
                                filterdProductWithOffersList.add(po);
                            }
                        }
                        sortList(chipGroup.getCheckedChipId());
                        adapter.notifyDataSetChanged();
                        break;
                    case "Süssigkeiten" :
                        filterdProductWithOffersList.clear();
                        for(ProductWithOffers po : productWithOffersList){
                            if(po.product.isFavorite() && po.product.getCategory().equals("Süssigkeiten")){
                                filterdProductWithOffersList.add(po);
                            }
                        }
                        sortList(chipGroup.getCheckedChipId());
                        adapter.notifyDataSetChanged();
                        break;
                    default:
                        filterdProductWithOffersList.clear();
                        for(ProductWithOffers po : productWithOffersList){
                            if(po.product.isFavorite()){
                                filterdProductWithOffersList.add(po);
                            }
                        }
                        sortList(chipGroup.getCheckedChipId());
                        adapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return root;
    }

    // Methode zum Sortieren der Liste
    private void sortList(int checkedId){
        switch (checkedId) {
            case R.id.filterNameChip :
                this.filterdProductWithOffersList.sort(Comparator.comparing(ProductWithOffers::getProductName));
                break;
            case R.id.filterOffersChip :
                this.filterdProductWithOffersList.sort(Comparator.comparing(ProductWithOffers::getOfferCount).reversed());
                break;
            default:
                this.filterdProductWithOffersList.sort(Comparator.comparing(ProductWithOffers::getProductId));
                break;
        }
        this.adapter.notifyDataSetChanged();
    }

    // Methode zum Aufrufen einer Produktdetailseite
    private void onProductSelected(ProductWithOffers productWithOffers){
        List<Offer> offers = new ArrayList<>();
        offers.addAll(productWithOffers.offers);
        Fragment fragment = new ProductDetailsFragment(productWithOffers.product, offers);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment, "favorite");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Erstellung einer Snackbar für eine Feedback-Nachricht
    private void showFeedbackMessage(String msg) {
        Snackbar.make(getActivity().findViewById(R.id.favoriteViewRoot), msg, BaseTransientBottomBar.LENGTH_SHORT).show();
    }

    // Versuch, die TopBar anzusprechen
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        /*
        NavController navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
        Toolbar toolbar = view.findViewById(R.id.toolbar_favorite);

        NavigationUI.setupWithNavController(
                toolbar, navController, appBarConfiguration);

         */

    }
}
