package com.terium.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;

import com.terium.modelos.CBDataBancoModel;
import com.terium.utils.DBUtils;

public class CBDataBancoDAO {
	private static final Logger logger = Logger.getLogger(CBDataBancoDAO.class);
	

	// inserta datos de archivos masivamente
	public int insertaMasivos(List<CBDataBancoModel> registros, String formatFecha) {
		
		//Se cambia el query para que se lea el formato enviado por la configuracion
		String INSERTA_MASIVOS_BANCO = "INSERT " + " INTO CB_DATA_BANCO "
				+ "   ( " + "     cBDataBancoId, " + "     cod_Cliente, "
				+ "     telefono, " + "     tipo, " + "     fecha, "
				+ "     cBCatalogoBancoId, " + "     cBCatalogoAgenciaId, "
				+ "     cBBancoAgenciaConfrontaId, " + "     monto, "
				+ "     transaccion, " + "     estado, " + "     mes, "
				+ "     texto1, " + "     texto2, " + "     creado_Por, "
				+ "     modificado_Por, " + "     fecha_Creacion, "
				+ "     fecha_Modificacion, id_archivos_insertados, " + " dia, " 
				+ " 	codigo "
				+ "  ) " + "   VALUES " + "   ( "
				+ "     cb_data_banco_sq.nextval, " + "     ?, " + "     ?, "
				+ "     ?, " + "     to_date(?, '"+formatFecha+"'), "
				+ "     ?, " + "     ?, " + "     ?, " + "     ?, " + "     ?, "
				+ "     ?, " + "     ?, " + "     ?, " + "     ?, " + "     ?, "
				+ "     ?, " + "     sysdate, "
				+ "     to_date(?, '"+formatFecha+"'), " + " ?, "
				+ "  trunc(to_date(?, '"+formatFecha+"'), 'dd'), " 
				+ "		? " + " )";
		
		int res = 0;
		try {
			Connection con = DBUtils.getConnection();
			try {
				QueryRunner qr = new QueryRunner();
				List<Object[]> dataList = new ArrayList<Object[]>(
						registros.size());
				Object[] param;
				Object[][] dataObj = new Object[registros.size()][18];
				for (CBDataBancoModel registro : registros) {
					param = new Object[] { registro.getCodCliente(),
							registro.getTelefono(), registro.getTipo(),
							registro.getFecha(),
							registro.getcBCatalogoBancoId(),
							registro.getcBCatalogoAgenciaId(),
							registro.getcBBancoAgenciaConfrontaId(),
							registro.getMonto(), registro.getTransaccion(),
							registro.getEstado(), registro.getMes(),
							registro.getTexto1(), registro.getTexto2(),
							registro.getCreado_Por(),
							registro.getModificado_Por(),
							registro.getFecha_Modificacion(),
							registro.getIdMaestroCarga(), registro.getDia(),
							registro.getCodigoAgencia()};
					dataList.add(param);
				}
				dataObj = dataList.toArray(dataObj);
				int[] objRet = qr.batch(con, INSERTA_MASIVOS_BANCO, dataObj);
				res = objRet.length;
				System.out.println("cantidad insertada: " + res);
				return res;

			} finally {
				con.close();
			}
		} catch (Exception e) {
			System.out.println("error al insertar masivamente: "
					+ e.getMessage());
			logger.error("error al insertar masivamente: " + e.getMessage());
		}
		return res;
	}


	// Realiza la consulta por medio del nombre para obtener el id maestro de
	// ese archivo
	private static String CONSULTA_ARCHIVO = "Select id_archivos_insertados idArchivosViejos, nombre_archivo nombreArchivo from cb_archivos_insertados "
			+ " where NOMBRE_ARCHIVO = ? and id_archivos_insertados !=  ?";
	
	private static String CONSULTA_ARCHIVO_SIMPLE = "Select id_archivos_insertados idArchivosViejos, nombre_archivo nombreArchivo "
			+ "from cb_archivos_insertados where 1=1";
	// Borra todos los registros de la tabla concilaciones del archivo
	// selecionado
	private String BORRAR_CONCILACIONES = "DELETE "
			+ " cb_conciliacion "
			+ " WHERE cbdatabancoid IN "
			+ " (SELECT cbdatabancoid FROM cb_data_banco WHERE ID_ARCHIVOS_INSERTADOS = ? "
			+ " ) ";
	// Borra el archivo con el id maestro
	private static String BORRAR_ARCHIVO = "Delete cb_archivos_insertados "
			+ " where id_archivos_insertados != ? and nombre_archivo = ? ";
	// Borra los registros con el id maestro
	private static String BORRAR_REGISTRO = "Delete cb_data_banco "
			+ " where id_archivos_insertados = ?";
	// Borra los registros que no proceso
	private static String BORRAR_NO_REGISTRADOS = "Delete cb_data_sin_procesar "
			+ " where id_archivos_insertados = ?";
	// elimina registros pendientes
	private String BORRA_CONCILIACIONES_PENDIENTES = "delete "
			+ " FROM cb_conciliacion " + " WHERE tipo                    = 1 "
			+ " AND cbbancoagenciaconfrontaid = ? "
			+ " AND dia                       = TO_DATE(?, 'dd/MM/yy') "
			+ " AND (cbpagosid               IS  NULL "
			+ " OR cbdatabancoid             IS  NULL)";

	// consuta si el archivo existe y borra el archivo y los registros
	public void borrarArchivo(String nombre, String idArchivo,
			int bancoAgenciaConfronta, String fechaConciliacion)
			throws ParseException {
		DateFormat df = new SimpleDateFormat("dd/MM/yy");
		fechaConciliacion = df.format(df.parse(fechaConciliacion));
		PreparedStatement ps = null;
		ResultSet rs = null;
		System.out.println("1");
		try {
			Connection con = DBUtils.getConnection();
			System.out.println("2");
			try {
				System.out.println("3");
				ps = con.prepareStatement(CONSULTA_ARCHIVO);
				ps.setString(1, nombre);
				ps.setString(2, idArchivo);

				rs = ps.executeQuery();
				System.out.println("4");
				while (rs.next()) {
					// priemero limpia conciliaciones
					String resultado = rs.getString("idArchivosViejos");
					System.out.println("FECHA CONCILIACION: "
							+ fechaConciliacion);
					System.out.println("ID CONCILIACIONES PENDIENTES: "
							+ bancoAgenciaConfronta);
					System.out.println("id del archivo a eliminar: "
							+ resultado);

					System.out.println("Elimina conciliaciones...");
					ps = con.prepareStatement(BORRAR_CONCILACIONES);
					ps.setString(1, resultado);
					ps.executeQuery();
					System.out.println("Las Concilaciones fueron borradas");
					logger.info("Las Concilaciones fueron borradas");
					System.out.println("5");
					// *****************************************************************

					// AGREGADO PARA ELIMINACION DE RESITROS VIEJOS
					System.out.println("Elimina conciliaciones pendientes...");
					ps = con.prepareStatement(BORRA_CONCILIACIONES_PENDIENTES);
					ps.setInt(1, bancoAgenciaConfronta);
					ps.setString(2, fechaConciliacion);
					ps.executeQuery();
					System.out
							.println("las conciliaciones pendientes fueron borradas...");
					logger.info("Las Concilaciones pendientes fueron borradas");
					System.out.println("6");
					// ********************************************************************

					// segundo limpia cb_data_banco
					System.out.println("Elimina data banco...");
					ps = con.prepareStatement(BORRAR_REGISTRO);
					ps.setString(1, resultado);
					ps.executeQuery();
					System.out.println("Los Registros Fueron Borrados");
					logger.info("Los Registros Fueron Borrados");
					System.out.println("7");
					// *****************************************************************

					// tercero limpia data no registrada
					System.out.println("Elimina datos no registrados...");
					ps = con.prepareStatement(BORRAR_NO_REGISTRADOS);
					ps.setString(1, resultado);
					ps.executeQuery();
					System.out
							.println("Los Registros no Procesados Fueron Borrados");
					logger.info("Los Registros no Procesados Fueron Borrados");
					System.out.println("8");
					// ******************************************************************

					// cuarto limpia
					System.out.println("Elimina los archivos anteriores :)...");
					ps = con.prepareStatement(BORRAR_ARCHIVO);
					ps.setString(1, idArchivo);
					ps.setString(2, nombre);
					ps.executeQuery();
					System.out.println("El Archivo fue Borrado");
					logger.info("El Archivo fue Borrado");
					System.out.println("9");
				}
			} finally {
				con.close();
			}

		} catch (Exception e) {
			System.out.println("Error al consultar la Base: " + e.getMessage());
			logger.error("Error al consultar la Base: " + e.getMessage());
		}
	}

	private static String EJECUTA_PROCESO_CB_CONCILIACION_SP = "{call CB_CONCILIACION_SP(?)}";

	public void ejecutaProcesoConciliacion(String idMaestroCarga) {
		System.out.println("IDMAESTRO: " + idMaestroCarga);
		if (idMaestroCarga == null) {
			System.out.println("No se cargo ningun archivo");
		} else {
			CallableStatement callableStatement = null;
			try {
				Connection con = DBUtils.getConnection();
				try {
					System.out.println("Inicia proceso de conciliacion");
					logger.info("Inicia proceso de conciliacion");
					callableStatement = con
							.prepareCall(EJECUTA_PROCESO_CB_CONCILIACION_SP);
					callableStatement.setInt(1,
							Integer.parseInt(idMaestroCarga));
					callableStatement.execute();
					System.out
							.println("A finalizado el proceso de conciliacion");
					logger.info("A finalizado el proceso de conciliacion");
				} finally {
					con.close();
				}
			} catch (Exception e) {
				System.out
						.println("error al ejecutar el proceso de conciliacion: "
								+ e.getMessage());
				logger.error("error al ejecutar el proceso de conciliacion: "
						+ e.getMessage());
			}
		}
		
	}
	
	/*
	 * Agrega sp de comisiones confrontas Ovidio Santos 27/09/2018
	 */
	
	private static String CALL_CB_COMISIONES_CONFRONTAS_SP = "{call cb_comision_confronta_sp(?)}";

	public void ejecutaProcesoComisionesConfrontas(String idMaestroCarga) {
		System.out.println("IDMAESTRO: " + idMaestroCarga);
		if (idMaestroCarga == null) {
			System.out.println("No se cargo ningun archivo");
		} else {
			CallableStatement callableStatement = null;
			try {
				Connection con = DBUtils.getConnection();
				try {
					System.out.println("Inicia proceso de conciliacion");
					logger.info("Inicia proceso de comisiones confrontas");
					callableStatement = con
							.prepareCall(CALL_CB_COMISIONES_CONFRONTAS_SP);
					callableStatement.setInt(1,
							Integer.parseInt(idMaestroCarga));
					callableStatement.execute();
					System.out
							.println("A finalizado el proceso de comisiones confrontas");
					logger.info("A finalizado el proceso de comisiones confrontas");
				} finally {
					con.close();
				}
			} catch (Exception e) {
				System.out
						.println("error al ejecutar el proceso de comisiones confrontas: "
								+ e.getMessage());
				logger.error("error al ejecutar el proceso de comisiones confrontas: "
						+ e.getMessage());
			}
		}
		
	}
	
	

	// Verifica si la fecha que trae la primera linea del archivo ya a sido
	// cargada
	
	public boolean verificaCargaDataBanco(String fechaVerificar, int banco,
			int agencia, int confronta, String formato) {
		
		String VERIFICA_CARGA_DATA_BANCO = "SELECT * FROM CB_DATA_BANCO "
				+ " WHERE TO_CHAR(DIA, '"+formato+"') = ? "
				+ " AND CBCATALOGOBANCOID = ? " + " AND CBCATALOGOAGENCIAID = ? "
				+ " AND CBBANCOAGENCIACONFRONTAID = ?";

		boolean verifica = false;
		PreparedStatement ps = null;
		ResultSet rs = null;
		System.out.println("FECHA A VERIFICAR: " + fechaVerificar);
		System.out.println("BANCO A VERIFICAR: " + banco);
		System.out.println("AGENCIA A VERIFICAR: " + agencia);
		System.out.println("CONFRONTA A VERIFICAR: " + confronta);
		try {
			Connection con = DBUtils.getConnection();
			try {
				ps = con.prepareStatement(VERIFICA_CARGA_DATA_BANCO);
				ps.setString(1, fechaVerificar);
				ps.setInt(2, banco);
				ps.setInt(3, agencia);
				ps.setInt(4, confronta);
				rs = ps.executeQuery();
				if (rs.next()) {
					verifica = true;
				}
			} finally {
				con.close();
			}
		} catch (Exception e) {
			System.out
					.println("error al verificar carga de archivo en Data Banco: "
							+ e.getMessage());
			logger.error("error al verificar carga de archivo en Data Banco: "
					+ e.getMessage());
			verifica = false;
		}
		return verifica;
	}

	// Manda a llamar al proceso que envia los correos de las entidades que se
	// conciliaron
	private String EJECUTA_CB_ENVIA_CORREO_CONCILIACION = "{call CB_ENVIA_CORREO_CONCILIACION(?)}";

	public void ejecutaCbEnviaCorreoConciliacion(String idMaestro) {
		if (idMaestro == null || idMaestro.equals("")) {
			System.out.println("No se cargo ningun archivo");
		} else {
			System.out.println("id's a enviar: " + idMaestro);
			CallableStatement callableStatement = null;
			try {
				Connection con = DBUtils.getConnection();
				try {
					System.out.println("Inicia proceso de envio de correo");
					logger.info("Inicia proceso de envio de correo");
					callableStatement = con
							.prepareCall(EJECUTA_CB_ENVIA_CORREO_CONCILIACION);
					callableStatement.setString(1, idMaestro);
					callableStatement.execute();
					System.out
							.println("A finalizado el proceso de envio de correo");
					logger.info("A finalizado el proceso de envio de correo");
				} finally {
					con.close();
				}
			} catch (Exception e) {
				System.out
						.println("error al ejecutar el proceso CB_ENVIA_CORREO_CONCILIACION: "
								+ e.getMessage());
				logger.error("error al ejecutar el proceso CB_ENVIA_CORREO_CONCILIACION: "
						+ e.getMessage());
			}
		}
	}

	// Lllamada al proceso de ajustes pendientes
	private static String EJECUTA_PROCESO_CB_AJUSTES_PENDIENTES_SP = "{call CB_AJUSTES_PENDIENTES(?, ?)}";

	public void ejecutaProcesoAjustesPendientes(String fecha, int agencia) {
		System.out.println("FECHA: " + fecha);
		System.out.println("AGENCIA: " + agencia);
		CallableStatement callableStatement = null;
		try {
			Connection con = DBUtils.getConnection();
			try {
				System.out.println("Inicia proceso de ajustes pendientes");
				logger.info("Inicia proceso de ajustes pendientes");
				callableStatement = con
						.prepareCall(EJECUTA_PROCESO_CB_AJUSTES_PENDIENTES_SP);
				callableStatement.setString(1, fecha);
				callableStatement.setInt(2, agencia);
				callableStatement.execute();
				System.out
						.println("A finalizado el proceso de ajustes pendientes");
				logger.info("A finalizado el proceso de ajustes pendientes");
			} finally {
				con.close();
			}
		} catch (Exception e) {
			System.out
					.println("error al ejecutar el proceso de ajustes pendientes: "
							+ e.getMessage());
			logger.error("error al ejecutar el proceso de ajustes pendientes: "
					+ e.getMessage());
		}
	}

	// Lllamada al proceso de ajustes
	private static String EJECUTA_PROCESO_CB_AJUSTES_SP = "{call CB_AJUSTES(?, ?)}";

	public void ejecutaProcesoAjustes(String fecha, int agencia) {
		System.out.println("FECHA: " + fecha);
		System.out.println("AGENCIA: " + agencia);
		CallableStatement callableStatement = null;
		try {
			Connection con = DBUtils.getConnection();
			try {
				System.out.println("Inicia proceso de ajustes");
				logger.info("Inicia proceso de ajustes");
				callableStatement = con
						.prepareCall(EJECUTA_PROCESO_CB_AJUSTES_SP);
				callableStatement.setString(1, fecha);
				callableStatement.setInt(2, agencia);
				callableStatement.execute();
				System.out.println("A finalizado el proceso de ajustes");
				logger.info("A finalizado el proceso de ajustes");
			} finally {
				con.close();
			}
		} catch (Exception e) {
			System.out.println("error al ejecutar el proceso de ajustes: "
					+ e.getMessage());
			logger.error("error al ejecutar el proceso de ajustes: "
					+ e.getMessage());
		}
	}
	
private String QRY_LINEA_LECTURA = "SELECT NVL(LINEA_LECTURA, 0) linea FROM CB_CONFIGURACION_CONFRONTA WHERE CBCONFIGURACIONCONFRONTAID = ?";
	
	public int obtenerLineaLectura(int id) {
		int valor = 0;
		Connection conn = null;
		try {
			conn = DBUtils.getConnection();
			try {
				System.out.println("\n======== Obtiene linea de inicio de lectura de archivo ========\n");
				System.out.println("Id configuracion confronta enviado = " + id);
				System.out.println("Consulta para obtener linea donde se empezara a leer = " + QRY_LINEA_LECTURA);
				PreparedStatement cmd = conn.prepareStatement(QRY_LINEA_LECTURA);
				cmd.setInt(1, id);
				ResultSet rs = cmd.executeQuery();
				while (rs.next()) {
					valor = rs.getInt(1);
				}
				cmd.close();
			} catch (SQLException e) {
				System.out.println("Ha ocurrido un error: " + e.getMessage());
				e.printStackTrace();
			} finally {
				conn.close();
			}
		} catch (Exception e) {
			System.out.println("Ha ocurrido un error: " + e.getMessage());
			e.printStackTrace();
			try {
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} 
		System.out.println("** Linea donde se empezara a leer: " + valor);
		System.out.println("\n===============================================================\n");
		return valor;
	}
	
	public boolean consultaExistenciaArchivo(String archivo,
			String idArchivoNuevo) {
		boolean valor = false;
		Connection con = null;
		Statement ps = null;
		ResultSet rs = null;
		try {
			con = DBUtils.getConnection();			
			List<CBDataBancoModel> listadoConfig = new ArrayList<CBDataBancoModel>();
			String valor1 = "", valor2 = "", valor3 = "", where = "";
			
			System.out.println("Nombre del archivo a verificar: " + archivo);
			System.out.println("Id del archivo insertado: "+idArchivoNuevo);
			
			where += " AND id_archivos_insertados !=  '" + idArchivoNuevo + "' AND NOMBRE_ARCHIVO = '" + archivo + "'";
			
			ps = con.createStatement();
			rs = ps.executeQuery(CONSULTA_ARCHIVO_SIMPLE + where);
			System.out.println("query valida archivo:" + CONSULTA_ARCHIVO_SIMPLE+where);
			if (rs.next()) {
				valor = true;
				System.out.println("Archivo ya existe!");
			} 			
		} catch (Exception e) {
			java.util.logging.Logger.getLogger(CBDataBancoDAO.class.getName()).log(Level.SEVERE, null, e);
		} finally{
			try {
				if(rs != null)
					rs.close();
				if(ps != null)
					ps.close();
				if(con != null)
					con.close();
			} catch(SQLException e) {
				java.util.logging.Logger.getLogger(CBDataBancoDAO.class.getName()).log(Level.SEVERE, null, e);
			}
		}		
		return valor;
		
	}
	
	//Consulta existencia de archivo de fechas multiples
		private static String CONSULTA_ARCHIVO_MULTIPLE = "SELECT COUNT(nombre_archivo) as total FROM CB_ARCHIVOS_INSERTADOS "
				+ "WHERE 1=1";
	/*@author Nicolas Bermudez
	 * 
	 * Verifica la existencia de archivo de multiples fechas
	 * */
	public boolean consultaExistenciaArchivoMultiple(String archivo, String idArchivoNuevo) {
		boolean result = false;
		Connection con = null;
		Statement ps = null;
		ResultSet rs = null;
		String where = " and nombre_archivo LIKE '%"+archivo+"%' and id_archivos_insertados!="+idArchivoNuevo;	
		try {
			con = DBUtils.getConnection();
			java.util.logging.Logger.getLogger(CBDataBancoDAO.class.getName()).log(Level.INFO, 
					"archivo = " + archivo);
			java.util.logging.Logger.getLogger(CBDataBancoDAO.class.getName()).log(Level.INFO, 
					"idArchivoNuevo = " + idArchivoNuevo);
			java.util.logging.Logger.getLogger(CBDataBancoDAO.class.getName()).log(Level.INFO, 
					"Consulta existencia archivo = " + CONSULTA_ARCHIVO_MULTIPLE + where);		
			ps = con.createStatement();			
			rs = ps.executeQuery(CONSULTA_ARCHIVO_MULTIPLE + where);	
			System.out.println("query valida diferentes fechas CONSULTA_ARCHIVO_MULTIPLE:" + CONSULTA_ARCHIVO_MULTIPLE + where);
			while(rs.next()) {
				if(rs.getInt("total") > 0) {
					java.util.logging.Logger.getLogger(CBDataBancoDAO.class.getName()).log(Level.INFO, 
							"El archivo " + archivo + " ya esta registrado.");
					result = true;
				}
			}		
		} catch (SQLException e) {						
			java.util.logging.Logger.getLogger(CBDataBancoDAO.class.getName()).log(Level.SEVERE, null, e);
		} finally{
			try {
				if(rs != null)
					rs.close();
				if(ps != null)
					ps.close();
				if(con != null)
					con.close();
			} catch(SQLException e) {
				java.util.logging.Logger.getLogger(CBDataBancoDAO.class.getName()).log(Level.SEVERE, null, e);
			}
		}
		return result;
	}
	
	//Query para obtener la cantidad de fechas diferentes en la confronta
		private static String GET_DATE_CONFRONTAS = "SELECT count(DISTINCT trunc(fecha)) as total " + 
				"FROM cb_data_banco " + 
				"WHERE id_archivos_insertados = ?";
		
		/*@Author Nicolas Bermudez*/
	//Verificacion de confrontas con mas de una fecha en sus registros
		public int getDateConfronta(String idArchivo) {
			int result = 0;
			PreparedStatement ps = null;
			ResultSet rs = null;	
			Connection con = null;
			System.out.println("Parametro para el proceso: " + idArchivo);
			System.out.println("llamada al query en el java: "+ GET_DATE_CONFRONTAS + " " + idArchivo);
			
			try {		
				con = DBUtils.getConnection();
				 ps = con.prepareStatement(GET_DATE_CONFRONTAS);
				 System.out.println("query valida diferentes fechas GET_DATE_CONFRONTAS:" + GET_DATE_CONFRONTAS);
				 ps.setString(1, idArchivo);			 
				 rs = ps.executeQuery();
				 
				 while(rs.next()) {				
					result = rs.getInt("total");				 
				 }			 
				 System.out.println("Cantidad de fechas diferentes en la confronta: " + result);	
			} catch (SQLException e) {			
				java.util.logging.Logger.getLogger(CBDataBancoDAO.class.getName()).log(Level.SEVERE, null, e);
			} finally{
				try {
					if(rs != null)
						rs.close();
					if(ps != null)
						ps.close();				
				} catch(SQLException e) {
					java.util.logging.Logger.getLogger(CBDataBancoDAO.class.getName()).log(Level.SEVERE, null, e);
				}
			}	
			return result;
		}
		
		/*Agregado por Nicolas Bermudez*/
		// Llama a proceso cb_fechas_confrontas_sp
		private static String EJECUTA_PROCESO_CB_FECHAS_CONFRONTAS_SP = "{call CB_FECHAS_CONFRONTAS_SP(?)}";

		public void ejecutaProcesoSeparacionFechasConfronta(String idMaestroCarga) {
			CallableStatement callableStatement = null;
			System.out.println("Parametro para el proceso: " + idMaestroCarga);
			System.out.println("llamada de proceso en el java: "
					+ "{call CB_FECHAS_CONFRONTAS_SP(" + idMaestroCarga + ")}");
			
			Connection con = null;
			
			try {		
				con = DBUtils.getConnection();
				callableStatement = con.prepareCall(EJECUTA_PROCESO_CB_FECHAS_CONFRONTAS_SP);
				callableStatement.setInt(1, Integer.parseInt(idMaestroCarga));
				callableStatement.execute();						
			} catch (Exception e) {			
				java.util.logging.Logger.getLogger(CBDataBancoDAO.class.getName()).log(Level.SEVERE, null, e);
			} finally{
				try {
					if(callableStatement != null)
						callableStatement.close();				
				} catch(SQLException e) {
					java.util.logging.Logger.getLogger(CBDataBancoDAO.class.getName()).log(Level.SEVERE, null, e);
				}
			}	
		}
		
		//Query para obtener la cantidad de fechas diferentes en la confronta
				private static String GET_CONF_CONFRONTAS = "SELECT count(DISTINCT tipo) as total " + 
						"FROM cb_data_banco " + 
						"WHERE id_archivos_insertados = ?";
		
		/*@Author Nicolas Bermudez*/
		//Verificacion de confrontas con mas de un convenio en sus registros
			public int getConfConfronta(String idArchivo) {
				int result = 0;
				PreparedStatement ps = null;
				ResultSet rs = null;	
				Connection con = null;
				System.out.println("Parametro para el proceso: " + idArchivo);
				System.out.println("llamada al query en el java: "+ GET_CONF_CONFRONTAS + " " + idArchivo);
				
				try {		
					con = DBUtils.getConnection();
					 ps = con.prepareStatement(GET_CONF_CONFRONTAS);
					 ps.setString(1, idArchivo);			 
					 rs = ps.executeQuery();
					 
					 while(rs.next()) {				
						result = rs.getInt("total");				 
					 }			 
					 System.out.println("Cantidad de convenios diferentes en la confronta: " + result);	
				} catch (SQLException e) {			
					java.util.logging.Logger.getLogger(CBDataBancoDAO.class.getName()).log(Level.SEVERE, null, e);
				} finally{
					try {
						if(rs != null)
							rs.close();
						if(ps != null)
							ps.close();				
					} catch(SQLException e) {
						java.util.logging.Logger.getLogger(CBDataBancoDAO.class.getName()).log(Level.SEVERE, null, e);
					}
				}	
				return result;
			}
			
			// Llama a proceso CB_CONCILIACION_SP
			private static String EJECUTA_PROCESO_CB_CARCA_COONFRONTAS = "{call cb_carga_confrontas_sp(?)}";

			public void ejecutaProcesoSeparacionTipoConfrontas(String idMaestroCarga) {
				CallableStatement callableStatement = null;
				System.out.println("Parametro para el proceso carga confronta: " + idMaestroCarga);
				System.out.println("llamada de proceso en el java: "
						+ "{call cb_carga_confrontas_sp(" + idMaestroCarga + ")}");
				
				Connection con = null;
				try {				
					con = DBUtils.getConnection();
					callableStatement = con.prepareCall(EJECUTA_PROCESO_CB_CARCA_COONFRONTAS);
					callableStatement.setInt(1, Integer.parseInt(idMaestroCarga));
					callableStatement.execute();			
				} catch (Exception e) {			
					java.util.logging.Logger.getLogger(CBDataBancoDAO.class.getName()).log(Level.SEVERE, null, e);
				} finally{
					try {
						if(callableStatement != null)
							callableStatement.close();				
					} catch(SQLException e) {
						java.util.logging.Logger.getLogger(CBDataBancoDAO.class.getName()).log(Level.SEVERE, null, e);
					}
				}	
			}
			
			private String QRY_FORMATO_FECHA = "SELECT FORMATO_FECHA FROM CB_CONFIGURACION_CONFRONTA WHERE CBCONFIGURACIONCONFRONTAID = ?";

			public String obtenerFormatoFechaConfronta(int id) {
				String valor = "";
				Connection conn = null;
				PreparedStatement cmd = null;
				ResultSet rs = null;
				try {
					conn = DBUtils.getConnection();
					System.out.println("Consulta para obtener formato fecha = " + QRY_FORMATO_FECHA);
					cmd = conn.prepareStatement(QRY_FORMATO_FECHA);
					cmd.setInt(1, id);
					rs = cmd.executeQuery();
					while (rs.next()) {
						valor = rs.getString(1);
					}
				} catch (Exception e) {
					java.util.logging.Logger.getLogger(CBDataBancoDAO.class.getName()).log(Level.SEVERE, null, e);
					if (rs != null)
						try {
							rs.close();
						} catch (SQLException e2) {
							java.util.logging.Logger.getLogger(CBDataBancoDAO.class.getName()).log(Level.SEVERE, null, e);
						}
					if (cmd != null)
						try {
							cmd.close();
						} catch (SQLException e1) {
							java.util.logging.Logger.getLogger(CBDataBancoDAO.class.getName()).log(Level.SEVERE, null, e);
						}
					if (conn != null)
						try {
							conn.close();
						} catch (SQLException e3) {
							java.util.logging.Logger.getLogger(CBDataBancoDAO.class.getName()).log(Level.SEVERE, null, e3);
						}
				}
				System.out.println("Valor devuelto de formato fecha: " + valor);
				return valor;
			}
}
