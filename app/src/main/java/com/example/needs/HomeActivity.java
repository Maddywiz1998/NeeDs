package com.example.needs;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.needs.About.AboutAppFragment;
import com.example.needs.Cart.CartDetails;
import com.example.needs.Cart.CartFragment;
import com.example.needs.Cart.OrderFragment;
import com.example.needs.EmptyFragments.CommingSoonFragment;
import com.example.needs.EmptyFragments.EmptyCartFragment;
import com.example.needs.EmptyFragments.EmptyOrderFragment;
import com.example.needs.EmptyFragments.NotConnected;
import com.example.needs.Home.HomeFragment;
import com.example.needs.Home.ItemFragment;
import com.example.needs.Menu.DrawerAdapter;
import com.example.needs.Menu.DrawerItem;
import com.example.needs.Menu.SimpleItem;
import com.example.needs.Menu.SpaceItem;
import com.example.needs.NetworkListener.NetworkUtil;
import com.example.needs.Settings.ChangePasswordFragment;
import com.example.needs.Settings.ReportFragment;
import com.example.needs.Settings.SettingsFragment;
import com.example.needs.Settings.SubmitFeedBackFragment;
import com.facebook.login.LoginManager;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class HomeActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, DrawerAdapter.OnItemSelectedListener {
    private static final int POS_HOME = 0;
    private static final int POS_ORDER = 1;
    private static final int POS_ABOUT = 2;
    private static final int POS_SHARE = 3;
    private static final int POS_SETTINGS = 4;
    private static final int POS_LOGOUT = 6;
    private Fragment fragment = null;
    private SlidingRootNav slidingRootNav;
    private RelativeLayout cart;
    private String[] screenTitles;
    private String username;
    private TextView username_tile, cart_tile;
    private long cartValue;
    private Drawable[] screenIcons;
    private ImageView profilePic, toolbarIcon;
    private BroadcastReceiver networkChangeReceiver;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
    private DatabaseReference mDataBaseRef = mDataBase.getReference("Users").child(firebaseAuth.getCurrentUser().getUid());

    private FragmentManager fm = getSupportFragmentManager();

    private Toolbar toolbar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        cart = findViewById(R.id.cart);
        toolbarIcon = findViewById(R.id.toolBarIcon);
        toolBarIconCart();


        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(true)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();


        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        final DrawerAdapter drawAdapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_HOME).setChecked(true),
                createItemFor(POS_ORDER),
                createItemFor(POS_ABOUT),
                createItemFor(POS_SHARE),
                createItemFor(POS_SETTINGS),
                new SpaceItem(48),
                createItemFor(POS_LOGOUT)));
        drawAdapter.setListener(this);
        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(drawAdapter);
        networkChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {

                int status = NetworkUtil.getConnectivityStatusString(context);
                if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                    if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                        fragment = new NotConnected();
                        FragmentManager fm = getSupportFragmentManager();
                        fm.popBackStackImmediate(fragment.getTag(), 0);
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.container, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                        cart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                        drawAdapter.setListener(new DrawerAdapter.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(int position) {

                            }
                        });


                    } else {
                        fragment = new HomeFragment();
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.container, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                        toolBarIconCart();
                        drawAdapter.setListener(HomeActivity.this);

                    }
                }
            }
        };


        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
                if (f instanceof HomeFragment) {
                    toolbar.setTitle("Home");
                    toolBarIconCart();
                } else if (f instanceof ItemFragment || f instanceof CommingSoonFragment) {
                    toolbar.setTitle("Item");
                    toolBarIconCart();
                } else if (f instanceof CartFragment || f instanceof EmptyCartFragment) {
                    toolbar.setTitle("Cart");
                    toolBarIconCheckout(f);

                } else if (f instanceof OrderFragment || f instanceof EmptyOrderFragment) {
                    toolbar.setTitle("Order");
                    toolBarIconCart();
                } else if (f instanceof AboutAppFragment) {
                    toolbar.setTitle("About");
                    toolBarIconCart();
                } else if (f instanceof SettingsFragment) {
                    toolbar.setTitle("Settings");
                    toolBarIconCart();
                }


            }
        });


        new GetData().execute();


    }

    public void toolBarIconCart() {
        toolbarIcon.setImageResource(R.drawable.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(HomeActivity.this, "cart clicked", Toast.LENGTH_SHORT).show();
                Fragment fragment = new CartFragment();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.container, fragment);
                ft.addToBackStack(null);
                ft.commit();
                toolbar.setTitle("Cart");

            }
        });

    }

    public void toolBarIconCheckout(final Fragment fragment) {
        toolbarIcon.setImageResource(R.drawable.checkout);

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment instanceof CartFragment) {
                    CartFragment cartFragment = (CartFragment) fragment;
                    ArrayList<CartDetails> arrayList = cartFragment.getCartDetailsArrayList();
                    Intent intent = new Intent(HomeActivity.this, CheckoutActivity.class);
                    intent.putExtra("cart", arrayList);
                    startActivity(intent);
                } else
                    Toast.makeText(HomeActivity.this, "Add items in cart first", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onItemSelected(int position) {


        if (position == POS_HOME) {

            fragment = new HomeFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.container, fragment);
            ft.addToBackStack(null);
            ft.commit();

        } else if (position == POS_ORDER) {

            fragment = new OrderFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.container, fragment);
            ft.addToBackStack(null);
            ft.commit();

        } else if (position == POS_ABOUT) {

            fragment = new AboutAppFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.container, fragment);
            ft.addToBackStack(null);
            ft.commit();
        } else if (position == POS_SHARE) {

           /* Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, " Click to download Colors Soda app from wwww. ");
            sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "G E E N  B O X");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);*/

        } else if (position == POS_SETTINGS) {

            fragment = new SettingsFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.container, fragment);
            ft.addToBackStack(null);
            ft.commit();

        } else if (position == POS_LOGOUT) {

            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
        }

        slidingRootNav.closeMenu();


    }

    private DrawerItem createItemFor(int position) {

        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorSecondary))
                .withSelectedIconTint(color(R.color.navfun))
                .withSelectedTextTint(color(R.color.navfun));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {


    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public void ChangePassword(View view) {

        fragment = new ChangePasswordFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void ChangeData(View view) {

      /*  Intent intent = new Intent(HomeActivity.this, CourseInfoActivity.class);
        startActivity(intent);*/
    }

    public void ChangeTheme(View view) {

        /*fragment = new ThemeFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();*/
    }

    public void BugReport(View view) {

        fragment = new ReportFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void SubmitFeedBack(View view) {

        fragment = new SubmitFeedBackFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }


    class GetData extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            mDataBaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    username = dataSnapshot.child("Name").getValue(String.class);
                    String imageUrl = dataSnapshot.child("image").getValue(String.class);

                    cart_tile = findViewById(R.id.cart_tile);
                    username_tile = findViewById(R.id.username_title);
                    profilePic = findViewById(R.id.profilePic);
                    Glide.with(getApplicationContext()).load(imageUrl).into(profilePic);
                    username_tile.setText(username);
                    if (dataSnapshot.child("Cart").exists()) {
                        cartValue = dataSnapshot.child("Cart").getChildrenCount();
                        cart_tile.setText(Long.toString(cartValue));
                    } else {
                        cart_tile.setText("0");
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0)
            finish();

    }


}