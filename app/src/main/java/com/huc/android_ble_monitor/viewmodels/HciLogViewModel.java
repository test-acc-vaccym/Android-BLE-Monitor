package com.huc.android_ble_monitor.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.huc.android_ble_monitor.models.AttPacket;
import com.huc.android_ble_monitor.models.HciPacket;
import com.huc.android_ble_monitor.models.L2capPacket;
import com.huc.android_ble_monitor.util.HciSnoopLog;
import java.util.ArrayList;

public class HciLogViewModel extends ViewModel {
    private MutableLiveData<ArrayList<HciPacket>> mHciPackets = new MutableLiveData<>();
    private MutableLiveData<ArrayList<L2capPacket>> mL2capPackets = new MutableLiveData<>();
    private MutableLiveData<ArrayList<AttPacket>> mAttPackets = new MutableLiveData<>();

    public void init(){
        mHciPackets.setValue(new ArrayList<HciPacket>());
        mL2capPackets.setValue(new ArrayList<L2capPacket>());
        mAttPackets.setValue(new ArrayList<AttPacket>());
    }

    public void addSnoopPacket(HciPacket packet){
        ArrayList hciPackets = mHciPackets.getValue();
        packet.packet_number = hciPackets.size() + 1;
        hciPackets.add(packet);
        mHciPackets.postValue(hciPackets);

        //re-decode l2cap packets
        ArrayList l2capPackets = mL2capPackets.getValue();
        l2capPackets.clear();
        l2capPackets.addAll(HciSnoopLog.convertHciToL2cap(hciPackets));
        mL2capPackets.postValue(l2capPackets);
    }

    public LiveData<ArrayList<HciPacket>> getSnoopPackets(){
        return mHciPackets;
    }

    public ArrayList<HciPacket> getFilteredHciPackets(String type){
        ArrayList<HciPacket> filteredList = new ArrayList<>();

        //return unfiltered HCI Packets
        if(type.equals("All")){
            return mHciPackets.getValue();
        }

        //filter packets by provided type
        for (HciPacket p: mHciPackets.getValue()) {
            if(p.packet_type.equals(type.toUpperCase())){
                filteredList.add(p);
            }
        }
        return filteredList;
    }
}