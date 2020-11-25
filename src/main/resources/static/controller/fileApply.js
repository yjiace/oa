layui.define(["table","admin","form"],(function(t){var e=layui.$,a=layui.admin,l=layui.table;layui.view,layui.form;l.render({elem:"#apply_list_table",cols:[[{type:"checkbox",fixed:"left"},{field:"documentNumber",title:"审批编号"},{field:"initiatorUsername",title:"发起人",width:140},{field:"status",title:"审批进度",templet:"#apply_status",width:100},{field:"createTime",title:"发起时间",sort:!0},{title:"操作",width:200,align:"center",fixed:"right",toolbar:"#aplpy_list_table_edit"}]],url:"/document/approval/findAll",where:{},headers:{Authorization:layui.data("layuiAdmin").Authorization},page:!0,limit:30,response:{statusCode:200},parseData:function(t){return{code:t.code,msg:t.msg,count:t.data.totalElements,data:t.data.content.map((function(t){return t.createTime=window.utils.format(null,t.createTime),t}))}},done:function(t,e,a){console.log(t),console.log(e),console.log(a)},text:{none:"暂无数据"}}),l.on("tool(apply_list_table)",(function(t){var e=t.data;console.log(e),"edit"===t.event&&(location.hash="/approve/createApply/id="+e.id),"del"===t.event&&layer.confirm("确定要撤回？",(function(t){a.reqAsync({url:"/document/approval/withdrawalOfApproval",method:"POST",data:{id:e.id},done:function(e){e.code&&(l.reload("apply_list_table"),layer.close(t))}})}))}));var i={apply:function(){location.hash="/approve/createApply"}};e("#apply_btn").on("click",(function(){var t=e(this).data("type");i[t]&&i[t].call(this)})),t("fileApply",{})}));