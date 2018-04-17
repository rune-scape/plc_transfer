package com.porterlee.limstransfer.Scanner;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public abstract class Scanner {
    public static final int UNKNOWN_MODE = -1;
    public static final int CONTINUOUS_MODE = 1;
    public static final int ONE_SHOT_MODE = 2;
    private OnBarcodeScannedListener mOnBarcodeScannedListener;
    private int mScanMode;

    public abstract boolean init();
    protected abstract boolean onScanModeChanged(int scanMode);

    public void setOnBarcodeScannedListener(OnBarcodeScannedListener onBarcodeScannedListener) {
        mOnBarcodeScannedListener = onBarcodeScannedListener;
    }

    public OnBarcodeScannedListener getOnBarcodeScannedListener() {
        return mOnBarcodeScannedListener;
    }

    protected void onBarcodeScanned(String barcode) {
        if (mOnBarcodeScannedListener != null)
            mOnBarcodeScannedListener.onBarcodeScanned(barcode);
    }

    public boolean setScanMode(int scanMode) {
        return onScanModeChanged(mScanMode = scanMode);
    }

    public int getScanMode() {
        return mScanMode;
    }

    public void onStart(Context context) { }
    public void onResume(Context context) { }
    public void onPause(Context context) { }
    public void onDestroy(Context context) { }

    public interface OnBarcodeScannedListener {
        void onBarcodeScanned(String barcode);
    }
}