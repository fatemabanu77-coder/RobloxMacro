package com.robloxmacro.tsb;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.*;
import android.view.*;
import android.widget.*;
public class OverlayService extends Service {
private WindowManager wm;
private View btn,menu;
private WindowManager.LayoutParams bp,mp;
private MacroConfig cfg=new MacroConfig();
private Handler mh=new Handler(Looper.getMainLooper());
private Runnable mr;
private Handler lph=new Handler(Looper.getMainLooper());
private boolean isLp=false,drag=false,locked=false;
private int ix,iy;
private float itx,ity;
private static final String CH="TSBMacro";
public IBinder onBind(Intent i){return null;}
public void onCreate(){
super.onCreate();
if(Build.VERSION.SDK_INT>=26){NotificationChannel c=new NotificationChannel(CH,"TSB",NotificationManager.IMPORTANCE_LOW);((NotificationManager)getSystemService(NotificationManager.class)).createNotificationChannel(c);}
Notification n=new Notification.Builder(this,CH).setContentTitle("TSB Macro").setSmallIcon(android.R.drawable.ic_menu_manage).build();
startForeground(1,n);
wm=(WindowManager)getSystemService(WINDOW_SERVICE);
int sz=(int)(60*getResources().getDisplayMetrics().density);
btn=new View(this);
GradientDrawable d=new GradientDrawable();
d.setShape(GradientDrawable.OVAL);
d.setColor(0xFF0A84FF);
d.setStroke(4,Color.WHITE);
btn.setBackground(d);
bp=new WindowManager.LayoutParams(sz,sz,Build.VERSION.SDK_INT>=26?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_PHONE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);
bp.gravity=Gravity.TOP|Gravity.START;
bp.x=100;bp.y=300;
wm.addView(btn,bp);
TextView lb=new TextView(this);
lb.setText("M");lb.setTextColor(Color.WHITE);lb.setTextSize(18);lb.setGravity(Gravity.CENTER);
WindowManager.LayoutParams lp2=new WindowManager.LayoutParams(sz,sz,Build.VERSION.SDK_INT>=26?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_PHONE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,PixelFormat.TRANSLUCENT);
lp2.gravity=Gravity.TOP|Gravity.START;lp2.x=100;lp2.y=300;
wm.addView(lb,lp2);
btn.setOnTouchListener((v,e)->{
switch(e.getAction()){
case MotionEvent.ACTION_DOWN:
drag=false;ix=bp.x;iy=bp.y;itx=e.getRawX();ity=e.getRawY();isLp=false;
lph.postDelayed(()->{isLp=true;Vibrator vb=(Vibrator)getSystemService(VIBRATOR_SERVICE);if(vb!=null)vb.vibrate(80);showMenu();},600);
return true;
case MotionEvent.ACTION_MOVE:
float dx=e.getRawX()-itx,dy=e.getRawY()-ity;
if(!drag&&(Math.abs(dx)>10||Math.abs(dy)>10)){drag=true;lph.removeCallbacksAndMessages(null);}
if(drag&&!locked){bp.x=ix+(int)dx;bp.y=iy+(int)dy;lp2.x=bp.x;lp2.y=bp.y;wm.updateViewLayout(btn,bp);wm.updateViewLayout(lb,lp2);}
return true;
case MotionEvent.ACTION_UP:
lph.removeCallbacksAndMessages(null);
if(!drag&&!isLp){cfg.macroRunning=!cfg.macroRunning;GradientDrawable d2=new GradientDrawable();d2.setShape(GradientDrawable.OVAL);d2.setColor(cfg.macroRunning?0xFF30D158:0xFF0A84FF);d2.setStroke(4,cfg.macroRunning?0xFFFFD60A:0xFFFFFFFF);btn.setBackground(d2);Toast.makeText(getApplicationContext(),cfg.macroRunning?"Macro ON":"Macro OFF",Toast.LENGTH_SHORT).show();if(cfg.macroRunning){mr=new Runnable(){public void run(){if(!cfg.macroRunning)return;mh.postDelayed(this,cfg.m1SpeedMs);}};mh.post(mr);}else{if(mr!=null)mh.removeCallbacks(mr);}}
return true;}
return false;});
}
private void showMenu(){
if(menu!=null){wm.removeView(menu);menu=null;return;}
LinearLayout r=new LinearLayout(this);
r.setOrientation(LinearLayout.VERTICAL);
r.setPadding(40,40,40,40);
GradientDrawable bg=new GradientDrawable();
bg.setCornerRadius(40);bg.setColor(0xF0141432);bg.setStroke(2,0xFF0A84FF);
r.setBackground(bg);
TextView t=new TextView(this);t.setText("TSB Macro Settings");t.setTextColor(0xFF0A84FF);t.setTextSize(16);t.setGravity(Gravity.CENTER);r.addView(t);
r.addView(mkSw("M1 Spam",cfg.m1Spam,c->cfg.m1Spam=c));
r.addView(mkSw("Auto Combo",cfg.autoCombo,c->cfg.autoCombo=c));
r.addView(mkSw("Auto Ultimate",cfg.autoUlt,c->cfg.autoUlt=c));
r.addView(mkSw("Auto Dodge",cfg.autoDodge,c->cfg.autoDodge=c));
r.addView(mkSw("Lock Target",cfg.lockTarget,c->cfg.lockTarget=c));
r.addView(mkSw("Lock Button Position",locked,c->{locked=c;Toast.makeText(getApplicationContext(),locked?"Locked!":"Unlocked!",Toast.LENGTH_SHORT).show();}));
Button cb=new Button(this);cb.setText("Close");cb.setOnClickListener(v->{wm.removeView(menu);menu=null;});r.addView(cb);
ScrollView sv=new ScrollView(this);sv.addView(r);
menu=sv;
mp=new WindowManager.LayoutParams(600,WindowManager.LayoutParams.WRAP_CONTENT,Build.VERSION.SDK_INT>=26?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_PHONE,WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,PixelFormat.TRANSLUCENT);
mp.gravity=Gravity.CENTER;
wm.addView(menu,mp);
}
private View mkSw(String l,boolean i,java.util.function.Consumer<Boolean> cb){
LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.HORIZONTAL);r.setPadding(0,16,0,16);
TextView t=new TextView(this);t.setText(l);t.setTextColor(Color.WHITE);t.setTextSize(13);
LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1);t.setLayoutParams(lp);r.addView(t);
Switch sw=new Switch(this);sw.setChecked(i);sw.setOnCheckedChangeListener((b,c)->cb.accept(c));r.addView(sw);return r;
}
public void onDestroy(){super.onDestroy();if(btn!=null)wm.removeView(btn);if(menu!=null)wm.removeView(menu);}
  }
