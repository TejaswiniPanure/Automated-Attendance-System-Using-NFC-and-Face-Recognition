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
import android.widget.ListView;
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
import com.example.student.teacher.nfc.javaclass.Stud;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AddMarksFragment extends Fragment implements OnFragmentInteractionListener {

    private OnFragmentInteractionListener mListener;
    Spinner spinnerStudId;
    EditText student_marks;
    Button send_button;
    ArrayList<String> marksList;
    ProgressDialog progressDialog;
    ArrayAdapter<String> a;
    String str_strudent_marks;
    ArrayAdapter<String> studAdapter;
    ArrayList<Stud> studList;
    ArrayList<String> studList1;

    public AddMarksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MarksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddMarksFragment newInstance(String param1, String param2) {
        AddMarksFragment fragment = new AddMarksFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_addmarks, container, false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Wait");
        progressDialog.setCanceledOnTouchOutside(false);


        send_button = (Button) view.findViewById(R.id.send_button);
        spinnerStudId = (Spinner) view.findViewById(R.id.spinnerStudId);
        student_marks = (EditText) view.findViewById(R.id.student_marks);

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_strudent_marks = student_marks.getText().toString();
                if (str_strudent_marks.trim().length() !=0) {
                    addMarks();
                }else {
                    Toast.makeText(getContext(), "Enter Marks", Toast.LENGTH_SHORT).show();
                }
            }
        });

        studList1 = new ArrayList<>();
        // Creating adapter for spinner

        getAllStud();

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

    private void addMarks(){

        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        SharedPreferences sps = getActivity().getApplicationContext().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE);
        final String profId=sps.getString("id","");

        SharedPreferences sp=getActivity().getSharedPreferences("IP", getActivity().MODE_PRIVATE);
        String IP=sp.getString("IP","209.190.31.226");

        String url= "http://"+IP+ ProjectConfig.addMarksForStudent;
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
                    if (object.getString("Status").equals("Success")){
                        student_marks.setText("");
                        Toast.makeText(getContext(), "Marks Updated Success", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(), "Error "+object.getString("Message"), Toast.LENGTH_SHORT).show();
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
                params.put("marks",str_strudent_marks);
                params.put("profid",profId);
                params.put("studId",studList.get(spinnerStudId.getSelectedItemPosition()).getId());
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }


    private void getAllStud(){

        progressDialog.show();


        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        SharedPreferences sp=getActivity().getSharedPreferences("IP", getActivity().MODE_PRIVATE);
        String IP=sp.getString("IP","209.190.31.226");

        String url= "http://"+IP+ ProjectConfig.GETALLSTUDENT;
        url=url.replace(" ","%20");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {

                progressDialog.hide();

                jsonObject=jsonObject.substring(jsonObject.indexOf("{"),jsonObject.lastIndexOf("}")+1);


                Log.i("##", "##" + jsonObject.toString());

                System.out.println("## ##:" + jsonObject.toString());
                try {
                    studList = new ArrayList<>();
                    studList1 = new ArrayList<>();
                    JSONObject object=new JSONObject(jsonObject);
                    JSONArray jsonArray=object.getJSONArray("Students");
                    Log.e("####"," Student array size "+jsonArray.length());


                    if (jsonArray.length()!=0) {
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject studObject=jsonArray.getJSONObject(i);
                            studList.add(new Stud(studObject.getString("Id"),studObject.getString("Name")));
                            studList1.add(studObject.getString("Name"));
//                            studAdapter.notifyDataSetChanged();

                            Log.e("#####"," Student Nmae "+studObject.getString("Name")+" and ID "+studObject.getString("Id")+" list size is "+studList1.size());

                        }

                        studAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, studList1);

                        // Drop down layout style - list view with radio button
                        studAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // attaching data adapter to spinner
                        spinnerStudId.setAdapter(studAdapter);

                    }else {
                        Toast.makeText(getActivity(), "No student Available", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("###"," ERRROR "+e.getMessage());
                    Toast.makeText(getActivity(), "Error : "+e.toString(), Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("##", "##" + error.toString());
                Toast.makeText(getActivity(), "Error : "+error.toString(), Toast.LENGTH_SHORT).show();

                progressDialog.hide();
            }
        });
        requestQueue.add(stringRequest);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
