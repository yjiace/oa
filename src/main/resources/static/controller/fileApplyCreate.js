layui.define(["table","admin","form","laytpl","upload"],(function(e){var a=layui.$,n=layui.admin,t=layui.table,o=layui.view,i=layui.form,l=layui.upload,r=layui.laytpl,u=[],c=null,s=layui.router();function d(e){r(a("#apply_list").html()).render(e.concat([{}]),(function(e){a("#apply_list_view").html(e),a(".applyList_del").on("click",(function(e){var n=Number(a(this).data("type"));layer.confirm("真的删除么",(function(e){u.splice(n,1),d(u),layer.close(e)}))})),a(".applyList_edit").on("click",(function(e){var l=Number(a(this).data("type"));n.popup({title:"选择审批人",area:["600px","430px"],id:"car_apply",success:function(e,a){o(this.id).render("approve/selectPeople",{}).done((function(){i.render(null,"createApply_form"),y(u.map((function(e){return e.name}))),t.on("tool(LAY-user-back-manage)",(function(e){var n=e.data;console.log(n),"edit"===e.event&&(u[l]={name:n.username,id:n.id},d(u),layer.close(a))}))}))}})})),a(".addPeople").on("click",(function(e){n.popup({title:"选择审批人",area:["600px","430px"],id:"car_apply",success:function(e,a){o(this.id).render("approve/selectPeople",{}).done((function(){i.render(null,"createApply_form"),y(u.map((function(e){return e.name}))),t.on("tool(LAY-user-back-manage)",(function(e){var n=e.data;console.log(n),"edit"===e.event&&(u.push({name:n.username,id:n.id}),d(u),layer.close(a))}))}))}})}))}))}function m(e){a(".fileList_div").show(),a("#filedList").html('<i class="layui-icon">&#xe655;</i>'+e.fileName)}s.search&&s.search.id&&n.reqAsync({url:"/approval/findOneById",method:"GET",data:{id:s.search.id}}).then((function(e){var n;200===e.code?(console.log(e.data),n={documentNumber:e.data.attachmentFiles[0].documentNumber,securityClassification:e.data.attachmentFiles[0].securityClassification},setTimeout((function(){i.val("createApply_form",n),a("#reApply").val("重新提审")})),setTimeout((()=>{console.log(1),m({fileName:e.data.attachmentFiles[0].fileName}),d(e.data.documentApprovalNodes.map((function(a,n){return{name:a.user,comment:e.data.documentApprovalComments[n]}})))}),0)):(layer.msg("无权访问"),window.history.back())})),d(u);var p={},f=null;function y(e){t.render({elem:"#LAY-user-back-manage",cols:[[{field:"username",title:"登录名"},{field:"name",title:"真实姓名"},{title:"操作",align:"center",fixed:"right",toolbar:"#table-useradmin-admin"}]],url:"/sys/user/findAll",headers:{Authorization:layui.data("layuiAdmin").Authorization},page:!0,limit:30,response:{statusCode:200},parseData:function(a){return{code:a.code,msg:a.msg,count:a.data.totalElements,data:a.data.content.filter((function(a){return console.log(e,a.username),"Y"!==a.isDelete&&!e.includes(a.username)}))}},done:function(e,a,n){console.log(e),console.log(a),console.log(n)},text:{none:"暂无数据"}}),i.on("submit(LAY-user-back-search)",(function(e){var a=e.field.name,n={};a&&(n={search_AND_EQ_username:a}),t.reload("LAY-user-back-manage",{where:n})}))}setTimeout((function(){f=l.render({elem:"#upload_word",url:"/approval/submitForApproval",data:p,accept:"file",auto:!1,headers:{Authorization:layui.data("layuiAdmin").Authorization},choose:function(e){console.log(a(".layui-upload-file").get(0)),console.log(e);var n=e.pushFile(),t=Object.keys(n);t.forEach((function(e,a){a!==t.length-1&&delete n[e]})),m({fileName:(c=n[t[t.length-1]]).name})},done:function(e){200===e.code&&(layer.msg("成功发起审批"),window.history.back())},error:function(){}})}),0),i.on("submit(apply-submit)",(function(e){var a=e.field;s.search&&s.search.id;console.log(c),p.documentNumber=a.documentNumber,p.securityClassification=a.securityClassification,p.title=a.title,p.number=a.number,p.type="document",p.username=u.map((function(e){return e.name})),f.upload()})),i.verify({onlyone:function(e,a){var t="";return n.req({url:"/approval/checkNumber",async:!1,data:{number:e},done:function(e){t=!(200===e.code&&!e.data)&&"审批编号不能重复！"},error:function(){t="审批编号不能重复！"}}),t}}),e("fileApplyCreate",{})}));