package com.example.mynewsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mynewsapp.parameter.Articles;
import com.example.mynewsapp.parameter.Headlines;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
{
    private InterstitialAd mInterstitialAd;
    Context context;
    RecyclerView recyclerView;
    Adapter adapter;
    final String API_KEY="b06807bbce7e40ea8d0ab0da9bc91847";
    Button button;
    ImageButton floatingActionButton;
    List<Articles> articles=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        button=findViewById(R.id.refreshButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final String country=getCountry();
        fetchJSON(country,API_KEY);
        MobileAds.initialize(this);

        MobileAds.initialize(this, new OnInitializationCompleteListener()
        {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }

        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
               // Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                //Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback()
        {
            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed.
                Log.d("TAG", "The ad was dismissed.");
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when fullscreen content failed to show.
                Log.d("TAG", "The ad failed to show.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
                // Make sure to set your reference to null so you don't
                // show it a second time.
                mInterstitialAd = null;
                Log.d("TAG", "The ad was shown.");
            }

        });

        //mInterstitialAd =new InterstitialAd(context:this);
        //mInterstitialAd.setAdUnitId

        //floatingActionButton=(ImageButton)findViewById(R.id.floating);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(MainActivity.this,Intro.class);
                startActivity(intent);

            }
        });
    }

    private void fetchJSON(String country, String api_key)
    {
        Call<Headlines> call=Client.getInstance().getApi().getHeadlines(country,API_KEY);
        call.enqueue(new Callback<Headlines>() {
            @Override
            public void onResponse(Call<Headlines> call, Response<Headlines> response) {
                if (response.isSuccessful() && response.body()!=null)
                {
                    articles.clear();
                    articles=response.body().getArticles();
                    //adapter =new Adapter(MainActivity.this, articles);
                    //recyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onFailure(Call<Headlines> call, Throwable t)
            {
                Toast.makeText(MainActivity.this,"There is An Error",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getCountry()
    {
        Locale locale=Locale.getDefault();
        String country=locale.getCountry();
        return country.toLowerCase();
    }


}
