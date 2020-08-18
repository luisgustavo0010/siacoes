package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.Department;

public class DepartmentDAO {

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

	public Department findById(int id) throws SQLException{

		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			getPreparedStatement(
				"SELECT department.*, campus.name AS campusName " +
				"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
				"WHERE idDepartment = ?", Statement.RETURN_GENERATED_KEYS);
		
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
	
	public List<Department> listAll(boolean onlyActive) throws SQLException{
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();

			rs = stmt.executeQuery("SELECT department.*, campus.name AS campusName " +
					"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " + 
					(onlyActive ? " WHERE department.active=1" : "") + " ORDER BY department.name");
			
			List<Department> list = new ArrayList<Department>();
			
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
	
	public List<Department> listByCampus(int idCampus, boolean onlyActive) throws SQLException{
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();

			rs = stmt.executeQuery("SELECT department.*, campus.name AS campusName " +
					"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
					"WHERE department.idCampus=" + String.valueOf(idCampus) + (onlyActive ? " AND department.active=1" : "") + " ORDER BY department.name");
			
			List<Department> list = new ArrayList<Department>();
			
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
	
	public int save(int idUser, Department department) throws SQLException{
		boolean insert = (department.getIdDepartment() == 0);

		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				getPreparedStatement("INSERT INTO department(idCampus, name, logo, active, site, fullName, initials) VALUES(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				getPreparedStatement("UPDATE department SET idCampus=?, name=?, logo=?, active=?, site=?, fullName=?, initials=? WHERE idDepartment=?", Statement.RETURN_GENERATED_KEYS);
			}
			
			pstmt.setInt(1, department.getCampus().getIdCampus());
			pstmt.setString(2, department.getName());
			if(department.getLogo() == null){
				pstmt.setNull(3, Types.BINARY);
			}else{
				pstmt.setBytes(3, department.getLogo());
			}
			pstmt.setInt(4, department.isActive() ? 1 : 0);
			pstmt.setString(5, department.getSite());
			pstmt.setString(6, department.getFullName());
			pstmt.setString(7, department.getInitials());
			
			if(!insert){
				pstmt.setInt(8, department.getIdDepartment());
			}
			
			pstmt.execute();
			
			if(insert){
				rs = pstmt.getGeneratedKeys();
				
				if(rs.next()){
					department.setIdDepartment(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, department);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, department);
			}
			
			return department.getIdDepartment();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((pstmt != null) && !pstmt.isClosed())
				pstmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private Department loadObject(ResultSet rs) throws SQLException{
		Department department = new Department();
		
		department.setIdDepartment(rs.getInt("idDepartment"));
		department.getCampus().setIdCampus(rs.getInt("idCampus"));
		department.setName(rs.getString("name"));
		department.setFullName(rs.getString("fullName"));
		department.setLogo(rs.getBytes("logo"));
		department.setActive(rs.getInt("active") == 1);
		department.setSite(rs.getString("site"));
		department.getCampus().setName(rs.getString("campusName"));
		department.setInitials(rs.getString("initials"));
		
		return department;
	}
	
}
