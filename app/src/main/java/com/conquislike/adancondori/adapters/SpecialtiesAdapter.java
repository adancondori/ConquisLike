package com.conquislike.adancondori.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.conquislike.adancondori.R;
import com.conquislike.adancondori.adapters.holders.SpeecialtyViewHolder;
import com.conquislike.adancondori.model.Specialties;

import java.util.ArrayList;

public class SpecialtiesAdapter extends RecyclerView.Adapter<SpeecialtyViewHolder> {
    ArrayList<Specialties> mValues;
    Context mContext;
    protected ItemListener mListener;

    public SpecialtiesAdapter(Context context, ArrayList values, ItemListener itemListener) {
        mValues = values;
        mContext = context;
        mListener=itemListener;
    }

    @NonNull
    @Override
    public SpeecialtyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_grid_speciality, parent, false);

        return new SpeecialtyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpeecialtyViewHolder speecialtyViewHolder, int position) {
        speecialtyViewHolder.setData(mValues.get(position));
        speecialtyViewHolder.itemListener = mListener;
        ImageView image = (ImageView)speecialtyViewHolder.image;
        Glide.with(mContext)
                .load("http://www.conquislike.com/" + mValues.get(position).imagename)
                .into(image);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public interface ItemListener {
        void onItemClick(Specialties item);
    }
}
