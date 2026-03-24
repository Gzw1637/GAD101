<template>
  <div>
    <div class="card" style="margin-bottom: 5px">
      <el-input style="width: 240px; margin-right: 5px" v-model="data.name" placeholder="请输入真实名字查询" prefix-icon="Search"></el-input>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-button type="primary" @click="handleAdd">新增</el-button>
      <el-button type="danger" @click="deleteBatch">批量删除</el-button>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-table :data="data.tableData" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="35"/>
        <el-table-column label="照片">
          <template #default="scope">
            <img v-if="scope.row.avatar" :src="scope.row.avatar" alt="" style="display:block; width: 40px;height: 40px;border-radius: 50%"/>
          </template>
        </el-table-column>
        <el-table-column label="账号" prop="username"/>
        <el-table-column label="密码" prop="password"/>
        <el-table-column label="真实姓名" prop="name"/>
        <el-table-column label="性别" prop="sex"/>
        <el-table-column label="学号" prop="studentNumber" show-overflow-tooltip/>
        <el-table-column label="电话" prop="phone" show-overflow-tooltip/>
        <el-table-column label="邮箱" prop="email" show-overflow-tooltip/>
        <el-table-column label="年级" prop="grade"/>
        <el-table-column label="班级" prop="className"/>
        <el-table-column label="操作">
          <template #default="scope">
              <el-button link type="primary" @click="handleUpdate(scope.row)">编辑</el-button>
              <el-button link type="danger" @click="del(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div style="margin-top: 15px">
      <el-pagination   @current-change="handleCurrentChange"
                        @size-change="handleSizeChange"
                      v-model:current-page="data.pageNum"
                      v-model:page-size="data.pageSize"
                      :page-sizes="[2,10,15,20]"
                      background
                       layout="total,sizes,prev,pager,next,jumper"
                      :total="data.total"/>

    </div>
    <el-dialog title="学生信息" v-model="data.formVisible"  width="500" @close="closeDialog()">
      <el-form ref="formRef" :rules="data.rules" :model="data.form" label-width="85px">

        <el-form-item label="账号:" prop="username">
          <el-input size="large" v-model="data.form.username" placeholder="请输入账号" prefix-icon="User"></el-input>
        </el-form-item>

        <el-form-item label="头像:">
          <el-upload  ref="uploadRef"
                     action="http://localhost:8080/files/upload"
                     list-type="picture"
                     :on-success="handleAvatarSuccess"
          >
            <el-button type="primary">上传照片</el-button>
          </el-upload>
        </el-form-item>

        <el-form-item label="真实姓名:" prop="name">
          <el-input :disabled="data.form.id" size="large" v-model="data.form.name" placeholder="请输入真实姓名" prefix-icon="UserFilled"></el-input>
        </el-form-item>

        <el-form-item label="性别:" prop="sex">
          <el-radio-group v-model="data.form.sex" size="large" class="gender-group">
            <el-radio value="男">男</el-radio>
            <el-radio value="女">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="学号:" prop="studentNumber">
          <el-input size="large" v-model="data.form.studentNumber" placeholder="请输入学号" prefix-icon="Document"></el-input>
        </el-form-item>

        <el-form-item label="电话:" prop="phone">
          <el-input size="large" v-model="data.form.phone" placeholder="请输入电话" prefix-icon="Phone"></el-input>
        </el-form-item>

        <el-form-item label="邮箱:" prop="email">
          <el-input size="large" v-model="data.form.email" placeholder="请输入邮箱" prefix-icon="Message"></el-input>
        </el-form-item>

        <el-form-item label="年级:" prop="grade">
          <el-input size="large" v-model="data.form.grade" placeholder="请输入年级" prefix-icon="School"></el-input>
        </el-form-item>

        <el-form-item label="班级:" prop="class_name">
          <el-input size="large" v-model="data.form.className" placeholder="请输入班级" prefix-icon="School"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeDialog()">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import {reactive, ref} from "vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
const formRef = ref();
const uploadRef = ref();

const validatePhone = (rules, value, callback) => {
  const phoneReg = /^1[3-9]\d{9}$/;
  if (!value) {
    callback(new Error("请输入电话"));
  } else if (!phoneReg.test(value)) {
    callback(new Error("请输入11位有效手机号"));
  } else {
    callback();
  }
};
const validateEmail = (rules, value, callback) => {
  const emailReg = /^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]{2,7}$/;
  if (!value) {
    callback(new Error("请输入邮箱"));
  } else if (!emailReg.test(value)) {
    callback(new Error("请输入有效邮箱（如xxx@xxx.com）"));
  } else {
    callback();
  }
};

const data= reactive({
  name:null,
  tableData:[],
  pageNum:1,
  pageSize:10,
  total:null,
  formVisible:false,
  form:{},
  ids:[],
  rules:{
    username:[
      {required:true,message:"请输入账号",trigger:"blur"}
    ],
    name:[
      {required:true,message:"请输入真实姓名",trigger:"blur"}
    ],
    sex:[
      {required:true,message:"请输入性别",trigger:"blur"}
    ],
    studentNumber:[
      {required:true,message:"请输入学号",trigger:"blur"}
    ],
    phone:[
      {required:true,validator:validatePhone,trigger:"blur"}
    ],
    email:[
      {required:true,validator:validateEmail,trigger:"blur"}
    ],
    grade:[
      {required:true,message:"请输入年级",trigger:"blur"}
    ],
    className:[
      {required:true,message:"请输入班级",trigger:"blur"}
    ],
  }
})
const load=()=>{
  request.get("/student/selectPage",{
    params:{
      pageNum:data.pageNum,
      pageSize:data.pageSize,
      name:data.name
    }
  }).then(res=>{
    console.log(res.data)
    data.tableData=res.data.list
    data.total=res.data.total
  }).catch(err => {
    console.error("加载数据失败：", err);
  });
}
const reset=()=>{
  data.name=null
  load()
}
// 处理页码变化
const handleCurrentChange = (page) => {
  data.pageNum = page;
  load();
};
// 处理分页大小变化
const handleSizeChange = (size) => {
  data.pageSize = size;
  data.pageNum = 1; // 切换分页大小时重置为第1页
  load();
};
load();

const handleAdd =()=>{
  data.formVisible=true
  data.form={}
}

const save =()=>{
  data.form.id? update() : add()
}

const add =()=>{
  request.post("/student/add",data.form).then(res=>{
    if (res.code===200){
      closeDialog();
      ElMessage.success("操作成功")
      load()
    }else {
      ElMessage.error(res.msg)
    }
  })
}

const update =()=>{
  request.put("/student/Update",data.form).then(res=>{
    if (res.code===200){
      closeDialog();
      ElMessage.success("操作成功")
      load()
    }else {
      ElMessage.error(res.msg)
    }
  })
}

const handleUpdate=(row)=>{
  data.form=JSON.parse(JSON.stringify(row))
  data.formVisible=true
}

const del =(id)=>{
  // `/student/deleteById/${id}`
  ElMessageBox.confirm("删除数据后无法恢复，您确认删除吗？","删除确认",{type:"warning"}).then(()=>{
    request.delete("/student/deleteById/"+id).then(res=>{
      if (res.code===200){
        ElMessage.success("操作成功")
        load()
      }else {
        ElMessage.error(res.msg)
      }
    })
  }).catch()
}

const handleSelectionChange =(rows)=>{
  data.ids=rows.map(row=>row.id)
  console.log(data.ids)
}

const deleteBatch =()=>{
  if (data.ids.length===0){
    ElMessage.warning("请选择数据")
    return
  }
  ElMessageBox.confirm("删除数据后无法恢复，您确认删除吗？","删除确认",{type:"warning"}).then(()=> {
    request.delete("/student/deleteBatch", {data: data.ids}).then(res => {
      if (res.code === 200) {
        ElMessage.success("操作成功")
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch()
}
const handleAvatarSuccess =(res)=>{
  console.log(res.data)
  data.form.avatar=res.data
}

// 在 <script setup> 中定义（可放在 handleAdd 方法附近）
const closeDialog = () => {
  // 1. 关闭Dialog弹窗
  data.formVisible = false;

  // 2. 清空表单数据（文本框、性别、年级等所有字段）
  data.form = {}; // 直接重置为空对象，清除所有已填写内容

  // 3. 清空上传组件的未完成文件（关键：解决“未上传照片残留”问题）
  // 需先给 el-upload 加 ref，才能通过 ref 控制组件状态
  if (uploadRef.value) {
    uploadRef.value.clearFiles(); // 清空上传组件的所有已选择文件（包括未上传的）
  }

  // 4. 可选：重置表单验证状态（避免下次打开时显示旧的错误提示）
  if (formRef.value) {
    formRef.value.resetFields(); // 清除表单验证的错误标记和状态
  }
};
</script>
<style scoped>

</style>