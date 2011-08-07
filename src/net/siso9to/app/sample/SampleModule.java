/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package net.siso9to.app.sample;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollInvocation;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiContext;
import org.appcelerator.titanium.kroll.KrollCallback;
import org.appcelerator.titanium.util.Log;
import org.appcelerator.titanium.util.TiActivityResultHandler;
import org.appcelerator.titanium.util.TiActivitySupport;
import org.appcelerator.titanium.util.TiConfig;
import org.appcelerator.titanium.util.TiIntentWrapper;

import android.app.Activity;
import android.content.Intent;

@Kroll.module(name="Sample", id="net.siso9to.app.sample")
public class SampleModule extends KrollModule
{
	private static final String LCAT = "SampleModule";
	private static final boolean DBG = TiConfig.LOGD;

	protected static final int UNKNOWN_ERROR = 0;
	
	public SampleModule(TiContext tiContext) {
		super(tiContext);
	}

	// Methods
	@Kroll.method
	public String example() {
		Log.d(LCAT, "example called");
		return "hello world";
	}
	
	// Properties
	@Kroll.getProperty
	public String getExampleProp() {
		Log.d(LCAT, "get example property");
		return "hello world";
	}
	
	
	@Kroll.setProperty
	public void setExampleProp(String value) {
		Log.d(LCAT, "set example property: " + value);
	}

	@Kroll.method
	public void sample(KrollInvocation invocation, KrollDict options) {
		final KrollCallback successCallback = getCallback(options, "success");
		final KrollCallback cancelCallback = getCallback(options, "cancel");
		final KrollCallback errorCallback = getCallback(options, "error");
		
		final Activity activity = invocation.getTiContext().getActivity();
		final TiActivitySupport activitySupport = (TiActivitySupport) activity;

		final TiIntentWrapper sampleIntent = new TiIntentWrapper(new Intent(
				activity, SampleActivity.class));
		
		sampleIntent.setWindowId(TiIntentWrapper.createActivityName("SAMPLE"));

		SampleResultHandler resultHandler = new SampleResultHandler();
		resultHandler.successCallback = successCallback;
		resultHandler.cancelCallback = cancelCallback;
		resultHandler.errorCallback = errorCallback;
		resultHandler.activitySupport = activitySupport;
		resultHandler.sampleIntent = sampleIntent.getIntent();
		
		activity.runOnUiThread(resultHandler);
	}

	private KrollDict getDictForResult(final String result) {
		final KrollDict dict = new KrollDict();
		dict.put("sampleResult", result);
		return dict;
	}

	private KrollCallback getCallback(final KrollDict options, final String name) {
		if (options.containsKey(name)) {
			return (KrollCallback) options.get(name);
		} else {
			logError("Callback not found: " + name);
			return null;
		}
	}

	private void logError(final String msg) {
		Log.e(LCAT, msg);
	}

	private void logDebug(final String msg) {
		if (DBG) {
			Log.d(LCAT, msg);
		}
	}

	protected class SampleResultHandler implements TiActivityResultHandler,
			Runnable {

		protected int code;
		protected KrollCallback successCallback, cancelCallback, errorCallback;
		protected TiActivitySupport activitySupport;
		protected Intent sampleIntent;

		public void run() {
			code = activitySupport.getUniqueResultCode();
			activitySupport.launchActivityForResult(sampleIntent, code, this);
		}

		public void onError(Activity activity, int requestCode, Exception e) {
			String msg = "Problem with sample; " + e.getMessage();
			logError("error: " + msg);
			if (errorCallback != null) {
				errorCallback
						.callAsync(createErrorResponse(UNKNOWN_ERROR, msg));
			}
		}

		public void onResult(Activity activity, int requestCode,
				int resultCode, Intent data) {
			logDebug("onResult() called");

			if (resultCode == Activity.RESULT_CANCELED) {
				logDebug("process canceled");
				if (cancelCallback != null) {
					cancelCallback.callAsync();
				}
			} else {
				logDebug("process successful");
				String result = data
						.getStringExtra(SampleActivity.EXTRA_RESULT);
				logDebug("process result: " + result);
				successCallback.callAsync(getDictForResult(result));
			}
		}
	}

}
