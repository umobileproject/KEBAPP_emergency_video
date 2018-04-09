package uk.ac.ucl.umobile.data;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.ucl.kbapp.KebappService;
import uk.ac.ucl.kbapp.net.Link;
import uk.ac.ucl.umobile.utils.Config;
import uk.ac.ucl.kbapp.utils.G;
import android.os.PowerManager;

import net.named_data.jndn.Data;
import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.util.Blob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static java.lang.Thread.sleep;

public class UmobileService extends KebappService {

    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private VideoDatabaseHandler db;

    @Override
    public void onCreate(){
        super.onCreate();
        db = new VideoDatabaseHandler(this);
        for (Content content : db.getContentDownloaded()) {
            G.Log(TAG,"Content advertisement add element " + content.getName());
            init.add(content.getUri());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

        G.Log(TAG,"Service onBind");
        return m_ubicdnServiceMessenger.getBinder();
    }

    public void turnOnScreen(){
        // turn on screen
        G.Log(TAG,"ProximityActivity", "ON!");
        mPowerManager =  (PowerManager) getSystemService(Context.POWER_SERVICE);
        //PowerManager.ACQUIRE_CAUSES_WAKEUP
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
        mWakeLock.acquire();


    }

    @Override
    public void linkNetworkDiscovered(Link link, String network)
    {

        G.Log(TAG, "Frame received " + network+" "+db.getContentDownloaded().size());
        String[] separated = network.split(":");

        if(separated.length<=3) {
            if(db.getContentDownloaded().size()==0){
                setConnect(true);
                sendToast("Content discovered on link "+separated[0]);
            }else {
                sendToast("Link "+separated[0]+" with the same content");
            }
        }
        super.linkNetworkDiscovered(link,network);
    }

    @Override
    public void wifiLinkConnected(Link link, String network) {
        //int conn = stats.getConnections();
        //stats.setConnections(++conn);
        if (network.equals("\"UmoPi_AP\"")) {
            Content c = new Content(Config.video, Config.prefix + Config.video, Config.text, "");
            db.addContent(c);
            getFile(Config.video);
        }
        super.wifiLinkConnected(link,network);
    }

    @Override
    public void onVerified(Data data) {
        //  G.Log(TAG,"File received "+data.getName() +" "+data.getContent().size());

        sendToast("New video received " + data.getName().get(2).toEscapedString() + ".mp4. Signature verfication successful");
        //  G.Log(TAG,"File received "+data.getName() +" "+data.getContent().size());
        Blob b = data.getContent();

        try {
            G.Log(TAG,"Saving file "+getFilesDir() + "/" + data.getName().get(2).toEscapedString() + ".mp4");
            File f = new File(getFilesDir() + "/" + data.getName().get(2).toEscapedString() + ".mp4");
            // File f2 = new File(getFilesDir() + "/" + data.getName().get(2).toEscapedString() + ".mp4.bin");
            FileOutputStream fos = new FileOutputStream(f);
            //  FileOutputStream fos2 = new FileOutputStream(f2);
            fos.write(b.getImmutableArray());
            // fos2.write(b2.getImmutableArray());
        }catch (FileNotFoundException e){G.Log(TAG,e.getMessage());
        }catch (IOException e){G.Log(TAG,e.getMessage());}

        try {
            G.Log(TAG,"Saving file "+getFilesDir() + "/" + data.getName().get(2).toEscapedString() + ".mp4.bin");
            File f = new File(getFilesDir() + "/" + data.getName().get(2).toEscapedString() + ".mp4.bin");
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(data.wireEncode().getImmutableArray());
            Intent broadcast = new Intent(DOWNLOAD_COMPLETED);
            sleep(4000);
            sendBroadcast(broadcast);
        }catch (FileNotFoundException e){G.Log(TAG,e.getMessage());
        }catch (Exception e){G.Log(TAG,e.getMessage());}
        G.Log(TAG,"setcontentdownloaded "+data.getName().toString()+" "+db.getContentDownloaded().size());
        db.setContentDownloaded(data.getName().toString());
        G.Log(TAG,"setcontentdownloaded "+data.getName().toString()+" "+db.getContentDownloaded().size());
        super.onVerified(data);
    }

    public void onData(Interest interest, Data data){

        sendToast("New video received " + data.getName().get(2).toEscapedString() + ".mp4");
        //  G.Log(TAG,"File received "+data.getName() +" "+data.getContent().size());
        Blob b = data.getContent();

        try {
            G.Log(TAG,"Saving file "+getFilesDir() + "/" + data.getName().get(2).toEscapedString() + ".mp4");
            File f = new File(getFilesDir() + "/" + data.getName().get(2).toEscapedString() + ".mp4");
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(b.getImmutableArray());
            Intent broadcast = new Intent(DOWNLOAD_COMPLETED);
            sleep(4000);
            sendBroadcast(broadcast);
        }catch (FileNotFoundException e){G.Log(TAG,e.getMessage());
        }catch (Exception e){G.Log(TAG,e.getMessage());}

        db.setContentDownloaded(data.getName().toString());
        super.onData(interest,data);
    }
    @Override
    public void onDataValidationFailed(Data data, String reason) {
        G.Log(TAG,"Data Verification Failed: " + reason);
        //sendToast("Data Verification Failed. Video "+data.getName().get(2).toEscapedString()+" discarded");
        super.onDataValidationFailed(data,reason);
    }

    public void sendToast(final String msg)
    {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        msg,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
