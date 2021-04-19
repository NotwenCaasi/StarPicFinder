package starpicfinder.org;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class EnterSettings extends Activity {
    LinearLayout layout = null;
    EditText settings_resolution = null;
    EditText settings_focale = null;
    EditText settings_min_lum = null;
    EditText settings_box_size = null;
    private double image_resolution = 5.0;
    private double focale_length = 522.0;
    private double min_lum = 10.0;
    private int box_size = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layout = (LinearLayout) LinearLayout.inflate(this, R.layout.enter_settings, null);

        setContentView(layout);
    }

    public void closeSettings(View view) {
        settings_resolution = (EditText) layout.findViewById(R.id.settings_resolution);
        settings_focale = (EditText) layout.findViewById(R.id.settings_focale);
        settings_min_lum = (EditText) layout.findViewById(R.id.settings_min_lum);
        settings_box_size = (EditText) layout.findViewById(R.id.settings_box_size);

        this.image_resolution = Double.parseDouble(settings_resolution.getText().toString());
        this.focale_length = Double.parseDouble(settings_focale.getText().toString());
        this.min_lum = Double.parseDouble(settings_min_lum.getText().toString());
        this.box_size = Integer.parseInt(settings_box_size.getText().toString());

        MainActivity.set_image_parameters(this.image_resolution, this.focale_length, this.min_lum, this.box_size);
        EnterSettings.this.finish();
    }
}