package webserver;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;

public class RequestLine {
	private static final Logger log = LoggerFactory.getLogger(RequestLine.class);
	
	
	private String path;
	private Map<String, String> params = new HashMap<String, String>();
	private HttpMethod method;
	
	public RequestLine(String requestLine) {
		
		log.debug("requestLine : {}" , requestLine);
		String[] tokens = requestLine.split(" ");
		method = HttpMethod.valueOf(tokens[0]);
		
		if(method == HttpMethod.POST) {
			path = tokens[1];
			return;
		}
		
		int index = tokens[1].indexOf("?");
		if(index == -1) {
			path = tokens[1];
		}else {
		  	path = tokens[1].substring(0, index);
		  	params = HttpRequestUtils.parseQueryString(tokens[1].substring(index + 1));
		}
		
		
	}

	public HttpMethod getMehod() {
		return method;
	}
	public String getPath() {
		return path;
	}
	
	public Map<String , String> getParam( ) {
		return params;
	}
	
}
