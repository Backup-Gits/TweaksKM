package com.theradcolor.kernel.ui.home;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.theradcolor.kernel.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private HomeViewModel homeViewModel;
    public TextView textView;
    private ProgressDialog mprogress;
    private CardView eb,bb,bal,gm,pm;
    final Handler handler = new Handler();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        textView = root.findViewById(R.id.kernel_info);
        textView.setText(" " + getKernelVersion());
        eb = root.findViewById(R.id.cv_eb);
        bb = root.findViewById(R.id.cv_bb);
        bal = root.findViewById(R.id.cv_bal);
        gm = root.findViewById(R.id.cv_gm);
        pm = root.findViewById(R.id.cv_pm);

        eb.setOnClickListener(this);
        bb.setOnClickListener(this);
        bal.setOnClickListener(this);
        gm.setOnClickListener(this);
        pm.setOnClickListener(this);
        //final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        return root;
    }

    public static String getKernelVersion() {
        try {
            Process p = Runtime.getRuntime().exec("uname -a");
            InputStream is = null;
            if (p.waitFor() == 0) {
                is = p.getInputStream();
            } else {
                is = p.getErrorStream();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = br.readLine();
            Log.i("Kernel Version", line);
            br.close();
            return line;
        } catch (Exception ex) {
            return "ERROR: " + ex.getMessage();
        }
    }

    @Override
    public void onClick(View v) {
        int view = v.getId();
        switch (view) {

            case R.id.cv_eb:
                mprogress = new ProgressDialog(getContext());
                mprogress.setMessage("Applying Profile...");
                mprogress.show();
                mprogress.setCanceledOnTouchOutside(false);
                mprogress.setCancelable(false);

                execCommandLine("echo 40 > /proc/sys/vm/swappiness");
                execCommandLine("echo " + "\"noop\"" + " > /sys/block/mmcblk0/queue/scheduler");
                execCommandLine("echo 0 > /sys/class/kgsl/kgsl-3d0/devfreq/adrenoboost");
                execCommandLine("echo 512 > /sys/block/mmcblk0/queue/read_ahead_kb");
                execCommandLine("echo powersave > /sys/class/kgsl/kgsl-3d0/devfreq/governor");
                execCommandLine("echo 1401600 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
                execCommandLine("echo 1401600 > /sys/devices/system/cpu/cpu4/cpufreq/scaling_max_freq");


                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mprogress.dismiss();
                        Toast.makeText(getContext(),"Successful", Toast.LENGTH_SHORT).show();
                    }
                },2000);
                break;
            case R.id.cv_bb:
                mprogress = new ProgressDialog(getContext());
                mprogress.setMessage("Applying Profile...");
                mprogress.show();
                mprogress.setCanceledOnTouchOutside(false);
                mprogress.setCancelable(false);

                execCommandLine("echo 40 > /proc/sys/vm/swappiness");
                execCommandLine("echo " + "\"cfq\"" + " > /sys/block/mmcblk0/queue/scheduler");
                execCommandLine("echo 0 > /sys/class/kgsl/kgsl-3d0/devfreq/adrenoboost");
                execCommandLine("echo 512 > /sys/block/mmcblk0/queue/read_ahead_kb");
                execCommandLine("echo powersave > /sys/class/kgsl/kgsl-3d0/devfreq/governor");
                execCommandLine("echo 1536000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
                execCommandLine("echo 1747200 > /sys/devices/system/cpu/cpu4/cpufreq/scaling_max_freq");

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mprogress.dismiss();
                        Toast.makeText(getContext(),"Successful", Toast.LENGTH_SHORT).show();
                    }
                },2000);
                break;
            case R.id.cv_bal:
                mprogress = new ProgressDialog(getContext());
                mprogress.setMessage("Applying Profile...");
                mprogress.show();
                mprogress.setCanceledOnTouchOutside(false);
                mprogress.setCancelable(false);

                execCommandLine("echo 40 > /proc/sys/vm/swappiness");
                execCommandLine("echo " + "\"anxiety\"" + " > /sys/block/mmcblk0/queue/scheduler");
                execCommandLine("echo 0 > /sys/class/kgsl/kgsl-3d0/devfreq/adrenoboost");
                execCommandLine("echo 2048 > /sys/block/mmcblk0/queue/read_ahead_kb");
                execCommandLine("echo msm-adreno-tz > /sys/class/kgsl/kgsl-3d0/devfreq/governor");

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mprogress.dismiss();
                        Toast.makeText(getContext(),"Successful", Toast.LENGTH_SHORT).show();
                    }
                },2000);
                break;
            case R.id.cv_gm:
                mprogress = new ProgressDialog(getContext());
                mprogress.setMessage("Applying Profile...");
                mprogress.show();
                mprogress.setCanceledOnTouchOutside(false);
                mprogress.setCancelable(false);

                execCommandLine("echo 40 > /proc/sys/vm/swappiness");
                execCommandLine("echo " + "\"deadline\"" + " > /sys/block/mmcblk0/queue/scheduler");
                execCommandLine("echo 3 > /sys/class/kgsl/kgsl-3d0/devfreq/adrenoboost");
                execCommandLine("echo 2048 > /sys/block/mmcblk0/queue/read_ahead_kb");
                execCommandLine("echo msm-adreno-tz > /sys/class/kgsl/kgsl-3d0/devfreq/governor");

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mprogress.dismiss();
                        Toast.makeText(getContext(),"Successful", Toast.LENGTH_SHORT).show();
                    }
                },2000);
                break;
            case R.id.cv_pm:
                mprogress = new ProgressDialog(getContext());
                mprogress.setMessage("Applying Profile...");
                mprogress.show();
                mprogress.setCanceledOnTouchOutside(false);
                mprogress.setCancelable(false);

                execCommandLine("echo 40 > /proc/sys/vm/swappiness");
                execCommandLine("echo " + "\"bfq\"" + " > /sys/block/mmcblk0/queue/scheduler");
                execCommandLine("echo 2 > /sys/class/kgsl/kgsl-3d0/devfreq/adrenoboost");
                execCommandLine("echo 2048 > /sys/block/mmcblk0/queue/read_ahead_kb");
                execCommandLine("echo msm-adreno-tz > /sys/class/kgsl/kgsl-3d0/devfreq/governor");

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mprogress.dismiss();
                        Toast.makeText(getContext(),"Successful", Toast.LENGTH_SHORT).show();
                    }
                },2000);
                break;
        }
    }

    void execCommandLine(String command)
    {
        Runtime runtime = Runtime.getRuntime();
        Process proc = null;
        OutputStreamWriter osw = null;

        try
        {
            proc = runtime.exec("su");
            osw = new OutputStreamWriter(proc.getOutputStream());
            osw.write(command);
            osw.flush();
            osw.close();
        }
        catch (IOException ex)
        {
            Log.e("execCommandLine()", "Command resulted in an IO Exception: " + command);
            return;
        }
        finally
        {
            if (osw != null)
            {
                try
                {
                    osw.close();
                }
                catch (IOException e){}
            }
        }

        try
        {
            proc.waitFor();
        }
        catch (InterruptedException e){}

        if (proc.exitValue() != 0)
        {
            Log.e("execCommandLine()", "Command returned error: " + command + "\n  Exit code: " + proc.exitValue());
        }
    }

}