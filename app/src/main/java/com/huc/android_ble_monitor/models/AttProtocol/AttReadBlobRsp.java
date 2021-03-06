package com.huc.android_ble_monitor.models.AttProtocol;

import com.huc.android_ble_monitor.models.L2capPacket;

public class AttReadBlobRsp extends BaseAttPacket {
    public Byte[] mValue;

    public AttReadBlobRsp(L2capPacket p) {
        super(p);
        mValue = decodeValue(packet_data, 1, this.packet_length - 1);
    }

    @Override
    public String toString() {
        String res = super.toString() + "\n";
        res += "Value: ";
        for(byte b : this.mValue){
            res += String.format("%02x ", b);
        }
        return res;
    }
}
