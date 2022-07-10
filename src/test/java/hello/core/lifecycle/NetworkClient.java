package hello.core.lifecycle;

public class NetworkClient {

    private String url;

    public NetworkClient(){
        System.out.println("생성자의 호출,url = " + url);

    }

    public void setUrl(String url) {
        this.url = url;
    }

    //서비스 시작시 호출
    public void connect(){
        System.out.println("connect: " + url);
    }

    public void call(String message)
    {
        System.out.println("call = " + url + "message = " + message);
    }

    //서비스 종료시 호출
    public void disconnect(){
        System.out.println("close " + url);
    }


    public void init(){
        //의존관계 주입 끝나고 실행된다
        System.out.println("init");
        connect();
        call("초기화 연결 메세지");
    }

    public void close(){
        //빈 종료될때 호출System.out.println("close");
       disconnect();
    }
}
