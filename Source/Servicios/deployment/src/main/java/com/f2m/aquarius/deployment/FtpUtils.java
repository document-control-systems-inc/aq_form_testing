package com.f2m.aquarius.deployment;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.IOException;    


public class FtpUtils {

	private Session session;

	public void connect(String host, int port, String username, String password)
			throws JSchException, IllegalAccessException {
		if (this.session == null || !this.session.isConnected()) {
			JSch jsch = new JSch();
			this.session = jsch.getSession(username, host, port);
			this.session.setPassword(password);
			this.session.setConfig("StrictHostKeyChecking", "no");
			this.session.connect();
		} else {
			throw new IllegalAccessException("Sesion SFTP ya iniciada.");
		}
	}

	public final void addFile(String ftpPath, String filePath, String fileName)
			throws IllegalAccessException, IOException, SftpException, JSchException {
		if (this.session != null && this.session.isConnected()) {
			ChannelSftp sftp = (ChannelSftp)session.openChannel("sftp");
	        sftp.connect();
	        sftp.cd(ftpPath);
	        sftp.put(filePath, fileName);
	        sftp.exit();
	        sftp.disconnect();
		} else {
			throw new IllegalAccessException("No existe sesion SFTP iniciada.");
		}
	}
	
	public final void disconnect() {
        this.session.disconnect();
    }
	
	public static void main(String[] args) {
		FtpUtils ftp = new FtpUtils();
		try {
			ftp.connect("aquarius-test.centralus.cloudapp.azure.com", 22, "aquarius", "Aquarius%2017");
			ftp.addFile("/home/aquarius/vagrant/web", "C:\\Users\\gomado\\Documents\\F2M\\Aquarius\\Repositorio F2M\\aquarius\\Vagrant\\provisioning\\web\\portal.zip", "portal.zip");
			//ftp: /home/aquarius/vagrant/web
			//local: "C:\\Users\\gomado\\Documents\\F2M\\Aquarius\\Repositorio F2M\\aquarius\\Vagrant\\provisioning\\web\\portal.zip"
			ftp.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ftp.disconnect();
		}
	}
}
