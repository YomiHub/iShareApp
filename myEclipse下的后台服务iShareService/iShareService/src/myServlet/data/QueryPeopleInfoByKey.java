package myServlet.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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

import com.sun.rowset.CachedRowSetImpl;

import net.sf.json.JSONArray;

/**
 * Servlet implementation class QueryPeopleInfo
 */
@WebServlet("/QueryPeopleInfoByKey")
public class QueryPeopleInfoByKey extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	CachedRowSetImpl rowSet = null;    //存储表中全部记录的行集对象
	int pageSize=10; //每页加载数量
    int pageNum=1;   //第几页
    int totalRecord;  //总记录数
    int totalPage;   //总页数
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryPeopleInfoByKey() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
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
        String keyWord = request.getParameter("nameKey");	
        
        if(keyWord == null||keyWord.length()==0) {
			return;
		}
	
        String condition ="select * from user where username like '%"+keyWord+"%'" ;   //按关键词查找
	
		Connection connection = null;
		Statement sql = null;
		ResultSet rs = null;

		try {
			connection = JdbcUtils.getConnection();
			sql = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = sql.executeQuery(condition);
			
			rowSet = new CachedRowSetImpl();  //创建行集对象
			rowSet.populate(rs);
			
			//按查询页数返回结果
			returnByPage(request,response,rowSet);
		} catch (Exception e) {	
			out.println(condition+"异常："+e.toString());
			e.printStackTrace();
		}finally {
			//5.释放资源 connection prepareStatement
			JdbcUtils.statementClose(connection, sql, rs);
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public void returnByPage(HttpServletRequest request, HttpServletResponse response,CachedRowSetImpl rowSet)throws ServletException, IOException{
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
			JSONArray jsonArray = new JSONArray();//存放返回的jsonOjbect数组
			JSONArray TotaljsonArray = new JSONArray();//存放返回的jsonOjbect数组
			//将rowSet的数据提取到Map
			List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
			
			//将rowSet的数据提取到Map
			List<Map<String,Object>> Totaldata = new ArrayList<Map<String,Object>>();
			try {
				PrintWriter out = response.getWriter();
				try {
					rowSet.last();    //移到随后一行
		    		totalRecord = rowSet.getRow();
		    
		    		if(totalRecord%pageSize==0){
		    			totalPage = totalRecord/pageSize;  //总页数
		    		}else{
		    			totalPage = totalRecord/pageSize+1; 
		    		}
		    		
		    		int index = (pageNum-1)*pageSize+1;
	    			rowSet.absolute(index);   //查询位置移动到查询页的起始记录位置
	    			boolean boo = true;
	    			
	    			for(int i=1; i<=totalRecord&&boo;i++){		
	    				String userName = rowSet.getString(1);    //用户昵称	    				
	    				//String passWord = rowSet.getString(2);   //用户密码 				
	    				String signature = rowSet.getString(3);   //用户签名
	    				String userLogImage = rowSet.getString(4);   //用户头像
	    				
	    				Map<String,Object> map = new HashMap<String,Object>();
	    				map.put("userName", userName);
	    				/*map.put("passWord", passWord);*/
	    				map.put("signature", signature);
	    				map.put("userLogImage", userLogImage);
	    		
	    				data.add(map);
						boo = rowSet.next();
	    			}
	    			jsonArray = JsonUtils.formatRsToJsonArray(data);
	    			Map<String,Object> map = new HashMap<String,Object>();
    				map.put("totalRecord", totalRecord);
    				map.put("RecordDetail", jsonArray);
    				Totaldata.add(map);
    				TotaljsonArray = JsonUtils.formatRsToJsonArray(Totaldata);
	    			out.println(TotaljsonArray.toString());  //返回json
				}catch(Exception e) {
					out.println("null"); 
				}
				
			}catch(IOException e) {
				
			}
		
	}

}
