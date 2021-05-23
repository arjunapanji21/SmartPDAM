package com.rozikmaliki.smartpdam;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ListAdapter extends ArrayAdapter {
    private Activity mContext;
    List<DataAir> dataAirList;

    public ListAdapter(Activity mContext, List<DataAir> dataAirList){
        super(mContext,R.layout.list_data,dataAirList);
        this.mContext = mContext;
        this.dataAirList = dataAirList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        View listItemView = inflater.inflate(R.layout.list_data, null, true);

        TextView txtBulan = listItemView.findViewById(R.id.txtBulan);
        TextView txtAir = listItemView.findViewById(R.id.txtAir);
        TextView txtBiaya = listItemView.findViewById(R.id.txtBiaya);

        DataAir dataAir = dataAirList.get(position);

        txtBulan.setText(dataAir.getBulan());
        txtAir.setText(dataAir.getAir()+" L");
        txtBiaya.setText("Rp. "+dataAir.getBiaya());
        return listItemView;
    }
}
