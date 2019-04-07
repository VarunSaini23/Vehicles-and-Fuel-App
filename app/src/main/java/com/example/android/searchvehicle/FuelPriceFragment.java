package com.example.android.searchvehicle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


public class FuelPriceFragment extends Fragment {

    final String BASE_FUEL_PRiCE_URL = "https://www.petroldieselprice.com/search-petrol-diesel-price-with-postal-pincode";
    final String BASE_URL = "https://www.petroldieselprice.com/";
    final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
    final String TEST_PINCODE = "160030";
    String[][] PricesDifferentDays = new String[10][10] ;
    String TAG = "fuelpricepostal";
//    ArrayList<SearchFuelPriceState> searchFuelPriceStates;
    ArrayList<String> arrayList;
    ArrayList<String> arrayList1;

    TextView todayDate,todayPetrolPrice,todayDieselPrice,
            yesterdayDate,yesterdayPetrolPrice,yesterdayDieselPrice,
            dayBeforeYestDate,dayBeforeYestPetrolPrice,dayBeforeYestDieselPrice;

    TextView locality;
    String cityName,cityLink;

    CardView card1;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fuel_price, container, false);

        cityLink = this.getArguments().getString("cityLink");
        Log.d(TAG, "onCreateView: "+cityLink);
        arrayList = new ArrayList<>();
        arrayList1 = new ArrayList<>();

        Log.d(TAG, "onCreateView: "+cityLink);

        card1 = v.findViewById(R.id.card1);
        todayDate = v.findViewById(R.id.todayDate);
        todayPetrolPrice = v.findViewById(R.id.todayPetrolPrice);
        todayDieselPrice = v.findViewById(R.id.todayDieselPrice);
        yesterdayDate = v.findViewById(R.id.yesterdayDate);
        yesterdayPetrolPrice = v.findViewById(R.id.yesterdayPetrolPrice);
        yesterdayDieselPrice = v.findViewById(R.id.yesterdayDieselPrice);
        dayBeforeYestDate = v.findViewById(R.id.dayBeforeYestDate);
        dayBeforeYestPetrolPrice = v.findViewById(R.id.dayBeforeYestPetrolPrice);
        dayBeforeYestDieselPrice = v.findViewById(R.id.dayBeforeYestDieselPrice);
        locality = v.findViewById(R.id.locality);
        locality.setSelected(true);
        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),SearchCitiesDialogActivity.class);
                i.putStringArrayListExtra("arrayList",arrayList);
                i.putStringArrayListExtra("arrayList1",arrayList1);
                startActivity(i);
            }
        });

        TextView[] textViews = {todayDate,todayPetrolPrice,todayDieselPrice,
                yesterdayDate,yesterdayPetrolPrice,yesterdayDieselPrice,
                dayBeforeYestDate,dayBeforeYestPetrolPrice,dayBeforeYestDieselPrice,locality};

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Montserrat.ttf");
        for(TextView tx : textViews){
            tx.setTypeface(tf);
        }

        if(LoginActivity.PostalCode==null){
            locality.setText("Can't retrieve location. Click on search button to Manually select City");

        }else if(LoginActivity.PostalCode!=null && cityName==null){
            locality.setText(LoginActivity.PostalCode + " , " + LoginActivity.Locality);
            MyFuelPriceAsyncTask myFuelPriceAsyncTask = new MyFuelPriceAsyncTask();
            myFuelPriceAsyncTask.execute(LoginActivity.PostalCode);
        }else if(cityName!=null){
            locality.setText(cityLink);

        }

        AllCitiesFetch allCitiesFetch = new AllCitiesFetch();
        allCitiesFetch.execute();
        return v;
    }

    public class MyFuelPriceAsyncTask extends AsyncTask<String,Void,String[][]>{

        @Override
        protected String[][] doInBackground(String... postalCode) {

            try {
                    org.jsoup.Connection.Response loginPageResponse = Jsoup.connect(BASE_FUEL_PRiCE_URL)
                            .method(org.jsoup.Connection.Method.POST)
                            .userAgent(USER_AGENT)
                            .execute();

                    FormElement loginForm = (FormElement) loginPageResponse.parse().select("div.woocommerce-info > form").first();

                    Element pincodeField = loginForm.select("input").first();
                    pincodeField.val(postalCode[0]);
                    Log.d(TAG, "doInBackground: " + postalCode[0]);

                    org.jsoup.Connection.Response resultPageResponse = loginForm.submit()
                            .cookies(loginPageResponse.cookies())
                            .userAgent(USER_AGENT)
                            .execute();


                    Element threeDaysFuelPriceData = resultPageResponse.parse().select("table.shop_table > tfoot").first();
                    Elements refinedFuelPriceData = threeDaysFuelPriceData.select("tfoot > tr");

                    int i = 0, j = 0;
                    for (Element e : refinedFuelPriceData) {
                        Elements ee = e.select("td");
                        for (Element eee : ee) {

                            PricesDifferentDays[i][j] = eee.text().toString();
                            Log.d("PricesDiffereent days", "doInBackground: " + i + " " + j + PricesDifferentDays[i][j]);
                            j++;
                        }
                        i++;
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

                return PricesDifferentDays;

            }

        @Override
        protected void onPostExecute(String[][] responseStr) {

            todayDate.setText(correctDateFormConverter(responseStr[0][0]));
            todayPetrolPrice.setText(correctFuelFormConverter(responseStr[0][1]));
            todayDieselPrice.setText(correctFuelFormConverter(responseStr[0][2]));
            yesterdayDate.setText(correctDateFormConverter(responseStr[1][3]));
            yesterdayPetrolPrice.setText(correctFuelFormConverter(responseStr[1][4]));
            yesterdayDieselPrice.setText(correctFuelFormConverter(responseStr[1][5]));
            dayBeforeYestDate.setText(correctDateFormConverter(responseStr[2][6]));
            dayBeforeYestPetrolPrice.setText(correctFuelFormConverter(responseStr[2][7]));
            dayBeforeYestDieselPrice.setText(correctFuelFormConverter(responseStr[2][8]));

        }
    }

    public class AllCitiesFetch extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            //populate the content for onclick of fab
            try {
                org.jsoup.Connection.Response baseUrlResponse = Jsoup.connect(BASE_URL)
                        .method(org.jsoup.Connection.Method.GET)
                        .userAgent(USER_AGENT).execute();


                Elements stateNameUrl = baseUrlResponse.parse().select("div#order_review a");

                for (Element data : stateNameUrl){
                    Log.d(TAG, "doInBackground: "+data);
                    String stateName = data.text();
                    String stateUrl = data.attr("href");
//                    searchFuelPriceStates.add(new SearchFuelPriceState(stateName,stateUrl));
                    arrayList.add(stateName);
                    arrayList1.add(stateUrl);
                    Log.d(TAG, "doInBackground: "+stateName+"\n");

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    public class FuelPriceByCity extends AsyncTask<String,Void,String[][]>{

        @Override
        protected String[][] doInBackground(String... strings) {
            return new String[0][];
        }

        @Override
        protected void onPostExecute(String[][] strings) {
            super.onPostExecute(strings);
        }

    }

    private String correctDateFormConverter(String date){
        String[] dateMonthYear = date.split("-");
        String month = dateMonthYear[1];
        Log.d(TAG, "correctDateFormConverter: "+month);
        String correctFormat = " ";
        if (month.equals("01")){
            correctFormat = "Jan.";
        }else if(month.equals("02")){
            correctFormat = "Feb.";
        }else if(month.equals("03")){
            correctFormat = "March";
        }else if(month.equals("04")){
            correctFormat = "April";
        }else if(month.equals("05")){
            correctFormat = "May";
        }else if(month.equals("06")){
            correctFormat = "June";
        }else if(month.equals("07")){
            correctFormat = "July";
        }else if(month.equals("08")){
            correctFormat = "Aug.";
        }else if(month.equals("09")){
            correctFormat = "Sept.";
        }else if(month.equals("10")){
            correctFormat = "Oct";
        }else if(month.equals("11")){
            correctFormat = "Nov.";
        }else if(month.equals("12")){
            correctFormat = "Dec.";
        }else{
            correctFormat = "";
        }

        String returnCorrectFormat = dateMonthYear[0]+" "+correctFormat;
        return returnCorrectFormat;

    }

    private String correctFuelFormConverter(String fuel){
        String[] ppl = fuel.split(" ");
        return ppl[0] + " " + ppl[1] + " " + "Per" + " " + "Ltr.";
    }

}
