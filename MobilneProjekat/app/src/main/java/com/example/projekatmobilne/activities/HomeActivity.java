package com.example.projekatmobilne.activities;

import static android.widget.Toast.makeText;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuProvider;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.databinding.ActivityHomeBinding;
import com.example.projekatmobilne.databinding.ActivityLoginBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.HashSet;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private ActionBar actionBar;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private AppBarConfiguration mAppBarConfiguration;

    private Set<Integer> topLevelDestinations = new HashSet<>();

    private NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        drawer = binding.drawerLayout;
        navigationView = binding.navView;
        toolbar = binding.activityHomeBase.toolbar;
        // Postavljamo toolbar kao glavnu traku za ovu aktivnost
        setSupportActionBar(toolbar);
        // Dobavljamo referencu na glavnu traku za ovu aktivnost
        actionBar = getSupportActionBar();
        if(actionBar != null){
            // postavlja prikazivanje "strelice prema nazad" (back arrow)
            // kao indikatora navigacije na lijevoj strani Toolbar-a.
            actionBar.setDisplayHomeAsUpEnabled(false);
            // postavlja ikonu koja se prikazuje umjesto strelice prema nazad.
            // U ovom slučaju, postavljena je ikona hamburger iz drawable resursa (ic_hamburger).
            actionBar.setHomeAsUpIndicator(R.drawable.ic_hamburger);
            //vo omogućuje da se klikom na gumb 'home' na Toolbar-u
            // aktivira povratak na prethodni zaslon.
            actionBar.setHomeButtonEnabled(true);
        }

        //  ActionBarDrawerToggle se koristi za povezivanje i upravljanje navigation drawer-om
        //  unutar Android aplikacije. ActionBarDrawerToggle je klasa koja olakšava sinhronizaciju
        //  između navigation drawer-a i ActionBar-a (ili Toolbar-a) te omogućava otvaranje
        //  i zatvaranje navigation drawer-a putem ikone u ActionBar-u ili Toolbar-u.
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,  R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        // dodajemo navigation drawer-u listener za događaje koji se dese.
        // actionBarDrawerToggle prati promene stanja drawera i reaguje na njih.
        drawer.addDrawerListener(actionBarDrawerToggle);
        // syncState() se koristi kako bi se uskladile ikone (npr. "hamburger" ikona)
        // i stanja između ActionBar-a (ili Toolbar-a) i drawer-a. Ova metoda osigurava
        // da se ikona na ActionBar-u (ili Toolbar-u) pravilno menja u zavisnosti
        // od stanja drawer-a (otvoreno ili zatvoreno).
        actionBarDrawerToggle.syncState();

//        topLevelDestinations.add(R.id.nav_language);
//        topLevelDestinations.add(R.id.nav_settings);
        // NavigationController se koristi za upravljanje promenama destinacija unutar Android
        // aplikacije korištenjem Android Navigation komponente.
        // Pomoću NavController i OnDestinationChangedListener, prati se promena trenutne
        // destinacije (screen-a/fragmenta) unutar aplikacije.
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            Log.i("ShopApp", "Destination Changed");
            // Implementacija koja se poziva kada se promijeni destinacija
            // Check if the current destination is a top-level destination (destination outside the drawer)
            int id = navDestination.getId();
            //boolean isTopLevelDestination = topLevelDestinations.contains(id);
            /* Logic to determine if the destination is top level */;
            //if (!isTopLevelDestination) {
            switch (id) {
                case R.id.nav_home: // Replace with your actual menu item ID
                    // Do something when this item is selected, such as navigating to a specific fragment
                    // For example:
                    // navController.navigate(R.id.nav_products); // Replace with your destination fragment ID
                    makeText(HomeActivity.this, "Home", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.nav_account:
                    makeText(HomeActivity.this, "New product", Toast.LENGTH_SHORT).show();
                    break;

            }
                // Close the drawer if the destination is not a top level destination
                //drawer.closeDrawers();
            //}
        });

        // AppBarConfiguration odnosi se na konfiguraciju ActionBar-a (ili Toolbar-a) u Android aplikaciji
        // kako bi se omogućila navigacija koristeći Android Navigation komponentu.
        // Takođe, postavlja se bočni meni (navigation drawer) u skladu sa
        // konfiguracijom akcione trake i navigacije.
        // Svaki ID menija prosleđuje se kao skup ID-ova jer svaki meni treba smatrati odredištima najvišeg nivoa.
        mAppBarConfiguration = new AppBarConfiguration
                .Builder(R.id.nav_home, R.id.nav_account)
                .setOpenableLayout(drawer)
                .build();
        // Ova linija koda postavlja navigationView da radi zajedno sa NavController-om.
        // To znači da će NavigationView reagovati na korisničke interakcije i navigaciju kroz aplikaciju putem NavController-a.
        NavigationUI.setupWithNavController(navigationView, navController);
        // Ova linija koda povezuje NavController sa ActionBar-om (ili Toolbar-om) tako da ActionBar (ili Toolbar)
        // može pravilno reagovati na navigaciju kroz različite destinacije koje su navedene unutar mAppBarConfiguration.
        // NavController će upravljati povratnom strelicom i ponašanjem akcione trake u skladu sa postavljenim destinacijama.
        // NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);


        addMenu();
    }

    private void addMenu()
    {
        MenuProvider menuProvider = new MenuProvider()
        {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                menuInflater.inflate(R.menu.nav_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem)
            {
                NavController navController = Navigation.findNavController(HomeActivity.this, R.id.fragment_nav_content_main);

                // Nakon toga, koristi se NavigationUI.onNavDestinationSelected(item, navController)
                // kako bi se omogućila integracija između MenuItem-a i odredišta unutar aplikacije
                // definisanih unutar navigacionog grafa (NavGraph).
                // Ova funkcija proverava da li je odabrana stavka izbornika povezana s nekim
                // odredištem unutar navigacionog grafa i pokreće tu navigaciju ako postoji
                // odgovarajuće podudaranje.
                return NavigationUI.onNavDestinationSelected(menuItem, navController);
            }
        };

        addMenuProvider(menuProvider, HomeActivity.this, Lifecycle.State.RESUMED);
    }
}