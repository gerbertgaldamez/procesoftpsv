package com.terium.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.Logger;

import com.terium.modelos.CBConexionConfModel;
import com.terium.utils.DBUtils;

public class CBConexionConfDAO {
	public static final Logger logger = Logger
			.getLogger(CBConexionConfDAO.class);

	private String CONSULTA_CON_FTP = "Select ID_CONEXION_CONF idConexionConf, "
			+ "IP_CONEXION ipConexion, "
			+ "USUARIO usuario, "
			+ "PASS pass, "
			+ "CREADO_POR creadoPor, "
			+ "FECHA_CREACION fechaCreacion, "
			+ "NOMBRE_CONEXION nombreConexion "
			+ "from cb_conexion_conf "
			+ " where ID_CONEXION_CONF = ? ";

	public List<CBConexionConfModel> obtieneListadoDeConexionConf(
			String idConexion) {
		System.out.println("IDCONEXION: " + idConexion);
		List<CBConexionConfModel> listado = new ArrayList<CBConexionConfModel>();
		logger.debug("obteniendo conexiones...");
		try {
			Connection con = DBUtils.getConnection();
			try {
				QueryRunner qr = new QueryRunner();
				BeanListHandler<CBConexionConfModel> bhl = new BeanListHandler<CBConexionConfModel>(
						CBConexionConfModel.class);
				listado = qr.query(con, CONSULTA_CON_FTP, bhl,
						new Object[] { idConexion });
				
			} finally {
				con.close();
			}
		} catch (Exception e) {
			System.out.println("Error al consultar tabla ConexionConf"
					+ e.getMessage());
		}

		return listado;
	}

	// Query para recuperar los datos de conexion FTP
	private String DATOS_CONEXION = "SELECT IP_CONEXION ipConexion, "
			+ " USUARIO usuario, " + " PASS pass" + " FROM CB_CONEXION_CONF "
			+ " WHERE ID_CONEXION_CONF = ?";

	// Metodo que recupera la ip de conexion del ftp
	public String ipConexionFTP(String idConexion) {
		String ipConexion = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			Connection con = DBUtils.getConnection();
			try {
				ps = con.prepareStatement(DATOS_CONEXION);
				ps.setInt(1, Integer.parseInt(idConexion));
				rs = ps.executeQuery();
				rs.next();
				ipConexion = rs.getString("ipConexion");
			} finally {
				con.close();
			}
		} catch (Exception e) {
			System.out.println("error al recuperar la ip de la conexion: "
					+ e.getMessage());
		}
		return ipConexion;
	}

	// Metodo que recupera el usuario de la conexion ftp
	public String usuarioFTP(String idConexion) {
		String usuario = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			Connection con = DBUtils.getConnection();
			try {
				ps = con.prepareStatement(DATOS_CONEXION);
				ps.setInt(1, Integer.parseInt(idConexion));
				rs = ps.executeQuery();
				rs.next();
				usuario = rs.getString("usuario");
			} finally {
				con.close();
			}
		} catch (Exception e) {
			System.out.println("error al recuperar el usuario de la conexion: "
					+ e.getMessage());
		}
		return usuario;
	}

	// Metodo que recupera el password de la conexion ftp
	public String passwordFTP(String idConexion) {
		String password = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			Connection con = DBUtils.getConnection();
			try {
				ps = con.prepareStatement(DATOS_CONEXION);
				ps.setInt(1, Integer.parseInt(idConexion));
				rs = ps.executeQuery();
				rs.next();
				password = rs.getString("pass");
			} finally {
				con.close();
			}
		} catch (Exception e) {
			System.out
					.println("error al recuperar el password de la conexion: "
							+ e.getMessage());
		}
		return password;
	}
}
