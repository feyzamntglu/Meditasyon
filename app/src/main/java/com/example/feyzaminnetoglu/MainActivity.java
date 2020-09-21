package com.example.feyzaminnetoglu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawer;
    Button menuButton;
    ListViewAdapter adapter;

    String url= "http://mistikyol.com/mistikmobil/mobiljson.php";
    RequestQueue queue;
    SQLiteHandler veritabanı;

    ListView listView;
    String[] adlar= new String[]{
            "Son Eklenenler",
            "Favorilerim",
            "İyi Bir Uyku",
            "Kişisel Gelişim",
            "Mistik İşler",
            "Olumlamalar",
            "Motivasyon",
            "Çakra Bilgileri",
            "Çekim Yasası Bilgileri",
            "Astroloji",
    };

    String[] linkler=new String[]{
            "0",
            "00",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue= NetworkController.getInstance(this).getRequestQueue();
        queue.add(new JsonObjectRequest(0, url, null, new listener(), new error()));

        veritabanı=new SQLiteHandler(getApplicationContext());

        drawer=findViewById(R.id.drawer_layout);
        menuButton=findViewById(R.id.menuBtn);
        listView= findViewById(R.id.left_drawer_child);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {


            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

                menuButton.setBackgroundResource(R.drawable.menus);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                menuButton.setBackgroundResource((R.drawable.menu));

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        adapter=new ListViewAdapter(this, adlar, linkler);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                drawer.closeDrawer(GravityCompat.START);//kapanma şekli oluşturuldu
                Toast.makeText(MainActivity.this, linkler[position] + " Numaralı menü öğesine tıklandınız", Toast.LENGTH_SHORT).show();
            }
        });
        //menü butonuna basınca açılması
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //drawer açıksa kapansın, kapalı ise açılsın

                if(drawer.isDrawerOpen(GravityCompat.START)){
                    drawer.closeDrawer(GravityCompat.START);
                }else{
                    drawer.openDrawer(GravityCompat.START);
                }

            }
        });
    }
    private class listener implements Response.Listener<JSONObject>{
        public void onResponse(JSONObject response){
            
            try {
                JSONArray meditasyonlar= response.getJSONArray("meditasyonlar");
                int length= meditasyonlar.length();
                for (int i=0; i < length; i++){
                    try {

                        JSONObject meditasyon= meditasyonlar.getJSONObject(i);
                        Cursor kayitlar = veritabanı.getWritableDatabase().rawQuery(" SELECT count(*) FROM veriler WHERE anahtar = '"
                                + meditasyon.getString("id") + "'" , null);
                        kayitlar.moveToFirst();
                        int sayi= kayitlar.getInt(0);

                        if(sayi==0){
                            veritabanı.veriEkle(meditasyon.getString("baslik"), meditasyon.getString("aciklama"),
                                    meditasyon.getString("thumbnail"), meditasyon.getString("sesdosyasi"),
                                    meditasyon.getString("tarih"), meditasyon.getString("kategori"),
                                    meditasyon.getString("id"));
                        }


                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, "Hata Oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                
            }catch (Exception e){
                Toast.makeText(MainActivity.this, "Hata Oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
            
            Log.i("Gelen Cevap", response.toString());
        }
    }
    private class error implements Response.ErrorListener{
        public  void onErrorResponse(VolleyError error){
            Toast.makeText(MainActivity.this, "Hata Oluştu:" +error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

        }
    }
}
