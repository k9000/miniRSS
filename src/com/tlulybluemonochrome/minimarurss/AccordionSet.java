package com.tlulybluemonochrome.minimarurss;

import jp.sharakova.android.urlimageview.UrlImageView;
import jp.sharakova.android.urlimageview.UrlImageView.OnImageLoadListener;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class AccordionSet {

	private ImageButton _btn;
	private LinearLayout _content;
	private Handler _handler;
	private float _height;
	private float current = 0.0f;
	private float rotation = -90.0f;
	private Thread _thread;
	private String _bound = "close";
	private int _startTime;
	private DecelerateInterpolator mInterpolator = new DecelerateInterpolator();
	private int easeTime = 400;

	public AccordionSet(ImageButton btn, final LinearLayout content, final UrlImageView mImageView, final String uri, float height) {
		_btn = btn;
		_content = content;
		_handler = new Handler();
		_height =  height;
		mInterpolator = new DecelerateInterpolator();
		//_content.setLayoutParams(new LinearLayout.LayoutParams(
				//LayoutParams.MATCH_PARENT, 0));
		_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_startTime = (int) System.currentTimeMillis();
				if (_bound.equals("open")) {
					_bound = "close";
					//content.setVisibility(View.GONE);
				} else {
					content.setVisibility(View.VISIBLE);
					if(uri!=null){
						mImageView.setImageUrl(uri,
								imageLoadListener);
					}
					_bound = "open";
				}
				if (_thread == null || !_thread.isAlive()) {
					_thread = null;
					makeThread();
					_thread.start();
				}

			}
		});

	}

	private void makeThread() {
		_thread = new Thread(new Runnable() {
			public void run() {
				while (easeTime > (int) System.currentTimeMillis() - _startTime) {
					int diff = (int) System.currentTimeMillis() - _startTime;
					if (_bound.equals("open")) {
						current = _height
								* mInterpolator.getInterpolation((float) diff
										/ (float) easeTime);
						rotation = 90 * mInterpolator.getInterpolation((float) diff
								/ (float) easeTime) - 90;
					} else {
						current = _height
								- _height
								* mInterpolator.getInterpolation((float) diff
										/ (float) easeTime);
						rotation = 0 - 90 * mInterpolator.getInterpolation((float) diff
								/ (float) easeTime);
					}
					threadFunc();
				}
			}
		});
	}

	private void threadFunc() {
		_handler.post(new Runnable() {
			

			public void run() {
				_content.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, (int) current));
				_btn.setRotation(rotation);
			}
		});
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
		}
	}

	public void deleteAccordion() {
		_btn.setOnClickListener(null);
		_btn = null;
		_content = null;
	}
	
	final private OnImageLoadListener imageLoadListener = new OnImageLoadListener() {
		public void onStart(String url) {
		}

		public void onComplete(String url) {
		}
	};
}
