package com.terium.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.Logger;

import com.terium.modelos.CBConfiguracionConfrontaModel;
import com.terium.utils.DBUtils;

public class CBConfiguracionConfrontaDAO {
	private static Logger logger = Logger
			.getLogger(CBConfiguracionConfrontaDAO.class);
	private String CONSULTA_CONF_CONFRONTA = "SELECT nombre nombre, "
			+ "delimitador1 delimitador1, " + "delimitador2 delimitador2, "
			+ "cantidad_agrupacion cantidad_Agrupacion, "
			+ "nomenclatura nomenclatura, " + "estado estado, " + "tipo tipo, "
			+ "formato_fecha formato_Fecha, " + " posiciones posiciones, "
			+ " longitud_cadena longitudCadena "
			+ "FROM cb_configuracion_confronta "
			+ "WHERE cbconfiguracionconfrontaid = ?";

	// consulta
	public List<CBConfiguracionConfrontaModel> obtieneListadoConfConfronta(
			int idConfronta) {
		List<CBConfiguracionConfrontaModel> listado = new ArrayList<CBConfiguracionConfrontaModel>();
		logger.debug("obtiene listado confrontas...");

		try {
			Connection con = DBUtils.getConnection();
			try {
				QueryRunner qr = new QueryRunner();
				BeanListHandler<CBConfiguracionConfrontaModel> bhl = new BeanListHandler<CBConfiguracionConfrontaModel>(
						CBConfiguracionConfrontaModel.class);
				listado = qr.query(con, CONSULTA_CONF_CONFRONTA, bhl,
						new Object[] { idConfronta });
			} finally {
				con.close();
			}
		} catch (Exception e) {
			logger.error("error al consultar listado confronras");
			System.out.println("error al consultar listado confronras");
		}

		return listado;
	}
}
