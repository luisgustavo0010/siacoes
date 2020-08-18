package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.ActivityUnit;

public class ActivityUnitDAO {

	private Connection conn = null;
	private Statement stmt = null;
	private PreparedStatement pstmt = null;

	public Connection getConnection() throws SQLException {
		return conn;
	}

	public Statement getStatement() throws SQLException {
		stmt = conn.createStatement();
		return stmt;
	}

	public PreparedStatement getPreparedStatement(String SQL, int returnGeneratedKeys) throws SQLException {
		pstmt = conn.prepareStatement(SQL);
		return pstmt;

	}
	
	public List<ActivityUnit> listAll() throws SQLException{

		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();

			rs = stmt.executeQuery("SELECT * FROM activityunit ORDER BY description");
			
			List<ActivityUnit> list = new ArrayList<ActivityUnit>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public ActivityUnit findById(int id) throws SQLException{

		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();

			getPreparedStatement("SELECT * FROM activityunit WHERE idActivityUnit=?", Statement.RETURN_GENERATED_KEYS);
		
			pstmt.setInt(1, id);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				return null;
			}
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((pstmt != null) && !pstmt.isClosed())
				pstmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public int save(int idUser, ActivityUnit unit) throws SQLException{
		boolean insert = (unit.getIdActivityUnit() == 0);
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				pstmt = getPreparedStatement("INSERT INTO activityunit(description, fillAmount, amountDescription) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				pstmt = getPreparedStatement("UPDATE activityunit SET description=?, fillAmount=?, amountDescription=? WHERE idActivityUnit=?", Statement.RETURN_GENERATED_KEYS);
			}
			
			pstmt.setString(1, unit.getDescription());
			pstmt.setInt(2, (unit.isFillAmount() ? 1 : 0));
			pstmt.setString(3, unit.getAmountDescription());
			
			if(!insert){
				pstmt.setInt(4, unit.getIdActivityUnit());
			}
			
			pstmt.execute();
			
			if(insert){
				rs = pstmt.getGeneratedKeys();
				
				if(rs.next()){
					unit.setIdActivityUnit(rs.getInt(1));
				}
				
				new UpdateEvent(conn).registerInsert(idUser, unit);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, unit);
			}
			
			return unit.getIdActivityUnit();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((pstmt != null) && !pstmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private ActivityUnit loadObject(ResultSet rs) throws SQLException{
		ActivityUnit unit = new ActivityUnit();
		
		unit.setIdActivityUnit(rs.getInt("idActivityUnit"));
		unit.setDescription(rs.getString("Description"));
		unit.setFillAmount(rs.getInt("fillAmount") == 1);
		unit.setAmountDescription(rs.getString("amountDescription"));
		
		return unit;
	}

}
