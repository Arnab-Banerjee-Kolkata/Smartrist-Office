package android.arnab.organisationalstaff;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.tempos21.t21crypt.exception.CrypterException;
import com.tempos21.t21crypt.exception.DecrypterException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdateCardActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener
{
    Context mContext;
    static EditText nameVal,activeStateVal;
    static TextView idVal;
    TextView walletAmtVal;
    Button updateInfoBtn;
    Spinner gradeVal,deptVal;
    static String fields[];
    static TextView msg;
    static RelativeLayout load;
    static boolean isResp1=false, isResp2=false;
    ImageView updateImg;
    int grade=0;
    String dept="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_card);
        Toolbar toolbar=findViewById(R.id.toolbar3);
        toolbar.setTitle("Update Details");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mContext=getApplicationContext();

        idVal=findViewById(R.id.idVal);
        nameVal=findViewById(R.id.nameVal);
        gradeVal=findViewById(R.id.gradeVal);
        deptVal=findViewById(R.id.deptVal);
        walletAmtVal=findViewById(R.id.walletAmtVal);
        activeStateVal=findViewById(R.id.activeStateVal);
        updateInfoBtn=findViewById(R.id.updateInfoBtn);
        load=findViewById(R.id.load);
        updateImg=findViewById(R.id.updateImg);

        Glide.with(this).load(R.drawable.img1).into(updateImg);


        updateInfoBtn.setOnClickListener(this);
        gradeVal.setOnItemSelectedListener(this);
        deptVal.setOnItemSelectedListener(this);
        load.setVisibility(View.GONE);
        makeWindowResponsive();

        String[] itemyear=new String[]{"1","2","3","4"};
        ArrayAdapter<String> myadapter1 = new ArrayAdapter<String>(this,
                R.layout.spinnertext, itemyear);
        myadapter1.setDropDownViewResource(R.layout.spinnertext);
        gradeVal.setAdapter(myadapter1);
        gradeVal.setSelection(0,false);


        String[] itemstream=new String[]{"CSE","ECE","IT","BT"};
        ArrayAdapter<String> myadapter2 = new ArrayAdapter<String>(this,
                R.layout.spinnertext, itemstream);
        myadapter2.setDropDownViewResource(R.layout.spinnertext);
        deptVal.setAdapter(myadapter2);
        deptVal.setSelection(0,false);



        IntentIntegrator integrator=new IntentIntegrator(UpdateCardActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setCameraId(0);
        integrator.setOrientationLocked(false);
        integrator.setPrompt("Scanning");
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        //Toast.makeText(getApplicationContext(),"here",Toast.LENGTH_SHORT).show();

        IntentResult result=IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null && result.getContents()!=null)
        {
            String information=result.getContents();
            QR qr=new QR(mContext,this.getResources().getString(R.string.KEY_TOKEN));
            try {
                information=qr.getDecryptedString(information);
                getCandidateDetails(mContext,information);
                load.setVisibility(View.VISIBLE);
                makeScreenUnresponsive();

            } catch (CrypterException e)
            {
                e.printStackTrace();
                Toast.makeText(mContext,"Invalid card",Toast.LENGTH_SHORT).show();
            } catch (DecrypterException e)
            {
                e.printStackTrace();
                Toast.makeText(mContext,"Invalid card",Toast.LENGTH_SHORT).show();
            }


        }
        if(resultCode==0)
        {
            finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getCandidateDetails(final Context mContext, String information)
    {
        fields=information.split("@");
        final long id=Long.parseLong(fields[0]);
        Response.Listener<String> detailsResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                isResp1=true;
                Map<String, String> params = new HashMap<>();
                JSONObject jsonResponse = null;

                if (response != null)
                {
                    try
                    {
                        jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            int activeState = jsonResponse.getInt("activeState");
                            if (activeState == 1) {
                                //$Id, $personName, $grade, $department, $orgWalletVal, $activeState, $OTP

                                if (Integer.parseInt(fields[6]) == jsonResponse.getInt("OTP"))
                                {
                                    load.setVisibility(View.GONE);
                                    makeWindowResponsive();
                                    idVal.setText(Long.toString(jsonResponse.getLong("id")));
                                    nameVal.setText(jsonResponse.getString("personName"));

                                    String temp1=jsonResponse.getString("department");
                                    int temp2=jsonResponse.getInt("grade");

                                    if(temp1.equalsIgnoreCase("cse")) {
                                        deptVal.setSelection(0);
                                        dept="cse";
                                    }
                                    else if(temp1.equalsIgnoreCase("ece")) {
                                        deptVal.setSelection(1);
                                        dept="ece";
                                    }
                                    else if(temp1.equalsIgnoreCase("it")) {
                                        deptVal.setSelection(2);
                                        dept="it";
                                    }
                                    else {
                                        deptVal.setSelection(3);
                                        dept="bt";
                                    }

                                    gradeVal.setSelection(temp2-1);
                                    grade=temp2;
                                    walletAmtVal.setText(Integer.toString(jsonResponse.getInt("orgWalletVal")));
                                    activeStateVal.setText(Integer.toString(jsonResponse.getInt("activeState")));



                                }
                                else
                                {
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

    @Override
    public void onClick(View v)
    {
        if(v.equals(updateInfoBtn))
        {
            fields[0]=idVal.getText().toString();
            fields[1]=nameVal.getText().toString();
            fields[2]=Integer.toString(grade);
            fields[3]=dept;
            fields[4]=walletAmtVal.getText().toString();
            fields[5]=activeStateVal.getText().toString();
            if(!(fields[0].equals("") || fields[1].equals("") || fields[2].equals("") || fields[3].equals("") ||
                    fields[4].equals("") || fields[5].equals("")))
            {
                long id = Long.parseLong(fields[0]);
                String name = fields[1];
                int grade = Integer.parseInt(fields[2]);
                String department = fields[3];
                int orgWalletVal = Integer.parseInt(fields[4]);
                int activeState = Integer.parseInt(fields[5]);
                if(activeState<=1 && orgWalletVal<=10000)
                {
                    updateValues(mContext,id,name,grade,department,orgWalletVal,activeState);
                    load.setVisibility(View.VISIBLE);
                }
                else
                {
                    Toast.makeText(mContext,"Invalid values entered",Toast.LENGTH_SHORT).show();
                }


            }
            else
            {
                Toast.makeText(mContext,"Enter all fields",Toast.LENGTH_SHORT).show();
            }

        }

    }

    void updateValues(final Context mContext, long id, String name, int grade, String department, int orgWalletVal, int activeState)
    {
        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                isResp2=true;
                //msg.setText(response);
                try {
                    JSONObject jsonResponse=new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success)
                    {
                        Toast.makeText(mContext,"Updated successfully",Toast.LENGTH_SHORT).show();
                        load.setVisibility(View.GONE);
                        finish();
                    }
                    else {
                        Toast.makeText(mContext,"Some error occured",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(mContext,OfficeWelcome.class);
                        UpdateCardActivity.this.startActivity(intent);
                        finish();
                        }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext,"Some error occured",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(mContext,OfficeWelcome.class);
                    UpdateCardActivity.this.startActivity(intent);
                    finish();
                }

            }
        };
        UpdateCandidateDetails updateCandidateDetails=new UpdateCandidateDetails(id,name,grade,department,orgWalletVal,activeState,responseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(updateCandidateDetails);
    }

    @Override
    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        if(parent.equals(gradeVal))
        {
            grade=position+1;
        }
        else if(parent.equals(deptVal))
        {
            switch (position)
            {
                case 0:
                    dept="cse";
                    break;
                case 1:
                    dept="ece";
                    break;
                case 2:
                    dept="it";
                    break;
                case 3:
                    dept="bt";
                    break;
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
