package com.example.user.devicemeters;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

class OneCpuInfo {
    long idle = 0;
    long total = 0;
}

public class MainActivity extends Activity {
    private TextView availableMemory, availableStorage,txtCPU,coreText;
    private ActivityManager memoryAM;
    private Button clearMemory,processList,connectUs;
    private ImageView imageView,imageView2;
    private Handler myHandler;
    private AnimationDrawable animationDrawable,animationDrawable2;
    private static OneCpuInfo mLastInfo = null;
    private static int sLastCpuCoreCount = -1;
    private int cpuCore=0;
    private android.os.Handler handlerCPU;
    private Thread deviceTH;
    boolean RUN_THREAD = true;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (mLastInfo == null) {
            mLastInfo = takeCpuUsageSnapshot();
        }
        handlerCPU = new Handler();

        availableMemory = (TextView) findViewById(R.id.availableMemory_text);
        availableStorage = (TextView) findViewById(R.id.availableStorage_text);
        coreText = (TextView)findViewById(R.id.coreText);
        txtCPU = (TextView) findViewById(R.id.txtCPU);
        processList = (Button)findViewById(R.id.plbtn);
        processList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pintent = new Intent();
                pintent.setClass(MainActivity.this, ProcessList.class);
                pintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(pintent);

            }
        });
        connectUs = (Button)findViewById(R.id.conbtn);
        connectUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cintent = new Intent();
                cintent.setClass(MainActivity.this, ConnectUs.class);
                cintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(cintent);
            }
        });
        clearMemory = (Button) findViewById(R.id.clearMemory_btn);

        clearMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("memoryLog", "MemoryButton onClicked !");
                killBackgroundPro();
                Toast.makeText(MainActivity.this, "(σ′▽‵)σ  Success!", Toast.LENGTH_SHORT).show();

            }
        });
        cpuCore = calcCpuCoreCount();

        imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setBackgroundResource(R.drawable.animation_list);
        animationDrawable = (AnimationDrawable)imageView.getBackground();
        animationDrawable.start();

        imageView2 = (ImageView)findViewById(R.id.imageView2);
        imageView2.setBackgroundResource(R.drawable.animation_list2);
        animationDrawable2 = (AnimationDrawable)imageView2.getBackground();
        animationDrawable2.start();
//        Timer timer= new Timer(true);
//        timer.schedule(new MyTimerTask(), 1000, 3000);
        Log.i("memoryLog", "onCreate !");
    }

    @Override
    protected void onStart() {
        super.onStart();
//        timer.schedule(new MyTimerTask(), 1000, 3000);
        Log.i("memoryLog", "onStart !");
    }

    @Override
    protected void onResume() {
        super.onResume();
        availableStorage.setText(getSdStorage() + "MB");
        availableMemory.setText(getSystemAvaialbeMemorySize());
        timer= new Timer(true);
        timer.schedule(new MyTimerTask(), 1000, 3000);
        coreText.setText("" + cpuCore);

//        deviceTH = new deviceTH();
//        RUN_THREAD = true;
//        deviceTH.start();
//        Log.i("memoryLog", "onResume  deviceTH is start !");


    }

    @Override
    protected void onPause() {
        super.onPause();
//        RUN_THREAD = false;
//        deviceTH.interrupt();

        timer.cancel();


    }

    @Override
    protected void onStop() {
        super.onStop();
//        RUN_THREAD = false;
//        deviceTH.interrupt();
        timer.cancel();

        Log.i("memoryLog", "onStop !");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("memoryLog", "onDestroy  !");
    }

    private long getSdStorage() {
        File sd = android.os.Environment.getExternalStorageDirectory();

        long sdStorage = sd.getUsableSpace() / 1024 / 1024;
        Log.i("memoryLog", "The Availabel Storage Size is" + sd.getTotalSpace());
        return sdStorage;
    }


public static OneCpuInfo takeCpuUsageSnapshot() {


    String[] cmdArgs = {"/system/bin/cat", "/proc/stat"};

    String cpuLine = "";
    StringBuffer cpuBuffer = new StringBuffer();

    ProcessBuilder cmd = new ProcessBuilder(cmdArgs);

    try {

        Process process = cmd.start();

        InputStream in = process.getInputStream();


        byte[] lineBytes = new byte[1024];

        while (in.read(lineBytes) != -1) {

            cpuBuffer.append(new String(lineBytes));

        }

        in.close();

    } catch (IOException e) {

    }

    cpuLine = cpuBuffer.toString();


    int start = cpuLine.indexOf("cpu");
    int end = cpuLine.indexOf("cpu0");

    cpuLine = cpuLine.substring(start, end);

    //     user  nice system idle iowait  irq     softirq     steal
    //cpu  48200 4601 35693 979258 5095 1 855 0 0 0
    //cpu0 26847 1924 25608 212324 2212 1 782 0 0 0
    //cpu1 8371 1003 4180 254096 1026 0 50 0 0 0
    //cpu2 8450 983 3916 252872 1304 0 9 0 0 0
    //cpu3 4532 691 1989 259966 553 0 14 0 0 0

    final String[] tokens = cpuLine.split(" +");
    final OneCpuInfo OCI = new OneCpuInfo();
    OCI.idle = Long.parseLong(tokens[4]);
    OCI.total = Long.parseLong(tokens[1])
            + Long.parseLong(tokens[2])
            + Long.parseLong(tokens[3])
            + OCI.idle
            + Long.parseLong(tokens[5])
            + Long.parseLong(tokens[6])
            + Long.parseLong(tokens[7]);


    for(String x :tokens){

            Log.e("cpu", x);

    }
    return OCI;
}
    public static int calcCpuUsages(OneCpuInfo currentInfo, OneCpuInfo lastInfo) {
//        float cpuUseide=0;
//        float cpuUsetotal=0;
//        if(currentInfo.total < lastInfo.total){
//            cpuUseide =(lastInfo.idle-currentInfo.idle);
//            cpuUsetotal =(lastInfo.total-currentInfo.total);
//        }else{
//            cpuUseide =(currentInfo.idle-lastInfo.idle);
//            cpuUsetotal =(currentInfo.total-lastInfo.total);
//        }
        float cpuUseide=(currentInfo.idle-lastInfo.idle);
        float cpuUsetotal=(currentInfo.total-lastInfo.total);

        float cpuUse = cpuUseide/cpuUsetotal;
        cpuUse = 100-(cpuUse*100);

        calcCpuCoreCount();
        mLastInfo = currentInfo;
        Log.e("cpu","cpuUseide:"+cpuUseide+"cpuUsetotal:"+cpuUsetotal);
        Log.e("cpu","CPU:"+cpuUse);
        Log.e("cpu", "LastTotal:" + lastInfo.total + "LastIdle:" + lastInfo.idle);
        Log.e("cpu", "NowTotal:" + currentInfo.total + "NowIdle:" + currentInfo.idle);
//        NumberFormat nf = NumberFormat.getInstance();
//        nf.setMaximumFractionDigits(1);
//        Math.round(cpuUse);
//        String cpu = nf.format(cpuUse);

        int cpu2 = Math.round(cpuUse);
        int abcpu = Math.abs(cpu2);
        Log.e("cpu", "Round CPU:" + cpu2+"||"+abcpu);
        return abcpu;
    }
    private String getSystemAvaialbeMemorySize() {
        memoryAM = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

        memoryAM.getMemoryInfo(memoryInfo);
        long memSize = memoryInfo.availMem;
        String availMemStr = formateFileSize(memSize);
        Log.i("memoryLog", "The Availabel Memory Size is" + availMemStr);
        return availMemStr;
    }

    private void killBackgroundPro() {
        List<ActivityManager.RunningAppProcessInfo> appProcessList = memoryAM
                .getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
            String[] packageList = appProcessInfo.pkgList;
            for (String pkg : packageList) {
                memoryAM.killBackgroundProcesses(pkg);
            }
        }
    }

    private String formateFileSize(long size) {
        return Formatter.formatFileSize(MainActivity.this, size);
    }


    public class MyTimerTask extends TimerTask{
        @Override
        public void run() {
            final int CPU = calcCpuUsages(takeCpuUsageSnapshot(), mLastInfo);

            handlerCPU.post(new Runnable() {

                @Override
                public void run() {

                    txtCPU.setText(CPU+"%");

                    availableMemory.setText(getSystemAvaialbeMemorySize());

                }
            });
        }
    }
    public static int calcCpuCoreCount() {

        if (sLastCpuCoreCount >= 1) {
            return sLastCpuCoreCount;
        }

        try {
            // Get directory containing CPU info
            final File dir = new File("/sys/devices/system/cpu/");
            // Filter to only list the devices we care about
            final File[] files = dir.listFiles(new FileFilter() {

                public boolean accept(File pathname) {
                    //Check if filename is "cpu", followed by a single digit number
                    if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                        return true;
                    }
                    return false;
                }
            });

            sLastCpuCoreCount = files.length;

        } catch(Exception e) {
            sLastCpuCoreCount = Runtime.getRuntime().availableProcessors();
        }
            int core = Runtime.getRuntime().availableProcessors();
        Log.e("cpu","CpuCore:"+core);
        return sLastCpuCoreCount;

    }
    class deviceTH extends Thread {
        public void run() {
            while (RUN_THREAD) {
                final int CPU = calcCpuUsages(takeCpuUsageSnapshot(), mLastInfo);
                handlerCPU.post(new Runnable() {
                    public void run() {
                        txtCPU.setText(CPU + "%");
                        availableMemory.setText(getSystemAvaialbeMemorySize());
                    }
                });
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

