package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.BugReport;
import br.edu.utfpr.dv.siacoes.model.BugReport.BugStatus;
import br.edu.utfpr.dv.siacoes.model.Module;
import br.edu.utfpr.dv.siacoes.model.User;

public class BugReportDAO {

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
	
	public BugReport findById(int id) throws SQLException{

		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			getPreparedStatement("SELECT bugreport.*, \"user\".name " +
				"FROM bugreport INNER JOIN \"user\" ON \"user\".idUser=bugreport.idUser " +
				"WHERE idBugReport = ?", Statement.RETURN_GENERATED_KEYS);
		
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

	public List<BugReport> listAll() throws SQLException{

		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();

			rs = stmt.executeQuery("SELECT bugreport.*, \"user\".name " +
					"FROM bugreport INNER JOIN \"user\" ON \"user\".idUser=bugreport.idUser " +
					"ORDER BY status, reportdate");
			List<BugReport> list = new ArrayList<BugReport>();
			
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
	
	public int save(BugReport bug) throws SQLException{
		boolean insert = (bug.getIdBugReport() == 0);

		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				getPreparedStatement("INSERT INTO bugreport(idUser, module, title, description, reportDate, type, status, statusDate, statusDescription) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				getPreparedStatement("UPDATE bugreport SET idUser=?, module=?, title=?, description=?, reportDate=?, type=?, status=?, statusDate=?, statusDescription=? WHERE idBugReport=?", Statement.RETURN_GENERATED_KEYS);
			}
			
			pstmt.setInt(1, bug.getUser().getIdUser());
			pstmt.setInt(2, bug.getModule().getValue());
			pstmt.setString(3, bug.getTitle());
			pstmt.setString(4, bug.getDescription());
			pstmt.setDate(5, new java.sql.Date(bug.getReportDate().getTime()));
			pstmt.setInt(6, bug.getType().getValue());
			pstmt.setInt(7, bug.getStatus().getValue());
			if(bug.getStatus() == BugStatus.REPORTED){
				pstmt.setNull(8, Types.DATE);
			}else{
				pstmt.setDate(8, new java.sql.Date(bug.getStatusDate().getTime()));
			}
			pstmt.setString(9, bug.getStatusDescription());
			
			if(!insert){
				pstmt.setInt(10, bug.getIdBugReport());
			}
			
			pstmt.execute();
			
			if(insert){
				rs = pstmt.getGeneratedKeys();
				
				if(rs.next()){
					bug.setIdBugReport(rs.getInt(1));
				}
			}
			
			return bug.getIdBugReport();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((pstmt != null) && !pstmt.isClosed())
				pstmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private BugReport loadObject(ResultSet rs) throws SQLException{
		BugReport bug = new BugReport();
		
		bug.setIdBugReport(rs.getInt("idBugReport"));
		bug.setUser(new User());
		bug.getUser().setIdUser(rs.getInt("idUser"));
		bug.getUser().setName(rs.getString("name"));
		bug.setModule(Module.SystemModule.valueOf(rs.getInt("module")));
		bug.setTitle(rs.getString("title"));
		bug.setDescription(rs.getString("description"));
		bug.setReportDate(rs.getDate("reportDate"));
		bug.setType(BugReport.BugType.valueOf(rs.getInt("type")));
		bug.setStatus(BugReport.BugStatus.valueOf(rs.getInt("status")));
		bug.setStatusDate(rs.getDate("statusDate"));
		bug.setStatusDescription(rs.getString("statusDescription"));
		
		return bug;
	}

}
