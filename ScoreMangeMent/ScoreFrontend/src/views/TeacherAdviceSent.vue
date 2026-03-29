<template>
  <div class="teacher-advice-sent-content">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>发件箱</span>
          <el-button type="primary" @click="handleAddAdvice">
            <el-icon><Plus /></el-icon>
            发意见
          </el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-select
          v-model="filterAdviceType"
          placeholder="意见类型"
          style="width: 140px; margin-right: 10px"
          clearable
        >
          <el-option label="教学意见" value="10141001" />
          <el-option label="班级意见" value="10141002" />
          <el-option label="生活意见" value="10141003" />
          <el-option label="学习意见" value="10141004" />
          <el-option label="其它" value="10141005" />
        </el-select>
        <el-select
          v-model="filterAdviceStatus"
          placeholder="状态"
          style="width: 120px; margin-right: 10px"
          clearable
        >
          <el-option label="未读" value="10131001" />
          <el-option label="已读" value="10131002" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="info" @click="resetSearch">重置</el-button>
      </div>

      <el-table :data="adviceList" style="width: 100%" border v-loading="loading">
        <el-table-column label="序号" width="80">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="收件人" width="120">
          <template #default="scope">
            {{ scope.row.recipientName }}
          </template>
        </el-table-column>
        <el-table-column label="内容" width="300">
          <template #default="scope">
            <el-tooltip :content="scope.row.adviceDesc" placement="top" :max-width="300" :show-after="800">
              <div class="content-cell" v-html="scope.row.adviceDesc ? (scope.row.adviceDesc.length > 50 ? scope.row.adviceDesc.replace(/<[^>]*>/g, '').substring(0, 50) + '...' : scope.row.adviceDesc) : ''"></div>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="图片" width="150">
          <template #default="scope">
            <div v-if="scope.row.imageUrls && scope.row.imageUrls.length > 0" style="display: flex; gap: 5px; flex-wrap: wrap;">
              <el-image
                v-for="(url, index) in scope.row.imageUrls"
                :key="index"
                :src="url"
                :preview-src-list="scope.row.imageUrls"
                :initial-index="index"
                :preview-teleported="true"
                fit="cover"
                style="width: 40px; height: 40px; border-radius: 4px; cursor: pointer;"
              >
                <template #placeholder>
                  <div style="width: 40px; height: 40px; background: #f5f7fa; display: flex; align-items: center; justify-content: center;">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>
            <span v-else style="color: #999;">无图片</span>
          </template>
        </el-table-column>
        <el-table-column label="意见类型" width="120">
          <template #default="scope">
            {{ scope.row.adviceTypeName }}
          </template>
        </el-table-column>
        <el-table-column label="实名/匿名" width="100">
          <template #default="scope">
            {{ scope.row.senderTypeName }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="scope">
            <el-tag :type="getAdviceStatusTagType(scope.row.adviceStatus)">
              {{ scope.row.adviceStatusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发件时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="danger" size="small" @click="handleDeleteAdvice(scope.row.adviceId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination" style="margin-top: 20px">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加意见对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="发意见"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="收件人用户名" prop="recipientUserName">
          <el-input
            v-model="formData.recipientUserName"
            placeholder="请输入收件人用户名"
            style="width: 100%"
            @blur="handleUserNameBlur"
            @keyup.enter="handleUserNameEnter"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="收件人姓名" prop="recipientId">
          <el-input
            v-model="formData.recipientName"
            placeholder="收件人姓名"
            style="width: 100%"
            :disabled="true"
          />
        </el-form-item>
        <el-form-item label="意见内容" prop="adviceDesc">
          <QuillEditor
            ref="quillEditorRef"
            v-model:content="formData.adviceDesc"
            placeholder="请输入意见内容"
            :options="{
              modules: {
                toolbar: [
                  ['bold', 'italic', 'underline', 'strike'],
                  ['blockquote', 'code-block'],
                  [{ 'header': 1 }, { 'header': 2 }],
                  [{ 'list': 'ordered' }, { 'list': 'bullet' }],
                  [{ 'script': 'sub' }, { 'script': 'super' }],
                  [{ 'indent': '-1' }, { 'indent': '+1' }],
                  [{ 'direction': 'rtl' }],
                  [{ 'size': ['small', false, 'large', 'huge'] }],
                  [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
                  [{ 'color': [] }, { 'background': [] }],
                  [{ 'font': [] }],
                  [{ 'align': [] }],
                  ['clean']
                ]
              },
              theme: 'snow'
            }"
            style="width: 100%; height: 300px"
          />
        </el-form-item>
        <el-form-item label="意见类型" prop="adviceType">
          <el-select v-model="formData.adviceType" placeholder="请选择意见类型" style="width: 100%">
            <el-option label="教学意见" value="10141001" />
            <el-option label="班级意见" value="10141002" />
            <el-option label="生活意见" value="10141003" />
            <el-option label="学习意见" value="10141004" />
            <el-option label="其它" value="10141005" />
          </el-select>
        </el-form-item>
        <el-form-item label="发送者类型" prop="senderType">
          <el-select v-model="formData.senderType" placeholder="请选择发送者类型" style="width: 100%">
            <el-option label="实名" value="10151001" />
            <el-option label="匿名" value="10151002" />
          </el-select>
        </el-form-item>
        <el-form-item label="上传图片" prop="imageFiles">
          <el-upload
            ref="uploadRef"
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :file-list="fileList"
            multiple
            :limit="9"
            list-type="picture-card"
            :on-exceed="handleExceed"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div style="font-size: 12px; color: #999; margin-top: 5px;">
            支持 jpg/png 格式，最多上传 9 张
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, watch } from 'vue'
import { Plus, Picture, Search } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'

// 筛选条件
const filterAdviceType = ref(null)
const filterAdviceStatus = ref(null)

// 分页数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 意见列表
const adviceList = ref([])

// 加载状态
const loading = ref(true)

// 对话框相关
const dialogVisible = ref(false)
const formRef = ref(null)
const uploadRef = ref(null)
const quillEditorRef = ref(null)
const submitting = ref(false)
const formData = reactive({
  recipientId: null,
  recipientUserName: '',
  recipientName: '',
  adviceDesc: '',
  adviceType: null,
  senderType: null
})

// 文件列表
const fileList = ref([])
const imageFiles = ref([])

// 根据用户名搜索用户
const searchUserByUserName = async (userName) => {
  if (!userName) return

  try {
    const response = await request.get(`/api/user/searchByUsername?username=${encodeURIComponent(userName)}`)
    if (response.code === 200 && response.data && response.data.length > 0) {
      const user = response.data[0]
      formData.recipientId = user.userId
      formData.recipientName = user.name
    } else {
      ElMessage.warning('未找到该用户，请检查用户名是否正确')
      formData.recipientId = null
      formData.recipientName = ''
    }
  } catch (error) {
    console.error('搜索用户失败:', error)
    ElMessage.error('搜索用户失败，请稍后重试')
  }
}

// 处理用户名输入框失焦
const handleUserNameBlur = () => {
  if (formData.recipientUserName) {
    searchUserByUserName(formData.recipientUserName)
  }
}

// 处理用户名输入框回车
const handleUserNameEnter = () => {
  if (formData.recipientUserName) {
    searchUserByUserName(formData.recipientUserName)
  }
}

// 表单验证规则
const rules = {
  recipientUserName: [
    { required: true, message: '请输入收件人用户名', trigger: 'blur' }
  ],
  recipientId: [
    { required: true, message: '请确认收件人信息', trigger: 'change' }
  ],
  adviceDesc: [
    { required: true, message: '请输入意见内容', trigger: 'blur' }
  ],
  adviceType: [
    { required: true, message: '请选择意见类型', trigger: 'change' }
  ],
  senderType: [
    { required: true, message: '请选择发送者类型', trigger: 'change' }
  ]
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 获取状态标签类型
const getAdviceStatusTagType = (status) => {
  const typeMap = {
    10131001: 'danger',
    10131002: 'success'
  }
  return typeMap[status] || 'info'
}

// 获取意见列表
const loadAdviceList = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/advice/my/sent', {
      headers: {
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      }
    })
    if (response.code === 200) {
      let data = response.data || []

      // 如果有搜索条件，根据条件过滤
      if (filterAdviceType.value) {
        data = data.filter(advice => advice.adviceType === filterAdviceType.value)
      }
      if (filterAdviceStatus.value) {
        data = data.filter(advice => advice.adviceStatus === filterAdviceStatus.value)
      }

      // 按创建时间排序
      const sortedList = data.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))

      // 设置总数
      total.value = sortedList.length

      // 前端分页
      const startIndex = (currentPage.value - 1) * pageSize.value
      const endIndex = startIndex + pageSize.value
      adviceList.value = sortedList.slice(startIndex, endIndex)
    }
  } catch (error) {
    console.error('获取发件箱失败:', error)
    ElMessage.error('获取发件箱失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadAdviceList()
}

// 重置搜索
const resetSearch = () => {
  filterAdviceType.value = null
  filterAdviceStatus.value = null
  currentPage.value = 1
  loadAdviceList()
}

// 添加意见
const handleAddAdvice = () => {
  dialogVisible.value = true
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(formData, {
    recipientId: null,
    recipientUserName: '',
    recipientName: '',
    adviceDesc: '',
    adviceType: null,
    senderType: null
  })
  fileList.value = []
  imageFiles.value = []
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

// 处理文件变化
const handleFileChange = (file) => {
  imageFiles.value.push(file.raw)
}

// 处理文件移除
const handleFileRemove = (file) => {
  imageFiles.value = imageFiles.value.filter(f => f !== file.raw)
}

// 处理超出限制
const handleExceed = () => {
  ElMessage.warning('最多只能上传 9 张图片')
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    // 处理富文本内容，获取 HTML 字符串
    let adviceDesc = ''
    try {
      if (quillEditorRef.value) {
        const editor = quillEditorRef.value.getEditor()
        if (editor) {
          if (editor.root) {
            adviceDesc = editor.root.innerHTML
          } else if (editor.getHTML) {
            adviceDesc = editor.getHTML()
          }
        }
      }
    } catch (error) {
      console.error('获取富文本内容失败:', error)
    }

    if (!adviceDesc && formData.adviceDesc) {
      if (typeof formData.adviceDesc === 'object' && formData.adviceDesc !== null) {
        if (formData.adviceDesc.ops && Array.isArray(formData.adviceDesc.ops)) {
          adviceDesc = formData.adviceDesc.ops.map(op => op.insert || '').join('')
        } else {
          adviceDesc = JSON.stringify(formData.adviceDesc)
        }
      } else {
        adviceDesc = String(formData.adviceDesc)
      }
    }

    // 先创建意见
    const submitData = {
      recipientId: formData.recipientId,
      senderId: parseInt(JSON.parse(localStorage.getItem('userInfo')).userId),
      adviceDesc: adviceDesc,
      adviceType: parseInt(formData.adviceType),
      senderType: parseInt(formData.senderType),
      adviceStatus: 10131001
    }

    const response = await request.post('/api/advice/add', submitData)
    if (response.code === 200) {
      const adviceId = response.data

      // 如果有图片，上传图片
      if (imageFiles.value.length > 0) {
        const formDataUpload = new FormData()
        imageFiles.value.forEach(file => {
          formDataUpload.append('files', file)
        })

        await request.post(`/api/advice/upload/${adviceId}`, formDataUpload, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
      }

      ElMessage.success('发送意见成功')
      dialogVisible.value = false
      loadAdviceList()
    } else {
      ElMessage.error(response.msg || '发送意见失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    if (error.name !== 'Error') {
      ElMessage.error('操作失败，请稍后重试')
    }
  } finally {
    submitting.value = false
  }
}

// 删除意见
const handleDeleteAdvice = (adviceId) => {
  ElMessageBox.confirm('确定要删除这个意见吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await request.delete(`/api/advice/delete/${adviceId}`)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        loadAdviceList()
      } else {
        ElMessage.error(response.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除意见失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  loadAdviceList()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  loadAdviceList()
}

// 组件挂载时加载数据
onMounted(async () => {
  await loadAdviceList()
})
</script>

<style scoped>
.teacher-advice-sent-content {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}

.content-cell {
  padding: 4px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>