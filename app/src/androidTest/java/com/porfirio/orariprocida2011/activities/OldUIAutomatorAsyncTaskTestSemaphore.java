package com.porfirio.orariprocida2011.activities;


import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class OldUIAutomatorAsyncTaskTestSemaphore {

    private static final String BASIC_SAMPLE_PACKAGE
            = "com.porfirio.orariprocida2011";
    private static final int LAUNCH_TIMEOUT = 5000;
    private static final String STRING_TO_BE_TYPED = "UiAutomator";
    private UiDevice mDevice;


    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    @BeforeClass
    public static void setTestValues() {
    }

/*
    @Test
    public void PrimoTest() throws InterruptedException {
        // SEQUENZA UI->TASK1->TASK2
        // 0 - Avvio l'app (che carica la UI e tenta di iniziare i task meteo e download, che vengono subito bloccati)
        // 1- Avvio il task meteo dopo 2 sec
        // 2 - Termino il task meteo dopo altri 1 sec
        // 3- Avvio il task download dopo 2 altro sec
        // 4 - Termino il task download dopo altri 1 sec
        //ESITO NEGATIVO: CONCURRENCY BUG
        // Il problema è causato dalla UI che non immagina che i task siano pending al momento di visualizzare
        // un toast che ne evidenzia lo stato
        // java.lang.NullPointerException: Attempt to invoke virtual method 'int java.util.Calendar.get(int)' on a null object reference
        // BUG: causa del bug è l'errata migrazione da sincrono verso asincrono, nella quale il toast è eseguito
        // dalla UI senza sincronizzarsi sulla terminazione dei task
        // COMMENTI: Il comportamento di questo test è analogo a PrimoTest_2

        // Definisco i semafori, uno per ogni task, eventualmente settando il numero di task possibili

        DownloadMezziTask.taskDownload = new Semaphore(1);
        DownloadMezziTask.taskDownloadStart = new Semaphore(1);
        LeggiMeteoTask.taskMeteo = new Semaphore(1);
        LeggiMeteoTask.taskMeteoStart = new Semaphore(1);


        //Il test mette rosso i semafori, in modo da poterne determinare autonomamente lo sblocco
        Log.d("TEST", "Il test prova ad acquisire i semafori");
        DownloadMezziTask.taskDownload.acquire();
        LeggiMeteoTask.taskMeteo.acquire();
        DownloadMezziTask.taskDownloadStart.acquire();
        LeggiMeteoTask.taskMeteoStart.acquire();

        Log.d("TEST", "Inizia il test, valori dei semafori: Download=" + DownloadMezziTask.taskDownload.availablePermits() + " meteo=" + LeggiMeteoTask.taskMeteo.availablePermits());

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
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        Log.d("TEST", "TEST: Avvio la activity");
        //Log.d("TEST","TEST: valori dei semafori: Download="+DownloadMezziTask.taskDownload.availablePermits()+" meteo="+LeggiMeteoTask.taskMeteo.availablePermits());
        //Thread.sleep(5000);
        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT * 100);
        Log.d("TEST", "TEST: Fine before");
        //La app è stata avviata


        Thread.sleep(2000);
        Log.d("TEST", "TEST: Il test sblocca l'avvio del task meteo");
        LeggiMeteoTask.taskMeteoStart.release();
        Log.d("TEST", "TEST: Rilasciato il semaforo meteo");

        Thread.sleep(1000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task meteo");
        LeggiMeteoTask.taskMeteo.release();
        //Log.d("TEST", "TEST: Rilasciato il semaforo meteo");

        Thread.sleep(2000);
        Log.d("TEST", "TEST: Il test sblocca l'avvio del task download");
        DownloadMezziTask.taskDownloadStart.release();
        //Log.d("TEST", "TEST: Rilasciato il semaforo download");

        Thread.sleep(1000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download");
        DownloadMezziTask.taskDownload.release();

    }

    @Test
    public void PrimoTest_2() throws InterruptedException {
        // SEQUENZA UI -> TASK METEO
        // 0 - Avvio l'app (che carica la UI e tenta di iniziare i task meteo e download, ma il solo task meteo viene bloccato)
        // 1- Avvio il task meteo dopo 2 sec
        // 2 - Termino il task meteo dopo altri 1 sec
        //ESITO NEGATIVO: CONCURRENCY BUG
        // Il problema è causato dalla UI che non immagina che il task meteo possa essere pending al momento di visualizzare
        // un toast che ne evidenzia lo stato
        // java.lang.NullPointerException: Attempt to invoke virtual method 'int java.util.Calendar.get(int)' on a null object reference
        // BUG: causa del bug è l'errata migrazione da sincrono verso asincrono, nella quale il toast è eseguito
        // dalla UI senza sincronizzarsi sulla terminazione dei task


        // Definisco i semafori, uno per ogni task, eventualmente settando il numero di task possibili
        DownloadMezziTask.taskDownload = new Semaphore(1);
        DownloadMezziTask.taskDownloadStart = new Semaphore(1);
        LeggiMeteoTask.taskMeteo = new Semaphore(1);
        LeggiMeteoTask.taskMeteoStart = new Semaphore(1);


        //Il test mette rosso i semafori, in modo da poterne determinare autonomamente lo sblocco
        Log.d("TEST", "Il test prova ad acquisire i semafori");
        //DownloadMezziTask.taskDownload.acquire();
        LeggiMeteoTask.taskMeteo.acquire();
        //DownloadMezziTask.taskDownloadStart.acquire();
        LeggiMeteoTask.taskMeteoStart.acquire();

        Log.d("TEST", "Inizia il test, valori dei semafori: Download=" + DownloadMezziTask.taskDownload.availablePermits() + " meteo=" + LeggiMeteoTask.taskMeteo.availablePermits());

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
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        Log.d("TEST", "TEST: Avvio la activity");
        //Log.d("TEST","TEST: valori dei semafori: Download="+DownloadMezziTask.taskDownload.availablePermits()+" meteo="+LeggiMeteoTask.taskMeteo.availablePermits());
        //Thread.sleep(5000);
        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT * 100);
        Log.d("TEST", "TEST: Fine before");
        //La app è stata avviata


        Thread.sleep(2000);
        Log.d("TEST", "TEST: Il test sblocca l'avvio del task meteo");
        LeggiMeteoTask.taskMeteoStart.release();
        Log.d("TEST", "TEST: Rilasciato il semaforo meteo");

        Thread.sleep(1000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task meteo");
        LeggiMeteoTask.taskMeteo.release();
        //Log.d("TEST", "TEST: Rilasciato il semaforo meteo");

    }

    @Test
    public void PrimoTest_3() throws InterruptedException {
        // SEQUENZA UI -> TASK DOWNLOAD
        // 0 - Avvio l'app (che carica la UI e tenta di iniziare i task meteo e download, ma il solo task download viene bloccati)
        // 1- Avvio il task download dopo 2 altro sec
        // 2 - Termino il task download dopo altri 1 sec
        //ESITO NEGATIVO: CONCURRENCY BUG
        // Il problema è causato dalla UI che non immagina che i task siano pending al momento di visualizzare
        // un toast che ne evidenzia lo stato
        // java.lang.NullPointerException: Attempt to invoke virtual method 'int java.util.Calendar.get(int)' on a null object reference
        // BUG: causa del bug è l'errata migrazione da sincrono verso asincrono, nella quale il toast è eseguito
        // dalla UI senza sincronizzarsi sulla terminazione dei task
        // COMMENTI: il test analogo riguardante il task download non causa crash semplicemente perchè gli orari
        // su disco o cablati nel codice rappresentano un valore di default sempre disponibile. E' possibile, però
        // che il toast visualizzai un orario che poi verrà aggiornato

        // Definisco i semafori, uno per ogni task, eventualmente settando il numero di task possibili
        DownloadMezziTask.taskDownload = new Semaphore(1);
        DownloadMezziTask.taskDownloadStart = new Semaphore(1);
        LeggiMeteoTask.taskMeteo = new Semaphore(1);
        LeggiMeteoTask.taskMeteoStart = new Semaphore(1);


        //Il test mette rosso i semafori, in modo da poterne determinare autonomamente lo sblocco
        Log.d("TEST", "Il test prova ad acquisire i semafori");
        DownloadMezziTask.taskDownload.acquire();
        //LeggiMeteoTask.taskMeteo.acquire();
        DownloadMezziTask.taskDownloadStart.acquire();
        //LeggiMeteoTask.taskMeteoStart.acquire();

        Log.d("TEST", "Inizia il test, valori dei semafori: Download=" + DownloadMezziTask.taskDownload.availablePermits() + " meteo=" + LeggiMeteoTask.taskMeteo.availablePermits());

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
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        Log.d("TEST", "TEST: Avvio la activity");
        //Log.d("TEST","TEST: valori dei semafori: Download="+DownloadMezziTask.taskDownload.availablePermits()+" meteo="+LeggiMeteoTask.taskMeteo.availablePermits());
        //Thread.sleep(5000);
        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT * 100);
        Log.d("TEST", "TEST: Fine before");
        //La app è stata avviata

        Thread.sleep(2000);
        Log.d("TEST", "TEST: Il test sblocca l'avvio del task download");
        DownloadMezziTask.taskDownloadStart.release();
        //Log.d("TEST", "TEST: Rilasciato il semaforo download");

        Thread.sleep(1000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download");
        DownloadMezziTask.taskDownload.release();

    }


    @Test
    public void SecondoTest() throws InterruptedException {
        // SEQUENZA (UI | METEO START) ->TERMINAZIONE METEO -> DOWNLOAD
        // 0 - Avvio l'app (che carica la UI e tenta di iniziare i task meteo e download, bloccando solo download e la terminazione di meteo)
        // 1- Avvio il task meteo subito
        // 2 - Termino il task meteo dopo altri 1 sec
        // 3- Avvio il task download dopo 2 altro sec
        // 4 - Termino il task download dopo altri 10 sec
        //ESITO POSITIVO
        //COMMENTI: Come nel test precedente, l'avvio ritardato di download non causa crash, ma causa
        //comunque un'anomalia (non riscontrabile) nel messaggio Toast, che fa riferimento al vecchio aggiornamento degli orari

        // Definisco i semafori, uno per ogni task, eventualmente settando il numero di task possibili
        DownloadMezziTask.taskDownload = new Semaphore(1);
        DownloadMezziTask.taskDownloadStart = new Semaphore(1);
        LeggiMeteoTask.taskMeteo = new Semaphore(1);
        LeggiMeteoTask.taskMeteoStart = new Semaphore(1);


        //Il test mette rosso i semafori, in modo da poterne determinare autonomamente lo sblocco
        Log.d("TEST", "Il test prova ad acquisire i semafori");
        DownloadMezziTask.taskDownload.acquire();
        LeggiMeteoTask.taskMeteo.acquire();
        DownloadMezziTask.taskDownloadStart.acquire();
        //LeggiMeteoTask.taskMeteoStart.acquire();

        Log.d("TEST", "Inizia il test, valori dei semafori: Download=" + DownloadMezziTask.taskDownload.availablePermits() + " meteo=" + LeggiMeteoTask.taskMeteo.availablePermits());


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
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        Log.d("TEST", "TEST: Avvio la activity");
        //Log.d("TEST","TEST: valori dei semafori: Download="+DownloadMezziTask.taskDownload.availablePermits()+" meteo="+LeggiMeteoTask.taskMeteo.availablePermits());
        //Thread.sleep(5000);
        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT * 100);
        Log.d("TEST", "TEST: Fine before");
        //La app è stata avviata



        Thread.sleep(1000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task meteo");
        LeggiMeteoTask.taskMeteo.release();
        //Log.d("TEST", "TEST: Rilasciato il semaforo meteo");

        Thread.sleep(2000);
        Log.d("TEST", "TEST: Il test sblocca l'avvio del task download");
        DownloadMezziTask.taskDownloadStart.release();
        //Log.d("TEST", "TEST: Rilasciato il semaforo download");

        Thread.sleep(10000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download");
        DownloadMezziTask.taskDownload.release();

    }

    @Test
    public void TerzoTest() throws InterruptedException {
        // SEQUENZA (UI | START METEO) -> TERMINA METEO -> AVVIO DONLOAD -> TERMINA DOWNLOAD -> UI -> DOWNLOAD
        // 0 - Avvio l'app (che carica la UI e tenta di iniziare i task meteo e download, bloccando solo download e la terminazione di meteo)
        // 1- Avvio il task meteo subito
        // 2 - Termino il task meteo dopo altri 1 sec
        // 3- Avvio il task download dopo 2 altro sec
        // 4 - Termino il task download dopo altri 1 sec
        // 5 - Clicco su aggiorna orari web
        // 6- Avvio il task download dopo 2 altro sec
        // 7 - Termino il task download dopo altri 10 sec
        //ESITO POSITIVO
        //COMMENTI: L'evento sulla UI avviene a task terminati, quindi in uno stato sostanzialmente coerente

        // Definisco i semafori, uno per ogni task, eventualmente settando il numero di task possibili
        DownloadMezziTask.taskDownload = new Semaphore(1);
        DownloadMezziTask.taskDownloadStart = new Semaphore(1);
        LeggiMeteoTask.taskMeteo = new Semaphore(1);
        LeggiMeteoTask.taskMeteoStart = new Semaphore(1);


        //Il test mette rosso i semafori, in modo da poterne determinare autonomamente lo sblocco
        Log.d("TEST", "Il test prova ad acquisire i semafori");
        DownloadMezziTask.taskDownload.acquire();
        LeggiMeteoTask.taskMeteo.acquire();
        DownloadMezziTask.taskDownloadStart.acquire();
        //LeggiMeteoTask.taskMeteoStart.acquire();

        Log.d("TEST", "Inizia il test, valori dei semafori: Download=" + DownloadMezziTask.taskDownload.availablePermits() + " meteo=" + LeggiMeteoTask.taskMeteo.availablePermits());


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
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        Log.d("TEST", "TEST: Avvio la activity");
        //Log.d("TEST","TEST: valori dei semafori: Download="+DownloadMezziTask.taskDownload.availablePermits()+" meteo="+LeggiMeteoTask.taskMeteo.availablePermits());
        //Thread.sleep(5000);
        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT * 100);
        Log.d("TEST", "TEST: Fine before");
        //La app è stata avviata


        Thread.sleep(1000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task meteo");
        LeggiMeteoTask.taskMeteo.release();
        //Log.d("TEST", "TEST: Rilasciato il semaforo meteo");

        Thread.sleep(2000);
        Log.d("TEST", "TEST: Il test sblocca l'avvio del task download");
        DownloadMezziTask.taskDownloadStart.release();
        //Log.d("TEST", "TEST: Rilasciato il semaforo download");

        Thread.sleep(1000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download");
        DownloadMezziTask.taskDownload.release();

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        ViewInteraction textView = onView(
                allOf(withId(android.R.id.title), withText("Aggiorna orari da Web"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.android.internal.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        textView.perform(click());

        Log.d("TEST", "Click su aggiorna orari da web");

        Thread.sleep(2000);
        Log.d("TEST", "TEST: Il test sblocca l'avvio del task download");
        DownloadMezziTask.taskDownloadStart.release();
        //Log.d("TEST", "TEST: Rilasciato il semaforo download");

        Thread.sleep(10000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download");
        DownloadMezziTask.taskDownload.release();


    }

    @Test
    public void QuartoTest() throws InterruptedException {
        // SEQUENZA (UI | METEO START) -> TERMINA METEO -> START DOWNLOAD -> UI -> START DOWNLOAD 2 -> FINE DOWNLOAD -> FINE DOWNLOAD 2
        // 0 - Avvio l'app (che carica la UI e tenta di iniziare i task meteo e download, bloccando solo download e la terminazione di meteo)
        // 1- Avvio il task meteo subito
        // 2 - Termino il task meteo dopo altri 1 sec
        // 3- Avvio il task download dopo 2 altro sec
        // 4 - Clicco su aggiorna orari web  QUI IMPAZZISCE ESPRESSO?
        // 5 - Avvio l'altro task download
        // 5 - Termino il task download dopo altri 10 sec
        // PROBLEMA: ESPRESSO previene l'esecuzione di un evento UI in presenza di task pendenti
        // Come possibile effetto collaterale viene rilevato un TIMEOUT nel task
        //ATTENZIONE: onView di Android Espresso NON può essere eseguita se ne non sono terminati i task
        // https://developer.android.com/training/testing/espresso/

        // Definisco i semafori, uno per ogni task, eventualmente settando il numero di task possibili
        DownloadMezziTask.taskDownload = new Semaphore(2);
        DownloadMezziTask.taskDownloadStart = new Semaphore(2);
        LeggiMeteoTask.taskMeteo = new Semaphore(1);
        LeggiMeteoTask.taskMeteoStart = new Semaphore(1);


        //Il test mette rosso i semafori, in modo da poterne determinare autonomamente lo sblocco
        Log.d("TEST", "Il test prova ad acquisire i semafori");
        DownloadMezziTask.taskDownload.acquire(2);
        LeggiMeteoTask.taskMeteo.acquire();
        DownloadMezziTask.taskDownloadStart.acquire(2);
        //LeggiMeteoTask.taskMeteoStart.acquire();

        Log.d("TEST", "Inizia il test, valori dei semafori: Download=" + DownloadMezziTask.taskDownload.availablePermits() + " meteo=" + LeggiMeteoTask.taskMeteo.availablePermits());


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
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        Log.d("TEST", "TEST: Avvio la activity");
        //Log.d("TEST","TEST: valori dei semafori: Download="+DownloadMezziTask.taskDownload.availablePermits()+" meteo="+LeggiMeteoTask.taskMeteo.availablePermits());
        //Thread.sleep(5000);
        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT * 100);
        Log.d("TEST", "TEST: Fine before");
        //La app è stata avviata


        Thread.sleep(1000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task meteo");
        LeggiMeteoTask.taskMeteo.release();
        //Log.d("TEST", "TEST: Rilasciato il semaforo meteo");

        Thread.sleep(2000);
        Log.d("TEST", "TEST: Il test sblocca l'avvio del task download");
        DownloadMezziTask.taskDownloadStart.release();
        //Log.d("TEST", "TEST: Rilasciato il semaforo download");
        Log.d("TEST", "TEST: valori dei semafori: StartDownload=" + DownloadMezziTask.taskDownloadStart.availablePermits() + " download=" + DownloadMezziTask.taskDownload.availablePermits());

        Thread.sleep(1000);
        Log.d("TEST", "Click su aggiorna orari da web");
        Log.d("TEST", "TEST: valori dei semafori: StartDownload=" + DownloadMezziTask.taskDownloadStart.availablePermits() + " download=" + DownloadMezziTask.taskDownload.availablePermits());

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        ViewInteraction textView = onView(
                allOf(withId(android.R.id.title), withText("Aggiorna orari da Web"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.android.internal.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        textView.perform(click());
        Log.d("TEST", "TEST: valori dei semafori: StartDownload=" + DownloadMezziTask.taskDownloadStart.availablePermits() + " download=" + DownloadMezziTask.taskDownload.availablePermits());


        Thread.sleep(2000);
        Log.d("TEST", "TEST: Il test sblocca l'avvio del task download");
        DownloadMezziTask.taskDownloadStart.release();
        //Log.d("TEST", "TEST: Rilasciato il semaforo download");

        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download");
        DownloadMezziTask.taskDownload.release();

        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download");
        DownloadMezziTask.taskDownload.release();


    }

    @Test
    public void QuartoTest_2() throws InterruptedException, UiObjectNotFoundException, RemoteException {
        // SEQUENZA (UI | METEO START) -> TERMINA METEO -> START DOWNLOAD -> UI -> START DOWNLOAD 2 -> FINE DOWNLOAD -> FINE DOWNLOAD 2
        // 0 - Avvio l'app (che carica la UI e tenta di iniziare i task meteo e download, bloccando solo download e la terminazione di meteo)
        // 1- Avvio il task meteo subito
        // 2 - Termino il task meteo dopo altri 1 sec
        // 3- Avvio il task download dopo 2 altro sec
        // 4 - Clicco su aggiorna orari web  ORA UTILIZZO UIAUTOMATOR, CHE FUNZIONA ANCHE IN QUESTE CONDIZIONI
        // 5 - Avvio l'altro task download
        // 6 - Termino il task download dopo altri 3 sec
        // 7 - Termino il task download dopo altri 3 sec
        // PROBLEMA: l'impressione è che l'app si chiuda senza attendere il completamento del task (per esaurimento del test?)

        // Definisco i semafori, uno per ogni task, eventualmente settando il numero di task possibili
        DownloadMezziTask.taskDownload = new Semaphore(1);
        DownloadMezziTask.taskDownloadStart = new Semaphore(1);
        LeggiMeteoTask.taskMeteo = new Semaphore(1);
        LeggiMeteoTask.taskMeteoStart = new Semaphore(1);


        //Il test mette rosso i semafori, in modo da poterne determinare autonomamente lo sblocco
        Log.d("TEST", "Il test prova ad acquisire i semafori");
        DownloadMezziTask.taskDownload.acquire();
        LeggiMeteoTask.taskMeteo.acquire();
        DownloadMezziTask.taskDownloadStart.acquire();
        //LeggiMeteoTask.taskMeteoStart.acquire();

        Log.d("TEST", "Inizia il test, valori dei semafori: Download=" + DownloadMezziTask.taskDownload.availablePermits() + " meteo=" + LeggiMeteoTask.taskMeteo.availablePermits());


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
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        Log.d("TEST", "TEST: Avvio la activity");
        //Log.d("TEST","TEST: valori dei semafori: Download="+DownloadMezziTask.taskDownload.availablePermits()+" meteo="+LeggiMeteoTask.taskMeteo.availablePermits());
        //Thread.sleep(5000);
        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT * 1000);
        Log.d("TEST", "TEST: Fine before");
        //La app è stata avviata


        Thread.sleep(1000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task meteo");
        LeggiMeteoTask.taskMeteo.release();
        //Log.d("TEST", "TEST: Rilasciato il semaforo meteo");

        Thread.sleep(2000);
        Log.d("TEST", "TEST: Il test sblocca l'avvio del task download 1");
        DownloadMezziTask.taskDownloadStart.release();
        //Log.d("TEST", "TEST: Rilasciato il semaforo download");
        Log.d("TEST", "TEST: valori dei semafori: StartDownload=" + DownloadMezziTask.taskDownloadStart.availablePermits() + " download=" + DownloadMezziTask.taskDownload.availablePermits());

        Thread.sleep(1000);
        Log.d("TEST", "Click su aggiorna orari da web");
        Log.d("TEST", "TEST: valori dei semafori: StartDownload=" + DownloadMezziTask.taskDownloadStart.availablePermits() + " download=" + DownloadMezziTask.taskDownload.availablePermits());

        mDevice.pressMenu();

        UiObject ui = mDevice.findObject(new UiSelector().text("Aggiorna orari da Web"));
        ui.click();

        Log.d("TEST", "TEST: valori dei semafori: StartDownload=" + DownloadMezziTask.taskDownloadStart.availablePermits() + " download=" + DownloadMezziTask.taskDownload.availablePermits());


        Thread.sleep(2000);
        Log.d("TEST", "TEST: Il test sblocca l'avvio del task download 2");
        DownloadMezziTask.taskDownloadStart.release();
        //Log.d("TEST", "TEST: Rilasciato il semaforo download");

        Log.d("TEST", "TEST: valori dei semafori: StartDownload=" + DownloadMezziTask.taskDownloadStart.availablePermits() + " download=" + DownloadMezziTask.taskDownload.availablePermits());
        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download 1");
        DownloadMezziTask.taskDownload.release();

        Log.d("TEST", "TEST: valori dei semafori: StartDownload=" + DownloadMezziTask.taskDownloadStart.availablePermits() + " download=" + DownloadMezziTask.taskDownload.availablePermits());
        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download 2");
        DownloadMezziTask.taskDownload.release();

        //LeggiMeteoTask.taskMeteoStart.acquire();
        Log.d("TEST", "TEST: Fine del test");
    }

    @Test
    public void QuintoTest() throws InterruptedException, UiObjectNotFoundException, RemoteException {
        // SEQUENZA (UI | METEO START) -> TERMINA METEO -> START DOWNLOAD -> UI -> START DOWNLOAD 2 -> FINE DOWNLOAD -> FINE DOWNLOAD 2
        // 0 - Avvio l'app (che carica la UI e tenta di iniziare i task meteo e download, bloccando solo download e la terminazione di meteo)
        // 1- Avvio il task meteo subito
        // 2 - Termino il task meteo dopo altri 1 sec
        // 3- Avvio il task download dopo 2 altro sec
        // 4 - Clicco su aggiorna orari web  ORA UTILIZZO UIAUTOMATOR, CHE FUNZIONA ANCHE IN QUESTE CONDIZIONI
        // 5 - Avvio l'altro task download
        // 6 - Termino il task download dopo altri 10 sec
        // 7 - Termino il task download dopo altri 10 sec
        // PROBLEMA: l'impressione è che l'app si chiuda senza attendere il completamento del task (per esaurimento del test?)

        // Definisco i semafori, uno per ogni task, eventualmente settando il numero di task possibili
        DownloadMezziTask.taskDownload = new Semaphore(1);
        DownloadMezziTask.taskDownloadStart = new Semaphore(1);
        LeggiMeteoTask.taskMeteo = new Semaphore(1);
        LeggiMeteoTask.taskMeteoStart = new Semaphore(1);


        //Il test mette rosso i semafori, in modo da poterne determinare autonomamente lo sblocco
        Log.d("TEST", "Il test prova ad acquisire i semafori");
        DownloadMezziTask.taskDownload.acquire();
        LeggiMeteoTask.taskMeteo.acquire();
        DownloadMezziTask.taskDownloadStart.acquire();
        //LeggiMeteoTask.taskMeteoStart.acquire();

        Log.d("TEST", "Inizia il test, valori dei semafori: Download=" + DownloadMezziTask.taskDownload.availablePermits() + " meteo=" + LeggiMeteoTask.taskMeteo.availablePermits());


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
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        Log.d("TEST", "TEST: Avvio la activity");
        //Log.d("TEST","TEST: valori dei semafori: Download="+DownloadMezziTask.taskDownload.availablePermits()+" meteo="+LeggiMeteoTask.taskMeteo.availablePermits());
        //Thread.sleep(5000);
        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT * 1000);
        Log.d("TEST", "TEST: Fine before");
        //La app è stata avviata


        Thread.sleep(1000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task meteo");
        LeggiMeteoTask.taskMeteo.release();
        //Log.d("TEST", "TEST: Rilasciato il semaforo meteo");

        Thread.sleep(2000);
        Log.d("TEST", "TEST: Il test sblocca l'avvio del task download 1");
        DownloadMezziTask.taskDownloadStart.release();
        //Log.d("TEST", "TEST: Rilasciato il semaforo download");
        Log.d("TEST", "TEST: valori dei semafori: StartDownload=" + DownloadMezziTask.taskDownloadStart.availablePermits() + " download=" + DownloadMezziTask.taskDownload.availablePermits());

        Thread.sleep(1000);
        Log.d("TEST", "Click su aggiorna orari da web");
        Log.d("TEST", "TEST: valori dei semafori: StartDownload=" + DownloadMezziTask.taskDownloadStart.availablePermits() + " download=" + DownloadMezziTask.taskDownload.availablePermits());

        mDevice.pressHome();
        Thread.sleep(2000);
        mDevice.pressRecentApps();
        Thread.sleep(1000);
        mDevice.pressRecentApps();

        Thread.sleep(1000);
        mDevice.pressMenu();

        UiObject ui = mDevice.findObject(new UiSelector().text("Aggiorna orari da Web"));
        ui.click();

        Log.d("TEST", "TEST: valori dei semafori: StartDownload=" + DownloadMezziTask.taskDownloadStart.availablePermits() + " download=" + DownloadMezziTask.taskDownload.availablePermits());


        Thread.sleep(2000);
        Log.d("TEST", "TEST: Il test sblocca l'avvio del task download (1)");
        DownloadMezziTask.taskDownloadStart.release();
        //Log.d("TEST", "TEST: Rilasciato il semaforo download");

        Log.d("TEST", "TEST: valori dei semafori: StartDownload=" + DownloadMezziTask.taskDownloadStart.availablePermits() + " download=" + DownloadMezziTask.taskDownload.availablePermits());
        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download (2)");
        DownloadMezziTask.taskDownload.release();

        Log.d("TEST", "TEST: valori dei semafori: StartDownload=" + DownloadMezziTask.taskDownloadStart.availablePermits() + " download=" + DownloadMezziTask.taskDownload.availablePermits());
        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download (3)");
        DownloadMezziTask.taskDownload.release();

        //LeggiMeteoTask.taskMeteoStart.acquire();
        Log.d("TEST", "TEST: Fine del test");
    }

    @Test
    public void SestoTest() throws InterruptedException, UiObjectNotFoundException, RemoteException {
        // SEQUENZA (UI | START DOWNLOAD) -> UI -> START DOWNLOAD2 -> UI -> START DOWNLOAD 3 -> FINE DOWNLOAD 1 -> FINE DOWNLOAD 2 -> FINE DOWNLOAD 3
        // 0 - Avvio l'app (che carica la UI e tenta di iniziare i task meteo e download, bloccando solo download e la terminazione di meteo)
        // 3- Avvio il task download subito
        // 4 - Clicco su aggiorna orari web  ORA UTILIZZO UIAUTOMATOR, CHE FUNZIONA ANCHE IN QUESTE CONDIZIONI
        // 5 - Avvio l'altro task download
        // 4 - Clicco su aggiorna orari web  ORA UTILIZZO UIAUTOMATOR, CHE FUNZIONA ANCHE IN QUESTE CONDIZIONI
        // 5 - Avvio l'altro task download
        // 6 - Termino il task download dopo altri 10 sec
        // 7 - Termino il task download dopo altri 10 sec
        // 7 - Termino il task download dopo altri 10 sec
        // ESITO: nessun problema
        // COMMENTI: l'esecuzione di oggetti task diversi corrispondenti alla stessa classe task è stata parallelizzata
        // Non sono stati riscontrati problemi relativi a corse critiche anche se potenzialmente potevano esistere
        // PROBLEMA : Con il sistema del singolo semaforo non posso decidere l'ordine di sblocco dei task
        // Se l'applicazione fosse stata programmata in maniera peggiore utilizzando più volte lo stesso oggetto task
        // ci sarebbero stati problemi (ma sarebbero stati rivelati anche da semplici test funzionali?)


        // Definisco i semafori, uno per ogni task, eventualmente settando il numero di task possibili
        DownloadMezziTask.taskDownload = new Semaphore(1);
        DownloadMezziTask.taskDownloadStart = new Semaphore(1);
        LeggiMeteoTask.taskMeteo = new Semaphore(1);
        LeggiMeteoTask.taskMeteoStart = new Semaphore(1);


        //Il test mette rosso i semafori, in modo da poterne determinare autonomamente lo sblocco
        Log.d("TEST", "Il test prova ad acquisire i semafori");
        DownloadMezziTask.taskDownload.acquire();
        //LeggiMeteoTask.taskMeteo.acquire();
        //DownloadMezziTask.taskDownloadStart.acquire();
        //LeggiMeteoTask.taskMeteoStart.acquire();

        Log.d("TEST", "Inizia il test, valori dei semafori: Download=" + DownloadMezziTask.taskDownload.availablePermits() + " meteo=" + LeggiMeteoTask.taskMeteo.availablePermits());


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
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        Log.d("TEST", "TEST: Avvio la activity");
        //Log.d("TEST","TEST: valori dei semafori: Download="+DownloadMezziTask.taskDownload.availablePermits()+" meteo="+LeggiMeteoTask.taskMeteo.availablePermits());
        //Thread.sleep(5000);
        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT * 1000);
        Log.d("TEST", "TEST: Fine before");
        //La app è stata avviata

        Thread.sleep(1000);
        Log.d("TEST", "Click su aggiorna orari da web");
        Log.d("TEST", "TEST: valori dei semafori: StartDownload=" + DownloadMezziTask.taskDownloadStart.availablePermits() + " download=" + DownloadMezziTask.taskDownload.availablePermits());

        mDevice.pressMenu();
        mDevice.findObject(new UiSelector().text("Aggiorna orari da Web")).click();

        Log.d("TEST", "TEST: valori dei semafori: StartDownload=" + DownloadMezziTask.taskDownloadStart.availablePermits() + " download=" + DownloadMezziTask.taskDownload.availablePermits());
        Thread.sleep(2000);
        Log.d("TEST", "TEST: Il test sblocca l'avvio del task download 2");
        DownloadMezziTask.taskDownloadStart.release();
        //Log.d("TEST", "TEST: Rilasciato il semaforo download");

        Thread.sleep(1000);
        Log.d("TEST", "Click su aggiorna orari da web");
        Log.d("TEST", "TEST: valori dei semafori: StartDownload=" + DownloadMezziTask.taskDownloadStart.availablePermits() + " download=" + DownloadMezziTask.taskDownload.availablePermits());

        mDevice.pressMenu();
        mDevice.findObject(new UiSelector().text("Aggiorna orari da Web")).click();

        Log.d("TEST", "TEST: valori dei semafori: StartDownload=" + DownloadMezziTask.taskDownloadStart.availablePermits() + " download=" + DownloadMezziTask.taskDownload.availablePermits());
        Thread.sleep(2000);
        Log.d("TEST", "TEST: Il test sblocca l'avvio del task download 2");
        DownloadMezziTask.taskDownloadStart.release();
        //Log.d("TEST", "TEST: Rilasciato il semaforo download");

        Log.d("TEST", "TEST: valori dei semafori: StartDownload=" + DownloadMezziTask.taskDownloadStart.availablePermits() + " download=" + DownloadMezziTask.taskDownload.availablePermits());
        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download 1");
        DownloadMezziTask.taskDownload.release();

        Log.d("TEST", "TEST: valori dei semafori: StartDownload=" + DownloadMezziTask.taskDownloadStart.availablePermits() + " download=" + DownloadMezziTask.taskDownload.availablePermits());
        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download 2");
        DownloadMezziTask.taskDownload.release();

        Log.d("TEST", "TEST: valori dei semafori: StartDownload=" + DownloadMezziTask.taskDownloadStart.availablePermits() + " download=" + DownloadMezziTask.taskDownload.availablePermits());
        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download 2");
        DownloadMezziTask.taskDownload.release();

        Log.d("TEST", "TEST: Fine del test");
    }

    @Test
    public void SettimoTest() throws InterruptedException, UiObjectNotFoundException, RemoteException {
        // SEQUENZA (UI | START DOWNLOAD ) -> PAUSE -> RESUME -> FINE DOWNLOAD
        // 0 - Avvio l'app (che carica la UI e tenta di iniziare i task meteo e download, bloccando solo download e la terminazione di meteo)
        // pause
        // resume
        // 6 - Termino il task download dopo altri 10 sec
        // 7 - Termino il task download dopo altri 10 sec
        // PROBLEMA: l'impressione è che l'app si chiuda senza attendere il completamento del task (per esaurimento del test?)

        // Definisco i semafori, uno per ogni task, eventualmente settando il numero di task possibili
        DownloadMezziTask.taskDownload = new Semaphore(1);
        DownloadMezziTask.taskDownloadStart = new Semaphore(1);
        LeggiMeteoTask.taskMeteo = new Semaphore(1);
        LeggiMeteoTask.taskMeteoStart = new Semaphore(1);


        //Il test mette rosso i semafori, in modo da poterne determinare autonomamente lo sblocco
        Log.d("TEST", "Il test prova ad acquisire i semafori");
        DownloadMezziTask.taskDownload.acquire();
        //LeggiMeteoTask.taskMeteo.acquire();
        //DownloadMezziTask.taskDownloadStart.acquire();
        //LeggiMeteoTask.taskMeteoStart.acquire();

        Log.d("TEST", "Inizia il test, valori dei semafori: Download=" + DownloadMezziTask.taskDownload.availablePermits() + " meteo=" + LeggiMeteoTask.taskMeteo.availablePermits());


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
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        Log.d("TEST", "TEST: Avvio la activity");
        //Log.d("TEST","TEST: valori dei semafori: Download="+DownloadMezziTask.taskDownload.availablePermits()+" meteo="+LeggiMeteoTask.taskMeteo.availablePermits());
        //Thread.sleep(5000);
        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT * 1000);
        Log.d("TEST", "TEST: Fine before");
        //La app è stata avviata

        // PERCHE' IL TEST FINISCE QUI?
        Thread.sleep(10000);


        mDevice.pressHome();
        Log.d("TEST", "TEST: Pause app");
        Thread.sleep(2000);
        mDevice.pressRecentApps();
        Thread.sleep(1000);
        mDevice.pressRecentApps();
        Log.d("TEST", "TEST: Resume app");


        Log.d("TEST", "TEST: valori dei semafori: StartDownload=" + DownloadMezziTask.taskDownloadStart.availablePermits() + " download=" + DownloadMezziTask.taskDownload.availablePermits());
        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download ");
        DownloadMezziTask.taskDownload.release();


        //LeggiMeteoTask.taskMeteoStart.acquire();
        Log.d("TEST", "TEST: Fine del test");
    }

    @Test
    public void OttavoTest() throws InterruptedException, UiObjectNotFoundException, RemoteException {
        // SEQUENZA (UI | START DOWNLOAD ) -> PAUSE -> RESUME -> FINE DOWNLOAD
        // 0 - Avvio l'app (che carica la UI e tenta di iniziare i task meteo e download, bloccando la terminazione di entrambi)
        // pause
        // resume
        // 6 - Termino il task download dopo altri 10 sec
        // 7 - Termino il task meteo dopo altri 10 sec
        // PROBLEMA: I task lasciati appesi terminano senza poter comunicare i risultati, gisutamente

        // Definisco i semafori, uno per ogni task, eventualmente settando il numero di task possibili
        DownloadMezziTask.taskDownload = new Semaphore(1);
        DownloadMezziTask.taskDownloadStart = new Semaphore(1);
        LeggiMeteoTask.taskMeteo = new Semaphore(1);
        LeggiMeteoTask.taskMeteoStart = new Semaphore(1);


        //Il test mette rosso i semafori, in modo da poterne determinare autonomamente lo sblocco
        Log.d("TEST", "Il test prova ad acquisire i semafori");
        DownloadMezziTask.taskDownload.acquire();
        LeggiMeteoTask.taskMeteo.acquire();
        //DownloadMezziTask.taskDownloadStart.acquire();
        //LeggiMeteoTask.taskMeteoStart.acquire();

        Log.d("TEST", "Inizia il test, valori dei semafori: Download=" + DownloadMezziTask.taskDownload.availablePermits() + " meteo=" + LeggiMeteoTask.taskMeteo.availablePermits());


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
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        Log.d("TEST", "TEST: Avvio la activity");
        //Log.d("TEST","TEST: valori dei semafori: Download="+DownloadMezziTask.taskDownload.availablePermits()+" meteo="+LeggiMeteoTask.taskMeteo.availablePermits());
        //Thread.sleep(5000);
        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT * 1000);
        Log.d("TEST", "TEST: Fine before");
        //La app è stata avviata

        // PERCHE' IL TEST FINISCE QUI?
        Thread.sleep(10000);


        mDevice.pressHome();
        Log.d("TEST", "TEST: Pause app");
        Thread.sleep(2000);
        mDevice.pressRecentApps();
        Thread.sleep(1000);
        mDevice.pressRecentApps();
        Log.d("TEST", "TEST: Resume app");


        Log.d("TEST", "TEST: valori dei semafori: StartDownload=" + DownloadMezziTask.taskDownloadStart.availablePermits() + " download=" + DownloadMezziTask.taskDownload.availablePermits());
        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download ");
        DownloadMezziTask.taskDownload.release();
        Log.d("TEST", "TEST: Task Download Terminato");

        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task meteo ");
        LeggiMeteoTask.taskMeteo.release();
        Log.d("TEST", "TEST: Task meteo Terminato");

        Log.d("TEST", "TEST: Fine del test");
    }

    @Test
    public void NonoTest() throws InterruptedException, UiObjectNotFoundException, RemoteException {
        // SEQUENZA (UI) -> START DOWNLOAD 1 --> START DOWNLOAD 2 --> END DOWNLOAD 2 --> END DOWNLOAD 1
        // 0 - Avvio l'app (che carica la UI e tenta di iniziare i task meteo bloccando il task download)
        // 1 - avvio un altro task download
        // 2 - termino l'ultimo task download
        // 6 - Termino il primo task download
        // PROBLEMA: I task lasciati appesi terminano senza poter comunicare i risultati, gisutamente

        // Definisco i semafori, uno per ogni task, eventualmente settando il numero di task possibili
        DownloadMezziTask.taskDownload = new Semaphore(1);
        DownloadMezziTask.taskDownloadStart = new Semaphore(1);
        LeggiMeteoTask.taskMeteo = new Semaphore(1);
        LeggiMeteoTask.taskMeteoStart = new Semaphore(1);


        //Il test mette rosso i semafori, in modo da poterne determinare autonomamente lo sblocco
        Log.d("TEST", "Il test prova ad acquisire i semafori");
        DownloadMezziTask.taskDownload.acquire();
        //LeggiMeteoTask.taskMeteo.acquire();
        DownloadMezziTask.taskDownloadStart.acquire();
        //LeggiMeteoTask.taskMeteoStart.acquire();

        Log.d("TEST", "Inizia il test, valori dei semafori: Download=" + DownloadMezziTask.taskDownload.availablePermits() + " meteo=" + LeggiMeteoTask.taskMeteo.availablePermits());


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
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        Log.d("TEST", "TEST: Avvio la activity");
        //Log.d("TEST","TEST: valori dei semafori: Download="+DownloadMezziTask.taskDownload.availablePermits()+" meteo="+LeggiMeteoTask.taskMeteo.availablePermits());
        //Thread.sleep(5000);
        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT * 1000);
        Log.d("TEST", "TEST: Fine before");
        //La app è stata avviata

        // PERCHE' IL TEST FINISCE QUI?
        Thread.sleep(2000);
        Log.d("TEST", "TEST: Il test sblocca l'avvio del task download 1");
        DownloadMezziTask.taskDownloadStart.release();
        //Log.d("TEST", "TEST: Rilasciato il semaforo download");

        Thread.sleep(1000);
        Log.d("TEST", "Click su aggiorna orari da web");

        mDevice.pressMenu();
        UiObject ui = mDevice.findObject(new UiSelector().text("Aggiorna orari da Web"));
        ui.click();

        Thread.sleep(2000);
        Log.d("TEST", "TEST: Il test sblocca l'avvio del task download 2");
        DownloadMezziTask.taskDownloadStart.release();
        //Log.d("TEST", "TEST: Rilasciato il semaforo download");

        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download ");
        DownloadMezziTask.taskDownload.release();
        Log.d("TEST", "TEST: Task Download Terminato");

        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download ");
        DownloadMezziTask.taskDownload.release();
        Log.d("TEST", "TEST: Task Download Terminato");

    }

    @Test
    public void DecimoTest() throws InterruptedException, UiObjectNotFoundException, RemoteException {
        // SEQUENZA (UI | START DOWNLOAD | START METEO) -> START DOWNLOAD 2 --> START METEO 2 --> PAUSE -> RESUME -> FINE METEO --> FINE DOWNLOAD --> FINE DOWNLOAD --> FINE METEO
        // 0 - Avvio l'app (che carica la UI e tenta di iniziare i task meteo e download, bloccando la terminazione di entrambi)
        // 1 - Avvio un altro download da UI
        // 2 - Avvio un altro meteo da UI
        // pause
        // resume
        // 7 - Termino il task meteo dopo altri 10 sec
        // 6 - Termino il task download dopo altri 10 sec
        // 6 - Termino il task download dopo altri 10 sec
        // 7 - Termino il task meteo dopo altri 10 sec
        // PROBLEMA: Va in crash su start download la app, a causa della coesistenza di due task download
        //java.lang.NullPointerException: Attempt to invoke virtual method 'int java.util.Calendar.get(int)' on a null object reference
        //at com.porfirio.orariprocida2011.activities.OrariProcida2011Activity.onOptionsItemSelected(OrariProcida2011Activity.java:177)

        // Definisco i semafori, uno per ogni task, eventualmente settando il numero di task possibili
        DownloadMezziTask.taskDownload = new Semaphore(1);
        DownloadMezziTask.taskDownloadStart = new Semaphore(1);
        LeggiMeteoTask.taskMeteo = new Semaphore(1);
        LeggiMeteoTask.taskMeteoStart = new Semaphore(1);


        //Il test mette rosso i semafori, in modo da poterne determinare autonomamente lo sblocco
        Log.d("TEST", "Il test prova ad acquisire i semafori");
        DownloadMezziTask.taskDownload.acquire();
        LeggiMeteoTask.taskMeteo.acquire();
        //DownloadMezziTask.taskDownloadStart.acquire();
        //LeggiMeteoTask.taskMeteoStart.acquire();

        Log.d("TEST", "Inizia il test, valori dei semafori: Download=" + DownloadMezziTask.taskDownload.availablePermits() + " meteo=" + LeggiMeteoTask.taskMeteo.availablePermits());


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
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        Log.d("TEST", "TEST: Avvio la activity");
        //Log.d("TEST","TEST: valori dei semafori: Download="+DownloadMezziTask.taskDownload.availablePermits()+" meteo="+LeggiMeteoTask.taskMeteo.availablePermits());
        //Thread.sleep(5000);
        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT * 1000);
        Log.d("TEST", "TEST: Fine before");
        //La app è stata avviata

        //AVVIO UN DOWNLOAD DA UI
        Thread.sleep(3000);
        mDevice.pressMenu();
        UiObject ui = mDevice.findObject(new UiSelector().text("Aggiorna orari da Web"));
        ui.click();
        Thread.sleep(1000);
        DownloadMezziTask.taskDownloadStart.release();


        //AVVIO UN METEO DA UI
        Thread.sleep(1000);
        mDevice.pressMenu();
        UiObject ui2 = mDevice.findObject(new UiSelector().text("Aggiorna dati meteo"));
        ui2.click();
        LeggiMeteoTask.taskMeteoStart.release();

        mDevice.pressHome();
        Log.d("TEST", "TEST: Pause app");
        Thread.sleep(2000);
        mDevice.pressRecentApps();
        Thread.sleep(1000);
        mDevice.pressRecentApps();
        Log.d("TEST", "TEST: Resume app");

        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task meteo ");
        LeggiMeteoTask.taskMeteo.release();
        Log.d("TEST", "TEST: Task meteo Terminato");

        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download ");
        DownloadMezziTask.taskDownload.release();
        Log.d("TEST", "TEST: Task Download Terminato");

        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download ");
        DownloadMezziTask.taskDownload.release();
        Log.d("TEST", "TEST: Task Download Terminato");

        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task meteo ");
        LeggiMeteoTask.taskMeteo.release();
        Log.d("TEST", "TEST: Task meteo Terminato");

        Log.d("TEST", "TEST: Fine del test");

    }

    @Test
    public void UndecimoTest() throws InterruptedException, UiObjectNotFoundException, RemoteException {
        // SEQUENZA (UI ) -> DOWNLOAD 2 DA UI -->  START DOWNLOAD 1 --> START DOWNLOAD 2 --> FINE DOWNLOAD --> FINE DOWNLOAD
        // 0 - Avvio l'app (che carica la UI ma blocca l'inizio del download)
        // 1 - Avvio un altro download da UI
        // 2 - Termino il task download dopo altri 10 sec
        // 3 - Termino il task download dopo altri 10 sec
        // PROBLEMA: Va in crash su start download la app per la contemporanea presenza di due task download attivi
        //java.lang.NullPointerException: Attempt to invoke virtual method 'int java.util.Calendar.get(int)' on a null object reference
        //at com.porfirio.orariprocida2011.activities.OrariProcida2011Activity.onOptionsItemSelected(OrariProcida2011Activity.java:177)

        // Definisco i semafori, uno per ogni task, eventualmente settando il numero di task possibili
        DownloadMezziTask.taskDownload = new Semaphore(1);
        DownloadMezziTask.taskDownloadStart = new Semaphore(1);
        LeggiMeteoTask.taskMeteo = new Semaphore(1);
        LeggiMeteoTask.taskMeteoStart = new Semaphore(1);


        //Il test mette rosso i semafori, in modo da poterne determinare autonomamente lo sblocco
        Log.d("TEST", "Il test prova ad acquisire i semafori");
        DownloadMezziTask.taskDownload.acquire();
        //LeggiMeteoTask.taskMeteo.acquire();
        DownloadMezziTask.taskDownloadStart.acquire();
        //LeggiMeteoTask.taskMeteoStart.acquire();

        Log.d("TEST", "Inizia il test, valori dei semafori: Download=" + DownloadMezziTask.taskDownload.availablePermits() + " meteo=" + LeggiMeteoTask.taskMeteo.availablePermits());


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
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        Log.d("TEST", "TEST: Avvio la activity");
        //Log.d("TEST","TEST: valori dei semafori: Download="+DownloadMezziTask.taskDownload.availablePermits()+" meteo="+LeggiMeteoTask.taskMeteo.availablePermits());
        //Thread.sleep(5000);
        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT * 1000);
        Log.d("TEST", "TEST: Fine before");
        //La app è stata avviata

        //SELEZIONO UN DOWNLOAD DA UI
        Thread.sleep(3000);
        mDevice.pressMenu();
        UiObject ui = mDevice.findObject(new UiSelector().text("Aggiorna orari da Web"));
        ui.click();

        // AVVIO IL PRIMO DOWNLOAD
        Thread.sleep(1000);
        DownloadMezziTask.taskDownloadStart.release();

        // AVVIO L'ALTRO DOWNLOAD
        Thread.sleep(1000);
        DownloadMezziTask.taskDownloadStart.release();


        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download ");
        DownloadMezziTask.taskDownload.release();
        Log.d("TEST", "TEST: Task Download Terminato");

        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download ");
        DownloadMezziTask.taskDownload.release();
        Log.d("TEST", "TEST: Task Download Terminato");


        Log.d("TEST", "TEST: Fine del test");

    }

    @Test
    public void DodicesimoTest() throws InterruptedException, UiObjectNotFoundException, RemoteException {
        // SEQUENZA (UI ) -> meteo 2 DA UI -->  START meteo 1 --> START meteo 2 --> FINE meteo --> FINE meteo
        // 0 - Avvio l'app (che carica la UI ma blocca l'inizio del meteo)
        // 1 - Avvio un altro meteo da UI
        // 2 - Termino il task meteo dopo altri 10 sec
        // 3 - Termino il task meteo dopo altri 10 sec
        // PROBLEMA: Va in crash su start meteo la app per la contemporanea presenza di due task meteo attivi
        //java.lang.NullPointerException: Attempt to invoke virtual method 'int java.util.Calendar.get(int)' on a null object reference
        //at com.porfirio.orariprocida2011.activities.OrariProcida2011Activity.setSpinner(OrariProcida2011Activity.java:989)

        // Definisco i semafori, uno per ogni task, eventualmente settando il numero di task possibili
        DownloadMezziTask.taskDownload = new Semaphore(1);
        DownloadMezziTask.taskDownloadStart = new Semaphore(1);
        LeggiMeteoTask.taskMeteo = new Semaphore(1);
        LeggiMeteoTask.taskMeteoStart = new Semaphore(1);


        //Il test mette rosso i semafori, in modo da poterne determinare autonomamente lo sblocco
        Log.d("TEST", "Il test prova ad acquisire i semafori");
        //DownloadMezziTask.taskDownload.acquire();
        LeggiMeteoTask.taskMeteo.acquire();
        //DownloadMezziTask.taskDownloadStart.acquire();
        LeggiMeteoTask.taskMeteoStart.acquire();

        Log.d("TEST", "Inizia il test, valori dei semafori: Download=" + DownloadMezziTask.taskDownload.availablePermits() + " meteo=" + LeggiMeteoTask.taskMeteo.availablePermits());


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
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        Log.d("TEST", "TEST: Avvio la activity");
        //Log.d("TEST","TEST: valori dei semafori: Download="+DownloadMezziTask.taskDownload.availablePermits()+" meteo="+LeggiMeteoTask.taskMeteo.availablePermits());
        //Thread.sleep(5000);
        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT * 1000);
        Log.d("TEST", "TEST: Fine before");
        //La app è stata avviata

        //SELEZIONO UN meteo DA UI
        Thread.sleep(3000);
        mDevice.pressMenu();
        UiObject ui = mDevice.findObject(new UiSelector().text("Aggiorna dati meteo"));
        ui.click();

        // AVVIO IL PRIMO METEO
        Thread.sleep(1000);
        LeggiMeteoTask.taskMeteoStart.release();

        // AVVIO L'ALTRO METEO
        Thread.sleep(1000);
        LeggiMeteoTask.taskMeteoStart.release();


        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task meteo ");
        LeggiMeteoTask.taskMeteo.release();
        Log.d("TEST", "TEST: Task meteo Terminato");

        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task meteo ");
        LeggiMeteoTask.taskMeteo.release();
        Log.d("TEST", "TEST: Task Meteo Terminato");


        Log.d("TEST", "TEST: Fine del test");

    }

    @Test
    public void QuindicesimoTest() throws InterruptedException, UiObjectNotFoundException, RemoteException {
        // SEQUENZA (UI | START DOWNLOAD ) --> PAUSE --> FINE DOWNLOAD -> RESUME --> START DOWNLOAD DA UI --> FINE DOWNLOAD
        // 0 - Avvio l'app
        // 1 - Sblocco un altro download da UI
        // pause
        // 6 - Termino il task download dopo altri 10 sec
        // resume
        // PROBLEMA: No dà crash ma è da valutare l'esito rispetto a quello atteso

        // Definisco i semafori, uno per ogni task, eventualmente settando il numero di task possibili
        DownloadMezziTask.taskDownload = new Semaphore(1);
        DownloadMezziTask.taskDownloadStart = new Semaphore(1);
        LeggiMeteoTask.taskMeteo = new Semaphore(1);
        LeggiMeteoTask.taskMeteoStart = new Semaphore(1);


        //Il test mette rosso i semafori, in modo da poterne determinare autonomamente lo sblocco
        Log.d("TEST", "Il test prova ad acquisire i semafori");
        DownloadMezziTask.taskDownload.acquire();
        //LeggiMeteoTask.taskMeteo.acquire();
        //DownloadMezziTask.taskDownloadStart.acquire();
        //LeggiMeteoTask.taskMeteoStart.acquire();

        Log.d("TEST", "Inizia il test, valori dei semafori: Download=" + DownloadMezziTask.taskDownload.availablePermits() + " meteo=" + LeggiMeteoTask.taskMeteo.availablePermits());


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
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        Log.d("TEST", "TEST: Avvio la activity");
        //Log.d("TEST","TEST: valori dei semafori: Download="+DownloadMezziTask.taskDownload.availablePermits()+" meteo="+LeggiMeteoTask.taskMeteo.availablePermits());
        //Thread.sleep(5000);
        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT * 1000);
        Log.d("TEST", "TEST: Fine before");
        //La app è stata avviata


        mDevice.pressHome();
        Log.d("TEST", "TEST: Pause app");
        Thread.sleep(2000);

        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download ");
        DownloadMezziTask.taskDownload.release();
        Log.d("TEST", "TEST: Task Download Terminato");

        mDevice.pressRecentApps();
        Thread.sleep(1000);
        mDevice.pressRecentApps();
        Log.d("TEST", "TEST: Resume app");

        //SELEZIONO UN DOWNLOAD DA UI
        Thread.sleep(3000);
        mDevice.pressMenu();
        UiObject ui = mDevice.findObject(new UiSelector().text("Aggiorna orari da Web"));
        ui.click();

        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download ");
        DownloadMezziTask.taskDownloadStart.release();
        Log.d("TEST", "TEST: Task Download Terminato");

        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download ");
        DownloadMezziTask.taskDownload.release();
        Log.d("TEST", "TEST: Task Download Terminato");


        Log.d("TEST", "TEST: Fine del test");

    }

    @Test
    public void QuattordicesimoTest() throws InterruptedException, UiObjectNotFoundException, RemoteException {
        // SEQUENZA (UI | START METEO ) --> PAUSE --> FINE METEO -> RESUME
        // 0 - Avvio l'app
        // 1 - Sblocco un altro meteo da UI
        // pause
        // 6 - Termino il task meteo dopo altri 10 sec
        // resume
        // PROBLEMA: No dà crash ma è da valutare l'esito rispetto a quello atteso, probabilmente con un log

        // Definisco i semafori, uno per ogni task, eventualmente settando il numero di task possibili
        DownloadMezziTask.taskDownload = new Semaphore(1);
        DownloadMezziTask.taskDownloadStart = new Semaphore(1);
        LeggiMeteoTask.taskMeteo = new Semaphore(1);
        LeggiMeteoTask.taskMeteoStart = new Semaphore(1);


        //Il test mette rosso i semafori, in modo da poterne determinare autonomamente lo sblocco
        Log.d("TEST", "Il test prova ad acquisire i semafori");
        //DownloadMezziTask.taskDownload.acquire();
        LeggiMeteoTask.taskMeteo.acquire();
        //DownloadMezziTask.taskDownloadStart.acquire();
        //LeggiMeteoTask.taskMeteoStart.acquire();

        Log.d("TEST", "Inizia il test, valori dei semafori: Download=" + DownloadMezziTask.taskDownload.availablePermits() + " meteo=" + LeggiMeteoTask.taskMeteo.availablePermits());


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
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        Log.d("TEST", "TEST: Avvio la activity");
        //Log.d("TEST","TEST: valori dei semafori: Download="+DownloadMezziTask.taskDownload.availablePermits()+" meteo="+LeggiMeteoTask.taskMeteo.availablePermits());
        //Thread.sleep(5000);
        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT * 1000);
        Log.d("TEST", "TEST: Fine before");
        //La app è stata avviata


        mDevice.pressHome();
        Log.d("TEST", "TEST: Pause app");
        Thread.sleep(2000);

        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task meteo ");
        LeggiMeteoTask.taskMeteo.release();
        Log.d("TEST", "TEST: Task Download Terminato");

        mDevice.pressRecentApps();
        Thread.sleep(1000);
        mDevice.pressRecentApps();
        Log.d("TEST", "TEST: Resume app");


        Log.d("TEST", "TEST: Fine del test");

    }

    @Test
    public void TredicesimoTest() throws InterruptedException, UiObjectNotFoundException, RemoteException {
        // SEQUENZA (UI ) --> PAUSE --> START DOWNLOAD --> FINE DOWNLOAD -> RESUME --> DOWNLOAD DA UI --> FINE DOWNLOAD --> FINE DOWNLOAD
        // 0 - Avvio l'app
        // 1 - Sblocco un altro download da UI
        // pause
        // 6 - Termino il task download dopo altri 10 sec
        // resume
        // PROBLEMA: Da notare il toast in sovraimpressione sulla Home, ma nessun crash

        // Definisco i semafori, uno per ogni task, eventualmente settando il numero di task possibili
        DownloadMezziTask.taskDownload = new Semaphore(1);
        DownloadMezziTask.taskDownloadStart = new Semaphore(1);
        LeggiMeteoTask.taskMeteo = new Semaphore(1);
        LeggiMeteoTask.taskMeteoStart = new Semaphore(1);


        //Il test mette rosso i semafori, in modo da poterne determinare autonomamente lo sblocco
        Log.d("TEST", "Il test prova ad acquisire i semafori");
        DownloadMezziTask.taskDownload.acquire();
        //LeggiMeteoTask.taskMeteo.acquire();
        DownloadMezziTask.taskDownloadStart.acquire();
        //LeggiMeteoTask.taskMeteoStart.acquire();

        Log.d("TEST", "Inizia il test, valori dei semafori: Download=" + DownloadMezziTask.taskDownload.availablePermits() + " meteo=" + LeggiMeteoTask.taskMeteo.availablePermits());


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
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        Log.d("TEST", "TEST: Avvio la activity");
        //Log.d("TEST","TEST: valori dei semafori: Download="+DownloadMezziTask.taskDownload.availablePermits()+" meteo="+LeggiMeteoTask.taskMeteo.availablePermits());
        //Thread.sleep(5000);
        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT * 1000);
        Log.d("TEST", "TEST: Fine before");
        //La app è stata avviata


        mDevice.pressHome();
        Log.d("TEST", "TEST: Pause app");
        Thread.sleep(2000);

        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca l'avvio del task download ");
        DownloadMezziTask.taskDownloadStart.release();
        Log.d("TEST", "TEST: Task Download Avviato");

        mDevice.pressRecentApps();
        Thread.sleep(1000);
        mDevice.pressRecentApps();
        Log.d("TEST", "TEST: Resume app");

        //SELEZIONO UN DOWNLOAD DA UI
        Thread.sleep(3000);
        mDevice.pressMenu();
        UiObject ui = mDevice.findObject(new UiSelector().text("Aggiorna orari da Web"));
        ui.click();
        Thread.sleep(3000);

        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca l'avvio del task download ");
        DownloadMezziTask.taskDownloadStart.release();
        Log.d("TEST", "TEST: Task Download Avviato");

        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download ");
        DownloadMezziTask.taskDownload.release();
        Log.d("TEST", "TEST: Task Download Terminato");

        Thread.sleep(3000);
        Log.d("TEST", "TEST: Il test sblocca la terminazione del task download ");
        DownloadMezziTask.taskDownload.release();
        Log.d("TEST", "TEST: Task Download Terminato");

        Log.d("TEST", "TEST: Fine del test");

    }


*/
}
