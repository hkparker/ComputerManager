package com.hkparker.bootmycomputer;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.text.SimpleDateFormat;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.glass.app.Card;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.timeline.TimelineManager;
import java.io.IOException;

public class ShutdownActivity extends Activity{
	public String ssh_command = "touch shutdown";
	
	public boolean send_command() throws IOException {
		AsyncTask<String, Void, Boolean> ssh_exec = new BackgroundSSHExec().execute(this.ssh_command);
		Boolean success = false;
		try {
			success = (Boolean) ssh_exec.get();
		} catch (InterruptedException e) { e.printStackTrace();
		} catch (ExecutionException e) { e.printStackTrace(); }
		return success;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TimelineManager timeline = TimelineManager.from(this);
		AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat date_format = new SimpleDateFormat("KK:mm a, LLLL F");
		String date = String.format(date_format.format(calendar.getTime()));
		Card boot_card = new Card(this);
		try {
			if(send_command()){
				boot_card.setText("Computer shut down");
				boot_card.setFootnote(date);
				timeline.insert(boot_card);
				audio.playSoundEffect(Sounds.SUCCESS);
			} else {
				boot_card.setText("Computer failed to shut down");
				boot_card.setFootnote(date);
				timeline.insert(boot_card);
				audio.playSoundEffect(Sounds.ERROR);
			}
		} catch (IOException e) { }
		finish();
	}
}
