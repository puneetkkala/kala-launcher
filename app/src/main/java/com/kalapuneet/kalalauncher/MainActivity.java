package com.kalapuneet.kalalauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.kalapuneet.kalalauncher.adapter.ApplicationAdapter;

import java.util.List;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity implements TextWatcher {

    public static final String GOOGLE_PLAY_STORE = "Play Store";
    public static final String GOOGLE_APP = "Google";
    public static final String PHONE = "Phone";

    private RecyclerView launcherRv;
    private ApplicationAdapter applicationAdapter;
    private TreeMap<String,ActivityInfo> applicationInfoTree;
    private TreeMap<String,ActivityInfo> displayTree;
    private EditText appNameSearch;
    private PackageManager packageManager;
    private ImageView contactsApp;
    private ImageView messengerApp;
    private ImageView playStoreApp;
    private ImageView mediaPlayerApp;
    List<ResolveInfo> pkgAppsList;

    private void prepareList() {
        displayTree = new TreeMap<>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        pkgAppsList = getPackageManager().queryIntentActivities(intent,0);
        applicationInfoTree = new TreeMap<>();
        for (ResolveInfo resolveInfo: pkgAppsList) {
            applicationInfoTree.put(resolveInfo.loadLabel(packageManager).toString(), resolveInfo.activityInfo);
        }
        displayTree.putAll(applicationInfoTree);
    }

    private ActivityInfo defaultContactsApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_CONTACTS);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent,0);
        return resolveInfo.activityInfo;
    }

    private ActivityInfo defaultSmsApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_MESSAGING);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent,0);
        return resolveInfo.activityInfo;
    }

    private ActivityInfo defaultMediaApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_MUSIC);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent,0);
        return resolveInfo.activityInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        packageManager = getPackageManager();
        launcherRv = (RecyclerView) findViewById(R.id.launcher_rv);
        appNameSearch = (EditText) findViewById(R.id.app_name_search);
        appNameSearch.addTextChangedListener(this);
        contactsApp = (ImageView) findViewById(R.id.contacts_app);
        messengerApp = (ImageView) findViewById(R.id.messenger_app);
        playStoreApp = (ImageView) findViewById(R.id.play_store_app);
        mediaPlayerApp = (ImageView) findViewById(R.id.media_player_app);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int i, int i1, int i2) {
        if (displayTree != null && applicationInfoTree != null) {
            if (s.toString().length() == 0) {
                displayTree = new TreeMap<>(applicationInfoTree);
            } else {
                displayTree = new TreeMap<>();
                for (String name : applicationInfoTree.keySet()) {
                    if (name.toLowerCase().startsWith(s.toString().toLowerCase())) {
                        displayTree.put(name, applicationInfoTree.get(name));
                    }
                }
            }
            applicationAdapter = new ApplicationAdapter(this, displayTree, packageManager);
            launcherRv.setAdapter(applicationAdapter);
            launcherRv.setLayoutManager(new GridLayoutManager(this, 4));
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(packageManager.getLaunchIntentForPackage(applicationInfoTree.get(GOOGLE_APP).packageName));
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: {
                Intent intent = new Intent(packageManager.getLaunchIntentForPackage(applicationInfoTree.get(GOOGLE_APP).packageName));
                startActivity(intent);
                break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareList();
        applicationAdapter = new ApplicationAdapter(this,displayTree,packageManager);
        launcherRv.setAdapter(applicationAdapter);
        launcherRv.setLayoutManager(new GridLayoutManager(this,4));
        contactsApp.setImageDrawable(applicationInfoTree.get(PHONE).loadIcon(packageManager));
        contactsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(packageManager.getLaunchIntentForPackage(defaultContactsApp().packageName));
                startActivity(intent);
            }
        });
        messengerApp.setImageDrawable(defaultSmsApp().loadIcon(packageManager));
        messengerApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(packageManager.getLaunchIntentForPackage(defaultSmsApp().packageName));
                startActivity(intent);
            }
        });
        playStoreApp.setImageDrawable(applicationInfoTree.get(GOOGLE_PLAY_STORE).loadIcon(packageManager));
        playStoreApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(packageManager.getLaunchIntentForPackage(applicationInfoTree.get(GOOGLE_PLAY_STORE).packageName));
                startActivity(intent);
            }
        });
        mediaPlayerApp.setImageDrawable(defaultMediaApp().loadIcon(packageManager));
        mediaPlayerApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(packageManager.getLaunchIntentForPackage(defaultMediaApp().packageName));
                startActivity(intent);
            }
        });
    }
}
