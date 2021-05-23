package com.rozikmaliki.smartpdam;

import android.provider.ContactsContract;

public class DataAir {
    String bulan, air, biaya;

    public DataAir(){}

    public DataAir(String bulan, String air, String biaya) {
        this.bulan = bulan;
        this.air = air;
        this.biaya = biaya;
    }

    public String getBulan() {
        return bulan;
    }

    public String getAir() {
        return air;
    }

    public String getBiaya() {
        return biaya;
    }
}
