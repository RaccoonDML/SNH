package jimageprocessor;


import java.io.File;

public class test {
    public static void main(String[] args) {

        String dir = "python"+ File.separator +"data";
        //String dir = "python\\data";
        //boolean success = (new File(dir)).delete();
        delAllFile(dir);
        /*
        try {
            Socket socket = new Socket("127.0.0.1", 8888);
            System.out.println("Client start!");
            PrintWriter out = new PrintWriter(socket.getOutputStream()); // 输出，to 服务器 socket
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream())); // 输入， from 服务器 socket
            out.println("Client request! :-) ");
            out.flush(); // 刷缓冲输出，to 服务器
            System.out.println(in.readLine()); // 打印服务器发过来的字符串
            System.out.println("Client end!");
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
            }
        }
        return flag;
    }
}

