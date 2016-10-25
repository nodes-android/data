package dk.nodes.exampleproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);


        UserDataManager userDataManager = new UserDataManager(this.getApplicationContext());
        UserData userData = userDataManager.load();
        userData.userName = "test";
        userDataManager.save(userData);

        userData = userDataManager.load();
        textView.setText(userData.userName);
    }
}
