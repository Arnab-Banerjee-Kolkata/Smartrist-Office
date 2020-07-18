package android.arnab.organisationalstaff;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UpdateCandidateDetails extends StringRequest
{
    private static String REGISTER_REQUEST_URL= "http://arnabbanerjee.dx.am/updateCandidateDetails.php";
    private Map<String, String> params;
    int MY_SOCKET_TIMEOUT_MS=60000;

    public UpdateCandidateDetails(long id, String personName, int grade, String department,
                           int orgWalletVal, int activeState, Response.Listener<String> listener)
    {
        super(Method.POST,REGISTER_REQUEST_URL,listener,null);
        params=new HashMap<>();
        params.put("id",id+"");
        params.put("personName",personName);
        params.put("grade",grade+"");
        params.put("department",department);
        params.put("orgWalletVal",orgWalletVal+"");
        params.put("activeState",activeState+"");

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
