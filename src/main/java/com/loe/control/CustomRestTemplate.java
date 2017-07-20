/*=====================================================================
 * 작 업 명  : 사용자 정의 RestTemplate
 * 파 일 명  : CustomRestTemplate.java  
 * 작 업 자  : 민민식
 * 작 업 일  : 2016-07-27
 * 변경이력 
   2016-07-27 / 민민식 / 최초작성
=======================================================================*/
package com.loe.control;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class CustomRestTemplate extends RestTemplate {

	private String callMethod;
	
	public CustomRestTemplate(String method) {
		this.callMethod = method;
	}
	
	public CustomRestTemplate(String method, int timeout) {
		this.callMethod = method;
		
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setConnectTimeout(timeout);
		this.setRequestFactory(factory);
	}
	
	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
			Class<T> responseType, Object... uriVariables) throws RestClientException {
		
		logger.debug(String.format("%s : Request Url [HttpMethod] : %s  [ %s ]", callMethod, url, method.toString()));		
		logger.debug(String.format("%s : Request Headers : %s", callMethod, requestEntity.getHeaders().toSingleValueMap())); 
		logger.debug(String.format("%s : Request Body : %s", callMethod, requestEntity.getBody()));
		ResponseEntity<T> result = super.exchange(url, method, requestEntity, responseType, uriVariables); 
		logger.debug(String.format("%s : Response Status : %s %s", callMethod, result.getStatusCode().value(), result.getStatusCode().name()));
		logger.debug(String.format("%s : Response Headers : %s", callMethod, result.getHeaders().toSingleValueMap())); 
		logger.debug(String.format("%s : Response Body : %s", callMethod, result.getBody()));
		return result;
	}
	
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
			Class<T> responseType, MultiValueMap<String, String> params) throws RestClientException {

		UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(url).queryParams(params).build();
		url = uriComponents.toUriString();		
		logger.debug(String.format("%s : uriComponents : %s", callMethod, url));
		return this.exchange(url, method, requestEntity, responseType);
	}	
	
}
