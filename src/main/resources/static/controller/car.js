layui.define(["table","admin","form"],(function(e){var t=layui.$,i=layui.admin,a=layui.table,o=layui.view;form=layui.form,a.render({elem:"#car_list_table",cols:[[{type:"checkbox",fixed:"left"},{field:"name",title:"车辆名称"},{field:"number",title:"车辆编号"},{field:"model",title:"车辆型号"},{field:"plateNumber",title:"车牌号"},{field:"company",title:"单位"},{field:"status",title:"状态",templet:"#buttonTpl"},{field:"createTime",title:"创建时间",sort:!0},{title:"操作",width:150,align:"center",fixed:"right",toolbar:"#car_list_table_edit"}]],url:"/vehicle/information/findAll",headers:{Authorization:layui.data("layuiAdmin").Authorization},page:!0,limit:30,response:{statusCode:200},parseData:function(e){return{code:e.code,msg:e.msg,count:e.data.totalElements,data:e.data.content.map((function(e){return e.createTime=window.utils.format(null,e.createTime),e}))}},done:function(e,t,i){console.log(e),console.log(t),console.log(i)},text:{none:"暂无数据"}}),a.on("tool(car_list_table)",(function(e){var t=e.data;({del:function(){layer.confirm("确定删除此车辆？",(function(t){e.del(),layer.close(t)}))},edit:function(){i.popup({title:"编辑车辆",area:["420px","430px"],id:"LAY-popup-useradmin-add",success:function(e,a){o(this.id).render("car/carForm",t).done((function(){form.render(null,"layuiadmin-form-admin"),form.on("submit(LAY-user-back-submit)",(function(e){var o=e.field;o.id=t.id,console.log(o),i.req({url:"/vehicle/information/update",method:"post",data:o,done:function(e){console.log(e),200===e.code&&(layui.table.reload("car_list_table"),layer.close(a))}})}))}))}})}})[e.event]()}));var l={delCar:function(){},addCar:function(){i.popup({title:"添加车辆",area:["420px","430px"],id:"LAY-popup-useradmin-add",success:function(e,t){o(this.id).render("car/carForm").done((function(){form.render(null,"layuiadmin-form-admin"),form.on("submit(LAY-user-back-submit)",(function(e){var a=e.field;console.log(a),i.req({url:"/vehicle/information/save",method:"post",data:a,done:function(e){console.log(e),200===e.code&&(layui.table.reload("car_list_table"),layer.close(t))}})}))}))}})}};t(".layui-btn.layuiadmin-btn-admin").on("click",(function(){var e=t(this).data("type");l[e]&&l[e].call(this)})),e("car",{})}));