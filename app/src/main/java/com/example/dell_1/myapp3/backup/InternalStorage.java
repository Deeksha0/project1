package com.example.dell_1.myapp3.backup;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell_1.myapp3.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class InternalStorage extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {
    MyRecyclerViewAdapter adapter;
    MenuItem mSort, mSettings, mRename, mSelectAll, mProperties,mCreate;
    private ArrayList<String> myList, myList2,selected;
    String path = "//storage//sdcard0/";//Environment.getExternalStorageDirectory().getAbsolutePath();
    public static boolean selectallflag = false;
    public static boolean cutbuttonclicked = false;
    RecyclerView recyclerView;
    long result ;
    String string1,string3,x;
    AlertDialog.Builder alert;
    EditText etRenameFile;
    int fileIndex = 0;
    File destination1,f1,file2,dir;
    private static final String TAG = "com.example.dell_1.myapp3.InternalMemory";
    File f = new File(path);//converted string object to file//getting the list of files in string array

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_storage);

        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        topToolBar.setTitle("");
        topToolBar.setSubtitle("");
        topToolBar.setLogoDescription(getResources().getString(R.string.logo_desc));

        method1(f);
        method2(f);

        // set up the RecyclerView
        setAdapter();

        alert = new AlertDialog.Builder(this);
        etRenameFile = new EditText(getApplicationContext());
        alert.setTitle("Do you want to rename the file?");
        alert.setMessage(" ");
        alert.setView(etRenameFile);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int whichButton) {
                renameFileAlert();
                adapter.notifyDataSetChanged();
            }
        });

        alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            // what ever you want to do with No option.
            }
        });

        final ImageButton button3 = (ImageButton) findViewById(R.id.button3);
        final ImageButton button4 = (ImageButton) findViewById(R.id.button4);
        final ImageButton buttoncut = (ImageButton) findViewById(R.id.button1);
        final ImageButton button2 = (ImageButton) findViewById(R.id.button2);
        final ImageButton buttonpaste = (ImageButton) findViewById(R.id.buttonpaste);
        buttonpaste.setVisibility(View.GONE);

        final Bundle exe = getIntent().getExtras();
        if(exe != null){
            f1= new File(exe.getString("DIR_PATH"));
            Log.v(TAG, f1.toString());
            if(f1.equals(new File("/storage/emulated"))){
                Intent intent = new Intent(this,Bacon1.class);
                startActivity(intent);
            }
            method2(f1);
            method1(f1);
            if(cutbuttonclicked){
                button2.setVisibility(View.GONE);
                button3.setVisibility(View.GONE);
                button4.setVisibility(View.GONE);
                buttoncut.setVisibility(View.GONE);
                buttonpaste.setVisibility(View.VISIBLE);
            }
            setAdapter();
        }

        buttonpaste.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View view){
                        moveItem();
                    }
                }
        );

        button3.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(InternalStorage.this);
                        builder1.setMessage("Are you sure you want to delete it ?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        adapter.deleteItem();
                                    }
                                });

                        builder1.setNegativeButton(
                                "No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }
                });

        button4.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        adapter.shareItem(view);
                    }
                }
        );
    }

    public void OnClick(View view){
        cutbuttonclicked= true;
        final ImageButton button3 = (ImageButton) findViewById(R.id.button3);
        final ImageButton button4 = (ImageButton) findViewById(R.id.button4);
        final ImageButton buttoncut = (ImageButton) findViewById(R.id.button1);
        final ImageButton button2 = (ImageButton) findViewById(R.id.button2);
        final ImageButton buttonpaste = (ImageButton) findViewById(R.id.buttonpaste);
        button3.setVisibility(View.GONE);
        button4.setVisibility(View.GONE);
        buttoncut.setVisibility(View.GONE);
        button2.setVisibility(View.GONE);
        buttonpaste.setVisibility(View.VISIBLE);
    }


    @Override
    public void onItemClick(View view, int position) {
        string1 = adapter.getItem(position);
        File directory = new File(string1);
        if (directory.isDirectory()) {
            method1(directory);
            method2(directory);
            setAdapter();
        } else if (string1.endsWith(".mp3")) {
            Intent viewIntent1 = new Intent(Intent.ACTION_VIEW);
            viewIntent1.setDataAndType(Uri.fromFile(directory), "audio/mpeg");
            startActivity(Intent.createChooser(viewIntent1, null));
        } else if (string1.endsWith(".zip")) {
            Intent viewIntent1 = new Intent(Intent.ACTION_VIEW);
            viewIntent1.setDataAndType(Uri.fromFile(directory), "application/zip");
            startActivity(Intent.createChooser(viewIntent1, null));
        } else if (string1.endsWith(".mp4")) {
            Intent viewIntent1 = new Intent(Intent.ACTION_VIEW);
            viewIntent1.setDataAndType(Uri.fromFile(directory), "video/mp4");
            startActivity(Intent.createChooser(viewIntent1, null));
        } else if (string1.endsWith(".jpeg")) {
            Intent viewIntent1 = new Intent(Intent.ACTION_VIEW);
            viewIntent1.setDataAndType(Uri.fromFile(directory), "image/*");
            startActivity(Intent.createChooser(viewIntent1, null));
        } else if (string1.endsWith(".png")) {
            Intent viewIntent1 = new Intent(Intent.ACTION_VIEW);
            viewIntent1.setDataAndType(Uri.fromFile(directory), "image/*");
            startActivity(Intent.createChooser(viewIntent1, null));
        } else if (string1.endsWith(".pdf")) {
            Intent viewIntent1 = new Intent(Intent.ACTION_VIEW);
            viewIntent1.setDataAndType(Uri.fromFile(directory), "application/pdf");
            startActivity(Intent.createChooser(viewIntent1, null));
        } else if (string1.endsWith(".apk")) {
            Intent viewIntent1 = new Intent(Intent.ACTION_VIEW);
            viewIntent1.setDataAndType(Uri.fromFile(directory), "application/vnd.android.package-archive");
            startActivity(Intent.createChooser(viewIntent1, null));
        } else if (string1.endsWith(".txt")) {
            Intent viewIntent1 = new Intent(Intent.ACTION_VIEW);
            viewIntent1.setDataAndType(Uri.fromFile(directory), "text/*");
            startActivity(Intent.createChooser(viewIntent1, null));
        } else Toast.makeText(this, "unsupported format", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public boolean onLongClick(View view, int position) {
        fileIndex =position;
        hideMenuItem();
        return true;
    }

    public ArrayList<String> method1(File f) {
        File list2[] = f.listFiles();
        myList = new ArrayList<>();
        if (list2 != null) {
            for (int i = 0; i < list2.length; i++) {
                myList.add(list2[i].getName());
            }
        } else Toast.makeText(this, "the folder is empty", Toast.LENGTH_SHORT)
                .show();
        return myList;
    }

    public ArrayList<String> method2(File f) {
        File list2[] = f.listFiles();
        myList2 = new ArrayList<>();
        if (list2 != null) {
            for (int i = 0; i < list2.length; i++) {
                myList2.add(list2[i].getPath());
            }
        } else Toast.makeText(this, "the folder is empty", Toast.LENGTH_SHORT)
                .show();
        return myList2;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionmenu, menu);
        mSort = menu.findItem(R.id.action_sort);
        mSettings = menu.findItem(R.id.action_settings);
        mCreate = menu.findItem(R.id.action_newFolder);
        mRename = menu.findItem(R.id.action_rename);
        mRename.setVisible(false);
        mSelectAll = menu.findItem(R.id.action_selectAll);
        mSelectAll.setVisible(false);
        mProperties = menu.findItem(R.id.action_properties);
        mProperties.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_settings:
                // search action
                return true;

            case R.id.action_newFolder:
                if(string1==null) {
                    dir = new File(Environment.getExternalStorageDirectory(), "Folder");
                    try {
                        if (dir.mkdir()) {
                            Log.v(TAG, "Directory is created");
                        } else {
                            Log.v(TAG, "Directory is not created");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else dir = new File(string1,"Folder");
                try {
                    if (dir.mkdir()) {
                        Log.v(TAG, "Directory is created");
                    } else {
                        Log.v(TAG, "Directory is not created");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                notifyMediaStoreScanner(dir);
                adapter.notifyDataSetChanged();
                // search action
                return true;

            case R.id.action_sort:
                showRadioButtonDialog();
                // location found
                return true;

            case R.id.action_rename:
                etRenameFile.setText(myList.get(fileIndex));
                etRenameFile.setTextColor(getResources().getColor(R.color.colorBlack));
                etRenameFile.selectAll();
                alert.show();
                Log.i("ZAA", "Image Gallery action rename");
                // location found
                return true;

            case R.id.action_selectAll:
                adapter.selectAll();
                adapter.notifyDataSetChanged();
                selectallflag = true;
                // location found
                Log.i("ZAA", "Image Gallery action action_selectAll");
                return true;

            case R.id.action_properties:
               properties();
                // location found
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void hideMenuItem(){
        mSort.setVisible(false);
        mSettings.setVisible(false);
        mRename.setVisible(true);
        mSelectAll.setVisible(true);
        mProperties.setVisible(true);
        selected = adapter.getList();
        Log.v(TAG, Integer.toString(selected.size())+ " is the size");
        if(selected.size()>0){
            mProperties.setVisible(false);
            mRename.setVisible(false);
        }
    }

    private void properties(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setMessage("Properties");
        builder.setView(R.layout.properties);
        View dialogView = inflater.inflate(R.layout.properties, null);
        builder.setView(dialogView);
        TextView displayname = (TextView) dialogView.findViewById(R.id.displayname);
        TextView displaysize = (TextView) dialogView.findViewById(R.id.displaysize);
        TextView displaylastmodified = (TextView) dialogView.findViewById(R.id.displaylastmodified);
        TextView displaydatetaken = (TextView) dialogView.findViewById(R.id.displaydatetaken);
        TextView displaypath  = (TextView) dialogView.findViewById(R.id.displaypath);
        for(int i=0;i<selected.size();i++){
            Log.v(TAG, Integer.toString(selected.size())+ " is the final size");
            if(selected.size()>0) {
                Log.v(TAG, Integer.toString(selected.size()));
                File file = new File(myList2.get(Integer.parseInt(selected.get(i))));
                String strFileName = file.getName();
                displayname.setText(strFileName);
                Date lastModified = new Date(file.lastModified());
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String formattedDateString = formatter.format(lastModified);
                displaylastmodified.setText(formattedDateString);
                displaydatetaken.setText(formattedDateString);
                String path = myList2.get(Integer.parseInt(selected.get(i)));
                displaypath.setText(path);
                if (file.isFile()) {
                    float fileSizeInBytes = file.length();
                    String calString = Float.toString(fileSizeInBytes);
                    displaysize.setText(calString + " bytes");
                    Log.v(TAG, Long.toString(file.length()));
                    if (fileSizeInBytes > 1024) {
                        float fileSizeInKB = fileSizeInBytes / 1024;
                        String calString2 = Float.toString(fileSizeInKB);
                        displaysize.setText(calString2 + " KB");
                        if (fileSizeInKB > 1024) {
                            float fileSizeInMB = fileSizeInKB / 1024;
                            String calString3 = Float.toString(fileSizeInMB);
                            displaysize.setText(calString3 + " MB");
                        }
                    }
                }
                else if(file.isDirectory()){
                    result = 0;
                    final List<File> dirs = new LinkedList<>();
                    dirs.add(file);
                    while (!dirs.isEmpty()) {
                        final File dir = dirs.remove(0);
                        if (!dir.exists())
                            continue;
                        final File[] listFiles = dir.listFiles();
                        if (listFiles == null || listFiles.length == 0)
                            continue;
                        for (final File child : listFiles) {
                            result += child.length();
                            if (child.isDirectory())
                                dirs.add(child);
                        }
                    }
                    float fileSizeInBytes = result;
                    String calString = Float.toString(fileSizeInBytes);
                    displaysize.setText(calString + " bytes");
                    if (fileSizeInBytes > 1024) {
                        float fileSizeInKB = fileSizeInBytes / 1024;
                        String calString2 = Float.toString(fileSizeInKB);
                        displaysize.setText(calString2 + " KB");
                        if (fileSizeInKB > 1024) {
                            float fileSizeInMB = fileSizeInKB / 1024;
                            String calString3 = Float.toString(fileSizeInMB);
                            displaysize.setText(calString3 + " MB");
                        }
                    }
                }
            }
        }
        AlertDialog alert12 = builder.create();
        alert12.show();
    }

    @Override
    public void onBackPressed() {
        if(string1 == null){
            Intent intent = new Intent(this, Bacon1.class);
            startActivity(intent);
            Log.v(TAG,"gets done");
        }
        else if(f1 == null) {
            selectallflag = false;
            file2 = new File(string1).getParentFile();
            string3 = file2.getAbsolutePath();
            Toast.makeText(this, string1+"11111", Toast.LENGTH_SHORT)
                    .show();
        }
        else {
            file2 = f1.getParentFile();
            string3 = file2.getAbsolutePath();
            Toast.makeText(this, string3+"22222", Toast.LENGTH_SHORT)
                    .show();
        }

        string3 = file2.getAbsolutePath();

        String previousDir = string3;
        if (previousDir != null) { //if deferent root path
            Intent activityDir = new Intent(this, InternalStorage.class);
            activityDir.putExtra("DIR_PATH", previousDir);
            startActivity(activityDir);
            Log.v(TAG, "great");
        } else Log.v(TAG, "Fuck off");

        finish();
    }


    private void setAdapter(){
        recyclerView = (RecyclerView) findViewById(R.id.rvNumbers);
        int numberOfColumns = 4;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        adapter = new MyRecyclerViewAdapter(this, myList, myList2);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void moveItem(){
        selected= adapter.getList();
        Log.v(TAG, Integer.toString(selected.size()));
        for (int i = 0; i < selected.size()+1; i++) {
                File source1 = new File(myList2.get(Integer.parseInt(selected.get(i))));
                destination1 = new File(string1 + File.separator + source1.getName());
                try {
                    moveFile(source1, destination1, false);
                    notifyMediaStoreScanner(destination1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private void moveFile(File file_Source, File file_Destination, boolean isCopy) throws IOException {
        FileChannel source = null;
        FileChannel destination = null;
        if (!file_Destination.exists()) {
            file_Destination.createNewFile();
        }

        try {
            source = new FileInputStream(file_Source).getChannel();
            destination = new FileOutputStream(file_Destination).getChannel();

            long count = 0;
            long size = source.size();
            while ((count += destination.transferFrom(source, count, size - count)) < size) ;
            if (!isCopy) {
                file_Source.delete();
                MediaScannerConnection.scanFile(this,
                        new String[]{file_Source.toString()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                Log.i("ExternalStorage", "-> uri=" + uri);
                            }
                        });
            }
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    public final void notifyMediaStoreScanner(File file) {
//        try {
//            MediaStore.Images.Media.insertImage(getBaseContext().getContentResolver(),
//                    file.getAbsolutePath(), file.getName(), null);
        getBaseContext().sendBroadcast(new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        getBaseContext().sendBroadcast(new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    private void renameFileAlert(){

        String renameFile = etRenameFile.getText().toString();
        String filename= myList.get(fileIndex);

        File oldFilePath = new File(myList2.get(fileIndex));
// Log.d("OLDFILEPATH", oldFilePath.toString());

        x = myList2.get(fileIndex);
        File renamedFile = new File(x.replace(filename, renameFile));
// Log.d("NEWFILE", renamedFile.toString());

        oldFilePath.renameTo(renamedFile);
        notifyMediaStoreScanner(renamedFile);

    }

    private void showRadioButtonDialog() {

        final CharSequence[] items = {" Name "," Date taken"," Size "," last modified "};
        final CharSequence[] items2 = {" Only for this folder"};
        final ArrayList seletedItems=new ArrayList();
        // custom dialog
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(InternalStorage.this);
        builder2.setTitle("SORT BY");

        builder2.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch(item)
                {
                    case 0:
                        // Your code when first option seletced
                        break;
                    case 1:
                        // Your code when 2nd  option seletced

                        break;
                    case 2:
                        // Your code when 3rd option seletced
                        break;
                    case 3:
                        // Your code when 4th  option seletced
                        break;

                }
                dialog.dismiss();
            }

        });


        AlertDialog alert12 = builder2.create();
        alert12.show();

    }


}