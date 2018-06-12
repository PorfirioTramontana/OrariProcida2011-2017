package it.unina.ptramont.utilityTest;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.Until;
import android.util.Log;

import java.util.concurrent.Semaphore;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class GeneralEvent {
    public static final int START = 0;
    public static final int FINISH = 1;

    public static final int NORMAL = 1000;
    public static final int SLOW = 2000;
    public static final int FAST = 100;
    private static final int LAUNCH_TIMEOUT = 5000;
    private static final String STRING_TO_BE_TYPED = "UiAutomator";
    public static int TIME = FAST;
    protected static UiDevice mDevice;


    public GeneralEvent() {
        return;
    }

    ;

    public static void setTime(int t) {
        TIME = t;
        return;
    }

    public static void declareSemaphore(Semaphore s[]) {
        s[START] = new Semaphore(1);
        s[FINISH] = new Semaphore(1);
        return;
    }

    public static void declareandSetSemaphore(Semaphore s[]) {
        s[START] = new Semaphore(1);
        s[FINISH] = new Semaphore(1);
        try {
            s[START].acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            s[FINISH].acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return;
    }

    public static void acquireSemaphore(Semaphore s) {
        try {
            s.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return;
    }

    public static void startApp(String s) {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Start from the home screen
        mDevice.pressHome();

        // Wait for launcher
        final String launcherPackage = mDevice.getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)),
                LAUNCH_TIMEOUT);

        // Launch the app
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(s);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        Log.d("TEST", "TEST: Avvio la activity");
        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(s).depth(0)),
                LAUNCH_TIMEOUT * 100);
        try {
            Thread.sleep(TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void releaseSemaphore(Semaphore semaphore) {
        try {
            Thread.sleep(TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        semaphore.release();
    }

    public static void pause() {
        try {
            Thread.sleep(TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mDevice.pressHome();
    }

    public static void pause19() {
        try {
            Thread.sleep(TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            mDevice.pressRecentApps();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d("TEST", "pause");
    }

    public static void resume19() {
        try {
            Thread.sleep(TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            mDevice.pressRecentApps();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d("TEST", "resume");
    }

    public static void resume() {
        try {
            Thread.sleep(TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            mDevice.pressRecentApps();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            mDevice.pressRecentApps();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void start(Semaphore[] sem) {
        releaseSemaphore(sem[START]);
    }

    public static void finish(Semaphore[] sem) {
        releaseSemaphore(sem[FINISH]);
    }

    public static void rotateLeft() {
        try {
            Thread.sleep(TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            mDevice.setOrientationLeft();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d("TEST", "RotateToLeft");
    }

    public static void rotateRight() {
        try {
            Thread.sleep(TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            mDevice.setOrientationRight();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d("TEST", "RotateToRight");
    }

    public static void rotatetoNatural() {
        try {
            Thread.sleep(TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            mDevice.setOrientationNatural();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d("TEST", "RotateToNatural");
    }

    public static void doubleRotation() {
        rotateLeft();
        rotatetoNatural();
    }
}
