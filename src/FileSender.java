import java.io.*;
import java.net.*;

public class FileSender {
	
	public static void main(String[] args) throws Exception{
		// TODO �Զ����ɵķ������
		if(args.length!=2) {
			System.out.println("�����������Խ��շ�ip��ַ���ļ�·����Ϊ������");
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
					System.out.print("\r�ѷ���"+sendLength+"bytes "+sendLength*100/fileLength+"%");
					readLength = fileInput.read(data);
					sendLength+=readLength;
				}
				DatagramPacket overPack = new DatagramPacket("OVER".getBytes(), 0, "OVER".getBytes().length, InetAddress.getByName(args[0]), 61111);
				socket.send(overPack);
				System.out.print("\n������ϣ�");
			}
			catch(UnknownHostException e) {
				System.out.println("ip��ַ����");
			}
			catch(FileNotFoundException e) {
				System.out.println("δ�ҵ��ļ���");
			}
			catch(SocketTimeoutException e) {
				System.out.println("\n�޷����ӵ����շ���");
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}