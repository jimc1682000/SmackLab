package tw.jimmy.lab;

import java.io.IOException;
import java.util.Scanner;

import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

public class Smack {
	private XMPPTCPConnection conn;
	private Chat chat;
	private String user;
	private String password;
	private String otherUid;

	public Smack(String user, String password, String otherUid) {
		super();
		this.user = user;
		this.password = password;
		this.otherUid = otherUid;
		conn = createConnection();
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("user: ");
		// user1, user2
		String user = scanner.nextLine();
		// default password: password
		System.out.print("password: ");
		String password = scanner.nextLine();
		System.out.print("The user you want to chat: ");
		// user1@jim, user2@jim, admin@jim
		String otherUid = scanner.nextLine();
		Smack smack = new Smack(user, password, otherUid);
		// Smack smack = new Smack("user2", "password", "admin@jim");
		System.out.println("Start Chatting!");

		smack.init();
		smack.send("YO~~~~~~~");
		// 跑一個迴圈，掃使用者輸入的字串並傳出
		try {
			while (true) {
				String msg = scanner.nextLine();
				if (msg.equals("exit"))
					break;
				smack.send(msg);
			}
		} finally {
			scanner.close();
			smack.disconnect();
		}
	}

	public void init() {
		connectAndLogin();
		chat = ChatManager.getInstanceFor(conn).createChat(otherUid);
		// 監聽所有人傳進來的訊息
		ChatManager.getInstanceFor(conn).addChatListener(
				createChatManagerListener());
	}

	private ChatManagerListener createChatManagerListener() {
		ChatManagerListener chatManagerListener = new ChatManagerListener() {
			@Override
			public void chatCreated(Chat chat, boolean createdLocally) {
				chat.addMessageListener(new ChatMessageListener() {
					@Override
					public void processMessage(Chat chat, Message message) {
						System.out.println(message.getBody());
					}
				});
			}
		};
		return chatManagerListener;
	}

	private void send(String msg) {
		try {
			chat.sendMessage(msg);
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}

	private void disconnect() {
		conn.disconnect();
	}

	private void connectAndLogin() {
		try {
			conn.connect();
			conn.login();
		} catch (SmackException | IOException | XMPPException e) {
			e.printStackTrace();
		}
	}

	private XMPPTCPConnection createConnection() {
		return new XMPPTCPConnection(XMPPTCPConnectionConfiguration.builder()
				.setUsernameAndPassword(user, password).setHost("localhost")
				.setPort(5222).setServiceName("jim")
				.setSecurityMode(SecurityMode.disabled).build());
	}

}
