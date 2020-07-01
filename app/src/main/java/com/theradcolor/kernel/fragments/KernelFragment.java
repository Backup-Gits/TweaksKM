package com.theradcolor.kernel.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;

import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.RootUtils;
import com.theradcolor.kernel.R;
import com.theradcolor.kernel.activities.KcalActivity;
import com.theradcolor.kernel.activities.SpectrumActivity;
import com.theradcolor.kernel.utils.kernel.Battery;
import com.theradcolor.kernel.utils.kernel.Network;
import com.theradcolor.kernel.utils.kernel.Sound;
import com.theradcolor.kernel.utils.kernel.sRGB;

import java.util.List;


public class KernelFragment extends Fragment implements View.OnClickListener{

    Network mNetwork; Battery mBattery; Sound mSound;
    View root;
    TextView vib,ffc,tcp;
    SeekBar seekBar;
    LinearLayout sRGB, kCAL, spectrum, vibration, ll_ffc, ll_tcp, hpg, mcg;
    int progressChangedValue = 1;
    public static final int MIN_VIBRATION = 116;
    public static final int MAX_VIBRATION = 3596;
    int  vibrationValue;
    SharedPreferences.Editor editor;
    Switch vibSW, srgbSW;
    SharedPreferences preferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_kernel, container, false);

        InitView();
        InitUI();

        final Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                vib.setText(progress + getString(R.string.percent));
                Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrationValue = (int) (progress / 100.0 * (MAX_VIBRATION - MIN_VIBRATION) + MIN_VIBRATION);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                }
                RootUtils.runCommand("echo " +vibrationValue+ " > /sys/devices/virtual/timed_output/vibrator/vtg_level");
                v.cancel();
                editor = preferences.edit();
                editor.putInt("vibration",progressChangedValue);
                editor.apply();
                v.vibrate(50);
            }
        });
        setsw();
        if(setvib()==-1){
            seekBar.setEnabled(false);
            vib.setText(" ");
            vibSW.setEnabled(false);
        }else{
            seekBar.setProgress(setvib());
        }
        return root;
    }

    public void InitView(){
        seekBar = root.findViewById(R.id.vibration);
        vib = root.findViewById(R.id.pervib);
        ffc = root.findViewById(R.id.txt_ffc);
        tcp = root.findViewById(R.id.txt_tcp);
        sRGB = root.findViewById(R.id.ll_srgb);
        kCAL = root.findViewById(R.id.ll_kcal);
        spectrum = root.findViewById(R.id.ll_spec);
        vibration = root.findViewById(R.id.ll_vib);
        ll_ffc = root.findViewById(R.id.ll_ffc);
        ll_tcp = root.findViewById(R.id.ll_tcp);
        vibSW = root.findViewById(R.id.vibsw);
        srgbSW = root.findViewById(R.id.srgbsw);

        mBattery = new Battery();
        mNetwork = new Network();
        mSound = new Sound();

        hpg = root.findViewById(R.id.ll_hpg);
        mcg = root.findViewById(R.id.ll_mcg);
        hpg.setOnClickListener(this);
        mcg.setOnClickListener(this);
    }

    public void InitUI(){
        seekBar.setPadding(16,16,16,16);
        spectrum.setOnClickListener(this);
        vibSW.setOnCheckedChangeListener(myCheckboxListener);
        srgbSW.setOnCheckedChangeListener(myCheckboxListener);
        preferences = getActivity().getSharedPreferences("preferences",Context.MODE_PRIVATE);
        vib.setText(preferences.getInt("vibration",1)+getString(R.string.percent));
        seekBar.setProgress(preferences.getInt("vibration",1));
        sRGB.setOnClickListener(this);
        kCAL.setOnClickListener(this);
        vibration.setOnClickListener(this);
        ll_ffc.setOnClickListener(this);
        ll_tcp.setOnClickListener(this);
        ffc.setText(mBattery.FastChargeStatus());
        tcp.setText(mNetwork.getTcpCongestion());
    }

    public void refreshUI(){
        vib.setText(preferences.getInt("vibration",1)+getString(R.string.percent));
        seekBar.setProgress(preferences.getInt("vibration",1));
        ffc.setText(mBattery.FastChargeStatus());
        tcp.setText(mNetwork.getTcpCongestion());
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUI();
    }

    private int setvib(){
        String vib = RootUtils.runCommand("cat /sys/devices/virtual/timed_output/vibrator/vtg_level");
        if(vib==null){
            return -1;
        }
        int hapticval = Integer.parseInt(vib);
        int x = (int) ((hapticval * 100.0 - 100.0 * MIN_VIBRATION) /  (MAX_VIBRATION - MIN_VIBRATION));
        return x;
    }

    private void setsw(){
        if(preferences.getBoolean("vibsw",false)){
            vibSW.setChecked(true);
        }
        if(preferences.getBoolean("srgbsw",false)){
            srgbSW.setChecked(true);
        }
    }

    private CompoundButton.OnCheckedChangeListener myCheckboxListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()){
                case R.id.vibsw:
                    if(vibSW.isChecked()){
                        editor = preferences.edit();
                        editor.putBoolean("vibsw",true);
                        editor.putInt("vibval",getVibration());
                        editor.apply();
                    }else{
                        editor = preferences.edit();
                        editor.putBoolean("vibsw",false);
                        editor.apply();
                    }
                    break;
                case R.id.srgbsw:
                    if(srgbSW.isChecked()){
                        editor = preferences.edit();
                        editor.putBoolean("srgbsw",true);
                        editor.apply();
                    }else{
                        editor = preferences.edit();
                        editor.putBoolean("srgbsw",false);
                        editor.apply();
                    }
                    break;
            }
        }
    };

    private int getVibration() {
      String vib = RootUtils.runCommand("cat /sys/devices/virtual/timed_output/vibrator/vtg_level");
      int vibval = Integer.parseInt(vib);
      return vibval;
    }

    private void srgb()
    {
        com.theradcolor.kernel.utils.kernel.sRGB sRGB = new sRGB();;
        AlertDialog.Builder alertDialog =  new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.Theme_AppCompat_DayNight_Dialog_Alert))
                .setTitle("sRGB colors")
                .setPositiveButton("On", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       sRGB.srgbON();
                    }
                })
                .setNegativeButton("Off", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sRGB.srgbOFF();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        int view = v.getId();
        switch (view) {
            case R.id.ll_srgb:
                srgb();
                break;
            case R.id.ll_kcal:
                startActivity(new Intent(getContext(), KcalActivity.class));
                break;
            case R.id.ll_spec:
                startActivity(new Intent(getContext(), SpectrumActivity.class));
                break;
            case R.id.ll_vib:
                break;
            case R.id.ll_ffc:
                if(mBattery.FastChargeStatus().equals("Enabled")){
                    mBattery.ForceFastChargeEnable(false, getContext());
                }else if(mBattery.FastChargeStatus().equals("Disabled")){
                    mBattery.ForceFastChargeEnable(true, getContext());
                }
                refreshUI();
                break;
            case R.id.ll_tcp:
                tcpDialog(getView());
                break;
            case R.id.ll_hpg:
                hpgDialog(getView());
                break;
            case R.id.ll_mcg:
                mcgDialog(getView());
                break;
        }
    }

    SeekBar hp_lft_sb, hp_rgt_sb, mp_sb;
    TextView hp_lft_txt, hp_rgt_txt, mp_txt;

    public void hpgDialog(View view) {        // create an alert builder
        AlertDialog.Builder builder =  new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.Theme_AppCompat_DayNight_Dialog_Alert));
        builder.setTitle(R.string.title_hp_gain);        // set the custom layout
        final View hpLayout = getLayoutInflater().inflate(R.layout.headgain_dialog, null);

        hp_lft_sb = hpLayout.findViewById(R.id.hp_left);
        hp_rgt_sb = hpLayout.findViewById(R.id.hp_right);
        hp_lft_sb.setProgress(Utils.strToInt(mSound.getHeadphoneFlar("left")));
        hp_rgt_sb.setProgress(Utils.strToInt(mSound.getHeadphoneFlar("right")));
        hp_lft_txt = hpLayout.findViewById(R.id.hp_left_txt);
        hp_rgt_txt = hpLayout.findViewById(R.id.hp_right_txt);
        hp_lft_txt.setText(""+mSound.getHeadphoneFlar("left"));
        hp_rgt_txt.setText(""+mSound.getHeadphoneFlar("right"));

        hp_lft_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                hp_lft_txt.setText(""+progress);
                mSound.setHeadphoneFlar("left", ""+progress, getContext());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        hp_rgt_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                hp_rgt_txt.setText(""+progress);
                mSound.setHeadphoneFlar("right", ""+progress, getContext());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        builder.setView(hpLayout);        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
            }
        });        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void mcgDialog(View view) {        // create an alert builder
        AlertDialog.Builder builder =  new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.Theme_AppCompat_DayNight_Dialog_Alert));
        builder.setTitle(R.string.title_mp_gain);        // set the custom layout
        final View mpLayout = getLayoutInflater().inflate(R.layout.micgain_dialog, null);
        mp_sb = mpLayout.findViewById(R.id.mp_gain);
        mp_sb.setProgress(Utils.strToInt(mSound.getMicrophoneFlar()));

        mp_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mp_txt.setText(""+progress);
                mSound.setMicrophoneFlar(""+progress,getContext());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mp_txt = mpLayout.findViewById(R.id.mp_txt);
        mp_txt.setText(""+mSound.getMicrophoneFlar());
        builder.setView(mpLayout);        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
            }
        });        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void tcpDialog(View view) {        // create an alert builder
        AlertDialog.Builder builder =  new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.Theme_AppCompat_DayNight_Dialog_Alert));
        builder.setTitle("TCP congestion algorithm");
        String[] tcps = mNetwork.getTcpAvailableCongestions().toArray(new String[0]);
        builder.setItems(tcps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mNetwork.setTcpCongestion(tcps[which], getContext());
                refreshUI();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}