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
		// TODO �Զ����ɵķ������
		if(args.length!=1) {
			System.out.println("������������Ҫ����ļ���Ŀ¼��Ϊ������");
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
						System.out.println("�ļ�Ŀ¼��ʽ����");
						System.exit(0);
					}
				}
				else {
					dir.mkdirs();
				}
				System.out.println("�ļ�������ڣ�" + dir.getCanonicalPath());
				System.out.println("�ȴ������ļ�...");
	    		DatagramPacket receivePack = new DatagramPacket(new byte[8192], 8192);
				socket.receive(receivePack);
				//�յ��ĵ�һ���������ļ���Ϣ
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
							System.out.print("\n�������!");
							break;
						}
						else {
							fileInput.write(receivePack.getData(),0,receivePack.getLength());
							DatagramPacket confirmPack = new DatagramPacket(new byte[1], 0, 1, receivePack.getSocketAddress());
							socket.send(confirmPack);
							receivedLength+=receivePack.getLength();
							System.out.print("\r�ѽ���" + receivedLength + "bytes " + receivedLength*100/fileLength + "%");
						}
					}
				}
				catch(SocketTimeoutException e) {
					System.out.println("\n�޷����ӵ����շ���");
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
