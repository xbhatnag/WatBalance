package com.cg.watbalance.preferences;

import android.content.Context;
import android.util.Log;

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
            Log.d("FILE", "WRITE : " + myContext.getFilesDir() + "/" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openFileInput(String fileName) {
        try {
            fis = new FileInputStream(myContext.getFilesDir() + "/" + fileName);
            ois = new ObjectInputStream(fis);
            Log.d("FILE", "READ : " + myContext.getFilesDir() + "/" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeData(Object myData) {
        try {
            oos.writeObject(myData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object readData() {
        Object myData = null;
        try {
            myData = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myData;
    }

    public void closeFileOutput() {
        try {
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeFileInput() {
        try {
            ois.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
