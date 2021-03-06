package com.huc.android_ble_monitor.models.AttProtocol;

import com.huc.android_ble_monitor.models.L2capPacket;
import com.huc.android_ble_monitor.util.BinaryUtil;
import java.util.ArrayList;

public class AttReadMultipleReq extends BaseAttPacket {
    public ArrayList<Short> mHandles;

    public AttReadMultipleReq(L2capPacket p) {
        super(p);
        mHandles = decodeHandles(packet_data);
    }

    private ArrayList<Short> decodeHandles(Byte[] data){
        int i = 1;
        int len = packet_length - 1;
        ArrayList<Short> res = new ArrayList<>();

        while(len >= 2){
            res.add(decode16BitValue(data[i], data[i+1]));
            len -= 2;
            i += 2;
        }

        return res;
    }

    @Override
    public String toString() {
        String res = super.toString() + "\n";
        res += "Handles to read: \n";
        for(short s : mHandles){
            res += BinaryUtil.shortToHexString(s) + "\n";
        }
        return res;
    }
}
