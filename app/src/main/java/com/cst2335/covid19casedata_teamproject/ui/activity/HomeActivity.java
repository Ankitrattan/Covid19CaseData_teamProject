package com.cst2335.covid19casedata_teamproject.ui.activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Toast;

import com.cst2335.covid19casedata_teamproject.R;
import com.cst2335.covid19casedata_teamproject.data.CaseData;
import com.cst2335.covid19casedata_teamproject.databinding.ActivityHomeBinding;
import com.cst2335.covid19casedata_teamproject.db.DatabaseHelper;
import com.cst2335.covid19casedata_teamproject.utils.NetworkCheck;
import com.cst2335.covid19casedata_teamproject.utils.PreferencesManager;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.yariksoffice.lingver.Lingver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding mBinding;
    private String mFromDate, mToDate;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private SimpleDateFormat mUIDateFormat, mServerDateFormat;
    private ArrayList<CaseData> mCaseDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mBinding.drawerLayout, R.string.nav_open, R.string.nav_close);
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        mBinding.drawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUIDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        mServerDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());

        mBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mBinding.edtCountry.getText())) {
                    Toast.makeText(HomeActivity.this, "Enter Country", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mBinding.edtFromDate.getText())) {
                    Toast.makeText(HomeActivity.this, "Choose From Date", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mBinding.edtToDate.getText())) {
                    Toast.makeText(HomeActivity.this, "Choose To Date", Toast.LENGTH_SHORT).show();
                } else {
                    if (new NetworkCheck().connectivityCheck(HomeActivity.this)) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        new GetCovid19CaseData().execute(String.format(getString(R.string.url), mBinding.edtCountry.getText().toString().trim(), mFromDate, mToDate));
                    } else {
                        Toast.makeText(HomeActivity.this, "No Internet connection", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        mBinding.edtFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(1);
            }
        });

        mBinding.edtToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(2);
            }
        });

        mBinding.cardRecentSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferencesManager preferencesManager = new PreferencesManager(HomeActivity.this);
                mBinding.edtCountry.setText(preferencesManager.getStringValue("country"));
                mBinding.edtFromDate.setText(preferencesManager.getStringValue("from_date"));
                mBinding.edtToDate.setText(preferencesManager.getStringValue("to_date"));
            }
        });

        mBinding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.action_list){
                    Intent intent = new Intent(HomeActivity.this, CaseDataListActivity.class);
                    intent.putExtra("flag", "saved");
                    startActivity(intent);
                    mBinding.drawerLayout.closeDrawer(GravityCompat.START);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        setRecentSearchFromPreferences();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == R.id.action_list) {
            Intent intent = new Intent(HomeActivity.this, CaseDataListActivity.class);
            intent.putExtra("flag", "saved");
            startActivity(intent);
        } else if (item.getItemId() == R.id.action_help) {
            AlertDialog.Builder helpDialog = new AlertDialog.Builder(this);
            helpDialog.setTitle(R.string.help);
            helpDialog.setMessage(R.string.home_help_message);
            helpDialog.setPositiveButton("Ok", null);
            helpDialog.show();
        } else if(item.getItemId() == R.id.action_language){
            showLanguageChangeDialog();
        }
        return true;
    }

    private void openDatePicker(int type) { // 1 -> from date 2 -> To Date
        final Calendar newCalendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                if (type == 1) { // from date
                    mBinding.edtFromDate.setText(mUIDateFormat.format(newDate.getTime()));
                    mFromDate = mServerDateFormat.format(newDate.getTime());
                } else { // to date
                    mBinding.edtToDate.setText(mUIDateFormat.format(newDate.getTime()));
                    mToDate = mServerDateFormat.format(newDate.getTime());
                }
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(newCalendar.getTimeInMillis());
        datePickerDialog.show();
    }

    public class GetCovid19CaseData extends AsyncTask<String, Void, String> {
        private ProgressDialog mProgressDialog;
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        public GetCovid19CaseData() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(HomeActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Fetching Details Please Wait !!!");
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String stringUrl = params[0];
            String result;
            String inputLine;
            try {
                Log.i("url", stringUrl);
                //Create a URL object holding our url
                URL myUrl = new URL(stringUrl);
                //Create a connection
                HttpURLConnection connection = (HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
                result = parseAndInsertToDB(result);
            } catch (IOException e) {
                e.printStackTrace();
                result = null;
            }
            return result;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            if (result != null) {
                if (result.equalsIgnoreCase("Data Added Successfully")) {
                    saveDateinPreferences();
                    Intent listIntent = new Intent(HomeActivity.this, CaseDataListActivity.class);
                    listIntent.putExtra("flag", "search");
                    listIntent.putExtra("list", mCaseDataList);
                    listIntent.putExtra("country", mBinding.edtCountry.getText().toString());
                    listIntent.putExtra("fromDate", mBinding.edtFromDate.getText().toString());
                    listIntent.putExtra("toDate", mBinding.edtToDate.getText().toString());
                    startActivity(listIntent);
                }
            } else {
                try {
                    Snackbar.make(mBinding.getRoot(), "No Response", Snackbar.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mBinding.edtCountry.setText("");
            mBinding.edtFromDate.setText("");
            mBinding.edtToDate.setText("");

        }
    }

    private void saveDateinPreferences() {
        PreferencesManager preferencesManager = new PreferencesManager(this);
        preferencesManager.setStringValue("country", mBinding.edtCountry.getText().toString());
        preferencesManager.setStringValue("from_date", mBinding.edtFromDate.getText().toString());
        preferencesManager.setStringValue("to_date", mBinding.edtToDate.getText().toString());
        preferencesManager.setStringValue("server_from_date", mFromDate);
        preferencesManager.setStringValue("server_to_date", mToDate);

    }

    private void setRecentSearchFromPreferences() {
        PreferencesManager preferencesManager = new PreferencesManager(this);
        mBinding.txtRecentSearchCountry.setText(String.format(getString(R.string._country_value),preferencesManager.getStringValue("country")));
        mBinding.txtRecentSearchFromDate.setText(String.format(getString(R.string._country_value),preferencesManager.getStringValue("from_date")));
        mBinding.txtRecentSearchToDate.setText(String.format(getString(R.string._country_value),preferencesManager.getStringValue("to_date")));
        mFromDate = preferencesManager.getStringValue("server_from_date");
        mToDate = preferencesManager.getStringValue("server_to_date");
    }

    private String parseAndInsertToDB(String result) {
        if (result != null) {
            try {
                Log.i("result", result);
                mCaseDataList = new ArrayList<>();
                JSONArray resultsArray = new JSONArray(result);
                if (resultsArray.length() > 0) {
                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject resultObj = resultsArray.getJSONObject(i);
                        CaseData caseData = new CaseData();
                        caseData.setCountry(resultObj.optString("Country", ""));
                        caseData.setProvince(resultObj.optString("Province", ""));
                        caseData.setCount(resultObj.optInt("Cases", 0));
                        caseData.setDate(mUIDateFormat.format(mServerDateFormat.parse(resultObj.optString("Date", ""))));
                        mCaseDataList.add(caseData);
                    }
                    insertDataToDB(mCaseDataList);
                    return "Data Added Successfully";
                } else {
                    return "No records";
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Invalid Response";
            }

        } else {
            return "No Response";
        }
    }

    private void insertDataToDB(ArrayList<CaseData> caseDataList) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        for (CaseData caseData : caseDataList) {
            long status = databaseHelper.addCaseData(caseData);
            Log.i("status", status + "");
        }
    }

    private void showLanguageChangeDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        final View customLayout = getLayoutInflater().inflate(R.layout.dialig_language_selection, null);
        RadioButton mEnglishRBtn = customLayout.findViewById(R.id.txt_english);
        RadioButton mFrenchRBtn = customLayout.findViewById(R.id.txt_french);
        AlertDialog alert = null;
        alertDialog.setView(customLayout);
        alert = alertDialog.create();
        AlertDialog finalAlert = alert;
        customLayout.findViewById(R.id.btn_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mEnglishRBtn.isChecked()){
                    Lingver.getInstance().setLocale(HomeActivity.this, "en");
                }if (mFrenchRBtn.isChecked()){
                    Lingver.getInstance().setLocale(HomeActivity.this, "fr");
                }
                finalAlert.dismiss();
                startActivity(new Intent(HomeActivity.this,HomeActivity.class));
                finish();
            }
        });
        alert.show();
    }

}