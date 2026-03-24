<template>
  <div class="admin-subject-detail-content">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>{{ subjectName }}教师查询</span>
          <el-button type="info" @click="goBack">返回</el-button>
        </div>
      </template>
      
      <div class="search-bar">
        <el-input v-model="searchQuery" placeholder="请输入教师姓名" style="width: 300px; margin-right: 10px">
          <template #append>
            <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
          </template>
        </el-input>
        <el-button type="info" @click="resetSearch">重置</el-button>
      </div>
      
      <el-table :data="teacherList" style="width: 100%" border v-loading="loading">
        <el-table-column label="序号" width="60">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="teacherCode" label="教师编号" width="120" />
        <el-table-column prop="name" label="教师姓名" width="100" />
        <el-table-column prop="userName" label="用户名" width="150" />
        <el-table-column label="性别" width="60">
          <template #default="scope">
            <span>{{ scope.row.sexDesc || '未知' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column label="任教科目" width="100">
          <template #default="scope">
            <span>{{ scope.row.teachSubjectDesc || '未知' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="教师类型" width="100">
          <template #default="scope">
            <span>{{ scope.row.teacherTypeDesc || '未知' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.teacherStatus)" size="small">
              {{ scope.row.teacherStatusDesc || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="任教年级" width="80">
          <template #default="scope">
            <span v-if="scope.row.gra">{{ scope.row.gra }}级</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="任教班级" width="80">
          <template #default="scope">
            <span v-if="scope.row.cla">{{ scope.row.cla }}班</span>
            <span v-else>-</span>
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
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

// 获取路由参数
const subjectCode = computed(() => route.params.subjectCode)
const subjectName = computed(() => route.params.subjectName)

// 搜索查询
const searchQuery = ref('')

// 分页数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 教师列表
const teacherList = ref([])

// 加载状态
const loading = ref(true)

// 获取状态标签类型
const getStatusType = (status) => {
  const statusMap = {
    '10021001': 'success',    // 在职
    '10021002': 'danger',     // 离职
    '10021003': 'warning'     // 休假
  }
  return statusMap[status] || 'info'
}

// 获取教师列表
const loadTeacherList = async () => {
  loading.value = true
  try {
    // 调用后端接口，按学科查询教师
    const response = await request.get(`/api/teacher/list-by-subject/${subjectCode.value}`)
    if (response.code === 200 && response.data) {
      let data = response.data
      console.log('原始教师数据:', data)
      
      // 如果有搜索条件，根据教师姓名过滤
      if (searchQuery.value) {
        data = data.filter(teacher => teacher.name && teacher.name.includes(searchQuery.value))
      }
      
      // 按教师编号排序
      const sortedList = data.sort((a, b) => a.teacherCode - b.teacherCode)
      // 设置总数
      total.value = sortedList.length
      // 根据当前页和每页数量进行切片（前端分页）
      const startIndex = (currentPage.value - 1) * pageSize.value
      const endIndex = startIndex + pageSize.value
      teacherList.value = sortedList.slice(startIndex, endIndex)
      
      console.log('最终教师列表:', teacherList.value)
      
      if (teacherList.value.length === 0 && searchQuery.value) {
        ElMessage.info('未找到该教师')
      }
    }
  } catch (error) {
    console.error('获取教师列表失败:', error)
    ElMessage.error('获取教师列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索教师
const handleSearch = () => {
  loadTeacherList()
}

// 重置搜索
const resetSearch = () => {
  searchQuery.value = ''
  loadTeacherList()
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  loadTeacherList()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  loadTeacherList()
}

// 返回学科管理首页
const goBack = () => {
  router.push({ name: 'AdminSubject' })
}

// 组件挂载时加载数据
onMounted(() => {
  loadTeacherList()
})
</script>

<style scoped>
.admin-subject-detail-content {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}
</style>
