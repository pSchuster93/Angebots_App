package com.example.angebots_app.ui.more;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.angebots_app.R;
import com.example.angebots_app.ui.shopSearch.ShopSearchFragment;

/**
 *  Diese Klasse stellt die Seite "Mehr" in der Anwendung dar.
 */
public class MoreFragment extends Fragment {

    private ListView lv;
    private ArrayAdapter<String> adapter;
    private String[] options = {"Marktsuche"};

    // Initialisierung der Seite
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Seite aufbauen und Verbindung zu Elementen setzen
        View root = inflater.inflate(R.layout.fragment_more, container, false);
        this.lv = root.findViewById(R.id.moreListView);

        // Adapter für die Liste erstellen und setzen
        this.adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, options);
        this.lv.setAdapter(this.adapter);

        // Aktion für das Klicken auf ein Listenelement setzen
        this.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = (String) parent.getItemAtPosition(position);
                if(s.equals("Marktsuche"))
                {
                    onItemSelected(new ShopSearchFragment());
                }
            }
        });

        return root;
    }

    // Neue Seite aufrufen
    private void onItemSelected(Fragment fragment){
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment, "more");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Versuch, die TopBar anzusprechen
    /*@Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        NavController navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
        Toolbar toolbar = view.findViewById(R.id.toolbar_more);

        NavigationUI.setupWithNavController(
                toolbar, navController, appBarConfiguration);

    }

     */
}
