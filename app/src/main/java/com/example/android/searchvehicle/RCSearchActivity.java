package com.example.android.searchvehicle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.florent37.materialtextfield.MaterialTextField;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class RCSearchActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    String registrationNumber = "";
    public static Map<String, String> cookies;
    private Bitmap bitmap;
    public static String formNumber;
    public static String viewState;
    ImageView imageCaptcha;
    ProgressBar progressBarCaptcha;
    MaterialTextField materialTextFieldCaptcha;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rcsearch);
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                if (tabId == R.id.tab_vehicle) {
                    Toast.makeText(RCSearchActivity.this, "Vehicle", Toast.LENGTH_SHORT).show();
                } else if (tabId == R.id.tab_recent) {
                    Toast.makeText(RCSearchActivity.this, "Recents", Toast.LENGTH_SHORT).show();
                } else if (tabId == R.id.tab_favorite) {
                    Toast.makeText(RCSearchActivity.this, "Favorite", Toast.LENGTH_SHORT).show();
                } else if (tabId == R.id.tab_user) {
                    Toast.makeText(RCSearchActivity.this, "User", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final EditText editText = findViewById(R.id.editText);
        imageCaptcha = findViewById(R.id.imageCaptcha);
        progressBarCaptcha = findViewById(R.id.progressBarCaptcha);
        materialTextFieldCaptcha = findViewById(R.id.materialTextFieldCaptcha);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText() != null) {
                    Toast.makeText(RCSearchActivity.this, editText.getText().toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RCSearchActivity.this, "Enter Valid Registration Number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        new MyCapthaFindingAsyncTask().execute();
    }

    public class MyCapthaFindingAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Connection.Response form = null;
            try {
                form = Jsoup.connect("https://parivahan.gov.in/rcdlstatus/")
                        .method(Connection.Method.GET)
                        .timeout(10000)
                        .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.101 Safari/537.36")
                        .execute();
                cookies = form.cookies();
                Log.d("asa", "doInBackground: inninini");
            } catch (IOException e) {
                e.printStackTrace();
            }

            cookies = form.cookies();

            org.jsoup.nodes.Document formDocument = null;
            try {
                formDocument = form.parse();
            } catch (IOException e) {
                e.printStackTrace();
            }
            formNumber = formDocument.select("button[class=ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only]").attr("name");
            viewState = formDocument.select("input[name=javax.faces.ViewState]").attr("value");

            String captcha = formDocument.getElementsByTag("img").get(1).attr("src");
            String captchaURL = "https://parivahan.gov.in" + captcha;
            InputStream input = null;
            try {
                input = new java.net.URL(captchaURL).openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bitmap = BitmapFactory.decodeStream(input);

            return bitmap;

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageCaptcha.setImageBitmap(bitmap);
            progressBarCaptcha.setVisibility(View.INVISIBLE);
            materialTextFieldCaptcha.setVisibility(View.VISIBLE);
        }
    }
}
