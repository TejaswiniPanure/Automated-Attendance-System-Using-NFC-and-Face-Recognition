package com.example.student.teacher.nfc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class UploadedFileFragment extends Fragment implements OnFragmentInteractionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int BUFFER_SIZE = 4096;
String fileName;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
ListView list;
    ArrayAdapter<String> a;
    ArrayList<String> fileList;
    private OnFragmentInteractionListener mListener;
ProgressDialog progressDialog;
    public UploadedFileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadedFileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadedFileFragment newInstance(String param1, String param2) {
        UploadedFileFragment fragment = new UploadedFileFragment();
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
        View view= inflater.inflate(R.layout.fragment_uploaded_file, container, false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        list= (ListView) view.findViewById(R.id.list);
fileList=new ArrayList<>();
        a=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,fileList);

list.setAdapter(a);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fileName=fileList.get(position);

                SharedPreferences sp=getActivity().getSharedPreferences("IP", getActivity().MODE_PRIVATE);
                String IP=sp.getString("IP","209.190.31.226");

                        String fileUrl = "http://"+IP+ProjectConfig.URL1 + "Files/Students/" + fileName;
                System.out.println("############ fileUrl =  "+fileUrl);
                new DownloadVideo().execute(fileUrl);
            }
        });
        getAllFiles();
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




    private void getAllFiles(){

        progressDialog.show();

        SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE);
        final String studId=sp.getString("id","");
        final String role=sp.getString("user","");

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        sp=getActivity().getSharedPreferences("IP", getActivity().MODE_PRIVATE);
        String IP=sp.getString("IP","209.190.31.226");

        String url= "http://"+IP+ ProjectConfig.GETDOC;
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

                        JSONArray doc=object.getJSONArray("Documents");
                        if (doc.length()!=0){
                            for (int i=0;i<doc.length();i++) {
                                JSONObject docObject=doc.getJSONObject(i);

                                fileList.add(docObject.getString("Document"));

                            }
                            a.notifyDataSetChanged();
                        }else {
                            Toast.makeText(getActivity(), "File not found", Toast.LENGTH_SHORT).show();

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
                params.put("StudentId",studId);
                params.put("Type",role);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class DownloadVideo extends AsyncTask<String,Integer,Integer> {
        File outputfile,fName;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Downloading file....");
            progressDialog.show();


            outputfile = new File(Environment.getExternalStorageDirectory()+"/Download");
            if(!outputfile.exists())
            {

                outputfile.mkdir();

            }




        }

        @Override
        protected Integer doInBackground(String... params) {
            int res = 0;
            String url = params[0];
            fName = new File(outputfile+"/"+fileName);

            if (!fName.exists()){

                try {
                    fName.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                res=  downloadFile(url,fName.getAbsolutePath());


            } catch (IOException e) {
                e.printStackTrace();
            }


            return res;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            progressDialog.dismiss();
            if (integer==0){

                Toast.makeText(getActivity(), "Download failed", Toast.LENGTH_SHORT).show();

            }else {

                Toast.makeText(getActivity(),"Download Completed and saved to "+outputfile.getAbsolutePath().toString(),Toast.LENGTH_LONG).show();
                // deleteFile(email, fileName);

            }

        }
    }


    public  int downloadFile(String fileURL, String saveDir)
            throws IOException {


        String encodedURL=java.net.URLEncoder.encode(fileURL,"UTF-8");


        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }

            System.out.println("## Content-Type = " + contentType);
            System.out.println("##  Content-Disposition = " + disposition);
            System.out.println("## Content-Length = " + contentLength);
            System.out.println("## fileName = " + fileName);

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            //String saveFilePath = saveDir + File.separator + fileName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveDir);

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            System.out.println("## File downloaded");
            return 1;
        } else {

            System.out.println("## No file to download. Server replied HTTP code: " + responseCode);
            httpConn.disconnect();
            return 0;
        }


    }
}
