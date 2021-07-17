import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @program: server
 * @description:
 * @author: radCircle
 * @create: 2021-07-17 16:45
 **/

public class httpServer implements Runnable{
    private static int port=8080;
    private ServerSocket serverSocket=null;

    // 初始化，绑定服务器socket运行端口
    private httpServer(){
        try{
            serverSocket = new ServerSocket(port);
        }catch(Exception e){
            System.out.println("httpServer occur an error while starting");
        }
        new Thread(this).start();
        System.out.println("server is start");
    }

    //服务器主线程
    @Override
    public void run(){
        while(true){
            try{
                Socket client=null;
                System.out.println("waiting for connect.....");
                client=serverSocket.accept();//io操作堵塞，直到有浏览器线程访问8080端口
                if(client!=null){
                    try{
                        System.out.println("connecting establish");
                        BufferedReader in=new BufferedReader(new InputStreamReader(
                                client.getInputStream()));
                        //截取请求头，判断操作类型和参数
                        String line=in.readLine();
                        String method=line.substring(line.indexOf("/")+1,line.indexOf("?"));
                        String partA =
                                line.substring(line.indexOf("?") + 1, line.indexOf("&"));
                        String partB = line.substring(line.indexOf("&")+1,line.indexOf("H")-1);
                        String a = partA.substring(partA.indexOf("=")+1);
                        String b = partB.substring(partB.indexOf("=")+1);

                        if(method.equals("add")){
                            output(client,add(a,b));
                        }
                        else if(method.equals("mult")){
                            output(client,multiple(a,b));
                        }
                        else{
                            output(client,wrongPattern());
                        }

                        closeSocket(client);
                    }catch(Exception e){
                        System.out.println("httpServer occur an error while running");
                    }
                }
            }catch (Exception e){
                System.out.println("httpServer occur an error before connecting");
            }
        }
    }

    private String add(String a,String b) {
        int sum=Integer.parseInt(a)+Integer.parseInt(b);
        return "The result of "+a+" + "+b+" is "+String.valueOf(sum);
    }

    private String multiple(String a,String b){
        int sum=Integer.parseInt(a)*Integer.parseInt(b);
        return "The result of "+a+" * "+b+" is "+String.valueOf(sum);
    }

    private String wrongPattern(){
        return "maybe the the url pattern is wrong";
    }

    //设置返回头刷新输出
    private void output(Socket client,String result) throws IOException {
        BufferedWriter out=new BufferedWriter(new OutputStreamWriter(
                client.getOutputStream()));
        String header="HTTP/1.0 200 OK\r\n"+
                "Content-length: "+result.length()+"\r\n"+
                "Content-type: text/plain\r\n\r\n";
        out.write(header);
        out.write(result);
        out.flush();
        out.close();
    }

    //关闭套接字
    private void closeSocket(Socket socket){
        try {
            socket.close();
        }catch(Exception e){
            System.out.println("error on close socket");
        }
    }

    public static void main(String[] args) throws Exception{
        if(args.length==1){
            port=Integer.parseInt(args[0]);
        }else{
            System.out.println("port 8080 start");
        }
        new httpServer();
    }
}
