package com.example.angebots_app;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.angebots_app.tasks.DeleteShoppingItemTask;
import com.example.angebots_app.tasks.UpdateShoppingItemTask;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 *  Individueller Adapter für die Listen aus dem ShoppingListFragment
 */
public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    private List<ProductAndOffer> productAndOfferList;
    private IProductCRUDOperations crudOperations;
    private Activity activity;
    private List<ShoppingItem> allShoppingItems = new ArrayList<>();
    private List<ProductAndOffer> lidlList, reweList, doneList;
    private RecyclerView lidl, rewe, done;

    public ShoppingListAdapter(List<ProductAndOffer> productAndOfferList, IProductCRUDOperations crudOperations,
                               Activity owner, List<ShoppingItem> allShoppingItems, List<ProductAndOffer> doneList,
                               List<ProductAndOffer> lidlList, List<ProductAndOffer> reweList,
                               RecyclerView lidl, RecyclerView rewe, RecyclerView done){
        this.productAndOfferList = productAndOfferList;
        this.crudOperations = crudOperations;
        this.activity = owner;
        this.allShoppingItems = allShoppingItems;
        this.doneList = doneList;
        this.reweList = reweList;
        this.lidlList = lidlList;
        this.lidl = lidl;
        this.rewe = rewe;
        this.done = done;
    }

    // Listenelement befüllen und binden
    @NonNull
    @Override
    public ShoppingListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_shoppinglist_listelement, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    // Daten und Aktionen für ein Element der Liste definieren
    @Override
    public void onBindViewHolder(@NonNull ShoppingListAdapter.ViewHolder holder, int position) {
        holder.setProductAndOffer(productAndOfferList.get(position));
        holder.bindData();

        holder.delIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productAndOfferList.remove(holder.productAndOffer);
                new DeleteShoppingItemTask(crudOperations, activity)
                        .execute(findShoppingItem(holder.productAndOffer.getShoppingItemId()))
                        .thenAccept(aBoolean -> {
                            showFeedbackMessage("Produkt wurde aus der Einkaufsliste entfernt");
                            notifyDataSetChanged();
                        });
            }
        });

        holder.checkBox.setOnCheckedChangeListener(null);

        // Checkbox setzen
        if(this.doneList == null) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        // Aktion für das Ändern der Checkbox definieren
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    ShoppingItem s = findShoppingItem(holder.productAndOffer.getShoppingItemId());
                    doneList.add(holder.productAndOffer);
                    productAndOfferList.remove(holder.productAndOffer);
                    s.setDone(true);
                    new UpdateShoppingItemTask(crudOperations, activity)
                            .execute(s)
                            .thenAccept(aBoolean -> {
                                done.getAdapter().notifyDataSetChanged();
                                notifyDataSetChanged();
                            });
                } else {
                    ShoppingItem s = findShoppingItem(holder.productAndOffer.getShoppingItemId());
                    if(holder.productAndOffer.getOffer().getShopName().equals("Rewe")){
                        reweList.add(holder.productAndOffer);
                    }
                    if(holder.productAndOffer.getOffer().getShopName().equals("Lidl")){
                        lidlList.add(holder.productAndOffer);
                    }
                    productAndOfferList.remove(holder.productAndOffer);
                    s.setDone(false);
                    new UpdateShoppingItemTask(crudOperations, activity)
                            .execute(s)
                            .thenAccept(aBoolean -> {
                                lidl.getAdapter().notifyDataSetChanged();
                                rewe.getAdapter().notifyDataSetChanged();
                                notifyDataSetChanged();
                            });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productAndOfferList.size();
    }

    private ShoppingItem findShoppingItem(long shoppingItemId){
        for(ShoppingItem s : this.allShoppingItems){
            if(s.getId() == shoppingItemId)
                return s;
        }
        return null;
    }

    private void showFeedbackMessage(String msg) {
        Snackbar.make(this.activity.findViewById(R.id.shoppingListViewRoot), msg, BaseTransientBottomBar.LENGTH_SHORT).show();
    }

    // Klasse, welche eine komplexe Klasse in einem Listenelement darstellen kann
    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ProductAndOffer productAndOffer;
        private TextView productNameShoppingList, priceShoppingList;
        private ImageView productPictureShoppingList;
        private ImageView delIcon;
        private MaterialCheckBox checkBox;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            delIcon = itemView.findViewById(R.id.shoppingListIconList);
            checkBox = itemView.findViewById(R.id.checkDone);
        }

        public ProductAndOffer getProductAndOffer() {
            return productAndOffer;
        }

        public void setProductAndOffer(ProductAndOffer productAndOffer) {
            this.productAndOffer = productAndOffer;
        }

        public ImageView getDelIcon() {
            return delIcon;
        }

        public void setDelIcon(ImageView delIcon) {
            this.delIcon = delIcon;
        }

        public MaterialCheckBox getCheckBox() {
            return checkBox;
        }

        public void setCheckBox(MaterialCheckBox checkBox) {
            this.checkBox = checkBox;
        }

        // Daten in das Listenelement eintragen
        public void bindData(){
            if(productNameShoppingList == null){
                productNameShoppingList = itemView.findViewById(R.id.productNameShoppingList);
            }
            if(priceShoppingList == null){
                priceShoppingList = itemView.findViewById(R.id.PriceShoppingList);
            }
            if (productPictureShoppingList == null){
                productPictureShoppingList = itemView.findViewById(R.id.productPictureShoppingList);
            }
            productNameShoppingList.setText(productAndOffer.getProduct().getProductName());
            priceShoppingList.setText(Double.toString(productAndOffer.getOffer().getPrice()) + " €");
            priceShoppingList.setTextColor(productAndOffer.getOffer().isOffer() ? Color.parseColor("#37B242") : Color.BLACK);
            productPictureShoppingList.setImageResource(productAndOffer.getProduct().getResourceSrc());


        }
    }
}
