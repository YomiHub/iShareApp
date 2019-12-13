package myServlet.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.utils.JdbcUtils;
import org.utils.JsonUtils;

import net.sf.json.JSONArray;

/**
 * Servlet implementation class AddFocus
 */
@WebServlet("/AddFocus")
public class AddFocus extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddFocus() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        
      
		String infoId = request.getParameter("InfoId").trim();
		String username = request.getParameter("username").trim();
		
		if(infoId == null||infoId == "") {
			return;
		}
		
		if(username == null||username == "") {
			return;
		}
		
	 	Connection connection = null;
		PreparedStatement prepareStatement = null;
		PreparedStatement prepareStatement2 = null;
		ResultSet rs = null;
		ResultSet rs2 =null;
		
		String backnews="";
		boolean boo = false;
		
		boo = infoId.length()>0;
		
		try {
			connection = JdbcUtils.getConnection();
		
			//3.��ȡstatement
			String sql ="select * from focus where info_id=? and username=?";
			prepareStatement = connection.prepareStatement(sql);
			prepareStatement.setString(1, infoId);
			prepareStatement.setString(2, username);
			
			if(boo) {
    			//4.ִ��sql
    			rs = prepareStatement.executeQuery();
    			boolean m = rs.next();
    			
    			if(m!=true) {
    				String changeSql = "INSERT INTO focus VALUES(?,?,?)";
    				prepareStatement2 = connection.prepareStatement(changeSql);
    				prepareStatement2.setInt(1, 0);
    				prepareStatement2.setString(2, username);
    				prepareStatement2.setString(3, infoId);
    				
    			    prepareStatement2.execute();
    			    
					JSONArray jsonArray = new JSONArray();//��ŷ��ص�jsonOjbect����
    				List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
    				Map<String,Object> map = new HashMap<String,Object>();
    				map.put("addOk", true);
			
					data.add(map);
					jsonArray = JsonUtils.formatRsToJsonArray(data);
					out.println(jsonArray.toString());  //����json
    				
    			}else {
    				JSONArray jsonArray = new JSONArray();//��ŷ��ص�jsonOjbect����
    				List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
 
    			
    				Map<String,Object> map = new HashMap<String,Object>();
    				map.put("addOk", false);
			
					data.add(map);
					jsonArray = JsonUtils.formatRsToJsonArray(data);
					out.println(jsonArray.toString());  //����json
    			}
			}else {
				JSONArray jsonArray = new JSONArray();//��ŷ��ص�jsonOjbect����
				List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();

			
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("addOk", false);
		
				data.add(map);
				jsonArray = JsonUtils.formatRsToJsonArray(data);
				out.println(jsonArray.toString());  //����json
			}
			out.print(backnews.toString());
		} catch (Exception e) {
			backnews="�޸�ʧ��"+e.toString();
			out.print(backnews); 
			e.printStackTrace();
		
		}finally {
			//5.�ͷ���Դ connection prepareStatement
			JdbcUtils.close(connection, prepareStatement, rs);
			JdbcUtils.close(connection, prepareStatement2, rs2);
		}
	     out.flush();
	     out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
