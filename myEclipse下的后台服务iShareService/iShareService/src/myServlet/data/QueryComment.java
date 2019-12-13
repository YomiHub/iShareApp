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
 * Servlet implementation class QueryComment
 */
@WebServlet("/QueryComment")
public class QueryComment extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	CachedRowSetImpl rowSet = null;    //存储表中全部记录的行集对象
	int pageSize=10; //每页加载数量
    int pageNum=1;   //第几页
    int totalRecord;  //总记录数
    int totalPage;   //总页数
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryComment() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
    
        String infoId = request.getParameter("infoId").trim();
		
        String condition = "select * from comment where comment_info='"+infoId+"' order by comment_id desc";   
	
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
	    			
	    			for(int i=1; i<=pageSize&&boo;i++){	
	    			
	    				int commentId = rowSet.getInt(1);    //评论内容ID				
	    				String commentUser = rowSet.getString(2);   //评论者   	
	    				int commentInfo =  rowSet.getInt(3); 
	    				String commentDetail = rowSet.getString(4);   //评论详情

	    				Map<String,Object> map = new HashMap<String,Object>();
	    				map.put("commentId", commentId);
	    				map.put("commentUser", commentUser);
	    				map.put("commentInfo", commentInfo);
	    				map.put("commentDetail", commentDetail);

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
