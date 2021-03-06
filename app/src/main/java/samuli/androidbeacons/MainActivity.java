package samuli.androidbeacons;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import samuli.androidbeacons.loginandregister.LogingActivity;
import samuli.androidbeacons.utils.PreferenceUtils;

public class MainActivity extends AppCompatActivity {

    private static Context context;

    private PageAdapter pageAdapter;
    private ViewPager viewPager;

    static int userId;
    static String username;

    public static int getUserId() { return userId; }
    public static String getUsername() { return username; }

    public static Context getContext() {
        return context;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are you sure you want to log out?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PreferenceUtils.saveUsername("",MainActivity.this);
                                PreferenceUtils.savePassword("", MainActivity.this);
                                Intent intent = new Intent(MainActivity.this, LogingActivity.class);
                                MainActivity.this.startActivity(intent);
                            }
                        })
                        .create()
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        context = getApplicationContext();

        pageAdapter = new PageAdapter(getSupportFragmentManager());

        viewPager = findViewById(R.id.container);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        Intent intent = getIntent();
        username = intent.getStringExtra("name");
        userId = intent.getIntExtra("user_id", -1);
    }

    private void setupViewPager(ViewPager viewPager) {
        PageAdapter adapter = new PageAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentMeasure(), "FRONT PAGE");
        adapter.addFragment(new FragmentUserData(), "USER DATA");
        adapter.addFragment(new FragmentBeaconData(), "BEACON DATA");
        viewPager.setAdapter(adapter);
    }
}