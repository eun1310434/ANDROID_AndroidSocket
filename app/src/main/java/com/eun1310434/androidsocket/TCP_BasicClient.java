/*=====================================================================
□ Infomation
  ○ Data : 20.03.2018
  ○ Mail : eun1310434@naver.com
  ○ Blog : https://blog.naver.com/eun1310434
  ○ Reference
     - Do it android app Programming
     - 쉽게 배우는 소프트웨어 공학
     - Java Documentation
     - 헬로 자바 프로그래밍
     - programmers.co.kr

□ Function
  ○ Client
  ○ IP 관리 - 목적지 주소 입력
  ○ Data 전송
  ○ Buffer 비우기 - PrintWriter에는 자체적으로 flush기능이 있음
  ○ PrintWriter 종료
  ○ 소켓 종료


□ Study
  ○ Network
    - Protocol : 규약, 약속, 보내고 받기위한 약속
    - Port : 한대의 컴퓨터에 포트는 14만여개 있음, TCP 포트와 UDP 포트가 있음
    - TCP/IP(Transmission Control Protocol) : 부하가 높으나 안전성이 높음
    - UDP(User Datagram Protocol)
    - Multicast : UDP의 확장버젼
    - RMI(Remote Method Invocation)

  ○ structure
                컴퓨터A[응용계층(FTP, 텔넷, HTTP) → 전송계층(TCP,UDP) → 네트워크 계층(IP) → 링크계층(이더넷 토큰링) → 물리계층(케이블)]
     → 컴퓨터B[물리계층(케이블) → 링크계층(이더넷 토큰링) → 네트워크 계층(IP) → 전송계층(TCP,UDP) → 응용계층(FTP, 텔넷, HTTP)]

  ○ 2-tier C/S 모델
     : 클라이언트와 서버가 일대일로 연결하는 방식
       01) 클라이언트 → (요청, Request) → 서버
       02) 서버  → (응답, Response) → 클라이언트

  ○ 3-tier 모델
     : 서버를 좀 더 유연하게 구성, 응용 서버와 데이터 서버로 구성하는 경우 데이터베이스를 분리
       01) 클라이언트 → (요청, Request) → 응용서버 → (데이터 요청, Request) → 데이터 서버
       02) 데이터서버  → (데이터 응답, Response) → 응용서버 → (응답, Response) → 클라이언트

  ○ 안드로이드는 비연결성(stateless) 특성으로 인해 실시간으로 데이터를 처리하는 애플리케이션의 경우, 응답속도를 높이기 위해 HTTP보다 소켓 연결 선호

  ○ 안드로이드의 소켓연결방식
      - 안드로이드에서 표준 자바의 소켓을 그대로 사용할 수 있음
      - 서버쪽에는 서버소켓을 만들어 실행함(포트지정)
      - 클라이언트쪽에서는 소켓을 만들어 서버소켓으로 연결함(IP와 포트 지정)
      - Stream 객체를 이용해 데이터를 보내거나 받을 수 있음

  ○ 안드로이드는 반드시 스레드를 사용하여 네트워킹함
      - 최신 버전의 안드로이드에서는 네트워킹을 사용할 때는 반드시 스레드를 사용하도록 변경됨

  ○ 안드로이드는 스레드를 사용하므로 UI 업데이트를 위해서는 반드시 핸들러 사용
      - post() 메소드 사용 가능
      - 작동방법
        01) 클라이언트 (생성) → 스레드
        02) 스레드 (요청)  → 서버 (응답) → 스레드
        03) 스레드 (post)  → UI
=====================================================================*/
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
    int port;


    //인터페이스를 활용하여 변경시 연결을 위한 리스너 새로 정의
    //innerClass
    public interface OnClientListener {
        void ConnectionResult(int result);
    }
    OnClientListener client_listener;

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
                            client_listener.ConnectionResult(1);
                        } catch (IOException e) {
                            e.printStackTrace();
                            ConnectionCheck = false;
                            client_listener.ConnectionResult(2);
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
                            client_listener.ConnectionResult(3);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
    }

    public boolean getConntionCheck(){
        return ConnectionCheck;
    }
}
