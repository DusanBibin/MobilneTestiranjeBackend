package com.example.projekatmobilne.activities;

import static android.widget.Toast.makeText;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.databinding.ActivityHomeBinding;
import com.example.projekatmobilne.model.Enum.Role;
import com.example.projekatmobilne.tools.JWTManager;
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
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if(actionBar != null){

            actionBar.setDisplayHomeAsUpEnabled(false);

            actionBar.setHomeAsUpIndicator(R.drawable.ic_hamburger);

            actionBar.setHomeButtonEnabled(true);
        }

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,  R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();


        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
//        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
//            Log.i("ShopApp", "Destination Changed");
//
//            int id = navDestination.getId();
//
////            switch (id) {
////                case R.id.nav_home:
////                    break;
////                case R.id.nav_account:
////                    break;
////            }
//
//        });
        System.out.println("ROLA ZA OVU GRESKU STO HOCU DA ULOVIM JE: " + JWTManager.getRole());
        populateNavigationToolbarMenu(Role.valueOf(JWTManager.getRole()));
        //addMenu();
//        mAppBarConfiguration = new AppBarConfiguration
//                .Builder(R.id.nav_home_guest, R.id.nav_account)
//                .setOpenableLayout(drawer)
//                .build();

        NavigationUI.setupWithNavController(navigationView, navController);



    }

    private void populateNavigationToolbarMenu(Role userRole) {
        navigationView.getMenu().clear();

        MenuInflater inflater = getMenuInflater();
        if(userRole.equals(Role.GUEST)){
            inflater.inflate(R.menu.guest_nav_menu, navigationView.getMenu());
            navController.setGraph(R.navigation.guest_navigation);
        }
        if(userRole.equals(Role.OWNER)){
            inflater.inflate(R.menu.host_nav_menu, navigationView.getMenu());
            navController.setGraph(R.navigation.owner_navigation);
        }

    }


    private void addMenu()
    {

        Role role = Role.valueOf(JWTManager.getRole());
        System.out.println(role);
        MenuProvider menuProvider = new MenuProvider()
        {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();

                if(role.equals(Role.GUEST)) menuInflater.inflate(R.menu.guest_nav_menu, menu);
                if(role.equals(Role.OWNER)){
                    System.out.println("OVDE SMO USLI");
                    menuInflater.inflate(R.menu.host_nav_menu, menu);
                }
                //TODO dodati za admina isto

            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem)
            {
                NavController navController = Navigation.findNavController(HomeActivity.this, R.id.fragment_nav_content_main);

                return NavigationUI.onNavDestinationSelected(menuItem, navController);
            }
        };

        addMenuProvider(menuProvider, HomeActivity.this, Lifecycle.State.RESUMED);
    }
}