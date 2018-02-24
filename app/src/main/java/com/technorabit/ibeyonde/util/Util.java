package com.technorabit.ibeyonde.util;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.technorabit.ibeyonde.fragment.dailog.BaseFragmentDialog;
import com.technorabit.ibeyonde.fragment.dailog.LoadingDialog;

/**
 * Created by raja on 31/08/17.
 */

public class Util {
    public static void hideKeyBoard(FragmentActivity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromInputMethod(view.getWindowToken(), InputMethodManager.RESULT_HIDDEN);
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }


    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    public static BaseFragmentDialog showBaseLoading(FragmentActivity fragmentActivity) {
        LoadingDialog dFragment = new LoadingDialog();
        dFragment.show(fragmentActivity.getSupportFragmentManager(), "Dialog Fragment");
        return dFragment;
    }


    public static String recoverImageUrl(String path) {
        if (path != null)
            path = path.replace(" ", "%20");
        if (!path.startsWith("http"))
            path = "http://" + path;
        return path;
    }
}
