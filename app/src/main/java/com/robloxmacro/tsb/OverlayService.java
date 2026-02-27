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
