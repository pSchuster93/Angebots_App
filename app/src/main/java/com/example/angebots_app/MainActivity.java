package com.example.angebots_app;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

/**
*  Hauptaktivität, auf welcher die Fragmente gewechselt werden
*/
public class MainActivity extends AppCompatActivity {

    private String location = "29439 Lüchow";
    private boolean clearDB = false;
    private boolean productsCreated = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        // TopBars setzen
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
              R.id.navigation_search, R.id.navigation_favorite, R.id.navigation_shoppingList, R.id.navigation_more)
              .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        // BottomNavigation setzen
        NavigationUI.setupWithNavController(navView, navController);
        }
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }

     */

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isClearDB() {
        return clearDB;
    }

    public void setClearDB(boolean clearDB) {
        this.clearDB = clearDB;
    }

    public boolean isProductsCreated() {
        return productsCreated;
    }

    public void setProductsCreated(boolean productsCreated) {
        this.productsCreated = productsCreated;
    }
}
