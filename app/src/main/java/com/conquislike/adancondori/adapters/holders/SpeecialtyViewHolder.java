package com.conquislike.adancondori.adapters.holders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.conquislike.adancondori.R;
import com.conquislike.adancondori.adapters.SpecialtiesAdapter;
import com.conquislike.adancondori.model.Specialties;

public class SpeecialtyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public static final String TAG = SpeecialtyViewHolder.class.getSimpleName();
    public TextView date_public;
    public ImageView image;
    public TextView title;
    public TextView description;
    public Button download;
    public SpecialtiesAdapter.ItemListener itemListener;

    Specialties item;

    public SpeecialtyViewHolder(@NonNull View v) {
        super(v);
        v.setOnClickListener(this);
        date_public = (TextView) v.findViewById(R.id.date_public);
        image = (ImageView) v.findViewById(R.id.image);
        title = (TextView) v.findViewById(R.id.title);
        description = (TextView) v.findViewById(R.id.description);
        download = (Button) v.findViewById(R.id.download);
    }

    @Override
    public void onClick(View v) {
        if (itemListener != null) {
            itemListener.onItemClick(item);
        }
    }

    public void setData(Specialties item) {
        this.item = item;
        date_public.setText(item.created_at);
        title.setText(item.name);
        description.setText(item.description);
        //imageView.setImageResource(item.drawable);
        //relativeLayout.setBackgroundColor(Color.parseColor(item.color));
    }
}
