package bankzworld.movies.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import javax.inject.Inject;

import bankzworld.movies.R;
import bankzworld.movies.activity.MainActivity;
import bankzworld.movies.injection.DaggerApplication;

public class MainPreferenceFragment extends PreferenceFragment {

    @Inject
    SharedPreferences preferences;

    private FragmentListener fragmentListener = null;
    private int listIndex = 0;

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs);

        ((DaggerApplication) getActivity().getApplication()).getAppComponent().inject(this);

        // sort preference change listener
        bindPreferenceSummaryToValue(findPreference(getString(R.string.sort_key)));

        //get the int key of saved category
        listIndex = preferences.getInt(getString(R.string.sort_key), 0);

        final CheckBoxPreference switchPreference = (CheckBoxPreference) findPreference(getString(R.string.theme_key));
        switchPreference.setSummaryOn(getString(R.string.night));
        switchPreference.setSummaryOff(getString(R.string.day));

        switchPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean isChecked = (boolean) newValue;
            preferences.edit().putBoolean(getString(R.string.get_theme_mode), isChecked).apply();
//            restartActivity();
            fragmentListener.onThemeChanged(true);
            return true;
        });

        // feedback preference click listener
        Preference myPref = findPreference(getString(R.string.key_send_feedback));
        myPref.setOnPreferenceClickListener(preference -> {
            sendFeedback(getActivity());
            return true;
        });
    }

    @Override
    public void onAttach(Context context){
        if(context instanceof FragmentListener){
            fragmentListener = (FragmentListener)context;
        }else{
            throw new RuntimeException(context.toString() + "must implement FragmentListener");
        }
        super.onAttach(context);
    }

    private void restartActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        getActivity().finish();
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();
            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                // Set the summary to reflect the new value.
                String summary = (index >= 0 ? listPreference.getEntries()[index].toString() : "");
                preference.setSummary(TextUtils.isEmpty(summary)? null: summary);
                // store in sharedPrefs
                if(index != listIndex){
                    preferences.edit().putInt(getString(R.string.sort_key), index).apply();
                    fragmentListener.onCategoryChanged(summary);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Email client intent to send support mail
     * Appends the necessary device information to email body
     * useful when providing support
     */
    public static void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"vsquare396@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Application Issue");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }

    public interface FragmentListener {
        void onThemeChanged(boolean value);
        void onCategoryChanged(String newCategory);
    }

}
