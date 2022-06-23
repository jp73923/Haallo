package com.haallo.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class UiUtils {
    private static final String TAG = UiUtils.class.getSimpleName();


    public static void hideKeyboard(final Context context) {
        if (context == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        Activity activity = null;
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else if (context instanceof ContextWrapper) {
            Context parentContext = ((ContextWrapper) context).getBaseContext();
            if (parentContext instanceof Activity) {
                activity = (Activity) parentContext;
            }
        }

        if (activity == null) {
            Log.w(TAG, "Try to hide keyboard but context type is incorrect " + context.getClass().getSimpleName());
            return;
        }

        if (activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } else {
            Log.w(TAG, "Try to hide keyboard but there is no current focus view");
        }
    }

    public static void showKeyboard(final Context context) {
        if (context == null) {
            return;
        }
        final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        Activity activity = null;
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else if (context instanceof ContextWrapper) {
            Context parentContext = ((ContextWrapper) context).getBaseContext();
            if (parentContext instanceof Activity) {
                activity = (Activity) parentContext;
            }
        }

        if (activity == null) {
            Log.w(TAG, "Try to show keyboard but context type is incorrect " + context.getClass().getSimpleName());
            return;
        }

        final View currentFocusView = activity.getCurrentFocus();
        if (currentFocusView != null) {
            currentFocusView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    inputMethodManager.showSoftInput(currentFocusView, 0);
                }
            }, 100);
        } else {
            Log.w(TAG, "Try to show keyboard but there is no current focus view");
        }
    }

    public static void tintViewGroupIcons(ViewGroup viewGroup, int color) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) != null && viewGroup.getChildAt(i) instanceof ViewGroup) {
                tintViewGroupIcons((ViewGroup) viewGroup.getChildAt(i), color);
            } else if (viewGroup.getChildAt(i) != null && viewGroup.getChildAt(i) instanceof ImageView) {
                ImageView imageView = (ImageView) viewGroup.getChildAt(i);
                imageView.setImageDrawable(tintDrawable(imageView.getDrawable(), color));
            }
        }
    }

    public static void tintMenuIcons(@NonNull Menu menu, int color) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            Drawable normalDrawable = menuItem.getIcon();
            menuItem.setIcon(tintDrawable(normalDrawable, color));
        }
    }

    public static Drawable tintDrawable(Drawable normalDrawable, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrappedDrawable.mutate(), color);
        return DrawableCompat.unwrap(wrappedDrawable);
    }

    public static void tintProgressDrawable(@ColorInt int colour, ProgressBar progressBar) {
        if (progressBar.getIndeterminateDrawable() != null) {
            progressBar.setIndeterminateDrawable(tintDrawable(progressBar.getIndeterminateDrawable(), colour));
        }
    }

    public static Drawable generateOvalBackgroundDrawable(@ColorInt int colour) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.mutate();
        shape.setColor(colour);
        return shape;
    }

    public static Drawable generateRoundedBackgroundDrawable(@ColorInt int colour, int radius) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{radius, radius, radius, radius, radius, radius, radius, radius});
        shape.setColor(colour);
        shape.mutate();
        return shape;
    }

    public static Drawable generateStrokedBackgroundDrawable(@ColorInt int colour, int strokeSize, int radius) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setStroke(strokeSize, colour);
        shape.mutate();
        shape.setCornerRadii(new float[]{radius, radius, radius, radius, radius, radius, radius, radius});
        shape.setColor(Color.TRANSPARENT);
        return shape;
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Get the status bar height set in the internal android configuration.
     *
     * @param context context for resources
     * @return status bar height or 0 if it isn't set
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Get the display height in pixels
     *
     * @param activityContext context for getting windowManage
     * @return status bar height or 0 if it isn't set
     */
    public static int getDisplayHeightInPx(Activity activityContext) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activityContext.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static void exitImmersiveMode(Window window) {
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    public static void enterImmersiveMode(Window window) {
        int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= 19) {
            visibility = visibility | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        window.getDecorView().setSystemUiVisibility(visibility);
    }

    public static void setFullscreen(boolean fullscreen, Window window) {
        if (window == null) {
            return;
        }
        final View view = window.getDecorView();
        if (view == null) {
            return;
        }

        if (!fullscreen) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    public static boolean hideKeyboard(@NonNull Window window) {
        View view = window.getCurrentFocus();
        return hideKeyboard(window, view);
    }

    private static boolean hideKeyboard(@NonNull Window window, @Nullable View view) {
        if (view == null) {
            return false;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) window.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            return inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        return false;
    }

    /**
     * Find the top fragment in FragmentManager.
     *
     * @param fragmentManager
     * @param fragmentContainerId this has to be the fragment container id in xml
     * @return
     */
    @Nullable
    public static final Fragment findTopFragment(FragmentManager fragmentManager, @IdRes int fragmentContainerId) {
        return fragmentManager.findFragmentById(fragmentContainerId);
    }
}
