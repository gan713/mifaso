package com.mifashow;

import org.apache.http.Header;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mifashow.data.Constance;
import com.mifashow.domain.User;
import com.mifashow.tool.AlertHelper;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.widget.EditText;

public class PasswordActivity extends FragmentActivity {
	private App app;
	EditText et_current,et_new;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password);
		setupActionBar();
		app=(App) getApplication();
		et_current=(EditText) findViewById(R.id.password_et_current);
		et_new=(EditText) findViewById(R.id.password_et_new);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			String currentPassword=et_current.getText().toString();
			String newPassword=et_new.getText().toString();
			if(!currentPassword.equals(app.getLoginUser().getPassword())){
				et_current.setError(getResources().getString(R.string.password_et_current_err));
				et_current.requestFocus();
			}else if(currentPassword.equals(newPassword) || "".equals(newPassword)){
				et_new.setError(getResources().getString(R.string.password_et_new_err));
				et_new.requestFocus();
			}else{
				RequestParams params = new RequestParams();
				params.put("newPassword", newPassword);
				AsyncHttpClient client=new AsyncHttpClient();
				UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(app.getLoginUser().getSign(), app.getLoginUser().getPassword());
				Header header = BasicScheme.authenticate(credentials, "UTF-8", false);
				client.addHeader(header.getName(), header.getValue());
				client.put(this, Constance.SERVER_URL+"password", params, new AsyncHttpResponseHandler(){
					@Override
					public void onStart(){
						getActionBar().setDisplayHomeAsUpEnabled(false);
						getActionBar().setTitle(R.string.status_sending);
					}
					@Override
					   public void onSuccess(int statusCode,Header[] headers,byte[] content) {
						User u=new Gson().fromJson(new String(content), User.class);
						AlertHelper.showToast(PasswordActivity.this, R.string.info_changePasswordDone);
						app.setLoginUser(u);
						finish();
					   }
					@Override
					public void onFailure(int statusCode,Header[] headers,byte[] content,java.lang.Throwable error){
						AlertHelper.showToast(PasswordActivity.this, R.string.error_serverError);
					}
					@Override
					public void onFinish(){
						getActionBar().setDisplayHomeAsUpEnabled(true);
						getActionBar().setTitle(R.string.password_at_label);
					}
				});
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
