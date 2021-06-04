import java.io.*;
import java.net.*;
import java.util.Scanner;

public class FileSender {
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		
		if(args.length!=2) {
			System.out.println("参数错误！请以接收方ip地址和文件路径作为参数！");
		}
		else {
			File file = new File(args[1]);
			long fileLength = file.length();
			try(DatagramSocket socket = new DatagramSocket(61110);
				InputStream fileInput = new FileInputStream(file);){
				socket.setSoTimeout(5000);
				String fileInfo = fileLength + ";" + file.getName();
				DatagramPacket infoPack = new DatagramPacket(fileInfo.getBytes(), 0, fileInfo.getBytes().length, InetAddress.getByName(args[0]), 61111);
				socket.send(infoPack);
				System.out.println("已发送文件信息，等待确认...");
				DatagramPacket infoConfirmPack = new DatagramPacket(new byte[1], 1);
				socket.receive(infoConfirmPack);
//				while(true) {
//					try {
//						infoConfirmPack = new DatagramPacket(new byte[1], 1);
//						socket.receive(infoConfirmPack);
//						if(new String(infoConfirmPack.getData(),0,infoConfirmPack.getLength()).equals("ERROR")) {
//							//对方没有收到上一个数据包
//							socket.send(infoPack);
//						}
//						else {
//							//收到补发的包
//							break;
//						}
//					}
//					catch(SocketTimeoutException e) {
//						DatagramPacket errorPack = new DatagramPacket("ERROR".getBytes(), 0, "ERROR".getBytes().length, InetAddress.getByName(args[0]), 61111);
//						socket.send(errorPack);
//					}
//				}
				System.out.println("接收方已确认信息，准备传输");
				long sendLength=0;
				byte[] data = new byte[8192];
				int readLength = fileInput.read(data);
				sendLength+=readLength;
				while(readLength!=-1) {
					DatagramPacket sentPack = new DatagramPacket(data, 0, readLength, InetAddress.getByName(args[0]), 61111);
					socket.send(sentPack);
					DatagramPacket confirmPack = new DatagramPacket(new byte[1], 1);
					socket.receive(confirmPack);
//					while(true) {
//						try {
//							confirmPack = new DatagramPacket(new byte[1], 1);
//							socket.receive(confirmPack);
//							if(new String(infoConfirmPack.getData(),0,infoConfirmPack.getLength()).equals("ERROR")) {
//								//对方没有收到上一个数据包
//								socket.send(sentPack);
//							}
//							else {
//								//收到补发的包
//								break;
//							}
//						}
//						catch(SocketTimeoutException e) {
//							DatagramPacket errorPack = new DatagramPacket("ERROR".getBytes(), 0, "ERROR".getBytes().length, InetAddress.getByName(args[0]), 61111);
//							socket.send(errorPack);
//						}
//					}
					System.out.print("\r已发送"+sendLength+"bytes "+sendLength*100/fileLength+"%");
					readLength = fileInput.read(data);
					sendLength+=readLength;
				}
				DatagramPacket overPack = new DatagramPacket("OVER".getBytes(), 0, "OVER".getBytes().length, InetAddress.getByName(args[0]), 61111);
				socket.send(overPack);
				System.out.print("\n发送完毕！");
			}
			catch(UnknownHostException e) {
				System.out.println("ip地址有误！");
			}
			catch(FileNotFoundException e) {
				System.out.println("未找到文件！");
			}
			catch(SocketTimeoutException e) {
				System.out.println("连接超时！");
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			System.out.println("程序已退出");
		}
	}
}