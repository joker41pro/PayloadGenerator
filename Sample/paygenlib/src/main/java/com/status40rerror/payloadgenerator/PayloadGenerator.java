package com.status40rerror.payloadgenerator;

import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.widget.*;
import android.view.*;
import android.view.View.*;
import android.text.*;
import java.util.*;
import android.widget.AdapterView.*;
import android.app.AlertDialog.*;
import android.widget.CompoundButton.*;

public class PayloadGenerator extends AlertDialog.Builder implements OnItemSelectedListener, CompoundButton.OnCheckedChangeListener
{

	private static SharedPreferences sp;
	private static SharedPreferences prefs;
	private static SharedPreferences.Editor editor;
	private static SharedPreferences.Editor edit;
	private static EditText payload;
	private static EditText editxt;
	private static RadioButton rNomal, rSplit, rDirect;
	private static CheckBox cbRotate, cbOnline, cbForward, cbReverse, cbKeep, cbUser, cbReferer, cbFront, cbBack, cbRaw, cbDual;
	private static Spinner requestSpin, injectSpin, splitSpin;
	private static Context context;
	private static View v;
	private static String keys;
	private static LayoutInflater inflater;
	private static String[] request_items = new String[]{ "CONNECT", "GET", "POST", "PUT", "HEAD", "TRACE", "OPTIONS", "PATCH", "PROPATCH", "DELETE"};
	private static String[] inject_items = new String[]{"NORMAL", "FRONT INJECT", "BACK INJECT"};
	private static String[] split_items = new String[]{"NORMAL","INSTANT SPLIT", "DELAY SPLIT"};
	private static ArrayAdapter<String> rAdapter, iAdapter, sAdapter;

	/*public PayloadGenerator(Context con, EditText editxt){
	 super(con);
	 this.context = con;
	 this.editxt = editxt;
	 setView(view());
	 setTitle("Payload Generator");
	 setPositiveButton("Generate", this);
	 }*/
	public PayloadGenerator(Context con){
		super(con);
		this.context = con;
		//this.editxt = editxt;
		setView(view());
		setTitle("Payload Generator");

	}

	/*public PayloadGenerator(Context con, String key){
	 super(con); 
	 this.keys = key;
	 sp = PreferenceManager.getDefaultSharedPreferences(con);
	 edit = sp.edit();
	 setTitle("Payload Generator");
	 setPositiveButton("Generate", this);
	 }*/

	public static void showPaygen(final Context con, final EditText et){
		AlertDialog.Builder adb = new PayloadGenerator(con);
		context = con;
		editxt = et;
		adb.setPositiveButton("Generate", etClick);
		adb.create().show();

	}

	public static void showForEditTextAndSharedPreference(final Context con, final EditText et, final String key){
		AlertDialog.Builder adb = new PayloadGenerator(con);
		context = con;
		sp = PreferenceManager.getDefaultSharedPreferences(con);
		edit = sp.edit();
		editxt = et;
		keys = key;
		adb.setPositiveButton("Generate", etWspClick);
		adb.create().show();
	}

	public static void showForSharedPreference(final Context con, final String key){
		AlertDialog.Builder adb = new PayloadGenerator(con);
		context = con;
		sp = PreferenceManager.getDefaultSharedPreferences(con);
		edit = sp.edit();
		//editxt = et;
		keys = key;
		adb.setPositiveButton("Generate", spClick);
		adb.create().show();
	}


	private View view(){
		inflater = LayoutInflater.from(context);
		v = inflater.inflate(R.layout.paygen, null);
		payload = (EditText) v.findViewById(R.id.paygenEditText);
		rNomal = (RadioButton) v.findViewById(R.id.rNormal);
		rSplit = (RadioButton) v.findViewById(R.id.rSplit);
		rDirect = (RadioButton) v.findViewById(R.id.rDirect);
		cbRotate = (CheckBox) v.findViewById(R.id.cbRotate);
		cbOnline = (CheckBox) v.findViewById(R.id.cbOnline);
		cbForward = (CheckBox) v.findViewById(R.id.cbForward);
		cbReverse = (CheckBox) v.findViewById(R.id.cbReverse);
		cbKeep = (CheckBox) v.findViewById(R.id.cbKeep);
		cbUser = (CheckBox) v.findViewById(R.id.cbUser);
		cbReferer = (CheckBox) v.findViewById(R.id.cbReferer);
		cbFront = (CheckBox) v.findViewById(R.id.cbFront);
		cbBack = (CheckBox) v.findViewById(R.id.cbBack);
		cbRaw = (CheckBox) v.findViewById(R.id.cbRaw);
		cbDual = (CheckBox) v.findViewById(R.id.cbDual);
		requestSpin = (Spinner) v.findViewById(R.id.request_spin);
		injectSpin = (Spinner) v.findViewById(R.id.inject_spin);
		splitSpin = (Spinner) v.findViewById(R.id.split_spin);
		rNomal.setOnCheckedChangeListener(this);
		rSplit.setOnCheckedChangeListener(this);
		rDirect.setOnCheckedChangeListener(this);
		cbFront.setOnCheckedChangeListener(this);
		cbBack.setOnCheckedChangeListener(this);
		cbRaw.setOnCheckedChangeListener(this);
		cbDual.setOnCheckedChangeListener(this);
		rAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, request_items);
		iAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, inject_items);
		sAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, split_items);
		requestSpin.setAdapter(rAdapter);
		injectSpin.setAdapter(iAdapter);
		splitSpin.setAdapter(sAdapter);
		rAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		iAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		requestSpin.setPrompt("Request Method");
		injectSpin.setPrompt("Injection Method");
		splitSpin.setPrompt("Split Method");
		requestSpin.setOnItemSelectedListener(this);
		injectSpin.setOnItemSelectedListener(this);
		load();
		splitSpin.setSelection(0);
		this.requestSpin.setSelection(0);
		this.injectSpin.setSelection(0);
		rNomal.setChecked(true);
		return v;
	}



	private static DialogInterface.OnClickListener etClick = new DialogInterface.OnClickListener(){

		@Override
		public void onClick(DialogInterface p1, int p2)
		{
			switch(p2){
				case p1.BUTTON_POSITIVE:
					String sPayload = payload.getText().toString();
					StringBuilder sb = new StringBuilder();
					String crlf = "[crlf]";
					//String intro = " [protocol][crlf]";
					String space = " ";
					String connect = "CONNECT ";
					String host = "Host: ";
					String host_port = "[host_port]";
					String protocol = " [protocol]";
					String outro = crlf + crlf;
					String onePone = "HTTP/1.1";
					String http = "http://";
					String raw = "[raw]";
					int r = requestSpin.getSelectedItemPosition();
					int i = injectSpin.getSelectedItemPosition();
					if(!rDirect.isChecked()){
						switch(i){
							case 0:
								sb.append(connect);
								if(cbFront.isChecked()){
									sb.append(sPayload).append("@");
									if(r == 0){
										sb.append(host_port);
										sb.append(protocol).append(crlf);
									}else{
										sb.append(host_port).append(space);
										sb.append(onePone);
										sb.append(outro);
										sb.append((String) requestSpin.getSelectedItem()).append(space);
										sb.append(http.concat(sPayload).concat("/"));
										sb.append(protocol).append(crlf);
									}
								}else if(cbBack.isChecked()){
									sb.append(host_port).append("@").append(sPayload);
									if(r == 0){
										sb.append(protocol).append(crlf);
									}else{
										sb.append(space);
										sb.append(onePone);
										sb.append(outro);
										sb.append((String) requestSpin.getSelectedItem()).append(space);
										sb.append(http.concat(sPayload).concat("/"));
										sb.append(protocol).append(crlf);
									}
								}else{
									if(r == 0){
										sb.append(host_port);
										sb.append(protocol).append(crlf);
										if(rSplit.isChecked()){
											int s = splitSpin.getSelectedItemPosition();
											switch(s){
												case 0:
													sb.append("[split]");
													sb.append(connect).append(http).append(sPayload).append("/").append(space).append(onePone).append(crlf);
													break;
												case 1:
													sb.append("[instant_split]");
													sb.append(connect).append(http).append(sPayload).append("/").append(space).append(onePone).append(crlf);
													break;
												case 2:
													sb.append("[delay_split]");
													sb.append(connect).append(http).append(sPayload).append("/").append(space).append(onePone).append(crlf);
													break;
												default: break;
											}
										}
									}
								}
								break;
							case 1:
								sb.append((String) requestSpin.getSelectedItem()).append(space);
								sb.append(http.concat(sPayload).concat("/")).append(space);
								sb.append(onePone).append(crlf);
								break;
							case 2:
								sb.append(connect);
								if(cbFront.isChecked()){
									sb.append(sPayload).append("@");
									sb.append(host_port);
								}else if(cbBack.isChecked()){
									sb.append(host_port);
									sb.append("@").append(sPayload);
								}else{
									sb.append(host_port);
								}
								if(rSplit.isChecked()){
									sb.append(protocol);
									//sb.append(space);
									sb.append(crlf);
								}else{
									sb.append(space);
									sb.append(onePone);
									sb.append(crlf);
								}
								if(rSplit.isChecked()){
									int s = splitSpin.getSelectedItemPosition();
									switch(s){
										case 0:
											sb.append("[split]");
											break;
										case 1:
											sb.append("[instant_split]");
											break;
										case 2:
											sb.append("[delay_split]");
											break;
										default: break;
									}
								}else{
									sb.append(crlf);
								}
								sb.append((String) requestSpin.getSelectedItem()).append(space);
								sb.append(http.concat(sPayload).concat("/"));
								if(rSplit.isChecked()){
									sb.append(space).append(onePone).append(crlf);
								}else{
									sb.append(protocol).append(crlf);
								}
								break;
						}
					}else{
						String d = (String) requestSpin.getSelectedItem();
						sb.append(d);
						if(cbFront.isChecked())
							sb.append(space).append(sPayload).append("@").append(host_port).append(protocol).append(crlf);
						else if(cbBack.isChecked())
							sb.append(" [host_port]").append("@").append(sPayload).append(protocol).append(crlf);
						else
							sb.append(space).append(host_port).append(protocol).append(crlf);
					}
					if(sPayload.isEmpty() || sPayload.equals("")){

					}else{
						sb.append(host).append(sPayload);

						if(cbOnline.isChecked()){
							sb.append(crlf).append("X-Online-Host: ").append(sPayload);
						}
						if(cbForward.isChecked()){
							sb.append(crlf).append("X-Forward-Host: ").append(sPayload);
						}
						if(cbReverse.isChecked()){
							sb.append(crlf).append("X-Forwarded-For: ").append(sPayload);
						}
					}
					if(cbKeep.isChecked()){
						sb.append(crlf).append("Connection: Keep-Alive");
					}
					if(cbUser.isChecked()){
						sb.append(crlf).append("User-Agent: [ua]");
					}
					if(cbReferer.isChecked()){
						sb.append(crlf).append("Referer: ").append(sPayload);
					}
					if(cbDual.isChecked()){
						sb.append(crlf).append(connect).append(host_port).append(protocol);
					}
					//sb.append(outro);
					if(i ==1){
						sb.append(outro);
						if(rSplit.isChecked()){
							int s = splitSpin.getSelectedItemPosition();
							switch(s){
								case 0:
									sb.append("[split]");
									break;
								case 1:
									sb.append("[instant_split]");
									break;
								case 2:
									sb.append("[delay_split]");
									break;
								default: break;
							}
						}
						sb.append(connect);
						if(cbFront.isChecked()){
							sb.append(sPayload).append("@").append(host_port).append(protocol).append(outro);
						}else if(cbBack.isChecked()){
							sb.append(host_port).append("@").append(sPayload).append(protocol).append(outro);
						}else{
							sb.append(host_port).append(protocol).append(outro);
						}
					}else{
						sb.append(outro);
					}
					String f = sb.toString();
					if(cbRaw.isChecked()){
						if(f.contains("CONNECT [host_port] [protocol]")){
							String rw = f.replace("CONNECT [host_port] [protocol]",raw);
							if(cbRotate.isChecked()){
								if(!rw.contains(";")){
									Toast.makeText(context, "Invalid URL/Host",Toast.LENGTH_LONG).show();
								}else{
									editxt.setText(rw);
								}
							}else{
								editxt.setText(rw);
							}

						}
					}else{
						if(cbRotate.isChecked()){
							if(!sb.toString().contains(";")){
								Toast.makeText(context, "Invalid URL/Host",Toast.LENGTH_LONG).show();
							}else{
								editxt.setText(sb.toString());
							}
						}else{
							editxt.setText(sb.toString());
						}

					}
					save();
					break;
			}
			// TODO: Implement this method
		}


	};
	private static DialogInterface.OnClickListener etWspClick = new DialogInterface.OnClickListener(){

		@Override
		public void onClick(DialogInterface p1, int p2)
		{
			switch(p2){
				case p1.BUTTON_POSITIVE:
					String sPayload = payload.getText().toString();
					StringBuilder sb = new StringBuilder();
					String crlf = "[crlf]";
					//String intro = " [protocol][crlf]";
					String space = " ";
					String connect = "CONNECT ";
					String host = "Host: ";
					String host_port = "[host_port]";
					String protocol = " [protocol]";
					String outro = crlf + crlf;
					String onePone = "HTTP/1.1";
					String http = "http://";
					String raw = "[raw]";
					int r = requestSpin.getSelectedItemPosition();
					int i = injectSpin.getSelectedItemPosition();
					if(!rDirect.isChecked()){
						switch(i){
							case 0:
								sb.append(connect);
								if(cbFront.isChecked()){
									sb.append(sPayload).append("@");
									if(r == 0){
										sb.append(host_port);
										sb.append(protocol).append(crlf);
									}else{
										sb.append(host_port).append(space);
										sb.append(onePone);
										sb.append(outro);
										sb.append((String) requestSpin.getSelectedItem()).append(space);
										sb.append(http.concat(sPayload).concat("/"));
										sb.append(protocol).append(crlf);
									}
								}else if(cbBack.isChecked()){
									sb.append(host_port).append("@").append(sPayload);
									if(r == 0){
										sb.append(protocol).append(crlf);
									}else{
										sb.append(space);
										sb.append(onePone);
										sb.append(outro);
										sb.append((String) requestSpin.getSelectedItem()).append(space);
										sb.append(http.concat(sPayload).concat("/"));
										sb.append(protocol).append(crlf);
									}
								}else{
									if(r == 0){
										sb.append(host_port);
										sb.append(protocol).append(crlf);
										if(rSplit.isChecked()){
											int s = splitSpin.getSelectedItemPosition();
											switch(s){
												case 0:
													sb.append("[split]");
													sb.append(connect).append(http).append(sPayload).append("/").append(space).append(onePone).append(crlf);
													break;
												case 1:
													sb.append("[instant_split]");
													sb.append(connect).append(http).append(sPayload).append("/").append(space).append(onePone).append(crlf);
													break;
												case 2:
													sb.append("[delay_split]");
													sb.append(connect).append(http).append(sPayload).append("/").append(space).append(onePone).append(crlf);
													break;
												default: break;
											}
										}
									}
								}
								break;
							case 1:
								sb.append((String) requestSpin.getSelectedItem()).append(space);
								sb.append(http.concat(sPayload).concat("/")).append(space);
								sb.append(onePone).append(crlf);
								break;
							case 2:
								sb.append(connect);
								if(cbFront.isChecked()){
									sb.append(sPayload).append("@");
									sb.append(host_port);
								}else if(cbBack.isChecked()){
									sb.append(host_port);
									sb.append("@").append(sPayload);
								}else{
									sb.append(host_port);
								}
								if(rSplit.isChecked()){
									sb.append(protocol);
									//sb.append(space);
									sb.append(crlf);
								}else{
									sb.append(space);
									sb.append(onePone);
									sb.append(crlf);
								}
								if(rSplit.isChecked()){
									int s = splitSpin.getSelectedItemPosition();
									switch(s){
										case 0:
											sb.append("[split]");
											break;
										case 1:
											sb.append("[instant_split]");
											break;
										case 2:
											sb.append("[delay_split]");
											break;
										default: break;
									}
								}else{
									sb.append(crlf);
								}
								sb.append((String) requestSpin.getSelectedItem()).append(space);
								sb.append(http.concat(sPayload).concat("/"));
								if(rSplit.isChecked()){
									sb.append(space).append(onePone).append(crlf);
								}else{
									sb.append(protocol).append(crlf);
								}
								break;
						}
					}else{
						String d = (String) requestSpin.getSelectedItem();
						sb.append(d);
						if(cbFront.isChecked())
							sb.append(space).append(sPayload).append("@").append(host_port).append(protocol).append(crlf);
						else if(cbBack.isChecked())
							sb.append(" [host_port]").append("@").append(sPayload).append(protocol).append(crlf);
						else
							sb.append(space).append(host_port).append(protocol).append(crlf);
					}
					if(sPayload.isEmpty() || sPayload.equals("")){

					}else{
						sb.append(host).append(sPayload);

						if(cbOnline.isChecked()){
							sb.append(crlf).append("X-Online-Host: ").append(sPayload);
						}
						if(cbForward.isChecked()){
							sb.append(crlf).append("X-Forward-Host: ").append(sPayload);
						}
						if(cbReverse.isChecked()){
							sb.append(crlf).append("X-Forwarded-For: ").append(sPayload);
						}
					}
					if(cbKeep.isChecked()){
						sb.append(crlf).append("Connection: Keep-Alive");
					}
					if(cbUser.isChecked()){
						sb.append(crlf).append("User-Agent: [ua]");
					}
					if(cbReferer.isChecked()){
						sb.append(crlf).append("Referer: ").append(sPayload);
					}
					if(cbDual.isChecked()){
						sb.append(crlf).append(connect).append(host_port).append(protocol);
					}
					//sb.append(outro);
					if(i ==1){
						sb.append(outro);
						if(rSplit.isChecked()){
							int s = splitSpin.getSelectedItemPosition();
							switch(s){
								case 0:
									sb.append("[split]");
									break;
								case 1:
									sb.append("[instant_split]");
									break;
								case 2:
									sb.append("[delay_split]");
									break;
								default: break;
							}
						}
						sb.append(connect);
						if(cbFront.isChecked()){
							sb.append(sPayload).append("@").append(host_port).append(protocol).append(outro);
						}else if(cbBack.isChecked()){
							sb.append(host_port).append("@").append(sPayload).append(protocol).append(outro);
						}else{
							sb.append(host_port).append(protocol).append(outro);
						}
					}else{
						sb.append(outro);
					}
					String f = sb.toString();
					if(cbRaw.isChecked()){
						if(f.contains("CONNECT [host_port] [protocol]")){
							String rw = f.replace("CONNECT [host_port] [protocol]",raw);
							if(cbRotate.isChecked()){
								if(!rw.contains(";")){
									Toast.makeText(context, "Invalid URL/Host",Toast.LENGTH_LONG).show();
								}else{
									editxt.setText(rw);
									edit.putString(keys, rw).apply();
								}
							}else{
								editxt.setText(rw);
								edit.putString(keys,rw).apply();
							}

						}
					}else{
						if(cbRotate.isChecked()){
							if(!sb.toString().contains(";")){
								Toast.makeText(context, "Invalid URL/Host",Toast.LENGTH_LONG).show();
							}else{
								editxt.setText(sb.toString());
								edit.putString(keys, sb.toString()).apply();
							}
						}else{
							editxt.setText(sb.toString());
							edit.putString(keys,sb.toString()).apply();
						}

					}
					save();
					break;
			}
			// TODO: Implement this method
		}


	};
	private static DialogInterface.OnClickListener spClick = new DialogInterface.OnClickListener(){

		@Override
		public void onClick(DialogInterface p1, int p2)
		{
			switch(p2){
				case p1.BUTTON_POSITIVE:
					String sPayload = payload.getText().toString();
					StringBuilder sb = new StringBuilder();
					String crlf = "[crlf]";
					//String intro = " [protocol][crlf]";
					String space = " ";
					String connect = "CONNECT ";
					String host = "Host: ";
					String host_port = "[host_port]";
					String protocol = " [protocol]";
					String outro = crlf + crlf;
					String onePone = "HTTP/1.1";
					String http = "http://";
					String raw = "[raw]";
					int r = requestSpin.getSelectedItemPosition();
					int i = injectSpin.getSelectedItemPosition();
					if(!rDirect.isChecked()){
						switch(i){
							case 0:
								sb.append(connect);
								if(cbFront.isChecked()){
									sb.append(sPayload).append("@");
									if(r == 0){
										sb.append(host_port);
										sb.append(protocol).append(crlf);
									}else{
										sb.append(host_port).append(space);
										sb.append(onePone);
										sb.append(outro);
										sb.append((String) requestSpin.getSelectedItem()).append(space);
										sb.append(http.concat(sPayload).concat("/"));
										sb.append(protocol).append(crlf);
									}
								}else if(cbBack.isChecked()){
									sb.append(host_port).append("@").append(sPayload);
									if(r == 0){
										sb.append(protocol).append(crlf);
									}else{
										sb.append(space);
										sb.append(onePone);
										sb.append(outro);
										sb.append((String) requestSpin.getSelectedItem()).append(space);
										sb.append(http.concat(sPayload).concat("/"));
										sb.append(protocol).append(crlf);
									}
								}else{
									if(r == 0){
										sb.append(host_port);
										sb.append(protocol).append(crlf);
										if(rSplit.isChecked()){
											int s = splitSpin.getSelectedItemPosition();
											switch(s){
												case 0:
													sb.append("[split]");
													sb.append(connect).append(http).append(sPayload).append("/").append(space).append(onePone).append(crlf);
													break;
												case 1:
													sb.append("[instant_split]");
													sb.append(connect).append(http).append(sPayload).append("/").append(space).append(onePone).append(crlf);
													break;
												case 2:
													sb.append("[delay_split]");
													sb.append(connect).append(http).append(sPayload).append("/").append(space).append(onePone).append(crlf);
													break;
												default: break;
											}
										}
									}
								}
								break;
							case 1:
								sb.append((String) requestSpin.getSelectedItem()).append(space);
								sb.append(http.concat(sPayload).concat("/")).append(space);
								sb.append(onePone).append(crlf);
								break;
							case 2:
								sb.append(connect);
								if(cbFront.isChecked()){
									sb.append(sPayload).append("@");
									sb.append(host_port);
								}else if(cbBack.isChecked()){
									sb.append(host_port);
									sb.append("@").append(sPayload);
								}else{
									sb.append(host_port);
								}
								if(rSplit.isChecked()){
									sb.append(protocol);
									//sb.append(space);
									sb.append(crlf);
								}else{
									sb.append(space);
									sb.append(onePone);
									sb.append(crlf);
								}
								if(rSplit.isChecked()){
									int s = splitSpin.getSelectedItemPosition();
									switch(s){
										case 0:
											sb.append("[split]");
											break;
										case 1:
											sb.append("[instant_split]");
											break;
										case 2:
											sb.append("[delay_split]");
											break;
										default: break;
									}
								}else{
									sb.append(crlf);
								}
								sb.append((String) requestSpin.getSelectedItem()).append(space);
								sb.append(http.concat(sPayload).concat("/"));
								if(rSplit.isChecked()){
									sb.append(space).append(onePone).append(crlf);
								}else{
									sb.append(protocol).append(crlf);
								}
								break;
						}
					}else{
						String d = (String) requestSpin.getSelectedItem();
						sb.append(d);
						if(cbFront.isChecked())
							sb.append(space).append(sPayload).append("@").append(host_port).append(protocol).append(crlf);
						else if(cbBack.isChecked())
							sb.append(" [host_port]").append("@").append(sPayload).append(protocol).append(crlf);
						else
							sb.append(space).append(host_port).append(protocol).append(crlf);
					}
					if(sPayload.isEmpty() || sPayload.equals("")){

					}else{
						sb.append(host).append(sPayload);

						if(cbOnline.isChecked()){
							sb.append(crlf).append("X-Online-Host: ").append(sPayload);
						}
						if(cbForward.isChecked()){
							sb.append(crlf).append("X-Forward-Host: ").append(sPayload);
						}
						if(cbReverse.isChecked()){
							sb.append(crlf).append("X-Forwarded-For: ").append(sPayload);
						}
					}
					if(cbKeep.isChecked()){
						sb.append(crlf).append("Connection: Keep-Alive");
					}
					if(cbUser.isChecked()){
						sb.append(crlf).append("User-Agent: [ua]");
					}
					if(cbReferer.isChecked()){
						sb.append(crlf).append("Referer: ").append(sPayload);
					}
					if(cbDual.isChecked()){
						sb.append(crlf).append(connect).append(host_port).append(protocol);
					}
					//sb.append(outro);
					if(i ==1){
						sb.append(outro);
						if(rSplit.isChecked()){
							int s = splitSpin.getSelectedItemPosition();
							switch(s){
								case 0:
									sb.append("[split]");
									break;
								case 1:
									sb.append("[instant_split]");
									break;
								case 2:
									sb.append("[delay_split]");
									break;
								default: break;
							}
						}
						sb.append(connect);
						if(cbFront.isChecked()){
							sb.append(sPayload).append("@").append(host_port).append(protocol).append(outro);
						}else if(cbBack.isChecked()){
							sb.append(host_port).append("@").append(sPayload).append(protocol).append(outro);
						}else{
							sb.append(host_port).append(protocol).append(outro);
						}
					}else{
						sb.append(outro);
					}
					String f = sb.toString();
					if(cbRaw.isChecked()){
						if(f.contains("CONNECT [host_port] [protocol]")){
							String rw = f.replace("CONNECT [host_port] [protocol]",raw);
							if(cbRotate.isChecked()){
								if(!rw.contains(";")){
									Toast.makeText(context, "Invalid URL/Host",Toast.LENGTH_LONG).show();
								}else{
									//editxt.setText(rw);
									edit.putString(keys, rw).apply();
								}
							}else{
								//editxt.setText(rw);
								edit.putString(keys,rw).apply();
							}

						}
					}else{
						if(cbRotate.isChecked()){
							if(!sb.toString().contains(";")){
								Toast.makeText(context, "Invalid URL/Host",Toast.LENGTH_LONG).show();
							}else{
								//editxt.setText(sb.toString());
								edit.putString(keys, sb.toString()).apply();
							}
						}else{
							//editxt.setText(sb.toString());
							edit.putString(keys,sb.toString()).apply();
						}

					}
					save();
					break;
			}
			// TODO: Implement this method
		}


	};

	/*
	 public void onClick(DialogInterface p1, int p2)
	 {
	 switch(p2){
	 case p1.BUTTON_POSITIVE:
	 String sPayload = payload.getText().toString();
	 StringBuilder sb = new StringBuilder();
	 String crlf = "[crlf]";
	 //String intro = " [protocol][crlf]";
	 String space = " ";
	 String connect = "CONNECT ";
	 String host = "Host: ";
	 String host_port = "[host_port]";
	 String protocol = " [protocol]";
	 String outro = crlf + crlf;
	 String onePone = "HTTP/1.1";
	 String http = "http://";
	 String raw = "[raw]";
	 int r = requestSpin.getSelectedItemPosition();
	 int i = injectSpin.getSelectedItemPosition();
	 if(!rDirect.isChecked()){
	 switch(i){
	 case 0:
	 sb.append(connect);
	 if(cbFront.isChecked()){
	 sb.append(sPayload).append("@");
	 if(r == 0){
	 sb.append(host_port);
	 sb.append(protocol).append(crlf);
	 }else{
	 sb.append(host_port).append(space);
	 sb.append(onePone);
	 sb.append(outro);
	 sb.append((String) requestSpin.getSelectedItem()).append(space);
	 sb.append(http.concat(sPayload).concat("/"));
	 sb.append(protocol).append(crlf);
	 }
	 }else if(cbBack.isChecked()){
	 sb.append(host_port).append("@").append(sPayload);
	 if(r == 0){
	 sb.append(protocol).append(crlf);
	 }else{
	 sb.append(space);
	 sb.append(onePone);
	 sb.append(outro);
	 sb.append((String) requestSpin.getSelectedItem()).append(space);
	 sb.append(http.concat(sPayload).concat("/"));
	 sb.append(protocol).append(crlf);
	 }
	 }else{
	 if(r == 0){
	 sb.append(host_port);
	 sb.append(protocol).append(crlf);
	 if(this.rSplit.isChecked()){
	 int s = splitSpin.getSelectedItemPosition();
	 switch(s){
	 case 0:
	 sb.append("[split]");
	 sb.append(connect).append(http).append(sPayload).append("/").append(space).append(onePone).append(crlf);
	 break;
	 case 1:
	 sb.append("[instant_split]");
	 sb.append(connect).append(http).append(sPayload).append("/").append(space).append(onePone).append(crlf);
	 break;
	 case 2:
	 sb.append("[delay_split]");
	 sb.append(connect).append(http).append(sPayload).append("/").append(space).append(onePone).append(crlf);
	 break;
	 default: break;
	 }
	 }
	 }
	 }
	 break;
	 case 1:
	 sb.append((String) requestSpin.getSelectedItem()).append(space);
	 sb.append(http.concat(sPayload).concat("/")).append(space);
	 sb.append(onePone).append(crlf);
	 break;
	 case 2:
	 sb.append(connect);
	 if(cbFront.isChecked()){
	 sb.append(sPayload).append("@");
	 sb.append(host_port);
	 }else if(cbBack.isChecked()){
	 sb.append(host_port);
	 sb.append("@").append(sPayload);
	 }else{
	 sb.append(host_port);
	 }
	 if(rSplit.isChecked()){
	 sb.append(protocol);
	 //sb.append(space);
	 sb.append(crlf);
	 }else{
	 sb.append(space);
	 sb.append(onePone);
	 sb.append(crlf);
	 }
	 if(this.rSplit.isChecked()){
	 int s = splitSpin.getSelectedItemPosition();
	 switch(s){
	 case 0:
	 sb.append("[split]");
	 break;
	 case 1:
	 sb.append("[instant_split]");
	 break;
	 case 2:
	 sb.append("[delay_split]");
	 break;
	 default: break;
	 }
	 }else{
	 sb.append(crlf);
	 }
	 sb.append((String) requestSpin.getSelectedItem()).append(space);
	 sb.append(http.concat(sPayload).concat("/"));
	 if(rSplit.isChecked()){
	 sb.append(space).append(onePone).append(crlf);
	 }else{
	 sb.append(protocol).append(crlf);
	 }
	 break;
	 }
	 }else{
	 String d = (String) requestSpin.getSelectedItem();
	 sb.append(d);
	 if(cbFront.isChecked())
	 sb.append(space).append(sPayload).append("@").append(host_port).append(protocol).append(crlf);
	 else if(cbBack.isChecked())
	 sb.append(" [host_port]").append("@").append(sPayload).append(protocol).append(crlf);
	 else
	 sb.append(space).append(host_port).append(protocol).append(crlf);
	 }
	 if(sPayload.isEmpty() || sPayload.equals("")){

	 }else{
	 sb.append(host).append(sPayload);

	 if(cbOnline.isChecked()){
	 sb.append(crlf).append("X-Online-Host: ").append(sPayload);
	 }
	 if(cbForward.isChecked()){
	 sb.append(crlf).append("X-Forward-Host: ").append(sPayload);
	 }
	 if(cbReverse.isChecked()){
	 sb.append(crlf).append("X-Forwarded-For: ").append(sPayload);
	 }
	 }
	 if(cbKeep.isChecked()){
	 sb.append(crlf).append("Connection: Keep-Alive");
	 }
	 if(cbUser.isChecked()){
	 sb.append(crlf).append("User-Agent: [ua]");
	 }
	 if(cbReferer.isChecked()){
	 sb.append(crlf).append("Referer: ").append(sPayload);
	 }
	 if(cbDual.isChecked()){
	 sb.append(crlf).append(connect).append(host_port).append(protocol);
	 }
	 //sb.append(outro);
	 if(i ==1){
	 sb.append(outro);
	 if(rSplit.isChecked()){
	 int s = splitSpin.getSelectedItemPosition();
	 switch(s){
	 case 0:
	 sb.append("[split]");
	 break;
	 case 1:
	 sb.append("[instant_split]");
	 break;
	 case 2:
	 sb.append("[delay_split]");
	 break;
	 default: break;
	 }
	 }
	 sb.append(connect);
	 if(cbFront.isChecked()){
	 sb.append(sPayload).append("@").append(host_port).append(protocol).append(outro);
	 }else if(cbBack.isChecked()){
	 sb.append(host_port).append("@").append(sPayload).append(protocol).append(outro);
	 }else{
	 sb.append(host_port).append(protocol).append(outro);
	 }
	 }else{
	 sb.append(outro);
	 }
	 String f = sb.toString();
	 if(cbRaw.isChecked()){
	 if(f.contains("CONNECT [host_port] [protocol]")){
	 String rw = f.replace("CONNECT [host_port] [protocol]",raw);
	 if(cbRotate.isChecked()){
	 if(!rw.contains(";")){
	 Toast.makeText(context, "Invalid URL/Host",Toast.LENGTH_LONG).show();
	 }else{
	 editxt.setText(rw);
	 edit.putString(keys, rw).apply();
	 }
	 }else{
	 editxt.setText(rw);
	 edit.putString(keys,rw).apply();
	 }

	 }
	 }else{
	 if(cbRotate.isChecked()){
	 if(!sb.toString().contains(";")){
	 Toast.makeText(context, "Invalid URL/Host",Toast.LENGTH_LONG).show();
	 }else{
	 editxt.setText(sb.toString());
	 edit.putString(keys, sb.toString()).apply();
	 }
	 }else{
	 editxt.setText(sb.toString());
	 edit.putString(keys,sb.toString()).apply();
	 }

	 }
	 save();
	 break;
	 }
	 // TODO: Implement this method
	 }*/
	@Override
	public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
	{
		int id = p1.getId();
		if(id == R.id.request_spin){
			if(p3 == 1 || p3 == 2 || p3 == 3 || p3 == 4 || p3 == 5|| p3 == 6 || p3 == 7 || p3 == 8 || p3 ==9){
				if(injectSpin.getSelectedItemPosition() == 1){
					return;
				}else{
					injectSpin.setSelection(2);
				}
			}
		}else if(id == R.id.inject_spin){
			if(p3 == 1 || p3 == 2){
				requestSpin.setSelection(1);
			}else if(p3 == 0){
				if(this.rSplit.isChecked()){
					this.rNomal.setChecked(true);
				}
				this.requestSpin.setSelection(0);
			}
		}
		// TODO: Implement this method
	}

	@Override
	public void onNothingSelected(AdapterView<?> p1)
	{
		// TODO: Implement this method
	}
	private static void save(){
		prefs = context.getSharedPreferences("status404error_prefs", context.MODE_PRIVATE);
		editor = prefs.edit();
		editor.putString("my_inputted_payload",payload.getText().toString()).apply();
		editor.putBoolean("rNormal_vmodz", rNomal.isChecked()).apply();
		editor.putBoolean("rSplit_vmodz", rSplit.isChecked()).apply();
		editor.putBoolean("rDirect_vmodz", rDirect.isChecked()).apply();
	}

	private static void load(){
		prefs = context.getSharedPreferences("status404error_prefs", context.MODE_PRIVATE);
		editor = prefs.edit();
		payload.setText(prefs.getString("my_inputted_payload",""));
		rNomal.setChecked(prefs.getBoolean("rNormal_vmodz", true));
		rSplit.setChecked(prefs.getBoolean("rSplit_vmodz", false));
		rDirect.setChecked(prefs.getBoolean("rDirect_vmodz", false));
	}


	@Override
	public void onCheckedChanged(CompoundButton p1, boolean p2)
	{
		int id = p1.getId();
		if(id == R.id.rNormal){
			if(p2){
				this.splitSpin.setEnabled(false);
				this.injectSpin.setEnabled(true);
				this.requestSpin.setSelection(0);
				this.injectSpin.setSelection(0);
			}
		}else if(id == R.id.rSplit){
			if(p2){
				this.splitSpin.setEnabled(true);
				this.requestSpin.setSelection(1);
				this.injectSpin.setSelection(2);
				this.splitSpin.setSelection(2);
			}
		}else if(id == R.id.rDirect){
			if(p2){
				this.splitSpin.setEnabled(false);
				this.injectSpin.setEnabled(false);
			}
		}else if(id == R.id.cbRaw){
			if(p2){
				this.cbDual.setEnabled(false);
			}else{
				this.cbDual.setEnabled(true);
			}
		}else if(id == R.id.cbDual){
			if(p2){
				this.cbRaw.setEnabled(false);
			}else{
				this.cbRaw.setEnabled(true);
			}
		}else if(id == R.id.cbFront){
			if(p2){
				if(this.cbBack.isChecked()){
					this.cbBack.setChecked(false);
				}
			}
		}else if(id == R.id.cbBack){
			if(p2){
				if(this.cbFront.isChecked()){
					this.cbFront.setChecked(false);
				}
			}
		}
		// TODO: Implement this method
	}

}
