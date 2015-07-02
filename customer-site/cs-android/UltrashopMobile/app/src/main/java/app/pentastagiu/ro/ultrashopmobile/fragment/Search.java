package app.pentastagiu.ro.ultrashopmobile.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import app.pentastagiu.ro.ultrashopmobile.R;
import app.pentastagiu.ro.ultrashopmobile.adapter.ProductAdapter;
import app.pentastagiu.ro.ultrashopmobile.model.Product;

/**
 * Created by deni on 7/1/2015.
 */
public class Search extends Fragment {

    private List<Product> productList = new ArrayList<Product>();
    private ProductAdapter productAdapter;

    public Search() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rooView = inflater.inflate(R.layout.fragment_search, container, false);
        //all products are listed
        new SearchProducts(getActivity()).execute("http://192.168.108.131:8080/ultrashop/ws/products");
        return rooView;
    }
/**
 *
 */
    /**
     * Async task class to get json objects by making HTTP calls
     */
    private class SearchProducts extends AsyncTask<String, Void, Void> {
        Activity thisContext;

        public SearchProducts(Activity context) {
            thisContext = context;
        }

        @Override
        protected Void doInBackground(String... args) {
            try {
                JSONArray url = getJSONfromURL(args[0]);
                if (url != null) {
                    productList = parseFromJson(url);
                }
            } catch (JSONException jsonExc) {
                Log.d("JSONError", "ExceptionJSON: " + jsonExc.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            GridView gridView = (GridView) thisContext.findViewById(R.id.gridView);
            productAdapter = new ProductAdapter(productList, thisContext);
            gridView.setAdapter(productAdapter);
        }
        public JSONArray getJSONfromURL(String url) {
            InputStream is = null;
            String result = "";
            JSONArray jArray = null;
            if (isOnline()) {
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpGet httppget = new HttpGet(url);
                    httppget.setHeader("Content-Type", "application/JSON");
                    httppget.setHeader("Accept", "application/JSON");
                    HttpResponse response = httpclient.execute(httppget);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                } catch (Exception e) {
                    Log.e("log_tag", "Error in http connection " + e.getStackTrace().toString());
                    Log.e("exc", "http", e);
                }
                //convert response to string
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();
                } catch (Exception e) {
                    Log.d("ErrorConvert", "Error while converting result: " + e.getMessage());
                }
                try {
                    jArray = new JSONArray(result);
                } catch (JSONException e) {
                    Log.d("jsonObj", "Error parsing data: " + e.getMessage());
                }
            } else {
                productList = new ArrayList<Product>();
                Product product = new Product(0, "No internet connection !", null);
                productList.add(product);
            }
            return jArray;
        }
        private List<Product> parseFromJson(JSONArray jsonObjects) throws JSONException {
            ArrayList<Product> products = new ArrayList<Product>();

            for (int i = 0; i < jsonObjects.length(); i++) {
                JSONObject jsonProduct = jsonObjects.getJSONObject(i);
                Integer id = jsonProduct.getInt("id");
                String name = jsonProduct.getString("name");
                Double price = jsonProduct.getDouble("price");

                Product product = new Product(id, name, price);
                products.add(product);
            }
            return products;
        }

        // check internet connection
        public Boolean isOnline() {
            try {
                Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.ro");
                int returnVal = p1.waitFor();
                boolean reachable = (returnVal == 0);
                return reachable;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return false;
        }
    }
}
