package com.mifashow;

import java.net.URLEncoder;

import org.apache.http.Header;
import org.apache.http.HttpStatus;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mifashow.data.Constance;
import com.mifashow.domain.User;
import com.mifashow.tool.AlertHelper;
import com.mifashow.tool.TextLengthInputFilter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
//	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private App app;
	private String s_sign;
	private String s_password;

	// UI references.
	private AutoCompleteTextView  et_sign;
	private EditText et_password;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app=(App) getApplication();

		setContentView(R.layout.activity_login);
		setupActionBar();


		et_sign = (AutoCompleteTextView ) findViewById(R.id.login_et_sign);
		et_sign.setThreshold(1);
//		et_sign.setFilters(new InputFilter[]{new TextLengthInputFilter(15),new LoginFilter.UsernameFilterGMail()});
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, app.getLoginSigns());
		et_sign.setAdapter(adapter);
		et_password = (EditText) findViewById(R.id.login_et_password);
		et_password.setFilters(new InputFilter[]{new TextLengthInputFilter(15),new LoginFilter.PasswordFilterGMail()});
		et_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.login_bt_login).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		findViewById(R.id.login_bt_findPassword).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						findPassword();
					}
				});
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// TODO: If Settings has multiple levels, Up should navigate up
			// that hierarchy.
//			NavUtils.navigateUpFromSameTask(this);
			Intent intent=new Intent();
	    	intent.setClass(this, MainActivity.class);
	    	this.startActivity(intent);
	    	LoginActivity.this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void findPassword(){
		s_sign = et_sign.getText().toString();
		et_sign.setError(null);
		et_password.setError(null);
		if (TextUtils.isEmpty(s_sign) || !s_sign.matches("\\w+@\\w+\\.\\w+")) {
			et_sign.setError(getString(R.string.err_signInvalid));
			et_sign.requestFocus();
		} else{
			AsyncHttpClient client = new AsyncHttpClient();
			String encodedSign = null;
			try{
				encodedSign=URLEncoder.encode(s_sign,"utf-8");
				client.get(Constance.SERVER_URL+"password/"+encodedSign, new AsyncHttpResponseHandler(){
					@Override
					   public void onSuccess(int statusCode,Header[] headers,byte[] content) {
						Log.d("-loginActivity","findPassword:success");
						AlertHelper.showToast(LoginActivity.this, getResources().getString(R.string.info_findPasswordDone,s_sign));
					   }
					@Override
					public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
						Log.d("-loginActivity","findPassword:failure:"+error.getMessage()+",httpstatus="+statusCode);
						AlertHelper.showToast(LoginActivity.this, R.string.error_serverError);
					}
				});
			}catch(Exception e){}
		}
	}
	
	public void attemptLogin() {
//		if (mAuthTask != null) {
//			return;
//		}

		// Reset errors.
		et_sign.setError(null);
		et_password.setError(null);

		// Store values at the time of the login attempt.
		s_sign = et_sign.getText().toString();
		s_password = et_password.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(s_password)) {
			et_password.setError(getString(R.string.login_err_null_password));
			focusView = et_password;
			cancel = true;
		} else if (s_password.length() < 6) {
			et_password.setError(getString(R.string.login_err_length_password));
			focusView = et_password;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(s_sign)) {
			et_sign.setError(getString(R.string.login_err_null_sign));
			focusView = et_sign;
			cancel = true;
		} 
//		else if (s_userName.length()<5||s_userName.length()>11) {
//			et_userName.setError(getString(R.string.login_err_length_userName));
//			focusView = et_userName;
//			cancel = true;
//		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText("");
			showProgress(true);
			AsyncHttpClient client = new AsyncHttpClient();
			Log.d("-loginActivity","login:url="+Constance.SERVER_URL+"user"+",s_sign="+s_sign+",s_password:"+s_password);
			client.setBasicAuth(s_sign, s_password);
//			RequestParams params=new RequestParams();
//			params.put("sign", s_sign);
//			params.put("password", s_password);
			client.get(Constance.SERVER_URL+"user", new AsyncHttpResponseHandler(){
				@Override
				   public void onSuccess(int statusCode,Header[] headers,byte[] content) {
					Log.d("-loginActivity","login:success");
					User user=new Gson().fromJson(new String(content), User.class);
					app.setLoginUser(user);
					Intent intent=new Intent();
			    	intent.setClass(LoginActivity.this, MainActivity.class);
			    	startActivity(intent);
			    	finish();
				   }
				@Override
				public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
					Log.d("-loginActivity","login:failure:"+error.getMessage()+",httpstatus="+statusCode);
					switch(statusCode){
					case HttpStatus.SC_UNAUTHORIZED:
						et_password.setError(getString(R.string.login_err_wrong_password));
						et_password.requestFocus();
						break;
					case HttpStatus.SC_INTERNAL_SERVER_ERROR:
						AlertHelper.showToast(LoginActivity.this, R.string.error_serverError);
						break;
					}
				}
				@Override
			     public void onFinish() {
					Log.d("-loginActivity","login:finish");
					showProgress(false);
			     }
			});
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
//	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
//		@Override
//		protected Boolean doInBackground(Void... params) {
//			try {
//				HttpGet request = new HttpGet(Constance.SERVER_URL+"user?phone="+mPhone+"&password="+mPassword);
//				Gson gson = new Gson();
////				message= str;
//				HttpResponse httpResponse = new DefaultHttpClient().execute(request);
//				String retSrc = EntityUtils.toString(httpResponse.getEntity());
//				User user=gson.fromJson(retSrc, User.class);
//				if(user!=null){
//					((App)getApplication()).setLoginUser(user);
//					((App)getApplication()).saveLogin(user.getUserId());
//					return true;
//				}
////				message= retSrc;
//			} catch (Exception e) {
////				message=e.getMessage();
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} 
//
//			// TODO: register the new account here.
//			return false;
//		}
//
//		@Override
//		protected void onPostExecute(final Boolean success) {
//			mAuthTask = null;
//			showProgress(false);
//
//			if (success) {
//				Intent intent=new Intent();
//		    	intent.setClass(LoginActivity.this, MainActivity.class);
//		    	LoginActivity.this.startActivity(intent);
//		    	LoginActivity.this.finish();
//			} else {
//				mPasswordView
//						.setError(getString(R.string.login_err_wrong_password));
//				mPasswordView.requestFocus();
//			}
//		}
//
//		@Override
//		protected void onCancelled() {
//			mAuthTask = null;
//			showProgress(false);
//		}
//	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode==KeyEvent.KEYCODE_BACK){
			Intent intent=new Intent();
	    	intent.setClass(this, MainActivity.class);
	    	this.startActivity(intent);
	    	LoginActivity.this.finish();
	    	return true;
		}else{
			return false;
		}
	}
}
