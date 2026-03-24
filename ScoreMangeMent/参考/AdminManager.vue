<template>
  <div>

    <div style="height:60px;background-color: #00bc19;display: flex;align-items: center">
      <div style="width: 600px;display: flex;align-items: center;padding-left: 20px">
        <img src="@/assets/re-data.png" alt="" style="width: 10px;height: 40px;margin-right: 10px">
        <span style="font-size: 25px;color: white">教学管理系统</span>
      </div>
      <div style="flex: 1"></div>
      <div style="width: fit-content;display: flex;align-items: center;padding-right: 15px">
        <img :src="data.user.avatar||'@/assets/375d441b0a9f6ff.jpg'" alt="" style="width: 40px;height: 40px;border-radius: 50%">
        <span style="font-size: 20px;color: white">{{data.user.username}}</span>
      </div>
    </div>

    <div style="display: flex">
      <div style="width:200px;border-right: 1px solid #ddd; min-height: calc(100vh - 60px)">
        <el-menu router :default-active="router.currentRoute.value.path" style="border: 0">
          <el-menu-item index="/adminmanager/student">
            <el-icon><User/></el-icon>
            学生信息
          </el-menu-item>

          <el-menu-item index="/adminmanager/teacher">
            <el-icon><User/></el-icon>
            教师信息
          </el-menu-item>

          <el-menu-item index="/adminmanager/getcourse">
            <el-icon><Document  /></el-icon>
            课程信息
          </el-menu-item>

          <el-menu-item index="/adminmanager/getcourse2">
            <el-icon><Select  /></el-icon>
            选课信息
          </el-menu-item>

          <el-menu-item index="/adminmanager/personal">
            <el-icon><UserFilled/></el-icon>
            个人信息
          </el-menu-item>

          <el-menu-item index="/adminmanager/password">
            <el-icon><Lock/></el-icon>
            修改密码
          </el-menu-item>

          <el-menu-item @click="logout">
            <el-icon class="bold-icon"><SwitchButton /></el-icon>
            退出
          </el-menu-item>
        </el-menu>
      </div>

      <div style="flex: 1;width:0;padding: 10px">
        <RouterView @updateUser="updateUser"/>
      </div>

    </div>

  </div>
</template>
<script setup>
import {reactive} from "vue";
import {UserFilled,User,Document,Select,Histogram,Lock,SwitchButton} from "@element-plus/icons-vue";
import {useRouter} from "vue-router";
const router = useRouter();
const data=reactive({
  user: JSON.parse(localStorage.getItem('education'))
})
const updateUser=()=>{
  data.user=JSON.parse(localStorage.getItem('education'))
}
const logout=()=>{
  localStorage.removeItem('education');
  router.push('/adminlogin');
}
</script>