package webserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import util.IOUtils;

public class HttpRequest {
	private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
	
	private Map<String, String> headers = new HashMap<String, String>();
	private Map<String, String> params = new HashMap<String, String>();
	private RequestLine requestLine;
	
	public HttpRequest(InputStream in) {
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader( in , "UTF-8"));
			String line = br.readLine();
			if(line == null) {
				return;
			}
			
			requestLine = new RequestLine(line);
			
			line = br.readLine();
			while(!line.equals("")) {
				log.debug("headers : {} ", line);
				String[] tokens = line.split(":");
				headers.put(tokens[0].trim(), tokens[1].trim());
				line = br.readLine();
				
			}
			
			if("POST".equals(requestLine.getMehod())) {
				String body = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
				params = HttpRequestUtils.parseQueryString(body);
			}else {
				params = requestLine.getParam();
			}
		}catch (Exception e) {
			log.error(e.getMessage());
		}
		
	}
	
	public HttpMethod getMethod() {
		return requestLine.getMehod();
	}
	
	public String getPath() {
		return requestLine.getPath();
	}
	
	public String getParameter(String name) {
		return params.get(name);
	}
	
	public String getHeader( String name) {
		return headers.get(name);
	}
	

}
