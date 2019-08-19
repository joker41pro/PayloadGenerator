package demo.status404error.paygen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.status404error.paygen.PayloadGenerator;
import android.widget.Button;
import android.widget.Switch;
import android.widget.RadioButton;
import android.widget.*;
import android.view.*;
import android.content.*;
import android.preference.*;
import demo.status404error.paygen.R;
import android.app.*;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener
{
	private PayloadGenerator paygen;
	private Switch paygen_switch;
	private RadioButton ssh_radio, sni_radio;
	private Button show_btn;
	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		//initialize SharedPreference
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.editor = prefs.edit();
		/*initialize PayloadGenerator
		 * do not use getApplicationContext() 
		 * when initializing context for PayloadGenerator
		 * else it will not work
		 */
		 this.paygen = new PayloadGenerator(this, prefs);
		 /* to Add Banner use addBanner(int resources) method
		  * add your Layout file on the parameter
		  * put addBanner(int resources) before show method
		  */
		 this.paygen.addBanner(R.layout.banner_sample);
		 this.paygen_switch = (Switch) findViewById(R.id.paygen_switch);
		 this.ssh_radio = (RadioButton) findViewById(R.id.ssh_radio);
		 this.sni_radio = (RadioButton) findViewById(R.id.sni_edit);
		 this.show_btn = (Button) findViewById(R.id.show_btn);
		 this.ssh_radio.setChecked(true);
		 this.paygen_switch.setOnCheckedChangeListener(this);
		 this.show_btn.setOnClickListener(this);
		 /** Add onCancelClickListener **/
		 this.paygen.setOnCancelClickedListener(new PayloadGenerator.OnCancelClickedListener(){

				@Override
				public void OnCancelClickListener(DialogInterface dialogInterface)
				{
					// you can uncheck the switch here
					// you can also disable the usage of custom payload
					paygen_switch.setChecked(false);
					editor.putBoolean("use_custom_payload", false).apply();
					// TODO: Implement this method
				}
		 });
		 //add custom proxy
		 paygen.setProxyKey("use_custom_proxy","generated_proxy");
    }
	
	@Override
	public void onClick(View view)
	{
		switch(view.getId()){
			case R.id.show_btn:
				AlertDialog.Builder adb = new AlertDialog.Builder(this);
				adb.setTitle("Result");
				if(prefs.getBoolean("use_custom_payload",false)==true){
					if(ssh_radio.isChecked()){
						if(prefs.getBoolean("use_custom_proxy", false)==true){
							adb.setMessage("Payload:\n"+prefs.getString("generated_ssh","")+"\nProxy:\n"+prefs.getString("generated_proxy",""));
						}else{
							adb.setMessage(prefs.getString("generated_ssh",""));
						}
						
					}else{
						adb.setMessage(prefs.getString("generated_sni",""));
					}
				}else{
					adb.setMessage("Custom Payload Disabled");
				}
				
				adb.setPositiveButton("Close",null);
				adb.create().show();
				break;
		}
		// TODO: Implement this method
	}

	@Override
	public void onCheckedChanged(CompoundButton widget, boolean ischeck)
	{
		switch(widget.getId()){
			case R.id.paygen_switch:
				if(ischeck){
					//you must commit the boolean here for custom payload to true
					editor.putBoolean("use_custom_payload",true).apply();
					//add condition for switches 
					//if use ssh or sni
					if(ssh_radio.isChecked()){
						paygen.showForSSH("generated_ssh");
					}else{
						paygen.showForSNI("generated_sni");
					}
				}else{
					//set custom payload to false
					editor.putBoolean("use_custom_payload",false).apply();
				}
				break;
		}
		// TODO: Implement this method
	}
}
