package com.status404error.paygen;

/*
 * Made: Status404Error
 * From: Team Vmodz
 * For: Freenet World in PH
 * Credits: Evozi Http Injector, Fagmmmu, Raksss, MushRoom RBuild.
 */

import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;
import com.status404error.paygen.*;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class PayloadGenerator extends AlertDialog.Builder
{
    private static ArrayAdapter<String> rAdapter, iAdapter, sAdapter;
    private static CheckBox cbBack;
    private static CheckBox cbDual;
    private static CheckBox cbForward;
    private static CheckBox cbFront;
    private static CheckBox cbKeep;
    private static CheckBox cbOnline;
    private static CheckBox cbRaw;
    private static CheckBox cbReferer;
    private static CheckBox cbReverse;
    private static CheckBox cbRotate;
    private static CheckBox cbUser;
    private static Context context;
    private static EditText editxt;
    private static EditText mainEdit;
    private static EditText mainPort;
    private static EditText mainProxy;
    private static EditText payload;
    private static EditText sni;
    private static LayoutInflater inflater;
    private static LayoutInflater mainInflater;
    private static LayoutInflater sniInflater;
    private static RadioButton rNomal;
    private static RadioButton rSplit;
    private static RadioButton rDirect;
    private static SharedPreferences prefs;
    private static SharedPreferences sp;
    private static SharedPreferences.Editor edit;
    private static SharedPreferences.Editor editor;
    private static Spinner injectSpin;
    private static Spinner requestSpin;
    private static Spinner splitSpin;
    private static String proxyKey;
    private static String proxySetting;
    private static String[] inject_items = new String[]{"NORMAL", "FRONT INJECT", "BACK INJECT"};
    private static String[] request_items = new String[]{ "CONNECT", "GET", "POST", "PUT", "HEAD", "TRACE", "OPTIONS", "PATCH", "PROPATCH", "DELETE"};
    private static String[] split_items = new String[]{"NORMAL","INSTANT SPLIT", "DELAY SPLIT"};
    private static Switch gen_switch;
    private static Switch proxy_switch;
    private static View mainV;
    private static View sniV;
    private static View v;

    public PayloadGenerator(Context con, SharedPreferences sharedpref){
        super(con);
        context = con;
        sp = sharedpref;
        edit = sp.edit();
    }

    public static void showForSSH(final String key){
        AlertDialog.Builder adb = new PayloadGenerator(context, sp);
        adb.setView(mainView());
        adb.setTitle("SSH Custom Payload");
        editxt=mainEdit;
        adb.setPositiveButton("Save", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface p1, int p2)
            {
                String m = mainEdit.getText().toString();
                edit.putString(key, m).commit();
                editor.putString("full_payload_vmodz",m).commit();
                if(isCustomProxy()){
                    String rp = mainProxy.getText().toString()+":"+mainPort.getText().toString();
                    edit.putString(proxySetting, rp).commit();
                    edit.putBoolean(proxyKey,proxy_switch.isChecked()).commit();
                    editor.putString("custom_proxyIP_vmodz", mainProxy.getText().toString()).commit();
                    editor.putString("custom_proxyPort_vmodz",mainPort.getText().toString()).commit();
                }
            }
        });
        adb.setCancelable(false);
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface p1, int p2){
                try{
                    gen_switch.setChecked(false);
                    p1.dismiss();
                }catch(Exception e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    p1.dismiss();
                }
            }
        });
        adb.setNeutralButton("Generator", null);
        final AlertDialog d = adb.create();
        d.show();
        d.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View p1)
            {
                boolean canDismiss = false;
                showPaygenSSH(editxt);
                canDismiss = false;
                if(canDismiss == true){
                    d.dismiss();
                }
            }
        });
    }

    public static void showForSNI(final String key){
        AlertDialog.Builder adb = new PayloadGenerator(context, sp);
        //keys = key;
        adb.setTitle("Custom SNI");
        adb.setView(sniView());
        adb.setCancelable(false);
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface p1, int p2){
                try{
                    gen_switch.setChecked(false);
                    p1.dismiss();
                }catch(Exception e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    p1.dismiss();
                }

            }
        });
        adb.setPositiveButton("Save", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface p1, int p2)
            {
                edit.putString(key, sni.getText().toString()).commit();
                editor.putString("custom_sni_vmodz",sni.getText().toString()).commit();
            }
        });
        adb.create().show();
    }

    public static void addPayloadSwitch(Switch your_switch){
        gen_switch = your_switch;
    }

    private static View sniView(){
        sniInflater = LayoutInflater.from(context);
        sniV = sniInflater.inflate(R.layout.sni_paygen_vmodz, null);
        sni = (EditText) sniV.findViewById(R.id.sni_edit);
        sniLoad();
        return sniV;
    }

    private static void showPaygenSSH(final EditText edit){
        AlertDialog.Builder adb = new PayloadGenerator(context, sp);
        adb.setView(generatorView());
        adb.setTitle("Payload Generator");
        adb.setCancelable(false);
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface p1, int p2){
                p1.dismiss();
            }
        });
        adb.setPositiveButton("Generate", spClick);
        adb.create().show();
    }

    private static View generatorView(){
        inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.paygen_vmodz, null);
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
        rNomal.setOnCheckedChangeListener(cb);
        rSplit.setOnCheckedChangeListener(cb);
        rDirect.setOnCheckedChangeListener(cb);
        cbFront.setOnCheckedChangeListener(cb);
        cbBack.setOnCheckedChangeListener(cb);
        cbRaw.setOnCheckedChangeListener(cb);
        cbDual.setOnCheckedChangeListener(cb);
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
        requestSpin.setOnItemSelectedListener(isl);
        injectSpin.setOnItemSelectedListener(isl);
        load();
        splitSpin.setSelection(0);
        requestSpin.setSelection(0);
        injectSpin.setSelection(0);
        rNomal.setChecked(true);
        return v;
    }

    public static void setProxyKey(String key, String setting){
        proxyKey = key;
        proxySetting = setting;
    }

    private static View mainView(){
        mainInflater = LayoutInflater.from(context);
        mainV = mainInflater.inflate(R.layout.main_paygen_vmodz, null);
        mainEdit = (EditText) mainV.findViewById(R.id.payEdit);
        mainProxy = (EditText) mainV.findViewById(R.id.proxy_ip);
        mainPort = (EditText) mainV.findViewById(R.id.proxy_port);
        proxy_switch = (Switch) mainV.findViewById(R.id.custom_proxy_switch);
        proxy_switch.setOnCheckedChangeListener(cb);
        mainProxy.setEnabled(false);
        mainPort.setEnabled(false);
        mainLoad();
        return mainV;
    }
    private static DialogInterface.OnClickListener spClick = new DialogInterface.OnClickListener(){

        @Override
        public void onClick(DialogInterface p1, int p2)
        {
            switch(p2){
                case BUTTON_POSITIVE:
                    String sPayload = payload.getText().toString();
                    StringBuilder sb = new StringBuilder();
                    String crlf = "[crlf]";
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
        }


    };

    private static AdapterView.OnItemSelectedListener isl = new AdapterView.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
        {
            int id = p1.getId();
            if(id == R.id.request_spin){
                if(p3!=0){
                    if(injectSpin.getSelectedItemPosition() == 1){
                        return;
                    }else{
                        injectSpin.setSelection(2);
                    }
                }
                editor.putInt("reqSpin_vmodz", p3).commit();
            }else if(id == R.id.inject_spin){
                if(p3 !=0 ){
                    requestSpin.setSelection(1);
                }else if(p3 == 0){
                    if(rSplit.isChecked()){
                        rNomal.setChecked(true);
                    }
                    requestSpin.setSelection(0);
                }
                editor.putInt("injSpin_vmodz", p3).commit();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> p1)
        {
        }
    };



    private static void save(){
        prefs = context.getSharedPreferences("status404error_prefs", context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.putString("my_inputted_payload",payload.getText().toString()).commit();
        editor.putBoolean("rNormal_vmodz", rNomal.isChecked()).commit();
        editor.putBoolean("rSplit_vmodz", rSplit.isChecked()).commit();
        editor.putBoolean("rDirect_vmodz", rDirect.isChecked()).commit();
        editor.putBoolean("cbFront_vmodz", cbFront.isChecked()).commit();
        editor.putBoolean("cbBack_vmodz", cbBack.isChecked()).commit();
        editor.putBoolean("cbOnline_vmodz", cbOnline.isChecked()).commit();
        editor.putBoolean("cbForward_vmodz", cbForward.isChecked()).commit();
        editor.putBoolean("cbReverse_vmodz", cbReverse.isChecked()).commit();
        editor.putBoolean("cbKeep_vmodz", cbKeep.isChecked()).commit();
        editor.putBoolean("cbUser_vmodz", cbUser.isChecked()).commit();
        editor.putBoolean("cbReferer_vmodz", cbReferer.isChecked()).commit();
        editor.putBoolean("cbRaw_vmodz", cbRaw.isChecked()).commit();
        editor.putBoolean("cbDual_vmodz", cbDual.isChecked()).commit();
    }

    private static void mainLoad(){
        prefs = context.getSharedPreferences("status404error_prefs", context.MODE_PRIVATE);
        editor = prefs.edit();
        proxy_switch.setChecked(prefs.getBoolean("proxy_enable_vmodz",false));
        mainEdit.setText(prefs.getString("full_payload_vmodz",""));
        mainProxy.setText(prefs.getString("custom_proxyIP_vmodz",""));
        mainPort.setText(prefs.getString("custom_proxyPort_vmodz",""));
    }

    private static void sniLoad(){
        prefs = context.getSharedPreferences("status404error_prefs", context.MODE_PRIVATE);
        editor = prefs.edit();
        sni.setText(prefs.getString("custom_sni_vmodz",""));
    }

    private static void load(){
        prefs = context.getSharedPreferences("status404error_prefs", context.MODE_PRIVATE);
        editor = prefs.edit();
        payload.setText(prefs.getString("my_inputted_payload",""));
        rNomal.setChecked(prefs.getBoolean("rNormal_vmodz", true));
        rSplit.setChecked(prefs.getBoolean("rSplit_vmodz", false));
        rDirect.setChecked(prefs.getBoolean("rDirect_vmodz", false));
        cbBack.setChecked(prefs.getBoolean("cbBack_vmodz", false));
        cbOnline.setChecked(prefs.getBoolean("cbOnline_vmodz", false));
        cbForward.setChecked(prefs.getBoolean("cbForward_vmodz", false));
        cbReverse.setChecked(prefs.getBoolean("cbReverse_vmodz", false));
        cbKeep.setChecked(prefs.getBoolean("cbKeep_vmodz", false));
        cbUser.setChecked(prefs.getBoolean("cbUser_vmodz", false));
        cbReferer.setChecked(prefs.getBoolean("cbReferer_vmodz", false));
        cbRaw.setChecked(prefs.getBoolean("cbRaw_vmodz", false));
        cbDual.setChecked(prefs.getBoolean("cbDual_vmodz", false));
        requestSpin.setSelection(prefs.getInt("reqSpin_vmodz",0));
        injectSpin.setSelection(prefs.getInt("injSpin_vmodz",0));
    }

    private static CompoundButton.OnCheckedChangeListener cb = new CompoundButton.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(CompoundButton p1, boolean p2)
        {
            int id = p1.getId();
            if(id == R.id.rNormal){
                if(p2){
                    splitSpin.setEnabled(false);
                    injectSpin.setEnabled(true);
                    requestSpin.setSelection(0);
                    injectSpin.setSelection(0);
                }
            }else if(id == R.id.rSplit){
                if(p2){
                    splitSpin.setEnabled(true);
                    requestSpin.setSelection(1);
                    injectSpin.setSelection(2);
                    splitSpin.setSelection(2);
                }
            }else if(id == R.id.rDirect){
                if(p2){
                    splitSpin.setEnabled(false);
                    injectSpin.setEnabled(false);
                }
            }else if(id == R.id.cbRaw){
                if(p2){
                    cbDual.setEnabled(false);
                }else{
                    cbDual.setEnabled(true);
                }
            }else if(id == R.id.cbDual){
                if(p2){
                    cbRaw.setEnabled(false);
                }else{
                    cbRaw.setEnabled(true);
                }
            }else if(id == R.id.cbFront){
                if(p2){
                    if(cbBack.isChecked()){
                        cbBack.setChecked(false);
                    }
                }
            }else if(id == R.id.cbBack){
                if(p2){
                    if(cbFront.isChecked()){
                        cbFront.setChecked(false);
                    }
                }
            }else if(id == R.id.custom_proxy_switch){
                if(p2){
                    mainProxy.setEnabled(true);
                    mainPort.setEnabled(true);
                    editor.putBoolean("proxy_enable_vmodz",true).commit();
                    edit.putBoolean(proxyKey,true).commit();
                }else{
                    mainProxy.setEnabled(false);
                    mainPort.setEnabled(false);
                    editor.putBoolean("proxy_enable_vmodz",false).commit();
                    edit.putBoolean(proxyKey, false).commit();
                }
            }else if(id == R.id.cbRaw){
                if(p2){
                    payload.setHint("URL/HOST");
                }else{
                    payload.setHint("URL/HOST (eg: 1.com;2.com;3.com)");
                }
            }
        }

    };

    private static boolean isCustomProxy(){
        if(!prefs.contains("proxy_enable_vmodz")){
            editor.putBoolean("proxy_enable_vmodz",false);
        }
        return prefs.getBoolean("proxy_enable_vmodz",false);
    }
}
