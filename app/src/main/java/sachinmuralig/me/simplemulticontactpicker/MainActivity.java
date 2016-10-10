package sachinmuralig.me.simplemulticontactpicker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView contactsDisplay;
    Button pickContacts;
    final int CONTACT_PICK_REQUEST = 1000;
    private static final int REQUEST_CONTACT_PERMISSION_CODE = 903;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactsDisplay = (TextView) findViewById(R.id.txt_selected_contacts);
        pickContacts = (Button) findViewById(R.id.btn_pick_contacts);

        if (weHavePermissionToReadContacts()) {
            enableContactsButton();
        } else {
            requestReadContactsPermissionFirst();
        }
    }

    private void enableContactsButton() {
        pickContacts.setEnabled(true);
        pickContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentContactPick = new Intent(MainActivity.this,ContactsPickerActivity.class);
                // Not required
                // intentContactPick.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                MainActivity.this.startActivityForResult(intentContactPick,CONTACT_PICK_REQUEST);
            }
        });
    }

    private void disableContactsButtonAndShowError() {
        pickContacts.setEnabled(false);
        contactsDisplay.setText(String.valueOf("Need permission to read contacts"));
    }

    private boolean weHavePermissionToReadContacts() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadContactsPermissionFirst() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(this, "We need permission so you can text your friends.", Toast.LENGTH_LONG).show();
            requestForResultContactsPermission();
        } else {
            requestForResultContactsPermission();
        }
    }

    private void requestForResultContactsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACT_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CONTACT_PERMISSION_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            enableContactsButton();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            disableContactsButtonAndShowError();
        }
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CONTACT_PICK_REQUEST && resultCode == RESULT_OK){
            ArrayList<Contact> selectedContacts = data.getParcelableArrayListExtra("SelectedContacts");
            String display = "";
            for(int i = 0; i < selectedContacts.size(); i++) {
                Contact contact = selectedContacts.get(i);
                display += (i+1) + ". " + contact.toString() + "\n";
            }
            contactsDisplay.setText(String.format(Locale.getDefault(), "Selected Contacts : \n\n%s",
                    display));
        }
    }
}
