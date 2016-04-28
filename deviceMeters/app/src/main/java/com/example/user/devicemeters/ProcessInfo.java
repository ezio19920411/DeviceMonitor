package com.example.user.devicemeters;

/**
 * Created by user on 2016/3/26.
 */
public class ProcessInfo {
    private int pid;
    private int uid;
    private int memSize;
    private String processName;

    public void setPid(int p){
        pid=p;
    }
    public void setUid(int u){
        uid=u;
    }
    public void setMemSize(int m){
        memSize=m;
    }
    public void setProcessName(String pn){
        processName=pn;
    }
    public int getPid(){
        return pid;
    }
    public int getUid(){
        return uid;
    }
    public int getMemSize(){
        return memSize;
    }
    public String getProcessName(){
        return processName;
    }
}
