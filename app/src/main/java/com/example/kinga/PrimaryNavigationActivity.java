package com.example.kinga;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.kinga.core.RedirectResolver;
import com.example.kinga.core.userUtils.SessionHandler;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kinga.databinding.ActivityMainBinding;

public class PrimaryNavigationActivity extends AppCompatActivity {

    public boolean mReloadTripIndex = false;
    public boolean mReloadProfileView = false;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    SessionHandler mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_trip_create,
                R.id.nav_trip_index,
                R.id.nav_payment,
                R.id.nav_support,
                R.id.nav_profile_view,
                R.id.nav_discount
        )
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Get user session
        mSession = new SessionHandler(this);

        TextView userName=  navigationView.getHeaderView(0).findViewById(R.id.header)
                .findViewById(R.id.nav_header_primary_navigation_username);
        userName.setText(mSession.getUserDetails().getUsername());

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            RedirectResolver.logoutUserAndRedirectToLogin(this, mSession);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}