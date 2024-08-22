package com.example.projekatmobilne.activities;

import static android.widget.Toast.makeText;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);




        //actionBar = getSupportActionBar();
        if(getSupportActionBar() != null){

            if(JWTManager.getRoleEnum() == null){
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                //getSupportActionBar().setDisplayShowTitleEnabled(false);

                //binding.toolbar.setVisibility(View.GONE);
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }else{
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_hamburger);

                getSupportActionBar().setHomeButtonEnabled(true);
                            actionBarDrawerToggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar,  R.string.navigation_drawer_open, R.string.navigation_drawer_close);

                drawer.addDrawerListener(actionBarDrawerToggle);

                actionBarDrawerToggle.syncState();
            }


        }







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

        if(JWTManager.getRoleEnum() != null) populateNavigationToolbarMenu(JWTManager.getRoleEnum());
        else populateNavigationToolbarMenu(null);
        //addMenu();
//        mAppBarConfiguration = new AppBarConfiguration
//                .Builder(R.id.nav_home_guest, R.id.nav_account)
//                .setOpenableLayout(drawer)
//                .build();

        NavigationUI.setupWithNavController(navigationView, navController);



    }

    private void populateNavigationToolbarMenu(Role role) {
        navigationView.getMenu().clear();

        MenuInflater inflater = getMenuInflater();
        if(role == null){

            inflater.inflate(R.menu.guest_nav_menu, navigationView.getMenu());
            navController.setGraph(R.navigation.guest_navigation);

        }else{
            if(role.equals(Role.GUEST)){
                inflater.inflate(R.menu.guest_nav_menu, navigationView.getMenu());
                navController.setGraph(R.navigation.guest_navigation);
            }else if(role.equals(Role.OWNER)){
                inflater.inflate(R.menu.host_nav_menu, navigationView.getMenu());
                navController.setGraph(R.navigation.owner_navigation);
            }else if(role.equals(Role.ADMIN)){
                inflater.inflate(R.menu.admin_nav_menu, navigationView.getMenu());
                navController.setGraph(R.navigation.admin_navigation);
            }
        }

    }


    private void addMenu()
    {

        Role role = Role.valueOf(JWTManager.getRole());
        MenuProvider menuProvider = new MenuProvider()
        {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();

                if(role.equals(Role.GUEST)) menuInflater.inflate(R.menu.guest_nav_menu, menu);
                if(role.equals(Role.OWNER)){menuInflater.inflate(R.menu.host_nav_menu, menu);}
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


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back button press here
            if(JWTManager.getRoleEnum() == null){
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish(); // Optional: if you want to close the current activity
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }
}

