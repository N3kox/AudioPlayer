package Config;

import android.os.Environment;

public class GlobalData {
    public final static int MSG_ERROR = -1;
    public final static int MSG_FETCHSEARCHLIST = 1;


    public final static int MSG_SONG_DURATION = 399;
    public final static int MSG_SONG_READY = 400;
    public final static int MSG_CURRENT_POSITION = 401;
    public final static int MSG_SONG_NOTAVAILABLE = 404;



    public final static String dirPath = Environment.getExternalStorageDirectory().toString();

}
