package com.example.student.teacher.nfc;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.student.teacher.nfc.config.ProjectConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_Login extends ActionBarActivity {
    Dialog d;
    String fcmId;

    //Edittext declaration
    EditText edtUserName,edtPassword;
    String strUserName,strPassword;

    //login button declaration
    Button btnLogin;

    //Database sqllite declaration

    private ProgressDialog progressDialog;
    Spinner spinnerRole;
    String role;
    ArrayAdapter<String> roleAdapter;
    ArrayList<String> roleList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);
        setTitle("LOGIN");
       //username initilization
        edtUserName=(EditText)findViewById(R.id.editTextUserName);
        //Password initilization
        edtPassword=(EditText)findViewById(R.id.editTextPassword);
        spinnerRole=(Spinner) findViewById(R.id.spinnerRole);

        // //Login button initilization
        btnLogin=(Button)findViewById(R.id.buttonLogin);




        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);


        roleList=new ArrayList<>();
        roleList.add("Select Role");
        roleList.add("teacher");
        roleList.add("student");
        roleList.add("parent");

        //on click on button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get username and password from edittext and save in variable
                strUserName=edtUserName.getText().toString();
                strPassword=edtPassword.getText().toString();
                if(role!=null)
                {

                if(!strUserName.equals(""))
                {

                    if(!strPassword.equals(""))
                    {

                       checkLogin();

                    }else{
                        edtPassword.setText("");
                        edtPassword.setHint("Please Enter Password");
                        edtPassword.requestFocus();
                    }

                }else{
                    edtUserName.setText("");
                    edtUserName.setHint("Please Enter Email Id");
                    edtUserName.requestFocus();
                }
                }else{
                    Toast.makeText(Activity_Login.this, "Please Select your Role", Toast.LENGTH_SHORT).show();
                }


            }
        });




        // Creating adapter for spinner
        roleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, roleList);

        // Drop down layout style - list view with radio button
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // attaching data adapter to spinner
        spinnerRole.setAdapter(roleAdapter);

        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0){
                    role=roleList.get(position);

                } else{
                    role=null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }




    void checkLogin(){


        progressDialog.show();;


        SharedPreferences sp=getSharedPreferences("fcmID",MODE_PRIVATE);
        fcmId=sp.getString("fcmId","");
        Log.e("#####", "fcmId :" + fcmId);

        SharedPreferences sp1=getSharedPreferences("IP", MODE_PRIVATE);
        String IP=sp1.getString("IP","209.190.31.226");
        String url="http://"+IP+ ProjectConfig.LOGIN;
        // String url="http://192.168.0.132:8080/ProjectManagementSystem/rest/teacher/register";

        StringRequest stringRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                Log.e("#####", "response :" + response);
//downloading the converted video file
                       /* if(code==PackageConfig.UPLOAD_VIDEO){*/
                response=response.substring(response.indexOf("{"),response.lastIndexOf("}")+1);
                System.out.println("#########      response   " + response);
                try {
                    JSONObject object = new JSONObject(response);

                    if (object.getString("Status").equals("Success")) {

                        //show message
                        Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("isLogin", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("isLogin", "1");
                        editor.apply();
                //        {"phone":"9495546646","adminapproval":"Approved","post":"Professor","last":"a","status":"success","department":"Computer","password":"a","message":"Teacher Login Successful","email":"a@gmail.com","permanent":"a","gender":"Male","local":"a","user":"Teacher","first":"a"}

                      /*  {"Id":3,"Name":"Pramod Nawale","Address":"Katraj","ClassFId":1,"DepartmentFId":1,"MobileNumber":"1234567890",
                                "ParentEmailId":"pparent@gmail.com","ParentMobileNumber":"1234567890","Age":21,"NFCID":"1199402358","StudentPassword":"123","ParentPassword":"321"}
                      */  SharedPreferences sp = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putString("id", object.getInt("Id")+"");
                      edit.putString("Name", object.getString("Name"));
                        /*  edit.putString("Address", object.getString("Address"));
                        edit.putString("ClassFId", object.getString("ClassFId"));
                        edit.putString("DepartmentFId", object.getString("DepartmentFId"));
                        edit.putString("MobileNumber", object.getString("MobileNumber"));
                        edit.putString("ParentEmailId", object.getString("ParentEmailId"));
                        edit.putString("ParentMobileNumber", object.getString("ParentMobileNumber"));
                        edit.putString("Age", ""+object.getInt("Age"));
                        edit.putString("NFCID", object.getString("NFCID"));
                        edit.putString("ParentPassword", object.getString("ParentPassword"));
                        edit.putString("StudentPassword", object.getString("StudentPassword"));*/
                        edit.putString("user", role);

                        edit.apply();





                        //when login success then go to new activity
                        Intent i=new Intent(getApplicationContext(),HomeActivity.class);

                        startActivity(i);
                        finish();


                    }else{
                        Toast.makeText(getApplicationContext(), "Login Unsuccessfull : ", Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "Login Error : "+e.toString(), Toast.LENGTH_SHORT).show();


                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                Log.e("#####", "Error  :" + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<String, String>();
                params.put("mobile", strUserName);
                params.put("password", strPassword);
                params.put("type",role);
              //  params.put("fcmId",fcmId);



                return params;
            }
        };



        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);


    }











    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_ip:
                d = new Dialog(Activity_Login.this);
                d.setTitle("Set IP");
                d.setContentView(R.layout.dialog);
                d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                final EditText ip = (EditText) d.findViewById(R.id.ip);
                Button submit = (Button) d.findViewById(R.id.submit);
                SharedPreferences sp=getSharedPreferences("IP", MODE_PRIVATE);
                String ipStr=sp.getString("IP","209.190.31.226");
                ip.setText(ipStr);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str=ip.getText().toString();
                        SharedPreferences sp=getSharedPreferences("IP", MODE_PRIVATE);
                        SharedPreferences.Editor e=sp.edit();
                        e.putString("IP",str);
                        ///  ProjectConfig.IP=str;
                        d.dismiss();
                        e.apply();
                    }
                });
                d.show();
                //  Toast.makeText(this, "Option1", Toast.LENGTH_SHORT).show();
                return true;

        }
        return true;
    }

}
