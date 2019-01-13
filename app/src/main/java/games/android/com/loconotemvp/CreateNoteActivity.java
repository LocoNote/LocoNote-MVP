package games.android.com.loconotemvp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import games.android.com.loconotemvp.MapsActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class CreateNoteActivity extends AppCompatActivity {

    private Button submitNote;
    private  EditText messageText;
    private LatLng currLoc;
    private double lat, lng;
    private DatabaseReference noteReference;
    private String[] emotions = {"cool","happy","laughing","love","sad","surprise","weird"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        Intent intent = getIntent();
        List<String> spinnerArray =  new ArrayList<String>();

        for(int i = 0; i<7; i++){
            spinnerArray.add(emotions[i]);
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner sItems = (Spinner) findViewById(R.id.MySpinner);
        sItems.setAdapter(adapter);

        Bundle extras = intent.getExtras();

        double lat = extras.getDouble("lat");
        double lng = extras.getDouble("lng");

        currLoc = new LatLng(lat,lng);
        noteReference = FirebaseDatabase.getInstance().getReference();


        messageText = (EditText) findViewById(R.id.message_text);

        submitNote = (Button) findViewById(R.id.submit_note);

        submitNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String finalMessage = messageText.getText().toString();
                String tag = sItems.getSelectedItem().toString();
                if(tag != null && finalMessage != null){
                    Message message = new Message(finalMessage,currLoc,tag);
                    DatabaseReference noteKey = noteReference.child("Notes").push();
                    noteKey.setValue(message);
                    Toast.makeText(CreateNoteActivity.this,tag,Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(CreateNoteActivity.this, MapsActivity.class);
                    startActivity(intent1);
                    finish();
                }



            }
        });


    }
}
