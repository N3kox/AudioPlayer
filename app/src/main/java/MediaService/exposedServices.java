package MediaService;
import Bean.song;

public interface exposedServices{
    boolean playFromStart(int id, String mediaPath);
    void setSong(song song);
    song getSong();
    boolean resume();
    boolean pause();
    boolean stop();
    boolean prev();
    boolean next();
    boolean isPlaying();
    int getSeekLength();
    int getDuration();
    void updateSeekBar();
    void stopUpdateSeekBar();
    void seekToPosition(int currentPosition);
    int getSongId();
}
