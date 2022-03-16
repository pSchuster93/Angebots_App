package com.example.angebots_app.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.angebots_app.IProductCRUDOperations;
import com.example.angebots_app.MainActivity;
import com.example.angebots_app.Offer;
import com.example.angebots_app.Product;
import com.example.angebots_app.ProductWithOffers;
import com.example.angebots_app.R;
import com.example.angebots_app.RoomProductCRUDOperations;
import com.example.angebots_app.databinding.FragmentSearchProductBinding;
import com.example.angebots_app.tasks.CreateOfferTask;
import com.example.angebots_app.tasks.CreateProductTask;
import com.example.angebots_app.tasks.DeleteAllOffersTask;
import com.example.angebots_app.tasks.DeleteAllProductsTask;
import com.example.angebots_app.tasks.DeleteAllShoppingItemsTask;
import com.example.angebots_app.tasks.DeleteShoppingItemTask;
import com.example.angebots_app.tasks.GetAllProductsAndOffersTask;
import com.example.angebots_app.tasks.ReadAllProductsTask;
import com.example.angebots_app.ui.productDetails.ProductDetailsFragment;

import java.util.ArrayList;
import java.util.List;

/**
* Diese Klasse kümmert sich um die Darstellung der "Suche" Seite
*/
public class SearchFragment extends Fragment {

    ListView lv, lv2;
    SearchView sv,sv2;
    IProductCRUDOperations crudOperations;
    List<Product> prods = new ArrayList<>();
    List<Product> filteredProducts = new ArrayList<>();
    List<ProductWithOffers> productWithOffers = new ArrayList<>();
    String location;
    boolean clearDB, productsCreated;
    MainActivity a;
    String[] cities={"39108 Magdeburg","10115 Berlin","20095 Hamburg","21337 Lüneburg","29345 Uelzen","29439 Lüchow"};
    ArrayAdapter<Product> adapter;
    ArrayAdapter<String> adapter2;

    // Initialisierung der Seite
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Aufbauen der Seite
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        // Datenbankzugriff erstellen und Elemente verbinden
        this.crudOperations = new RoomProductCRUDOperations(this.getActivity());
        lv=(ListView) root.findViewById(R.id.listView1);
        sv=(SearchView) root.findViewById(R.id.searchView1);
        lv2=(ListView) root.findViewById(R.id.listView2);
        sv2=(SearchView) root.findViewById(R.id.searchView2);

        this.a = (MainActivity) getActivity();

        // Standort setzen, sowie die Informationen zum Löschen der DB und des Erzeugen von neuen Produkten übernehmen.
        this.location = a.getLocation();
        this.clearDB = a.isClearDB();
        this.productsCreated = a.isProductsCreated();

        this.readAllProducts(this.clearDB);

        // Adapter für die Liste am Suchfeld erstellen
        adapter=new ArrayAdapter<Product>(this.getActivity(), R.layout.fragment_search_product, filteredProducts) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View existingView, @NonNull ViewGroup parent) {
                // Binding setzen
                FragmentSearchProductBinding binding = null;
                View currentView = null;

                if (existingView != null) {
                    currentView = existingView;
                    binding = (FragmentSearchProductBinding) existingView.getTag();
                } else {
                    binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_search_product, null, false);
                    currentView = binding.getRoot();
                    currentView.setTag(binding);
                }


                Product product = getItem(position);
                binding.setProduct(product);
                return currentView;
            }

            // Filter für die Liste erstellen
            @NonNull
            @Override
            public Filter getFilter() {
                Filter filter = new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {

                        FilterResults filterResults = new FilterResults();
                        if(constraint == null || constraint.length() == 0){
                            filterResults.count = prods.size();
                            filterResults.values = prods;

                        }else{
                            List<Product> resultsModel = new ArrayList<>();
                            String searchStr = constraint.toString().toLowerCase();

                            for(Product product:prods){
                                if(product.getProductName().toLowerCase().contains(searchStr) ){
                                    resultsModel.add(product);

                                }
                                filterResults.count = resultsModel.size();
                                filterResults.values = resultsModel;
                            }
                        }

                        return filterResults;
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        filteredProducts.clear();
                        filteredProducts.addAll((List<Product>) results.values);
                        notifyDataSetChanged();
                    }
                };
                return filter;
            }
        };

        // Adapter für die Liste am Standort-Suchfeld erstellen
        adapter2 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1,cities);
        lv.setAdapter(adapter);
        lv2.setAdapter(adapter2);

        // Aktion beim Klicken auf ein Item aus der Liste des Suchfelds definieren
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = adapter.getItem(position);
                onProductSelected(product);
            }
        });

        // Aktion beim Klicken auf ein Item aus der Liste des Standort-Suchfelds definieren
        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String loc = adapter2.getItem(position);
                a.setLocation(loc);
                location = loc;
                sv2.setQuery(loc, false);
            }
        });

        // Sichtbarkeit der Suchvorschläge prüfen
        sv.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
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

        // Sichtbarkeit der Suchvorschläge prüfen
        sv2.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, final boolean hasFocus)
            {
                if (!hasFocus)
                {
                    // Collapse the searchbox on ActionBar
                    lv2.setVisibility(View.GONE);
                } else if (hasFocus) {
                    lv2.setVisibility(View.VISIBLE);
                }
            }
        });

        // Aktion für Eingabe in Suchfeld definieren
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

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

        // Aktion für Eingabe in Standort-Suchfeld definieren
        sv2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String text) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {

                adapter2.getFilter().filter(text);

                return false;
            }
        });

        sv2.setQuery(this.location, false);

        this.adapter.notifyDataSetChanged();

        return root;
    }

    // Versuch, die TopBar anzusprechen
    /*
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        NavController navController = NavHostFragment.findNavController(this);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_search, R.id.navigation_favorite, R.id.navigation_shoppingList, R.id.navigation_more)
                .build();
        Toolbar toolbar = view.findViewById(R.id.toolbar_search);

        NavigationUI.setupWithNavController(
                toolbar, navController, appBarConfiguration);
        toolbar.inflateMenu(R.menu.bottom_nav_menu);
    }
*/
    // Methode zum Lesen aller Produkte
    private void readAllProducts(boolean clearDB) {
        // Neuerstellung der Datenbank
        if(clearDB){
            this.a.setClearDB(false);
            this.clearDB = false;
            new DeleteAllProductsTask(this.crudOperations, this.getActivity())
                    .execute()
                    .thenAccept(aBoolean -> {
                        new DeleteAllOffersTask(this.crudOperations,this.getActivity())
                                .execute()
                                .thenAccept(aBoolean1 -> {
                                    new DeleteAllShoppingItemsTask(this.crudOperations, this.getActivity())
                                            .execute()
                                            .thenAccept(aBoolean2 -> {
                                                List<Product> createdProducts = createInitialProducts();
                                                this.prods.addAll(createdProducts);
                                                this.filteredProducts.addAll(createdProducts);
                                                this.adapter.notifyDataSetChanged();
                                            });
                                });
                    });
        } else {
            // Lesen aller Produkte aus der Datenbank
            new ReadAllProductsTask(this.crudOperations, this.getActivity())
                    .execute()
                    .thenAccept(products -> {
                        if (products.size() == 0) {
                            List<Product> createdProducts = createInitialProducts();
                            this.prods.addAll(createdProducts);
                            this.filteredProducts.addAll(createdProducts);
                            new GetAllProductsAndOffersTask(this.crudOperations, this.getActivity())
                                    .execute()
                                    .thenAccept(prodOffers -> {
                                        this.productWithOffers.addAll(prodOffers);
                                        this.adapter.notifyDataSetChanged();
                                    });
                        } else {
                            if(!this.productsCreated) {
                                a.setProductsCreated(true);
                                this.productsCreated = true;
                                products.addAll(createProducts());
                            }
                            prods.addAll(products);
                            filteredProducts.addAll(products);
                            new GetAllProductsAndOffersTask(this.crudOperations, this.getActivity())
                                    .execute()
                                    .thenAccept(prodOffers -> {
                                        this.productWithOffers.addAll(prodOffers);
                                    });
                        }
                    });
        }
    }

    // Methode zum Erstellen aller initialen Produkte
    private List<Product> createInitialProducts(){
        List<Product> newProducts = new ArrayList<>();

        newProducts.add(new Product("Coca Cola", "0,33 L", false, "Text", 100, 20, 5, 6, 6, "Getränke"));
        newProducts.add(new Product("Milka Erdbeer", "100 g", true, "Text", 400, 50, 87, 6, 6, "Süssigkeiten"));
        newProducts.add(new Product("Teekanne Organics Happy Time", "110 g", false, "Text", 10, 1, 0, 0, 0.1, "Tee"));
        newProducts.add(new Product("Teekanne Fresh Pfirsich", "0,5 L", false, "Text", 20, 4, 0, 0, 0, "Getränke"));
        for (Product p : newProducts){
            new CreateProductTask(this.crudOperations, this.getActivity())
                    .execute(p)
                    .thenAccept(this::createInitialOffers);
        }

        return newProducts;
    }

    // Methode zum Erstellen von neuen Produkten
    private List<Product> createProducts(){
        List<Product> newProducts = new ArrayList<>();

        newProducts.add(new Product("Haribo Fruchtgummi Tropifrutti", "200 g", true,
                "HARIBO Tropifrutti macht das Leben einfach tropischer! Ob als Tukan, Palme, Maracujablüte, Banane, Mango-Mandarine, Cassis oder Ananas - HARIBO Tropifrutti ist ein einzigartig fruchtiges Naschvergnügen!\n\n" +
                        "Zutaten: Zucker; Glukosesirup; Gelatine; Säuerungsmittel: Citronensäure; Frucht- und Pflanzenkonzentrate: Saflor, Spirulina, Apfel, Karotte, Heidelbeere, Holunderbeere, Schwarze Johannisbeere, Orange, Kiwi, Zitrone, Aronia, Traube, Mango, Passionsfrucht; Aroma; Holunderbeerextrakt; Überzugsmittel: Bienenwachs weiß und gelb, Carnaubawachs; Karamellsirup; Invertzuckersirup. Kann Spuren von Milch, Weizen enthalten.\n\n" +
                        "Allergenhinweise: Kann enthalten: Glutenhaltiges Getreide und glutenhaltige Getreideerzeugnisse. Weizen und Weizenerzeugnisse (glutenhaltiges Getreide). Milch und Milcherzeugnisse (einschließlich Lactose) [1].\n\n",
               349, 82, 0.5, 4.5, 0, "Süssigkeiten"));

        for (Product p : newProducts){
            new CreateProductTask(this.crudOperations, this.getActivity())
                    .execute(p)
                    .thenAccept(this::createOffers);
        }

        return newProducts;
    }

    // Methode zum Erstellen der Angebote für die initialen Produkte
    private void createInitialOffers(Product product){

        switch (product.getProductName()){
            case "Coca Cola":
                new CreateOfferTask(this.crudOperations,this.getActivity())
                        .execute(new Offer(product.getId(), "Lidl", 0.49, true))
                        .thenAccept(createdOffer -> {
                            new CreateOfferTask(this.crudOperations,this.getActivity())
                                    .execute(new Offer(product.getId(), "Rewe", 0.69, false))
                                    .thenAccept(createdOffer2 -> {
                                        List<Offer> offerList = new ArrayList<>();
                                        offerList.add(createdOffer);
                                        offerList.add(createdOffer2);
                                        this.productWithOffers.add(new ProductWithOffers(product, offerList));
                                    });
                        });
                break;
            case "Milka Erdbeer":
                new CreateOfferTask(this.crudOperations,this.getActivity())
                        .execute(new Offer(product.getId(), "Lidl", 0.95, true))
                        .thenAccept(createdOffer -> {
                            new CreateOfferTask(this.crudOperations,this.getActivity())
                                    .execute(new Offer(product.getId(), "Rewe", 0.89, true))
                                    .thenAccept(createdOffer2 -> {
                                        List<Offer> offerList = new ArrayList<>();
                                        offerList.add(createdOffer);
                                        offerList.add(createdOffer2);
                                        this.productWithOffers.add(new ProductWithOffers(product, offerList));
                                    });
                        });
                break;
            case "Teekanne Organics Happy Time":
                new CreateOfferTask(this.crudOperations,this.getActivity())
                        .execute(new Offer(product.getId(), "Lidl", 2.99, false))
                        .thenAccept(createdOffer -> {
                            new CreateOfferTask(this.crudOperations,this.getActivity())
                                    .execute(new Offer(product.getId(), "Rewe", 3.19, false))
                                    .thenAccept(createdOffer2 -> {
                                        List<Offer> offerList = new ArrayList<>();
                                        offerList.add(createdOffer);
                                        offerList.add(createdOffer2);
                                        this.productWithOffers.add(new ProductWithOffers(product, offerList));
                                    });
                        });

                break;
            case "Teekanne Fresh Pfirsich":
                new CreateOfferTask(this.crudOperations,this.getActivity())
                        .execute(new Offer(product.getId(), "Lidl", 1.19, false))
                        .thenAccept(createdOffer -> {
                            new CreateOfferTask(this.crudOperations,this.getActivity())
                                    .execute(new Offer(product.getId(), "Rewe", 0.89, true))
                                    .thenAccept(createdOffer2 -> {
                                        List<Offer> offerList = new ArrayList<>();
                                        offerList.add(createdOffer);
                                        offerList.add(createdOffer2);
                                        this.productWithOffers.add(new ProductWithOffers(product, offerList));
                                    });
                        });

                break;
        }
    }

    // Methode zum Erstellen der Angebote für die neune Produkte
    private void createOffers(Product product){
        new CreateOfferTask(this.crudOperations,this.getActivity())
                .execute(new Offer(product.getId(), "Lidl", 0.49, true))
                .thenAccept(createdOffer -> {
                    new CreateOfferTask(this.crudOperations,this.getActivity())
                            .execute(new Offer(product.getId(), "Edeka", 0.89, false))
                            .thenAccept(createdOffer2 -> {
                                List<Offer> offerList = new ArrayList<>();
                                offerList.add(createdOffer);
                                offerList.add(createdOffer2);
                                this.productWithOffers.add(new ProductWithOffers(product, offerList));
                            });
                });

    }

    // Methode zum öffnen der Produktdetails
    private void onProductSelected(Product product){
        List<Offer> offers = findOffersForProduct(product);
        Fragment fragment = new ProductDetailsFragment(product, offers);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment, "search");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Methode zum Zurückgeben aller Angebote für ein Produkt
    private List<Offer> findOffersForProduct(Product product){
        for(ProductWithOffers p : productWithOffers){
            if(p.product.equals(product)){
                return p.offers;
            }
        }
        return new ArrayList<>();
    }

}
