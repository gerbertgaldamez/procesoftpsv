/**
 * 
 */
package com.terium.procesos;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.terium.dao.CbBancoAgenciaConfrontaDAO;
import com.jcraft.jsch.Session;
import com.terium.dao.CBConexionConfDAO;
import com.terium.dao.CbParametrosDAO;
import com.terium.modelos.CBBancoAgenciaConfrontaModel;
import com.terium.utils.DBUtils;

/**
 * @author aaron4431
 * 
 */
public class CambiarArchivo {
	List<CBBancoAgenciaConfrontaModel> bancoAgenciaConfronta;

	private FTPFile[] archivosFtp;

	// Metodo que mueve el archivo del directorio original a cada uno de los
	// configurados para cada confronta
	/*public void procesarArchivo() {
		CbBancoAgenciaConfrontaDAO cbac = new CbBancoAgenciaConfrontaDAO();
		CBConexionConfDAO cccd = new CBConexionConfDAO();
		CbParametrosDAO cpd = new CbParametrosDAO();
		// Lleno el listado con la tabla CB_BANCO_AGENCIA_CONFRONTA
		bancoAgenciaConfronta = new ArrayList<CBBancoAgenciaConfrontaModel>(
				cbac.listadoBancoAgenciaConfronta());
		String nombreArchivo = null;
		String ipConex = null;
		String usuarioConex = null;
		String passConex = null;
		Session conFTP = null;
		BufferedInputStream buffIn = null;
		File dir;
		// Recupero el directorio donde estan todos los archivos
		String realFTPPath = cpd.recuperaParametros() + "/";
		// Recorro el listado
		for (int i = 0; i < bancoAgenciaConfronta.size(); i++) {
			// Recupero los datos de conexion
			ipConex = cccd.ipConexionFTP(bancoAgenciaConfronta.get(i)
					.getIdConexionConf());
			usuarioConex = cccd.usuarioFTP(bancoAgenciaConfronta.get(i)
					.getIdConexionConf());
			passConex = cccd.passwordFTP(bancoAgenciaConfronta.get(i)
					.getIdConexionConf());
			try {
				// Hago la conexion hacia el FTP
				conFTP = DBUtils.obtieneConexionFtp(ipConex, usuarioConex,
						passConex);
				// Cambio el directorio
				//conFTP.changeWorkingDirectory(realFTPPath);
				// Reviso cuantos archivos hay
				//archivosFtp = conFTP.listFiles();
				for (FTPFile archivo : archivosFtp) {
					if (archivo.isFile()) {
						// Recupero el directorio temporal a donde va ser
						// descargado el
						// archivo
						String pathTemporal = cpd.recuperaParametrosDescarga()
								+ "/";
						// recupero el directorio a donde pertenece el archivo
						String pathDescarga = bancoAgenciaConfronta.get(i)
								.getPathftp() + "/";
						// Recupero el nombre del archivo a verificar
						nombreArchivo = archivo.getName();
						// Recupero la palabra clave a buscar
						String palabra = bancoAgenciaConfronta.get(i)
								.getPalabraArchivo();
						System.out.println("NOMBRE DEL ARCHIVO: "
								+ nombreArchivo);
						System.out.println("PALABRA CLAVE: " + palabra);

						if (palabra == null || palabra.compareTo("") == 0) {
							System.out.println("No hay palabra a buscar");
						} else {
							// Verifico que la palabra se encuentre dentro de
							// algun
							// archivo
							if (nombreArchivo.contains(palabra)) {
								System.out
										.println("Moviendo archivo a carpeta: "
												+ pathDescarga);
								// Muevo el archivo a la carpeta temporal
								FileOutputStream fos = new FileOutputStream(
										new File(pathTemporal + nombreArchivo));
								conFTP.retrieveFile(
										realFTPPath + nombreArchivo, fos);
								// Coloco el archivo en la carpeta que pertenece
								buffIn = new BufferedInputStream(
										new FileInputStream(pathTemporal
												+ nombreArchivo));
								conFTP.enterLocalPassiveMode();
								conFTP.storeFile(pathDescarga + nombreArchivo,
										buffIn);
								// Cierro los buffers
								fos.close();
								buffIn.close();
								dir = new File(pathTemporal + nombreArchivo);
								// Elimino los otros archivos
								dir.delete();
								conFTP.deleteFile(realFTPPath + nombreArchivo);
								// // Directorio origen
								// File origen = new File(realFTPPath
								// + nombreArchivo);
								// System.out.println("origen: " + origen);
								// // Directorio destino
								// File destino = new File(pathDescarga
								// + nombreArchivo);
								// System.out.println("destino: " + destino);
								// // Comienzo a leer el archivo
								// InputStream in = new FileInputStream(origen);
								// OutputStream out = new
								// FileOutputStream(destino);
								// byte[] buf = new byte[1024];
								// int len;
								// // Comienzo a escribir el archivo en el nuevo
								// // directorio
								// while ((len = in.read(buf)) > 0) {
								// out.write(buf, 0, len);
								// }
								// in.close();
								// out.close();
								// // Elimino el archivo del antiguo directorio
								// conFTP.deleteFile(realFTPPath +
								// nombreArchivo);
								System.out
										.println("a finalizado el movimiento dde archivos");
							} else {
								System.out
										.println("no hay archivos para mover");
							}
						}
					}
				}

			} catch (IOException e) {
				System.out.println("error al recuperar archivos: "
						+ e.getMessage());
			}
		}
	}*/
}
