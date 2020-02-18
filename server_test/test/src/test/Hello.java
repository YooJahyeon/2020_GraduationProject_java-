package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Hello {

	public static void main(String[] args) throws IOException {  // 메인함수, IOException
		ServerSocket serverSocket = null; //서버소켓 생성
		Socket clientSocket = null; // 서버소켓이 지정한 포트를 타고온 상대 ip를 저장할 수 있다.
		PrintWriter out = null;  // String 타입의 문자를 보낼수 있는 함수.
		BufferedReader in = null;//stream 타입의 문자를 읽어서 저장할 수 있는 함수.

		serverSocket = new ServerSocket(9997); //서버소켓 생성

		try {
		// 서버 소켓을 만들고 연결을 기다린다.
			clientSocket = serverSocket.accept(); //클라이언트로부터 데이터가 오는것을 감지한다.
			System.out.println("클라이언트 연결");
	
			// 클라이언트로 부터 데이터를 받는다.
			out = new PrintWriter(clientSocket.getOutputStream(), true); //String 타입을 stream 형태로 변환하여 
			                                                                                                            //전송한다는 뜻.
			in = new BufferedReader(new InputStreamReader(
			clientSocket.getInputStream())); //소켓에서 넘오는 stream 형태의 문자를 얻은 후 읽어 들어서         //bufferstream 형태로 in에 저장.
	
			while (true) {
				String inputLine = null; //in 으로 받아들인 데이터를 저장할 string 생성
				inputLine = in.readLine(); //in에 저장된 데이터를 String 형태로 변환 후 읽어들어서 String에 저장
				System.out.println("클라이언트로 부터 받은 문자열:" + inputLine); //저장된값 콘솔 출력
				out.println(inputLine); //돌아온값을 다시 되돌려 보낸다. //String이 stream으로 변환되어 전송됨.
				if (inputLine.equals("quit")) //만약 받은 값이 quit 일경우 종료
				break;
			}
			out.close();
			in.close();
			clientSocket.close();
			serverSocket.close();  //열린 모든것을 닫아준다.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
