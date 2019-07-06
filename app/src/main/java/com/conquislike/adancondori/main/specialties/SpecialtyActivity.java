package com.conquislike.adancondori.main.specialties;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.conquislike.adancondori.Constants;
import com.conquislike.adancondori.R;
import com.conquislike.adancondori.adapters.SpecialtiesAdapter;
import com.conquislike.adancondori.main.base.BaseActivity;
import com.conquislike.adancondori.main.followPosts.FollowingPostsActivity;
import com.conquislike.adancondori.main.main.MainActivity;
import com.conquislike.adancondori.main.rest.Rest;
import com.conquislike.adancondori.main.search.SearchActivity;
import com.conquislike.adancondori.model.Dato;
import com.conquislike.adancondori.model.Specialties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SpecialtyActivity extends BaseActivity<SpecialtyView, SpecialtyPresenter>
        implements SpecialtyView, SpecialtiesAdapter.ItemListener, Callback<Dato>, EasyPermissions.PermissionCallbacks {

    private RecyclerView recyclerView;
    private SpecialtiesAdapter mAdapter;
    private GridLayoutManager layoutManager;
    private Dato dato = new Dato();
    private ArrayList<Specialties> specialties = new ArrayList<>();
    private ProgressBar postsProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialty);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        postsProgressBar = findViewById(R.id.specialtiesProgressBar);
        specialties.clear();

        //generateSpeciaties();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new GridLayoutManager(this, 2);//new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new SpecialtiesAdapter(getApplicationContext(), specialties, this);
        //recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        recyclerView.setAdapter(mAdapter);
    }

//    public void generateSpeciaties(){
//        Specialties specialties = new Specialties();
//        specialties.user_id = new Long(1);
//        specialties.name = "Filigrana";
//        specialties.description = "AM 023 - Especialidad de Santuario, perteneciente al Área de Actividades misioneras y comunitarias.";
//        specialties.imagename = "/image/upload/specialties/4saAEQNhpmyqNY8XdRZ9D90QqLVW0BbZ9sBN3oGe.png";
//        items.add(specialties);
//        specialties = new Specialties();
//        specialties.user_id = new Long(1);
//        specialties.name = "Santuario";
//        specialties.description = "AM 023 - Especialidad de Santuario, perteneciente al Área de Actividades misioneras y comunitarias.";
//        specialties.imagename = "/image/upload/specialties/4saAEQNhpmyqNY8XdRZ9D90QqLVW0BbZ9sBN3oGe.png";
//        items.add(specialties);
//        specialties = new Specialties();
//        specialties.user_id = new Long(1);
//        specialties.name = "3 Hola Como";
//        specialties.description = "Descripccion ok";
//        specialties.imagename = "/image/upload/specialties/4saAEQNhpmyqNY8XdRZ9D90QqLVW0BbZ9sBN3oGe.png";
//        items.add(specialties);
//        specialties = new Specialties();
//        specialties.user_id = new Long(1);
//        specialties.name = "4 Hola Como";
//        specialties.description = "Descripccion ok";
//        specialties.imagename = "/image/upload/specialties/4saAEQNhpmyqNY8XdRZ9D90QqLVW0BbZ9sBN3oGe.png";
//        items.add(specialties);
//    }

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
        new DownloadFile().execute(Constants.BASE_URL + item.filename);
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

    @Override
    protected void onStart() {
        super.onStart();
        sindronizeData();
    }

    public void sindronizeData(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_API)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Rest request = retrofit.create(Rest.class);

        Call<Dato> call = request.loadChanges("status:open");
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<Dato> call, Response<Dato> response) {
        postsProgressBar.setVisibility(View.GONE);
        if(response.isSuccessful()) {
            dato = response.body();
            System.out.println(dato.data.size());
            for (int i = 0; i < dato.data.size(); i++) {
                specialties.addAll(dato.data);
            }
        } else {
            System.out.println(response.errorBody());
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(Call<Dato> call, Throwable t) {
        System.out.println(t.toString());
        Toast.makeText(this, "Error al Descargar Datos", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }


    public class DownloadFile extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private boolean isDownloaded;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(SpecialtyActivity.this);
            this.progressDialog.setTitle("Descargando especialidad");
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();


                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());

                //Append timestamp to file name
                fileName = timestamp + "_" + fileName;

                //External directory path to save file
                folder = Environment.getExternalStorageDirectory() + File.separator + "ConquisLike/";

                //Create androiddeft folder if it does not exist
                File directory = new File(folder);

                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Output stream to write file
                OutputStream output = new FileOutputStream(folder + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    Log.d("ADAN", "Progress: " + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                return "Downloaded at: " + folder + fileName;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return "Something went wrong";
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();

            // Display File path after downloading
            Toast.makeText(SpecialtyActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }
}
