layui.define(["table","admin","form","laytpl"],(function(e){var t=layui.$,a=layui.admin,l=layui.table,r=layui.view,o=layui.form,i=layui.laytpl;function n(e){return a.reqAsync({url:"/vehicle/information/createVehicleRecord",method:"POST",data:{id:e.id,operation:e.operation,remarks:e.remarks}})}l.render({elem:"#car_list_table",cols:[[{type:"checkbox",fixed:"left"},{field:"name",title:"车辆名称"},{field:"number",title:"车辆编号"},{field:"model",title:"车辆型号"},{field:"plateNumber",title:"车牌号"},{field:"status",title:"状态",templet:"#buttonTpl"},{field:"createTime",title:"创建时间",sort:!0},{title:"操作",width:180,align:"center",fixed:"right",toolbar:"#car_list_table_edit"}]],url:"/vehicle/information/findAll",headers:{Authorization:layui.data("layuiAdmin").Authorization},page:!0,limit:30,response:{statusCode:200},parseData:function(e){return{code:e.code,msg:e.msg,count:e.data.totalElements,data:window.utils.addPermission(e.data.content).map((function(e){return e.createTime=window.utils.format(null,e.createTime),e.status=e.currentCarRecord&&e.currentCarRecord.status,e.canRecord=["NotLeaving","NotReturned"].includes(e.status),e.statusText={NotInUse:"未使用",Approval:"审批中",NotLeaving:"未离场",NotReturned:"未归还",withdrawalOfApproval:"已撤回",rejectedApproval:"已拒绝"}[e.status]||"未使用",e}))}},done:function(e,t,a){console.log(e),console.log(t),console.log(a)},text:{none:"暂无数据"}}),o.on("submit(carList_search_form)",(function(e){var t=e.field,a={};t.plateNumber?a.search_AND_LIKE_plateNumber=t.plateNumber:a=null,console.log(a),l.reload("car_list_table",{where:a})})),l.on("tool(car_list_table)",(function(e){var c=e.data;({del:function(){"Approval"!==c.status?layer.confirm("确定删除此车辆？",(function(t){a.req({url:"/vehicle/information/delVehicleRecord",method:"POST",data:{id:c.id},done:function(a){200===a.code?(layer.msg("操作成功"),e.del(),layui.table.reload("car_list_table")):layer.msg("操作失败"),layer.close(t)}})})):layer.msg("审批中的车辆无法删除")},edit:function(){a.popup({title:"编辑车辆",area:["420px","430px"],id:"LAY-popup-useradmin-add",success:function(e,t){r(this.id).render("car/carForm",c).done((function(){o.render(null,"layuiadmin-form-admin"),o.on("submit(LAY-user-back-submit)",(function(e){var l=e.field;l.id=c.id,console.log(l),a.req({url:"/vehicle/information/update",method:"post",data:l,done:function(e){console.log(e),200===e.code&&(layui.table.reload("car_list_table"),layer.close(t))}})}))}))}})},log:function(){i(t("#car_list_log_open").html()).render(c,(function(e){layer.open({title:"记录车辆操作",content:e,btn:"NotLeaving"===c.currentCarRecord.status?["车辆离场"]:["车辆返场"],yes:function(e){"NotLeaving"===c.currentCarRecord.status?n({id:c.id,operation:"VehicleDeparture",remarks:t("#car_text_opener").val()}).then((function(t){200===t.code?layer.msg("操作成功"):layer.msg("操作失败"),layer.close(e),l.reload("car_list_table",{where:null})})):n({id:c.id,operation:"ReturnVehicle",remarks:t("#car_text_opener").val()}).then((function(t){200===t.code?layer.msg("操作成功"):layer.msg("操作失败"),layer.close(e),l.reload("car_list_table",{where:null})}))},btn2:function(e){n({id:c.id,operation:"VehicleDeparture",remarks:t("#car_text_opener").val()}).then((function(t){200===t.code?layer.msg("操作成功"):layer.msg("操作失败"),layer.close(e),l.reload("car_list_table",{where:null})}))}})}))}})[e.event]()}));var c={delCar:function(){},addCar:function(){a.popup({title:"添加车辆",area:["420px","430px"],id:"LAY-popup-useradmin-add",success:function(e,t){r(this.id).render("car/carForm").done((function(){o.render(null,"layuiadmin-form-admin"),o.on("submit(LAY-user-back-submit)",(function(e){var l=e.field;console.log(l),a.req({url:"/vehicle/information/save",method:"post",data:l,done:function(e){console.log(e),200===e.code&&(layui.table.reload("car_list_table"),layer.close(t))}})}))}))}})}};t(".layui-btn.layuiadmin-btn-admin").on("click",(function(){var e=t(this).data("type");c[e]&&c[e].call(this)})),e("car",{})}));