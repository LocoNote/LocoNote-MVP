package games.android.com.loconotemvp;

/**
 * Created by paen3 on 1/12/2019.
 */

public class Message {
    private String messsage;
    private games.android.com.loconotemvp.LatLng latLng;

    public Message(){

    }

    public Message(String messsage_text, LatLng newLoc){

        this.latLng = newLoc;
        this.messsage = messsage_text;

    }

    public String getMesssage() {
        return messsage;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setMesssage(String messsage_text) {
        this.messsage = messsage_text;
    }

    public void setLatLng(LatLng newLoc) {
        this.latLng = newLoc;
    }
}

