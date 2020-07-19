import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Tomcat的处理流程：把URL对应处理的Servlet关系形成，解析HTTP协议，封装请求/响应对象，利用反射实例化具体的Servlet进行处理即可
 */
public class MyTomcat {
	private int port = 20000;
	private Map<String, String> urlServletMap = new HashMap<String, String>();
	public MyTomcat(int port){
		this.port = port;
	}
	public void start(){
		//初始化url与对应处理的servlet的关系
		initServletMapping();
		ServerSocket serverSocket= null;
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("MyTomcat is start...");
			while(true){
				Socket socket = serverSocket.accept();
				InputStream inputStream = socket.getInputStream();
				OutputStream outputStream = socket.getOutputStream();
				MyRequest myRequest = new MyRequest(inputStream);
				System.out.println("myRequest:"+myRequest);
				MyResponse myResponse = new MyResponse(outputStream);
				dispatch(myRequest, myResponse);
				socket.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException {
		new MyTomcat(20000).start();
	}
	
	public void initServletMapping(){
		for(ServletMapping servletMapping : ServletMappingConfig.servletMappingList){
			urlServletMap.put(servletMapping.getUrl(), servletMapping.getClazz());
		}
	}
	
	public void dispatch(MyRequest myRequest, MyResponse myResponse){
		String clazz = urlServletMap.get(myRequest.getUrl());
		try{
			Class<MyServlet> myServletClass = (Class<MyServlet>) Class.forName(clazz);
			MyServlet myServlet = myServletClass.newInstance();
			myServlet.service(myRequest, myResponse);
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
}
