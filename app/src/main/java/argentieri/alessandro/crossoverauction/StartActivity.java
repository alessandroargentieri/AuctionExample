package argentieri.alessandro.crossoverauction;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    String email_pref;
    String passw_pref;
    Boolean remember;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        email_pref = sharedpreferences.getString("EMAIL_SAVED", "");
        passw_pref = sharedpreferences.getString("PASSW_SAVED", "");
        remember = sharedpreferences.getBoolean("REMEMBER", false);
    }


    public void SignIn(View v){
       // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // custom dialog
        final Dialog dialog = new Dialog(StartActivity.this);
        dialog.setContentView(R.layout.sign_in_layout);    //il suo layout personalizzato
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // set the custom dialog components - text, image and button
        final EditText email = (EditText) dialog.findViewById(R.id.editEmailSignIn);
        final EditText password = (EditText) dialog.findViewById(R.id.editPasswordSignIn);
        final CheckBox rememberCheck = (CheckBox) dialog.findViewById(R.id.check_in);


        if(remember == true && !email_pref.equals("") && !passw_pref.equals("")){
            email.setText(email_pref);
            password.setText(passw_pref);
            rememberCheck.setChecked(true);
        }

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_confirm_signin);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString();
                String psw = password.getText().toString();
                //QUERY AL DB PER VEDERE SE L'UTENTE ESISTE E ACCESSO EVENTUALE ALL'ACTIVITY SUCCESSIVA OPPURE ERRORE
                String query = "SELECT email, password FROM users WHERE email='" + mail + "' AND password = '" + psw + "'";
                final SQLiteDatabase db = openOrCreateDatabase("AuctionDB", MODE_PRIVATE, null);
                //db.execSQL(query);
                Cursor c = db.rawQuery(query, null);
                if(!c.moveToFirst()){
                    Toast.makeText(getApplicationContext(),"No matches!", Toast.LENGTH_LONG).show();
                }else{
                    //Toast.makeText(getApplicationContext(),"UTENTE TROVATO", Toast.LENGTH_LONG).show();
                    dialog.dismiss();

                    if(rememberCheck.isChecked()){
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("EMAIL_SAVED", email.getText().toString());
                        editor.putString("PASSW_SAVED", password.getText().toString());
                        editor.putBoolean("REMEMBER", true);
                        editor.commit();
                    }else{
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("EMAIL_SAVED", "");
                        editor.putString("PASSW_SAVED", "");
                        editor.putBoolean("REMEMBER", false);
                        editor.commit();
                    }

                    //pass to the ViewActivity
                    Intent intent = new Intent(StartActivity.this, ViewActivity.class);
                    Bundle b = new Bundle();
                    b.putString("email", mail);
                    intent.putExtras(b);
                    startActivity(intent);
                    finish();
                }

            }
        });
        dialog.show();
    }

    public void SignUp(View v){
        // custom dialog
        final Dialog dialog = new Dialog(StartActivity.this);
        dialog.setContentView(R.layout.sign_up_layout);    //il suo layout personalizzato
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // set the custom dialog components - text, image and button
        final EditText email = (EditText) dialog.findViewById(R.id.editEmailSignUp);
        final EditText password = (EditText) dialog.findViewById(R.id.editPasswordSignUp);
        final EditText password_confirm = (EditText) dialog.findViewById(R.id.editPasswordAgainSignUp);
        final CheckBox rememberCheck = (CheckBox) dialog.findViewById(R.id.check_up);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_confirm_signup);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString();
                String psw = password.getText().toString();
                String psw_conf = password_confirm.getText().toString();

                if(psw.equals(psw_conf)){
                    //QUERY AL DB PER VEDERE SE L'UTENTE ESISTE E ACCESSO EVENTUALE ALL'ACTIVITY SUCCESSIVA OPPURE ERRORE
                    final SQLiteDatabase db = openOrCreateDatabase("AuctionDB", MODE_PRIVATE, null);
                    String query = "INSERT into users VALUES ('" + mail + "','" + psw + "')";
                    try {
                        db.execSQL(query);
                        Toast.makeText(getApplicationContext(),"User registered!",Toast.LENGTH_LONG).show();
                        dialog.dismiss();


                        if(rememberCheck.isChecked()){
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("EMAIL_SAVED", email.getText().toString());
                            editor.putString("PASSW_SAVED", password.getText().toString());
                            editor.putBoolean("REMEMBER", true);
                            editor.commit();
                        }else{
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("EMAIL_SAVED", "");
                            editor.putString("PASSW_SAVED", "");
                            editor.putBoolean("REMEMBER", false);
                            editor.commit();
                        }

                        //pass to the ViewActivity
                        Intent intent = new Intent(StartActivity.this, ViewActivity.class);
                        Bundle b = new Bundle();
                        b.putString("email", mail);
                        intent.putExtras(b);
                        startActivity(intent);

                    }catch(SQLException e){
                        Toast.makeText(getApplicationContext(),"User already exists!",Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(getApplicationContext(),"Retype your password!",Toast.LENGTH_LONG).show();
                }
                //dialog.dismiss();
            }
        });
        dialog.show();
    }

}
