var s = window.location.href;
var r = new RegExp("user_token=(.+)&.+", "g");
var m = r.exec(s);
var usertoken = m[1];
// alert(usertoken);
document.getElementById("user_token").value = usertoken;