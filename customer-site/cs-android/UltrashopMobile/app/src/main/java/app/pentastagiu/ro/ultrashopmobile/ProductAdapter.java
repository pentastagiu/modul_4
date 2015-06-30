package app.pentastagiu.ro.ultrashopmobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Razvan on 19/06/2015.
 * Class for adapting the products grid view.
 */
public class ProductAdapter extends ArrayAdapter<Product> {

    private List<Product> productList;
    private Context context;
    private static Toast mToast;

    public ProductAdapter(List<Product> productList, Context context) {
        super(context, R.layout.row_layout, productList);
        this.productList = productList;
        this.context = context;
    }

    public int getSize() {
        return productList.size();
    }

    public Product getProduct(int position) {
        return productList.get(position);
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View view = convertView;
        ProductHolder holder = new ProductHolder();

        //first verify if convertView is not null
        if (convertView == null) {
            //this is a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_layout, null);
            // now we can fill the layout with the right values
            TextView productNameView = (TextView) view.findViewById(R.id.name);
            TextView productPriceView = (TextView) view.findViewById(R.id.price);
            ImageView imageView = (ImageView) view.findViewById(R.id.imgProduct);
            Button btnAddToCart = (Button) view.findViewById(R.id.btnAddToCart);

            holder.productNameView = productNameView;
            holder.priceView = productPriceView;
            holder.imageView = imageView;
            holder.btnAddToCart = btnAddToCart;

            view.setTag(holder);
        } else {
            holder = (ProductHolder) view.getTag();
        }

        Product product = productList.get(position);
        // Go to product info activity on image click
        holder.imageView.setTag(product.getId());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductInfo.class);
                intent.putExtra("id", v.getTag().toString());
                context.startActivity(intent);
            }
        });

        holder.btnAddToCart.setTag(product.getId());
        holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Cart tag = " + v.getTag().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.productNameView.setText(product.getName());
        holder.priceView.setText("" + product.getPrice());
        holder.productId = product.getId();
        new DownloadImageTask(holder.imageView).execute("http://192.168.108.218:90/images/" + product.getId() + "/1.jpg");
        return view;
    }

    public void swapProductList(List<Product> products) {
        clear();

        for (Product object : products) {
            add(object);
        }

        notifyDataSetChanged();
    }

    /* *********************************
     * We use the holder pattern
	 * It makes the view faster and avoid finding the component
	 * **********************************/
    protected static class ProductHolder {
        public Button btnAddToCart;
        public ImageView imageView;
        public TextView productNameView;
        public TextView priceView;
        public Integer productId;
    }
}
