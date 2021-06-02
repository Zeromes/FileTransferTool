import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class FileReceiver {

	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		if(args.length!=1) {
			System.out.println("参数错误！请以要存放文件的目录作为参数！");
		}
		else {
			try
			{
				Process p = Runtime.getRuntime().exec("netsh advfirewall firewall add rule name= \"Open Port 61111 in\" dir=in action=allow protocol=UDP localport=61111 remoteport=61110");
				p = Runtime.getRuntime().exec("netsh advfirewall firewall add rule name= \"Open Port 61111 out\" dir=out action=allow protocol=UDP localport=61111 remoteport=61110");
			}
			catch (Exception e)
			{
			    e.printStackTrace();
			}
			try(DatagramSocket socket = new DatagramSocket(61111);){
				File dir = new File(args[0]);
				if(dir.exists()) {
					if(!dir.isDirectory()) {
						System.out.println("文件目录形式有误！");
						System.exit(0);
					}
				}
				else {
					dir.mkdirs();
				}
				System.out.println("文件将存放在：" + dir.getCanonicalPath());
				System.out.println("等待接收文件...");
	    		DatagramPacket receivePack = new DatagramPacket(new byte[8192], 8192);
				socket.receive(receivePack);
				//收到的第一个包包含文件信息
				String fileInfo = new String(receivePack.getData(),0,receivePack.getLength());
				long fileLength = Long.parseLong(fileInfo.split(";")[0]);
				String fileName = fileInfo.split(";")[1];
				File file = new File(dir.getCanonicalPath()+"\\"+fileName);
				socket.setSoTimeout(5000);
				try(OutputStream fileInput = new FileOutputStream(file);){
					DatagramPacket InfoConfirmPack = new DatagramPacket(new byte[1], 0, 1, receivePack.getSocketAddress());
					socket.send(InfoConfirmPack);
					long receivedLength = 0;
					while(true) {
						receivePack = new DatagramPacket(new byte[8192], 8192);
						socket.receive(receivePack);
						if(new String(receivePack.getData(),0,receivePack.getLength()).equals("OVER")) {
							System.out.print("\n接收完毕!");
							break;
						}
						else {
							fileInput.write(receivePack.getData(),0,receivePack.getLength());
							DatagramPacket confirmPack = new DatagramPacket(new byte[1], 0, 1, receivePack.getSocketAddress());
							socket.send(confirmPack);
							receivedLength+=receivePack.getLength();
							System.out.print("\r已接收" + receivedLength + "bytes " + receivedLength*100/fileLength + "%");
						}
					}
				}
				catch(SocketTimeoutException e) {
					System.out.println("\n无法连接到接收方！");
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
