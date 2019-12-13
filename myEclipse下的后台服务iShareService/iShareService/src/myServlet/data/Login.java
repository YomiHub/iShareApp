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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.utils.JdbcUtils;
import org.utils.JsonUtils;

import net.sf.json.JSONArray;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
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
       
	          
		String username = request.getParameter("username").trim();
		String password = request.getParameter("password").trim();
		String signature = "������д���������ɣ�";
		String userHeadImg = "";
	          
        
		if(username == null) {
			username = "";
		}
		
		if(password == null) {
			password = "";
		}
      
	 	Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet rs = null;
		String backnews="";
		boolean boo = false;
		
		boo = username.length()>0&&password.length()>0;
		
		try {
			connection = JdbcUtils.getConnection();
			
			//3.��ȡstatement
			String sql ="select * from user where username=? and password=?";
			prepareStatement = connection.prepareStatement(sql);
			prepareStatement.setString(1, username);
			prepareStatement.setString(2, password);
			
			if(boo) {
    			//4.ִ��sql
				boolean m =false;
				
    			rs = prepareStatement.executeQuery();
    			while(rs.next()) {
    				signature = rs.getString(3);
    				userHeadImg = rs.getString(4);
    				m = true;
    			}
    			
    			if(m==true) {
    				//��¼�ɹ�
    				success(request,response,username,userHeadImg,signature,backnews);
    			}else {
    				backnews="��������û�������������";
    				fail(request,response,username,backnews);
    			}
			}else {
				backnews="�������û���������";
				fail(request,response,username,backnews);
			}
			
  
		} catch (Exception e) {
			backnews=""+e.toString();
			e.printStackTrace();
			fail(request,response,username,backnews);
		}finally {
			//5.�ͷ���Դ connection prepareStatement
			JdbcUtils.close(connection, prepareStatement, rs);
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
	
	public void success(HttpServletRequest request, HttpServletResponse response,String userName,String userLogImage,String signature,String backnews) {
		try {
			JSONArray jsonArray = new JSONArray();//��ŷ��ص�jsonOjbect����
			List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
			
			PrintWriter out = response.getWriter();
	
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("userName", userName);
			map.put("signature", signature);
			map.put("userLogImage", userLogImage);
	
			data.add(map);
			jsonArray = JsonUtils.formatRsToJsonArray(data);
			out.println(jsonArray.toString());  //����json
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void fail(HttpServletRequest request, HttpServletResponse response,String logname,String backnews) {
		response.setContentType("text/html;charset=utf-8");
		try {
			 PrintWriter out = response.getWriter();
			 out.println(backnews);
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

}
