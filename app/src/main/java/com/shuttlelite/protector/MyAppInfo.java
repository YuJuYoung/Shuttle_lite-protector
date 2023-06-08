package com.shuttlelite.protector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MyAppInfo {

    private static final String DIRECTORY_PATH = "/data/data/com.shuttlelite.protector/files";
    private static final String FILE_NAME = "occupant_number.json";

    private static MyAppInfo myAppInfo = new MyAppInfo();

    private String occupantNumber = null;
    private String driverPhoneNumber = null;

    private MyAppInfo() {

    }

    public static MyAppInfo getInstance() {
        return myAppInfo;
    }

    public String getOccupantNumber() {
        return occupantNumber;
    }

    public String getDriverPhoneNumber() {
        return driverPhoneNumber;
    }

    public void setDriverPhoneNumber(String phoneNumber) {
        driverPhoneNumber = phoneNumber;
    };

    public boolean setOccupantNumber() {
        File file = new File(DIRECTORY_PATH, FILE_NAME);

        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                StringBuilder sb = new StringBuilder();

                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    sb.append(line);
                }
                JSONObject json = new JSONObject(sb.toString());
                occupantNumber = json.getString("occupantNumber");

                br.close();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean writeOccupantNumber(String number) {
        File file = new File(DIRECTORY_PATH, FILE_NAME);

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            JSONObject json = new JSONObject();
            json.put("occupantNumber", number);

            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(json.toString());
            bw.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

}
