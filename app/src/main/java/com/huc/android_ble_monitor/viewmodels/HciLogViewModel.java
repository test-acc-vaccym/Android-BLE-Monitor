package com.huc.android_ble_monitor.viewmodels;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.huc.android_ble_monitor.R;
import com.huc.android_ble_monitor.activities.HciLogActivity;
import com.huc.android_ble_monitor.adapters.AttPacketListAdapter;
import com.huc.android_ble_monitor.adapters.HciPacketListAdapter;
import com.huc.android_ble_monitor.adapters.L2capPacketListAdapter;
import com.huc.android_ble_monitor.models.AttPacket;
import com.huc.android_ble_monitor.models.HciPacket;
import com.huc.android_ble_monitor.models.L2capPacket;
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

        //convert to L2CAP
        if(packet.packet_type.equals("ACL_DATA")){
            if(packet.packet_boundary_flag == HciPacket.boundary.COMPLETE_PACKET ||
                    packet.packet_boundary_flag == HciPacket.boundary.FIRST_PACKET_FLUSHABLE ||
                    packet.packet_boundary_flag == HciPacket.boundary.FIRST_PACKET_NON_FLUSHABLE){
                ArrayList L2capPackets = mL2capPackets.getValue();
                L2capPacket l2capPacket = new L2capPacket(packet, L2capPackets.size() + 1);
                L2capPackets.add(l2capPacket);
                mL2capPackets.postValue(L2capPackets);
                //convert to ATT
                ArrayList AttPackets = mAttPackets.getValue();
                AttPacket attPacket = new AttPacket(l2capPacket.packet_data, AttPackets.size() + 1);
                AttPackets.add(attPacket);
                mAttPackets.postValue(AttPackets);
            }else{
                //TODO: add continuing ACL packets to already existing L2CAP packets
            }
        }

    }

    public void changeProtocol(String protocol, final HciLogActivity ctx){
        //disable type spinner when L2CAP is selected
        if(protocol.equals("L2CAP")){
            ctx.mTypeSpinner.setEnabled(false);
            ctx.mTypeSpinner.setClickable(false);
        }else {
            ctx.mTypeSpinner.setEnabled(true);
        }

        switch (protocol){
            case "HCI":
                ctx.mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String type = parent.getItemAtPosition(position).toString();
                        ctx.mAdapter = new HciPacketListAdapter(ctx, getFilteredHciPackets(type));
                        ctx.mListView.setAdapter(ctx.mAdapter);
                        ctx.mAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
                ctx.mTypeSpinner.setAdapter(ArrayAdapter.createFromResource(ctx, R.array.HciTypeArray, android.R.layout.simple_spinner_item));
                ctx.mAdapter = new HciPacketListAdapter(ctx, mHciPackets.getValue());
                ctx.mListView.setAdapter(ctx.mAdapter);
                break;
            case "ATT":
                ctx.mTypeSpinner.setAdapter(ArrayAdapter.createFromResource(ctx, R.array.AttTypeArray, android.R.layout.simple_spinner_item));
                ctx.mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String type = parent.getItemAtPosition(position).toString();
                        ctx.mAdapter = new AttPacketListAdapter(ctx, getFilteredAttPackets(type));
                        ctx.mListView.setAdapter(ctx.mAdapter);
                        ctx.mAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
                ctx.mAdapter = new AttPacketListAdapter(ctx, mAttPackets.getValue());
                ctx.mListView.setAdapter(ctx.mAdapter);
                break;
            case "L2CAP":
                ctx.mAdapter = new L2capPacketListAdapter(ctx, mL2capPackets.getValue());
                ctx.mListView.setAdapter(ctx.mAdapter);
                break;
        }
    }

    public LiveData<ArrayList<HciPacket>> getSnoopPackets(){
        return mHciPackets;
    }

    public ArrayList<HciPacket> getFilteredHciPackets(String type){
        //return unfiltered HCI Packets
        if(type.equals("All")){
            return mHciPackets.getValue();
        }

        //filter packets by provided type
        ArrayList<HciPacket> filteredList = new ArrayList<>();
        for (HciPacket p: mHciPackets.getValue()) {
            if(p.packet_type.equals(type.toUpperCase())){
                filteredList.add(p);
            }
        }
        return filteredList;
    }

    public ArrayList<AttPacket> getFilteredAttPackets(String type){
        //return unfiltered ATT Packets
        if(type.equals("All")){
            return mAttPackets.getValue();
        }

        //filter packets by provided type
        ArrayList<AttPacket> filteredList = new ArrayList<>();
        for (AttPacket p: mAttPackets.getValue()) {
            if(p.packet_type.equals(type)){
                filteredList.add(p);
            }
        }
        return filteredList;
    }
}
