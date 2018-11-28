package com.f4pl0.ami;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.f4pl0.ami.Fragments.MainFragments.MenuSurroundsFragment;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ProgressDialog progress;
    BottomNavigationView bottomNavigationView;
    MenuSurroundsFragment menuSurroundsFragment;
    Fragment currentFragment;
    int currentFragmentNo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Create a new progress dialog to show for loading
        progress = new ProgressDialog(this);
        progress.setTitle("Please wait a bit");
        progress.setMessage("Loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog

        //Get the stored session
        final String session = getApplicationContext().getSharedPreferences("shared",MODE_PRIVATE).getString("SessionID","");
        // Check if user has a storred session
        if(!session.isEmpty()){
            //Check if storred session is valid
            //POST REQUEST TO THE SERVER
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url ="http://ami.earth/android/api/checkSession.php";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            dismissLoading();
                            try {
                                if (response.contains("invalid")) {
                                    //Session is invalid, go to setup and delete stored session
                                    getApplicationContext().getSharedPreferences("shared",MODE_PRIVATE).edit().putString("SessionID", "").commit();
                                    Intent myIntent = new Intent(MainActivity.this, SetupActivity.class);
                                    startActivity(myIntent);
                                    finish();
                                } else if (response.contains("valid")){
                                    //Session is valid, continue with loading of the main screen.
                                    setContentView(R.layout.activity_main);

                                    // CODE FOR INITIALIZATION AND EVERYTHING ELSE GOES HERE
                                    InitializeComponents();

                                }else{
                                    Toast.makeText(MainActivity.this, "There was an error." , Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }catch(Exception e){
                                Toast.makeText(MainActivity.this, "There was an error." , Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this, "There was an error connecting to the server.", Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {
                    //Put the POST parameters to the request
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("sessionTXT", session);
                    return params;
                }
            };
            //Show the dialog and do the POST Server request
            showLoading("Loading...");
            queue.add(postRequest);
        }else{
            dismissLoading();
            Intent myIntent = new Intent(this, SetupActivity.class);
            startActivity(myIntent);
            finish();
        }
    }
    private void InitializeComponents(){
        //Method for initializing main components
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        menuSurroundsFragment = new MenuSurroundsFragment();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch(menuItem.getItemId()){
                    case R.id.navigation_menu_surrounds:
                        if(currentFragmentNo == 0)return false;
                        currentFragment = menuSurroundsFragment;
                        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                        currentFragmentNo = 0;
                        break;
                    case R.id.navigation_menu_discover:
                        if(currentFragmentNo == 1)return false;
                        currentFragment = menuSurroundsFragment;
                        if(currentFragmentNo > 1){
                            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                        }else{
                            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                        }
                        currentFragmentNo = 1;
                        break;
                    case R.id.navigation_menu_matching:
                        if(currentFragmentNo == 2)return false;
                        currentFragment = menuSurroundsFragment;
                        if(currentFragmentNo > 2){
                            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                        }else{
                            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                        }
                        currentFragmentNo = 2;
                        break;
                    case R.id.navigation_menu_chats:
                        if(currentFragmentNo == 3)return false;
                        currentFragment = menuSurroundsFragment;
                        if(currentFragmentNo > 3){
                            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                        }else{
                            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                        }
                        currentFragmentNo = 3;
                        break;
                    case R.id.navigation_menu_profile:
                        if(currentFragmentNo == 4)return false;
                        currentFragment = menuSurroundsFragment;
                        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                        currentFragmentNo = 4;
                        break;
                }
                transaction.replace(R.id.mainFragment, currentFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            }
        });
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        currentFragment = menuSurroundsFragment;
        transaction.replace(R.id.mainFragment, currentFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public void showLoading(String message){
        //Method for showing the loading progress dialog
        progress.setMessage(message);
        progress.show();
    }
    public void dismissLoading(){
        //Method for dismissing the possibly ongoing progress dialog
        progress.dismiss();
    }
}
