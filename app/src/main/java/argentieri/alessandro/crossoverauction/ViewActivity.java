package argentieri.alessandro.crossoverauction;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


import argentieri.alessandro.crossoverauction.AuctionItem;
import argentieri.alessandro.crossoverauction.ItemAdapter;

public class ViewActivity extends AppCompatActivity{

    SharedPreferences sharedpreferences;
    public String email = "";
    public TextView summary;

    private ListView listView;
    private ItemAdapter adapter;
    private List<AuctionItem> itemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            Bundle b = getIntent().getExtras();
            email = b.getString("email");
        //    Toast.makeText(getApplicationContext(), "e-mail: " + email, Toast.LENGTH_LONG).show();
        }catch(Exception e){
            Log.d("BUNDLE","No email received!");
        }

        setContentView(R.layout.activity_view);

        //toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.mytoolbar);
        //myToolbar.setLogo(R.drawable.logo_splash);
        myToolbar.setTitle(R.string.app_name);
        myToolbar.setSubtitle("User: " + email);
        setSupportActionBar(myToolbar);

        sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

        summary = (TextView)findViewById(R.id.summary);

        //list which will contains all the items requested
        listView = (ListView) findViewById(R.id.list_view);


        itemList = new ArrayList<>();
        adapter = new ItemAdapter(this, itemList, email);
        listView.setAdapter(adapter);

        // gestisco l'evento onClick sulla riga
        listView.setOnItemClickListener(new ListClickHandler()); //aaaa



        viewAllItems();
        //ViewWon();

    } //END OF ON CREATE


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.view_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
               //cancella le preferenze nel logout
               SharedPreferences.Editor editor = sharedpreferences.edit();
               editor.putString("EMAIL_SAVED", "");
               editor.putString("PASSW_SAVED", "");
               editor.putBoolean("REMEMBER", false);
               editor.commit();

               Intent out = new Intent(ViewActivity.this, StartActivity.class);
               startActivity(out);
               finish();
               return true;
            case R.id.search_cate:
              //  Toast.makeText(getApplicationContext(),"Search Cathegory",Toast.LENGTH_LONG).show();
                //appare un dialog con un edittext per la ricerca
                // custom dialog
                final Dialog dialog = new Dialog(ViewActivity.this);
                dialog.setContentView(R.layout.custom_search_dialog);    //il suo layout personalizzato
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                final EditText search = (EditText) dialog.findViewById(R.id.SearchEdit);   //notare il dialog.
                Button dialogButton = (Button) dialog.findViewById(R.id.searchconfirm);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String value = search.getText().toString().toLowerCase();
                        if(!value.equals("")){
                            SearchFor("cathegory", value);
                            dialog.dismiss();
                        }else{
                            Toast.makeText(getApplicationContext(),"Insert a valid research",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialog.show();

                return true;
            case R.id.search_name:
                // custom dialog
                final Dialog dialog2 = new Dialog(ViewActivity.this);
                dialog2.setContentView(R.layout.custom_search_dialog);    //il suo layout personalizzato
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                final EditText search2 = (EditText) dialog2.findViewById(R.id.SearchEdit);   //notare il dialog.
                Button dialogButton2 = (Button) dialog2.findViewById(R.id.searchconfirm);
                // if button is clicked, close the custom dialog
                dialogButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String value = search2.getText().toString().toLowerCase();
                        if(!value.equals("")){
                            SearchFor("name", value);
                            dialog2.dismiss();
                        }else{
                            Toast.makeText(getApplicationContext(),"Insert a valid research",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialog2.show();
                return true;
            case R.id.submit:
                //Toast.makeText(getApplicationContext(),"Submit",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ViewActivity.this, NewProductActivity.class);
                Bundle b = new Bundle();
                b.putString("email", email);
                intent.putExtras(b);
                startActivity(intent);
                return true;
            case R.id.biddings:
                ViewBiddings();
                return true;
            case R.id.won:
                ViewWon();
                return true;
            case R.id.viewall:
                viewAllItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void viewAllItems(){
        /* system of queries to get update data organized in arrays or arraylists all with the same dimension
         * the dimension corresponds to the number of items stored into the DB and the number of ArrayList
         * (not their dimension!) corresponds to the number of fields of items table into DB*/
        summary.setText("All items");
        //reset of itemlist
        itemList.clear();

        String query = "SELECT * FROM items";

        final SQLiteDatabase db = openOrCreateDatabase("AuctionDB", MODE_PRIVATE, null);
        //db.execSQL(query);
        Cursor c = db.rawQuery(query, null);
        if(c.moveToFirst()==true) {
        //c.moveToFirst();

        String QueryResult = "";
        Log.d("QUERYRESULT", "c.getCount() = " + c.getCount());

        for(int i=0; i<c.getCount();i++){
            c.moveToPosition(i);

            int _id_item = c.getInt(c.getColumnIndex("_id"));
            String _name = c.getString(c.getColumnIndex("item_name"));
            String _description = c.getString(c.getColumnIndex("item_description"));
            String _cathegory = c.getString(c.getColumnIndex("item_cathegory"));
            String _uri = c.getString(c.getColumnIndex("uri_photo"));
            String _expiration_date = c.getString(c.getColumnIndex("expiration_date"));
            int _offer = c.getInt(c.getColumnIndex("starting_price"));
            int _won = c.getInt(c.getColumnIndex("won"));
            String _seller = c.getString(c.getColumnIndex("fk_user"));
            String _offering_user = "none";

            //check into offers table if there is the best offer for this product
            String queryOffers = "SELECT * FROM offers WHERE fk_item=" + _id_item + " ORDER BY offer DESC";
            Cursor co = db.rawQuery(queryOffers, null);
            if(co.moveToFirst()==true) {
                co.moveToPosition(0);
                _offer = co.getInt(co.getColumnIndex("offer"));
                _offering_user = co.getString(co.getColumnIndex("fk_user"));
            }

            if(!NewProductActivity.isFuture(_expiration_date)){
                _won = 1;
                //update item table because Auction is expired
                String UpdateQuery = "UPDATE items SET won=1 WHERE _id=" + _id_item + "";
                db.execSQL(UpdateQuery);
            }

            AuctionItem ai = new AuctionItem(_id_item,_name, _description, _cathegory, _uri, _expiration_date, _offer, _won, _offering_user, _seller);
            itemList.add(ai);

            adapter.notifyDataSetChanged();

        }
        }
    }


    public void SearchFor(String parameter, String value){
        summary.setText("Result of the research");
        //reset of itemlist
        itemList.clear();

        String query = "SELECT * FROM items";

        if(parameter.equals("name")){
            query = query + " WHERE item_name = '" + value + "'";
        }else if(parameter.equals("cathegory")){
            query = query + " WHERE item_cathegory = '" + value + "'";
        }


        final SQLiteDatabase db = openOrCreateDatabase("AuctionDB", MODE_PRIVATE, null);
        //db.execSQL(query);
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        Log.d("QUERYRESULT", "c.getCount() = " + c.getCount());

        for(int i=0; i<c.getCount();i++){
            c.moveToPosition(i);

            int _id_item = c.getInt(c.getColumnIndex("_id"));
            String _name = c.getString(c.getColumnIndex("item_name"));
            String _description = c.getString(c.getColumnIndex("item_description"));
            String _cathegory = c.getString(c.getColumnIndex("item_cathegory"));
            String _uri = c.getString(c.getColumnIndex("uri_photo"));
            String _expiration_date = c.getString(c.getColumnIndex("expiration_date"));
            int _offer = c.getInt(c.getColumnIndex("starting_price"));
            int _won = c.getInt(c.getColumnIndex("won"));
            String _seller = c.getString(c.getColumnIndex("fk_user"));
            String _offering_user = "none";

            //check into offers table if there is the best offer for this product
            String queryOffers = "SELECT * FROM offers WHERE fk_item=" + _id_item + " ORDER BY offer DESC";
            Cursor co = db.rawQuery(queryOffers, null);
            if(co.moveToFirst()==true) {
                co.moveToPosition(0);
                _offer = co.getInt(co.getColumnIndex("offer"));
                _offering_user = co.getString(co.getColumnIndex("fk_user"));
            }

            if(!NewProductActivity.isFuture(_expiration_date)){
                _won = 1;
                //update item table because Auction is expired
                String UpdateQuery = "UPDATE items SET won=1 WHERE _id=" + _id_item + "";
                db.execSQL(UpdateQuery);
            }

            AuctionItem ai = new AuctionItem(_id_item,_name, _description, _cathegory, _uri, _expiration_date, _offer, _won, _offering_user, _seller);
            itemList.add(ai);
            adapter.notifyDataSetChanged();
        }

    }



    public void ViewBiddings(){
        summary.setText("Your biddings");
        itemList.clear();
        adapter.notifyDataSetChanged();
        final SQLiteDatabase db = openOrCreateDatabase("AuctionDB", MODE_PRIVATE, null);

        String queryOffers = "SELECT * FROM offers WHERE fk_user = '" + email +"'";
        Cursor c = db.rawQuery(queryOffers, null);
        if(c.moveToFirst())

        for(int i=0; i<c.getCount();i++){
            c.moveToPosition(i);
            //int _id_offer = c.getInt(c.getColumnIndex("_id"));
            int _offer = c.getInt(c.getColumnIndex("offer"));
            int _fk_item = c.getInt(c.getColumnIndex("fk_item"));

            String queryItems = "SELECT * FROM items WHERE _id = " + _fk_item + "";   //aaaa
          //  String queryItems = "SELECT * FROM items WHERE offer = (SELECT MAX(offer) FROM offers WHERE _id = " + _fk_item + ")";

            Cursor ci = db.rawQuery(queryItems, null);
            ci.moveToFirst();

            for(int j=0; j<ci.getCount();j++){
                ci.moveToPosition(j);

                int _id_item = ci.getInt(ci.getColumnIndex("_id"));
                String _name = ci.getString(ci.getColumnIndex("item_name"));
                String _description = ci.getString(ci.getColumnIndex("item_description"));
                String _cathegory = ci.getString(ci.getColumnIndex("item_cathegory"));
                String _uri = ci.getString(ci.getColumnIndex("uri_photo"));
                String _expiration_date = ci.getString(ci.getColumnIndex("expiration_date"));
                //int _offer = c.getInt(c.getColumnIndex("starting_price"));
                int _won = ci.getInt(ci.getColumnIndex("won"));
                String _seller = ci.getString(ci.getColumnIndex("fk_user"));
                //String _offering_user = "none";

                if(!NewProductActivity.isFuture(_expiration_date)){
                    _won = 1;
                    //update item table because Auction is expired
                    String UpdateQuery = "UPDATE items SET won=1 WHERE _id=" + _id_item + "";
                    db.execSQL(UpdateQuery);
                }

                AuctionItem ai = new AuctionItem(_id_item,_name, _description, _cathegory, _uri, _expiration_date, _offer, _won, email, _seller);
                itemList.add(ai);
                adapter.notifyDataSetChanged();
            }
        }


    }

    public void ViewWon(){
        summary.setText("Your won auctions");
        //reset of itemlist
        itemList.clear();
        adapter.notifyDataSetChanged();
        String query = "SELECT * FROM items";

        final SQLiteDatabase db = openOrCreateDatabase("AuctionDB", MODE_PRIVATE, null);
        //db.execSQL(query);
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        String QueryResult = "";
        Log.d("QUERYRESULT", "c.getCount() = " + c.getCount());

        for(int i=0; i<c.getCount();i++){
            c.moveToPosition(i);

            int _id_item = c.getInt(c.getColumnIndex("_id"));
            String _name = c.getString(c.getColumnIndex("item_name"));
            String _description = c.getString(c.getColumnIndex("item_description"));
            String _cathegory = c.getString(c.getColumnIndex("item_cathegory"));
            String _uri = c.getString(c.getColumnIndex("uri_photo"));
            String _expiration_date = c.getString(c.getColumnIndex("expiration_date"));
            int _offer = c.getInt(c.getColumnIndex("starting_price"));
            int _won = c.getInt(c.getColumnIndex("won"));
            String _seller = c.getString(c.getColumnIndex("fk_user"));
            String _offering_user = "none";

            //check into offers table if there is the best offer for this product
            String queryOffers = "SELECT * FROM offers WHERE fk_item=" + _id_item + " ORDER BY offer DESC";
            Cursor co = db.rawQuery(queryOffers, null);
            if(co.moveToFirst()==true) {
                co.moveToPosition(0);
                _offer = co.getInt(co.getColumnIndex("offer"));
                _offering_user = co.getString(co.getColumnIndex("fk_user"));

                if(_won==1 && _offering_user.equals(email)){
                    //item won! Add to list!
                    AuctionItem ai = new AuctionItem(_id_item,_name, _description, _cathegory, _uri, _expiration_date, _offer, _won, _offering_user, _seller);
                    itemList.add(ai);

                    adapter.notifyDataSetChanged();

                }

            }




        }

    }



    public Bitmap loadBitmap(String url){
        Bitmap bm = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            bm = BitmapFactory.decodeStream(bis);
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (bis != null){
                try{
                    bis.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            if (is != null){
                try{
                    is.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return bm;
    }

    /*
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                AuctionItem itemclicked = itemList.get(pos);
                String name = itemclicked.getName();
                Toast.makeText(getApplicationContext(),"pos: " + pos + ", nome: " + name, Toast.LENGTH_LONG).show();

    }*/


    private class AsyncLoadBitmap extends AsyncTask<Void,Void,Void> {
        Bitmap photo;

        @Override
        protected void onPreExecute(){

        }
        @Override
        protected Void doInBackground(Void...params){
            //Operazioni da fare in background
            //    photo = loadBitmap(Environment.getExternalStorageDirectory() + "/crossoverauction/prova.png");
            photo = new ImageSaver(ViewActivity.this).setFileName("prova.png").setDirectoryName("images").load();

            //   this.photo = BitmapFactory.decodeResource(getResources(), R.drawable.adminshoes);
           /* runOnUiThread(new Runnable(){
                public void run(){//Interazione con l'interfaccia
                    mImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.adminshoes));
                }
            });*/


            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            // mImageView.setImageBitmap(this.photo);
            //  mImageView.setImageResource(R.drawable.adminshoes);


        }
    }


    private class ListClickHandler implements OnItemClickListener {


        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int pos, long arg3) {
            // TODO Auto-generated method stub
            final AuctionItem itemclicked = itemList.get(pos);

           // Toast.makeText(getApplicationContext(),"pos: " + pos + ", nome: " + name, Toast.LENGTH_LONG).show();

            CharSequence scelta[] = new CharSequence[] {"Make an offer", "Description"};

            AlertDialog.Builder builder = new AlertDialog.Builder(ViewActivity.this);
            builder.setTitle("Option");
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setCancelable(true);
            builder.setItems(scelta, new Dialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which==0){
                        //azione cliccando il primo elemento
                        if(NewProductActivity.isFuture(itemclicked.getExpirationDate())){
                            //opens a new Activity

                            Intent intent = new Intent(ViewActivity.this, OfferActivity.class);
                            Bundle b = new Bundle();
                            b.putString("email", email);
                            b.putString("name",itemclicked.getName());
                            b.putString("uri",itemclicked.getUri());
                            b.putString("seller",itemclicked.getSeller());
                            b.putString("cathegory",itemclicked.getCathegory());
                            b.putInt("offer",itemclicked.getOffer());
                            b.putString("expiration_date",itemclicked.getExpirationDate());
                            b.putInt("id_item",itemclicked.getId());
                            intent.putExtras(b);
                            startActivity(intent);
                            finish();
//                      Toast.makeText(mContext, "Last offer is " + _offer/100 + "by " + _offering_user, Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"You can't bid because this auction is expired!", Toast.LENGTH_LONG).show();
                        }




                        dialog.dismiss();
                    }else if(which==1){
                        //azione cliccando il secondo
                        AlertDialog.Builder DescriptionAlert;
                        AlertDialog alert;

                        DescriptionAlert = new AlertDialog.Builder(ViewActivity.this);  //getApplicationContext() o getBaseContext() possono dare errore
                        DescriptionAlert.setTitle("Product description");
                        DescriptionAlert.setMessage(itemclicked.getDescription());
                        DescriptionAlert.setCancelable(true);
                        DescriptionAlert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        alert = DescriptionAlert.create();
                        alert.show();
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        }

    }

    ///////////////




}
