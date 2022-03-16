package com.example.angebots_app.ui.productDetails;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.angebots_app.IProductCRUDOperations;
import com.example.angebots_app.MainActivity;
import com.example.angebots_app.Offer;
import com.example.angebots_app.Product;
import com.example.angebots_app.R;
import com.example.angebots_app.RoomProductCRUDOperations;
import com.example.angebots_app.ShoppingItem;
import com.example.angebots_app.databinding.FragmentProductdetailsBinding;
import com.example.angebots_app.databinding.FragmentProductdetailsOfferBinding;
import com.example.angebots_app.tasks.CreateShoppingItemTask;
import com.example.angebots_app.tasks.UpdateProductTask;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

/**
 * Diese Klasse stellt die "Produktdetails" Seiten in der Anwendung dar.
 */
public class ProductDetailsFragment extends Fragment {

    public static final String ARG_ITEM = "product";
    private Product product;
    private List<Offer> offers;
    private ListView lvOffers;
    private ArrayAdapter<Offer> adapter;
    private TextView divider1;
    private IProductCRUDOperations crudOperations;
    private String location;

    // Beim Aufruf werden das Produkt und die Liste der Angebote gesetzt
    public ProductDetailsFragment(Product product, List<Offer> offers){
        this.product = product;
        this.offers = offers;
    }

    // Initialiserung der Seite
    public View onCreateView(@NonNull LayoutInflater inflater,
                         ViewGroup container, Bundle savedInstanceState) {

        // Seite aufbauen und Binding zum Produkt erstellen
        FragmentProductdetailsBinding fragmentProductdetailsBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_productdetails, container, false);
        View root = fragmentProductdetailsBinding.getRoot();
        fragmentProductdetailsBinding.setProduct(this.product);

        // aktuellen Standort aus der MainActivity lesen
        MainActivity mainActivity = (MainActivity) this.getActivity();
        this.location = mainActivity.getLocation();
        // Verbindung zur Datenbank aufbauen
        this.crudOperations = new RoomProductCRUDOperations(this.getActivity());
        // Titel der Seite setzen
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(product.getProductName() + ", " + product.getProductSize());

        // Verbindung zu Elementen aufbauen
        TextView productDescription = root.findViewById(R.id.productDescription);
        productDescription.setText(product.getProductDescription());

        // Text für die Nährwerte zusammenstellen
        TextView nutritions = root.findViewById(R.id.nutritions);
        StringBuilder sb = new StringBuilder();
        sb.append("pro 100 g/ml\n").append("Kalorien").append("\t\t\t\t\t\t\t").append(product.getKcal()).append(" kCal\n").append("Fett").append("\t\t\t\t\t\t\t\t\t\t\t\t").append(product.getFat())
                .append(" g\n").append("Kohlenhydrate").append("\t\t").append(product.getCarbs()).append(" g\n").append("Eiweiß").append("\t\t\t\t\t\t\t\t\t")
                .append(product.getProtein()).append(" g\n").append("Ballaststoffe").append("\t\t\t").append(product.getFibers()).append(" g");
        String s = new String(sb);
        nutritions.setText(s);

        this.lvOffers = root.findViewById(R.id.offerList);

        // Adapter für die Liste der Angebote setzen
        adapter = new ArrayAdapter<Offer>(this.getActivity(), R.layout.fragment_productdetails_offer, this.offers){
            // Binding für die Angebote der Liste setzen
            public View getView(int position, @Nullable View existingView, @NonNull ViewGroup parent) {
                FragmentProductdetailsOfferBinding binding = null;
                View currentView = null;

                if (existingView != null) {
                    currentView = existingView;
                    binding = (FragmentProductdetailsOfferBinding) existingView.getTag();
                } else {
                    binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_productdetails_offer, null, false);
                    currentView = binding.getRoot();
                    currentView.setTag(binding);
                }


                Offer offer = getItem(position);
                binding.setOffer(offer);

                ImageView locIcon = currentView.findViewById(R.id.location);
                ImageView addIcon = currentView.findViewById(R.id.addShoppingList);
                TextView price = currentView.findViewById(R.id.Price);

                // Farbe des Preis ändern, wenn es ein Angebotspreis ist
                price.setTextColor(offer.isOffer() ? getResources().getColor(R.color.colorSecondary, null) : Color.BLACK);

                // Aktion fürs Klicken auf den Einkaufswagen setzen
                addIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new CreateShoppingItemTask(crudOperations,getActivity())
                                .execute(new ShoppingItem(product.getId(), offer.getId(), false))
                                .thenAccept(shoppingItem -> {
                                    showFeedbackMessage("Produkt in den Einkaufswagen gelegt");
                                });
                    }
                });

                // Aktion fürs Klicken auf das Standort-Icon setzen
                locIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + offer.getShopName() + "," + location);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);

                    }
                });

                return currentView;
            }
        };

        this.lvOffers.setAdapter(adapter);
        this.divider1 = root.findViewById(R.id.divider1);
        // Favoriten-Herz in dem ersten Trennfeld setzen
        if(this.product.isFavorite()) {
            this.divider1.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_favorite_black_24dp, 0);
        } else {
            this.divider1.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_favorite_border_black_24dp, 0);
        }

        // Aktion fürs Klicken auf das Favoriten-Herz setzen
        this.divider1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= divider1.getRight() - divider1.getTotalPaddingRight()) {
                        if(product.isFavorite()){
                            product.setFavorite(false);
                            new UpdateProductTask(crudOperations,getActivity())
                                    .execute(product)
                                    .thenAccept(aBoolean -> {
                                        divider1.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_favorite_border_black_24dp, 0);
                                    });
                        } else {
                            product.setFavorite(true);
                            new UpdateProductTask(crudOperations,getActivity())
                                    .execute(product)
                                    .thenAccept(aBoolean -> {
                                        divider1.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_favorite_black_24dp, 0);
                                    });
                        }

                        return true;
                    }
                }
                return true;
            }
        });



        return root;

    }

    // Feedback-Nachricht erstellen
    private void showFeedbackMessage(String msg) {
        Snackbar.make(getActivity().findViewById(R.id.productdetailsViewRoot), msg, BaseTransientBottomBar.LENGTH_SHORT).show();
    }



    // Versuch, die TopBar anzusprechen
    /*@Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        NavController navController = NavHostFragment.findNavController(this);
        Toolbar toolbar = view.findViewById(R.id.toolbar_productDetails);
        NavigationUI.setupWithNavController(
                toolbar, navController);

    }

     */
}
