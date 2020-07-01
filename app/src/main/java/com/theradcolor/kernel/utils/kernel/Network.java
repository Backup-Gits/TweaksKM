package com.theradcolor.kernel.utils.kernel;

import android.content.Context;

import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Network {

    private static final String TCP_AVAILABLE_CONGESTIONS = "/proc/sys/net/ipv4/tcp_available_congestion_control";

    public String getTcpCongestion() {
        return getTcpAvailableCongestions().get(0);
    }

    public void setTcpCongestion(String tcpCongestion, Context context) {
        run("sysctl -w net.ipv4.tcp_congestion_control=" + tcpCongestion, TCP_AVAILABLE_CONGESTIONS, context);
    }

    public List<String> getTcpAvailableCongestions() {
        return new ArrayList<>(Arrays.asList(Utils.readFile(TCP_AVAILABLE_CONGESTIONS).split(" ")));
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, "NETWORK", id, context);
    }


}
