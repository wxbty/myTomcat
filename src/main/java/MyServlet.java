/**
 * Tomcat是满足Servlet规范的容器，那么自然Tomcat需要提供API。这里你看到了Servlet常见的doGet/doPost/service方法
 */
public abstract class MyServlet {

	public abstract void doGet(MyRequest myRequest,MyResponse myResponse);
	public abstract void doPost(MyRequest myRequest,MyResponse myResponse);
	public void service(MyRequest myRequest,MyResponse myResponse){
		if("GET".equalsIgnoreCase(myRequest.getMethod())){
			doGet(myRequest, myResponse);
		}else if ("POST".equalsIgnoreCase(myRequest.getMethod())) {
			doPost(myRequest, myResponse);
		}
	}
}
