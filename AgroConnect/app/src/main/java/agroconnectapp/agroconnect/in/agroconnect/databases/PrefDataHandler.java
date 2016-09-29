package agroconnectapp.agroconnect.in.agroconnect.databases;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import agroconnectapp.agroconnect.in.agroconnect.AgroConnect;

public class PrefDataHandler {
	private SharedPreferences sharedPref;
	private SharedPreferences.Editor editor;
	private PrefDataHandler() {
		sharedPref = PreferenceManager.getDefaultSharedPreferences(AgroConnect.getInstance().getContext());
		editor = sharedPref.edit();
	}
	private static class SingletonHelper{
		private static final PrefDataHandler INSTANCE = new PrefDataHandler();
	}
	public static PrefDataHandler getInstance(){
		return SingletonHelper.INSTANCE;
	}
	public SharedPreferences.Editor getEditor() {
		return editor;
	}
	public SharedPreferences getSharedPref() {
		return sharedPref;
	}
}
