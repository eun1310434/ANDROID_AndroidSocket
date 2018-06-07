/*==================================================================================================
□ INFORMATION
  ○ Data : Thursday - 07/06/18
  ○ Mail : eun1310434@naver.com
  ○ WebPage : https://eun1310434.github.io/
  ○ Reference
     - Do it android app Programming

□ Function
   ○ Process
      01) MainActivity : onCreate() -> TCP_BasicServer : start()
                                    -> TCP_BasicServer : quit()
                                    -> TCP_BasicServer : get_server_ing()

      02) MainActivity : onCreate() -> TCP_BasicClient : start()
                                    -> TCP_BasicClient : send()
                                    -> TCP_BasicClient : quit()
                                    -> TCP_BasicClient : get_client_ing()


   ○ Unit
      - public class MainActivity extends AppCompatActivity
        01) protected void onCreate(Bundle savedInstanceState)

      - public class TCP_BasicServer
        01) public TCP_BasicServer(int _port, OnServerListener _server_listener)
        02) public void start()
        03) public void quit()
        04) public boolean get_server_ing()

      - public interface  TCP_BasicServer.OnServerListenerprogress
        01) public void printLog(String tag, String msg)

      - public class TCP_BasicClient
        01) public TCP_BasicClient(int _port, OnClientListener _client_listener)
        02) public void connection()
        03) public void send(final String message)
        04) public void quit()
        05) public boolean get_client_ing()

      - public interface  TCP_BasicClient.OnClientListener
        01) public void printLog(String tag, String msg)

□ Study
  ○ Network
    - Protocol : 규약, 약속, 보내고 받기위한 약속
    - Port : 한대의 컴퓨터에 포트는 14만여개 있음, TCP 포트와 UDP 포트가 있음
    - TCP/IP(Transmission Control Protocol) : 부하가 높으나 안전성이 높음
    - UDP(User Datagram Protocol)
    - Multicast : UDP의 확장버전
    - RMI(Remote Method Invocation)

  ○ structure
     - Computer A
        01) Application Layer (응용계층) : FTP, 텔넷, HTTP
        02) Transport Layer (전송계층) : TCP, UDP
        03) Network Layer (네트워크 계층) : IP
        04) Link Layer : 링크계층 (이더넷 토큰링)
        05) Physical Layer : 물리계층 (케이블)

     → Computer B
        01) Physical Layer : 물리계층 (케이블)
        02) Link Layer : 링크계층 (이더넷 토큰링)
        03) Network Layer : 네트워크 계층 (IP)
        04) Transport Layer : 전송계층 (TCP,UDP)
        05) Application Layer : 응용계층 (FTP, 텔넷, HTTP)

  ○ 2-tier C/S Architecture
     : 클라이언트와 서버가 일대일로 연결하는 방식
       01) Client → (Request) → Server
       02) Server → (Response) → Client

  ○ 3-tier Architecture
     : 서버를 좀 더 유연하게 구성, 응용 서버와 데이터 서버로 구성하는 경우 데이터베이스를 분리
       01) Client → (Request) → Application Server → (Data Request) → DB-Server
       02) DB-Server  → (Data Response) → Application Server → (Response) → Client

  ○ 안드로이드는 비연결성(stateless) 특성으로 인해 실시간으로 데이터를 처리하는 애플리케이션의 경우,
     응답속도를 높이기 위해 HTTP보다 소켓 연결 선호

  ○ Android Socket Connection (안드로이드의 소켓 연결)
      - 안드로이드에서 표준 자바의 소켓을 그대로 사용할 수 있음
      - 서버쪽에는 서버소켓을 만들어 실행함(포트지정)
      - 클라이언트쪽에서는 소켓을 만들어 서버소켓으로 연결함(IP와 포트 지정)
      - Stream 객체를 이용해 데이터를 보내거나 받을 수 있음

  ○ 안드로이드는 반드시 스레드를 사용하여 네트워킹 함
      - 최신 버전의 안드로이드에서는 네트워킹을 사용할 때는 반드시 스레드를 사용하도록 변경 됨

  ○ 안드로이드는 스레드를 사용하므로 UI-Update 를 위해서는 반드시 Handler 사용
      - post() 메소드 사용 가능
      - 작동방법
        01) Client (production) → Thread
        02) Thread (request)  → Server (response) → Thread
        03) Thread (post)  → UI
==================================================================================================*/
package com.eun1310434.androidsocket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TCP_BasicClient {
    private Socket socket = null;
    private PrintWriter out = null;
    private boolean ConnectionCheck = false;
    private int port;

    //인터페이스를 활용하여 변경시 연결을 위한 리스너 새로 정의
    //innerClass
    public interface OnClientListener {
        void printLog(int result, String msg);
    }
    private OnClientListener client_listener;

    public TCP_BasicClient(int _port, OnClientListener _client_listener){
        this.port = _port;
        this.client_listener = _client_listener;
    }

    public void connection(){
        ConnectionCheck = true;
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //Connectoin Request
                            //IP 관리 - 목적지 주소 입력
                            //IP address for the given host name
                            //remote_addr = InetAddress.getByName("000.000.000.000");
                            InetAddress remote_addr = InetAddress.getLocalHost();//자신의 홈 IP 주소로 자체 통신
                            socket = new Socket(remote_addr, port);
                            out = new PrintWriter(
                                    new BufferedWriter(
                                            new OutputStreamWriter(socket.getOutputStream())));
                            client_listener.printLog(1,"SOCKET OPEN");
                        } catch (IOException e) {
                            e.printStackTrace();
                            ConnectionCheck = false;
                            client_listener.printLog(2,"ACCESS ERROR");
                        }
                    }
                }).start();
    }

    public void send(final String message){
        if(ConnectionCheck){
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            //Data 전송
                            out.println(message);

                            //Buffer 비우기 - PrintWriter에는 자체적으로 flush기능이 있음
                            out.flush();
                            client_listener.printLog(4, message);
                        }
                    }).start();
        }
    }

    public void quit()  {
        ConnectionCheck = false;
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //PrintWriter 종료
                            //out.close();

                            socket.shutdownOutput();

                            //소켓 종료
                            socket.close();
                            client_listener.printLog(3,"SOCKET CLOSE\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
    }

    public boolean get_client_ing(){
        return ConnectionCheck;
    }
}
