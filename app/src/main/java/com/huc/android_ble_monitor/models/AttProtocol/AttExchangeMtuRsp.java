package com.huc.android_ble_monitor.models.AttProtocol;

public class AttExchangeMtuRsp extends BaseAttPacket{
    public short mReceivedAttMTU;

    public AttExchangeMtuRsp(Byte[] data) {
        super(data);
        mReceivedAttMTU = decodeMtu(data);

        // Set ATT MTU to received Value
        BaseAttPacket.MTU_SIZE = mReceivedAttMTU;
    }

    private short decodeMtu(Byte[] data){
        byte LSB = data[1];
        byte MSB = data[2];
        return (short)((MSB << 8) + LSB);
    }

    @Override
    public String toString(){
        String res = super.toString() + "\n";
        res += "MTU Response from Server: " + this.mReceivedAttMTU + " Byte\n";
        return res;
    }
}
