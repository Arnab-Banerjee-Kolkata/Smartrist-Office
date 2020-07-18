package android.arnab.organisationalstaff;

import android.content.Context;
import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OfficeWelcome extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener
{
    EditText idNum, name, balance;
    Button createCard, updateCard;
    ImageView studentBg;
    RelativeLayout regWait;
    Spinner grade, dept;
    String stream="",year="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_welcome);
        studentBg=findViewById(R.id.studentBg);
        Glide.with(getApplicationContext()).load(this.getResources().getDrawable(R.drawable.img1)).into(studentBg);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Student's Section");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        idNum=findViewById(R.id.idNum);
        name=findViewById(R.id.name);
        grade=findViewById(R.id.year);
        dept=findViewById(R.id.stream);
        balance=findViewById(R.id.balance);
        createCard=findViewById(R.id.createCard);
        updateCard=findViewById(R.id.updateCard);
        regWait=findViewById(R.id.regWait);

        updateCard.setOnClickListener(this);
        createCard.setOnClickListener(this);

        grade.setOnItemSelectedListener(this);
        dept.setOnItemSelectedListener(this);



        regWait.setVisibility(View.GONE);
        makeWindowResponsive();


        String[] itemyear=new String[]{"Year*","1","2","3","4"};
        ArrayAdapter<String> myadapter1 = new ArrayAdapter<String>(this,
                R.layout.spinnertext, itemyear);
        myadapter1.setDropDownViewResource(R.layout.spinnertext);
        grade.setAdapter(myadapter1);
        grade.setSelection(0,false);


        String[] itemstream=new String[]{"Stream*","CSE","ECE","IT","BT"};
        ArrayAdapter<String> myadapter2 = new ArrayAdapter<String>(this,
                R.layout.spinnertext, itemstream);
        myadapter2.setDropDownViewResource(R.layout.spinnertext);
        dept.setAdapter(myadapter2);
        dept.setSelection(0,false);
    }

    @Override
    public void onClick(View v)
    {
        if(v.equals(createCard))
        {
            final long idNum;
            String name,dept;
            final int grade,balance;
            String info[]=new String[5];
            info[0]=this.idNum.getText().toString();
            info[1]=this.name.getText().toString();
            info[2]=year;
            info[3]=stream;
            info[4]=this.balance.getText().toString();
            String message="";
            if(info[0].equals("") || info[1].equals("") || info[2].equals("") ||
                    info[3].equals("") || info[4].equals("") || info[0]==null || info[1]==null ||
                    info[2]==null || info[3]==null || info[4]==null)
            {
               Toast.makeText(getApplicationContext(),"Please enter all * marked fields",Toast.LENGTH_SHORT).show();
            }
            else if(Integer.parseInt(info[4])>10000 || Integer.parseInt(info[4])<=0)
            {
                Toast.makeText(getApplicationContext(),"Wallet balance should be between 1 and 10000 inclusive",Toast.LENGTH_LONG).show();
            }
            else
            {
                regWait.setVisibility(View.VISIBLE);
                makeScreenUnresponsive();
                idNum=Long.parseLong(info[0]);
                name=info[1];
                grade=Integer.parseInt(info[2]);
                dept=info[3];
                balance=Integer.parseInt(info[4]);

                Response.Listener<String> responseListener=new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        regWait.setVisibility(View.GONE);
                        makeWindowResponsive();
                        //msg.setText(response);
                        try {
                            JSONObject jsonResponse=new JSONObject(response);
                            boolean success=jsonResponse.getBoolean("success");
                            if(success)
                            {
                                updateTransaction(getApplicationContext(),idNum,balance,balance);
                                Toast.makeText(getApplicationContext(),"Card created",Toast.LENGTH_SHORT).show();
                                finish();
//                                overridePendingTransition(0, 0);
//                                startActivity(getIntent());
//                                overridePendingTransition(0, 0);
                            }
                            else {
                                Toast.makeText(getApplicationContext(),"Somme erroor occured",Toast.LENGTH_SHORT).show();
                                if(response.contains("card exists")) {
                                    Toast.makeText(getApplicationContext(),"Card exists",Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            if(response.contains("card exists")) {
                                Toast.makeText(getApplicationContext(),"Card exists",Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                };
                RegisterRequest registerRequest=new RegisterRequest(idNum,name,grade,dept,balance,1,1234,responseListener);
                RequestQueue queue=Volley.newRequestQueue(OfficeWelcome.this);
                queue.add(registerRequest);
            }
        }
        else if(v.equals(updateCard))
        {
            Intent intent=new Intent(getApplicationContext(),UpdateCardActivity.class);
            startActivity(intent);
            this.finish();
        }
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        if(parent.equals(grade))
        {
            switch (position)
            {
                case 0:
                    year="";
                    break;
                case 1:
                    year="1";
                    break;
                case 2:
                    year="2";
                    break;
                case 3:
                    year="3";
                    break;
                case 4:
                    year="4";
                    break;
            }
        }
        else if(parent.equals(dept))
        {
            switch (position)
            {
                case 0:
                    stream="";
                    break;
                case 1:
                    stream="cse";
                    break;
                case 2:
                    stream="ece";
                    break;
                case 3:
                    stream="it";
                    break;
                case 4:
                    stream="bt";
                    break;
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

    void updateTransaction(final Context mContext, final long id, final int transAmt, final int balance)
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
        String details="Opening Amount";

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(new Date());

        RequestPostTransaction requestPostTransaction=new RequestPostTransaction(id,type,details,date,time,balance,
                transAmt,postTransResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(requestPostTransaction);
    }

}
