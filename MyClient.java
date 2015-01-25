package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

//read command/data from server
class ClientThread extends Thread {
	private BufferedReader bt;
	
	private void closeRs() {
		try {
			if (bt != null) bt.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public ClientThread(Socket sock) throws IOException {
		bt = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	}
	
	public void run() {
		String content = null;
		try {
			while ((content = bt.readLine()) != null) {
				//Format from server -- clientname:data
				String [] arr = content.split(":");
				/*
				if (arr[0].equals("error")) { 
					System.out.println(arr[0] + " not exist");
				} else
					System.out.println("from " + arr[1] + ":" + arr[2]);
				*/
				System.out.println("from " + arr[0] + ":" + arr[1]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			closeRs();
		} finally {
			closeRs();
		} 
	}
}

public class MyClient {
	private Socket sock;
	private BufferedReader bt;
	private PrintStream ps;
	
	public MyClient() {
		try {
		sock = new Socket("127.0.0.1", 3000);
		bt= new BufferedReader(new InputStreamReader(System.in));
		ps = new PrintStream(sock.getOutputStream());
		} catch (UnknownHostException ex) {
			System.out.println("找不到远程服务器，请确定服务器已经启动！");
			closeRs();
			System.exit(1);
		} catch (IOException e){
			closeRs();
			System.exit(1);
		}	
	}
	
	private void closeRs() {
		try {
			if (ps != null) ps.close();
			if (bt != null) bt.close();
			if (sock != null) sock.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public boolean login() {
		try {
			//setup a name
			System.out.println("setup your name:");
			String name = bt.readLine();
			ps.println(name);
			
			InputStream result = sock.getInputStream();
			byte[] arr = new byte[1];
			result.read(arr);
			if (arr[0] == '0') {//合法
				System.out.println("login success");
				return true;
			} else {               //非法
				System.out.println("name have exisit, please rename");
				return false;
			}	
		} catch (IOException ex) {
			closeRs();
			System.exit(1);
		}
		return false;
	}
	
	public void readDate() {
		try {
		//read command/data from server
		new ClientThread(sock).start();	
		} catch (IOException ex) {
			closeRs();
			System.exit(1);
		}
	}
	
	public void readFromKey() {
		try {
			System.out.println("input client:date ");
			//BufferedReader bt= new BufferedReader(new InputStreamReader(System.in));
			//PrintStream ps = new PrintStream(sock.getOutputStream());
			String content = null;
			while ((content = bt.readLine()) != null) {
				//write to server--format clientname:data
				ps.println(content);
			}
		} catch(IOException ex) {
			closeRs();
			System.exit(1);
		}	
	}
	
	public static void main(String[] args) {
		MyClient client = new MyClient();
		while(!client.login());
		client.readDate();
		client.readFromKey();	
	}
}
