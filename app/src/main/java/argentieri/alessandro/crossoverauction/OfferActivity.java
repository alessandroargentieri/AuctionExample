package argentieri.alessandro.crossoverauction;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OfferActivity extends AppCompatActivity {

    String email ="";
    String name_product="";
    String uri_photo="";
    String seller="";
    String cathegory="";
    int lastoffer=0;
    String expirationdate="";
    int id_product;

    float offer_dollars;


    TextView Product;
    TextView Cathegory;
    TextView Seller;
    TextView LastOffer;
    TextView ExpirationDate;
    EditText MyOffer;
    ImageView PicOfProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            Bundle b = getIntent().getExtras();
            email = b.getString("email");
            name_product = b.getString("name");
            uri_photo = b.getString("uri");
            seller = b.getString("seller");
            cathegory = b.getString("cathegory");
            lastoffer = b.getInt("offer");
            expirationdate = b.getString("expiration_date");
            id_product = b.getInt("id_item");

          //  Toast.makeText(getApplicationContext(), "e-mail: " + email, Toast.LENGTH_LONG).show();
        }catch(Exception e){
            Log.d("BUNDLE","No email received!");
        }

        setContentView(R.layout.activity_offer);

        Product = (TextView)findViewById(R.id.productText);
        Cathegory = (TextView)findViewById(R.id.cathegoryText);
        Seller = (TextView)findViewById(R.id.sellerText);
        LastOffer = (TextView)findViewById(R.id.lastofferText);
        MyOffer = (EditText)findViewById(R.id.myofferEdit);
        ExpirationDate = (TextView) findViewById(R.id.expirationText);
        PicOfProduct = (ImageView)findViewById(R.id.picImage);

        offer_dollars = (float) lastoffer/100;

        Product.setText(name_product);
        Cathegory.setText(cathegory);
        Seller.setText(seller);
        LastOffer.setText(offer_dollars + " $");
        ExpirationDate.setText(expirationdate);

        Bitmap photo = new ImageSaver(getApplicationContext()).setFileName(uri_photo).setDirectoryName("images").load();
        PicOfProduct.setImageBitmap(photo);

    }

    public void Offer(View v){
        String myoffer_string = MyOffer.getText().toString();
       // Toast.makeText(getApplicationContext(), myoffer_string, Toast.LENGTH_LONG).show();
        String offCentString ="";
        try {
            Double off = Double.parseDouble(myoffer_string) * 100;

            if(off>lastoffer){
                //valid offer
                String off_string = off + "";
                offCentString = off_string.split("\\.")[0];
                //connect to DB and save the offer
                final SQLiteDatabase db = openOrCreateDatabase("AuctionDB", MODE_PRIVATE, null);

                String selectQuery = "SELECT * FROM offers WHERE fk_user='"+email+"' AND fk_item=" + id_product;
                Cursor c = db.rawQuery(selectQuery, null);
                if(c.moveToFirst()){
                    String updateQuery = "UPDATE offers SET offer=" + offCentString + " WHERE fk_user = '" + email + "' AND fk_item = " + id_product;
                    Log.d("INSERTQUERY", updateQuery);
                    db.execSQL(updateQuery);
                }else{
                    //no offers for this product
                    String insertQuery = "INSERT INTO offers (offer, fk_user, fk_item) VALUES (" + offCentString + ", '" + email + "', " + id_product + ")";
                    Log.d("INSERTQUERY", insertQuery);
                    db.execSQL(insertQuery);
                }

                //come back to ViewActivity
                Intent intent = new Intent(OfferActivity.this, ViewActivity.class);
                Bundle b = new Bundle();
                b.putString("email", email);
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }else{
                //invalid offer
                Toast.makeText(getApplicationContext(), "Put a valid offer value!", Toast.LENGTH_LONG).show();
            }
         }catch(Exception e){
            Toast.makeText(getApplicationContext(), "Put a valid offer value!", Toast.LENGTH_LONG).show();
         }


    }
}
