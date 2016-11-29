package com.ucsc.pnp.bustrack;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private GoogleMap googlemap;
    private Marker busmarker;
    AlertReceive myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter("com.ucsc.pnp.bustrack.CUSTOM");

        myReceiver = new AlertReceive();
        registerReceiver(myReceiver, filter);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        googlemap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        startService(new Intent(this, NetService.class));

    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class AlertReceive extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String msg=intent.getStringExtra("data");
            Log.e("Service", msg);
            try {
                JSONObject obj = new JSONObject(msg);
                //JSONArray objarr=obj.getJSONArray("alert");
                JSONObject objarr = null;
                objarr = new JSONObject(obj.getString("overwaiting"));

                //if(objarr.length()>0){
                   // for(int i=0;i<objarr.length();i++){
                        JSONObject tempobj= (JSONObject) objarr;//.get(i);
                        //String alertType=tempobj.getString("alerttype");
                        if(tempobj == null){//alertType.equals("Overspeed")){
                            objarr = new JSONObject(obj.getString("overspeed"));
                            tempobj= (JSONObject) objarr;
                            showDialog("OverSpeed :- "+tempobj.getDouble("maxspeed")+" Kmph ");
                        }
                        else {//if (alertType.equals("Over waiting")){
                            JSONObject tempobjpos=tempobj.getJSONObject("movedata");

                            LatLng pos=new LatLng(tempobjpos.getDouble("lat"),tempobjpos.getDouble("lon"));

                            showDialog("Over waiting :- " + tempobj.getString("regno")+" ");
                            if(busmarker==null) {
                                busmarker = googlemap.addMarker(new MarkerOptions().position(pos));
                                busmarker.setPosition(pos);
                                Log.e("TAG","In null");

                            }
                            //else{
                                //busmarker.setPosition(pos);

                            //}

                       // }


                    //}
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        public void showDialog(String alertmsg) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setMessage(alertmsg);

            builder.setPositiveButton("DONE ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            builder.show();
        }

    }
}
