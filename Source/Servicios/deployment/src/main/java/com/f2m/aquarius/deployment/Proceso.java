package com.f2m.aquarius.deployment;

public class Proceso {
	
	private Utils utils = new Utils();
	
	private boolean cleanWeb() {
		
		String path = utils.getProperty("directory") + "\\node_modules";
		System.out.println("Eliminando node_modules... " + path);
		utils.deleteDirectory(path);
		path = utils.getProperty("directory") + "\\dist";
		System.out.println("Eliminando dist... " + path);
		utils.deleteDirectory(path);
		path = utils.getProperty("directory") + "\\package-lock.json";
		System.out.println("Eliminando package-lock... " + path);
		utils.deleteFile(path);
		path = utils.getProperty("directory") + "\\portal.zip";
		System.out.println("Eliminando portal.zip... " + path);
		utils.deleteFile(path);
		return true;
	}
	
	public boolean installNpm() {
		if (cleanWeb()) {
			String path = utils.getProperty("directory");
			System.out.println("Instalando módulos...");
			String response = utils.executeCommand(new String[] {"cmd.exe", "/c", "npm install"}, path);
			if (response != null && response.length() > 0) {
				int indexOf = response.indexOf("[Utils - ExecuteCode]:");
				if (indexOf > -1) {
					String result = response.substring(indexOf);
					if (result.equals("[Utils - ExecuteCode]:0")) {
						return true;
					}
				}
			}
			return false;
		} else {
			return false;
		}
	}
	
	private boolean updatePickDatetime() {
		String path = utils.getProperty("directory") + "\\node_modules\\ng-pick-datetime";
		System.out.println("Eliminando ng-pick-datetime... " + path);
		utils.deleteDirectory(path);
		System.out.println("Actualizando PickDateTime...");
		path = utils.getProperty("directory");
		String response = utils.executeCommand(new String[] {"cmd.exe", "/c", "npm install ng-pick-datetime@5.0.0-beta.15 --save"}, path);
		if (response != null && response.length() > 0) {
			int indexOf = response.indexOf("[Utils - ExecuteCode]:");
			if (indexOf > -1) {
				String result = response.substring(indexOf);
				System.out.println(result);
				if (result.equals("[Utils - ExecuteCode]:0")) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean compileWeb() {
		if (updatePickDatetime()) {
			String path = utils.getProperty("directory") + "\\dist";
			System.out.println("Eliminando dist... " + path);
			utils.deleteDirectory(path);
			path = utils.getProperty("directory");
			System.out.println("Compilando...");
			String response = utils.executeCommand(new String[] {"cmd.exe", "/c", "ng build --prod"}, path);
			if (response != null && response.length() > 0) {
				int indexOf = response.indexOf("[Utils - ExecuteCode]:");
				if (indexOf > -1) {
					String result = response.substring(indexOf);
					System.out.println(result);
					if (result.equals("[Utils - ExecuteCode]:0")) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean createZipWeb() {
		
		String path = utils.getProperty("directory");
		System.out.println("Generando zip...");
		String response = utils.executeCommand(new String[] {"cmd.exe", "/c", "powershell -nologo -noprofile -command \"& { Add-Type -A 'System.IO.Compression.FileSystem'; [IO.Compression.ZipFile]::CreateFromDirectory('dist', 'portal.zip'); }\""}, path);
		if (response != null && response.length() > 0) {
			int indexOf = response.indexOf("[Utils - ExecuteCode]:");
			if (indexOf > -1) {
				String result = response.substring(indexOf);
				System.out.println(result);
				if (result.equals("[Utils - ExecuteCode]:0")) {
					return moveZipWeb();
				}
			}
		}
		return false;
	}
	
	private boolean moveZipWeb() {
		String path = utils.getProperty("provision") + "\\portal.zip";
		System.out.println("Eliminando zip anterior... " + path);
		utils.deleteFile(path);
		path = utils.getProperty("directory");
		System.out.println("Moviendo zip...");
		String response = utils.executeCommand(new String[] {"cmd.exe", "/c", "move portal.zip \"" + utils.getProperty("provision") + "\\\""}, path);
		if (response != null && response.length() > 0) {
			int indexOf = response.indexOf("[Utils - ExecuteCode]:");
			if (indexOf > -1) {
				String result = response.substring(indexOf);
				System.out.println(result);
				if (result.equals("[Utils - ExecuteCode]:0")) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean vagrantUp() {
		String path = utils.getProperty("vagrant");
		System.out.println("Levantando VM...");
		String response = utils.executeCommand(new String[] {"cmd.exe", "/c", "vagrant up"}, path);
		if (response != null && response.length() > 0) {
			int indexOf = response.indexOf("[Utils - ExecuteCode]:");
			if (indexOf > -1) {
				String result = response.substring(indexOf);
				System.out.println(result);
				if (result.equals("[Utils - ExecuteCode]:0")) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean updateVM() {
		if (vagrantUp()) {
			String path = utils.getProperty("vagrant");
			System.out.println("Actualizando VM...");
			String response = utils.executeCommand(new String[] {"cmd.exe", "/c", "vagrant provision"}, path);
			if (response != null && response.length() > 0) {
				int indexOf = response.indexOf("[Utils - ExecuteCode]:");
				if (indexOf > -1) {
					String result = response.substring(indexOf);
					System.out.println(result);
					if (result.equals("[Utils - ExecuteCode]:0")) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean cleanAndBuildWeb() {
		if (installNpm()) {
			if (compileWeb()) {
				if (createZipWeb()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static void main (String[] args) {
		Proceso proceso = new Proceso();
		if (proceso.cleanAndBuildWeb()) {
			if (proceso.updateVM()) {
				System.out.println("Se actualizó el sitio Web en la VM local");
			}
		}
	}
}
