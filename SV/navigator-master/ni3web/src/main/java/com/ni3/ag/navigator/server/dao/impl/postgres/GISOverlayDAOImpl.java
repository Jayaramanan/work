package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.navigator.server.dao.GISOverlayDAO;
import com.ni3.ag.navigator.server.util.Utility;
import com.ni3.ag.navigator.shared.domain.GISOverlay;
import org.apache.log4j.Logger;
import org.postgis.PGgeometry;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.nativejdbc.CommonsDbcpNativeJdbcExtractor;

public class GISOverlayDAOImpl extends JdbcDaoSupport implements GISOverlayDAO{
	private static final Logger log = Logger.getLogger(GISOverlayDAOImpl.class);

	private RowMapper overlayRowMapper = new RowMapper(){
		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
			final GISOverlay overlay = new GISOverlay();
			overlay.setId(rs.getInt("id"));
			overlay.setSchemaId(rs.getInt("schemaid"));
			overlay.setName(rs.getString("name"));
			overlay.setTablename(rs.getString("tablename"));
			overlay.setVersion(rs.getInt("version"));
			final String color = rs.getString("lineColor");
			if (color != null){
				overlay.setColor(Utility.createColor(color));
			}
			overlay.setLineWidth(rs.getInt("lineWidth"));
			overlay.setFilled(rs.getInt("filled") == 1);
			return overlay;
		}
	};

	@Override
	public List<GISOverlay> getOverlaysForSchema(int schemaId){
		final String sql = "SELECT DISTINCT id, schemaid, name, tablename, linecolor, linewidth, filled, version"
		        + " FROM gis_overlay WHERE schemaid=?";
		return getJdbcTemplate().query(sql, new Object[] { schemaId }, overlayRowMapper);
	}

	private GISOverlay getOverlay(int overlayId){
		final String sql = "SELECT DISTINCT id, schemaid, name, tablename, linecolor, linewidth, filled, version"
		        + " FROM gis_overlay WHERE id=?";
		return (GISOverlay) getJdbcTemplate().queryForObject(sql, new Object[] { overlayId }, overlayRowMapper);
	}

	@Override
	public List<Integer> getOverlayGeometryList(int overlayId){
		List<Integer> result = new ArrayList<Integer>();
		GISOverlay overlay = getOverlay(overlayId);
		return getJdbcTemplate().query("SELECT gid FROM " + overlay.getTablename(), new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
				return rs.getInt("gid");
			}
		});
	}

	// TODO change this method
	@Override
	public PGgeometry getOverlayGeometry(int overlayId, int geometryId){
		PreparedStatement statement = null;
		PGgeometry result = null;
		GISOverlay overlay = getOverlay(overlayId);

		Connection connection = getPGConnection();
		try{
			// TOOD: Navigator should use 900913 as well
			statement = connection.prepareStatement("SELECT ST_transform(the_geom, 4326) AS the_geom FROM "
			        + overlay.getTablename() + " WHERE gid=?");
			statement.setInt(1, geometryId);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()){
				result = (PGgeometry) resultSet.getObject(1);
			}
			resultSet.close();
		} catch (Exception e){
			log.error("Can't read overlay geometry", e);
		} finally{
			if (statement != null)
				try{
					statement.close();
				} catch (SQLException e){
					log.error("Error closing statement", e);
				}
			//closed by transaction manager
//			if (connection != null){
//				try{
//					connection.close();
//				} catch (SQLException e){
//					log.error(e);
//				}
//			}
		}
		return result;
	}

	private Connection getPGConnection(){
		Connection conn = getConnection();
		try{
			final Connection connection = new CommonsDbcpNativeJdbcExtractor().getNativeConnection(conn);
			if (connection instanceof org.postgresql.PGConnection){
				((org.postgresql.PGConnection) connection).addDataType("geometry", Class.forName("org.postgis.PGgeometry"));
				((org.postgresql.PGConnection) connection).addDataType("box3d", Class.forName("org.postgis.PGbox3d"));
			}
		} catch (SQLException e){
			log.error(e);
		} catch (ClassNotFoundException e){
			log.error(e);
		}
		return conn;
	}
}
