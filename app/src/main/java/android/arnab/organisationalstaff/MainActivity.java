package android.arnab.organisationalstaff;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Smartrist : Office");
        int resid = R.drawable.img1;
        ImageView imageView1 = findViewById(R.id.imageView);
        ImageView iconImg=findViewById(R.id.iconImg);
        Button bcred=findViewById(R.id.creditb);
        bcred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Credits.class);
                startActivity(intent);
            }
        });
        Glide
                .with(this)
                .load(resid).into(imageView1);

        Glide.with(this).load(R.drawable.ofc).into(iconImg);

        Button b=findViewById(R.id.student);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(),OfficeWelcome.class);
                startActivity(intent);
            }
        });
        Button b2=findViewById(R.id.wallet);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),updateAmount.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
