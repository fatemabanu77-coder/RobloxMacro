package com.robloxmacro.tsb;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends Activity {
private static final int REQ=1234;
@Override
protected void onCreate(Bundle b){
super.onCreate(b);
setContentView(R.layout.activity_main);
TextView s=findViewById(R.id.statusText);
Button p=findViewById(R.id.btnPermission);
Button st=findViewById(R.id.btnStart);
Button sp=findViewById(R.id.btnStop);
update(s,p,st,sp);
p.setOnClickListener(v->startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+getPackageName())),REQ));
st.setOnClickListener(v->{if(Settings.canDrawOverlays(this)){startService(new Intent(this,OverlayService.class));update(s,p,st,sp);}});
sp.setOnClickListener(v->{stopService(new Intent(this,OverlayService.class));update(s,p,st,sp);});
}
private void update(TextView s,Button p,Button st,Button sp){
if(Settings.canDrawOverlays(this)){s.setText("Ready! Start the overlay.");p.setVisibility(View.GONE);st.setEnabled(true);}
else{s.setText("Grant permission first!");p.setVisibility(View.VISIBLE);st.setEnabled(false);}
}
protected void onActivityResult(int q,int r,Intent d){super.onActivityResult(q,r,d);if(q==REQ)update(findViewById(R.id.statusText),findViewById(R.id.btnPermission),findViewById(R.id.btnStart),findViewById(R.id.btnStop));}
}
