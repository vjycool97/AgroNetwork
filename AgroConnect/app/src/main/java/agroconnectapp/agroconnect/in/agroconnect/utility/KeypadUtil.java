package agroconnectapp.agroconnect.in.agroconnect.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by sumanta on 22/9/15.
 */
public final class KeypadUtil {
    /**
     * Show keypad and focus to given EditText
     */
    public static void showKeypad(Context context, EditText target) {
        if (context == null || target == null) {
            return;
        }
        InputMethodManager inputMethodManager = getInputMethodManager(context);
        inputMethodManager.showSoftInput(target, InputMethodManager.SHOW_IMPLICIT);
    }
    /**
     * Show keypad
     */
    public static void showKeypad(Activity activity) {
        try {
            InputMethodManager inputMethodManager = getInputMethodManager(activity);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }
    /**
     * Show keypad and focus to given EditText.
     * Use this method if target EditText is in Dialog.
     */
    public static void showKeypadInDialog(Dialog dialog, EditText target) {
        if (dialog == null || target == null) {
            return;
        }
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        target.requestFocus();
    }
    /**
     * hide keypad
     * @param target  View that currently has focus
     */
    public static void hideKeypad(Context context, View target) {
        if (context == null || target == null) {
            return;
        }
        InputMethodManager inputMethodManager = getInputMethodManager(context);
        inputMethodManager.hideSoftInputFromWindow(target.getWindowToken(), 0);
    }

    /**
     * hide keypad
     * @param activity Activity
     */
    public static void hideKeypad(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            hideKeypad(activity, view);
        }
    }
    private static InputMethodManager getInputMethodManager(Context context) {
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }
}
