<template>
  <div class="teacher-advice-received-content">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>收件箱</span>
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
        <el-table-column label="发送人" width="120">
          <template #default="scope">
            {{ scope.row.senderType === 10151002 ? '' : scope.row.senderName }}
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
        <el-table-column prop="createTime" label="收件时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleViewDetail(scope.row)">查看</el-button>
            <el-button type="success" size="small" @click="handleReply(scope.row)">回复</el-button>
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
    
    <!-- 查看详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="意见详情"
      width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="发送人">{{ currentDetail.senderType === 10151002 ? '' : currentDetail.senderName }}</el-descriptions-item>
        <el-descriptions-item label="收件人">{{ currentDetail.recipientName }}</el-descriptions-item>
        <el-descriptions-item label="意见类型">{{ currentDetail.adviceTypeName }}</el-descriptions-item>
        <el-descriptions-item label="实名/匿名">{{ currentDetail.senderTypeName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getAdviceStatusTagType(currentDetail.adviceStatus)">
            {{ currentDetail.adviceStatusName }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发件时间">{{ formatDate(currentDetail.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="意见内容" :span="2">
          <div style="white-space: pre-wrap; line-height: 1.8;" v-html="currentDetail.adviceDesc"></div>
        </el-descriptions-item>
        <el-descriptions-item label="图片附件" :span="2">
          <div v-if="currentDetail.imageUrls && currentDetail.imageUrls.length > 0" style="display: flex; gap: 10px; flex-wrap: wrap;">
            <el-image
              v-for="(url, index) in currentDetail.imageUrls"
              :key="index"
              :src="url"
              :preview-src-list="currentDetail.imageUrls"
              :initial-index="index"
              :preview-teleported="true"
              fit="cover"
              style="width: 100px; height: 100px; border-radius: 4px; cursor: pointer;"
            >
              <template #placeholder>
                <div style="width: 100px; height: 100px; background: #f5f7fa; display: flex; align-items: center; justify-content: center;">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </div>
          <span v-else style="color: #999;">无图片</span>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 回复对话框 -->
    <el-dialog
      v-model="replyDialogVisible"
      title="回复意见"
      width="800px"
    >
      <el-form :model="replyForm" :rules="replyRules" ref="replyFormRef" label-width="100px">
        <el-form-item label="接收者" prop="recipientId">
          <el-input v-model="replyForm.recipientName" disabled />
        </el-form-item>
        <el-form-item label="回复内容" prop="adviceDesc">
          <el-input 
            v-model="replyForm.adviceDesc" 
            placeholder="请输入回复内容" 
            type="textarea" 
            :rows="6" 
          />
        </el-form-item>
        <el-form-item label="意见类型" prop="adviceType">
          <el-select v-model="replyForm.adviceType" placeholder="请选择意见类型" style="width: 100%">
            <el-option label="教学意见" value="10141001" />
            <el-option label="班级意见" value="10141002" />
            <el-option label="生活意见" value="10141003" />
            <el-option label="学习意见" value="10141004" />
            <el-option label="其它" value="10141005" />
          </el-select>
        </el-form-item>
        <el-form-item label="发送者类型" prop="senderType">
          <el-select v-model="replyForm.senderType" placeholder="请选择发送者类型" style="width: 100%">
            <el-option label="实名" value="10151001" />
            <el-option label="匿名" value="10151002" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleReplySubmit" :loading="replySubmitting">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { Picture } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

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

// 详情对话框
const detailDialogVisible = ref(false)
const currentDetail = reactive({
  adviceId: null,
  senderName: '',
  recipientName: '',
  adviceTypeName: '',
  senderTypeName: '',
  adviceStatus: null,
  adviceStatusName: '',
  adviceDesc: '',
  createTime: null,
  imageUrls: []
})

// 回复对话框
const replyDialogVisible = ref(false)
const replyFormRef = ref(null)
const replySubmitting = ref(false)
const replyForm = reactive({
  recipientId: null,
  recipientName: '',
  adviceDesc: '',
  adviceType: null,
  senderType: null
})

// 回复表单验证规则
const replyRules = {
  recipientId: [
    { required: true, message: '请选择接收者', trigger: 'change' }
  ],
  adviceDesc: [
    { required: true, message: '请输入回复内容', trigger: 'blur' }
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
    const response = await request.get('/api/advice/my/received', {
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
    console.error('获取收件箱失败:', error)
    ElMessage.error('获取收件箱失败')
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

// 查看详情
const handleViewDetail = (row) => {
  Object.assign(currentDetail, {
    adviceId: row.adviceId,
    senderName: row.senderName,
    senderType: row.senderType,
    recipientName: row.recipientName,
    adviceTypeName: row.adviceTypeName,
    senderTypeName: row.senderTypeName,
    adviceStatus: row.adviceStatus,
    adviceStatusName: row.adviceStatusName,
    adviceDesc: row.adviceDesc,
    createTime: row.createTime,
    imageUrls: row.imageUrls || []
  })
  detailDialogVisible.value = true
}

// 回复意见
const handleReply = (row) => {
  Object.assign(replyForm, {
    recipientId: row.senderId,
    recipientName: row.senderType === 10151002 ? '' : row.senderName,
    adviceDesc: '',
    adviceType: row.adviceType,
    senderType: null
  })
  if (replyFormRef.value) {
    replyFormRef.value.resetFields()
  }
  replyDialogVisible.value = true
}

// 提交回复
const handleReplySubmit = async () => {
  if (!replyFormRef.value) return
  
  try {
    await replyFormRef.value.validate()
    replySubmitting.value = true
    
    const submitData = {
      recipientId: replyForm.recipientId,
      senderId: parseInt(JSON.parse(localStorage.getItem('userInfo')).userId),
      adviceDesc: replyForm.adviceDesc,
      adviceType: parseInt(replyForm.adviceType),
      senderType: parseInt(replyForm.senderType),
      adviceStatus: 10131001
    }
    
    const response = await request.post('/api/advice/reply', submitData, {
      headers: {
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      }
    })
    if (response.code === 200) {
      ElMessage.success('回复成功')
      replyDialogVisible.value = false
      loadAdviceList()
    } else {
      ElMessage.error(response.msg || '回复失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    if (error.name !== 'Error') {
      ElMessage.error('操作失败，请稍后重试')
    }
  } finally {
    replySubmitting.value = false
  }
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
.teacher-advice-received-content {
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