package fr.enssat.bozecromain.videoenrichie;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private VideoView myVideoView;
    private ProgressDialog myProgressDialog;
    private MediaController myMediaController;
    private JSONObject jObject;
    private WebView myWebView;
    private MapView mMapView;
    private String MAPVIEW_BUNDLE_KEY;

    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY); }
        mMapView = findViewById(R.id.mapview);
        mMapView.onCreate(mapViewBundle);
        // Initialisation du lecteur de fichier JSON
        try{
            InputStream inputStream = getResources().openRawResource(R.raw.chapters);
            JSONParser myJSONParser = new JSONParser(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = myJSONParser.getByteArrayOutputStream();
            jObject = new JSONObject(byteArrayOutputStream.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        this.videoInitialization();
        this.chaptersInitialization();
        this.webViewInitialization();

        this.initMap();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void videoInitialization(){
        myVideoView = (VideoView) findViewById(R.id.video_view);
        try {
            // Création du mediaController si non existant
            if (myMediaController == null) {
                myMediaController = new MediaController(MainActivity.this);
            }
            // Création & affichage de la barre de progression, affichée pendant le chargement de la vidéo
            myProgressDialog = new ProgressDialog(MainActivity.this);
            JSONObject jObjectFilm = jObject.getJSONObject("Film");
            String title = jObjectFilm.getString("title");
            String url = jObjectFilm.getString("file_url");
            myProgressDialog.setTitle(title);
            myProgressDialog.setMessage("Loadig...");
            myProgressDialog.setCancelable(false);
            myProgressDialog.show();
            // Application du mediaController à la vidéo
            myVideoView.setMediaController(myMediaController);
            // Récupération du chemin vers la vidéo à afficher
            myVideoView.setVideoPath(url);
            myVideoView.requestFocus();
            // Si la vidéo est prête à être afficher, suppression de la barre de progression et lancement
            myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mediaPlayer) {
                    myProgressDialog.dismiss();
                    myVideoView.seekTo(position);
                    if (position == 0) {
                        myVideoView.start();
                    } else {
                        myVideoView.pause();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void chaptersInitialization(){
        LinearLayout myChapters = (LinearLayout)findViewById(R.id.linearLayoutButtons);
        // Création et mise en place des buttons relatifs à chaque chapitre
        try {
            JSONArray jArray = jObject.getJSONArray(getResources().getString(R.string.JSONTitle));
            int cursor = 0;
            String title = "";
            String url = "";
            // Objet nous permettant de recuperer toutes les infos liées à un chapitre
            MetaData myMetaData;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            for (int i = 0; i < jArray.length(); i++) {
                title = jArray.getJSONObject(i).getString(getResources().getString(R.string.ChapterTitle));
                cursor = jArray.getJSONObject(i).getInt("pos");
                myMetaData = new MetaData(title, cursor, url);
                Button button = new Button(this);
                button.setTag(myMetaData);
                button.setText(title);
                button.setLayoutParams(layoutParams);
                button.setOnClickListener(chaptersListener);
                myChapters.addView(button);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void webViewInitialization(){
        try {
            myWebView = (WebView) findViewById(R.id.webview);
            JSONObject jObjectFilm = jObject.getJSONObject("Film");
            String webUrl = jObjectFilm.getString("synopsis_url");
            myWebView.setWebChromeClient(new WebChromeClient());
            myWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return false;
                }
            });
            myWebView.loadUrl(webUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener chaptersListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MetaData rMetaData = (MetaData) v.getTag();
            final int position = rMetaData.getPos() * 1000;
            // final String url = rMetaData.getUrl();
            myVideoView.seekTo(position);
        }
    };

    private void initMap(){
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                try {
                    JSONArray myWaypoints = jObject.getJSONArray("Waypoints");
                    long lat = 0;
                    long lng = 0;
                    String label = "";
                    int timestamp = 0;
                    for (int i = 0; i < myWaypoints.length(); i--) {
                        lat = myWaypoints.getJSONObject(i).getLong("lat");
                        lng = myWaypoints.getJSONObject(i).getLong("lng");
                        label = myWaypoints.getJSONObject(i).getString("label");
                        timestamp = myWaypoints.getJSONObject(i).getInt("timestamp");
                        Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(label));
                        marker.setTag(timestamp);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        int timestamp = (int)marker.getTag();
                        myVideoView.seekTo(timestamp * 1000);
                        return false;
                    } });
            } });
    }
}