import java.io.IOException;

public class HelloWorldServlet extends MyServlet{

	@Override
	public void doGet(MyRequest myRequest, MyResponse myResponse) {

		try {
			myResponse.write("get world...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doPost(MyRequest myRequest, MyResponse myResponse) {
		// TODO Auto-generated method stub
		try {
			myResponse.write("post world...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
