package com.conquislike.adancondori.main.specialties;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.conquislike.adancondori.R;
import com.conquislike.adancondori.adapters.SpecialtiesAdapter;
import com.conquislike.adancondori.main.base.BaseActivity;
import com.conquislike.adancondori.main.followPosts.FollowingPostsActivity;
import com.conquislike.adancondori.main.main.MainActivity;
import com.conquislike.adancondori.main.search.SearchActivity;
import com.conquislike.adancondori.model.Specialties;

import java.util.ArrayList;

public class SpecialtyActivity extends BaseActivity<SpecialtyView, SpecialtyPresenter> implements SpecialtyView, SpecialtiesAdapter.ItemListener {

    private RecyclerView recyclerView;
    private SpecialtiesAdapter mAdapter;
    private GridLayoutManager layoutManager;
    ArrayList<Specialties> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialty);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        generateSpeciaties();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new GridLayoutManager(this, 2);//new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new SpecialtiesAdapter(getApplicationContext(), items, this);
        //recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        recyclerView.setAdapter(mAdapter);
    }

    public void generateSpeciaties(){
        Specialties specialties = new Specialties();
        specialties.user_id = new Long(1);
        specialties.name = "Filigrana";
        specialties.description = "AM 023 - Especialidad de Santuario, perteneciente al Área de Actividades misioneras y comunitarias.";
        specialties.imagename = "/image/upload/specialties/4saAEQNhpmyqNY8XdRZ9D90QqLVW0BbZ9sBN3oGe.png";
        items.add(specialties);
        specialties = new Specialties();
        specialties.user_id = new Long(1);
        specialties.name = "Santuario";
        specialties.description = "AM 023 - Especialidad de Santuario, perteneciente al Área de Actividades misioneras y comunitarias.";
        specialties.imagename = "/image/upload/specialties/4saAEQNhpmyqNY8XdRZ9D90QqLVW0BbZ9sBN3oGe.png";
        items.add(specialties);
        specialties = new Specialties();
        specialties.user_id = new Long(1);
        specialties.name = "3 Hola Como";
        specialties.description = "Descripccion ok";
        specialties.imagename = "/image/upload/specialties/4saAEQNhpmyqNY8XdRZ9D90QqLVW0BbZ9sBN3oGe.png";
        items.add(specialties);
        specialties = new Specialties();
        specialties.user_id = new Long(1);
        specialties.name = "4 Hola Como";
        specialties.description = "Descripccion ok";
        specialties.imagename = "/image/upload/specialties/4saAEQNhpmyqNY8XdRZ9D90QqLVW0BbZ9sBN3oGe.png";
        items.add(specialties);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.profile:
                //presenter.onProfileMenuActionClicked();
                return true;

            case R.id.followingPosts:
                Intent followingPosts = new Intent(this, MainActivity.class);
                startActivity(followingPosts);
                return true;

            case R.id.search:
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
