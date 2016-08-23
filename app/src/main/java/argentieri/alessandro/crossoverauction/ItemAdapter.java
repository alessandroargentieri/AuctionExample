package argentieri.alessandro.crossoverauction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by pccasa on 12/08/2016.
 */
public class ItemAdapter extends ArrayAdapter<AuctionItem> {

    private Context mContext;
    private List<AuctionItem> itemList;

    private String _name;
    private String _description;
    private String _cathegory;
    public String _uri;
    private String _expiration_date;
    private int _offer;
    private int _won;
    private String _offering_user;
    private String _seller;
    private int _id;

    public ImageView image;
    public String email;




    public ItemAdapter(Context mContext, List<AuctionItem> itemList, String mail) {
        super(mContext,R.layout.custom_list_item,itemList);
        this.mContext = mContext;
        this.itemList = itemList;
        this.email = mail;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //1. Create inflater
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //2. Get the row View from inflater
        View rowView = inflater.inflate(R.layout.custom_list_item, parent, false);
        //3. Get the elements of custom_list_item
        TextView item_name = (TextView) rowView.findViewById(R.id.item_name);
        TextView item_cathegory = (TextView) rowView.findViewById(R.id.item_cathegory);
        TextView fk_user = (TextView) rowView.findViewById(R.id.fk_user);

        image = (ImageView) rowView.findViewById(R.id.image);
     /*   image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(image);
            }
        });*/


        TextView expiration_date = (TextView) rowView.findViewById(R.id.expiration_date);
        TextView offer = (TextView) rowView.findViewById(R.id.best_offer);
        TextView offerent = (TextView) rowView.findViewById(R.id.last_offerent);
        TextView state = (TextView) rowView.findViewById(R.id.state_of_auction);



       // try {
            AuctionItem item = itemList.get(position);
            //get all the information from the item
            _id = item.getId();
            _name = item.getName();
            _description = item.getDescription();
            _cathegory = item.getCathegory();
            _uri = item.getUri();
            _expiration_date = item.getExpirationDate();
            _offer = item.getOffer();
            _won = item.getWon();
            _offering_user = item.getOfferingUser();
            _seller = item.getSeller();

            float offer_dollars = (float) _offer / 100;


            //insert them into the custom_list_item View
            item_name.setText("Product: " + _name);
            item_cathegory.setText("Cathegory: " + _cathegory);
            fk_user.setText("Seller: " + _seller);
            expiration_date.setText("Expiration date: " + _expiration_date);

            if (_offering_user.equals("none")) {
                offer.setText("Starting Price: " + offer_dollars + " $");
                offerent.setText("no offers yet");

            } else {
                offer.setText("Last offer: " + offer_dollars + " $");
                offerent.setText("from " + _offering_user);
            }

            if (_won == 0) {
                state.setText("Auction in course");
            } else {
                if (_offering_user.equals("none"))
                    state.setText("Auction expired with no offers");
                else
                    state.setText("Auction won by: " + _offering_user);
            }


            // Glide.with(mContext).load(album.getLink()).into(thumbImage);
            //new AsyncLoadBitmap().execute();

            Bitmap photo = new ImageSaver(getContext()).setFileName(_uri).setDirectoryName("images").load();
            image.setImageBitmap(photo);
     //   }catch(Exception e){
     //       Log.e("getViewAdapter", e.toString());
     //   }
        return rowView;
    }
/*
    private class AsyncLoadBitmap extends AsyncTask<Void,Void,Void> {
        Bitmap photo;

        @Override
        protected void onPreExecute(){ }


        @Override
        protected Void doInBackground(Void...params){
            photo = new ImageSaver(getContext()).setFileName(_uri).setDirectoryName("images").load();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            image.setImageBitmap(this.photo);
        }
    }

*/

}

