<template>
  <div style="padding: 20px">

    <el-button type="primary" @click="handleAdd">新增学生</el-button>

    <el-table
        :data="tableData"
        border
        style="width: 100%; margin-top: 20px"
    >
      <el-table-column prop="studentCode" label="学号" />
      <el-table-column prop="studentName" label="姓名" />
      <el-table-column prop="sexName" label="性别" />
      <el-table-column prop="studentTypeName" label="学生类型" />
      <el-table-column prop="studentStatusName" label="学生状态" />
      <el-table-column prop="gra" label="年级" />
      <el-table-column prop="cla" label="班级" />

      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button
              size="small"
              type="primary"
              @click="handleEdit(scope.row)"
          >编辑</el-button>

          <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row.studentId)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 弹窗 -->
    <el-dialog
        v-model="dialogVisible"
        :title="isEdit ? '修改学生' : '新增学生'"
        width="500px"
    >
      <el-form :model="form" label-width="100px">

        <el-form-item label="用户名">
          <el-input v-model="form.userName" :disabled="isEdit" />
        </el-form-item>

        <el-form-item label="学号">
          <el-input v-model="form.studentCode" :disabled="isEdit" />
        </el-form-item>

        <el-form-item label="姓名">
          <el-input v-model="form.name" :disabled="isEdit" />
        </el-form-item>

        <el-form-item label="性别">
          <el-select v-model="form.sex">
            <el-option label="男" :value="10031001" />
            <el-option label="女" :value="10031002" />
          </el-select>
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>

        <el-form-item label="电话">
          <el-input v-model="form.phone" />
        </el-form-item>

        <el-form-item label="年级">
          <el-input v-model="form.gra" />
        </el-form-item>

        <el-form-item label="班级">
          <el-input v-model="form.cla" />
        </el-form-item>

      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const baseURL = 'http://localhost:8080'

const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)

// 初始化表单
const initForm = () => ({
  studentId: null,
  userName: '',
  studentCode: '',
  name: '',
  sex: 10031001,
  studentType: 11011001,
  studentStatus: 10011001,
  email: '',
  phone: '',
  gra: '',
  cla: ''
})

const form = ref(initForm())

// 查询全部
const loadData = async () => {
  try {
    const res = await axios.get(baseURL + '/student/getAll')
    console.log('返回数据：', res.data)
    tableData.value = res.data.data
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

// 新增
const createStudent = async () => {
  await axios.post(baseURL + '/student/create', form.value)
}

// 修改
const updateStudent = async () => {
  await axios.put(baseURL + '/student/update', form.value)
}

// 删除
const deleteStudent = async (id) => {
  await axios.delete(baseURL + `/student/delete/${id}`)
}

// 新增按钮
const handleAdd = () => {
  isEdit.value = false
  form.value = initForm()
  dialogVisible.value = true
}

// 编辑按钮（⭐ 手动映射字段，避免脏数据）
const handleEdit = (row) => {
  isEdit.value = true
  form.value = {
    studentId: row.studentId,
    userName: row.userName || '',
    studentCode: row.studentCode,
    name: row.studentName,
    sex: row.sex,
    studentType: row.studentType,
    studentStatus: row.studentStatus,
    email: row.email || '',
    phone: row.phone || '',
    gra: row.gra,
    cla: row.cla
  }
  dialogVisible.value = true
}

// 删除按钮
const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除吗？', '提示', { type: 'warning' })
      .then(async () => {
        await deleteStudent(id)
        ElMessage.success('删除成功')
        loadData()
      })
}

// 提交
const submitForm = async () => {
  // 表单验证
  if (!form.value.userName) {
    ElMessage.error('请输入用户名')
    return
  }
  if (!form.value.studentCode) {
    ElMessage.error('请输入学号')
    return
  }
  if (!form.value.name) {
    ElMessage.error('请输入姓名')
    return
  }
  if (!form.value.gra) {
    ElMessage.error('请输入年级')
    return
  }
  if (!form.value.cla) {
    ElMessage.error('请输入班级')
    return
  }

  try {
    if (isEdit.value) {
      await updateStudent()
      ElMessage.success('修改成功')
    } else {
      await createStudent()
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadData()
})
</script>