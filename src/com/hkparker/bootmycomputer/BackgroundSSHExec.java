package com.hkparker.bootmycomputer;

import java.io.IOException;
import java.security.PublicKey;

import android.os.AsyncTask;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;

public class BackgroundSSHExec extends AsyncTask<String, Void, Boolean> {
	public String ssh_server = "hkparker.no-ip.biz";
	public String ssh_user = "hayden";
	public String ssh_password = "";
	public Integer ssh_port = 22;
	public Integer expected_exit_status = 0;
	
	@Override
	protected Boolean doInBackground(String... cmd) {
		String ssh_command = cmd[0];
		SSHClient ssh = new SSHClient();
		HostKeyVerifier always_verify = new HostKeyVerifier() {
					public boolean verify(String arg0, int arg1, PublicKey arg2) {
						return true;
					}  
		}; 
		ssh.addHostKeyVerifier(always_verify);  
		Boolean success = false;
		try {
			ssh.connect(this.ssh_server);
			ssh.authPassword(this.ssh_user, this.ssh_password);
			Session session = ssh.startSession();
			Command command = session.exec(ssh_command);
			command.join();
			Integer exit_status = command.getExitStatus(); 
			if (exit_status == this.expected_exit_status) {
				success = true;
			}
			session.close();
			ssh.close();
		} catch (IOException e) { }
		return success;
	}
}