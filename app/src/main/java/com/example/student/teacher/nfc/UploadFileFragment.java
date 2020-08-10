package com.example.student.teacher.nfc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.student.teacher.nfc.config.ProjectConfig;
import com.example.student.teacher.nfc.javaclass.MultipartRequest;
import com.example.student.teacher.nfc.javaclass.MySingleton;
import com.example.student.teacher.nfc.javaclass.Prof;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link UploadFileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadFileFragment extends Fragment implements OnFragmentInteractionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    int flag=0;
    private static final int FILEBROWSER_REQUEST = 1;
    private String FILEPATH;
    Button btnUploadAPk,btnBrowse;

    EditText editFilepath;
    // TextView txtResult;

    ProgressDialog progressDialog;
    Spinner spinnerStudId;
    ArrayList<Prof> studList;
    ArrayList<String> studList1;
    ArrayAdapter<String> profAdapter;
    int code;


    //  SQLiteAdapter dbhelper;
    private SharedPreferences sp;


    public UploadFileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadFileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadFileFragment newInstance(String param1, String param2) {
        UploadFileFragment fragment = new UploadFileFragment();
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
        View view= inflater.inflate(R.layout.fragment_upload_file, container, false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("uploading");
        progressDialog.setCanceledOnTouchOutside(false);
        //    dbhelper=new SQLiteAdapter(getApplicationContext());

        spinnerStudId= (Spinner) view.findViewById(R.id.spinnerStudId);

        studList=new ArrayList<>();
        studList1=new ArrayList<>();





        btnBrowse = (Button) view.findViewById(R.id.btnBrowse);
        btnUploadAPk = (Button) view.findViewById(R.id.btnUpload);
        // txtResult = (TextView) view.findViewById(R.id.txtResults);
        editFilepath = (EditText) view.findViewById(R.id.edtFilePath);


/*

        if (code==PackageConfig.UPLOAD_VIDEO){

            btnUploadAPk.setText("Upload Video To Convert");

        }else {

            btnUploadAPk.setText("Upload File For Scan");
        }

*/


        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent i = new Intent(getActivity(), FileBrowser.class);
                i.putExtra(FileBrowser.EXTRA_MODE, FileBrowser.MODE_LOAD);
                //    i.putExtra(FileBrowser.EXTRA_ALLOWED_EXTENSIONS, "txt");
                // i.putExtra(FileBrowser.EXTRA_ALLOWED_EXTENSIONS,"mp4");
                startActivityForResult(i, FILEBROWSER_REQUEST);




            }
        });


        btnUploadAPk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                Log.d("####", "file path :" + FILEPATH);


                if(flag==1){

                    flag=0;
                    uploadFile();

                } else{

                    Toast.makeText(getActivity(),"Please Select File Path ", Toast.LENGTH_SHORT).show();
                }
            }

        });

        // Creating adapter for spinner
        profAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, studList1);

        // Drop down layout style - list view with radio button
        profAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // attaching data adapter to spinner
        spinnerStudId.setAdapter(profAdapter);
        getAllStudent();
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

public void uploadFile(){
    final HashMap<String,String> params = new HashMap<String, String>();
    SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE);

    final String StudentId=studList.get(spinnerStudId.getSelectedItemPosition()).getId();
    final String ProfessorId=sp.getString("id","");
    params.put("ProfessorId",ProfessorId);
    params.put("StudentId",StudentId);



    System.out.println("#### uid  " + sp.getString("uid", ""));
    // System.out.println("#### email  "+ sp.getString("email",""));
    progressDialog.show();

    String url;


    sp=getActivity().getSharedPreferences("IP", getActivity().MODE_PRIVATE);
    String IP=sp.getString("IP","209.190.31.226");
    url = "http://"+IP+ ProjectConfig.UPLOADFILE;
    Log.d("#####","url :"+url);

    MultipartRequest multipartRequest = new MultipartRequest(url, FILEPATH, params, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

            progressDialog.dismiss();

            Log.d("#####","response :"+response);

            System.out.println("#########      response   "+response);
            response=response.substring(response.indexOf("{"),response.lastIndexOf("}")+1);
            try {
                JSONObject object = new JSONObject(response);

                if (object.getString("Status").equals("Success")){

                    editFilepath.setText("");

                    Toast.makeText(getActivity(),"File successfully uploaded", Toast.LENGTH_SHORT).show();
                }else {
                    editFilepath.setText("");
                    Toast.makeText(getActivity(),"File uploade Fail", Toast.LENGTH_SHORT).show();

                }

            } catch (JSONException e) {
                editFilepath.setText("");

                e.printStackTrace();
                Toast.makeText(getActivity(),"Error : "+e.toString() , Toast.LENGTH_SHORT).show();

            }


        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.dismiss();
            editFilepath.setText("");
            Toast.makeText(getActivity(),"Error : "+error.toString() , Toast.LENGTH_SHORT).show();

            Log.d("#####","Error  :"+error.toString());
        }
    });





    multipartRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


 //   Volley.newRequestQueue(getActivity()).add(multipartRequest);

    MySingleton.getInstance(getActivity()).addToRequestQueue(multipartRequest);

}


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){

            case FILEBROWSER_REQUEST:

                if (resultCode ==getActivity().RESULT_OK && data != null) {
                    String path = data.getExtras()
                            .getString(FileBrowser.EXTRA_PATH);

                    if (editFilepath != null) {
                        editFilepath.setText(path);

                        FILEPATH = path;
                        flag =1;
                    } else {
                        Log.d("",
                                "##the File Path dialog textview should not be null?!?");
                    }
                }

                break;




            default: super.onActivityResult(requestCode, resultCode, data);
        }





    }



    private void getAllStudent(){

        progressDialog.show();

        SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE);
        final String studId=sp.getString("id","");
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        sp=getActivity().getSharedPreferences("IP", getActivity().MODE_PRIVATE);
        String IP=sp.getString("IP","209.190.31.226");

        String url= "http://"+IP+ ProjectConfig.GETALLSTUDENT;
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
                    JSONArray jsonArray=object.getJSONArray("Students");
                    if(jsonArray.length()!=0){
                        for(int i=0;i<jsonArray.length();i++) {
                            JSONObject prof=jsonArray.getJSONObject(i);
                            studList.add(new Prof(prof.getInt("Id")+"",prof.getString("Name")));
                            studList1.add(prof.getString("Name"));
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
