<template>
  <div class="admin-advice-content">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>意见管理</span>
        </div>
      </template>
      
      <div class="search-bar">
        <el-input
          v-model="filterRecipientName"
          placeholder="接收者姓名"
          style="width: 150px; margin-right: 10px"
          clearable
        />
        <el-input
          v-model="filterSenderName"
          placeholder="发送者姓名"
          style="width: 150px; margin-right: 10px"
          clearable
        />
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
        <el-table-column prop="senderName" label="发送人" width="120" />
        <el-table-column prop="recipientName" label="收件人" width="120" />
        <el-table-column label="内容" width="300">
          <template #default="scope">
            <el-tooltip :content="scope.row.adviceDesc" placement="top" :max-width="300" :show-after="800">
              <div class="content-cell">
                {{ scope.row.adviceDesc ? (scope.row.adviceDesc.length > 50 ? scope.row.adviceDesc.substring(0, 50) + '...' : scope.row.adviceDesc) : '' }}
              </div>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="图片" width="150">
          <template #default="scope">
            <div v-if="scope.row.imageUrls" style="display: flex; gap: 5px; flex-wrap: wrap;">
              <template v-if="typeof scope.row.imageUrls === 'string'">
                <el-image
                  v-for="(url, index) in scope.row.imageUrls.split(',')"
                  :key="index"
                  :src="url"
                  :preview-src-list="scope.row.imageUrls.split(',')"
                  :initial-index="index"
                  fit="cover"
                  style="width: 40px; height: 40px; border-radius: 4px; cursor: pointer;"
                >
                  <template #placeholder>
                    <div style="width: 40px; height: 40px; background: #f5f7fa; display: flex; align-items: center; justify-content: center;">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
              </template>
              <template v-else-if="Array.isArray(scope.row.imageUrls)">
                <el-image
                  v-for="(url, index) in scope.row.imageUrls"
                  :key="index"
                  :src="url"
                  :preview-src-list="scope.row.imageUrls"
                  :initial-index="index"
                  fit="cover"
                  style="width: 40px; height: 40px; border-radius: 4px; cursor: pointer;"
                >
                  <template #placeholder>
                    <div style="width: 40px; height: 40px; background: #f5f7fa; display: flex; align-items: center; justify-content: center;">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
              </template>
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
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleViewDetail(scope.row)">查看</el-button>
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
    
    <!-- 查看详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="意见详情"
      width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="发送人">{{ currentDetail.senderName }}</el-descriptions-item>
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
          <div v-if="currentDetail.imageUrls" style="display: flex; gap: 10px; flex-wrap: wrap;">
            <template v-if="typeof currentDetail.imageUrls === 'string'">
              <el-image
                v-for="(url, index) in currentDetail.imageUrls.split(',')"
                :key="index"
                :src="url"
                :preview-src-list="currentDetail.imageUrls.split(',')"
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
            </template>
            <template v-else-if="Array.isArray(currentDetail.imageUrls)">
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
            </template>
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
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { Picture } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox, ElIcon } from 'element-plus'

// 筛选条件
const filterRecipientName = ref('')
const filterSenderName = ref('')
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

// 字典数据
const adviceStatusMap = {
  10131001: '未读',
  10131002: '已读'
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  try {
    const d = new Date(date)
    return d.toLocaleString('zh-CN', { 
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch (error) {
    console.error('日期格式化错误:', error)
    return ''
  }
}

// 获取状态标签类型
const getAdviceStatusTagType = (status) => {
  try {
    const typeMap = {
      10131001: 'danger',
      10131002: 'success'
    }
    return typeMap[status] || 'info'
  } catch (error) {
    console.error('获取状态标签类型错误:', error)
    return 'info'
  }
}



// 获取意见列表
const loadAdviceList = async () => {
  loading.value = true
  try {
    // 构建查询参数
    const params = {}
    if (filterAdviceType.value) params.adviceType = filterAdviceType.value
    if (filterAdviceStatus.value) params.adviceStatus = filterAdviceStatus.value
    
    // 调用后端高级搜索接口
    const response = await request.post('/api/advice/search/advanced', null, { params })
    if (response.code === 200) {
      let data = response.data || []
      
      // 确保数据是数组
      if (!Array.isArray(data)) {
        data = []
      }
      
      // 如果有姓名搜索条件，在前端进行过滤
      if (filterRecipientName.value) {
        data = data.filter(advice => 
          advice && advice.recipientName && advice.recipientName.includes(filterRecipientName.value)
        )
      }
      if (filterSenderName.value) {
        data = data.filter(advice => 
          advice && advice.senderName && advice.senderName.includes(filterSenderName.value)
        )
      }
      
      // 按创建时间排序
      const sortedList = data.sort((a, b) => {
        const dateA = a && a.createTime ? new Date(a.createTime) : new Date(0)
        const dateB = b && b.createTime ? new Date(b.createTime) : new Date(0)
        return dateB - dateA
      })
      
      // 设置总数
      total.value = sortedList.length
      
      // 前端分页
      const startIndex = (currentPage.value - 1) * pageSize.value
      const endIndex = startIndex + pageSize.value
      adviceList.value = sortedList.slice(startIndex, endIndex)
    }
  } catch (error) {
    console.error('获取意见列表失败:', error)
    ElMessage.error('获取意见列表失败')
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
  filterRecipientName.value = ''
  filterSenderName.value = ''
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
    recipientName: row.recipientName,
    adviceTypeName: row.adviceTypeName,
    senderTypeName: row.senderTypeName,
    adviceStatus: row.adviceStatus,
    adviceStatusName: row.adviceStatusName,
    adviceDesc: row.adviceDesc,
    createTime: row.createTime,
    imageUrls: row.imageUrls
  })
  detailDialogVisible.value = true
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
.admin-advice-content {
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
