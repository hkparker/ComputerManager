package com.hkparker.bootmycomputer;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import android.app.Activity;
import android.os.Bundle;
import com.google.android.glass.app.Card;
import com.google.android.glass.timeline.TimelineManager;
import com.hkparker.bootmycomputer.BootActivity;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class BootActivity extends Activity{
	public String ssh_server = "";
	public String ssh_user = "";
	public String ssh_public_key_file = "";
	public String boot_command = "";
	
	
	public boolean send_command() throws IOException {
		SSHClient ssh = null;
		Boolean success = false;
		try {
			ssh = new SSHClient();
			ssh.connect(this.ssh_server);
			ssh.authPublickey("http://stackoverflow.com/questions/3686710/sshj-example-of-private-public-key-authentication");
			Session session = ssh.startSession();
			try {
                Command command = session.exec(this.boot_command);
                command.join();
                Integer exit_status = command.getExitStatus();
                if (exit_status == 0) {
                	success = true;
                }
            } finally {
                session.close();
            }
		} finally {
			ssh.close();
		}
		return success;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TimelineManager timeline = TimelineManager.from(this);
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat date_format = new SimpleDateFormat("KK:mm a, LLLL F");
		String date = String.format(date_format.format(calendar.getTime()));
		Card boot_card = new Card(this);
		try {
			if(send_command()){
				boot_card.setText("Computer booted");
				boot_card.setFootnote(date);
				timeline.insert(boot_card);
			} else {
				boot_card.setText("Computer failed to boot");
				boot_card.setFootnote(date);
				timeline.insert(boot_card);
			}
		} catch (IOException e) {}
		finish();
	}
}
