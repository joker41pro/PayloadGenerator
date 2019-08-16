# PayloadGenerator

For easier generating of payload for SSH. Based on the HTTP Injector of Evozi. Credits to them.

## Implementation

#### Add this to your _root_ build.gradle

``` gradle
allprojects { 		
   repositories {
       ... 			https://github.com/status404error/PayloadGenerator/edit/master/README.md
       maven { url 'https://jitpack.io' }
   } 	
}

```

#### Add this to your dependencies
```
dependencies { 	 
    compile 'com.github.status404error:PayloadGenerator:1.4' 	
}
```

I suggest to use **Switch** to show and to enable and disable the usage of custom payload.
Also take note that you must define your SharedPreference that you use for custom payload enabling and disabling 

Initialize PayloadGenerator

```java
private PayloadGenerator payloadGenerator;
```

On your OnCreate or OnCreateView (for FragmentActivity)

```java
Switch switch = (Switch) ...
payloadGenerator = new PayloadGenerator(this, prefs);


//add custom proxy
payloadGenerator.setProxyKey("use_custom_proxy","generated_proxy");

```

* On the **custom proxy**, you must add the boolean key used on SharedPreference for enabling custom proxy and then the string key
Example: on your PreferenceActivity you used CheckBoxPreference for Custom Proxy, the key for that CheckBoxPreference is your boolean key for enabling custom proxy, same timg for the EditTextPreferece for adding your custom proxy.


You can add this condition on your **OnCheckedChangeListener** of your Switch

```java

if(ischeck){
   //enable custom payload and show PayloadGenerator
   prefs.edit().putBoolean("use_custom_payload",true).apply();
   //add condition for switches 
   //if use ssh or sni
   if(ssh_radio.isChecked()){
      payloadGenerator.showForSSH("generated_ssh");
   }else{
      payloadGenerator.showForSNI("generated_sni");
   }

}else{
//disable custom payload
prefs.edit().putBoolean("use_custom_payload",false).apply();
}
```

*If you will not declare key for custom proxy, the Option on the **PayloadGenerator** for custom proxy will be useless

You can also add **OnCancelClickedListener** if you want to add condition if the "Cancel" Button of the PayloadGenerator is clicked
```java
SwitchButton switchButton = (SwitchButton) findViewById(R.id.switchButton);
payloadGenerator.setOnCancelClickedListener(new PayloadGenerator.OnCancelClickedListener(){

				@Override
				public void OnCancelClickListener(DialogInterface dialogInterface)
				{
					// you can uncheck the switch here
					// you can also disable the usage of custom payload
					switchButton.setChecked(false);
					editor.putBoolean("use_custom_payload", false).apply();
					// TODO: Implement this method
				}
		 });
```
Versions:

* 1.4
 - Removed addPayloadSwitch(Switch) and replaced with OnCancelClickedListener so you can use Switch repositories (eg.: SwitchButton)

* 1.3
 - Changing some condition

* 1.2
 - Fixed gradle issues


* 1.1
 - Fixed some issues


* 1.0
 - Initial Release
