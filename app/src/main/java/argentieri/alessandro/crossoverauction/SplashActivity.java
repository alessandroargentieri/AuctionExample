package argentieri.alessandro.crossoverauction;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

//branch_new commit
//questa è una splash activity
// e questi sono i miei commenti


public class SplashActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final SQLiteDatabase db = openOrCreateDatabase("AuctionDB", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS users (email VARCHAR(100) PRIMARY KEY, password VARCHAR(50))");
        db.execSQL("CREATE TABLE IF NOT EXISTS items (_id INTEGER PRIMARY KEY AUTOINCREMENT, item_name VARCHAR(50), item_description VARCHAR, item_cathegory VARCHAR(30), uri_photo VARCHAR, expiration_date DATETIME, starting_price INT, won INT, fk_user VARCHAR(100))");
        db.execSQL("CREATE TABLE IF NOT EXISTS offers (_id INTEGER PRIMARY KEY AUTOINCREMENT, offer INT, fk_user VARCHAR(100), fk_item INTEGER)");


        try {
            db.execSQL("INSERT INTO users VALUES ('adminuser@gmail.com','admin')");
            db.execSQL("INSERT INTO users VALUES ('botuser@gmail.com','bot1')");

            Bitmap shoes1 = BitmapFactory.decodeResource(getResources(), R.drawable.adminshoes);
            Bitmap shoes2 = BitmapFactory.decodeResource(getResources(), R.drawable.adminshoestwo);
            Bitmap pc1 = BitmapFactory.decodeResource(getResources(), R.drawable.adminpc);
            Bitmap pc2 = BitmapFactory.decodeResource(getResources(), R.drawable.adminmac);
            Bitmap cell = BitmapFactory.decodeResource(getResources(), R.drawable.admincell);
            Bitmap art1 = BitmapFactory.decodeResource(getResources(), R.drawable.adminart);
            Bitmap art2 = BitmapFactory.decodeResource(getResources(), R.drawable.adminarttwo);
            Bitmap art3 = BitmapFactory.decodeResource(getResources(), R.drawable.adminartthree);

            new ImageSaver(this).setFileName("shoes1_adminuser@gmail.png").setDirectoryName("images").save(shoes1);
            new ImageSaver(this).setFileName("shoes2_adminuser@gmail.png").setDirectoryName("images").save(shoes2);
            new ImageSaver(this).setFileName("pc1_adminuser@gmail.png").setDirectoryName("images").save(pc1);
            new ImageSaver(this).setFileName("pc2_adminuser@gmail.png").setDirectoryName("images").save(pc2);
            new ImageSaver(this).setFileName("cell_adminuser@gmail.png").setDirectoryName("images").save(cell);
            new ImageSaver(this).setFileName("art1_adminuser@gmail.png").setDirectoryName("images").save(art1);
            new ImageSaver(this).setFileName("art2_adminuser@gmail.png").setDirectoryName("images").save(art2);
            new ImageSaver(this).setFileName("art3_adminuser@gmail.png").setDirectoryName("images").save(art3);

            String base_query = "INSERT INTO items (item_name, item_description, item_cathegory, uri_photo, expiration_date, starting_price, won, fk_user) VALUES ";
            db.execSQL(base_query + "('black shoes','gymnastic shoes for sport', 'dressing', 'shoes1_adminuser@gmail.png', '2016-12-4 14:59:00', '1200', 0, 'adminuser@gmail.com')");
            db.execSQL(base_query + "('orange shoes','gymnastic shoes for sport', 'dressing', 'shoes2_adminuser@gmail.png', '2016-12-4 14:59:00', '700', 0, 'adminuser@gmail.com')");
            db.execSQL(base_query + "('personal computer','mac for business and family', 'electronics', 'pc1_adminuser@gmail.png', '2016-12-4 14:59:00', '80000', 0, 'adminuser@gmail.com')");
            db.execSQL(base_query + "('personal computer mac','mac personal computer for work', 'electronics', 'pc2_adminuser@gmail.png', '2016-12-4 14:59:00', '90000', 0, 'adminuser@gmail.com')");
            db.execSQL(base_query + "('smartphone','smartphone with 4G', 'electronics', 'cell_adminuser@gmail.png', '2016-08-4 14:59:00', '20000', 0, 'adminuser@gmail.com')");
            db.execSQL(base_query + "('painting of the city','wonderful painting', 'art', 'art1_adminuser@gmail.png', '2016-08-3 14:59:00', '35000', 0, 'adminuser@gmail.com')");
            db.execSQL(base_query + "('painting of the avenue','wonderful painting', 'art', 'art2_adminuser@gmail.png', '2016-12-4 14:59:00', '36000', 0, 'adminuser@gmail.com')");
            db.execSQL(base_query + "('painting of nature','nature painting', 'art', 'art3_adminuser@gmail.png', '2016-12-4 14:59:00', '40000', 0, 'adminuser@gmail.com')");

            db.execSQL("INSERT INTO offers (offer, fk_user, fk_item) VALUES (1250, 'botuser@gmail.com', 1)");
            db.execSQL("INSERT INTO offers (offer, fk_user, fk_item) VALUES (100000, 'adminuser@gmail.com', 5)");

        }catch(SQLException e){
            Log.e("SQLite", "User and Item Records already inserted! Error: " + e.toString());
        }

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashActivity.this, StartActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }




}