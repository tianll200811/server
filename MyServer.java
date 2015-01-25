package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class ServerThread extends Thread {
	private BufferedReader bt;
	private OutputStream ops;
	private Socket sock;
	
	public ServerThread(Socket sock) {
		try {
		this.sock = sock;
		this.bt = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		this.ops = sock.getOutputStream();
		} catch (IOException ex) {
			System.out.println("read sock error");
			closeResource();
		}
	}
	
	public void closeResource() {
		try {
			if (ops != null) ops.close();
			if (bt != null) bt.close();
			if (sock != null) sock.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void run() {
		String content = null;
		boolean login = false;
		try {
			while ((content = bt.readLine()) != null) {
				//user certificate,just at first time
				if (!login) {
					byte [] arr = new byte[1];
					if (MyServer.socketMap.containsKey(content)) {
						arr[0] = '1'; //error  exist
						ops.write(arr);
					} else {
						arr[0] = '0';//success 
						ops.write(arr); 
						MyServer.socketMap.put(content, sock);
						login = true;
					}	
					continue;
				}				
				System.out.println("from client:" + content);				
				//format from client-- clientname:data
				String Info[] = content.split(":");
				System.out.println(Info[0] + " " + Info[1]);
				if (Info[0].equals(new String("all"))) {
					for (Socket s : MyServer.socketList) {
						//检查该socket是否正常，不正常的应从 socketList 与 socketMap 中删除
						if (!s.getKeepAlive()) {
							MyServer.socketMap.remove(Info[0]);
							MyServer.socketList.remove(s);
							continue;
						}
						
						PrintStream ps = new PrintStream(s.getOutputStream());
						String srcName = MyServer.socketMap.getKeyByValue(this.sock);
						String msg = srcName + ":" + Info[1];
						ps.println(msg);
					}
				} else {
					// des_user exist
					if (MyServer.socketMap.containsKey(Info[0]))
					{
						Socket s = MyServer.socketMap.get(Info[0]);
						System.out.println(Info[0] + " status " + s.getKeepAlive());
						//检查该socket是否正常，不正常的应从 socketList 与 socketMap 中删除
						if (!s.getKeepAlive()) {
							MyServer.socketMap.remove(Info[0]);
							MyServer.socketList.remove(s);
							continue;
						}
						
						PrintStream ps = new PrintStream(s.getOutputStream());
						String srcName = MyServer.socketMap.getKeyByValue(this.sock);
						String msg = srcName + ":" + Info[1];
						ps.println(msg);
						//ps.println(Info[1]);//Info[1] stands date
					} else {
						System.out.println("des user not exists");
					}
				}
			}
		} catch (IOException e) {
			System.out.println("IO error");
			closeResource();
		} finally {
			System.out.println("finally clear resource");
			closeResource();
		}
	}
}

public class MyServer {
	public static ArrayList<Socket> socketList = new ArrayList<Socket>();
	public static HashMapEx<String, Socket> socketMap = new HashMapEx<String, Socket>();
	
	public static void main(String[] args) {
		//@SuppressWarnings("resource")
		try(ServerSocket serverSock = new ServerSocket(3000))
		{	
			while (true) {
				Socket sock = serverSock.accept();
				socketList.add(sock);
				new ServerThread(sock).start();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
