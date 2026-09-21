package com.example.makhovyklab1;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements AppContract {

    public static final String TAG = MainActivity.class.getSimpleName();

    private final Map<String, List<ListenerInfo<?>>> listeners = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            launchFragment(null, new MenuFragment());
        }
    }

    @Override
    public void toOptionsScreen(Fragment target, @Nullable Student student) {
        launchFragment(target, OptionsFragment.newInstance(student));
    }

    @Override
    public void toResultsScreen(Fragment target, Student student) {
        launchFragment(target, ResultsFragment.newInstance(student));
    }

    @Override
    public void cancel() {
        int count = getSupportFragmentManager()
                .getBackStackEntryCount();

        if (count <= 1) {
            finish();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public <T> void publish(T results) {

        Fragment currentFragment = getCurrentFragment();

        if (currentFragment == null) {
            Log.e(TAG, "Can't find the current fragment");
            return;
        }

        Fragment targetFragment = currentFragment.getTargetFragment();

        if (targetFragment == null) {
            Log.e(
                    TAG,
                    "Fragment " + currentFragment + " doesn't have a target"
            );
            return;
        }

        String tag = targetFragment.getTag();

        if (tag == null) {
            Log.e(
                    TAG,
                    "Target fragment exists but doesn't have a tag: "
                            + targetFragment
            );
            return;
        }

        List<ListenerInfo<?>> listenerList = listeners.get(tag);

        if (listenerList != null) {
            Iterator<ListenerInfo<?>> iterator = listenerList.iterator();

            while (iterator.hasNext()
                    && !iterator.next().tryPublish(results)) {
                // continue
            }
        }
    }

    @Override
    public <T> void registerListener(
            Fragment fragment,
            Class<T> clazz,
            ResponseListener<T> listener
    ) {

        if (fragment.getTag() == null) {
            Log.e(
                    TAG,
                    "Fragment '" + fragment + "' doesn't have a tag"
            );
            return;
        }

        List<ListenerInfo<?>> listenerList =
                listeners.get(fragment.getTag());

        if (listenerList == null) {
            listenerList = new ArrayList<>();
            listeners.put(fragment.getTag(), listenerList);
        }

        listenerList.add(new ListenerInfo<>(clazz, listener));
    }

    @Override
    public void unregisterListeners(Fragment fragment) {

        if (fragment.getTag() == null) {
            Log.e(
                    TAG,
                    "Fragment '" + fragment + "' doesn't have a tag"
            );
            return;
        }

        listeners.remove(fragment.getTag());
    }

    private void launchFragment(
            @Nullable Fragment target,
            Fragment fragment
    ) {

        if (target != null) {
            fragment.setTargetFragment(target, 0);
        }

        String tag = UUID.randomUUID().toString();

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragmentContainer, fragment, tag)
                .commit();
    }

    @Nullable
    private Fragment getCurrentFragment() {
        return getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);
    }

    private static class ListenerInfo<T> {

        private final Class<T> clazz;
        private final ResponseListener<T> listener;

        private ListenerInfo(
                Class<T> clazz,
                ResponseListener<T> listener
        ) {
            this.clazz = clazz;
            this.listener = listener;
        }

        boolean tryPublish(Object result) {

            if (result != null && result.getClass().equals(clazz)) {
                listener.onResults(clazz.cast(result));
                return true;
            }

            return false;
        }
    }
}