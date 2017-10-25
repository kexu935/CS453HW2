package com.example.kex.cs453hw2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.io.BufferedInputStream;
import java.io.InputStream;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import java.util.ArrayList;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import java.io.File;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by kex on 10/23/17.
 */

public class GetPOI extends ListActivity {
    ArrayList venuesList;
    final String google_key = "AIzaSyBt_vuNffF_aGQbgwIm0Lom4RBgcou5SKE";
    Double latitude;
    Double longitude;
    Double radius;
    ArrayAdapter myAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poi_list);
        Bundle b = getIntent().getExtras();
        latitude = b.getDouble("latitude", 0.0);
        longitude = b.getDouble("longitude", 0.0);
        radius = b.getDouble("radius", 1.0);
        new GooglePlaces().execute();
    }

    private class GooglePlaces extends AsyncTask {
        String temp;

        @Override
        protected Object doInBackground(Object[] objects) {
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=" + radius + "&key=" + google_key + "&sensor=true";
            temp = makeCall(url);
            System.out.println(url);
            return "";
        }

        @Override
        protected void onPreExecute(){
        }

        @Override
        protected void onPostExecute(Object o) {
            if (temp != null){
                venuesList = (ArrayList)parseGoogleParse(temp);
                List listTitle = new ArrayList<>();
                File root = new File(Environment.getExternalStorageDirectory(), "CS453HW2Outputs");
                if (!root.exists()) {
                    root.mkdirs();
                }
                String fileName = latitude +","+longitude + ","+radius;
                File gpxfile = new File(root, fileName);
                try {
                    FileWriter writer = new FileWriter(gpxfile);
                    for (int i = 0; i < venuesList.size(); i++) {
                        GooglePlace cur = (GooglePlace) venuesList.get(i);
                        int index = i + 1;
                        listTitle.add("No." + index + "\n" + cur.getName() + "\nLatitude: " + cur.getLat() + "\nLongitude: " + cur.getLng());
                        writer.write(cur.getName() + "\n" + cur.getLat() + "\n" + cur.getLng() + "\n" + "------------\n");
                    }
                    writer.close();
                    Toast.makeText(GetPOI.this, "Output as file/CS453HW2Outputs/" + fileName, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (listTitle.isEmpty()) {
                    Toast.makeText(GetPOI.this, "Nothing found in this location", Toast.LENGTH_LONG).show();
                } else {
                    myAdapter = new ArrayAdapter(GetPOI.this, R.layout.row_layout, R.id.listText, listTitle);
                    setListAdapter(myAdapter);
                }
            }
        }

        public String makeCall(String url) {
            StringBuffer sb = new StringBuffer(url);
            String reply = "";

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(sb.toString());
            try {
                HttpResponse response = httpclient.execute(httpget);
                InputStream is = response.getEntity().getContent();
                BufferedInputStream bis = new BufferedInputStream(is);
                ByteArrayBuffer baf = new ByteArrayBuffer(20);
                int current = 0;
                while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
                }
                reply = new String(baf.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return reply.trim();
        }

        private ArrayList parseGoogleParse(String response) {
            ArrayList temp = new ArrayList();
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("results")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject cur = jsonArray.getJSONObject(i);
                        GooglePlace poi = new GooglePlace();
                        poi.setName(cur.optString("name"));
                        poi.setLat(cur.getJSONObject("geometry").getJSONObject("location").optString("lat"));
                        poi.setLng(cur.getJSONObject("geometry").getJSONObject("location").optString("lng"));
                        temp.add(poi);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList();
            }
            return temp;
        }
    }
}
