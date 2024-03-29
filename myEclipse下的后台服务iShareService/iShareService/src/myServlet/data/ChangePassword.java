package myServlet.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.utils.JdbcUtils;

/**
 * Servlet implementation class ChangePassword
 */
@WebServlet("/ChangePassword")
public class ChangePassword extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChangePassword() {
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
       
		String oldPass = request.getParameter("oldPass");
		String newPass = request.getParameter("newPass");
		String userName = request.getParameter("username").trim();
        
		
		if(oldPass == null||oldPass.length()==0) {
			oldPass="";
		}
		
		if(newPass == null||newPass.length()==0) {
			newPass="";
		}
		
		
		if(userName == null||userName.length()==0) {
			userName = "";
		}
		
	 	Connection connection = null;
		PreparedStatement prepareStatement = null;
		PreparedStatement prepareStatement2 = null;
		ResultSet rs = null;
		ResultSet rs2 =null;
		
		String backnews="";
		boolean boo = false;
		
		boo = oldPass.length()>0&&oldPass.length()>0&&userName.length()>0;
		
		try {
			connection = JdbcUtils.getConnection();
		
			//3.获取statement
			String sql ="select * from user where username=?";
			prepareStatement = connection.prepareStatement(sql);
			prepareStatement.setString(1, userName);
			
			if(boo) {
    			//4.执行sql
    			rs = prepareStatement.executeQuery();
    			boolean m = rs.next();
    			
    			if(m==true) {
    				//查询成功
    				String changeSql = "update user set password=? where username=? and password=?";
    				prepareStatement2 = connection.prepareStatement(changeSql);
    				prepareStatement2.setString(1, newPass);
    				prepareStatement2.setString(2, userName);
    				prepareStatement2.setString(3, oldPass);
    				
    			    prepareStatement2.execute();
    				backnews="修改成功";
    				
    			}else {
    				backnews="没有该用户！";
    			}
			}else {
				backnews="密码不能为空!";
			}
			out.print(backnews.toString());
		} catch (Exception e) {
			backnews="修改失败"+e.toString();
			out.print(backnews); 
			e.printStackTrace();
		
		}finally {
			//5.释放资源 connection prepareStatement
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
