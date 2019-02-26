package bankzworld.movies.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import javax.inject.Inject;

import bankzworld.movies.R;

import bankzworld.movies.fragment.MainPreferenceFragment;
import bankzworld.movies.injection.DaggerApplication;
import bankzworld.movies.pref.AppCompatPreferenceActivity;
import bankzworld.movies.util.Config;

public class SettingsActivity extends AppCompatPreferenceActivity implements
        MainPreferenceFragment.FragmentListener {

    private static final String TAG = "SettingsActivity";

    static boolean isDirty = false;
    static String query = "";

    @Inject
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ((DaggerApplication) getApplication()).getAppComponent().inject(this);

        boolean getMode = preferences.getBoolean(getString(R.string.get_theme_mode), false);
        if (getMode) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        //create an intent to hold data to pass to calling activity
        Log.e(this.getLocalClassName(),String.valueOf(isDirty));
        Intent intent = new Intent();
        intent.putExtra(Config.IS_DIRTY,isDirty);
        intent.putExtra(Config.NEW_CATEGROY,query);
        setResult(Activity.RESULT_OK,intent);
        isDirty = false; query = "";
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onThemeChanged(boolean value) {
        if(value){
            //recreate the activity to apply the new theme
            this.recreate();
        }
        isDirty = value;
        Log.e(this.getLocalClassName(),String.valueOf(isDirty));
    }

    @Override
    public void onCategoryChanged(String newCategory) {
       query = newCategory;
    }
}
