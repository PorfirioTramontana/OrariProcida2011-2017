package com.porfirio.orariprocida2011.activities;


import android.os.RemoteException;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.util.Log;

import com.porfirio.orariprocida2011.tasks.DownloadMezziTask;
import com.porfirio.orariprocida2011.tasks.LeggiMeteoTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import it.unina.ptramont.utilityTest.GeneralEvent;
import it.unina.ptramont.utilityTest.SpecificUIEvent;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class GeneralUIAutomatorAsyncTaskTestSemaphore {

    @BeforeClass
    public static void startup() {
        GeneralEvent.setTime(GeneralEvent.NORMAL);
    }

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
        GeneralEvent.declareandSetSemaphore(DownloadMezziTask.sem);
        GeneralEvent.declareandSetSemaphore(LeggiMeteoTask.sem);

        GeneralEvent.startApp("com.porfirio.orariprocida2011");
        GeneralEvent.start(DownloadMezziTask.sem);
        GeneralEvent.start(LeggiMeteoTask.sem);
        GeneralEvent.finish(DownloadMezziTask.sem);
        GeneralEvent.finish(LeggiMeteoTask.sem);

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

        //Declare and Set Semaphores
        GeneralEvent.declareandSetSemaphore(DownloadMezziTask.sem);
        GeneralEvent.declareandSetSemaphore(LeggiMeteoTask.sem);

        GeneralEvent.start(DownloadMezziTask.sem);
        GeneralEvent.finish(DownloadMezziTask.sem);
        GeneralEvent.startApp("com.porfirio.orariprocida2011");
        GeneralEvent.start(LeggiMeteoTask.sem);
        GeneralEvent.finish(LeggiMeteoTask.sem);

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

        //Declare and Set Semaphores
        GeneralEvent.declareandSetSemaphore(DownloadMezziTask.sem);
        GeneralEvent.declareandSetSemaphore(LeggiMeteoTask.sem);

        GeneralEvent.start(LeggiMeteoTask.sem);
        GeneralEvent.finish(LeggiMeteoTask.sem);
        GeneralEvent.startApp("com.porfirio.orariprocida2011");
        GeneralEvent.start(DownloadMezziTask.sem);
        GeneralEvent.finish(DownloadMezziTask.sem);

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

        //Declare and Set Semaphores
        GeneralEvent.declareandSetSemaphore(DownloadMezziTask.sem);
        GeneralEvent.declareandSetSemaphore(LeggiMeteoTask.sem);

        GeneralEvent.start(LeggiMeteoTask.sem);
        GeneralEvent.startApp("com.porfirio.orariprocida2011");
        GeneralEvent.finish(LeggiMeteoTask.sem);
        GeneralEvent.start((DownloadMezziTask.sem));
        GeneralEvent.finish(DownloadMezziTask.sem);

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

        //Declare and Set Semaphores
        GeneralEvent.declareandSetSemaphore(DownloadMezziTask.sem);
        GeneralEvent.declareandSetSemaphore(LeggiMeteoTask.sem);

        GeneralEvent.start(LeggiMeteoTask.sem);
        GeneralEvent.startApp("com.porfirio.orariprocida2011");
        GeneralEvent.finish(LeggiMeteoTask.sem);
        GeneralEvent.start(DownloadMezziTask.sem);
        SpecificUIEvent.execute(SpecificUIEvent.AGGIORNA_ORARI_DA_WEB);
        GeneralEvent.start(DownloadMezziTask.sem);
        GeneralEvent.finish(DownloadMezziTask.sem);
        GeneralEvent.finish(DownloadMezziTask.sem);
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

        //Declare and Set Semaphores
        GeneralEvent.declareandSetSemaphore(DownloadMezziTask.sem);
        GeneralEvent.declareandSetSemaphore(LeggiMeteoTask.sem);

        GeneralEvent.start(LeggiMeteoTask.sem);
        GeneralEvent.startApp("com.porfirio.orariprocida2011");
        GeneralEvent.finish(LeggiMeteoTask.sem);
        GeneralEvent.start(DownloadMezziTask.sem);
        GeneralEvent.pause();
        GeneralEvent.resume();
        SpecificUIEvent.execute(SpecificUIEvent.AGGIORNA_ORARI_DA_WEB);
        GeneralEvent.start(DownloadMezziTask.sem);
        GeneralEvent.finish(DownloadMezziTask.sem);
        GeneralEvent.finish(DownloadMezziTask.sem);
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


        //Declare and Set Semaphores
        GeneralEvent.declareandSetSemaphore(DownloadMezziTask.sem);
        GeneralEvent.declareandSetSemaphore(LeggiMeteoTask.sem);

        GeneralEvent.start(LeggiMeteoTask.sem);
        GeneralEvent.finish(LeggiMeteoTask.sem);
        GeneralEvent.start(DownloadMezziTask.sem);
        GeneralEvent.startApp("com.porfirio.orariprocida2011");
        SpecificUIEvent.execute(SpecificUIEvent.AGGIORNA_ORARI_DA_WEB);
        GeneralEvent.start(DownloadMezziTask.sem);
        SpecificUIEvent.execute(SpecificUIEvent.AGGIORNA_ORARI_DA_WEB);
        GeneralEvent.start(DownloadMezziTask.sem);
        GeneralEvent.finish(DownloadMezziTask.sem);
        GeneralEvent.finish(DownloadMezziTask.sem);
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

        //Declare and Set Semaphores
        GeneralEvent.declareandSetSemaphore(DownloadMezziTask.sem);
        GeneralEvent.declareandSetSemaphore(LeggiMeteoTask.sem);

        GeneralEvent.start(DownloadMezziTask.sem);
        GeneralEvent.start(LeggiMeteoTask.sem);
        GeneralEvent.finish(LeggiMeteoTask.sem);
        GeneralEvent.startApp("com.porfirio.orariprocida2011");
        GeneralEvent.pause();
        GeneralEvent.resume();
        GeneralEvent.finish(DownloadMezziTask.sem);
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

        //Declare and Set Semaphores
        GeneralEvent.declareandSetSemaphore(DownloadMezziTask.sem);
        GeneralEvent.declareandSetSemaphore(LeggiMeteoTask.sem);

        GeneralEvent.start(DownloadMezziTask.sem);
        GeneralEvent.start(LeggiMeteoTask.sem);
        GeneralEvent.startApp("com.porfirio.orariprocida2011");
        GeneralEvent.pause();
        GeneralEvent.resume();
        GeneralEvent.finish(DownloadMezziTask.sem);
        GeneralEvent.finish(LeggiMeteoTask.sem);
    }

    @Test
    public void NonoTest() throws InterruptedException, UiObjectNotFoundException, RemoteException {
        // SEQUENZA (UI) -> START DOWNLOAD 1 --> START DOWNLOAD 2 --> END DOWNLOAD 2 --> END DOWNLOAD 1
        // 0 - Avvio l'app (che carica la UI e tenta di iniziare i task meteo bloccando il task download)
        // 1 - avvio un altro task download
        // 2 - termino l'ultimo task download
        // 6 - Termino il primo task download
        // PROBLEMA: I task lasciati appesi terminano senza poter comunicare i risultati, gisutamente

        //Declare and Set Semaphores
        GeneralEvent.declareandSetSemaphore(DownloadMezziTask.sem);
        GeneralEvent.declareandSetSemaphore(LeggiMeteoTask.sem);

        GeneralEvent.start(LeggiMeteoTask.sem);
        GeneralEvent.finish(LeggiMeteoTask.sem);
        GeneralEvent.startApp("com.porfirio.orariprocida2011");
        GeneralEvent.pause();
        GeneralEvent.resume();
        GeneralEvent.start(DownloadMezziTask.sem);
        SpecificUIEvent.execute(SpecificUIEvent.AGGIORNA_ORARI_DA_WEB);
        GeneralEvent.start(DownloadMezziTask.sem);
        GeneralEvent.finish(DownloadMezziTask.sem);
        GeneralEvent.finish(DownloadMezziTask.sem);
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

        //Declare and Set Semaphores
        GeneralEvent.declareandSetSemaphore(DownloadMezziTask.sem);
        GeneralEvent.declareandSetSemaphore(LeggiMeteoTask.sem);

        GeneralEvent.start(LeggiMeteoTask.sem);
        GeneralEvent.start(DownloadMezziTask.sem);
        GeneralEvent.startApp("com.porfirio.orariprocida2011");
        SpecificUIEvent.execute(SpecificUIEvent.AGGIORNA_ORARI_DA_WEB);
        GeneralEvent.start(DownloadMezziTask.sem);
        SpecificUIEvent.execute(SpecificUIEvent.LEGGI_METEO);
        GeneralEvent.start(LeggiMeteoTask.sem);
        GeneralEvent.pause();
        GeneralEvent.resume();
        GeneralEvent.finish(LeggiMeteoTask.sem);
        GeneralEvent.finish(DownloadMezziTask.sem);
        GeneralEvent.finish(DownloadMezziTask.sem);
        GeneralEvent.finish(LeggiMeteoTask.sem);
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

        //Declare and Set Semaphores
        GeneralEvent.declareandSetSemaphore(DownloadMezziTask.sem);
        GeneralEvent.declareandSetSemaphore(LeggiMeteoTask.sem);

        GeneralEvent.start(LeggiMeteoTask.sem);
        GeneralEvent.finish(LeggiMeteoTask.sem);
        GeneralEvent.startApp("com.porfirio.orariprocida2011");
        SpecificUIEvent.execute(SpecificUIEvent.AGGIORNA_ORARI_DA_WEB);
        GeneralEvent.start(DownloadMezziTask.sem);
        GeneralEvent.start(DownloadMezziTask.sem);
        GeneralEvent.finish(DownloadMezziTask.sem);
        GeneralEvent.finish(DownloadMezziTask.sem);
    }


    @Test
    public void DodicesimoTest() {
        // SEQUENZA (UI ) -> meteo 2 DA UI -->  START meteo 1 --> START meteo 2 --> FINE meteo --> FINE meteo
        // 0 - Avvio l'app (che carica la UI ma blocca l'inizio del meteo)
        // 1 - Avvio un altro meteo da UI
        // 2 - Termino il task meteo dopo altri 10 sec
        // 3 - Termino il task meteo dopo altri 10 sec
        // PROBLEMA: Va in crash su start meteo la app per la contemporanea presenza di due task meteo attivi
        //java.lang.NullPointerException: Attempt to invoke virtual method 'int java.util.Calendar.get(int)' on a null object reference
        //at com.porfirio.orariprocida2011.activities.OrariProcida2011Activity.setSpinner(OrariProcida2011Activity.java:989)

        //Declare and Set Semaphores
        GeneralEvent.declareandSetSemaphore(DownloadMezziTask.sem);
        GeneralEvent.declareandSetSemaphore(LeggiMeteoTask.sem);

        GeneralEvent.start(DownloadMezziTask.sem);
        GeneralEvent.finish(DownloadMezziTask.sem);
        GeneralEvent.startApp("com.porfirio.orariprocida2011");
        SpecificUIEvent.execute(SpecificUIEvent.LEGGI_METEO);
        GeneralEvent.start(LeggiMeteoTask.sem);
        GeneralEvent.start(LeggiMeteoTask.sem);
        GeneralEvent.finish(LeggiMeteoTask.sem);
        GeneralEvent.finish(LeggiMeteoTask.sem);
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

        //Declare and Set Semaphores
        GeneralEvent.declareandSetSemaphore(DownloadMezziTask.sem);
        GeneralEvent.declareandSetSemaphore(LeggiMeteoTask.sem);

        GeneralEvent.start(LeggiMeteoTask.sem);
        GeneralEvent.finish(LeggiMeteoTask.sem);
        GeneralEvent.start(DownloadMezziTask.sem);
        GeneralEvent.startApp("com.porfirio.orariprocida2011");
        GeneralEvent.pause();
        GeneralEvent.finish(DownloadMezziTask.sem);
        GeneralEvent.resume();
        SpecificUIEvent.execute(SpecificUIEvent.AGGIORNA_ORARI_DA_WEB);
        GeneralEvent.start(DownloadMezziTask.sem);
        GeneralEvent.finish(DownloadMezziTask.sem);

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

        //Declare and Set Semaphores
        GeneralEvent.declareandSetSemaphore(DownloadMezziTask.sem);
        GeneralEvent.declareandSetSemaphore(LeggiMeteoTask.sem);

        GeneralEvent.start(LeggiMeteoTask.sem);
        GeneralEvent.start(DownloadMezziTask.sem);
        GeneralEvent.finish(DownloadMezziTask.sem);
        GeneralEvent.startApp("com.porfirio.orariprocida2011");
        GeneralEvent.pause();
        GeneralEvent.finish(LeggiMeteoTask.sem);
        GeneralEvent.resume();
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

        //Declare and Set Semaphores
        GeneralEvent.declareandSetSemaphore(DownloadMezziTask.sem);
        GeneralEvent.declareandSetSemaphore(LeggiMeteoTask.sem);

        GeneralEvent.start(LeggiMeteoTask.sem);
        GeneralEvent.finish(LeggiMeteoTask.sem);
        GeneralEvent.startApp("com.porfirio.orariprocida2011");
        GeneralEvent.pause();
        GeneralEvent.start(DownloadMezziTask.sem);
        GeneralEvent.resume();
        SpecificUIEvent.execute(SpecificUIEvent.AGGIORNA_ORARI_DA_WEB);
        GeneralEvent.start(DownloadMezziTask.sem);
        GeneralEvent.finish(DownloadMezziTask.sem);
        GeneralEvent.finish(DownloadMezziTask.sem);
    }

    @After
    public void tearDown() {
        Log.d("TEST", "End test");
    }


}
