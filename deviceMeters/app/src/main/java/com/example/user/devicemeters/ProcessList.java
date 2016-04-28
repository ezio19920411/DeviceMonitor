package com.example.user.devicemeters;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ProcessList extends AppCompatActivity{
    private ListView processList;
    private Button backBtn;
    private TextView totalp;
    private ActivityManager mActivityManager = null;
    private List<ProcessInfo>processInfoList=null;
    private ProcessAdapter processAdapter;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_list);
        context=this;
        processList = (ListView)findViewById(R.id.listView);
        backBtn = (Button)findViewById(R.id.backbtn);
        totalp = (TextView)findViewById(R.id.textTotal);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bi = new Intent();
//                bi.setClass(ProcessList.this,MainActivity.class);
//                bi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(bi);
                finish();
            }
        });
        mActivityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);

        processInfoList = new ArrayList<ProcessInfo>();
        this.registerForContextMenu(processList);
        List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager.getRunningAppProcesses();

        for(ActivityManager.RunningAppProcessInfo appProcessInfo:appProcessList){

            int pid = appProcessInfo.pid;
            int uid = appProcessInfo.uid;
            String processName = appProcessInfo.processName;
            int[] myMempid = new int[] { pid };
            Debug.MemoryInfo[] memoryInfo = mActivityManager
                    .getProcessMemoryInfo(myMempid);
            int memSize = memoryInfo[0].dalvikPrivateDirty;

            ProcessInfo processInfo = new ProcessInfo();
            processInfo.setPid(pid);
            processInfo.setUid(uid);
            processInfo.setMemSize(memSize);
            processInfo.setProcessName(processName);
            processInfoList.add(processInfo);
        }
        processAdapter = new ProcessAdapter(context,processInfoList);
        processList.setAdapter(processAdapter);

        totalp.setText("Process_Total:"+processInfoList.size());
    }
}

