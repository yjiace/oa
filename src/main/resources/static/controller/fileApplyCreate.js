layui.define(["table","admin","form","laytpl","upload","tinymce"],(function(e){var t=layui.$,n=layui.admin,a=layui.table,i=layui.view,o=layui.form,l=layui.upload,r=(layui.tinymce,layui.laytpl),c=[],u=null,d=[],s=layui.router();function m(e){r(t("#apply_list").html()).render(e.concat([{}]),(function(e){t("#apply_list_view").html(e),t(".applyList_del").on("click",(function(e){var n=Number(t(this).data("type"));layer.confirm("真的删除么",(function(e){c.splice(n,1),m(c),layer.close(e)}))})),t(".applyList_edit").on("click",(function(e){var l=Number(t(this).data("type"));n.popup({title:"选择审批人",area:["600px","430px"],id:"car_apply",success:function(e,t){i(this.id).render("approve/selectPeople",{}).done((function(){o.render(null,"createApply_form"),g(c.map((function(e){return e.name}))),a.on("tool(LAY-user-back-manage)",(function(e){var n=e.data;console.log(n),"edit"===e.event&&(c[l]={name:n.username,id:n.id},m(c),layer.close(t))}))}))}})})),t(".addPeople").on("click",(function(e){n.popup({title:"选择审批人",area:["600px","430px"],id:"car_apply",success:function(e,t){i(this.id).render("approve/selectPeople",{}).done((function(){o.render(null,"createApply_form"),g(c.map((function(e){return e.name}))),a.on("tool(LAY-user-back-manage)",(function(e){var n=e.data;console.log(n),"edit"===e.event&&(c.push({name:n.username,id:n.id}),m(c),layer.close(t))}))}))}})}))}))}function p(e){console.log(e),t(".fileList_div").show();var n=e.reduce((function(e,t){return e+'<a class="file_a" style="display:block;line-height:1" id="filedList"><i class="layui-icon"></i>'+t.name+"</a>"}),"");t(".fileList_div").html(n)}var f,y=(f=f||{jimi:"内部机密文件",title:"武警第一机动总队机动第六支队参谋部呈件",fuTitle:"参管呈 [ 2020 ]           号",chengyue:"呈阅《XXXXXXXXXXX》",bushou:"部首长",content:"请输入正文",name:"姓名",time:window.utils.format("yyyy年MM月dd日"),kezhang:"",chengbanren:"",dianhua:"",approve:[]},i("writeWord").render("approve/writeWord",f).done((function(){var e=297*t("#writeWord").width()/210;t("#templete_1 .content").height(.8*e+1),t("#templete_1 .content").children().height(.8*e),t("#templete_1 .edit").on("click",(function(e){var n=t(this),a=n.text(),i=n.data("key");layer.prompt({formType:2,value:a,title:"请输入文本",area:["300px","200px"]},(function(e,t,a){n.text(e),f[i]=e,layer.close(t)}))})),t("#templete_1 .bottom span").on("click",(function(e){var n=t(this).find("i"),a=n.text(),i=n.data("key");layer.prompt({formType:2,value:a,title:"请输入文本",area:["300px","200px"]},(function(e,t,a){n.text(e),f[i]=e,layer.close(t)}))}))})),t("#writePrint").on("click",(function(){t("#templete_1").jqprint({})})),f);setTimeout((function(){v=l.render({elem:"#upload_word",url:"/file/uploadFile",data:h,multiple:!0,accept:"file",auto:!1,headers:{Authorization:layui.data("layuiAdmin").Authorization},choose:function(e){var t=e.pushFile();(u||[]).forEach((function(e,n){delete t[e]})),u=Object.keys(t),p(Object.values(t))},before:function(e){loadingIndex=layer.load(1)},allDone:function(e){if(console.log(e),e.total!==d.length)return layer.close(loadingIndex),void layer.msg("文件未上传成功，请重新尝试",{icon:5});n.req({url:"/approval/document/submitForApproval",data:t.extend(h,{fileIds:d.join(",")}),method:"POST",done:function(e){d=[],200===e.code?(layer.close(loadingIndex),layer.msg("成功发起审批"),setTimeout((function(){window.history.back()}),1e3)):layer.msg("操作失败")}})},done:function(e){200===e.code&&d.push(e.data.id)},error:function(){}})}),0),s.search&&s.search.id&&n.reqAsync({url:"/approval/findOneById",method:"GET",data:{id:s.search.id}}).then((function(e){200===e.code?(console.log(e.data),function(e){setTimeout((function(){o.val("createApply_form",e),t("#reApply").val("重新提审")}))}({documentNumber:e.data.attachmentFiles[0].documentNumber,securityClassification:e.data.attachmentFiles[0].securityClassification}),setTimeout((()=>{console.log(1),p({fileName:e.data.attachmentFiles[0].fileName}),m(e.data.documentApprovalNodes.map((function(t,n){return{name:t.user,comment:e.data.documentApprovalComments[n]}})))}),0)):(layer.msg("无权访问"),window.history.back())})),m(c);var h={},v=null;function g(e){var t=layer.load(1);a.render({elem:"#LAY-user-back-manage",cols:[[{field:"username",title:"登录名"},{field:"name",title:"真实姓名"},{title:"操作",align:"center",fixed:"right",toolbar:"#table-useradmin-admin"}]],url:"/sys/user/findAll",headers:{Authorization:layui.data("layuiAdmin").Authorization},page:!0,limit:30,response:{statusCode:200},parseData:function(t){return{code:t.code,msg:t.msg,count:t.data.totalElements,data:t.data.content.filter((function(t){return console.log(e,t.username),"Y"!==t.isDelete&&!e.includes(t.username)}))}},done:function(e,a,i){layer.close(t),window.utils.dealCode(e.code,n),console.log(e),console.log(a),console.log(i)},text:{none:"暂无数据"}}),o.on("submit(LAY-user-back-search)",(function(e){var t=e.field.name,n={};n=t?{search_AND_LIKE_username:t}:null,a.reload("LAY-user-back-manage",{where:n})}))}o.on("submit(apply-submit)",(function(e){var t=e.field;s.search&&s.search.id;console.log(u),h.securityClassification=t.securityClassification,h.title=t.title,h.organizer=t.organizer,h.extra=JSON.stringify(y),h.username=c.map((function(e){return e.name})).join(","),u&&v.upload()})),o.verify({onlyone:function(e,t){var a="";return n.req({url:"/approval/document/checkNumber",async:!1,data:{number:e},done:function(e){a=!(200===e.code&&!e.data)&&"审批编号不能重复！"},error:function(){a="审批编号不能重复！"}}),a}}),e("fileApplyCreate",{})}));