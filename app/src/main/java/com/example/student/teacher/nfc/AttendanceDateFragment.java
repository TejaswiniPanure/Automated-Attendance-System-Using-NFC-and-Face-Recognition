package com.example.student.teacher.nfc;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class AttendanceDateFragment extends Fragment implements OnFragmentInteractionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private DatePickerDialog date;

    private SimpleDateFormat dateFormatter;
    EditText etDate;
Button submit;
    ProgressDialog progressDialog;
    ListView list;
    ArrayList<String> attendanceList;
    ArrayAdapter<String> a;
    public AttendanceDateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AttendanceDateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AttendanceDateFragment newInstance(String param1, String param2) {
        AttendanceDateFragment fragment = new AttendanceDateFragment();
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
        View view = inflater.inflate(R.layout.fragment_attendance_date, container, false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        attendanceList=new ArrayList<>();

        list= (ListView) view.findViewById(R.id.list);
        a=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,attendanceList);
        list.setAdapter(a);
        submit = (Button) view.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAttendance();
            }
        });
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        etDate = (EditText) view.findViewById(R.id.date);
        etDate.setInputType(InputType.TYPE_NULL);
        etDate.requestFocus();


        setDateTimeField();


        return view;
    }

    private void setDateTimeField() {
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date.show();
            }
        });

        Calendar newCalendar = Calendar.getInstance();
        date = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                etDate.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

       /* toDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                toDateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));*/

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


    private void getAttendance(){

        progressDialog.show();

        SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE);
        final String studId=sp.getString("id","");
        Log.i("##", "## studId : " + studId);
 final String date=etDate.getText().toString();
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        sp=getActivity().getSharedPreferences("IP", getActivity().MODE_PRIVATE);
        String IP=sp.getString("IP","209.190.31.226");

        String url= "http://"+IP+ ProjectConfig.GETMYATTENDANCE;
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

                        JSONArray classAttendance=object.getJSONArray("ClassAttendance");
                        if(classAttendance.length()!=0) {
                            for (int i=0;i<classAttendance.length();i++) {
                                JSONObject attendance = classAttendance.getJSONObject(0);
                                String str = "Subject : "+attendance.getString("Subject")+"\nName : "+attendance.getString("Name")+"\nTime : "+attendance.getString("Time");
                                attendanceList.add(str);
                                a.notifyDataSetChanged();
                            }

                        }else {
                            Toast.makeText(getActivity(), "you are Not present in college", Toast.LENGTH_SHORT).show();

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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params=new HashMap<>();
                params.put("studId",studId);
                params.put("date",date);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


}
