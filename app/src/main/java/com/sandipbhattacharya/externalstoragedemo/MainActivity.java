package com.sandipbhattacharya.externalstoragedemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // Declare the View object references
    Button btnSave, btnLoad;
    EditText etInput;
    TextView tvLoad;
    // Define some String variables, initialized with empty string
    String filename = "";
    String filepath = "";
    String fileContent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get handles for the views
        btnSave = findViewById(R.id.btnSave);
        btnLoad = findViewById(R.id.btnLoad);
        etInput = findViewById(R.id.etInput);
        tvLoad = findViewById(R.id.tvLoad);
        // Initialize two String variables for storing filename and filepath
        filename = "myFile.txt";
        filepath = "MyFileDir";
        // Since external storage stays on the physical device that the user can remove, you need to
        // check if the external storage is available and is not read only before you try to read
        // from or write to external storage. If the condition is not true then make the
        // save button disabled.
        if(!isExternalStorageAvailableForRW()){
            btnSave.setEnabled(false);
        }
        // Attach OnClickListener with save button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear the TextView tvLoad
                tvLoad.setText("");
                // Get the input from EditText
                fileContent = etInput.getText().toString().trim();
                // Check for Storage Permission
                if(isStoragePermissionGranted()){
                    // If input is not empty, we'll proceed
                    if(!fileContent.equals("")){
                        // To access app-specific files from external storage, you can call
                        // getExternalFilesDir() method. It returns the path to
                        // storage > emulated > 0 > Android > data > [package_name] > files > MyFileDir
                        // or,
                        // storage > self > Android > data > [package_name] > files > MyFileDir
                        // directory on the SD card. Once the app is uninstalled files here also get
                        // deleted.
                        // Create a File object like this.
                        File myExternalFile = new File(getExternalFilesDir(filepath), filename);
                        // Create an object of FileOutputStream for writing data to myFile.txt
                        FileOutputStream fos = null;
                        try {
                            // Instantiate the FileOutputStream object and pass myExternalFile in constructor
                            fos = new FileOutputStream(myExternalFile);
                            // Write to the file
                            fos.write(fileContent.getBytes());
                            // Close the stream
                            fos.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // Clear the EditText
                        etInput.setText("");
                        // Show a Toast message to inform the user that the operation has been successfully completed.
                        Toast.makeText(MainActivity.this, "Information saved to SD card.", Toast.LENGTH_SHORT).show();
                    }else{
                        // If the Text field is empty show corresponding Toast message
                        Toast.makeText(MainActivity.this, "Text field can not be empty.", Toast.LENGTH_SHORT).show();
                    }
                }
                /*
                if(!fileContent.equals("")){
                    File myExternalFile = new File(getExternalFilesDir(filepath), filename);
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(myExternalFile);
                        fos.write(fileContent.getBytes());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    etInput.setText("");
                    Toast.makeText(MainActivity.this, "Information saved to SD card.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Text field can not be empty.", Toast.LENGTH_SHORT).show();
                }
                 */
            }
        });
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a FileReader object reference. FileReader is typically suitable for reading
                // streams of characters.
                // For reading streams of raw bytes, you can use a FileInputStream.
                FileReader fr = null;
                File myExternalFile = new File(getExternalFilesDir(filepath), filename);
                // Instantiate a StringBuilder object. This class is an alternative to String Class
                // and it is mutable, has methods such as append(), insert(), or replace() that allow to
                // modify strings. Hence it is more efficient.
                StringBuilder stringBuilder = new StringBuilder();
                try {
                    // Instantiate the FileReader object and pass myExternalFile in the constructor
                    fr = new FileReader(myExternalFile);
                    // Instantiate a BufferedReader object and pass FileReader object in constructor.
                    // The BufferedReader maintains an internal buffer and can be used with different
                    // types of readers to read text from an Input stream more efficiently.
                    BufferedReader br = new BufferedReader(fr);
                    // Next, call readLine() method on BufferedReader object to read a line of text.
                    String line = br.readLine();
                    // Use a while loop to read the entire file
                    while(line != null){
                        // Append the line read to StringBuilder object. Also, append a new-line
                        stringBuilder.append(line).append('\n');
                        // Again read the next line and store in variable line
                        line = br.readLine();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    // Convert the StringBuilder content into String and add text "File contents\n"
                    // at the beginning.
                    String fileContents = "File contents\n" + stringBuilder.toString();
                    // Set the TextView with fileContents
                    tvLoad.setText(fileContents);
                }
            }
        });
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                //Permission is granted
                return true;
            } else {
                //Permission is revoked
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            //permission is automatically granted on sdk<23 upon installation
            //Permission is granted
            return true;
        }
    }

    private boolean isExternalStorageAvailableForRW() {
        // Check if the external storage is available for read and write by calling
        // Environment.getExternalStorageState() method. If the returned state is MEDIA_MOUNTED,
        // then you can read and write files. So, return true in that case, otherwise, false.
        String extStorageState = Environment.getExternalStorageState();
        if(extStorageState.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        return false;
    }
}