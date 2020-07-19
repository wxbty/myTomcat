import java.io.IOException;

public class FindGirlServlet extends MyServlet{

	@Override
	public void doGet(MyRequest myRequest, MyResponse myResponse) {
		// TODO Auto-generated method stub
		try {
			myResponse.write("get girl...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doPost(MyRequest myRequest, MyResponse myResponse) {
		// TODO Auto-generated method stub
		try {
			myResponse.write("post girl...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
