import java.io.*;
import java.net.*;

public class FileSender {
	
	public static void main(String[] args) throws Exception{
		// TODO 自动生成的方法存根
		if(args.length!=2) {
			System.out.println("参数错误！请以接收方ip地址和文件路径作为参数！");
		}
		else {
			File file = new File(args[1]);
			long fileLength = file.length();
			try(DatagramSocket socket = new DatagramSocket(0);
				InputStream fileInput = new FileInputStream(file);){
				socket.setSoTimeout(5000);
				String fileInfo = fileLength + ";" + file.getName();
				DatagramPacket infoPack = new DatagramPacket(fileInfo.getBytes(), 0, fileInfo.getBytes().length, InetAddress.getByName(args[0]), 61111);
				socket.send(infoPack);
				DatagramPacket infoConfirmPack = new DatagramPacket(new byte[1], 1);
				socket.receive(infoConfirmPack);
				long sendLength=0;
				byte[] data = new byte[8192];
				int readLength = fileInput.read(data);
				sendLength+=readLength;
				while(readLength!=-1) {
					DatagramPacket sentPack = new DatagramPacket(data, 0, readLength, InetAddress.getByName(args[0]), 61111);
					socket.send(sentPack);
					DatagramPacket confirmPack = new DatagramPacket(new byte[1], 1);
					socket.receive(confirmPack);
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
				System.out.println("\n无法连接到接收方！");
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}