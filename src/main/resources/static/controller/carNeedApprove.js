layui.define(["table","admin","form"],(function(e){var t=layui.$,a=layui.admin,l=layui.table,o=layui.view;form=layui.form;function i(e,t){t&&a.req({url:"/approval/addComment",method:"POST",data:{id:e,message:t}})}l.render({elem:"#apply_list_table",cols:[[{type:"checkbox",fixed:"left"},{field:"title",title:"标题"},{field:"number",title:"审批编号"},{field:"initiatorUsername",title:"发起人",width:140},{field:"status",title:"审批进度",templet:"#apply_status",width:100},{field:"createTime",title:"发起时间",sort:!0},{title:"操作",minWidth:100,align:"center",fixed:"right",toolbar:"#aplpy_list_table_edit"}]],url:"/approval/findAll",where:{search_AND_EQ_type:"vehicle"},headers:{Authorization:layui.data("layuiAdmin").Authorization},page:!0,limit:30,response:{statusCode:200},parseData:function(e){return{code:e.code,msg:e.msg,count:e.data.totalElements,data:e.data.content.map((function(e){return e.createTime=window.utils.format(null,e.createTime),e}))}},done:function(e,t,a){console.log(e),console.log(t),console.log(a)},text:{none:"暂无数据"}}),l.on("tool(apply_list_table)",(function(e){var n=e.data;console.log(n),"view"===e.event&&(location.hash="/car/carViewApply/id="+n.id),"edit"===e.event&&a.popup({title:"审批",area:["400px","330px"],btn:["审批通过","审批拒绝"],id:"car_apply",yes:function(e){i(n.id,t("#comment_val").val()),a.req({url:"/approval/operationApproval",method:"POST",data:{id:n.id,operation:"Completed"},done:function(t){200===t.code?(layer.msg("审批通过"),layer.close(e),l.reload("apply_list_table")):layer.msg("操作失败")}})},btn2:function(e){i(n.id,t("#comment_val").val()),a.req({url:"/approval/operationApproval",method:"POST",data:{id:n.id,operation:"Rejected"},done:function(t){200===t.code&&(layer.msg("审批已拒绝"),layer.close(e))}})},success:function(e,t){o(this.id).render("approve/approveForm",{}).done((function(){form.render(null,"createApply_form"),l.on("tool(LAY-user-back-manage)",(function(e){var a=e.data;console.log(a),"edit"===e.event&&(applyList.push({name:a.username}),renderApply(applyList),layer.close(t))}))}))}})})),e("carNeedApprove",{})}));