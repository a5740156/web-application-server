package webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.DataBase;
import model.User;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
        	
        	HttpRequest request = new HttpRequest(in);
        	HttpResponse response = new HttpResponse(out);
        	String path = getDefaultPath(request.getPath());
        	
            if("/user/create".equals(path) || "/create".equals(path)) {
            	            	
            	User user = new User(
            			request.getParameter("userId"),
            			request.getParameter("password"),
            			request.getParameter("name"),
            			request.getParameter("email"));
            	
            	log.debug("User : {}" , user);
            	DataBase.addUser(user);
               response.sendRedirect("/index.html");
            }
            else if("/login".equals(path)) {            	
            	User user = DataBase.findUserById(request.getParameter("userId"));
            	if(user != null) {
            		if(user.getPassword().equals(request.getParameter("password"))) {
                		log.debug("login Success ! ");
                		 response.addHeader("Set-Cookie", "logined=true");
                		 response.sendRedirect("/index.html");
                	}else {
                		log.debug("Password MisMach ! ");
                		 response.sendRedirect("/user/login_failed.html");
                	}
            	}else {
            		response.sendRedirect("/user/login_failed.html");
            	}
            	
            }
            else if(path.endsWith(".css")) {
            	responseCssResource(out, path);
            }
            else{
            	responseResouece(out, path);
            }
            
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void response302HeaderWithCookie(DataOutputStream dos , String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: index.html \r\n");
            dos.writeBytes("Set-Cookie: "+cookie+"\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: index.html \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void response200HeaderWithCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private String getDefaultPath(String path) {
    	
    	if(path.equals("/")) {
    		return "/index.html";
    	}
    	
    	return path;
    }
    
    private boolean isLogin(String cookieValue) {
    	Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieValue);
    	String value = cookies.get("logined");
    	if(value == null) {
    		return false;
    	}
    	return Boolean.parseBoolean(value);
    }
    
    private void responseResouece(OutputStream out, String url) throws IOException {
    	
  		DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        // my test 
        // String url =  indexTest(in);
        // byte[] body = Files.readAllBytes(new File( "./webapp"+url).toPath());
        response200Header(dos, body.length);
        responseBody(dos, body);
    }
    
    private void responseCssResource(OutputStream out , String url) throws IOException {
    	DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        // my test 
        // String url =  indexTest(in);
        // byte[] body = Files.readAllBytes(new File( "./webapp"+url).toPath());
        response200HeaderWithCss(dos, body.length);
        responseBody(dos, body);
    }
    
}
