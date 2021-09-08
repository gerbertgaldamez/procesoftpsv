package com.terium.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.terium.utils.DBUtils;

public class ProcesarArchivoDAO {
	// Metodo para recuperar el path de descarga
	private String RECUPERA_PATH_DESCARGA = " SELECT CODIGO_PARAMETRO "
			+ " FROM CB_PARAMETROS "
			+ " WHERE DESCRIPCION_PARAMETRO = 'PATHDESCARGA' ";

	public String recuperaPathDescarga() {
		String resultado = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Connection con = DBUtils.getConnection();
			try {
				ps = con.prepareStatement(RECUPERA_PATH_DESCARGA);
				rs = ps.executeQuery();
				rs.next();
				resultado = rs.getString(1);
				System.out.println("PATH DE DESCARGA: " + resultado);
			} finally {
				con.close();
			}
		} catch (Exception e) {
			System.out.println("error al recuperar el path de descargas: "
					+ e.getMessage());
		}
		return resultado;
	}

	// Metodo para recuperar la carpeta de procesados
	private String RECUPERA_CARPETA_PROCESADOS = " SELECT CODIGO_PARAMETRO "
			+ " FROM CB_PARAMETROS "
			+ " WHERE DESCRIPCION_PARAMETRO = 'CARPETAPROCESADOS' ";

	public String recuperaCarpetaProcesados() {
		String resultado = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Connection con = DBUtils.getConnection();
			try {
				ps = con.prepareStatement(RECUPERA_CARPETA_PROCESADOS);
				rs = ps.executeQuery();
				rs.next();
				resultado = rs.getString(1);
				System.out.println("CARPETA PROCESADOS: " + resultado);
			} finally {
				con.close();
			}
		} catch (Exception e) {
			System.out.println("error al recuperar carpeta de procesados: "
					+ e.getMessage());
		}
		return resultado;
	}
}
