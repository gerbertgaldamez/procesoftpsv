package com.terium.procesos;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.terium.dao.CBDataBancoDAO;
import com.terium.dao.CBInsertMaestroCargaDAO;
import com.terium.dao.ProcesarArchivoDAO;
import com.terium.modelos.CBArchivosInsertadosModel;
import com.terium.modelos.CBBancoAgenciaConfrontaModel;
import com.terium.utils.DBUtils;

public class ProcesarArchivo {
	private static final Logger logger = Logger
			.getLogger(ProcesarArchivo.class);
	private FTPFile[] archivosFtp;
	private static String idMaestroCarga;
	private static String dia;
	private static int agenciaId;
	private ProcessFileTxtService servicio = new ProcessFileTxtService();
	private CBArchivosInsertadosModel insertar = new CBArchivosInsertadosModel();
	CBDataBancoDAO cbd = new CBDataBancoDAO();
	private Date fechaARchivo;
	DateFormat df = null;
	DateFormat newFormat = new SimpleDateFormat("HHmmssddMMyyyy");

	// Obtiene archivo FTP
	public void procesarArchivos(CBBancoAgenciaConfrontaModel bancoAgencia,
			String ipConex, String usuarioConex, String passConex, BigDecimal comision)
			throws ParseException {
		System.out.println("Ejecutando proceso para agencia: "+bancoAgencia.getcBCatalogoBancoId());
		
		//Declarando objetos para manejo de archivo
		FileReader fr = null;
		FileOutputStream fos = null;
		BufferedReader br = null;
		BufferedInputStream buffIn = null;
		File dir = null;
		
		//Variables para el manejo de ID
		int idBanco = bancoAgencia.getcBCatalogoBancoId();
		int idAgencia = bancoAgencia.getcBCatalogoAgenciaId();
		int idConfronta = bancoAgencia.getcBConfiguracionConfrontaId();
		int idBancoAgenciaConfranta = bancoAgencia.getcBBancoAgenciaConfrontaId();
		//int tipo = Integer.parseInt(bancoAgencia.getTipo());
		String tipo = bancoAgencia.getTipo();
		
		String user = "CONFRONTAS";
		String nombreArchivo = "";
		String nombreArchivo1 = "";
		String palabra = "";
		String realFTPPath = bancoAgencia.getPathftp() + "/";
		Session conFTP = null;
		
		/*Agregado por Nicolas Bermudez*/
		String formatoFechaConfronta = bancoAgencia.getFormatoFecha();
		formatoFechaConfronta = formatoFechaConfronta.replace("hh24", "HH");
		formatoFechaConfronta = formatoFechaConfronta.replace("mi", "mm");
		
		df = new SimpleDateFormat(formatoFechaConfronta);
		System.out.println("Formato de Fecha modificado en Java: " + formatoFechaConfronta);
		
		//Objetos
		CBInsertMaestroCargaDAO cbima = new CBInsertMaestroCargaDAO();
		MainProcess mp = new MainProcess();
		
		ProcesarArchivoDAO pad = new ProcesarArchivoDAO();
		String PathDescarga = pad.recuperaPathDescarga() + "/";
		String carpetaProcesados = pad.recuperaCarpetaProcesados() + "/";
		
		System.out.println("PATH: " + realFTPPath);
		String pathFull = realFTPPath;
		
		ChannelSftp chanelSftp = null;
		InputStream file_temp = null;
		
		try {
			System.out.println("2");
			conFTP = DBUtils.obtieneConexionFtp(ipConex, usuarioConex,  passConex);
			
			
			System.out.println("Conexion SFTP:" + conFTP.isConnected());
			chanelSftp = (ChannelSftp) conFTP.openChannel("sftp");
			chanelSftp.connect();
			chanelSftp.cd(pathFull);
			
			System.out.println("Directorio: " + chanelSftp.pwd());
			System.out.println("Tamanio de la carpeta: " + chanelSftp.ls(chanelSftp.pwd()));
			System.out.println("3");
			System.out.println("REALPATH: " + realFTPPath);
			
			System.out.println("4");
			//System.out.println("ARCHIVOS FTP: " + archivosFtp.length);
			System.out.println("5");
			System.out.println(realFTPPath);
			
			File f = new File(pathFull); 
			
			if(f.exists()) {
				for(File archivo: f.listFiles()) {
					System.out.println("6 --> Iterando archivos");
					if (archivo.isFile()) {
						System.out.println("7 --> Valida que lo leido sea un archivo correcto");
						// Recupero el nombre del archivo
						nombreArchivo = archivo.getName();
						
						// Recupero la palabra clave a buscar en archivo
						palabra = bancoAgencia.getPalabraArchivo();
						
							// Recupero la extension del archivo
							String extension = archivo.getName().substring(archivo.getName().length() - 4);
							
							System.out.println("TIPO DE ARCHIVO: "+ extension.toLowerCase());
							// Verifico que la palabra clave este en el archivo y
							// sea del tipo txt ****************************************************
							//Modificado por Nicolas Bermudez - Qitcorp 16/08/2017
							if (".txt".toUpperCase().equals(extension.toLowerCase().toUpperCase()) 
									|| ".log".toUpperCase().equals(extension.toUpperCase()) 
									|| ".dat".toUpperCase().equals(extension.toUpperCase())) {
								// Verifica si la conexion esta abierta
								logger.info("Procesando un archivo "+extension+"...");
								System.out.println("Procesando un archivo "+extension+"...");
								// cerrando conexiones
								//conFTP = null;
								// Recuperando la conexion
								
								
								idMaestroCarga = cbima.obtieneUltimoIDMaestroIns();
								logger.info("Inicia descarga de Archivo:"+ nombreArchivo);
								// mueve los archivos a una carpeta local
								logger.info("Moviendo archivo a carpeta temporal: "+ nombreArchivo);
								System.out.println("5");
	
								// Archivo a donde lo va a descargar para
								// procesar
								
		
								//Juankrlos --> 03/07/2017
								fos = new FileOutputStream(nombreArchivo);
								System.out.println("Nueva ubicacion para el archivo: "+ PathDescarga + nombreArchivo);
								// Carpeta descarga para procesamiento del
								// archivo
								System.out.println("ARCHIVO PATH ORIGEN: " + realFTPPath+ nombreArchivo);
								System.out.println("ARCHIVO PATH DESTINO: " + PathDescarga+ nombreArchivo);
								
								
								/*conFTP.retrieveFile(realFTPPath + nombreArchivo,fos);*/
								//se quita tempral: PathDescarga + 
								dir = new File(realFTPPath+nombreArchivo);
								fr = new FileReader(dir);
								br = new BufferedReader(fr);
								System.out.println("6 cerrando conexion ftp...");
								
								/*Modificado por Nicolas Bermudez 02/02/2017*/
								//conFTP.logout();
								//conFTP.disconnect();
								
								System.out.println("EL id Maestro generado es: "+ idMaestroCarga);
								insertar.setBanco(idBanco);
								insertar.setAgencia(idAgencia);
								insertar.setNombre_archivo(nombreArchivo);
								insertar.setCreadoPor(user);
								
								// ejecuta metodo para insertar en tabla maestro de carga
								cbima.insertaArchivos(insertar, idMaestroCarga);
								// Procesa Archivo
								logger.info("Procesando archivo: " + nombreArchivo);
								System.out.println("Procesando archivo: "+ nombreArchivo);
								System.out.println("idMaestroCarga envia a procesararchivo: "+ idMaestroCarga);
								ProcesaArchivo(br, idBanco, idAgencia, idConfronta,
										user, tipo, nombreArchivo, idMaestroCarga,
										idBancoAgenciaConfranta, comision);
								// mueve el archivo a su carpeta de procesados
								logger.debug("comienza proceso para mover archivo "+ nombreArchivo);
								System.out.println("idMaestroCarga : "+ getIdMaestroCarga());
								System.out.println("idMaestroCarga envia a procesararchivos: "+ idMaestroCarga);
								if (mp.getObtieneIdCarpeta() == null) {
									if (getIdMaestroCarga() == null) {
										System.out.println("no hay idMaestro1");
										//dir.delete(); *******************************************
										file_temp = new FileInputStream(archivo);
										nombreArchivo1 = "duplicado-"+nombreArchivo;//agrega ovidio
										chanelSftp.put(file_temp, pathFull+"procesados/"+nombreArchivo1);
										
										chanelSftp.rm(nombreArchivo);
										
										logger.info("Se removio archivo de carpeta temporal: "+ nombreArchivo1);
										System.out.println("Se removio archivo de carpeta temporal duplicado: "+ nombreArchivo1);
									} else {
										// Verifica si la conexion esta abierta
										logger.info("Recuperando conexion");
										System.out.println("Recuperando conexion");
										// cerrando conexiones
										
										
										/*conFTP.changeWorkingDirectory(realFTPPath);*/
										fechaARchivo = new Date();
										
										try {
											System.out.println("Moviendo archivo!! *************************** " + nombreArchivo);
											
											
											file_temp = new FileInputStream(archivo);
	
											chanelSftp.put(file_temp, pathFull+"procesados/"+nombreArchivo);
											
											chanelSftp.rm(nombreArchivo);
											
											
											System.out.println("NUEVO NOMBRE DE ARCHIVO"
													+ realFTPPath+"procesados/"+nombreArchivo);
											
											logger.info("NUEVO NOMBRE "
													+ realFTPPath
													+ carpetaProcesados
													+ newFormat
															.format(fechaARchivo)
													+ nombreArchivo);
											// Borra el archivo del directorio
											// origen
											logger.info("Removiendo archivo de carpeta origen");
											System.out.println("Removiendo archivo de carpeta origen");
											
											//conFTP.deleteFile(realFTPPath+ nombreArchivo);
											//dir.delete();
											
											logger.info("Se removio archivo de carpeta temporal: "+ nombreArchivo);
											System.out.println("Se removio archivo de carpeta temporal: "+ nombreArchivo);	
											// cierra los stream
											fr.close();
											br.close();
											fos.close();
											//buffIn.close();
										} catch (Exception e) {
											logger.info("Error al intentar mover el archivo a la carpeta procesado: "
													+ nombreArchivo);
											System.out
													.println("Error al intentar mover el archivo a la carpeta procesado: "
															+ nombreArchivo);
											System.out.println("error: "+e.getMessage());
										}
										// Gaurdando el idMaestro para el correo de
										// conciliacion
										mp.setObtieneIdCarpeta(getIdMaestroCarga());
										// Contador para el envio de correo
										mp.setContador(mp.getContador() + 1);
										System.out.println("primer contador: "
												+ mp.getContador());
										
										/*conFTP.logout();
										conFTP.disconnect();*/
									}
								} else {
									if (getIdMaestroCarga() == null) {
										System.out.println("no hay idMaestro2");
										//dir.delete(); ***************************
										file_temp = new FileInputStream(archivo);
										
										chanelSftp.put(file_temp, pathFull+"procesados/duplicado-"+nombreArchivo);
										
										chanelSftp.rm(nombreArchivo);
										
										logger.info("Se removio archivo de carpeta temporal: "
												+ nombreArchivo);
									} else {
										// Verifica si la conexion esta abierta
										logger.info("Recuperando conexion");
										System.out.println("Recuperando conexion");
	
										fechaARchivo = new Date();
										try {
											System.out.println("Moviendo archivo********************* " + nombreArchivo);
											
											file_temp = new FileInputStream(archivo);
											
											chanelSftp.put(file_temp, pathFull+"procesados/"+nombreArchivo);
											
											chanelSftp.rm(nombreArchivo);
											
											
											System.out.println("NUEVO NOMBRE "
													+ realFTPPath
													+ carpetaProcesados
													+ newFormat
															.format(fechaARchivo)
													+ nombreArchivo);
											logger.info("NUEVO NOMBRE "
													+ realFTPPath
													+ carpetaProcesados
													+ newFormat
															.format(fechaARchivo)
													+ nombreArchivo);
											// Borra el archivo del directorio
											// origen
											logger.info("Removiendo archivo de carpeta origen");
											System.out
													.println("Removiendo archivo de carpeta origen");
											
											//conFTP.deleteFile(realFTPPath
													//+ nombreArchivo);
											//dir.delete();
											
											logger.info("Se removio archivo de carpeta temporal: "
													+ nombreArchivo);
											// cierra los stream
											fr.close();
											br.close();
											fos.close();
											//buffIn.close();
										} catch (Exception e) {
											logger.info("Error al intentar mover el archivo a la carpeta procesado: "
													+ nombreArchivo);
											System.out
													.println("Error al intentar mover el archivo a la carpeta procesado: "
															+ nombreArchivo);
										}
										// Gaurdando el idMaestro para el correo de
										// conciliacion
										mp.setObtieneIdCarpeta(mp
												.getObtieneIdCarpeta()
												+ ";"
												+ getIdMaestroCarga());
										// Contador para el envio de correo
										mp.setContador(mp.getContador() + 1);
										System.out.println("CONTADOR DE CORREO: "
												+ mp.getContador());
										
										/*conFTP.logout();
										conFTP.disconnect();*/
									}
								}
								System.out.println("Id dentro de la carpeta: "
										+ mp.getObtieneIdCarpeta());
	
								if (getDia() != null && getIdMaestroCarga() != null) {
									logger.info("Id maestro de la carga: "
											+ getIdMaestroCarga());
									logger.info("Dia de carga: " + getDia());
									logger.info("Id agencia: " + idAgencia);
									System.out.println("DIA PARA AJUSTES: "
											+ getDia());
									// proceso de conciliacion
								//	cbd.ejecutaProcesoConciliacion(getIdMaestroCarga());
									
									
									// cierra los stream
									fr.close();
									br.close();
									fos.close();
									//buffIn.close();
								}
								setIdMaestroCarga(null);
								setDia(null);
								//Modificado por Nicolas Bermudez - Qitcorp 16/08/2017
							} else if(".xls".equals(extension.toLowerCase()) || "xlsx".equals(extension.toLowerCase())) {
								// Verifica si la conexion esta abierta
								logger.info("Recuperando conexion");
								System.out.println("Recuperando conexion");
								
								
								idMaestroCarga = cbima.obtieneUltimoIDMaestroIns();
								logger.info("Inicia descarga de Archivo:"
										+ nombreArchivo);

								// mueve los archivos a una carpeta local
								logger.info("Moviendo archivo a carpeta temporal: "
										+ nombreArchivo);
								System.out.println("5");
								System.out.println("Conexion ftp abierta: "+conFTP.isConnected());
								System.out.println("Real path: "+realFTPPath);
								// Archivo a donde lo va a descargar para
								// procesar
								//File fl = new File(nombreArchivo);
	
								System.out.println(archivo.getName());
								fos = new FileOutputStream(nombreArchivo);
								System.out
										.println("Nueva ubicacion para el archivo: "
												+ PathDescarga + nombreArchivo);
								// Carpeta descarga para procesamiento del
								// archivo
								System.out.println("ORIGEN: " + realFTPPath + nombreArchivo);
								System.out.println("DESTINO: " + PathDescarga + nombreArchivo);
								
								/*conFTP.retrieveFile(nombreArchivo, fos);*/
								
								//se quito el dato "PathDescarga + "
								dir = new File(realFTPPath+nombreArchivo);
								fr = new FileReader(dir);
								br = reedExcel(realFTPPath+nombreArchivo, formatoFechaConfronta);
								System.out.println("6");
								
								/*Comentado por Nicolas Bermudez*/
								//conFTP.logout();
								//conFTP.disconnect();
								System.out.println("EL id generado es: "
										+ idMaestroCarga);
								insertar.setBanco(idBanco);
								insertar.setAgencia(idAgencia);
								insertar.setNombre_archivo(nombreArchivo);
								insertar.setCreadoPor(user);
								// ejecuta metodo para insertar en tabla maestro
								// de
								// carga
								cbima.insertaArchivos(insertar, idMaestroCarga);
								// Procesa Archivo
								logger.info("Procesando archivo: " + nombreArchivo);
								System.out.println("Procesando archivo: "
										+ nombreArchivo);
								ProcesaArchivo(br, idBanco, idAgencia, idConfronta,
										user, tipo, nombreArchivo, idMaestroCarga,
										idBancoAgenciaConfranta, comision);
								// mueve el archivo a su carpeta de procesados
								logger.debug("comienza proceso para mover archivo "
										+ nombreArchivo);
								if (mp.getObtieneIdCarpeta() == null) {
									if (getIdMaestroCarga() == null) {
										System.out.println("no hay idMaestro1");
										//dir.delete(); *******************************************
										file_temp = new FileInputStream(archivo);
										nombreArchivo1 = "duplicado-"+nombreArchivo;//agrega ovidio
										chanelSftp.put(file_temp, pathFull+"procesados/"+nombreArchivo1);
										
										chanelSftp.rm(nombreArchivo);
										
										logger.info("Se removio archivo de carpeta temporal: "+ nombreArchivo1);
										System.out.println("Se removio archivo de carpeta temporal duplicado: "+ nombreArchivo1);
									} else {
										// Verifica si la conexion esta abierta
										logger.info("Recuperando conexion");
										System.out.println("Recuperando conexion");
										
										fechaARchivo = new Date();
										try {
											
											
											/*Modificado por Nicolas Bermudez*/
											System.out.println("Moviendo archivo ************** " + nombreArchivo);
											
											file_temp = new FileInputStream(archivo);
											
											chanelSftp.put(file_temp, pathFull+"procesados/"+nombreArchivo);
											
											chanelSftp.rm(nombreArchivo);
											
											System.out.println("NUEVO NOMBRE "
													+ realFTPPath
													+ carpetaProcesados
													+ newFormat
															.format(fechaARchivo)
													+ nombreArchivo);
											logger.info("NUEVO NOMBRE "
													+ realFTPPath
													+ carpetaProcesados
													+ newFormat
															.format(fechaARchivo)
													+ nombreArchivo);
											// Borra el archivo del directorio
											// origen
											logger.info("Removiendo archivo de carpeta origen");
											System.out
													.println("Removiendo archivo de carpeta origen");
											
											//conFTP.deleteFile(realFTPPath
													//+ nombreArchivo);
											//dir.delete();
											
											logger.info("Se removio archivo de carpeta temporal: "
													+ nombreArchivo);
	
											// cierra los stream
											fr.close();
											br.close();
											fos.close();
											//buffIn.close();
										} catch (Exception e) {
											logger.info("Error al intentar mover el archivo a la carpeta procesado: "
													+ nombreArchivo);
											System.out
													.println("Error al intentar mover el archivo a la carpeta procesado: "
															+ nombreArchivo);
										}
										// Gaurdando el idMaestro para el correo de
										// conciliacion
										mp.setObtieneIdCarpeta(getIdMaestroCarga());
										// Contador para el envio de correo
										mp.setContador(mp.getContador() + 1);
										System.out.println("primer contador: "
												+ mp.getContador());
										//conFTP.logout();
										//conFTP.disconnect();
									}
								} else {
									if (getIdMaestroCarga() == null) {
										System.out.println("no hay idMaestro4");
										dir.delete();
										logger.info("Se removio archivo de carpeta temporal: "
												+ nombreArchivo);
									} else {
										// Verifica si la conexion esta abierta
										logger.info("Recuperando conexion");
										System.out.println("Recuperando conexion");
										/*conFTP.changeWorkingDirectory(realFTPPath);*/
	
										fechaARchivo = new Date();
										try {
											
											System.out.println("Moviendo archivo **************** " + nombreArchivo);
											
											file_temp = new FileInputStream(archivo);
											
											chanelSftp.put(file_temp, pathFull+"procesados/"+nombreArchivo);
											
											chanelSftp.rm(nombreArchivo);
											System.out.println("NUEVO NOMBRE "
													+ realFTPPath
													+ carpetaProcesados
													+ newFormat
															.format(fechaARchivo)
													+ nombreArchivo);
											logger.info("NUEVO NOMBRE "
													+ realFTPPath
													+ carpetaProcesados
													+ newFormat
															.format(fechaARchivo)
													+ nombreArchivo);
											// Borra el archivo del directorio
											// origen
											logger.info("Removiendo archivo de carpeta origen");
											System.out
													.println("Removiendo archivo de carpeta origen");
											
											//conFTP.deleteFile(realFTPPath
													//+ nombreArchivo);
											//dir.delete();
											
											logger.info("Se removio archivo de carpeta temporal: "
													+ nombreArchivo);
											// cierra los stream
											fr.close();
											br.close();
											fos.close();
											//buffIn.close();
										} catch (Exception e) {
											logger.info("Error al intentar mover el archivo a la carpeta procesado: "
													+ nombreArchivo);
											System.out
													.println("Error al intentar mover el archivo a la carpeta procesado: "
															+ nombreArchivo);
										}
										// Gaurdando el idMaestro para el correo de
										// conciliacion
										mp.setObtieneIdCarpeta(mp
												.getObtieneIdCarpeta()
												+ ";"
												+ getIdMaestroCarga());
										// Contador para el envio de correo
										mp.setContador(mp.getContador() + 1);
										System.out.println("CONTADOR DE CORREO: "
												+ mp.getContador());
										
										/*conFTP.logout();
										conFTP.disconnect();*/
									}
								}
								System.out.println("Id dentro de la carpeta: "
										+ mp.getObtieneIdCarpeta());
	
								if (getDia() != null && getIdMaestroCarga() != null) {
									logger.info("Id maestro de la carga: "
											+ getIdMaestroCarga());
									logger.info("Dia de carga: " + getDia());
									logger.info("Id agencia: " + idAgencia);
									System.out.println("DIA PARA AJUSTES: "
											+ getDia());
									
									
									// Proceso de ajustes pendientes
									
									
									
									// cierra los stream
									fr.close();
									br.close();
									fos.close();
									//buffIn.close();
								}
								setIdMaestroCarga(null);
								setDia(null);
							}
						//Comentado por Nicolas Bermudez - Qitcorp 16/08/2017
						//}
					}else{
						System.out.println("No es un archivo valido...");
					}
	
				}
			}
			
			//Cerrando sesion sftp
			
		} catch (IOException e) {
			logger.error("error al obtener la lista de archivos: "
					+ e.getMessage());
			System.out.println("error io: " + e.getMessage());
			e.printStackTrace();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			if(conFTP != null)
				conFTP.disconnect();
			
			if(chanelSftp != null) {
				chanelSftp.exit();
				chanelSftp.disconnect();
			}
			
			if(file_temp != null)
				try {
					file_temp.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

	}

	// metodo para mandar los archivos para su ejecucion
	public void ProcesaArchivo(BufferedReader reader, int idBanco,
			int idAgencia, int idConfronta, String user, String tipo,
			String nombreArchivo, String idMaestroCarga,
			int idBancoAgenciaConfronta, BigDecimal comision) {
		
		servicio.leerArchivo(reader, idBanco, idAgencia, idConfronta, user,
				 nombreArchivo, tipo,comision, idBancoAgenciaConfronta,  idMaestroCarga );

	}

	public BufferedReader reedExcel(String archivo, String formatoFechaConfronta) {
		System.out.println("ARCHIVO A LEER EXCEL: " + archivo);
		String ext = "";
		String path = "";
		FileInputStream file1;
		String linea;
		String registro = "";
		String lineaCompleta = "";
		StringReader stringReader = null;
		DateFormat df = new SimpleDateFormat(formatoFechaConfronta);
		DataFormatter formatter = new DataFormatter();

		File file2;
		FileReader fr;
		BufferedReader br = null;
		try {
			file1 = new FileInputStream(new File(archivo));
			path = archivo;
			ext = FilenameUtils.getExtension(path);
			System.out.println("EXTENCION: " + ext);
			if ("xls".equals(ext.toLowerCase())) {
				try {
					HSSFWorkbook libro = (HSSFWorkbook) WorkbookFactory
							.create(file1);
					HSSFSheet hoja = libro.getSheetAt(0);
					Iterator<Row> rows = hoja.iterator();
					while (rows.hasNext()) {
						linea = "";
						HSSFRow row = (HSSFRow) rows.next();
						for (int celda = 0; celda < row
								.getPhysicalNumberOfCells(); celda++) {
							
							if (row.getCell(celda) != null && !row.getCell(celda).toString().trim().equals("")) {
								switch (row.getCell(celda).getCellType()) {
									case Cell.CELL_TYPE_NUMERIC:
										if (DateUtil.isCellDateFormatted(row
												.getCell(celda))) {
											registro = df.format(row.getCell(celda)
													.getDateCellValue());
										} else {
											registro = formatter.formatCellValue(row
													.getCell(celda));
										}
										break;
									case Cell.CELL_TYPE_STRING:
										registro = formatter.formatCellValue(row
												.getCell(celda));
										break;
								}
							}else {
								registro = " ";
							}
							
							
							if (linea.equals("")) {
								linea = registro;
							} else {
								linea = linea + "\t" + registro;
							}
						}
						lineaCompleta = lineaCompleta + linea + "\n";
					}
					stringReader = new StringReader(lineaCompleta);
					br = new BufferedReader(stringReader);
				} catch (InvalidFormatException e) {
					System.out.println("Error invalidformatException: "
							+ e.getMessage());
				} catch (IOException e) {
					System.out.println("Error ioException: " + e.getMessage());
				} catch (IllegalArgumentException e) {
					System.out
							.println("Archivo con errores leyendo como streemData");
					// return br = null;
					file2 = new File(archivo);
					fr = new FileReader(file2);
					br = new BufferedReader(fr);
				}
			} else if ("xlsx".equals(ext.toLowerCase())) {
				try {
					XSSFWorkbook libro = (XSSFWorkbook) WorkbookFactory
							.create(file1);
					XSSFSheet hoja = libro.getSheetAt(0);
					Iterator<Row> rows = hoja.iterator();
					while (rows.hasNext()) {
						linea = "";
						XSSFRow row = (XSSFRow) rows.next();
						for (int celda = 0; celda < row
								.getPhysicalNumberOfCells(); celda++) {
							switch (row.getCell(celda).getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								if (DateUtil.isCellDateFormatted(row
										.getCell(celda))) {
									registro = df.format(row.getCell(celda)
											.getDateCellValue());
								} else {
									registro = formatter.formatCellValue(row
											.getCell(celda));
								}
								break;
							case Cell.CELL_TYPE_STRING:
								registro = formatter.formatCellValue(row
										.getCell(celda));
								break;
							}
							if (linea.equals("")) {
								linea = registro;
							} else {
								linea = linea + "\t" + registro;
							}
						}
						lineaCompleta = lineaCompleta + linea + "\n";
					}
					stringReader = new StringReader(lineaCompleta);
					br = new BufferedReader(stringReader);
				} catch (InvalidFormatException e) {
					System.out.println("Error invalidformatException: "
							+ e.getMessage());
				} catch (IOException e) {
					System.out.println("Error ioException: " + e.getMessage());
				} catch (IllegalArgumentException e) {
					System.out
							.println("Archivo con errores leyendo como streemData");
					file2 = new File(archivo);
					fr = new FileReader(file2);
					br = new BufferedReader(fr);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException: " + e.getMessage());
		}
		return br;
	}

	public String getIdMaestroCarga() {
		return idMaestroCarga;
	}

	@SuppressWarnings("static-access")
	public void setIdMaestroCarga(String idMaestroCarga) {
		this.idMaestroCarga = idMaestroCarga;
	}

	public String getDia() {
		return dia;
	}

	@SuppressWarnings("static-access")
	public void setDia(String dia) {
		this.dia = dia;
	}

	public int getAgenciaId() {
		return agenciaId;
	}

	@SuppressWarnings("static-access")
	public void setAgenciaId(int agenciaId) {
		this.agenciaId = agenciaId;
	}

}
