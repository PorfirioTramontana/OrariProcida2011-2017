package it.unina.ptramont;

import android.util.Log;

import java.util.concurrent.Semaphore;

public class TaskTestUtility {
    final static int START = 0;
    final static int FINISH = 1;

    public static void finishTask(Semaphore sem[]) {
        if (sem[FINISH] != null) {
            //Wait prima della terminazione del task
            Log.d("TEST", "TASK: Il task download è pronto a terminare");
            try {
                sem[FINISH].acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sem[FINISH].release();
        }
    }

    public static void startTask(Semaphore sem[]) {
        if (sem[START] != null) {
            //Wait prima della terminazione del task
            Log.d("TEST", "TASK: Il task download è pronto a terminare");
            try {
                sem[START].acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sem[START].release();
        }
    }

}
