package com.cg.watbalance.preferences;

import android.content.Context;
import android.util.Log;

import com.cg.watbalance.data.WatCardData;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class FileManager {
    FileOutputStream fos;
    FileInputStream fis;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    Context myContext;

    public FileManager(Context newContext) {
        myContext = newContext;
    }

    public void openFileOutput(String fileName) {
        try {
            fos = new FileOutputStream(myContext.getFilesDir() + "/" + fileName);
            oos = new ObjectOutputStream(fos);
            Log.d("FILEMANAGER", "OPEN WRITE : " + myContext.getFilesDir() + "/" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openFileInput(String fileName) {
        try {
            fis = new FileInputStream(myContext.getFilesDir() + "/" + fileName);
            ois = new ObjectInputStream(fis);
            Log.d("FILEMANAGER", "OPEN READ : " + myContext.getFilesDir() + "/" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeData(WatCardData myData) {
        try {
            oos.writeObject(myData);
            Log.d("FILEMANAGER", "WRITE : " + myData.getFirstName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WatCardData readData() {
        WatCardData myData = null;
        try {
            myData = (WatCardData) ois.readObject();
            Log.d("FILEMANAGER", "READ : " + myData.getFirstName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myData;
    }

    public void closeFileOutput() {
        try {
            oos.close();
            fos.close();
            Log.d("FILEMANAGER", "CLOSE WRITE");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeFileInput() {
        try {
            ois.close();
            fis.close();
            Log.d("FILEMANAGER", "CLOSE READ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
