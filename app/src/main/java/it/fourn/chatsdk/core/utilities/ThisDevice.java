package it.fourn.chatsdk.core.utilities;

import android.os.Build;

public class ThisDevice {
    private String mVersion;
    private String mBuildNumber;
    private String mModel;
    private String mManufacturer;

    public ThisDevice(String version, String buildNumber, String model, String manufacturer) {
        mVersion = version;
        mBuildNumber = buildNumber;
        mModel = model;
        mManufacturer = manufacturer;
    }

    public static ThisDevice thisDevice() {
        return new ThisDevice(
                Build.VERSION.RELEASE,
                Build.ID,
                Build.MODEL,
                Build.MANUFACTURER
        );
    }

    @Override
    public String toString() {
        return "ThisDevice{" +
                "mVersion='" + mVersion + '\'' +
                ", mBuildNumber='" + mBuildNumber + '\'' +
                ", mModel='" + mModel + '\'' +
                ", mManufacturer='" + mManufacturer + '\'' +
                '}';
    }

    public String getVersion() {
        return mVersion;
    }

    public String getBuildNumber() {
        return mBuildNumber;
    }

    public String getModel() {
        return mModel;
    }

    public String getManufacturer() {
        return mManufacturer;
    }
}
