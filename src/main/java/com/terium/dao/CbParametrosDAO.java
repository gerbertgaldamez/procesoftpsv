/**
 * 
 */
package com.terium.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.terium.utils.DBUtils;

/**
 * @author aaron4431
 * 
 */
public class CbParametrosDAO {

	// Metodo para recuperar los parametros
	private String RECUPERA_PARAMETROS = "SELECT CODIGO_PARAMETRO PARAMETRO "
			+ " FROM CB_PARAMETROS "
			+ " WHERE DESCRIPCION_PARAMETRO = 'CARPETA'";

	public String recuperaParametros() {
		String resultado = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			Connection con = DBUtils.getConnection();
			try {
				ps = con.prepareStatement(RECUPERA_PARAMETROS);
				rs = ps.executeQuery();
				rs.next();
				resultado = rs.getString("PARAMETRO");
			} finally {
				con.close();
			}
		} catch (Exception e) {
			System.out.println("Error al recuperar el parametro: "
					+ e.getMessage());
		}
		return resultado;
	}

	// Metodo para recuperar los parametros de descarga
	private String RECUPERA_PARAMETROS_DESCAGA = "SELECT CODIGO_PARAMETRO PARAMETRO "
			+ " FROM CB_PARAMETROS "
			+ " WHERE DESCRIPCION_PARAMETRO = 'PATHDESCARGA'";

	public String recuperaParametrosDescarga() {
		String resultado = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			Connection con = DBUtils.getConnection();
			try {
				ps = con.prepareStatement(RECUPERA_PARAMETROS_DESCAGA);
				rs = ps.executeQuery();
				rs.next();
				resultado = rs.getString("PARAMETRO");
			} finally {
				con.close();
			}
		} catch (Exception e) {
			System.out.println("Error al recuperar el parametro: "
					+ e.getMessage());
		}
		return resultado;
	}

}
