package com.robloxmacro.tsb;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
public class OverlayService extends Service {
private WindowManager wm;
private View btn,menu;
private WindowManager.LayoutParams bp,mp;
private MacroConfig cfg=new MacroConfig();
private Handler mh=new Handler(Looper.getMainLooper());
private Runnable mr;
private Handler lph=new Handler(Looper.getMainLooper());
private boolean lp=false,drag=false,locked=false;
private int ix,iy;
private float itx,ity;
private static final String CH="TSBMacro";
public IBinder onBind(Intent i){return null;}
  public void onCreate(){
super.onCreate();
createChannel();
startForeground(1,buildNote());
wm=(WindowManager)getSystemService(WINDOW_SERVICE);
createBtn();
}
private void createBtn(){
btn=new View(this);
android.graphics.drawable.GradientDrawable s=new android.graphics.drawable.GradientDrawable();
s.setShape(android.graphics.drawable.GradientDrawable.OVAL);
s.setColor(Color.parseColor("#FF0A84FF"));
s.setStroke(4,Color.WHITE);
btn.setBackground(s);
int sz=dpToPx(60);
bp=new WindowManager.LayoutParams(sz,sz,Build.VERSION.SDK_INT>=26?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_PHONE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);
bp.gravity=Gravity.TOP|Gravity.START;
bp.x=cfg.buttonX;bp.y=cfg.buttonY;
wm.addView(btn,bp);
TextView lb=new TextView(this);
lb.setText("M");lb.setTextColor(Color.WHITE);lb.setTextSize(18);lb.setGravity(Gravity.CENTER);
WindowManager.LayoutParams lp2=new WindowManager.LayoutParams(sz,sz,Build.VERSION.SDK_INT>=26?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_PHONE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,PixelFormat.TRANSLUCENT);
lp2.gravity=Gravity.TOP|Gravity.START;lp2.x=cfg.buttonX;lp2.y=cfg.buttonY;
wm.addView(lb,lp2);
btn.setOnTouchListener((v,e)->{
switch(e.getAction()){
case MotionEvent.ACTION_DOWN:
drag=false;ix=bp.x;iy=bp.y;itx=e.getRawX();ity=e.getRawY();lp=false;
lph.postDelayed(()->{lp=true;vib();showMenu();},600);
return true;
case MotionEvent.ACTION_MOVE:
float dx=e.getRawX()-itx,dy=e.getRawY()-ity;
if(!drag&&(Math.abs(dx)>10||Math.abs(dy)>10)){drag=true;lph.removeCallbacksAndMessages(null);}
if(drag&&!locked){bp.x=ix+(int)dx;bp.y=iy+(int)dy;lp2.x=bp.x;lp2.y=bp.y;wm.updateViewLayout(btn,bp);wm.updateViewLayout(lb,lp2);}
return true;
case MotionEvent.ACTION_UP:
lph.removeCallbacksAndMessages(null);
if(!drag&&!lp){toggle();upda
private void updateColor(){
android.graphics.drawable.GradientDrawable s=new android.graphics.drawable.GradientDrawable();
s.setShape(android.graphics.drawable.GradientDrawable.OVAL);
s.setColor(cfg.macroRunning?Color.parseColor("#FF30D158"):Color.parseColor("#FF0A84FF"));
s.setStroke(4,cfg.macroRunning?Color.parseColor("#FFFFD60A"):Color.WHITE);
btn.setBackground(s);
}
private void toggle(){
cfg.macroRunning=!cfg.macroRunning;
if(cfg.macroRunning){startLoop();Toast.makeText(this,"Macro ON",Toast.LENGTH_SHORT).show();}
else{stopLoop();Toast.makeText(this,"Macro OFF",Toast.LENGTH_SHORT).show();}
}
private void startLoop(){
mr=new Runnable(){public void run(){if(!cfg.macroRunning)return;mh.postDelayed(this,cfg.m1SpeedMs);}};
mh.post(mr);
}
private void stopLoop(){if(mr!=null)mh.removeCallbacks(mr);}
private void showMenu(){
if(menu!=null){dismissMenu();return;}
menu=buildMenu();
mp=new WindowManager.LayoutParams(dpToPx(300),WindowManager.LayoutParams.WRAP_CONTENT,Build.VERSION.SDK_INT>=26?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_PHONE,WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,PixelFormat.TRANSLUCENT);
mp.gravity=Gravity.CENTER;
wm.addView(menu,mp);
}
private void dismissMenu(){if(menu!=null){wm.removeView(menu);menu=null;}}
private void vib(){Vibrator v=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);if(v!=null)v.vibrate(80);}
private int dpToPx(int dp){return(int)(dp*getResources().getDisplayMetrics().density);}
private View buildMenu(){
LinearLayout r=new LinearLayout(this);
r.setOrientation(LinearLayout.VERTICAL);
r.setPadding(dpToPx(16),dpToPx(16),dpToPx(16),dpToPx(16));
android.graphics.drawable.GradientDrawable bg=new android.graphics.drawable.GradientDrawable();
bg.setCornerRadius(dpToPx(16));
bg.setColor(Color.parseColor("#F0141432"));
bg.setStroke(2,Color.parseColor("#FF0A84FF"));
r.setBackground(bg);
TextView t=new TextView(this);
t.setText("TSB Macro Settings");
t.setTextColor(Color.parseColor("#FF0A84FF"));
t.setTextSize(16);t.setGravity(Gravity.CENTER);
r.addView(t);
r.addView(mkSw("M1 Spam",cfg.m1Spam,c->cfg.m1Spam=c));
r.addView(mkSk("M1 Speed",50,500,cfg.m1SpeedMs,v->cfg.m1SpeedMs=v));
r.addView(mkSw("Auto Combo",cfg.autoCombo,c->cfg.autoCombo=c));
r.addView(mkSk("Combo Pause",40,300,cfg.comboPauseMs,v->cfg.comboPauseMs=v));
r.addView(mkSw("Auto Ultimate",cfg.autoUlt,c->cfg.autoUlt=c));
r.addView(mkSw("Auto Dodge",cfg.autoDodge,c->cfg.autoDodge=c));
r.addView(mkSw("Lock Target",cfg.lockTarget,c->cfg.lockTarget=c));
r.addView(mkSw("Lock Button Position",locked,c->{locked=c;Toast.makeText(this,locked?"Locked!":"Unlocked!",Toast.LENGTH_SHORT).show();}));
android.widget.Button cb=new android.widget.Button(this);
cb.setText("Close");cb.setBackgroundColor(Color.parseColor("#FF0A84FF"));cb.setTextColor(Color.WHITE);
cb.setOnClickListener(v->dismissMenu());
r.addView(cb);
ScrollView sv=new ScrollView(this);sv.addView(r);return sv;
}
interface BC{void c(boolean v);}
interface IC{void c(int v);}
private View mkSw(String l,boolean i,BC cb){
LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.HORIZONTAL);r.setPadding(0,dpToPx(6),0,dpToPx(6));
TextView t=new TextView(this);t.setText(l);t.setTextColor(Color.WHITE);t.setTextSize(13);
LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1);t.setLayoutParams(lp);
r.addView(t);Switch sw=new Switch(this);sw.setChecked(i);sw.setOnCheckedChangeListener((b,c)->cb.c(c));r.addView(sw);return r;
}
private View mkSk(String l,int mn,int mx,int cur,IC cb){
LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);
TextView t=new TextView(this);t.setText(l+": "+cur);t.setTextColor(Color.WHITE);t.setTextSize(12);c.addView(t);
SeekBar s=new SeekBar(this);s.setMax(mx-mn);s.setProgress(cur-mn);
s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
public void onProgressChanged(SeekBar sb,int p,boolean f){int v=p+mn;t.setText(l+": "+v);cb.c(v);}
public void onStartTrackingTouch(SeekBar sb){}
public void onStopTrackingTouch(SeekBar sb){}});
c.addView(s);return c;
}
private void createChannel(){
if(Build.VERSION.SDK_INT>=26){
NotificationChannel ch=new NotificationChannel(CH,"TSB Macro",NotificationManager.IMPORTANCE_LOW);
NotificationManager nm=getSystemService(NotificationManager.class);if(nm!=null)nm.createNotificationChannel(ch);}
}
private Notification buildNote(){
Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(this,CH):new Notification.Builder(this);
return b.setContentTitle("TSB Macro").setContentText("Running").setSmallIcon(android.R.drawable.ic_menu_manage).build();
}
public void onDestroy(){super.onDestroy();stopLoop();if(btn!=null)wm.removeView(btn);dismissMenu();}
  }
