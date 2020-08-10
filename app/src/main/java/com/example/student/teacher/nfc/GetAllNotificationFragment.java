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
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.student.teacher.nfc.config.ProjectConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class GetAllNotificationFragment extends Fragment implements OnFragmentInteractionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
ListView list;
    ArrayAdapter<String> a;
    ProgressDialog progressDialog;
    ArrayList<String> notificationList;
    private OnFragmentInteractionListener mListener;

    public GetAllNotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GetAllNotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GetAllNotificationFragment newInstance(String param1, String param2) {
        GetAllNotificationFragment fragment = new GetAllNotificationFragment();
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

        View view=inflater.inflate(R.layout.fragment_get_all_notification, container, false);
        notificationList=new ArrayList<>();
        list= (ListView) view.findViewById(R.id.list);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        a=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,notificationList);
        list.setAdapter(a);
        getAllNotification();

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





    private void getAllNotification(){

        progressDialog.show();


        SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE);
        final String studId=sp.getString("id","");
        final String type=sp.getString("user","student");
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        sp=getActivity().getSharedPreferences("IP", getActivity().MODE_PRIVATE);
        String IP=sp.getString("IP","209.190.31.226");

        String url= "http://"+IP+ ProjectConfig.GETALLNOTIFICATION;
        url=url.replace(" ","%20");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {

                progressDialog.hide();

                jsonObject=jsonObject.substring(jsonObject.indexOf("["),jsonObject.lastIndexOf("]")+1);


                Log.i("##", "##" + jsonObject.toString());

                System.out.println("## response:" + jsonObject.toString());
                try {

                        JSONArray notification=new JSONArray(jsonObject);
                        if (notification.length()!=0){
                            for (int i=0;i<notification.length();i++) {
                                JSONObject notificationObject=notification.getJSONObject(i);

                                notificationList.add("Notification : "+notificationObject.getString("Notification")+"\n     Date : "+notificationObject.getString("Datetime"));

                            }
                            a.notifyDataSetChanged();
                        }else {
                            Toast.makeText(getActivity(), "Notification not found", Toast.LENGTH_SHORT).show();



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
                params.put("studId",studId);
                params.put("type",type);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
