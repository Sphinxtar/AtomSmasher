package com.atomsmasher;

import android.app.Activity;
import android.os.Bundle;
// import android.os.StrictMode;

public class MainActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//	StrictMode.enableDefaults();
		super.onCreate(savedInstanceState);
		setContentView(new AtomView(this));
    }
}
