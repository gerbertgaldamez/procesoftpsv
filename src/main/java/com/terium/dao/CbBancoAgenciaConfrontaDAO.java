/**
 * 
 */
package com.terium.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.terium.modelos.CBBancoAgenciaConfrontaModel;
import com.terium.utils.DBUtils;

/**
 * @author aaron4431
 * 
 */
public class CbBancoAgenciaConfrontaDAO {

	// Metodo para recuperar toda la dara de la tabla CB_BANCO_AGENCIA_CONFRONTA
	private String LISTADO_BANCO_AGENCIA_CONFRONTA = " SELECT CBBANCOAGENCIACONFRONTAID cBBancoAgenciaConfrontaId, "
			+ " CBCATALOGOBANCOID cBCatalogoBancoId, "
			+ " CBCATALOGOAGENCIAID cBCatalogoAgenciaId, "
			+ " CBCONFIGURACIONCONFRONTAID cBConfiguracionConfrontaId, "
			+ " TIPO tipo, "
			+ " PATHFTP pathftp, "
			+ " ESTADO estado, "
			+ " CREADO_POR creado_Por, "
			+ " MODIFICADO_POR modificado_Por, "
			+ " FECHA_CREACION fecha_Creacion, "
			+ " FECHA_MODIFICACION fecha_Modificacion, "
			+ " COD_AGENCIA codAgencia, "
			+ " ID_CONEXION_CONF idConexionConf, "
			+ " CBAGENCIASCONFRONTAID cBAgenciasConfrontaId, "
			+ " PALABRA_ARCHIVO palabraArchivo "
			+ " FROM CB_BANCO_AGENCIA_CONFRONTA WHERE 1 = 1 ";

	public List<CBBancoAgenciaConfrontaModel> listadoBancoAgenciaConfronta() {
		List<CBBancoAgenciaConfrontaModel> listado = new ArrayList<CBBancoAgenciaConfrontaModel>();
		try {
			Connection con = DBUtils.getConnection();
			try {
				QueryRunner qr = new QueryRunner();
				BeanListHandler<CBBancoAgenciaConfrontaModel> blh = new BeanListHandler<CBBancoAgenciaConfrontaModel>(
						CBBancoAgenciaConfrontaModel.class);
				listado = qr.query(con, LISTADO_BANCO_AGENCIA_CONFRONTA
						+ " ORDER BY CBBANCOAGENCIACONFRONTAID DESC", blh,
						new Object[] {});
			} finally {
				con.close();
			}
		} catch (Exception e) {
			System.out
					.println("error al recuperar el listado de banco agencia confronta: "
							+ e.getMessage());
		}
		return listado;
	}
}
