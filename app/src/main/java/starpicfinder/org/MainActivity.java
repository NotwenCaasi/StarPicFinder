package starpicfinder.org;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    LinearLayout layout = null;
    TextView results_AD = null;
    TextView results_Dec = null;
    TextView results_Rot = null;
    TextView param_res = null;
    TextView param_focale = null;
    TextView param_min_lum = null;
    TextView param_box_size = null;
    TextView details_calcul = null;
    ImageView picture_stars = null;
    public static final int PICK_IMAGE = 1;
    public static final int PICK_CONTACT_REQUEST = 2;
    Bitmap sky_picture;
    Boolean sky_picture_loaded = false;
    private static double image_resolution = 5.0;
    private static double focale_length = 522.0;
    private static double min_star_detection = 50;
    private static int box_size = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layout = (LinearLayout) LinearLayout.inflate(this, R.layout.activity_main, null);

        //  Results variables
        param_res = (TextView) layout.findViewById(R.id.param_res);
        param_focale = (TextView) layout.findViewById(R.id.param_focale);
        param_min_lum = (TextView) layout.findViewById(R.id.param_min_lum);
        param_box_size = (TextView) layout.findViewById(R.id.param_box_size);
        results_AD = (TextView) layout.findViewById(R.id.AD);
        results_Dec = (TextView) layout.findViewById(R.id.Dec);
        results_Rot = (TextView) layout.findViewById(R.id.Rot);

        // Read image
        // read raw image
        //Resources res = getResources();
        //InputStream in_s = res.openRawResource(R.raw.example);
        //BufferedImage buffImage = ImageIO.read(in_s);
        // convert it to jpg
        //Bitmap resized = Bitmap. createScaledBitmap ( yourBitmap , 400 , 400 , true ) ;
        //ivImage .setImageBitmap(resized) ;

        // display results
        picture_stars = (ImageView) layout.findViewById(R.id.image_view);
        //picture_stars.setBackgroundResource(R.drawable.example);

        // Details of computation : set scroll if text is too long
        details_calcul = (TextView) layout.findViewById(R.id.details_calcul);
        details_calcul.setText("");
        details_calcul.setMovementMethod(new ScrollingMovementMethod());

        // Display results
        results_AD.setText(getString(R.string.results_ad)+"07h 10m 34.203s");
        results_Dec.setText(getString(R.string.results_dec)+"+43° 30m 10.715s");
        results_Rot.setText(getString(R.string.results_rot)+"+46.4°");

        setContentView(layout);

    }
    public static void set_image_parameters(Double resolution, Double focale, Double min_lum, Integer box) {
        image_resolution = resolution;
        focale_length = focale;
        min_star_detection = min_lum;
        box_size = box;
    }
    public void display_image_parameters(){
        this.param_res.setText(getString(R.string.settings_resolution)+image_resolution);
        this.param_focale.setText(getString(R.string.settings_focale)+focale_length);
        this.param_min_lum.setText(getString(R.string.settings_min_lum)+min_star_detection);
        this.param_box_size.setText(getString(R.string.settings_box_size)+box_size);
    }
    public void uploadImage (View view) {
        Intent intent = new Intent();
        intent.setType( "image/*" );
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), PICK_IMAGE);
    }
    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
    public static <T extends Comparable<T>> void multiSort(final List<T> key, List<?>... lists){
        // Create a List of indices
        List<Integer> indices = new ArrayList<Integer>();
        for(int i = 0; i < key.size(); i++) {
            indices.add(i);
        }

        // Sort the indices list based on the key
        Collections.sort(indices, new Comparator<Integer>() {
            @Override public int compare(Integer i, Integer j) {
                return key.get(i).compareTo(key.get(j));
            }
        });

        // Create a mapping that allows sorting of the List by N swaps.
        // Only swaps can be used since we do not know the type of the lists
        Map<Integer,Integer> swapMap = new HashMap<Integer, Integer>(indices.size());
        List<Integer> swapFrom = new ArrayList<Integer>(indices.size()),
                swapTo   = new ArrayList<Integer>(indices.size());
        for (int i = 0; i < key.size(); i++) {
            int k = indices.get(i);
            while (i != k && swapMap.containsKey(k)) {
                k = swapMap.get(k);
            }

            swapFrom.add(i);
            swapTo.add(k);
            swapMap.put(i, k);
        }

        // use the swap order to sort each list by swapping elements
        for (List<?> list : lists)
            for (int i = 0; i < list.size(); i++)
                Collections.swap(list, swapFrom.get(i), swapTo.get(i));
    }

    public void enterSettings (View view) {
        details_calcul.append("\n> Enter image settings.");
        Intent myIntent = new Intent(MainActivity.this, EnterSettings.class);
        MainActivity.this.startActivityForResult(myIntent, PICK_CONTACT_REQUEST);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT_REQUEST) {
            this.display_image_parameters();
        } else if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                Uri imageUri = data.getData();
                Bitmap sky_picture_raw = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                int width = sky_picture_raw.getWidth();
                int height = sky_picture_raw.getHeight();
                sky_picture = Bitmap.createScaledBitmap(sky_picture_raw, 1000, 1000*height/width, false);

                picture_stars.setImageBitmap( sky_picture );
                sky_picture_loaded = true;
                details_calcul.append("\n> Image uploaded.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void findPosition (View view) {
        if (sky_picture_loaded) {
            Bitmap sky_picture_gray = toGrayscale(sky_picture);
            int width = sky_picture.getWidth();
            int height = sky_picture.getHeight();

            int[][] sky_picture_pixels = new int[height][width];
            int[][] sky_picture_pixels_detection = new int[height][width];
            int[] sky_picture_pixels_detection_1D = new int[height*width];
            Bitmap picture_stars_detected = sky_picture_gray.copy(Bitmap.Config.RGB_565,true);
            details_calcul.append("\n> Image copied.");

            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    int pixel = sky_picture.getPixel(col, row);
                    sky_picture_pixels[row][col] = Color.red(pixel);
                    sky_picture_pixels_detection[row][col] = Color.green(pixel);
                }
            }

            details_calcul.append("\n> Waiting for stars detection.");
            int n_stars_found = 0;
            List<Integer> stars_coord_x = new ArrayList<Integer>();
            List<Integer> stars_coord_y = new ArrayList<Integer>();
            List<Integer> stars_lum = new ArrayList<Integer>();
            for (int row = 0; row < height; row++) {
                int row2_min = (row - box_size > 0) ? row - box_size : 0;
                int row2_max = (row + box_size < height-1) ? row + box_size : height-1;
                for (int col = 0; col < width; col++) {
                    int col2_min = (col - box_size > 0) ? col - box_size : 0;
                    int col2_max = (col + box_size < width-1) ? col + box_size : width-1;
                    int max_luminance_zone = 0;
                    int row_max = row;
                    int col_max = col;

                    for (int row2 = row2_min; row2 < row2_max; row2++) {
                        for (int col2 = col2_min; col2 < col2_max; col2++) {
                            if (sky_picture_pixels[row2][col2] > max_luminance_zone) {
                                row_max = row2;
                                col_max = col2;
                                max_luminance_zone = sky_picture_pixels[row2][col2];
                            }
                        }
                    }
                    if (col==col_max && row==row_max && max_luminance_zone>this.min_star_detection){
                        ++n_stars_found;
                        stars_coord_x.add(row);
                        stars_coord_y.add(col);
                        stars_lum.add(max_luminance_zone);

                        for (int row2 = row2_min; row2 < row2_max; row2++) {
                            sky_picture_pixels_detection[row2][col2_min] = 130;
                            sky_picture_pixels_detection[row2][col2_max - 1] = 130;
                        }
                        for (int col2 = col2_min; col2 < col2_max; col2++) {
                            sky_picture_pixels_detection[row2_min][col2] = 130;
                            sky_picture_pixels_detection[row2_max][col2] = 130;
                        }
                    }
                }
            }
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    int pixel = sky_picture_pixels_detection[row][col];
                    sky_picture_pixels_detection_1D[row*width+col] = Color.rgb(pixel,pixel,pixel);
                }
            }
            Bitmap sky_picture_stars_detected = Bitmap.createBitmap(sky_picture_pixels_detection_1D, 0,width,width,height,Bitmap.Config.RGB_565);

            details_calcul.append("\nN stars found = "+n_stars_found);
            if (n_stars_found>0) {
                multiSort(stars_lum,stars_coord_x,stars_coord_y,stars_lum);
                for (int i=n_stars_found-1; i>n_stars_found-20; i--){
                    details_calcul.append("\n"+stars_coord_x.get(i)+" "+stars_coord_y.get(i)+" "+stars_lum.get(i));
                }
            }

            picture_stars.setImageBitmap( sky_picture_stars_detected );
            //picture_stars.setImageBitmap( sky_picture_gray );

            details_calcul.append("\n> Position found.");
        }
    }


}
