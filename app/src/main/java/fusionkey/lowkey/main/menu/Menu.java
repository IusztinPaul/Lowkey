package fusionkey.lowkey.main.menu;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;


import fusionkey.lowkey.LowKeyApplication;
import fusionkey.lowkey.R;
import fusionkey.lowkey.entryActivity.EntryActivity;
import fusionkey.lowkey.main.menu.about.About;
import fusionkey.lowkey.main.menu.license.LicenseActivity;
import fusionkey.lowkey.main.menu.profile.ChangePasswordActivity;
import fusionkey.lowkey.main.menu.profile.EditUserActivity;
import fusionkey.lowkey.main.menu.settings.SettingsActivity;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ConstraintLayout settings = findViewById(R.id.SettingsLayout);
        ConstraintLayout edit = findViewById(R.id.EditLayout);
        ConstraintLayout change = findViewById(R.id.ChangeLayout);
        ConstraintLayout about = findViewById(R.id.AboutLayoout);
        ConstraintLayout license = findViewById(R.id.LicenseLayout);
        ConstraintLayout logout = findViewById(R.id.LogoutLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditUserActivity.class);
                startActivity(intent);
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), About.class);
                startActivity(intent);
            }
        });
        license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LicenseActivity.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LowKeyApplication.userManager.logout();
                Intent intent = new Intent(getApplicationContext(), EntryActivity.class);
                startActivity(intent);
            }
        });

    }
    private void logOut(){
        LowKeyApplication.userManager.logout();
        this.startActivity(new Intent(this, EntryActivity.class));
    }
}
