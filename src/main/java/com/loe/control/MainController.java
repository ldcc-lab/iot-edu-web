package com.loe.control;

import java.io.IOException;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class MainController {
	@Value("${ldcc.startiot.url}")
	private String url;
	@Value("${ldcc.startiot.deviceid}")
	private String device_id;
	@Value("${ldcc.startiot.dkey}")
	private String dKey;
	@Autowired
	private SimpMessagingTemplate template;

	private static String paser(String body) throws Exception {
		JSONParser jsonParser = new JSONParser();
		JSONObject result = (JSONObject) jsonParser.parse(body);
		JSONObject sgn = (JSONObject) result.get("m2m:sgn");
		JSONObject nev = (JSONObject) sgn.get("nev");
		JSONObject rep = (JSONObject) nev.get("rep");
		JSONObject om = (JSONObject) nev.get("om");
		if (om.get("op").toString().equals("1")) {
			JSONObject cin = (JSONObject) rep.get("m2m:cin");
			return (String) cin.get("con");
		}else{
			return "error";
		}
	}

//	@RequestMapping(value = "/dashboard", method = RequestMethod.POST)
//	@ResponseStatus(value = HttpStatus.OK)
//	public void dashboard(@RequestBody String body, @RequestHeader HttpHeaders headers) throws Exception {
//		String content = paser(body);
//		System.out.println("in dashboard " + content);
//		if (content.equals("4")) {
//			System.out.println("contentInstance is Deleted");
//		} else {
//			// content = "{ \""+content.split("'")[1]+"\" :
//			// \""+content.split("'")[3]+"\" , \""+content.split("'")[5]+"\" :
//			// \""+content.split("'")[7]+"\" , \""+content.split("'")[9]+"\" :
//			// \""+content.split("'")[11]+"\" }";
//			//content = new String(Base64Utils.decodeFromString((String) content));
//
//			HttpEntity<String> entity = new HttpEntity<String>(content, headers);
//			this.template.convertAndSend("/dashboard", entity);
//		}
//
//	}
     
	
	  
	@RequestMapping(value = "/m2m", method = RequestMethod.POST) // 서버에서 보내온 정보를
	@ResponseStatus(value = HttpStatus.OK)
	public void notify(@RequestBody String body, @RequestHeader HttpHeaders headers) throws Exception {
		String desUrl = "http://211.249.60.59:18080/~/charlot/base/controller-0001000100010001_hue";
		HashMap returnHm = (HashMap) this.parsingSub(body, "0001000100010001_hue");
		String cmdName= "switch";
		String cmd = "0";
        if(returnHm.get("con").equals("0")){//opebn
    	    cmdName = "lamp2";
    		 cmd = "0";
    		 //sendMesageAPI("창문이 닫혔습니다.");
    		 //창문이 닫혔습니다.
        }else{
    	     cmdName = "lamp2";
    		 cmd = "1";
    		 //창문이 열렸습니다.
    		 //sendMesageAPI("창문이 열렸습니다.");
        }
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPut httpPut = new HttpPut(desUrl);
			httpPut.setHeader("X-M2M-RI", "RQI0001"); // 리퀘스트 ID
			httpPut.setHeader("X-M2M-Origin", "/S0001000100010001_hue"); // 제어자 이름
			httpPut.setHeader("Accept", "application/json");
			httpPut.setHeader("Authorization", "be4a2703-91c9-3c56-985f-2242e5a0b838");
			httpPut.setHeader("Content-Type", "application/vnd.onem2m-res+json");
			String body2 = "{ \"m2m:mgc\": {\"cmt\": 4,\"exra\": { \"any\":[{\"nm\" :\"" + cmdName + "\", \"val\" : \"" + cmd
					+ "\"}]},\"exm\" : 1,\"exe\":true,\"pexinc\":false}}";
			System.out.println(body2);
			httpPut.setEntity(new StringEntity(body2));

			CloseableHttpResponse res = httpclient.execute(httpPut);

			try {
				if (res.getStatusLine().getStatusCode() == 200) {
					org.apache.http.HttpEntity entity = (org.apache.http.HttpEntity) res.getEntity();
					System.out.println(EntityUtils.toString(entity));
				} else {
					System.out.println("eerr");
				}
			} finally {
				res.close();
			}
		} finally {
			httpclient.close();
		}


	}
	 
	public int sendMesageAPI(String message)throws Exception{
		String desUrl = "http://210.93.181.133:9090/v1/send/kakao-friend";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httpPost = new HttpPost(desUrl);
			//httpPost.setHeader("Authorization", "Basic Y2xhc3M6bm90X29wZW5fYXBp");
			httpPost.setHeader("Authorization", "Basic Y2xhc3M6c2VjcmV0MTIhQA==");
			httpPost.setHeader("Content-Type", "application/json; charset=EUC-KR");
			String body2 = "{ \"msg_id\" : \"iot\", \"dest_phone\" : \"01033492780\", \"send_phone\" : \"01033492780\", \"sender_key\" : \"d6b73318d4927aa80df1022e07fecf06c55b44bf\", \"msg_body\" : \""+message+"\", \"ad_flag\" : \"N\" }";
			
	        ByteArrayEntity entity = new ByteArrayEntity(body2.getBytes("UTF-8"));

			System.out.println(body2);
			httpPost.setEntity(entity);

			CloseableHttpResponse res = httpclient.execute(httpPost);

			try {
				if (res.getStatusLine().getStatusCode() == 200) {
					org.apache.http.HttpEntity entity2 = (org.apache.http.HttpEntity) res.getEntity();
					System.out.println(EntityUtils.toString(entity2));
				} else {
					System.out.println("eerr");
				}
			} finally {
				res.close();
			}
		} finally {
			httpclient.close();
		}
		return 0;
		
	}
	
	
	@RequestMapping(value = "/kakao/{id}", method = RequestMethod.POST)
	public void sendKaKao(@RequestBody String body, @PathVariable String id){
		System.out.print("template_id" + id + "  "); // 여기서 파싱을 하자.. 타입 별로..
		String kakao_key = "4FdPsS6XM1efrqrn9-3UaMQBF4REdbesprWRawo8BJ4AAAFc6M35xg";
			try {
				HashMap returnHm = (HashMap) this.parsingSub(body, id);
				String template_id ="";
				if(id.contains("door")){
					if(returnHm.get("con").equals("0")){
						template_id = "4488";
					}else{
						template_id = "4487";
					}
				}else if(id.contains("television")){
					if(returnHm.get("con").equals("1")){
						template_id="4489";
					}else{
						template_id="4490";
					}
				}
				String desUrl = "https://kapi.kakao.com/v1/api/talk/memo/send?template_id="+template_id;
				System.out.println("desurl : " + desUrl);
				CloseableHttpClient httpclient = HttpClients.createDefault();
				try {
					HttpPost httpPost = new HttpPost(desUrl);
					httpPost.setHeader("Authorization", "Bearer "+kakao_key);
					CloseableHttpResponse res = httpclient.execute(httpPost);

					try {
						if (res.getStatusLine().getStatusCode() == 200) {
							org.apache.http.HttpEntity entity = (org.apache.http.HttpEntity) res.getEntity();
							System.out.println(EntityUtils.toString(entity));
						} else {
							System.out.println("eerr");
						}
					} finally {
						res.close();
					}
				} finally {
					httpclient.close();
				}
				
			
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	
	
	
	@RequestMapping(value = "/dashboard/{id}", method = RequestMethod.POST)
	public void SubscribeData(@RequestBody String body, @PathVariable String id) {
		System.out.print("id:" + id + "  "); // 여기서 파싱을 하자.. 타입 별로..
		System.out.println("body:" + body);
		try {
			HashMap returnHm = (HashMap) this.parsingSub(body, id);
			if (!(returnHm.get("lt").equals("0"))) {
				this.template.convertAndSend("/dashboard", returnHm);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Map parsingSub(String body, String id) throws Exception {
		// TODO Auto-generated method stub
		HashMap returnMap = new HashMap();
		System.out.println("${Parsing}=" + body);
		JSONParser jsonParser = new JSONParser();
		JSONObject result = (JSONObject) jsonParser.parse(body);
		// System.out.println("-======================== : "
		// +(String)result.get("m2m:sgn"));
		JSONObject sgn = (JSONObject) result.get("m2m:sgn");
		JSONObject nev = (JSONObject) sgn.get("nev");
		JSONObject rep = (JSONObject) nev.get("rep");
		JSONObject cin = (JSONObject) rep.get("m2m:cin");
		JSONObject om = (JSONObject) nev.get("om");
		if (om.get("op").toString().equals("1")) {
			String resourceTime = (String) cin.get("lt"); // check
			String con = (String) cin.get("con");
			String cr = (String) cin.get("cr");
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
			Date d1 = format.parse(resourceTime);
			// nt_dashboard에서 디바이스 아이디 기반으로 rct를 찾아야한다.
			System.out.println("${con}:" + con);
			returnMap.put("lt", d1);
			returnMap.put("cr", cr);
			returnMap.put("con", con);
			returnMap.put("id", id);
			returnMap.put("timeMs", 0);
			returnMap.put("time", d1);
			return returnMap;
		} else {
			returnMap.put("lt", "0");
			return returnMap;
		}
	}

	//
	// @MessageMapping("/timeline") // 데모 페이지로 보냄.
	// @SendTo("/topic/subscribe")
	// public HttpEntity<String> timeline(@RequestBody String body) throws
	// Exception {
	// HttpHeaders headers = new HttpHeaders();
	// headers.setContentType(new MediaType("application", "json",
	// Charset.forName("UTF-8")));
	// HttpEntity<String> entity = new HttpEntity<String>(body, headers);
	// return entity;
	// }
	// @MessageMapping("/realwindow") // 데모 페이지로 보냄.
	// @SendTo("/topic/subscribe2")
	// public HttpEntity<String> realwindow(@RequestBody String body) throws
	// Exception {
	// HttpHeaders headers = new HttpHeaders();
	// headers.setContentType(new MediaType("application", "json",
	// Charset.forName("UTF-8")));
	// HttpEntity<String> entity = new HttpEntity<String>(body, headers);
	// return entity;
	// }

	public void sendMgmt(String url, String deviceName, String cmdName, String cmd, String cmdName1, String cmd1,
			String dKey) throws ParseException, IOException {
		// RP05 -> 전구
		String desUrl = url + "/controller-" + deviceName;
		System.out.println("desurl : " + desUrl);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPut httpPut = new HttpPut(desUrl);
			httpPut.setHeader("X-M2M-RI", "RQI0001"); // 리퀘스트 ID
			httpPut.setHeader("X-M2M-Origin", "/S" + deviceName); // 제어자 이름
			httpPut.setHeader("Accept", "application/json");
			httpPut.setHeader("Authorization", dKey);
			httpPut.setHeader("Content-Type", "application/vnd.onem2m-res+json");
			String body = "{ \"m2m:mgc\": {\"cmt\": 4,\"exra\": { \"any\":[{\"nm\" :\"" + cmdName + "\", \"val\" : \"" + cmd
					+ "\"}]},\"exm\" : 1,\"exe\":true,\"pexinc\":false}}";
			System.out.println(body);
			httpPut.setEntity(new StringEntity(body));

			CloseableHttpResponse res = httpclient.execute(httpPut);

			try {
				if (res.getStatusLine().getStatusCode() == 200) {
					org.apache.http.HttpEntity entity = (org.apache.http.HttpEntity) res.getEntity();
					System.out.println(EntityUtils.toString(entity));
				} else {
					System.out.println("eerr");
				}
			} finally {
				res.close();
			}
		} finally {
			httpclient.close();
		}

	}

	@RequestMapping(value = "/sendtoplug", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void sendToplug(@RequestBody String body, @RequestHeader HttpHeaders headers) throws Exception {
		System.out.println("in sendtoplug");
		System.out.println(body);
		if (body.equals("ON")) {
			sendMgmt(url, device_id, "switch", "ON", "switch1", "null", dKey);
		} else {
			sendMgmt(url, device_id, "switch", "OFF", "switch1", "null", dKey);
		}

	}

}
