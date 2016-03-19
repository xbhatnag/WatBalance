package com.cg.watbalance.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.cg.watbalance.R;
import com.cg.watbalance.login;

public class Preferences extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyPreferenceFragment myPrefFrag = new MyPreferenceFragment();
        myPrefFrag.setContext(getBaseContext());
        getFragmentManager().beginTransaction().replace(android.R.id.content, myPrefFrag).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        Context myContext;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            Preference myPref = findPreference("logout");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {
                    SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
                    SharedPreferences.Editor myEditor = myPreferences.edit();
                    myEditor.remove("login");
                    myEditor.apply();
                    Intent myIntent = new Intent(myContext, login.class);
                    startActivity(myIntent);
                    return true;
                }
            });
        }

        public void setContext(Context newContext) {
            myContext = newContext;
        }
    }
}
