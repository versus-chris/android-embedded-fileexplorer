package de.versus.efe;

import android.content.Context;
import android.content.SharedPreferences;

public class VersusPreferences {
	
	public static final String VERSUS_PREFERENCES = "versus_preferences";
	
	private SharedPreferences getVersusPreferences(Context c) {
		return c.getSharedPreferences(VERSUS_PREFERENCES, Context.MODE_PRIVATE);
	}	
	
	// *** GETter and SETter for various data types *** //
	
	public void removePreferenceValue(Context c, String... keys) {
		SharedPreferences preferences = getVersusPreferences(c);
		SharedPreferences.Editor editor = preferences.edit();
		boolean atLeastOnePreferenceHasBeenRemoved = false;
		for (String key : keys) {
			if (preferences.contains(key)) {
				editor.remove(key);
				atLeastOnePreferenceHasBeenRemoved = true;
			}
		}
		if (atLeastOnePreferenceHasBeenRemoved) {
			editor.commit();
		}
	}
	
	public void setPreferenceValue(Context c, boolean value, String... keys) {
		SharedPreferences preferences = getVersusPreferences(c);
		SharedPreferences.Editor editor = preferences.edit();
		for (String helpIndicator : keys) {
			editor.putBoolean(helpIndicator, value);
		}
		editor.commit();
	}
	
	public boolean getPreferenceValue(Context c, String helpIndicator, boolean defaultValue) {
		SharedPreferences preferences = getVersusPreferences(c);
		return preferences.getBoolean(helpIndicator, defaultValue);
	}

	public void setPreferenceValue(Context c, Integer value, String key) {
		SharedPreferences preferences = getVersusPreferences(c);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public int getPreferenceValue(Context c, String key, int defaultValue) {
		SharedPreferences preferences = getVersusPreferences(c);
		return preferences.getInt(key, defaultValue);
	}
	
	public void setPreferenceValue(Context c, String value, String... keys) {
		SharedPreferences preferences = getVersusPreferences(c);
		SharedPreferences.Editor editor = preferences.edit();
		for (String key : keys) {
			editor.putString(key, value);
		}
		editor.commit();
	}
	
	public String getPreferenceValue(Context c, String key, String defaultValue) {
		SharedPreferences preferences = getVersusPreferences(c);
		return preferences.getString(key, defaultValue);
	}

}
