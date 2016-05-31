package dk.nodes.exampleproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UserDataManager userDataManager = new UserDataManager(this.getApplicationContext());
        UserData userData = userDataManager.load();
    }
}
