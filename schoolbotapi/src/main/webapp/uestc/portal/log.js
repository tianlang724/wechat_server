/**
 * Created by Administrator on 2017/4/7.
 */
var checkPhone = function (field){
with (field) {
if(!(/\d{9}$/.test(value))){
    return false;
} else {
      return true;
  }
}
};

var checkPassword = function (field) {
    console.log("hell");
with (field) {
/*  var pattern=/^[a-zA-Z]{1}([a-zA-Z0-9]|[._]){5,19}$/;*/

if (!/.{5,19}$/.test(value)) {
    return false;
} else {
      return true;
  }
}
};

var noUser = function (type,value) {
if ( type == "Username" ) {
if ( value != "admin") {
    return false
}
} else if ( type == "Password" ) {
if ( value != "17380151227") {
    return false;
}
}  else {
       return true;
   }


};

var validateInformation = function (field,alertTxt) {
with (field)
{

if ( value == null || value=="" )
{
//alert(alertTxt + " must be filled out!");
setElementStatus("alert_box","block");
    $('#alert_text').html(alertTxt + " must be filled out!");
    return false;
}
if( alertTxt == "Username" && checkPhone(field) == false ) {
setElementStatus("alert_box","block");
    $('#alert_text').html(alertTxt + " format is wrong, it must be your phone number.");
//alert(alertTxt + " format is wrong, it must be your phone number.");
    return false;
} else if( alertTxt == "Password" && checkPassword(field) == false ) {
/* alert(alertTxt + " format is wrong." +
     " length must be 6 to 20,and must be letter,number,dot,underline.");*/
setElementStatus("alert_box","block");
    $('#alert_text').html(alertTxt + " format is wrong, " +
"length must be 6 to 20,and must be letter,number,dot,underline.");
    return false;
}
return true;

}
};

var validateForm = function (form) {
with (form)
{
// checkPassword(password);
if ( validateInformation(username,"Username") == false )
{
    username.focus();
    username.select();
    return false;

} else if ( validateInformation(password,"Password") == false ) {
    password.focus();
    password.select();
    return false;
} else {
  setElementStatus("alert_box","none");
      var m_pwd = document.getElementById('m_password');
      m_pwd.value = encryption(password);
      console.log("m_pwd.value :");
      console.log(m_pwd.value);
  // psword.focus();
      return true;

  }

if ( noUser(username) ==true ) {
    username.focus();

}

}
};

var encryption = function (data) {
    return hex_md5(data);
};

var setElementStatus = function (id,state) {
    console.log("state" + state);
    document.getElementById(id).style.display = state;
};