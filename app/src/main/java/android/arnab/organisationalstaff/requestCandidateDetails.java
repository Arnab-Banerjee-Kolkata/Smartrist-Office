package android.arnab.organisationalstaff;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class requestCandidateDetails extends StringRequest
{

    public  requestCandidateDetails(String URL, Response.Listener<String> listener)
    {
        super(Method.GET,URL,listener,null);
    }
}