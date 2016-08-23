package argentieri.alessandro.crossoverauction;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewProductActivity extends AppCompatActivity {

    public String email="";
    public Bitmap photo;
    private static final int CAMERA_REQUEST = 1888;
    ImageView image;
    EditText nameEdit;
    EditText cathegoryEdit;
    EditText descriptionEdit;
    EditText startingPriceEdit;
    DatePicker datePicker;

    Boolean sp_valid = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            Bundle b = getIntent().getExtras();
            email = b.getString("email");
            Toast.makeText(getApplicationContext(), "e-mail: " + email, Toast.LENGTH_LONG).show();
        }catch(Exception e){
            Log.d("BUNDLE","No email received!");
        }
        setContentView(R.layout.activity_new_product);

        image = (ImageView) findViewById(R.id.photo);
        nameEdit = (EditText)findViewById(R.id.editNameNew);
        cathegoryEdit = (EditText)findViewById(R.id.editCathegoryNew);
        descriptionEdit = (EditText)findViewById(R.id.editDescriptionNew);;
        startingPriceEdit = (EditText)findViewById(R.id.editPriceNew);
        datePicker = (DatePicker) findViewById(R.id.datePicker);



    }


    public void PickAPhoto(View v){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
        //the result in onActivityResult()
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==CAMERA_REQUEST && resultCode==RESULT_OK && data!=null){
            photo = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(photo);
        }

    }


    public void SubmitProduct(View v){
        //extract from all the edittext and the date picker and save into DB and recall ViewActivity.class
        String name =  nameEdit.getText().toString().toLowerCase();
        String cathegory = cathegoryEdit.getText().toString().toLowerCase();
        String description = descriptionEdit.getText().toString();
        String startingPrice = startingPriceEdit.getText().toString();
        String startCentPrice ="";
        try {
            Double sp = Double.parseDouble(startingPrice) * 100;
            String sp_string = sp + "";
            startCentPrice = sp_string.split("\\.")[0];
            sp_valid = true;
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), "Put a valid starting price!", Toast.LENGTH_LONG).show();
            sp_valid = false;
        }


        int year = datePicker.getYear();
        int month = datePicker.getMonth() + 1;
        int day = datePicker.getDayOfMonth();

        Calendar now = Calendar.getInstance();
        String nowDateTime = now.getTime().toString();
        String nowTime = nowDateTime.substring(11,19);
        String expirationDate = year + "-" + month + "-" + day + " " + nowTime;
        //Toast.makeText(getApplicationContext(), expirationDate, Toast.LENGTH_LONG).show();

        Boolean isFuture = isFuture(expirationDate);




        if(!name.equals("") && !cathegory.equals("") && !description.equals("") && !startingPrice.equals("") && photo!=null && sp_valid==true && isFuture){
            //save picture and insert into items table
            String namefilepic = name.toLowerCase() + "_";
            String[] split_email = email.split("\\.");
            for(int i=0; i<split_email.length - 1; i++){
                namefilepic = namefilepic + split_email[i];
            }
            namefilepic = namefilepic + ".png";

         //   Toast.makeText(getApplicationContext(), namefilepic, Toast.LENGTH_LONG).show();

            new ImageSaver(this).setFileName(namefilepic).setDirectoryName("images").save(photo);
            //declare DB and insert record into items table
            final SQLiteDatabase db = openOrCreateDatabase("AuctionDB", MODE_PRIVATE, null);
            String base_query = "INSERT INTO items (item_name, item_description, item_cathegory, uri_photo, expiration_date, starting_price, won, fk_user) VALUES ";
            db.execSQL(base_query + "('" + name + "','" + description + "', '" + cathegory + "', '" + namefilepic + "', '" + expirationDate + "', '" + startCentPrice + "', 0, '" + email + "')");
            Intent intent = new Intent(NewProductActivity.this, ViewActivity.class);
            Bundle b = new Bundle();
            b.putString("email", email);
            intent.putExtras(b);
            startActivity(intent);
            finish();

        }else{
            Toast.makeText(getApplicationContext(), "Fill the empty or wrong elements!", Toast.LENGTH_LONG).show();
        }

    }





    public static boolean isFuture(String data){

        Date parsed=new Date();
        Date current= new Date();
        //class calendar allows comparison between dates
        Calendar c= Calendar.getInstance();
        current=c.getTime();
        //date format in the string argument
        DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            parsed=df.parse(data);
        }catch(ParseException p){
            System.out.println(p.toString());
        }
        //Boolean b=current.after(parsed); //current date after the parsed one
        return current.before(parsed); //current date before the parsed one
    }



}
