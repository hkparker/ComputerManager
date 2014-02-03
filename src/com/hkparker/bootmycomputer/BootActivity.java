package com.hkparker.bootmycomputer;

import java.util.Calendar;
import java.nio.channels.FileChannel;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.text.SimpleDateFormat;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import com.google.android.glass.app.Card;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.timeline.TimelineManager;
import com.hkparker.bootmycomputer.BootActivity;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.userauth.keyprovider.KeyPairWrapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.security.cert.Certificate;

public class BootActivity extends Activity{
	public String ssh_server = "hkparker.com";
	public String ssh_user = "root";
	public Integer ssh_port = 22;
	public String ssh_public_key_file = "id_rsa.pub";
	public String boot_command = "touch it_worked";
	
	public boolean send_command() throws IOException {
		SSHClient ssh = null;
		Boolean success = false;
		File public_key_file = new File(this.ssh_public_key_file);
		
		byte[] public_key_bytes = toByteArray(public_key_file);
		CertificateFactory certificate_factory = CertificateFactory.getInstance("X509");
		Certificate certificate = certificate_factory.generateCertificate(new ByteArrayInputStream(public_key_bytes));
		PublicKey public_key = certificate.getPublicKey();
		KeyPair keypair = new KeyPair(public_key, null);
		try {
			ssh = new SSHClient();
			ssh.connect(this.ssh_server);
			ssh.authPublickey(this.ssh_user, new KeyPairWrapper(keypair));
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
		AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat date_format = new SimpleDateFormat("KK:mm a, LLLL F");
		String date = String.format(date_format.format(calendar.getTime()));
		Card boot_card = new Card(this);
		try {
			if(send_command()){
				boot_card.setText("Computer booted");
				boot_card.setFootnote(date);
				timeline.insert(boot_card);
				audio.playSoundEffect(Sounds.SUCCESS);
			} else {
				boot_card.setText("Computer failed to boot");
				boot_card.setFootnote(date);
				timeline.insert(boot_card);
				audio.playSoundEffect(Sounds.ERROR);
			}
		} catch (IOException e) {}
		finish();
	}
	
	
	
	
//	private static byte[] toByteArray(File file) throws IOException {
//		  FileInputStream in = new FileInputStream(this.ssh_public_key_file);
//		  FileChannel channel = in.getChannel();
//		  ByteArrayOutputStream out = new ByteArrayOutputStream();
//		  channel.transferTo(0, channel.size(), Channels.newChannel(out));
//		  return out.toByteArray();
//	}
	
	
}
