package android.arnab.organisationalstaff;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestPostTransaction extends StringRequest
{
    private static String REGISTER_REQUEST_URL= "http://arnabbanerjee.dx.am/RequestPostTransaction.php";
    private Map<String, String> params;
    int MY_SOCKET_TIMEOUT_MS=60000;

    public RequestPostTransaction(long id, String type,String details,String date, String time, int balance, int transAmt,
                                  Response.Listener<String> listener)
    {
        super(Request.Method.POST,REGISTER_REQUEST_URL,listener,null);
        params=new HashMap<>();
        params.put("id",id+"");
        params.put("transType",type);
        params.put("details",details);
        params.put("transDate",date);
        params.put("transTime",time);
        params.put("balance",balance+"");
        params.put("transAmt",transAmt+"");


        this.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}