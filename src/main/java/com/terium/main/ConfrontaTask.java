package com.terium.main;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.terium.procesos.MainProcess;

public class ConfrontaTask {
	public static final Logger logger = Logger.getLogger(ConfrontaTask.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//PropertyConfigurator.configure("log4j.properties");
		logger.info("Iniciando proceso de carga de confrontas...");
		MainProcess mainProcess = new MainProcess();
		//CambiarArchivo ca = new CambiarArchivo();
		//ca.procesarArchivo();
		mainProcess.iniciarProceso();
		logger.info("Finalizando proceso de carga de confrontas...");
	}
}
