var page = {

	init : function(event) {

		page.initLayout();
		page.initInterface();
	},

	initLayout : function() {
		// 전체 상태 조회 후 반영
		// LEMPCore.App.openProgress();
		LEMP.IoT.getAllState({
			"_fCallback" : function(response) {

				LEMPCore.App.closeProgress();
				var lamp1_con = response.stateArray[0].con;
				var lamp2_con = response.stateArray[1].con;
				var lamp3_con = response.stateArray[2].con;
				var temp_power = response.stateArray[3].con;
				var temp_power2 = response.stateArray[4].con;
				var tv_power = response.stateArray[5].con;
				var jukebox_con = response.stateArray[6].con;
				// var tv_channel = response.stateArray[7].con;
				// var tv_volume = response.stateArray[8].con;


				if (lamp1_con == "1") {
					lightSingleOnSign("lamp1");
				} else {
					lightSingleOffSign("lamp1");
				}

				if (lamp2_con == "1") {
					lightSingleOnSign("lamp2");
				} else {
					lightSingleOffSign("lamp2");
				}

				if (lamp3_con == "1") {
					lightSingleOnSign("lamp3");
				} else {
					lightSingleOffSign("lamp3");
				}

				if (temp_power == "1") {
					tempPowerOnSign();
				} else {
					tempPowerOffSign();
				}
				
				if (temp_power2 == "1") {
					tempPowerOnSign2();
				} else {
					tempPowerOffSign2();
				}

				if (tv_power == "1") {
					tvPowerOnSign();
				} else {
					tvPowerOffSign();
				}
				
				
				// jukebox (개발해야함)
			}
		});
	},

	initInterface : function() {

		// ** Setting page open ** //
		$("#IPsetBtn").click(function() {
			LEMP.Window.open({
				"_sPagePath" : "MAN/MAN0002.html",
				"_sType" : "popup",
				"_sOrientation" : "land",
				"_sWidth" : "50%",
				"_sHeight" : "50%"
			})
		});

		// ** Light Control ** //
		$("#ligntBnt_all_on").click(function() {
			lightAllOn();
		});

		$("#ligntBnt_all_off").click(function() {
			lightAllOff();
		});

		$("#lightBtn_1").click(function() {
			lightControl("lamp1")
		});

		$("#lightBtn_2").click(function() {
			lightControl("lamp2");
		});

		$("#lightBtn_3").click(function() {
			lightControl("lamp3");
		});

		// ** Temperature Control ** //
		$("#tempBtn").click(function() {
			if ($("#tempBtn_Img").hasClass("superfloating-image-36")) {
				tempPowerOn();
			} else {
				tempPowerOff();
			}
		});

		$("#tempBtn2").click(function() {
			if ($("#tempBtn2_Img").hasClass("superfloating-image-100")) {
				tempPowerOn2();
			} else {
				tempPowerOff2();
			}
		});

		$("#tempUp").click(function() {
			tempUp();
		});

		$("#tempDown").click(function() {
			tempDown();
		});

		// ** TV Control ** //
		$("#tvBtn").click(function() {
			if ($("#tvBtn_Img").hasClass("superfloating-image-52")) {
				tvPowerOn();
			} else {
				tvPowerOff();
			}
		});

		$("#CHUp").click(function() {
			channelUp();
		});

		$("#CHDown").click(function() {
			channelDown();
		});

		$("#VolUp").click(function() {
			volumeUp();
		});

		$("#VolDown").click(function() {
			volumeDown();
		});

		// ** WebSocket **//

		var socket = new SockJS('http://norimsu1.dlinkddns.com:19904/timeline');

		stompClient = Stomp.over(socket);

		stompClient.connect({}, function(frame) {
			// alert("connected");
			console.log('Connected: ' + frame);
			stompClient.subscribe('/dashboard', function(message) {
				console.log(message);
				LEMP.Logger.info({
					"_sMessage" : message
				});
				var con = JSON.parse(message.body).con;
				var id = JSON.parse(message.body).id;
				LEMP.Logger.info({
					"_sMessage" : id
				});
				LEMP.Logger.info({
					"_sMessage" : con
				});
				if (id.includes("lamp")) { // lamp1 or lamp2 or lamp3
					if (con == '1') { // ON
						lightSingleOnSign(id);
					} else { // OFF
						lightSingleOffSign(id);
					}
				} 
				else if (id.includes("button")) { // tv의 power
					if (con == '1') { // poweron
						tvPowerOnSign();
					} else if (con == '0') { // poweroff
						tvPowerOffSign();
					}
				} else if (id.includes("fan")) { // fan
					if (con == '1') {
						tempPowerOnSign();
					} else if (con == '0') {
						tempPowerOffSign();
					}
				} else if(id.includes("heater")){
					if(con == '1'){
						tempPowerOnSign2();
					}else if(con=='0'){
						tempPowerOffSign2();
					}
				}

			});
		}, function(error) {
			LEMP.Logger.info({
				"_sMessage" : error
			});
			disconnect();
		});

	}

};

/*
 * -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= view
 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
 */

var lightSingleOnSign = function(lampID) {
	$("#" + lampID + "_Img").removeClass("superfloating-image-32").addClass(
			"superfloating-image-32-on");
	$("#" + lampID + "_Text span span span").text("ON");
};

var lightSingleOffSign = function(lampID) {
	$("#" + lampID + "_Img").removeClass("superfloating-image-32-on").addClass(
			"superfloating-image-32");
	$("#" + lampID + "_Text span span span").text("OFF");
};

var tempPowerOnSign = function() {
	$("#tempBtn_Img").removeClass("superfloating-image-36").addClass(
			"superfloating-image-36-on");
	$("#tempBtn_Text span span").text("전원 : ON");
};

var tempPowerOffSign = function() {
	$("#tempBtn_Img").removeClass("superfloating-image-36-on").addClass(
			"superfloating-image-36");
	$("#tempBtn_Text span span").text("전원 : OFF");
};

var tempPowerOnSign2 = function() {
	$("#tempBtn2_Img").removeClass("superfloating-image-100").addClass(
			"superfloating-image-100-on");
	$("#tempBtn2_Text span span").text("전원 : ON");
};

var tempPowerOffSign2 = function() {
	$("#tempBtn2_Img").removeClass("superfloating-image-100-on").addClass(
			"superfloating-image-100");
	$("#tempBtn2_Text span span").text("전원 : OFF");
};

var tvPowerOnSign = function() {
	$("#tvBtn_Img").removeClass("superfloating-image-52").addClass(
			"superfloating-image-52-on");
	$("#tvBtn_Text span span").text("TV 전원:ON");
};

var tvPowerOffSign = function() {
	$("#tvBtn_Img").removeClass("superfloating-image-52-on").addClass(
			"superfloating-image-52");
	$("#tvBtn_Text span span").text("TV 전원:OFF");
};

var setConTemp = function(con_temp) {
	$("#conTemp span span").html(con_temp);
};

var setWantTemp = function(want_temp) {
	$("#setTemp span span").html(want_temp);
};

var setWantTemp_2 = function(want_temp) {
	$("#setTemp_2 span span").html(want_temp);
};

/*
 * -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= control
 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
 */
/* (1) light Control */
var lightControl = function(lampID) {
	if ($("#" + lampID + "_Img").hasClass("superfloating-image-32")) {
		LEMP.IoT.controlDevice({
			"_aControlArray" : [ {
				"device" : lampID,
				"type" : "lamps",
				"state" : "1"
			} ],
			"_fCallback" : function(resControlDevice) {
				// alert(JSON.stringify(resControlDevice));
			}
		});
		lightSingleOnSign(lampID);

	} else {
		LEMP.IoT.controlDevice({
			"_aControlArray" : [ {
				"device" : lampID,
				"type" : "lamps",
				"state" : "0"
			} ],
			"_fCallback" : function(resControlDevice) {
				// alert(JSON.stringify(resControlDevice));
			}
		});
		lightSingleOffSign(lampID);
	}
};

var lightAllOn = function() {
	LEMP.IoT.controlDevice({
		"_aControlArray" : [ {
			"device" : "lamp1",
			"type" : "lamps",
			"state" : "1"
		}, {
			"device" : "lamp2",
			"type" : "lamps",
			"state" : "1"
		}, {
			"device" : "lamp3",
			"type" : "lamps",
			"state" : "1"
		} ],
		"_fCallback" : function(resControlDevice) {
			// alert(JSON.stringify(resControlDevice));
		}
	});
	lightSingleOnSign("lamp1");
	lightSingleOnSign("lamp2");
	lightSingleOnSign("lamp3");
};

var lightAllOff = function() {
	LEMP.IoT.controlDevice({
		"_aControlArray" : [ {
			"device" : "lamp1",
			"type" : "lamps",
			"state" : "0"
		}, {
			"device" : "lamp2",
			"type" : "lamps",
			"state" : "0"
		}, {
			"device" : "lamp3",
			"type" : "lamps",
			"state" : "0"
		} ],
		"_fCallback" : function(resControlDevice) {
			// alert(JSON.stringify(resControlDevice));
		}
	});
	lightSingleOffSign("lamp1");
	lightSingleOffSign("lamp2");
	lightSingleOffSign("lamp3");
};

/* (2) temperature Control */
var tempPowerOn = function() {
	LEMP.IoT.controlDevice({
		"_aControlArray" : [ {
			"device" : "power",
			"type" : "fan",
			"state" : "1"
		} ],
		"_fCallback" : function(resControlDevice) {
			// alert(JSON.stringify(resControlDevice));
		}
	});
	tempPowerOnSign(); // 뷰 전환
};

var tempPowerOff = function() {
	LEMP.IoT.controlDevice({
		"_aControlArray" : [ {
			"device" : "power",
			"type" : "fan",
			"state" : "0"
		} ],
		"_fCallback" : function(resControlDevice) {
			// alert(JSON.stringify(resControlDevice));
		}
	});
	tempPowerOffSign(); // 뷰전환
};

var tempPowerOn2 = function() {
	LEMP.IoT.controlDevice({
		"_aControlArray" : [ {
			"device" : "power",
			"type" : "heater",
			"state" : "1"
		} ],
		"_fCallback" : function(resControlDevice) {
			// alert(JSON.stringify(resControlDevice));
		}
	});
	tempPowerOnSign2(); // 뷰 전환
};

var tempPowerOff2 = function() {
	LEMP.IoT.controlDevice({
		"_aControlArray" : [ {
			"device" : "power",
			"type" : "heater",
			"state" : "0"
		} ],
		"_fCallback" : function(resControlDevice) {
			// alert(JSON.stringify(resControlDevice));
		}
	});
	tempPowerOffSign2(); // 뷰전환
};

var tempUp = function() {
	var setTemp = $("#setTemp_2 span span").text();
	setTemp++;
	$("#setTemp_2 span span").html(setTemp); // 뷰 전환
	LEMP.IoT.controlDevice({
		"_aControlArray" : [ {
			"device" : "temp2",
			"type" : "temp",
			"state" : setTemp
		} ],
		"_fCallback" : function(resControlDevice) {
			// alert(JSON.stringify(resControlDevice));
		}
	});

};

var tempDown = function() {
	var setTemp = $("#setTemp_2 span span").text();
	setTemp--;
	$("#setTemp_2 span span").html(setTemp); // 뷰 전환
	LEMP.IoT.controlDevice({
		"_aControlArray" : [ {
			"device" : "temp2",
			"type" : "temp",
			"state" : setTemp
		} ],
		"_fCallback" : function(resControlDevice) {
			// alert(JSON.stringify(resControlDevice));
		}
	});

};

/* (3) television Control */
var tvPowerOn = function() {
	LEMP.IoT.controlDevice({
		"_aControlArray" : [ {
			"device" : "power",
			"type" : "tv",
			"state" : "1"
		} ],
		"_fCallback" : function(resControlDevice) {
			// alert(JSON.stringify(resControlDevice));
		}
	});
	tvPowerOnSign(); // 뷰 전환
};

var tvPowerOff = function() {
	LEMP.IoT.controlDevice({
		"_aControlArray" : [ {
			"device" : "power",
			"type" : "tv",
			"state" : "0"
		} ],
		"_fCallback" : function(resControlDevice) {
			// alert(JSON.stringify(resControlDevice));
		}
	});
	tvPowerOffSign(); // 뷰 전환
};

var channelUp = function() {
	LEMP.IoT.controlDevice({
		"_aControlArray" : [ {
			"device" : "channel",
			"type" : "tv",
			"state" : "UP"
		} ],
		"_fCallback" : function(resControlDevice) {
			// alert(JSON.stringify(resControlDevice));
		}
	});
};

var channelDown = function() {
	LEMP.IoT.controlDevice({
		"_aControlArray" : [ {
			"device" : "channel",
			"type" : "tv",
			"state" : "DOWN"
		} ],
		"_fCallback" : function(resControlDevice) {
			// alert(JSON.stringify(resControlDevice));
		}
	});
};

var volumeUp = function() {
	LEMP.IoT.controlDevice({
		"_aControlArray" : [ {
			"device" : "volume",
			"type" : "tv",
			"state" : "UP"
		} ],
		"_fCallback" : function(resControlDevice) {
			// alert(JSON.stringify(resControlDevice));
		}
	});
};

var volumeDown = function() {
	LEMP.IoT.controlDevice({
		"_aControlArray" : [ {
			"device" : "volume",
			"type" : "tv",
			"state" : "DOWN"
		} ],
		"_fCallback" : function(resControlDevice) {
			// alert(JSON.stringify(resControlDevice));
		}
	});
};

