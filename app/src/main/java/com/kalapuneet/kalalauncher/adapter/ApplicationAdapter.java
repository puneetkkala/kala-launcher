package com.kalapuneet.kalalauncher.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kalapuneet.kalalauncher.MainActivity;
import com.kalapuneet.kalalauncher.R;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by puneetkkala on 21/01/17.
 */

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {

    private TreeMap<String,ActivityInfo> applicationInfos;
    private Activity activity;
    private ArrayList<String> keySet;
    private PackageManager pm;

    public ApplicationAdapter(Activity activity, TreeMap<String,ActivityInfo> applicationInfos, PackageManager pm) {
        this.activity = activity;
        this.applicationInfos = applicationInfos;
        keySet = new ArrayList<>(applicationInfos.keySet());
        this.pm = pm;
    }

    @Override
    public ApplicationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_row,parent,false);
        return new ApplicationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ApplicationViewHolder holder, int position) {
        if(position > -1 && position < getItemCount()) {
            ActivityInfo applicationInfo = applicationInfos.get(keySet.get(position));
            holder.appName.setText(applicationInfo.loadLabel(pm).toString());
            holder.appImage.setImageDrawable(applicationInfo.loadIcon(pm));
        }
    }

    @Override
    public int getItemCount() {
        return applicationInfos.size();
    }

    public class ApplicationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView appName;
        public ImageView appImage;

        public ApplicationViewHolder(View itemView) {
            super(itemView);
            appName = (TextView) itemView.findViewById(R.id.app_name);
            appImage = (ImageView) itemView.findViewById(R.id.app_image);
            appName.setOnClickListener(this);
            appImage.setOnClickListener(this);
            appName.setOnLongClickListener(this);
            appImage.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getPosition();
            if(position > -1 && position < getItemCount()) {
                String key = keySet.get(position);
                ActivityInfo applicationInfo = applicationInfos.get(key);
                try {
                    Intent intent = pm.getLaunchIntentForPackage(applicationInfo.packageName);
                    if (intent != null) {
                        activity.startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public boolean onLongClick(View view) {
            int position = getPosition();
            if(position > -1 && position < getItemCount()) {
                final String key = keySet.get(position);
                final ActivityInfo applicationInfo = applicationInfos.get(key);
                AlertDialog alertDialog = new AlertDialog.Builder(activity)
                        .setTitle("Uninstall App")
                        .setMessage("Are you sure, you want to uninstall " + applicationInfo.loadLabel(pm).toString())
                        .setPositiveButton("Uninstall", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Uri packageUri = Uri.parse("package:" + applicationInfo.packageName);
                                Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);
                                activity.startActivity(uninstallIntent);
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create();
                alertDialog.setCancelable(true);
                alertDialog.show();
            }
            return true;
        }
    }
}
