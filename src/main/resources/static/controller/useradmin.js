/*! For license information please see useradmin.js.LICENSE.txt */
layui.define(["table","form"],(function(e){layui.$;var t=layui.admin,i=layui.view,l=layui.table,n=layui.form;l.render({elem:"#LAY-user-manage",url:"./json/useradmin/webuser.js",cols:[[{type:"checkbox",fixed:"left"},{field:"username",title:"用户名",width:100},{field:"username",title:"职位",width:100},{field:"avatar",title:"头像",width:100,templet:"#imgTpl"},{field:"phone",title:"手机"},{field:"email",title:"邮箱"},{field:"sex",width:80,title:"性别"},{field:"jointime",title:"加入时间",sort:!0},{title:"操作",width:150,align:"center",fixed:"right",toolbar:"#table-useradmin-webuser"}]],page:!0,limit:30,height:"full-320",text:"对不起，加载出现异常！"}),l.on("tool(LAY-user-manage)",(function(e){var l=e.data;"del"===e.event?layer.prompt({formType:1,title:"敏感操作，请验证口令"},(function(t,i){layer.close(i),layer.confirm("真的删除行么",(function(t){e.del(),layer.close(t)}))})):"edit"===e.event&&t.popup({title:"编辑用户",area:["500px","450px"],id:"LAY-popup-user-add",success:function(e,t){i(this.id).render("user/user/userform",l).done((function(){n.render(null,"layuiadmin-form-useradmin"),n.on("submit(LAY-user-front-submit)",(function(e){e.field;layui.table.reload("LAY-user-manage"),layer.close(t)}))}))}})})),l.render({elem:"#LAY-user-back-manage",cols:[[{type:"checkbox",fixed:"left"},{field:"username",title:"登录名"},{field:"name",title:"真实姓名"},{field:"phone",title:"手机"},{field:"mobile",title:"电话"},{field:"company",title:"单位"},{field:"position",title:"职位"},{field:"createTime",title:"创建时间",sort:!0},{title:"操作",width:150,align:"center",fixed:"right",toolbar:"#table-useradmin-admin"}]],url:"/sys/user/findAll",headers:{Authorization:layui.data("layuiAdmin").Authorization},page:!0,limit:30,response:{statusCode:200},parseData:function(e){return{code:e.code,msg:e.msg,count:e.data.totalElements,data:e.data.content.filter((function(e){return"Y"!==e.isDelete}))}},done:function(e,t,i){console.log(e),console.log(t),console.log(i)},text:{none:"暂无数据"}}),l.on("tool(LAY-user-back-manage)",(function(e){var l=e.data;"del"===e.event?layer.confirm("确定删除此管理员？",(function(i){console.log(e),t.req({url:"/sys/user/delete",method:"DELETE",data:{username:l.username},done:function(t){console.log(t),200===t.code&&(e.del(),layer.close(i))}})})):"edit"===e.event&&t.popup({title:"编辑管理员",area:["420px","600px"],id:"LAY-popup-user-add",success:function(e,o){i(this.id).render("user/administrators/adminform",l).done((function(){n.render(null,"layuiadmin-form-admin"),n.on("submit(LAY-user-back-submit)",(function(e){var i=e.field;t.req({url:"/sys/user/update",method:"post",data:i,done:function(e){console.log(e),200===e.code&&(layui.table.reload("LAY-user-back-manage"),layer.close(o))}})}))}))}})})),l.render({elem:"#LAY-user-back-role",cols:[[{type:"checkbox",fixed:"left"},{field:"name",title:"角色名"},{field:"comments",title:"拥有权限"},{title:"操作",width:150,align:"center",fixed:"right",toolbar:"#table-useradmin-admin"}]],url:"/sys/role/findAll",headers:{Authorization:layui.data("layuiAdmin").Authorization},page:!0,limit:30,response:{statusCode:200},parseData:function(e){return{code:e.code,msg:e.msg,count:e.data.totalElements,data:e.data.content.filter((function(e){return"Y"!==e.isDelete}))}},done:function(e,t,i){console.log(e),console.log(t),console.log(i)},text:{none:"暂无数据"}}),l.on("tool(LAY-user-back-role)",(function(e){var l=e.data;"del"===e.event?layer.confirm("确定删除此角色？",(function(t){e.del(),layer.close(t)})):"edit"===e.event&&t.popup({title:"编辑角色",area:["500px","480px"],id:"LAY-popup-user-add",success:function(e,t){i(this.id).render("user/administrators/roleform",l).done((function(){n.render(null,"layuiadmin-form-role"),n.on("submit(LAY-user-role-submit)",(function(e){e.field;layui.table.reload("LAY-user-back-role"),layer.close(t)}))}))}})})),e("useradmin",{})}));