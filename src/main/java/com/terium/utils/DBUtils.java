package com.terium.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.log4j.Logger;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class DBUtils {
	private static Logger logger = Logger.getLogger(DBUtils.class);
	protected static Properties propiedades;

	// obtiene archivo de propiedades
	public static String getValor(String llave) {
		try {
			logger.info("antes de obtener configuracion");
			if (propiedades == null) {
				propiedades = new Properties();
				//propiedades.load(new FileInputStream("src/main/resources/configuracion.properties"));
				propiedades.load(new FileInputStream("configuracion.properties"));
				//propiedades.load(new FileInputStream("configuracion.properties"));
			}
			return propiedades.getProperty(llave).trim();
		} catch (Exception e) {
			System.out.println("Error al leer el archivo de propiedades"
					+ e.getMessage());
			logger.error("Error al leer el archivo de propiedades"
					+ e.getMessage());
			return "";
		}
	}

	public static Connection getConnection() {
		logger.info("se obtiene el valor de la ip: "+getValor("ip_scl"));
		try {
			return getConnection("jdbc:oracle:thin:@" + getValor("ip_scl")
					+ ":" + getValor("puerto_scl") + ":"
					+ getValor("service_scl"), getValor("usuario_scl"),
					getValor("clave_scl"));
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	public static Connection getConnection(String strConexion, String user,
			String pass) {
		Connection conn = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			conn = DriverManager.getConnection(strConexion, user, pass);
			System.out.println("Conexion a bk: "+strConexion);
			logger.debug("Conexion a bk: "+strConexion);
		} catch (Exception e) {
			logger.error(e);
		}
		return conn;
	}

	private static FTPClient conFtp = new FTPClient();

	// obtiene conexion por FTP
	public static Session obtieneConexionFtp(String ipConexion,
			String usuarioConexion, String passConexion) {
		
		System.out.println("IP: " + ipConexion);
		System.out.println("USUARIO: " + usuarioConexion);
		System.out.println("PASS: " + passConexion);
		
		JSch conn = new JSch();
		Session sesionsftp = null;
		
		try {
			
			sesionsftp = conn.getSession(usuarioConexion, ipConexion, 22);

			sesionsftp.setPassword(passConexion);
			sesionsftp.setConfig("StrictHostKeyChecking", "no");
			sesionsftp.connect();
			System.out.println("conectado por sftp: " + sesionsftp.isConnected());
			
			return sesionsftp;
		} catch (Exception e) {
			System.out.println("error socket conexion ftp: " + e.getMessage());
			e.printStackTrace();
		}
		return sesionsftp;

	}

	public static void cierraConexion() {
		try {
			conFtp.logout();
			conFtp.disconnect();
		} catch (IOException e) {
			System.out
					.println("error al cerrar la conexion: " + e.getMessage());
		}
	}
}