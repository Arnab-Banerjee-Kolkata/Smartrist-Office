package android.arnab.organisationalstaff;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.tempos21.t21crypt.exception.CrypterException;
import com.tempos21.t21crypt.exception.DecrypterException;

public class updateAmount extends AppCompatActivity implements View.OnClickListener
{
    EditText amount;
    Button proceed,cancelBtn;
    String KEY_TOKEN;
    ImageView updateBalanceBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_amount);
        Toolbar toolbar=findViewById(R.id.toolbar2);
        toolbar.setTitle("Update Balance");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        KEY_TOKEN=this.getResources().getString(R.string.KEY_TOKEN);


        amount=findViewById(R.id.amount);
        proceed=findViewById(R.id.proceed);
        cancelBtn=findViewById(R.id.cancelBtn);
        updateBalanceBg=findViewById(R.id.updateBalanceBg);

        proceed.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        int img1=R.drawable.img1;

        Glide.with(getApplicationContext()).load(img1).into(updateBalanceBg);

    }

    @Override
    public void onClick(View v)
    {
        if(v.equals(proceed))
        {
            if(amount.getText().toString().equals("") || amount.getText().toString()==null)
            {
                Toast.makeText(getApplicationContext(),"Enter amount",Toast.LENGTH_SHORT).show();
            }
            else
            {
                int amt=Integer.parseInt(amount.getText().toString());
                if(amt<=0)
                    Toast.makeText(getApplicationContext(),"Amount must be greater than 0",Toast.LENGTH_SHORT).show();
                else
                {
                    IntentIntegrator integrator=new IntentIntegrator(updateAmount.this);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                    integrator.setCameraId(0);
                    integrator.setOrientationLocked(false);
                    integrator.setPrompt("Scanning");
                    integrator.setBeepEnabled(true);
                    integrator.setBarcodeImageEnabled(true);
                    integrator.initiateScan();

                }
            }
        }
        else if(v.equals(cancelBtn))
        {
            Intent intent=new Intent(getApplicationContext(),OfficeWelcome.class);
            startActivity(intent);
            finish();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult result=IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null && result.getContents()!=null)
        {
            Intent intent=new Intent(getApplicationContext(),updateWalletBalance.class);
            intent.putExtra("encodedInfo",result.getContents());
            intent.putExtra("amount",Integer.parseInt(amount.getText().toString()));
            startActivity(intent);
            this.finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
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
