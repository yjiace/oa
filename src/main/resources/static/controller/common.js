/*! For license information please see common.js.LICENSE.txt */
layui.define((function(e){layui.$,layui.layer,layui.laytpl,layui.setter,layui.view;var t=layui.admin;t.events.logout=function(){t.exit()},e("common",{})})),window.utils={format:function(e,t){e=e||"yyyy-MM-dd HH:mm:ss";var n={"M+":(t=t?new Date(t):new Date).getMonth()+1,"d+":t.getDate(),"H+":t.getHours(),"m+":t.getMinutes(),"s+":t.getSeconds(),"q+":Math.floor((t.getMonth()+3)/3),S:t.getMilliseconds()};for(var a in/(y+)/.test(e)&&(e=e.replace(RegExp.$1,(t.getFullYear()+"").substr(4-RegExp.$1.length))),n)new RegExp("("+a+")").test(e)&&(e=e.replace(RegExp.$1,1==RegExp.$1.length?n[a]:("00"+n[a]).substr((""+n[a]).length)));return e},formPost:function(e,t){var n=document.createElement("form");for(var a in n.action=e,n.target="_self",n.method="post",n.style.display="none",t){var o=document.createElement("textarea");o.name=a,o.value=t[a],n.appendChild(o)}document.body.appendChild(n),n.submit()},mime:{jpg:"image/jpg",gif:"mage/gif",jpg:"image/jpeg",jpe:"image/jpeg",jpeg:"image/jpeg",png:"image/png",xlsx:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",xls:"application/vnd.ms-excel",doc:"application/msword",docx:"application/vnd.openxmlformats-officedocument.wordprocessingml.document",txt:"text/plain"},addPermission:function(e){var t=layui.data("store");if(t.userInfo){console.log(t.userInfo);var n=t.userInfo.authorities.map((function(e){return e.authority}));return e.map((function(e){return n.forEach((function(t){e[t.trim()]=!0})),e}))}layer.msg("请先登录"),admin.exit()},hasPermission:function(e){var t=layui.data("store");if(t.userInfo)return console.log(t.userInfo),t.userInfo.authorities.map((function(e){return e.authority.trim()})).includes(e);layer.msg("请先登录"),admin.exit()}};