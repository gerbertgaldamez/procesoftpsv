package com.terium.procesos;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;

import com.terium.dao.CBBancoAgenciasConfrontaDao;
import com.terium.dao.CBConexionConfDAO;
import com.terium.dao.CBDataBancoDAO;
import com.terium.modelos.CBBancoAgenciaConfrontaModel;
import com.terium.modelos.CBConexionConfModel;

public class MainProcess {
	public static final Logger logger = Logger.getLogger(MainProcess.class);
	private ProcesarArchivo procesarArchivos = new ProcesarArchivo();

	private List<CBBancoAgenciaConfrontaModel> listadoAgencias;
	private List<CBConexionConfModel> listadoConexiones;
	private static String obtieneIdCarpeta;
	private static int contador = 0;
	private String todosLosId;

	public void iniciarProceso() {
		obtieneBancosAgenciasConfronta();
		leeCarpteAgenciaFTP();
	}

	// metodo para obtener el listado de agencias cuando su estado = ACTIVO
	public void obtieneBancosAgenciasConfronta() {
		logger.debug("obtiene agencias...");
		System.out.println("Obteniendo las agencias a procesar...");
		CBBancoAgenciasConfrontaDao cbbac = new CBBancoAgenciasConfrontaDao();
		setListadoAgencias(cbbac.obtieneListadoBancoAgenciaConfronta());
		logger.debug("Agencias obtenidas ==:> "+getListadoAgencias().size());
		System.out.println("Agencias obtenidas ==:> "+getListadoAgencias().size());
	}

	public void leeCarpteAgenciaFTP() {
		//CBConexionConfDAO cbcf = new CBConexionConfDAO();

		logger.debug("lee carpetas por FTP...");
		System.out.println("cantidad de agencias: "+ getListadoAgencias().size());
		for (CBBancoAgenciaConfrontaModel registros : getListadoAgencias()) {
			setObtieneIdCarpeta(null);
			System.out.println("***************************************INICIA UNA LECTURA #*************************************");
			//Coment by Juankrlos 12/07/2017
			//listadoConexiones = cbcf.obtieneListadoDeConexionConf(registros.getIdConexionConf());
			obtenerAchivosPorFTP(registros, registros.getIpConexion(), registros.getUsuario(),
					registros.getPass(), registros.getComision());
			System.out.println("Termina de obtener los archivos por FTP");
			if (getTodosLosId() == null) {
				if (getObtieneIdCarpeta() == null) {
					System.out.println("no hay archivos en la carpeta...");
				} else {
					setTodosLosId(getObtieneIdCarpeta());
					setObtieneIdCarpeta(null);
				}
			} else {
				if (getObtieneIdCarpeta() == null) {
					System.out.println("no hay archivos en la carpeta...");
				} else {
					setTodosLosId(getTodosLosId() + ";" + getObtieneIdCarpeta());
					setObtieneIdCarpeta(null);
				}

			}
			System.out.println("Variables de distintas carpetas "
					+ getTodosLosId());
			System.out.println("CONTADOR EN MAIN PROCESS: " + getContador());
			if (getContador() >= 15) {
				System.out.println("Llego a 15 ids");
				// Llama al proceso para enviar el correo con los id que se
				// conciliaron
				CBDataBancoDAO dbd = new CBDataBancoDAO();
				//dbd.ejecutaCbEnviaCorreoConciliacion(getTodosLosId());
				setContador(0);
				setTodosLosId("");
			}
			System.out.println("FINALIZA PROCESO DE LECTURA DE CONFRONTAS!!");
		}
		

		// Llama al proceso para enviar el correo con los id que se conciliaron
		CBDataBancoDAO dbd = new CBDataBancoDAO();
		//dbd.ejecutaCbEnviaCorreoConciliacion(getTodosLosId());
	}

	// metodo para obtener archivos FTP
	private void obtenerAchivosPorFTP(CBBancoAgenciaConfrontaModel bancoAgencia, String ipConex,
			String usuarioConex, String passConex, BigDecimal comision) {
		logger.debug("obtiene archivos por ftp..");
		try {
			procesarArchivos.procesarArchivos(bancoAgencia, ipConex,
					usuarioConex, passConex, comision);
		} catch (ParseException e) {
			System.out.println("error al procesar archivos: " + e.getMessage());
		}
	}

	// GET Y SET
	/**
	 * @return the listadoAgencias
	 */
	public List<CBBancoAgenciaConfrontaModel> getListadoAgencias() {
		return listadoAgencias;
	}

	public void setListadoAgencias(
			List<CBBancoAgenciaConfrontaModel> listadoAgencias) {
		this.listadoAgencias = listadoAgencias;
	}

	public List<CBConexionConfModel> getListadoConexiones() {
		return listadoConexiones;
	}

	public void setListadoConexiones(List<CBConexionConfModel> listadoConexiones) {
		this.listadoConexiones = listadoConexiones;
	}

	public String getObtieneIdCarpeta() {
		return obtieneIdCarpeta;
	}

	@SuppressWarnings("static-access")
	public void setObtieneIdCarpeta(String obtieneIdCarpeta) {
		this.obtieneIdCarpeta = obtieneIdCarpeta;
	}

	public String getTodosLosId() {
		return todosLosId;
	}

	public void setTodosLosId(String todosLosId) {
		this.todosLosId = todosLosId;
	}

	public int getContador() {
		return contador;
	}

	@SuppressWarnings("static-access")
	public void setContador(int contador) {
		this.contador = contador;
	}

}
