package com.example.admin.payforst_example;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.payfort.fort.android.sdk.base.FortSdk;
import com.payfort.fort.android.sdk.base.callbacks.FortCallBackManager;
import com.payfort.fort.android.sdk.base.callbacks.FortCallback;
import com.payfort.sdk.android.dependancies.base.FortInterfaces;
import com.payfort.sdk.android.dependancies.models.FortRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class MainActivity extends Activity {

    String android_id_sdk;
    String json_payfory;

    private FortCallBackManager fortCallback
            = null;
    UserFunctions UF ;
    StringBuffer sb = new StringBuffer();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        fortCallback = FortCallback.Factory.create();

        android_id_sdk = FortSdk.getDeviceId (this);
        UF = new UserFunctions(MainActivity.this);
        Log.i("signature", android_id_sdk);
        String text = "jgjsgcyfy6rfhkfaccess_code=gUErE32CkOPj2QGLqL97device_id="+android_id_sdk+"language=enmerchant_identifier=UIxcLVrJservice_command=SDK_TOKENjgjsgcyfy6rfhkf";
        Log.e("hash key.....", text);

        MessageDigest md = null;
        try
        {
            md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes());

        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        byte byteData[] = md.digest();
       for (int i = 0; i < byteData.length; i++)
        {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }



        System.out.println("Hex format : " + sb.toString());
        new GetJson().execute();
//////////////////////////////


       // Intent i = new Intent(MainActivity.this,CreditCardResponseActivity.class);
        // startActivity(i);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fortCallback.onActivityResult(requestCode,resultCode,data);
    }
    private class GetJson extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {


                try {

                    JSONObject prms = new JSONObject();
                    JSONObject prmsLogin = new JSONObject();

                    prmsLogin.put("access_code", "gUErE32CkOPj2QGLqL97");
                    prmsLogin.put("device_id", android_id_sdk);
                    prmsLogin.put("language", "en");
                    prmsLogin.put("merchant_identifier", "UIxcLVrJ");
                    prmsLogin.put("service_command", "SDK_TOKEN");
                    prmsLogin.put("signature", sb.toString());


                    json_payfory = UF.RegisterUser("https://sbpaymentservices.payfort.com/FortAPI/paymentApi", prmsLogin);


                    Log.e("latlngJson.. prms..",  prmsLogin.toString() );
                    Log.e("latlngJson.. prms..",  json_payfory.toString() );


                } catch (JSONException e) {

                }
            } catch (Exception e) {
            }
            return json_payfory;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
             Log.e("Result", json_payfory);
            json_payfory="{"+json_payfory+"}";
            try
            {
                JSONObject jobj = new JSONObject(json_payfory);
                String apires=jobj.getString("sdk_token");
                Log.e("getquotealldata ", apires);
                Random r = new Random();
                int i1 = r.nextInt(1880 - 10) + 65;
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("amount", "1000");
                map1.put("command", "AUTHORIZATION");
                map1.put("currency", "AED");
                map1.put("customer_email", "heena.jaypal@gmail.com");
                map1.put("installments", "");
                map1.put("language", "en");
                map1.put("merchant_reference", String.valueOf(i1)+"6456456");
                map1.put("sdk_token",apires.toString() );
                map1.put("payment_option", "");
                map1.put("token_name", apires.toString());

                FortRequest g=new FortRequest();
                g.setRequestMap(map1);
                g.setShowResponsePage(true);

                try {
                    FortSdk.getInstance().registerCallback(MainActivity.this, g,FortSdk.ENVIRONMENT.TEST , 5, fortCallback, new FortInterfaces.OnTnxProcessed() {
                        @Override
                        public void onCancel(Map<String, String> requestParamsMap, Map<String, String> responseMap) {
                            Toast.makeText(getApplicationContext(),"cancle",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Map<String, String> requestParamsMap, Map<String,
                                String> fortResponseMap) {
                            Toast.makeText(getApplicationContext(),"failure",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(Map<String, String> requestParamsMap, Map<String,
                                String> fortResponseMap) {
                            Toast.makeText(getApplicationContext(),"sucess",Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e)
                {
                    e.printStackTrace();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

///live info
    /*reqtrukker2016access_code=ZbzswidlgkuYgQhgDViYdevice_id=%@language=enmerchant_identifier=GqJmbjUhservice_command=SDK_TOKENreqtrukker2016



  @"ZbzswidlgkuYgQhgDViY",@"access_code",
                                       [payFort getUDID],@"device_id",
                                       @"en",@"language",
                                       @"GqJmbjUh",@"merchant_identifier",
                                       @"SDK_TOKEN",@"service_command",
                                       [NSString stringWithFormat:@"%@",stSignature],@"signature",


https://paymentservices.payfort.com/FortAPI/paymentApi*/

}
