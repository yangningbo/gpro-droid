package lib.app.music_player;

import android.lib.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MusicPlayerActivity extends Activity {
	private MusicPlayerService mMusicPlayerService = null;
	private MusicInfoController mMusicInfoController = null;
	private Cursor mCursor = null;

	private TextView mTextView = null;
	private Button mPlayPauseButton = null;
	private Button mStopButton = null;

	private ListView mListView = null;

	private ServiceConnection mPlaybackConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mMusicPlayerService = ((MusicPlayerService.LocalBinder) service)
					.getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			mMusicPlayerService = null;
		}
	};

	protected BroadcastReceiver mPlayerEvtReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(MusicPlayerService.PLAYER_PREPARE_END)) {
				// will begin to play
				mTextView.setVisibility(View.INVISIBLE);
				mPlayPauseButton.setVisibility(View.VISIBLE);
				mStopButton.setVisibility(View.VISIBLE);

				mPlayPauseButton.setText(R.string.pause);
			} else if (action.equals(MusicPlayerService.PLAY_COMPLETED)) {
				mPlayPauseButton.setText(R.string.play);
			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_player_list_layout);
		/*
		Intent scanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		this.sendBroadcast(scanIntent);*/
		
		mListView = (ListView) findViewById(R.id.listView);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mCursor == null || mCursor.getCount() == 0) {
					return;
				}
				mCursor.moveToPosition(position);
				String url = mCursor.getString(mCursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
				mMusicPlayerService.setDataSource(url);
				mMusicPlayerService.start();
			}

		});

		MusicPlayerApp musicPlayerApp = (MusicPlayerApp) getApplication();
		mMusicInfoController = musicPlayerApp.getMusicInfoController();

		// bind playback service
		Intent intent = new Intent(this, MusicPlayerService.class);
		startService(intent);
		bindService(intent, mPlaybackConnection, Context.BIND_AUTO_CREATE);

		mTextView = (TextView) findViewById(R.id.show_text);
		mPlayPauseButton = (Button) findViewById(R.id.play_pause_btn);
		mStopButton = (Button) findViewById(R.id.stop_btn);

		mPlayPauseButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				if (mMusicPlayerService != null
						&& mMusicPlayerService.isPlaying()) {
					mMusicPlayerService.pause();
					mPlayPauseButton.setText(R.string.play);
				} else if (mMusicPlayerService != null) {
					mMusicPlayerService.start();
					mPlayPauseButton.setText(R.string.pause);
				}
			}
		});

		mStopButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				if (mMusicPlayerService != null) {
					mTextView.setVisibility(View.VISIBLE);
					mPlayPauseButton.setVisibility(View.INVISIBLE);
					mStopButton.setVisibility(View.INVISIBLE);
					mMusicPlayerService.stop();
				}
			}
		});

		IntentFilter filter = new IntentFilter();
		filter.addAction(MusicPlayerService.PLAYER_PREPARE_END);
		filter.addAction(MusicPlayerService.PLAY_COMPLETED);
		registerReceiver(mPlayerEvtReceiver, filter);
	}

	protected void onResume() {
		super.onResume();
		mCursor = mMusicInfoController.getAllSongs();

		ListAdapter adapter = new MusicListAdapter(this,
				android.R.layout.simple_expandable_list_item_2, mCursor,
				new String[] {}, new int[] {});
		mListView.setAdapter(adapter);
	}
	
}

/**********************************
 * 
 *********************************/
class MusicListAdapter extends SimpleCursorAdapter {

	public MusicListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
	}

	public void bindView(View view, Context context, Cursor cursor) {

		super.bindView(view, context, cursor);

		TextView titleView = (TextView) view.findViewById(android.R.id.text1);
		TextView artistView = (TextView) view.findViewById(android.R.id.text2);
		int titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
		if (titleIndex != -1) {
			titleView.setText(cursor.getString(titleIndex));
		}

		int artistIndex = cursor
				.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
		if (artistIndex != -1) {
			artistView.setText(cursor.getString(artistIndex));
		}

		// int duration =
		// cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
	}

	public static String makeTimeString(long milliSecs) {
		StringBuffer sb = new StringBuffer();
		long m = milliSecs / (60 * 1000);
		sb.append(m < 10 ? "0" + m : m);
		sb.append(":");
		long s = (milliSecs % (60 * 1000)) / 1000;
		sb.append(s < 10 ? "0" + s : s);
		return sb.toString();
	}
}
