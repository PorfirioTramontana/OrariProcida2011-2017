package it.unina.ptramont.utilityTest;

import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

public class SpecificUIEvent extends GeneralEvent {

    public static final int AGGIORNA_ORARI_DA_WEB = 0;
    public static final int LEGGI_METEO = 1;

    public static void execute(int ev) {
        switch (ev) {
            case AGGIORNA_ORARI_DA_WEB:
                try {
                    Thread.sleep(TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mDevice.pressMenu();
                UiObject ui = mDevice.findObject(new UiSelector().text("Aggiorna orari da Web"));
                try {
                    ui.click();
                } catch (UiObjectNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case LEGGI_METEO:
                //AVVIO UN METEO DA UI
                try {
                    Thread.sleep(TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mDevice.pressMenu();
                UiObject ui2 = mDevice.findObject(new UiSelector().text("Aggiorna dati meteo"));
                try {
                    ui2.click();
                } catch (UiObjectNotFoundException e) {
                    e.printStackTrace();
                }
                break;

        }
    }
}
