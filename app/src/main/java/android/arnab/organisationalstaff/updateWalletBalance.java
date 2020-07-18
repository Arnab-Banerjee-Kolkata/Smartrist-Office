package android.arnab.organisationalstaff;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.tempos21.t21crypt.exception.CrypterException;
import com.tempos21.t21crypt.exception.DecrypterException;
import com.tempos21.t21crypt.exception.EncrypterException;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class updateWalletBalance extends AppCompatActivity
{
    static String encodedInfo="",information="";
    static Integer amount;
    static Activity activity;
    static RelativeLayout loadProcess;
    int changeInAmt=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_wallet_balance);

        activity=this;

        loadProcess=findViewById(R.id.loadProcess);
        loadProcess.setVisibility(View.VISIBLE);
        makeScreenUnresponsive();

        Bundle bundle=getIntent().getExtras();
        encodedInfo=bundle.getString("encodedInfo");
        amount=bundle.getInt("amount");


        QR qr=new QR(this,this.getResources().getString(R.string.KEY_TOKEN));
        try
        {
            information=qr.getDecryptedString(encodedInfo);
            getCandidateDetails(getApplicationContext(),information);

        }
        catch (CrypterException e) {
            e.printStackTrace();
        } catch (DecrypterException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getCandidateDetails(final Context mContext, String information)
    {
        final String fields[]=information.split("@");
        final long id=Long.parseLong(fields[0]);
        Response.Listener<String> detailsResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonResponse = null;

                if (response != null)
                {
                    try
                    {
                        jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success)
                        {
                            int activeState = jsonResponse.getInt("activeState");
                            if (activeState == 1)
                            {
                                //$Id, $personName, $grade, $department, $orgWalletVal, $activeState, $OTP

                                if (Integer.parseInt(fields[6]) == jsonResponse.getInt("OTP"))
                                {
                                    int orgBalance = jsonResponse.getInt("orgWalletVal");
                                    orgBalance = orgBalance + amount;
                                    if (orgBalance > 10000) {
                                        Toast.makeText(mContext, "Maximum wallet balance is 10,000", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        makeBalanceUpdated(updateWalletBalance.this,mContext, id,amount,
                                                orgBalance);
                                    }
                                } else {
                                    Toast.makeText(mContext, "Invalid card", Toast.LENGTH_SHORT).show();
                                    finish();
                                }


                            } else {
                                if (activeState == 0) {
                                    Toast.makeText(mContext, "Card not active. Contact office", Toast.LENGTH_SHORT).show();
                                    params.put("message", "Failed");
                                    finish();

                                }
                            }
                        } else {
                            Toast.makeText(mContext, "Invalid Id. Contact office", Toast.LENGTH_SHORT).show();
                            params.put("message", "Failed");
                            finish();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "Some error occured", Toast.LENGTH_SHORT).show();
                        params.put("message", "Failed");
                        finish();
                    }

                }
            }
        };



        String CANDIDATE_DETAILS_URL= String.format("http://arnabbanerjee.dx.am/requestCandidateDetails.php?id=%1$d",id);
        //msg.setText(CANDIDATE_DETAILS_URL);
        requestCandidateDetails requestDetails=new requestCandidateDetails(CANDIDATE_DETAILS_URL,detailsResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(requestDetails);
    }


    private void makeBalanceUpdated(final Activity mActivity, final Context mContext,final long id,final int amount,
                                    final int orgBalance)
    {
        Response.Listener<String> updateBalanceResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                try {
                    jsonResponse = new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success)
                    {
                        Toast.makeText(mContext,"Transaction Successful",Toast.LENGTH_SHORT).show();
                        updateTransaction(mContext,id,amount,orgBalance);


                        loadProcess.setVisibility(View.GONE);
                        makeWindowResponsive();
                        finish();


                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Toast.makeText(mContext,"Something went wrong",Toast.LENGTH_SHORT).show();
                    finish();
                }


            }
        };
        RequestUpdateBalance requestUpdateBalance=new RequestUpdateBalance(id,orgBalance,updateBalanceResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(requestUpdateBalance);
    }

    void updateTransaction(final Context mContext,final long id, final int transAmt, final int balance)
    {
        Response.Listener<String> postTransResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                //Toast.makeText(mContext,response,Toast.LENGTH_LONG).show();
                try {
                    jsonResponse = new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success)
                    {
                        //Toast.makeText(mContext,"Transaction Succes",Toast.LENGTH_SHORT).show();


                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    //Toast.makeText(mContext,"OTP not changed",Toast.LENGTH_SHORT).show();
                }


            }
        };

        Date cDate = new Date();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        String type="credit";
        String details="Office Deposit";

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(new Date());

        RequestPostTransaction requestPostTransaction=new RequestPostTransaction(id,type,details,date,time,balance,
                transAmt,postTransResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(requestPostTransaction);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void makeScreenUnresponsive()
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void makeWindowResponsive()
    {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}

