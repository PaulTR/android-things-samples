package com.example.upm.androidthings.driversamples;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.upm.androidthings.driversupport.BoardDefaults;

import mraa.mraa;

import static upm_ssd1351.javaupm_ssd1351Constants.SSD1351HEIGHT;
import static upm_ssd1351.javaupm_ssd1351Constants.SSD1351WIDTH;

public class SpiDisplayActivity extends Activity {
    private static final String TAG = "SpiDisplayActivity";

    public static final int BLACK = 0x0000;
    public static final int WHITE = 0xFFFF;
    public static final int INTEL_BLUE = 0x0BF8;


    upm_ssd1351.SSD1351 display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spi_display);

        BoardDefaults bd = new BoardDefaults(this.getApplicationContext());
        int gpioOcIndex = -1;
        int gpioDcIndex = -1;
        int gpioResetIndex = -1;

        switch (bd.getBoardVariant()) {
            case BoardDefaults.DEVICE_EDISON_ARDUINO:
                gpioOcIndex = mraa.getGpioLookup(getString(R.string.Display_OC_Edison_Arduino));
                gpioDcIndex = mraa.getGpioLookup(getString(R.string.Display_DC_Edison_Arduino));
                gpioResetIndex = mraa.getGpioLookup(getString(R.string.Display_RESET_Edison_Arduino));
                break;
            case BoardDefaults.DEVICE_EDISON_SPARKFUN:
                gpioOcIndex = mraa.getGpioLookup(getString(R.string.Display_OC_Edison_Sparkfun));
                gpioDcIndex = mraa.getGpioLookup(getString(R.string.Display_DC_Edison_Sparkfun));
                gpioResetIndex = mraa.getGpioLookup(getString(R.string.Display_RESET_Edison_Sparkfun));
                break;
            case BoardDefaults.DEVICE_JOULE_TUCHUCK:
                gpioOcIndex = mraa.getGpioLookup(getString(R.string.Display_OC_Joule_Tuchuck));
                gpioDcIndex = mraa.getGpioLookup(getString(R.string.Display_DC_Joule_Tuchuck));
                gpioResetIndex = mraa.getGpioLookup(getString(R.string.Display_RESET_Joule_Tuchuck));
                break;
            default:
                throw new IllegalStateException("Unknown Board Variant: " + bd.getBoardVariant());
        }

        display = new upm_ssd1351.SSD1351(gpioOcIndex, gpioDcIndex, gpioResetIndex);
        
        AsyncTask.execute(displayTask);

    }

    Runnable displayTask = new Runnable() {

        @Override
        public void run() {
            // Moves the current thread into the background
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            int colors[] = {0x0000, 0x000F, 0x03E0, 0x03EF,
                    0x7800, 0x780F, 0x7BE0, 0xC618,
                    0x7BEF, 0x001F, 0x07E0, 0x07FF,
                    0xF800, 0xF81F, 0xFFE0, 0xFFFF};
            try {

                // Test lines pixel by pixel
                for(int i = 0; i < SSD1351HEIGHT; i++) {
                    for(int j = 0; j < SSD1351WIDTH; j++) {
                        display.drawPixel((short) i, (short) j, colors[i/8]);
                    }
                }
                display.refresh();
                Thread.sleep(5000);

                // Test rectangles
                for(int i = 0; i < SSD1351HEIGHT/32; i++) {
                    for (int j = 0; j < SSD1351WIDTH/32; j++) {
                        display.fillRect((short) (i * 32), (short) (j * 32),(short) 32, (short)32, colors[i * 4 + j]);
                    }
                }
                display.refresh();
                Thread.sleep(5000);

                // Test circles
                display.fillScreen(0x2104);
                for(int i = 0; i < SSD1351HEIGHT/32; i++) {
                    for (int j = 0; j < SSD1351WIDTH/32; j++) {
                        display.drawCircle((short)(i * 32 + 15), (short) (j * 32 + 15), (short)15, colors[i * 4 + j]);
                    }
                }
                display.refresh();
                Thread.sleep(5000);

                // Test Text
                display.fillScreen(INTEL_BLUE);
                display.setTextColor(WHITE, INTEL_BLUE);
                display.setTextSize((short) 4);
                display.setCursor((short)7, (short)30);
                display.print("Intel");
                display.setCursor((short)5, (short)70);
                display.print("IoTDK");
                display.refresh();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                display.delete();
                SpiDisplayActivity.this.finish();

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Thread.currentThread().interrupt();
        display.delete();
    }
}
