package bankzworld.movies.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import bankzworld.movies.R;
import bankzworld.movies.fragment.PopularFragment;
import bankzworld.movies.fragment.TheatreFragment;
import bankzworld.movies.fragment.TopRatedFragment;
import bankzworld.movies.fragment.UpcomingFragment;
import bankzworld.movies.injection.DaggerApplication;
import bankzworld.movies.util.BaseActivity;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    SharedPreferences preferences;

    private static String FRAGMENT_KEY = "KEY";
    private static String FRAGMENT = "FRAGMENT";
    private Fragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set dagger
        ((DaggerApplication) getApplication()).getAppComponent().inject(this);
        boolean getMode = preferences.getBoolean(getString(R.string.get_theme_mode), false);
        if (getMode) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getMoviesOnline();
    }

    private boolean getMoviesOnline() {
        // get Item from preference
        int key = preferences.getInt(getString(R.string.sort_key), 0);
        switch (key) {
            case 0:
                fragment = new PopularFragment();
                loadFragment(fragment);
                break;
            case 1:
                fragment = new TopRatedFragment();
                loadFragment(fragment);
                break;
            case 2:
                fragment = new TheatreFragment();
                loadFragment(fragment);
                break;
            case 3:
                fragment = new UpcomingFragment();
                loadFragment(fragment);
                break;
            default:
                Log.e(TAG, "onCreate: error,");
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(FRAGMENT_KEY, FRAGMENT);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            loadFragment(fragment);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_favourite) {
            startActivity(new Intent(this, FavouriteActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        if (i == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }
}
