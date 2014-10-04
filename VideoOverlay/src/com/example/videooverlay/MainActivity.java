package com.example.videooverlay;

import java.io.IOException;

import android.app.Activity;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class MainActivity extends Activity implements OnPreparedListener, OnErrorListener, OnCompletionListener, Callback {
	//private VideoView mVideoView;
	private SurfaceView mSurfaceView;
	private boolean pausing = false;
	private Uri targetUri;
	private Uri mMediaUri;
	private MediaPlayer mPlayer;
	private SurfaceHolder mHolder;
	private boolean hasActiveHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/*mVideoView = (VideoView)findViewById(R.id.videoView);
	    mVideoView.setOnPreparedListener(this);
	    mVideoView.setDrawingCacheEnabled(true);
	    mVideoView.setOnErrorListener(this);*/

		mSurfaceView = (SurfaceView)findViewById(R.id.surfaceViewFrame);
		mPlayer = new MediaPlayer();

		mHolder = mSurfaceView.getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mPlayer = new MediaPlayer();
		//mPlayer.setDisplay(mHolder);

		mPlayer.setOnPreparedListener(this);
		mPlayer.setOnCompletionListener(this);
		mPlayer.setScreenOnWhilePlaying(true);

		mMediaUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
		//playVideo();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(mPlayer.isPlaying()) {
			mPlayer.stop();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		pausing = true;
		if(mPlayer.isPlaying()) {
			mPlayer.stop();
		}
		//mVideoView.stopPlayback();
	}

	private void playVideo() {
		pausing = false;
		try {
			mPlayer.setDataSource(getApplicationContext(),targetUri);
			mPlayer.prepare();
			//mVideoView.setVideoURI(targetUri);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 

		mPlayer.start();
		//mVideoView.start();
	}

	@Override
	public boolean onError(MediaPlayer player, int arg1, int arg2) {
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer player) {
		int videoWidth = player.getVideoWidth();
		int videoHeight = player.getVideoHeight();
		float videoProportion = (float) videoWidth / (float) videoHeight;       
		int screenWidth = getResources().getDisplayMetrics().widthPixels;
		int screenHeight =  getResources().getDisplayMetrics().heightPixels;
		float screenProportion = (float) screenWidth / (float) screenHeight;
		android.view.ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();

		if (videoProportion < screenProportion) {
			lp.width = screenWidth;
			lp.height = (int) ((float) screenWidth / videoProportion);
		} else {
			lp.width = (int) (videoProportion * (float) screenHeight);
			lp.height = screenHeight;
		}
		mSurfaceView.setLayoutParams(lp);

		if (!player.isPlaying()) {
			player.start();         
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		final Surface surface = holder.getSurface();

	    if ( surface == null ) return;

	    final boolean invalidSurfaceAccepted = true;
	    final boolean invalidSurface = ! surface.isValid();

	    if ( invalidSurface && ( ! invalidSurfaceAccepted ) ) return;
	    mPlayer.setDisplay(holder);
		Cursor cursor = getContentResolver().query(
				mMediaUri,
				null,
				null,
				null,
				MediaStore.Audio.Media.TITLE);

		if(cursor != null) {
			cursor.moveToFirst();
			cursor.moveToPosition(0);
			String _id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
			targetUri = Uri.withAppendedPath(mMediaUri, _id);
		}

		targetUri = Uri.parse("android.resource://" + getPackageName() + "/" 
				+ R.raw.mary_did_you_know_orig);
		playVideo();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if(mPlayer.isPlaying()) {
			mPlayer.stop();
		}
	}
}
