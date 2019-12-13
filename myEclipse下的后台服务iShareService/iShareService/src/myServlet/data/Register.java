package myServlet.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.utils.JdbcUtils;


public class Register extends HttpServlet{

	/**
	 * 注册
	 */
	private static final long serialVersionUID = 1L;
	

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
	          
		 String username =request.getParameter("username");
		 String password = request.getParameter("password");
		 String signature =request.getParameter("signature");
		 String userlogimage = "userImg.png";  /* 默认头像*/     
		 
	 	Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = JdbcUtils.getConnection();
			//3.获取statement
			String sql ="INSERT INTO user VALUES(?,?,?,?)";
			prepareStatement = connection.prepareStatement(sql);
			prepareStatement.setString(1, username);
			prepareStatement.setString(2, password);
			prepareStatement.setString(3, signature);
			prepareStatement.setString(4, userlogimage);
			//4.执行sql
			prepareStatement.execute();
			out.print("注册成功!");
  
		} catch (Exception e) {
			e.printStackTrace();
			out.print("该用户名已被使用，请更换名字！"+e);
			
		}finally {
			//5.释放资源 connection prepareStatement
			JdbcUtils.close(connection, prepareStatement, null);
		}
	     out.flush();
	     out.close();
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
	  doGet(request, response);
	}
}
