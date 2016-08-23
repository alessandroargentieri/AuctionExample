package argentieri.alessandro.crossoverauction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Bot which m
 */

public class BotReceiver extends BroadcastReceiver {

    public static String TAG = "BOT_RECEIVER";

    public BotReceiver(){
        Log.i(TAG,"INIT Receiver");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final SQLiteDatabase db = context.openOrCreateDatabase("AuctionDB", Context.MODE_PRIVATE, null);

        Log.i(TAG, "Bot started!");
        try{
            //Select on items and choose one item randomically among the not won ones
            Cursor c = db.rawQuery("SELECT _id, starting_price FROM items WHERE won=0", null);
            if(c.moveToFirst() && c.getCount()>0){
                int max = c.getCount();
                Log.i(TAG, "c.getCount(): " + max);
                int random = (int)Math.round(Math.random()*max);
                Log.i(TAG, "random: " + random);
                c.moveToPosition(random);
                int _id_random = c.getInt(c.getColumnIndex("_id"));
                int starting_price = c.getInt(c.getColumnIndex("starting_price"));

                //We have chosen the (random) item
                //On that item searches for the highest offer
                Cursor c1 = db.rawQuery("SELECT * FROM offers WHERE fk_item=" + _id_random + " ORDER BY offer DESC", null);
                if(c1.moveToFirst() && c.getCount()>0){
                    c1.moveToPosition(0);
                    int _offer = c1.getInt(c1.getColumnIndex("offer"));
                    String _offering_user = c1.getString(c1.getColumnIndex("fk_user"));
                    int bot_offer = _offer + (int)Math.round(Math.random()*_offer/4);

                    if(_offering_user.equals("botuser@gmail.com")){
                        String updateQuery = "UPDATE offers SET offer=" + bot_offer + " WHERE fk_user = 'botuser@gmail.com' AND fk_item = " + _id_random;
                        db.execSQL(updateQuery);
                        Log.i(TAG, "Update offer on id item: " + _id_random + " of $ cents: " + bot_offer);
                    }else{
                        String insertQuery = "INSERT INTO offers (offer, fk_user, fk_item) VALUES (" + bot_offer + ", 'botuser@gmail.com', " + _id_random + ")";
                        db.execSQL(insertQuery);
                        Log.i(TAG, "First offer for botuser@gmailccom on id item: " + _id_random + " of $ cents: " + bot_offer);
                    }
                }else{
                    //there aren't any offer on this random item, than make one major of the starting_price
                    int bot_offer = starting_price + (int)Math.round(Math.random()*starting_price/4);
                    String insertQuery = "INSERT INTO offers (offer, fk_user, fk_item) VALUES (" + bot_offer + ", 'botuser@gmail.com', " + _id_random + ")";
                    db.execSQL(insertQuery);
                    Log.i(TAG, "First offer on id item: " + _id_random + " of $ cents: " + bot_offer);
                }
            }
        }catch(SQLException sqlExc){    sqlExc.printStackTrace();    }
    }
}