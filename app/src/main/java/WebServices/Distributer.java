package WebServices;

import android.os.Handler;

public class Distributer {
    private Handler handler;
    private OkHttpRequests client;

    public Distributer(Handler handler){
        this.handler = handler;
        client = new OkHttpRequests();
    }

    public void fetchSearchList(String keyWord){
        client.fetchSearchList(handler,keyWord);
    }

    public void fetchMusic(int id){client.requestAvailable(handler,id);}
}
