package com.terium.procesos;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.terium.dao.CBConfiguracionConfrontaDAO;
import com.terium.dao.CBDataBancoDAO;
import com.terium.dao.CBDataSinProcesarDAO;
import com.terium.dao.CBInsertMaestroCargaDAO;
import com.terium.modelos.CBConfiguracionConfrontaModel;
import com.terium.modelos.CBDataBancoModel;
import com.terium.modelos.CBDataSinProcesarModel;
import com.terium.utils.Constants;
import com.terium.utils.CustomDate;

public class ProcessFileTxtService {
	private static final Logger logger = Logger.getLogger(ProcessFileTxtService.class);
	private CBConfiguracionConfrontaDAO listadoConfConfronta;
	private CBDataBancoDAO cbdbdao = new CBDataBancoDAO();
	private CBDataSinProcesarDAO cbdspdao = new CBDataSinProcesarDAO();
	List<CBDataBancoModel> dataBancoModels = new ArrayList<CBDataBancoModel>();
	private List<CBDataSinProcesarModel> datosSinProcesar = new ArrayList<CBDataSinProcesarModel>();

	private String posiciones;
	private String tamanoDeCadena;
	private String fechaArchivo;
	// valores de configuracion
	private String delimitador1;
	private String delimitador2;
	private int cantidadAgrupacion;
	private String nomenclatura;
	private String formatoFecha;

	/* Agregadp por Nicolas Bermudez */
	private int splitDataLenght = 0;

	public static final String UTF8_BOM = "\uFEFF";

	public void leerArchivo(BufferedReader bufferedReader, int idBanco, int idAgencia, int idConfronta, String user,
			String nombreArchivo, String tipo, BigDecimal comisionConfronta, int idAgeConfro, String idMaestroCarga) {

		logger.debug("comienza proceso de lectura del archivo");
		System.out.println("============= Comienza proceso de lectura del archivo ================");

		ProcesarArchivo pa = new ProcesarArchivo();
		CBInsertMaestroCargaDAO cmc = new CBInsertMaestroCargaDAO();
		Date fechaCreacion = new Date();
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

		String strLine = "";
		String strLine1 = "";
		String linea = "";

		int procesados = 0;
		int noProcesados = 0;

		/**
		 * Agregado por Nicolas Bermudez - Configuracion para leer desde la linea N de
		 * cualquier archivo
		 */
		CBDataBancoDAO objeDataBancoDAO = new CBDataBancoDAO();
		int contLinea = 0;
		int lineaEmpiezaLeer = objeDataBancoDAO.obtenerLineaLectura(idConfronta);
		;
		String formatoFechaConfronta = objeDataBancoDAO.obtenerFormatoFechaConfronta(idConfronta); // CarlosGodinez
		// -> 28/08/2017

		/**
		 *
		 * */

		try {
			getConfrontaConf(idConfronta);
			// Lee el archivo linea por linea
			while ((strLine = bufferedReader.readLine()) != null) {
				if (contLinea >= lineaEmpiezaLeer) {

					// strLine1 = strLine.replaceAll(" ", "").trim();
					strLine1 = strLine;

					// ************************************************************************************
					strLine1 = strLine1.replaceAll("\"", ""); // Agrega Carlos Godinez -> 27/06/2017

					/* Reemplaza el BOM de los archivos UTF8-BOM */
					if (strLine1.startsWith(UTF8_BOM)) {
						strLine1 = strLine1.substring(1);
					}

					System.out.println("Tamanio de linea" + strLine1.length());

					if (strLine1.trim().length() != 0) {

						// *********************************************************************************
						String delimitador = this.delimitador1;
						System.out.println("Linea: " + strLine1);
						System.out.println("Tamaï¿½o: " + strLine1.length());
						System.out.println("VALOR DE LA VARIABLE 'delimitador' = " + delimitador);
						if (delimitador.compareTo("n/a") == 0) {
							CBDataSinProcesarModel dataSinProcesarModel = new CBDataSinProcesarModel();
							// String fechaArchivo;
							// System.out
							// .println("QUE LLEVA LA LINEA ANTES DE EXTRAER
							// EL DIGITO: "
							// + strLine1);
							char aChar = strLine1.charAt(0);
							String cambio = "" + aChar;
							System.out.println("QUE TIENE EL CHAR: " + cambio);
							// int numero = Integer.parseInt(cambio);

							// ********************************************************************************
							// se quita temporalmente para validar que
							// agregue datos a la lista
							// && Character.isDigit(aChar) == true
							// los archivos sv siempre vienen con formato
							// D00001
							if (strLine1.length() > 8) {
								String[] pos = this.posiciones.split(",");
								String longit = this.tamanoDeCadena;
								String[] splitNomCl = this.nomenclatura.split(",");
								System.out.println("variable longit = " + longit);
								System.out.println("LONGITUD DE VARIABLE longit = " + strLine1.length() + "\n");
								if (strLine1.length() == Integer.parseInt(longit)) {

									System.out.println("longitud: " + longit);
									CBDataBancoModel bancoModel = new CBDataBancoModel();
									// CBDataBancoDAO cbd = new
									// CBDataBancoDAO();
									bancoModel.setcBCatalogoBancoId(idBanco);
									bancoModel.setcBCatalogoAgenciaId(idAgencia);
									bancoModel.setcBBancoAgenciaConfrontaId(idAgeConfro);
									bancoModel.setCreado_Por(user);
									bancoModel.setTipo(tipo);
									bancoModel.setFecha_Creacion(formato.format(fechaCreacion));
									bancoModel.setIdMaestroCarga(idMaestroCarga);
									bancoModel.setFecha(getFormatoFecha());
									// se cambia getFormatoFecha() por
									// fechaCreacion temporal para pruebas
									// temporalmente esta columna se mando a
									// la validacion del campo fecha
									// bancoModel.setDia(getFormatoFecha());
									bancoModel.setFormatofecha(getFormatoFecha());
									bancoModel.setComision(comisionConfronta);

									// obtiene las posiciones

									// ******************************************

									for (int countRec = 0; countRec < splitNomCl.length; countRec++) {
										int contador = 0;
										String posicion1;
										String posicion2;
										String ambasPosiciones = pos[countRec];

										String ambasPosReplace = ambasPosiciones.replace(" ", ",");
										String[] splitAmbasPosString = ambasPosReplace.split(",");

										posicion1 = splitAmbasPosString[contador];
										contador += 1;
										posicion2 = splitAmbasPosString[contador];
										String nomenCla = splitNomCl[countRec];

										/**
										 * Validacion temporal para omitir convenios diferentes a 001 y 002 Juankrlos
										 * --> 25/07/2017
										 * 
										 * Se omite validacion temporal CarlosGodinez -> 20/09/2018
										 */

										/*
										 * Agrega Juankrlos 21/07/2017 Convenios
										 */
										if ("CN".equals(nomenCla)) {

											String leeconvenio = strLine1.substring(Integer.parseInt(posicion1),
													Integer.parseInt(posicion2));
											System.out.println("Convenio: " + leeconvenio);
											int convenio = Integer.parseInt(leeconvenio);
											int ingresaconvenio = 0;
											if (convenio == 1) {
												ingresaconvenio = 2;
											} else if (convenio == 2) {
												ingresaconvenio = 1;
											} else {
												ingresaconvenio = convenio;
											}
											bancoModel.setTipo(String.valueOf(ingresaconvenio));

											bancoModel.setConvenioconf(true);
										}
										System.out.println("Valor que trae el convenio: " + bancoModel.getTipo());
										/**
										 * Commented by CarlosGodinez -> 20/09/2018 Se omite esta validacion para que
										 * pasen todos los convenios configurados en Costa Rica
										 */
										// Telefono
										if ("T".equals(nomenCla)) {
											String telefono = strLine1
													.substring(Integer.parseInt(posicion1), Integer.parseInt(posicion2))
													.trim();

											try {
												int entero = Integer.parseInt(telefono.trim());

												String telefono1 = ("" + entero).trim();
												System.out.println("TAMAÑO DEL TELEFONO: " + telefono1.length()
														+ " Telefono: " + telefono);
												if (telefono1.length() == 8) {
													bancoModel.setTelefono(telefono1.trim());
												} else {
													System.out.println("Codigo Cliente: " + telefono1);
													bancoModel.setCodCliente(telefono1.trim());
												}
											} catch (Exception e) {
												// TODO: handle exception
												System.out.println("Formato de telefono/codigo cliente erroneo.");
												bancoModel.setTelefono("");
												bancoModel.setCodCliente("");
											}
										}
										// }
										if ("C".equals(nomenCla)) {

											String telefono = strLine1
													.substring(Integer.parseInt(posicion1), Integer.parseInt(posicion2))
													.trim();
											int entero = Integer.parseInt(telefono.trim());
											String telefono1 = ("" + entero).trim();
											System.out.println("TAMAÑO DEL TELEFONO: " + telefono1.length()
													+ " Telefono: " + telefono);
											if (telefono1.length() == 8) {
												bancoModel.setTelefono(telefono1.trim());
											} else {
												System.out.println("Codigo Cliente: " + telefono1);
												bancoModel.setCodCliente(telefono1.trim());
											}
										}
										/**
										 * Added by CarlosGodinez -> 20/09/2018 Se agrega mapeo de nomenclatura TELEFONO
										 * ALFANUMERICO
										 */
										if ("TA".equals(nomenCla)) {
											String telefonoAlfaNum = strLine1
													.substring(Integer.parseInt(posicion1), Integer.parseInt(posicion2))
													.trim();
											if (telefonoAlfaNum.length() <= 10) {
												System.out.println("Telefono alfanumerico: " + telefonoAlfaNum);
												bancoModel.setTelefono(telefonoAlfaNum);
											}
										}
										/**
										 * FIN CarlosGodinez -> 20/09/2018
										 */
										// Monto
										if ("M".equals(nomenCla)) {
											/**
											 * Editado ultima vez por Carlos Godinez - Qitcorp - 28/08/2017
											 */
											String strMonto = strLine1.substring(Integer.parseInt(posicion1),
													Integer.parseInt(posicion2));
											strMonto = strMonto.replace("$", "");
											strMonto = strMonto.trim();
											System.out.println("** Monto antes de parsear: " + strMonto);

											if (strMonto.substring(0, 1).equals(".")
													|| strMonto.substring(0, 1).equals(",")) {
												strMonto = "0".concat(strMonto);
												System.out.println("Primer elemento: " + strMonto.substring(0, 1)
														+ " Nueva cadena: " + strMonto);
											}

											String cadena = currencyToBigDecimalFormat(strMonto);
											if (isBigDecimal(cadena)) {
												BigDecimal monto;
												if (cadena.indexOf(".") != -1) {
													System.out.println("viene con punto decimal con delimitador");
													monto = new BigDecimal(cadena);
												} else {
													System.out.println("viene sin punto decimal con delimitador");
													monto = new BigDecimal(cadena).divide(new BigDecimal(100));
												}

												System.out.println("** Monto parseado: " + monto);
												bancoModel.setMonto(monto);
											} else {
												System.out.println("Monto no valido");
											}
										}

										// Texto1
										if ("N".equals(nomenCla)) {

											String texto1 = strLine1.substring(Integer.parseInt(posicion1),
													Integer.parseInt(posicion2));
											System.out.println("Texto1: " + texto1);
											bancoModel.setTexto1(texto1.trim());

										}
										// Texto2
										if ("A".equals(nomenCla)) {
											String texto2 = strLine1.substring(Integer.parseInt(posicion1),
													Integer.parseInt(posicion2));
											System.out.println("Texto1: " + texto2);
											bancoModel.setTexto2(texto2.trim());
										}
										// Transaccion

										if ("O".equals(nomenCla)) {
											String transaccion = strLine1.substring(Integer.parseInt(posicion1),
													Integer.parseInt(posicion2));
											System.out.println("Transaccion: " + transaccion);
											bancoModel.setTransaccion(transaccion.trim());
										}
										if ("R".equals(nomenCla)) {
											String agencia = strLine1.substring(Integer.parseInt(posicion1),
													Integer.parseInt(posicion2));
											System.out.println("Agencia: " + agencia);
											bancoModel.setCbAgenciaVirfisCodigo(agencia.trim());
										}

										if ("D".equals(nomenCla)) {
											String fecha = strLine1.substring(Integer.parseInt(posicion1),
													Integer.parseInt(posicion2));
											System.out.println("Fecha: " + fecha);
											bancoModel.setFecha(fecha);
											bancoModel.setDia(fecha);
										}

									}
									if (isDate(bancoModel.getFecha(), formatoFechaConfronta)
											&& ((bancoModel.getTelefono() != null
													&& !bancoModel.getTelefono().equals(""))
													|| (bancoModel.getCodCliente() != null
															&& !bancoModel.getCodCliente().equals("")))) {

										dataBancoModels.add(bancoModel);
										System.out.println(
												"campo fecha: " + bancoModel.getFecha() + formatoFechaConfronta);
									} else {
										System.out.println("DATO QUE SE AGREGA A SIN PROCESAR = " + strLine);
										// CBDataSinProcesarModel dataSinProcesarModel = new CBDataSinProcesarModel();
										dataSinProcesarModel.setNombre_Archivo(nombreArchivo);
										dataSinProcesarModel.setData_Archivo(strLine);
										dataSinProcesarModel
												.setCausa("Numero de telefono o codigo de cliente invalido");
										dataSinProcesarModel.setEstado(1);
										dataSinProcesarModel.setCreado_Por(user);
										dataSinProcesarModel.setIdMaestroCarga(idMaestroCarga);
										// dataSinProcesarModel.setFechaCreacion(customDate.getMySQLDate());
										this.datosSinProcesar.add(dataSinProcesarModel);
										System.out.println("datos no procesados no lleva codigo de cliente telefono");
									}

								} else {
									// System.out
									// .println("No tiene los suficientes
									// caracteres: "
									// + strLine1);
									System.out.println("\nENTRA A VALIDACION DE REGISTROS SIN PROCESAR\n");

									dataSinProcesarModel.setNombre_Archivo(nombreArchivo);
									dataSinProcesarModel.setData_Archivo(strLine1);
									dataSinProcesarModel.setCausa(
											"Cantidad erronea de campos consulte la configuracion de la confronta");
									dataSinProcesarModel.setEstado(1);
									dataSinProcesarModel.setCreado_Por(user);
									dataSinProcesarModel.setIdMaestroCarga(idMaestroCarga);
									// dataSinProcesarModel.setFechaCreacion(customDate.getMySQLDate());
									this.datosSinProcesar.add(dataSinProcesarModel);
								}

							} else if (Character.isDigit(aChar) == true) {
								System.out.println("valida si es diguito: " + aChar);
								String cambioFecha = strLine1.toString();
								CustomDate customDate = new CustomDate();
								setFormatoFecha(customDate.getFormatFecha(cambioFecha.trim(), this.formatoFecha));
								// bancoModel.setFecha(fechaArchivo);
								// bancoModel.setDia(fechaArchivo);
								// Fecha

								// System.out.println("FECHA DEL ARCHIVO: "
								// + fechaArchivo);
								// dataBancoModels.add(bancoModel);
							} else {
								System.out.println("====> No agrega la info a la lista: ");
							}

						} else {
							strLine1 = strLine.trim();
							boolean isDataColumn = false;
							// char aChar = strLine1.charAt(0);
							// linea = strLine1.substring(0);
							if (strLine1.trim().length() != 0) {
								// strLine1 = strLine.trim();

								// linea = strLine1.substring(0);
								// System.out.println("Que lleva la linea: "
								// + linea);
								System.out.println("Delimitador para lectura1: " + this.delimitador1);
								System.out.println("Numero de columnas configuradas: " + this.cantidadAgrupacion);
								int cantAgrup = 0;
								String[] splitData = null;
								String[] splitDataValidos = null;

								if (nomenclatura.contains(Constants.COLUMN)) {
									splitData = getData(strLine1, this.delimitador1, 5);
									cantAgrup = this.cantidadAgrupacion - 2;
									splitDataValidos = new String[this.cantidadAgrupacion - 2];
									isDataColumn = true;

								} else if (nomenclatura.contains(Constants.NOM_FECHA_TIPO5)) {
									splitData = getDataFechaTipo5(strLine1, this.delimitador1);
									cantAgrup = this.cantidadAgrupacion;
									splitDataValidos = new String[cantAgrup];
								} else {
									splitData = strLine1.split("\\" + this.delimitador1);
									splitDataLenght = splitData.length;
									// Condicion editada por Carlos Godinez - Qitcorp - 22/05/2017
									splitDataValidos = new String[cantAgrup];
								}

								splitDataLenght = splitData.length;
								System.out.println("Numero de columnas del archivo: " + splitDataLenght);

								// Examina que el numero de columnas de la linea que se esta leyendo sea
								// mayor o igual a la cantidad de agrupacion configurada
								if (splitData.length >= this.cantidadAgrupacion) {
									for (int fila = 0; fila < this.cantidadAgrupacion; fila++) {
										splitDataValidos[fila] = splitData[fila];
									}
									if (splitDataValidos.length == this.cantidadAgrupacion) {
										// if (splitData.length ==
										// this.cantidadAgrupacion) {

										// System.out.println("Es digito TRUE: "
										// + Character.isDigit(aChar));
										// strLine.substring(0);
										// System.out.println("QUE LLEVA AQUI: "
										// + strLine1);
										System.out.println(
												"\n*** Cumple condiciï¿½n: splitDataValidos.length == this.cantidadAgrupacion ***\n");

										// Si la linea es valida, se pasan los
										// parametros a getBancoModel()
										/*
										 * getBancoModel(strLine1, idBanco, idAgencia, idConfronta, user, nombreArchivo,
										 * idMaestro, tipo, comision, getFormatoFecha(), idBancoAgenciaconfronta);
										 */
										getBancoModel(strLine1, idBanco, idAgencia, idConfronta, user, nombreArchivo,
												idMaestroCarga, tipo, comisionConfronta, getFormatoFecha(), idAgeConfro,
												formatoFechaConfronta, splitData, isDataColumn);

									} else {
										System.out.println(
												"\n*** NO Cumple condiciï¿½n: splitDataValidos.length == this.cantidadAgrupacion ***\n");
										CBDataSinProcesarModel dataSinProcesarModel = new CBDataSinProcesarModel();
										dataSinProcesarModel.setNombre_Archivo(nombreArchivo);
										dataSinProcesarModel.setData_Archivo(strLine1);
										dataSinProcesarModel.setCausa(
												"Cantidad erronea de campos consulte la configuracion de la confronta");
										dataSinProcesarModel.setEstado(1);
										dataSinProcesarModel.setCreado_Por(user);
										dataSinProcesarModel.setIdMaestroCarga(idMaestroCarga);
										// dataSinProcesarModel.setFechaCreacion(customDate.getMySQLDate());
										this.datosSinProcesar.add(dataSinProcesarModel);
									}
								} else {
									System.out.println(
											"\n*** NO Cumple condiciï¿½n: splitData.length >= this.cantidadAgrupacion ***\n");
									CBDataSinProcesarModel dataSinProcesarModel = new CBDataSinProcesarModel();
									dataSinProcesarModel.setNombre_Archivo(nombreArchivo);
									dataSinProcesarModel.setData_Archivo(strLine1);
									dataSinProcesarModel.setCausa(
											"Cantidad erronea de campos consulte la configuracion de la confronta");
									dataSinProcesarModel.setEstado(1);
									dataSinProcesarModel.setCreado_Por(user);
									dataSinProcesarModel.setIdMaestroCarga(idMaestroCarga);
									// dataSinProcesarModel.setFechaCreacion(customDate.getMySQLDate());
									this.datosSinProcesar.add(dataSinProcesarModel);
								}
							}
							// } else {
							// System.out.println("Es digito FALSE: "
							// + Character.isDigit(aChar));
							// strLine.substring(0);
							// System.out.println("QUE LLEVA AQUI: " +
							// aChar);
							// }

						}
					}
				}
				contLinea++;
			}

			/* Agregado por Nicolas Bermudez */
			// guarda datos
			if (getDataBancoModels().size() > 0) {
				String fechaV = "";
				fechaV = getDataBancoModels().get(0).getDia().toString();
				pa.setDia(fechaV);
				pa.setAgenciaId(idAgencia);
				try {
					if (cbdbdao.consultaExistenciaArchivo(nombreArchivo,
							getDataBancoModels().get(0).getIdMaestroCarga())) {
						System.out.println("*** ENTRA A CONSULTA EXISTENCIA ARCHIVO ***");
						cmc.borrarArchivo(pa.getIdMaestroCarga());
						pa.setIdMaestroCarga(null);
						dataBancoModels = new ArrayList<CBDataBancoModel>();

					} else if (cbdbdao.consultaExistenciaArchivoMultiple(nombreArchivo,
							getDataBancoModels().get(0).getIdMaestroCarga())) {
						System.out.println("*** ENTRA A CONSULTA EXISTENCIA ARCHIVO MULTIPLE ***");
						cmc.borrarArchivo(pa.getIdMaestroCarga());
						pa.setIdMaestroCarga(null);
						dataBancoModels = new ArrayList<CBDataBancoModel>();

					}
					/*
					 * else if (cbdbdao.verificaCargaDataBanco(fechaV, idBanco, idAgencia,
					 * idConfronta, this.formatoFecha)) { logger.
					 * info("ATENCION: Ya hay informacion de este dia cargada revisar el archivo " +
					 * nombreArchivo); System.out
					 * .println("ATENCION: Ya hay informacion de este dia cargada revisar el archivo "
					 * + nombreArchivo); System.out .println("archivo a borrar por fecha cargada: "
					 * + pa.getIdMaestroCarga()); cmc.borrarArchivo(pa.getIdMaestroCarga());
					 * pa.setIdMaestroCarga(null); dataBancoModels = new
					 * ArrayList<CBDataBancoModel>();
					 * 
					 * }
					 */
					else {
						System.out.println("VIENDO SI ENTRA A GRABAR");
						// Llama al metodo para borrar los registros de las
						// tablas
						// que tengan el nombreArchivo
						// cbdbdao.borrarArchivo(nombreArchivo, idMaestroCarga,
						// idBancoAgenciaconfronta,fechaV);
						// inserta procesados
						procesados = cbdbdao.insertaMasivos(getDataBancoModels(), this.formatoFecha);

						/* Verificando si hay multiples fechas */
						int dateConfrontas = cbdbdao.getDateConfronta(idMaestroCarga);

						/* Verificando si hay mas de un convenio */
						int confConfrontas = cbdbdao.getConfConfronta(idMaestroCarga);

						if (confConfrontas > 1) {
							System.out
									.println("*********** EJECUTA PROCESO DE SEPARACION DE CONVENIOS ****************");
							cbdbdao.ejecutaProcesoSeparacionTipoConfrontas(idMaestroCarga);
							System.out
									.println("*********** TERMINA PROCESO DE SEPARACION DE CONVENIOS ****************");
						} else {
							if (dateConfrontas > 1) {
								System.out.println(
										"*********** EJECUTA PROCESO DE SEPARACION DE FECHAS ****************");
								cbdbdao.ejecutaProcesoSeparacionFechasConfronta(idMaestroCarga);
								System.out.println(
										"*********** TERMINA PROCESO DE SEPARACION DE FECHAS ****************");
							} else {
								System.out.println("*********** EJECUTA PROCESO DE CONCILIACION ****************");
								// proceso de conciliacion
								cbdbdao.ejecutaProcesoConciliacion(idMaestroCarga);
							}
						}

						System.out.println("*********** EJECUTA PROCESO DE COMISIONES CONFRONTAS ****************");
						cbdbdao.ejecutaProcesoComisionesConfrontas(idMaestroCarga);

						dataBancoModels = new ArrayList<CBDataBancoModel>();
						if (getDatosSinProcesar().size() > 0) {
							noProcesados = cbdspdao.insertarMasivoNoProcesados(getDatosSinProcesar());

							datosSinProcesar = new ArrayList<CBDataSinProcesarModel>();
						}

					}
				} catch (Exception e) {
					System.out.println("error al verificar si existe la confronta: " + e.getMessage());
					logger.error("error al verificar si existe la confronta: " + e.getMessage());
				}
			} else {
				pa.setIdMaestroCarga(null);
			}

			logger.info(procesados + " Archivos grabados (Procesados) y " + noProcesados
					+ " Archivos grabados (Sin Procesar)");
			System.out.println(procesados + " Archivos grabados (Procesados) y " + noProcesados
					+ " Archivos grabados (Sin Procesar)");

		} catch (ArrayIndexOutOfBoundsException ex) {
			/*
			 * Messagebox.
			 * show("El numero de columnas del archivo es menor a la cantidad de agrupacion configurada. "
			 * + "\n\nNumero de columnas configuradas: " + this.cantidadAgrupacion +
			 * "\nNumero de columnas del archivo: " + splitDataLenght +
			 * "\n\nCambie la cantidad de agrupaciï¿½n de la confronta seleccionada en la pantalla de configuraciï¿½n de confrontas"
			 * , "ERROR", Messagebox.OK, Messagebox.EXCLAMATION);
			 */

			System.out.println("El numero de columnas del archivo es menor a la cantidad de agrupacion configurada. "
					+ "\n\nNumero de columnas configuradas: " + this.cantidadAgrupacion
					+ "\nNumero de columnas del archivo: " + splitDataLenght
					+ "\n\nCambie la cantidad de agrupaciï¿½n de la confronta seleccionada en la pantalla de configuraciï¿½n de confrontas");

			ex.printStackTrace();
		} catch (IOException ex) {
			// Logger.getLogger(ProcessFileTxtService.class.getName()).log(Level.SEVERE,
			// null, ex);
			System.out.println(ex.getMessage());
		}

	}

	private void getBancoModel(String strLine, int idBanco, int idAgencia, int idConfronta, String user,
			String nombreArchivo, String idMaestro, String tipo, BigDecimal comisionConfronta, String formatFecha,
			int idAgeConfro, String formatoFechaConfronta, String[] data, boolean isDataColumn) {

		CBDataSinProcesarModel dataSinProcesarModel = new CBDataSinProcesarModel();
		Date fechaCreacion = new Date();
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String[] splitData=null;
		String[] splitNomCl=null;
		
		if (ArrayUtils.isNotEmpty(data) && isDataColumn) {
			splitData = data;
			splitNomCl = ArrayUtils.removeAll(this.nomenclatura.split(","), 0, 1);

		} else {
			if (this.nomenclatura.contains(Constants.NOM_FECHA_TIPO5))
				splitData = data;
			else
				splitData = strLine.split("\\" + delimitador1);

			splitNomCl = this.nomenclatura.split(",");
		}
		// System.out.println("SPLITDATA: " + splitData.length);
		System.out.println("-----------------------------------");
		System.out.println("Nomenclatura: " + nomenclatura);
		System.out.println("splitData = " + splitData[0] + ".");
		System.out.println("splitData.length = " + splitData.length);
		System.out.println("cantidadAgrupacion = " + this.cantidadAgrupacion);
		System.out.println("-----------------------------------");
		String[] splitDataValidos = new String[this.cantidadAgrupacion];
		for (int fila = 0; fila < this.cantidadAgrupacion; fila++) {
			splitDataValidos[fila] = splitData[fila];
		}
		if (splitDataValidos.length == this.cantidadAgrupacion) {
			CBDataBancoModel bancoModel = new CBDataBancoModel();

			bancoModel.setcBCatalogoBancoId(idBanco);
			bancoModel.setcBCatalogoAgenciaId(idAgencia);
			bancoModel.setcBBancoAgenciaConfrontaId(idAgeConfro);
			bancoModel.setCreado_Por(user);
			bancoModel.setTipo(tipo);
			bancoModel.setFecha_Creacion(formato.format(fechaCreacion));
			bancoModel.setIdMaestroCarga(idMaestro);
			bancoModel.setFecha(getFormatoFecha());
			bancoModel.setComision(comisionConfronta);

			System.out.println("\nbanco agencia confronta usuario tipo fechaCreacion idMaestro comisionConfronta");
			System.out.println(idBanco + " " + idAgencia + " " + idConfronta + " " + user + " " + tipo + " "
					+ fechaCreacion + " " + idMaestro + " " + comisionConfronta + "\n");
			for (int countRec = 0; countRec < splitDataValidos.length; countRec++) {
				String valueToSave = splitNomCl[countRec];
				String strData = splitDataValidos[countRec];

				strData = strData.replaceAll("\"", ""); // Agrega Carlos Godinez -> 27/06/2017

				// Telefono Tipo 1
				if ("T".equals(valueToSave)) {
					System.out.println("\n##Valor que lleva en la nomeclatura T = " + strData.trim());
					String numeros = strData.trim();
					String numReplace = numeros.replace(".", ",");
					Object[] split = numReplace.split(",");

					Long telefono;
					String telstr = "";
					try {
						telefono = Long.parseLong((String) split[0]);
						telstr = "" + telefono;
					} catch (Exception e) {
						System.out.println("Numero vacio: " + e.getMessage());
						telstr = "";
					}

					if (telstr.length() == 8) {
						bancoModel.setTelefono(telstr.trim());
					} else {
						// Agrega Carlos Godinez - 12/09/2017
						if (telstr != null && !telstr.equals("") && !telstr.equals("0") && telstr.length() < 8) {
							System.out.println("Codigo Cliente asignado: " + telstr);
							bancoModel.setCodCliente(telstr.trim());
						}
						// FIN Carlos Godinez - 12/09/2017
					}
				}

				// Codigo - Transaccion
				if ("CT".equals(valueToSave)) {
					String numeros = strData.trim();

					int telefono;
					int transaccion;
					String telstr = "";
					String strtran = "";
					try {
						strtran = numeros.substring(0, numeros.length() - 10);
						telstr = numeros.substring(numeros.length() - 10, numeros.length());
						telefono = Integer.parseInt(strtran);
						transaccion = Integer.parseInt(telstr);
						telstr = String.valueOf(telefono);
						strtran = String.valueOf(transaccion);
					} catch (Exception e) {
						System.out.println("Numero vacio: " + e.getMessage());
						telstr = "";
					}

					if (telstr.length() == 8) {
						System.out.println("Telefono: " + telstr);
						bancoModel.setTelefono(telstr.trim());
					} else {
						System.out.println("Codigo Cliente: " + telstr);
						bancoModel.setCodCliente(telstr.trim());
					}
					System.out.println("Transaccion: " + strtran);
					bancoModel.setTransaccion(strtran);

					// Agrega Carlos Godinez - 12/09/2017
					if (bancoModel.getCodCliente() == null || bancoModel.getCodCliente().equals("")) {
						System.out.println("** Codigo de cliente va nulo, vacio o posee un valor de 0");
						System.out.println("** Se asigna primera cadena string obtenida a codigo de cliente");
						bancoModel.setCodCliente(numeros);
					}
					// FIN Carlos Godinez - 12/09/2017
				}

				if ("C".equals(valueToSave)) {
					System.out.println("\n## Valor que lleva en la nomeclatura C = " + strData.trim());
					String numeros = strData.trim();
					String numReplace = numeros.replace(".", ",");
					Object[] split = numReplace.split(",");
					int telefono;
					String telstr = "";
					try {
						telefono = Integer.parseInt((String) split[0]);
						telstr = "" + telefono;
					} catch (Exception e) {
						System.out.println("Numero vacio: " + e.getMessage());
						telstr = "";
					}

					if (telstr.length() == 8) {
						bancoModel.setTelefono(telstr.trim());
					} else {
						// Agrega Carlos Godinez - 12/09/2017
						if (telstr != null && !telstr.equals("") && !telstr.equals("0")) {
							System.out.println("Codigo Cliente asignado: " + telstr);
							bancoModel.setCodCliente(telstr.trim());
						}
						// FIN Carlos Godinez - 12/09/2017
					}
				}

				/**
				 * Added by CarlosGodinez -> 20/09/2018 Se agrega mapeo de nomenclatura TELEFONO
				 * ALFANUMERICO
				 */
				if ("TA".equals(valueToSave)) {
					String telefonoAlfaNum = strData.trim();
					if (telefonoAlfaNum.length() <= 10) {
						System.out.println("Telefono alfanumerico: " + telefonoAlfaNum);
						bancoModel.setTelefono(telefonoAlfaNum);
					}
				}
				/**
				 * FIN CarlosGodinez -> 20/09/2018
				 */

				if ("M".equals(valueToSave)) {
					// Editado por Carlos Godinez - Qitcorp - 24/08/2017
					strData = strData.replace("$", "");
					strData = strData.trim();
					System.out.println("** Monto antes de parsear = " + strData);

					if (strData.substring(0, 1).equals(".") || strData.substring(0, 1).equals(",")) {
						strData = "0".concat(strData);
						System.out.println("Primer elemento: " + strData.substring(0, 1) + " Nueva cadena: " + strData);
					}

					String cadena = currencyToBigDecimalFormat(strData);
					System.out.println("** Monto parseado = " + cadena);
					if (isBigDecimal(cadena)) {
						BigDecimal montoF = new BigDecimal(cadena);
						System.out.println("** Monto antes de guardar = " + montoF);
						bancoModel.setMonto(montoF);
					}
				}

				if ("MD".equals(valueToSave)) {
					// Editado por Carlos Godinez - Qitcorp - 24/08/2017
					strData = strData.replace("$", "");
					strData = strData.trim();
					System.out.println("** Monto antes de parsear = " + strData);
					String cadena = currencyToBigDecimalFormat(strData);
					System.out.println("** Monto parseado = " + cadena);
					if (isBigDecimal(cadena)) {
						BigDecimal montoF = new BigDecimal(cadena).divide(new BigDecimal(100));
						System.out.println("** Monto antes de guardar = " + montoF);
						bancoModel.setMonto(montoF);
					}
				}
				// FECHA TIPO1
				if ("D".equals(valueToSave)) {
					// Se quita este parseo y se deja el que tiene el insert con to_date
					bancoModel.setFecha(strData);
					bancoModel.setDia(strData);
				}
				// transaccion
				if ("O".equals(valueToSave)) {
					bancoModel.setTransaccion(strData.trim());
				}
				if ("H".equals(valueToSave)) {

					// se concatena la fecha obtenida anteriormente para cuando viene la hora
					// separada
					bancoModel.setFecha(bancoModel.getFecha() + " " + strData);
				}
				// FECHA TIPO3
				if ("P".equals(valueToSave)) {
					bancoModel.setFecha(bancoModel.getFecha() + " " + strData);
				}

				// FECHA TIPO4
				if ("DC".equals(valueToSave)) {

					String fecha4 = null;

					String string = strData;
					String[] parts = string.split("/");
					String part1 = parts[0];
					String part2 = parts[1];
					String part3 = parts[2];

					System.out.println("fecha tipo 5:" + part1);
					System.out.println("fecha tipo 51:" + part2);
					System.out.println("fecha tipo 52:" + part3);

					fecha4 = part2 + "/" + part1 + "/" + part3;

					// strData = strData.replace("am","");
					// strData = strData.replace("pm","");

					bancoModel.setFecha(fecha4);
					bancoModel.setDia(fecha4);
					System.out.println("fecha tipo 4:" + strData);
					System.out.println("fecha tipo 41:" + bancoModel.getDia());
					System.out.println("fecha tipo 42:" + bancoModel.getFecha());
				}

				if ("N".equals(valueToSave)) {
					bancoModel.setTexto1(strData.trim());
				}
				// texto dos
				if ("A".equals(valueToSave)) {
					bancoModel.setTexto2(strData.trim());
				}
				// Agencia Virtual/Fisica
				if ("R".equals(valueToSave)) {
					bancoModel.setCbAgenciaVirfisCodigo(strData.trim());
				}
				
				if (Constants.NOM_FECHA_TIPO5.equals(valueToSave)) {
					bancoModel.setFecha(strData);
					bancoModel.setDia(strData);
				}
			}

			if (isDate(bancoModel.getFecha(), formatoFechaConfronta)
					&& ((bancoModel.getTelefono() != null && !bancoModel.getTelefono().equals(""))
							|| (bancoModel.getCodCliente() != null && !bancoModel.getCodCliente().equals("")))) {

				dataBancoModels.add(bancoModel);
			} else {
				System.out.println("DATO QUE SE AGREGA A SIN PROCESAR = " + strLine);
				// CBDataSinProcesarModel dataSinProcesarModel = new CBDataSinProcesarModel();
				dataSinProcesarModel.setNombre_Archivo(nombreArchivo);
				dataSinProcesarModel.setData_Archivo(strLine);
				dataSinProcesarModel.setCausa("Numero de telefono o codigo de cliente invalido");
				dataSinProcesarModel.setEstado(1);
				dataSinProcesarModel.setCreado_Por(user);
				dataSinProcesarModel.setIdMaestroCarga(idMaestro);
				// dataSinProcesarModel.setFechaCreacion(customDate.getMySQLDate());
				this.datosSinProcesar.add(dataSinProcesarModel);

			}

		} else {
			System.out.println("DATO QUE SE AGREGA A SIN PROCESAR = " + strLine);
			// CBDataSinProcesarModel dataSinProcesarModel = new CBDataSinProcesarModel();
			dataSinProcesarModel.setNombre_Archivo(nombreArchivo);
			dataSinProcesarModel.setData_Archivo(strLine);
			dataSinProcesarModel.setCausa("Cantidad erronea de campos consulte la configuracion de la confronta");
			dataSinProcesarModel.setEstado(1);
			dataSinProcesarModel.setCreado_Por(user);
			dataSinProcesarModel.setIdMaestroCarga(idMaestro);
			// dataSinProcesarModel.setFechaCreacion(customDate.getMySQLDate());
			this.datosSinProcesar.add(dataSinProcesarModel);
		}
		// return bancoModel;
	}

	// obtiene la configuracion para las confrontas
	private void getConfrontaConf(int idConfronta) {
		/*
		 * System.out.println("id lista: " + idConfronta); if (listadoConfConfronta ==
		 * null) { listadoConfConfronta = new CBConfiguracionConfrontaDAO(); }
		 * List<CBConfiguracionConfrontaModel> dataList = listadoConfConfronta
		 * .obtieneListadoConfConfronta(idConfronta); for (CBConfiguracionConfrontaModel
		 * listadoConfrontas : dataList) {
		 * setDelimitador1(listadoConfrontas.getDelimitador1());
		 * setDelimitador2(listadoConfrontas.getDelimitador2());
		 * setCantidadAgrupacion(listadoConfrontas.getCantidad_Agrupacion());
		 * setNomenclatura(listadoConfrontas.getNomenclatura());
		 * setFormatoFecha(listadoConfrontas.getFormato_Fecha());
		 * setPosiciones(listadoConfrontas.getPosiciones());
		 * setTamanoDeCadena(listadoConfrontas.getLongitudCadena());
		 * System.out.println("cantidad agrupacion: " +
		 * listadoConfrontas.getCantidad_Agrupacion());
		 * System.out.println("AGRUPACION DEL ARCHIVO: " + this.cantidadAgrupacion);
		 * System.out.println("DELIMITADOR: " + this.delimitador1); }
		 */

		CBConfiguracionConfrontaDAO cccb = new CBConfiguracionConfrontaDAO();
		// System.out.println("id lista: " + idConfronta);
		List<CBConfiguracionConfrontaModel> dataList = cccb.obtieneListadoConfConfronta(idConfronta);
		int sizeList = dataList.size();
		// System.out.println("tama;o lista: " + sizeList);
		for (int countRec = 0; sizeList > countRec; countRec++) {
			CBConfiguracionConfrontaModel confrontaModel = (CBConfiguracionConfrontaModel) dataList.get(countRec);
			this.delimitador1 = confrontaModel.getDelimitador1();
			this.delimitador2 = confrontaModel.getDelimitador2();
			System.out.println("cantidad agrupacion: " + confrontaModel.getCantidad_Agrupacion());
			this.cantidadAgrupacion = confrontaModel.getCantidad_Agrupacion();
			// System.out.println("AGRUPACION DEL ARCHIVO: "
			// + this.cantidadAgrupacion);
			this.nomenclatura = confrontaModel.getNomenclatura();
			this.formatoFecha = confrontaModel.getFormato_Fecha();

			// valores para cuando la cadena no trae delimitadores
			this.posiciones = confrontaModel.getPosiciones();
			this.tamanoDeCadena = confrontaModel.getLongitudCadena();
			// System.out.println("DELIMITADOR: "
			// + confrontaModel.getDelimitador1());

		}

	}

	/**
	 * Agregado por Juan Carlos - 12/07/2017
	 * 
	 */

	public boolean isNumeric(String cadena) {
		try {
			double numero = Double.parseDouble(cadena);
			System.out.println("** Monto parseado = " + numero);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public boolean isBigDecimal(String cadena) {
		try {
			BigDecimal number = new BigDecimal(cadena);
			System.out.println("** Monto parseado = " + number);
		} catch (Exception nfe) {
			return false;
		}
		return true;
	}

	public static String currencyToBigDecimalFormat(String currency) {
		try {
			// Reemplazar todos los puntos decimales por comas
			currency = currency.replaceAll("\\.", ",");

			// Si el monto lleva decimales, el separador debe ser .
			if (currency.length() >= 3) {
				char[] chars = currency.toCharArray();
				if (chars[chars.length - 2] == ',') {
					chars[chars.length - 2] = '.';
				} else if (chars[chars.length - 3] == ',') {
					chars[chars.length - 3] = '.';
				}
				currency = new String(chars);
			}

			// Remover todas las comas
			return currency.replaceAll(",", "");
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Validamos si el string enviado es fecha
	 */
	public boolean isDate(String fecha, String formatoDeseado) {
		System.out.println("======== ENTRA A PARSEO DE FECHA ENVIADA ========");
		Date fec = null;
		try {
			System.out.println("Formato fecha para Oracle = " + formatoDeseado);
			System.out.println("Fecha enviada = " + fecha);
			formatoDeseado = formatoDeseado.replace("hh24", "HH");
			formatoDeseado = formatoDeseado.replace("HH24", "HH");
			formatoDeseado = formatoDeseado.replace("mi", "mm");
			formatoDeseado = formatoDeseado.replace("MI", "mm");
			formatoDeseado = formatoDeseado.replace("am", "a"); // CarlosGodinez -> 30/08/2017
			formatoDeseado = formatoDeseado.replace("am", "a"); // CarlosGodinez -> 30/08/2017
			formatoDeseado = formatoDeseado.replace("AM", "a"); // CarlosGodinez -> 30/08/2017
			formatoDeseado = formatoDeseado.replace("a.m.", "a"); // CarlosGodinez -> 30/08/2017

			System.out.println("Formato fecha para Java = " + formatoDeseado);
			SimpleDateFormat format = new SimpleDateFormat(formatoDeseado, Locale.US);
			format.setTimeZone(TimeZone.getTimeZone("UTC"));

			DateFormatSymbols symbols = format.getDateFormatSymbols();
			symbols = (DateFormatSymbols) symbols.clone();
			symbols.setAmPmStrings(new String[] { "a.m.", "p.m.", "AM", "PM" });
			format.setDateFormatSymbols(symbols);

			fec = format.parse(fecha);
			System.out.println("Fecha parseada con exito = " + fec);
			System.out.println("======== FIN DE PARSEO DE FECHA =========");
			return true;
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			System.out.println("fecha con error: " + fecha);
			System.out.println("======== FIN DE PARSEO DE FECHA =========");
			return false;
		} catch (NullPointerException e) {
			System.out.println(e.getMessage());
			System.out.println("fecha null: " + fecha);
			System.out.println("======== FIN DE PARSEO DE FECHA =========");
			return false;
		}
	}

	private String[] getData(String cadena, String delimitador, int posicion) {
		logger.debug("getData() -  inicia obtener Data ");
		String[] fistData = cadena.split("\\" + delimitador + "{1,}", posicion);
		String[] lastData = null;
		String[] dataFinal = null;
		String dataLast = null;
		if (fistData.length > 0) {
			dataLast = getEndData(fistData[posicion - 1]);
			lastData = dataLast.split("-");
			dataFinal = oderData(ArrayUtils.addAll(ArrayUtils.remove(fistData, fistData.length - 1), lastData));
		}
		for (int i = 0; i < dataFinal.length; i++) {
			logger.debug("getData() - Datos ------>> " + dataFinal[i]);
		}

		return dataFinal;
	}

	private String[] oderData(String[] allData) {
		String fecha = "";
		fecha = fecha.concat(allData[0]).concat(" ").concat(allData[1]).trim();
		String[] depurateData = ArrayUtils.removeAll(allData, 0, 1);
		depurateData = ArrayUtils.addFirst(depurateData, fecha);
		for (int i = 0; i < depurateData.length; i++) {
			logger.debug("getData() - Datos ORDENADOS ------>> " + depurateData[i]);
		}
		return depurateData;
	}

	private String getEndData(String data) {
		// TODO Auto-generated method stub
		logger.debug("getEndData() - inicia obtener endData");
		String datosEnd = "";
		int beginIndex = 0;
		beginIndex = searchDigit(data);
		int endIndex = 0;
		char[] cadena = data.toCharArray();
		String str1 = "";
		String str2 = "";
		String str3 = "";
		for (int i = 0; i < cadena.length; i++) {
			if (Character.isDigit(cadena[i])) {
				str2 += cadena[i];
				endIndex = endIndex + 1;
			}
		}
		str1 = data.substring(0, beginIndex - 1).trim();
		str3 = data.substring(beginIndex + endIndex).trim();
		datosEnd = str1.concat("-").concat(str2).concat("-").concat(str3);
		return datosEnd;
	}

	private int searchDigit(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (Character.isDigit(s.trim().charAt(i)) == true) {
				return i + 1;
			}
		}
		return 0;
	}

	private String[] getDataFechaTipo5(String cadena, String delimitador) {
		String methodName = "getDataFechaTipo5()";
		logger.debug(methodName + " - Inicia obtener Data para nomenclatura fecha tipo5");
		String[] firstData = cadena.split("\\" + delimitador);
		String[] lastData = new String[firstData.length];
		if (firstData.length > 0) {
			int posicion = 0;
			for (String s : firstData) {
				if (StringUtils.isNumeric(s)) {
					logger.debug(methodName + " - Depuracion data : " + s);
					if ((posicion == 1 || posicion == 2)) {
						s = s.replaceAll("^0+", "");
						if (s.isEmpty())
							continue;
					}
					if (posicion == firstData.length - 3) {
						if (Integer.parseInt(s.substring(s.length() - 1)) == 0)
							s = s.substring(0, s.length() - 1);

						logger.debug(methodName + " - Depuracion data fecha : " + s);
					}
				}
				lastData[posicion] = s;
				posicion = posicion + 1;
			}

			lastData = ArrayUtils.remove(lastData, lastData.length - 1);

		}

		return lastData;
	}

	/**
	 * @return the listadoConfConfronta
	 */
	public CBConfiguracionConfrontaDAO getListadoConfConfronta() {
		return listadoConfConfronta;
	}

	/**
	 * @param listadoConfConfronta the listadoConfConfronta to set
	 */
	public void setListadoConfConfronta(CBConfiguracionConfrontaDAO listadoConfConfronta) {
		this.listadoConfConfronta = listadoConfConfronta;
	}

	/**
	 * @return the delimitador1
	 */
	public String getDelimitador1() {
		return delimitador1;
	}

	/**
	 * @param delimitador1 the delimitador1 to set
	 */
	public void setDelimitador1(String delimitador1) {
		this.delimitador1 = delimitador1;
	}

	/**
	 * @return the delimitador2
	 */
	public String getDelimitador2() {
		return delimitador2;
	}

	/**
	 * @param delimitador2 the delimitador2 to set
	 */
	public void setDelimitador2(String delimitador2) {
		this.delimitador2 = delimitador2;
	}

	/**
	 * @return the cantidadAgrupacion
	 */
	public int getCantidadAgrupacion() {
		return cantidadAgrupacion;
	}

	/**
	 * @param cantidadAgrupacion the cantidadAgrupacion to set
	 */
	public void setCantidadAgrupacion(int cantidadAgrupacion) {
		this.cantidadAgrupacion = cantidadAgrupacion;
	}

	/**
	 * @return the nomenclatura
	 */
	public String getNomenclatura() {
		return nomenclatura;
	}

	/**
	 * @param nomenclatura the nomenclatura to set
	 */
	public void setNomenclatura(String nomenclatura) {
		this.nomenclatura = nomenclatura;
	}

	/**
	 * @return the formatoFecha
	 */
	public String getFormatoFecha() {
		return formatoFecha;
	}

	/**
	 * @param formatoFecha the formatoFecha to set
	 */
	public void setFormatoFecha(String formatoFecha) {
		this.formatoFecha = formatoFecha;
	}

	public List<CBDataBancoModel> getDataBancoModels() {
		return dataBancoModels;
	}

	public void setDataBancoModels(List<CBDataBancoModel> dataBancoModels) {
		this.dataBancoModels = dataBancoModels;
	}

	public List<CBDataSinProcesarModel> getDatosSinProcesar() {
		return datosSinProcesar;
	}

	public void setDatosSinProcesar(List<CBDataSinProcesarModel> datosSinProcesar) {
		this.datosSinProcesar = datosSinProcesar;
	}

	public String getFechaArchivo() {
		return fechaArchivo;
	}

	public void setFechaArchivo(String fechaArchivo) {
		this.fechaArchivo = fechaArchivo;
	}

	public String getPosiciones() {
		return posiciones;
	}

	public void setPosiciones(String posiciones) {
		this.posiciones = posiciones;
	}

	public String getTamanoDeCadena() {
		return tamanoDeCadena;
	}

	public void setTamanoDeCadena(String tamanoDeCadena) {
		this.tamanoDeCadena = tamanoDeCadena;
	}

}
