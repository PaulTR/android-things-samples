package com.example.upm.androidthings.driversamples;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.util.Log;

import mraa.mraa;
public class GroveRelay extends Activity {

    private static final String TAG = "GroveRelayActivity";

    static {
        try {
            System.loadLibrary("javaupm_grove");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native code library failed to load." +  e);
            System.exit(1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grove_relay);

        BoardDefaults bd = new BoardDefaults(this.getApplicationContext());
        int gpioIndex = -1;

        switch (bd.getBoardVariant()) {
            case BoardDefaults.DEVICE_EDISON_ARDUINO:
                gpioIndex = mraa.getGpioLookup(getString(R.string.RELAY_Edison_Arduino));
                break;
            case BoardDefaults.DEVICE_EDISON_SPARKFUN:
                gpioIndex = mraa.getGpioLookup(getString(R.string.RELAY_Edison_Sparkfun));
                break;
            case BoardDefaults.DEVICE_JOULE_TUCHUCK:
                gpioIndex = mraa.getGpioLookup(getString(R.string.RELAY_Joule_Tuchuck));
                break;
            default:
                throw new IllegalStateException("Unknown Board Variant: " + bd.getBoardVariant());
        }


        try {
            upm_grove.GroveRelay relay = new upm_grove.GroveRelay(gpioIndex);

            // This should be in a worker thread.
            for (int i = 0; i < 6; i++) {
                relay.on();
                if (relay.isOn())
                    Log.i(TAG, "Relay is on");
                Thread.sleep(1000);

                relay.off();
                if (relay.isOff())
                    Log.i(TAG, "Relay is off");
                Thread.sleep(1000);
            }

        // Shouldn't catch a universal exception and should exit in any case.
        } catch (Exception e) {
            Log.e(TAG, "Error in UPM APIs", e);
        }
    }
}