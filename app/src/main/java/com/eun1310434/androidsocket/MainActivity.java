/*=====================================================================
□ Infomation
  ○ Data : 20.03.2018
  ○ Mail : eun1310434@naver.com
  ○ Blog : https://blog.naver.com/eun1310434
  ○ Reference
     - Do it android app Programming

□ Function
  ○ Android Client 구성
  ○ Android Server (Service) 구성

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

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button server_button;
    TextView server_context;
    TCP_BasicServer server;

    Button client_send;
    Button client_connection;
    TCP_BasicClient client;

    int port = 1122;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();

        //Server
        //서버시작
        server_button = (Button) findViewById(R.id.server_button);
        server_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(server.get_server_ing()){
                    server_button.setText("Start");
                    server.quit();
                }else{
                    server_button.setText("Quit");
                    server.start();
                }
            }
        });

        //화면전시
        server_context = (TextView) findViewById(R.id.server_context);
        server = new TCP_BasicServer(port, new TCP_BasicServer.OnServerListener() {
            @Override
            public void SetContent(final String msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        server_context.append(msg+"\n");
                    }
                });
            }
        });


        //Client
        //전송
        client_send = (Button) findViewById(R.id.client_send);
        client_send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(client.getConntionCheck()){
                    client.send("Connection Check");
                }else{
                    Toast.makeText(getApplicationContext(), "서버에 접속하지 못했습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //접속 - 시작/종료
        client_connection = (Button) findViewById(R.id.client_connection);
        client_connection.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(client.getConntionCheck()){
                    client.quit();
                }else{
                    client.connection();
                }
            }
        });

        //접속
        client = new TCP_BasicClient(port, new TCP_BasicClient.OnClientListener() {
            @Override
            public void ConnectionResult(final int result) {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(result == 1){//접속성공
                            client_connection.setText("QUIT");

                        }else if(result == 2){//접속실패
                            client_connection.setText("CON");
                            Toast.makeText(getApplicationContext(), "서버가 꺼져있습니다.",Toast.LENGTH_SHORT).show();

                        }else if(result == 3){//접속종료
                            client_connection.setText("CON");

                        }
                    }
                });
            }
        });
    }

}
