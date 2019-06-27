package com.conquislike.adancondori.main.specialties;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.conquislike.adancondori.R;
import com.conquislike.adancondori.adapters.SpecialtiesAdapter;
import com.conquislike.adancondori.main.base.BaseActivity;
import com.conquislike.adancondori.model.Specialties;

import java.util.ArrayList;

public class SpecialtyActivity extends BaseActivity<SpecialtyView, SpecialtyPresenter> implements SpecialtyView, SpecialtiesAdapter.ItemListener {

    private RecyclerView recyclerView;
    private SpecialtiesAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<Specialties> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialty);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new SpecialtiesAdapter(getApplicationContext(), items, this);
        recyclerView.setAdapter(mAdapter);
    }

    @NonNull
    @Override
    public SpecialtyPresenter createPresenter() {
        if (presenter == null) {
            return new SpecialtyPresenter(this);
        }
        return presenter;
    }

    @Override
    public void onItemClick(Specialties item) {

    }

    @Override
    public void setName(String username) {

    }

    @Override
    public String getNameText() {
        return null;
    }

    @Override
    public void setNameError(String string) {

    }
}
