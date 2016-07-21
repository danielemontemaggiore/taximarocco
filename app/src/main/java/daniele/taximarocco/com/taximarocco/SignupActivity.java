package daniele.taximarocco.com.taximarocco;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @Bind(R.id.input_name)
    EditText _nameText;
    @Bind(R.id.input_family_name)
    EditText _familyNameText;
    @Bind(R.id.input_license_number)
    EditText _licenseNumberText;
    @Bind(R.id.input_username)
    EditText _usernameText;
    @Bind(R.id.input_password1)
    EditText _passwordText;
    @Bind(R.id.input_confirm_password)
    EditText _confirmPasswordText;
    @Bind(R.id.input_phone_number)
    EditText _phoneNumberText;
    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.btn_signup)
    Button _signupButton;
    @Bind(R.id.link_login)
    TextView _loginLink;

    private String name, familyName, licenseNumber, username, password1, confirmPassword, phoneNumber, email ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if(!validate()){
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        signin(name, familyName, licenseNumber, username, password1, phoneNumber, email );
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        /*
        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
*/
        // TODO: Implement your own signup logic here.


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);

    }



    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        name = _nameText.getText().toString();
        familyName = _familyNameText.getText().toString();
        licenseNumber = _licenseNumberText.getText().toString();
        username = _usernameText.getText().toString();
        password1 = _passwordText.getText().toString();
        confirmPassword = _confirmPasswordText.getText().toString();
        phoneNumber = _phoneNumberText.getText().toString();
        email = _emailText.getText().toString();


        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (familyName.isEmpty() || familyName.length() < 3) {
            _familyNameText.setError("at least 3 characters");
            valid = false;
        } else {
            _familyNameText.setError(null);
        }

        if (licenseNumber.isEmpty() || licenseNumber.length() < 3) {
            _licenseNumberText.setError("at least 3 characters");
            valid = false;
        } else {
            _familyNameText.setError(null);
        }

        if (username.isEmpty() || username.length() < 3) {
            _usernameText.setError("at least 3 characters");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password1.isEmpty() || password1.length() < 4 || password1.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (confirmPassword.isEmpty() || confirmPassword.length() < 4 || confirmPassword.length() > 10) {
            _confirmPasswordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _confirmPasswordText.setError(null);
        }

        if (!password1.equals(confirmPassword)) {
            _confirmPasswordText.setError("password mismatch");
            valid = false;
        } else {
            _confirmPasswordText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }



        return valid;
    }

    private void signin(final String name,String familyName, String licenseNumber ,String username,String password1,String phoneNumber,String email) {

        class LoginAsync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(SignupActivity.this, "Please wait", "Loading...");
            }

            @Override
            protected String doInBackground(String... params) {
                String name   = params[0];
                String faname = params[1];
                String lnumb  = params[2];
                String uname  = params[3];
                String pwd    = params[4];
                String pnumb  = params[5];
                String email  = params[6];

                InputStream is = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("name", name));
                nameValuePairs.add(new BasicNameValuePair("familyName", faname));
                nameValuePairs.add(new BasicNameValuePair("licenseNumber", lnumb));
                nameValuePairs.add(new BasicNameValuePair("username", uname));
                nameValuePairs.add(new BasicNameValuePair("password", pwd));
                nameValuePairs.add(new BasicNameValuePair("phoneNumber", pnumb));
                nameValuePairs.add(new BasicNameValuePair("email", email));
                String result = null;

                try{
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(
                            "http://appalermo.altervista.org/taximarocco/signin.php");
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();

                    is = entity.getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result){
                String s = result.trim();
                loadingDialog.dismiss();
                if(s.equalsIgnoreCase("success")){
                    Intent intent = new Intent(SignupActivity.this, UserProfile.class);
                    //intent.putExtra(USER_NAME, username);
                    finish();
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_LONG).show();
                }
            }
        }

        LoginAsync la = new LoginAsync();
        la.execute(name, familyName,licenseNumber, username, password1, phoneNumber, email);

    }
}