package com.qkvpn.vpn.Choose_Server;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qkvpn.vpn.DataManager;
import com.qkvpn.vpn.MainActivity;
import com.qkvpn.vpn.R;
import com.qkvpn.vpn.api_response_model;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.MODE_PRIVATE;

public class Choose_Server_Adapter extends RecyclerView.Adapter<Choose_Server_Adapter_ViewHolder> {

    public Context mContext;
    public ArrayList<api_response_model> modelArrayList;
    SharedPreferences choose_Server_preference;
    private api_response_model apiResponseModel;


    public Choose_Server_Adapter(Context mContext, ArrayList<api_response_model> mFlowerList) {

        this.mContext = mContext;
        this.modelArrayList = mFlowerList;
        if (mContext!=null)
        {
            choose_Server_preference = mContext.getSharedPreferences("DATA", MODE_PRIVATE);
        }
    }


    @NonNull
    @Override
    public Choose_Server_Adapter_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.server_item, parent, false);
        return new Choose_Server_Adapter_ViewHolder(mView);
    }


    @SuppressLint({"ObsoleteSdkInt", "SetTextI18n"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final Choose_Server_Adapter_ViewHolder holder, final int position) {

        apiResponseModel = modelArrayList.get(position);
        holder.textView_parent.setText(apiResponseModel.getHostName());
        holder.countryImage.setImageResource(mContext.getResources().getIdentifier(apiResponseModel.getFlag(), "drawable", mContext.getPackageName()));
        if (apiResponseModel.getType() == 2) {
            holder.tvVIP.setVisibility(View.VISIBLE);
        } else {
            holder.tvVIP.setVisibility(View.GONE);
        }



        if (apiResponseModel.getType() == 2) {
            holder.parent_items.setOnClickListener(v -> {
            if(DataManager.ADMOB_ENABLE) {
                MainActivity.LocationDialogSub.show();
                ((Activity) mContext).finish();


            }else {

                    MainActivity.is_run = true;
                    ((Activity) mContext).finish();
                    Intent intent = new Intent("server_ovpn");
                    intent.setPackage(mContext.getPackageName());
                    intent.putExtra("ValueId_ovpn", position);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    if (choose_Server_preference != null) {
                        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = choose_Server_preference.edit();
                        if (editor != null) {
                            editor.remove("host_name_sp").remove("flag_sp").apply();
                            editor.putString("host_name_sp", "" + apiResponseModel.getHostName()).putString("flag_sp", "" + apiResponseModel.getFlag())
                                    .apply();
                        }
                    }
                    Log.d("counryaaaa" , ""+apiResponseModel.getHostName()  + ""+position);

            }
            });
        } else {

            holder.parent_items.setOnClickListener(v -> {
                Log.d("Name_FLAG" , "" +modelArrayList.get(position).getHostName()  + "  " +  modelArrayList.get(position).getFlag());
                MainActivity.is_run = true;
                ((Activity) mContext).finish();
                Intent intent = new Intent("server_ovpn");
                intent.setPackage(mContext.getPackageName());
                intent.putExtra("ValueId_ovpn", position);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                if (choose_Server_preference != null) {
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = choose_Server_preference.edit();
                    if (editor != null) {
                        editor.remove("host_name_sp").remove("flag_sp").apply();
                        editor.putString("host_name_sp", "" + modelArrayList.get(position).getHostName()).putString("flag_sp", "" + modelArrayList.get(position).getFlag())
                                .apply();
                    }
                }
            });
        }


    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }


    @Override
    public long getItemId(int position) {

        return position;
    }


    @Override
    public int getItemCount() {

        return modelArrayList.size();
    }
}

class Choose_Server_Adapter_ViewHolder extends RecyclerView.ViewHolder  {

    public TextView textView_parent;
    ImageView countryImage,tvVIP;
    LinearLayout parent_items;

    Choose_Server_Adapter_ViewHolder(View itemView) {

        super(itemView);

        textView_parent = itemView.findViewById(R.id.countryName);
        countryImage = itemView.findViewById(R.id.countryImage);
        parent_items = itemView.findViewById(R.id.parent_items);
        tvVIP = itemView.findViewById(R.id.free_iv);
    }

}