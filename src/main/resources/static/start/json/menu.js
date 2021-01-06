{
  "code": 0
  ,"msg": ""
  ,"data": [
  {
    "title": "主页"
    ,"icon": "layui-icon-home"
    ,"list": [{
      "title": "控制台"
      ,"jump": "/"
    }]
  },
  {
    "name": "approve"
    ,"title": "审批管理"
    ,"icon": "layui-icon-component"
    ,"list": [
    {
      "name": "file-apply"
      ,"title": "发起审批"
      ,"jump": "/approve/createApply"
    },
    {
      "name": "approve-apply"
      ,"title": "我发起的"
      ,"jump": "/approve/apply"
    },
    {
      "name": "approve-approve"
      ,"title": "我审批的"
      ,"jump": "/approve/approve"
    }]
  },
  {
    "name": "car"
    ,"title": "车辆管理"
    ,"icon": "layui-icon-component"
    ,"list": [{
      "name": "car-list"
      ,"title": "车辆列表"
      ,"jump": "/car/list"
    }, {
      "name": "car-apply"
      ,"title": "申请用车"
      ,"jump": "/car/apply"
    },{
      "name": "car-apply"
      ,"title": "我发起的"
      ,"jump": "/car/carApplyList"
    }, {
      "name": "approve-approve"
      ,"title": "我审批的"
      ,"jump": "/car/carNeedApprove"
    },
    {
      "name": "car-log"
      ,"title": "日用车统计"
      ,"jump": "/car/log"
    }
    ]
  },
  {
    "name": "fileList"
    ,"title": "文件管理"
    ,"icon": "layui-icon-component"
    ,"list": [{
      "name": "approve-apply"
      ,"title": "文件管理"
      ,"jump": "/fileList/fileList"
    }]
  },
 {
    "name": "user"
    ,"title": "用户管理"
    ,"icon": "layui-icon-user"
    ,"list": [{
      "name": "administrators-list"
      ,"title": "人员管理"
      ,"jump": "user/administrators/list"
    }, {
      "name": "administrators-rule"
      ,"title": "角色管理"
      ,"jump": "user/administrators/role"
    }]
  },
  {
    "name": "fileList"
    ,"title": "消息管理"
    ,"icon": "layui-icon-component"
    ,"list": [{
      "name": "approve-apply"
      ,"title": "消息中心"
      ,"jump": "/app/message/"
    }]
  }
  ]
}