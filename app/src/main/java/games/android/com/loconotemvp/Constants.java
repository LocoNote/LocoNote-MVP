package games.android.com.loconotemvp;

/**
 * Created by paen3 on 1/13/2019.
 */

public class Constants {
    private float GEOFENCE_RADIUS_IN_METERS = 20;
    private long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 30000;
    private int GEOFENCE_TRANSITION_ENTER;
    private int GEOFENCE_TRANSITION_EXIT;
    public Constants(){
        this.GEOFENCE_EXPIRATION_IN_MILLISECONDS = 20;
        this.GEOFENCE_EXPIRATION_IN_MILLISECONDS = 3000;
        this.GEOFENCE_TRANSITION_ENTER = 0;
        this.GEOFENCE_TRANSITION_EXIT = 1;
    }

    public float getGEOFENCE_RADIUS_IN_METERS() {
        return GEOFENCE_RADIUS_IN_METERS;
    }

    public long getGEOFENCE_EXPIRATION_IN_MILLISECONDS() {
        return GEOFENCE_EXPIRATION_IN_MILLISECONDS;
    }

    public int getGEOFENCE_TRANSITION_ENTER() {
        return GEOFENCE_TRANSITION_ENTER;
    }

    public int getGEOFENCE_TRANSITION_EXIT() {
        return GEOFENCE_TRANSITION_EXIT;
    }
}
