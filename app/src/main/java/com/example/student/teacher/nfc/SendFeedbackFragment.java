package com.example.student.teacher.nfc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.student.teacher.nfc.config.ProjectConfig;
import com.example.student.teacher.nfc.javaclass.Prof;
import com.example.student.teacher.nfc.javaclass.Stud;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SendFeedbackFragment extends Fragment implements OnFragmentInteractionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button submit;
    EditText editFeedback;
    ProgressDialog progressDialog;
    Spinner spinnerProfId;
    ArrayList<Prof> profList;
    ArrayList<String> profList1;
    ArrayAdapter<String> profAdapter;
    private OnFragmentInteractionListener mListener;

    public SendFeedbackFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TeacherNotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SendFeedbackFragment newInstance(String param1, String param2) {
        SendFeedbackFragment fragment = new SendFeedbackFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_send_feedback, container, false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("uploading");
        progressDialog.setCanceledOnTouchOutside(false);
        getAllProf();

        submit= (Button) view.findViewById(R.id.btnUpload);
        editFeedback= (EditText) view.findViewById(R.id.note);
        spinnerProfId= (Spinner) view.findViewById(R.id.spinnerProfId);

        profList=new ArrayList<>();
        profList1=new ArrayList<>();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editFeedback.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Feedback Message not be Empty", Toast.LENGTH_SHORT).show();
                }else{
                    sendNotification();

                }


            }
        });

        // Creating adapter for spinner
        profAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, profList1);

        // Drop down layout style - list view with radio button
        profAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // attaching data adapter to spinner
        spinnerProfId.setAdapter(profAdapter);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }



    private void sendNotification(){

        progressDialog.show();

        final String message= editFeedback.getText().toString();

        SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE);
        final String studId=sp.getString("id","");
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        sp=getActivity().getSharedPreferences("IP", getActivity().MODE_PRIVATE);
        String IP=sp.getString("IP","209.190.31.226");

        String url= "http://"+IP+ ProjectConfig.SENDFEEDBACK;
        url=url.replace(" ","%20");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {

                progressDialog.hide();

                jsonObject=jsonObject.substring(jsonObject.indexOf("{"),jsonObject.lastIndexOf("}")+1);

                Log.i("##", "##" + jsonObject.toString());

                System.out.println("## response:" + jsonObject.toString());
                try {
                    JSONObject object=new JSONObject(jsonObject);
                    if(object.getString("Status").equals("Success")) {

                        Toast.makeText(getActivity(), "Feedback Send Successfully", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(getActivity(), "Feedback not send", Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error : "+e.toString(), Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("##", "##" + error.toString());
                Toast.makeText(getActivity(), "Error : "+error.toString(), Toast.LENGTH_SHORT).show();

                progressDialog.hide();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params=new HashMap<>();
                params.put("feedback",message);
                params.put("studId",studId);
                params.put("profid",profList.get(spinnerProfId.getSelectedItemPosition()).getId());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }




    private void getAllProf(){

        progressDialog.show();

        SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE);
        final String studId=sp.getString("id","");
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        sp=getActivity().getSharedPreferences("IP", getActivity().MODE_PRIVATE);
        String IP=sp.getString("IP","209.190.31.226");

        String url= "http://"+IP+ ProjectConfig.GETPROF;
        url=url.replace(" ","%20");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {

                progressDialog.hide();


jsonObject=jsonObject.substring(jsonObject.indexOf("{"),jsonObject.lastIndexOf("}")+1);
                Log.i("##", "##" + jsonObject.toString());

                System.out.println("## response:" + jsonObject.toString());
                try {

                    JSONObject object=new JSONObject(jsonObject);
                    JSONArray jsonArray=object.getJSONArray("Professors");
                    if(jsonArray.length()!=0){
                        for(int i=0;i<jsonArray.length();i++) {
                            JSONObject prof=jsonArray.getJSONObject(i);
                            profList.add(new Prof(prof.getInt("Id")+"",prof.getString("Name")));
                            profList1.add(prof.getString("Name"));
                            profAdapter.notifyDataSetChanged();
                        }
                    }





                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error : "+e.toString(), Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("##", "##" + error.toString());
                Toast.makeText(getActivity(), "Error : "+error.toString(), Toast.LENGTH_SHORT).show();

                progressDialog.hide();
            }
        });
        requestQueue.add(stringRequest);
    }




}
