package com.terium.dao;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.Logger;

import com.terium.modelos.CBBancoAgenciaConfrontaModel;
import com.terium.utils.DBUtils;

public class CBBancoAgenciasConfrontaDao {
	public static final Logger logger = Logger
			.getLogger(CBBancoAgenciasConfrontaDao.class);

	/*
	private String CONSULTA_AGENCIAS = "SELECT cbcatalogobancoid cBCatalogoBancoId, "
			+ "cbcatalogoagenciaid cBCatalogoAgenciaId, "
			+ "cbconfiguracionconfrontaid cBConfiguracionConfrontaId, "
			+ "CBBANCOAGENCIACONFRONTAID cBBancoAgenciaConfrontaId, "
			+ "tipo tipo, "
			+ "pathftp pathftp, "
			+ "estado estado, "
			+ "ID_CONEXION_CONF idConexionConf, "
			+ "PALABRA_ARCHIVO palabraArchivo "
			+ "FROM CB_BANCO_AGENCIA_CONFRONTA " + "WHERE estado = 1";*/
	
	private String CONSULTA_AGENCIAS ="SELECT a.cbcatalogobancoid cBCatalogoBancoId, " + 
			"a.cbcatalogoagenciaid cBCatalogoAgenciaId, " + 
			"a.cbconfiguracionconfrontaid cBConfiguracionConfrontaId, " + 
			"a.CBBANCOAGENCIACONFRONTAID cBBancoAgenciaConfrontaId, " + 
			"a.tipo tipo, " + 
			"a.pathftp pathftp, " + 
			"a.estado estado, " + 
			"a.ID_CONEXION_CONF idConexionConf, " + 
			"a.PALABRA_ARCHIVO palabraArchivo," + 
			"a.COMISION comision," +
			"b.IP_CONEXION ipConexion, " + 
			"b.USUARIO usuario, " + 
			"b.PASS pass, " + 
			"b.CREADO_POR creadoPor, " + 
			"b.FECHA_CREACION fechaCreacion, " + 
			"b.NOMBRE_CONEXION nombreConexion, " +
			"(SELECT c.FORMATO_FECHA FROM CB_CONFIGURACION_CONFRONTA c WHERE c.CBCONFIGURACIONCONFRONTAID = a.CBCONFIGURACIONCONFRONTAID) as formatoFecha " +
			"FROM CB_BANCO_AGENCIA_CONFRONTA a, cb_conexion_conf b " + 
			"WHERE estado = 1 " + 
			"AND a.ID_CONEXION_CONF = b.ID_CONEXION_CONF ";  

	// consulta listado banco agencias confronta
	public List<CBBancoAgenciaConfrontaModel> obtieneListadoBancoAgenciaConfronta() {
		List<CBBancoAgenciaConfrontaModel> listado = new ArrayList<CBBancoAgenciaConfrontaModel>();
		logger.debug("consulta agencias...");
		try {
			Connection con = DBUtils.getConnection();
			try {
				QueryRunner qr = new QueryRunner();
				BeanListHandler<CBBancoAgenciaConfrontaModel> bhl = new BeanListHandler<CBBancoAgenciaConfrontaModel>(
						CBBancoAgenciaConfrontaModel.class);
				System.out.println("query de agencias " + CONSULTA_AGENCIAS);
				listado = qr.query(con, CONSULTA_AGENCIAS
						+ " ORDER BY CBBANCOAGENCIACONFRONTAID DESC", bhl,
						new Object[] {});
			} finally {
				con.close();
			}
		} catch (Exception e) {
			System.out
					.println("error al consultar tabla BancoAgenciasConfrontas" + e.getMessage());
		}
		return listado;
	}

}
