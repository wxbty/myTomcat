import java.io.IOException;
import java.io.InputStream;

/**
 * 通过输入流，对HTTP协议进行解析，拿到了HTTP请求头的方法以及URL。
 */
public class MyRequest {
	private String url;
	private String method;
	public MyRequest(InputStream inputStream) throws IOException {
		/*
		    GET /girl HTTP/1.1
			Accept: text/html, application/xhtml+xml, image/jxr, *
			Accept-Language: zh-CN
			User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko
			Accept-Encoding: gzip, deflate
			Host: localhost:8080
			Connection: Keep-Alive
		*/
		String httpRequest="";
		byte[] httpRequestBytes = new byte[1024];
		int leng = 0;
		if((leng=inputStream.read(httpRequestBytes))>0){
			httpRequest = new String(httpRequestBytes,0,leng);
		}

		String httpHead = httpRequest.split("\n")[0];//获取第一行
		url = httpHead.split("\\s")[1];//以空格分开
		method = httpHead.split("\\s")[0];//以空格分开
		System.out.println("url:"+url+",method:"+method);
	}
	public String getUrl() {
		return url;
	}
	public String getMethod() {
		return method;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	
}
