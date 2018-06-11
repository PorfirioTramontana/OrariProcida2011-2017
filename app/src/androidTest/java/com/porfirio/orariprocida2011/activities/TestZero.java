package com.porfirio.orariprocida2011.activities;


import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.porfirio.orariprocida2011.tasks.DownloadMezziTask;
import com.porfirio.orariprocida2011.tasks.LeggiMeteoTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import utilityTest.GeneralEvent;
import utilityTest.SpecificUIEvent;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class TestZero {

    @BeforeClass
    public static void startup() {
        GeneralEvent.setTime(GeneralEvent.NORMAL);
    }

    @Test
    public void ZeroTest() throws InterruptedException {
        GeneralEvent.declareandSetSemaphore(DownloadMezziTask.sem);
        GeneralEvent.declareandSetSemaphore(LeggiMeteoTask.sem);
        GeneralEvent.start(DownloadMezziTask.sem);
        GeneralEvent.finish(DownloadMezziTask.sem);
        GeneralEvent.start(LeggiMeteoTask.sem);
        GeneralEvent.finish(LeggiMeteoTask.sem);

        GeneralEvent.startApp("com.porfirio.orariprocida2011");
        GeneralEvent.pause();
        GeneralEvent.resume();
        GeneralEvent.doubleRotation();
        SpecificUIEvent.execute(SpecificUIEvent.AGGIORNA_ORARI_DA_WEB);
        SpecificUIEvent.execute(SpecificUIEvent.LEGGI_METEO);
    }

    @Test
    public void PrimoTest() throws InterruptedException {
        GeneralEvent.declareandSetSemaphore(DownloadMezziTask.sem);
        GeneralEvent.declareandSetSemaphore(LeggiMeteoTask.sem);
        GeneralEvent.start(DownloadMezziTask.sem);
        GeneralEvent.finish(DownloadMezziTask.sem);
        GeneralEvent.start(LeggiMeteoTask.sem);
        GeneralEvent.finish(LeggiMeteoTask.sem);

        GeneralEvent.startApp("com.porfirio.orariprocida2011");

        GeneralEvent.doubleRotation();

        GeneralEvent.pause();
        Log.d("TEST", "Between pause and resume");
        GeneralEvent.resume();
        SpecificUIEvent.execute(SpecificUIEvent.AGGIORNA_ORARI_DA_WEB);
        SpecificUIEvent.execute(SpecificUIEvent.LEGGI_METEO);
    }

    @After
    public void tearDown() {
        Log.d("TEST", "End test");
    }


}
