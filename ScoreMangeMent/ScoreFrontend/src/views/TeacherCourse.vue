<template>
  <div class="teacher-course-content">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>课程列表</span>
          <div class="header-actions">
            <el-input v-model="searchQuery" placeholder="请输入课程名称" style="width: 200px; margin-right: 10px" />
            <el-button type="primary">查询</el-button>
            <el-button type="success" style="margin-left: 10px">重置</el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="courses" style="width: 100%">
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column prop="studentCount" label="学生人数" width="100" />
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '进行中' ? 'success' : 'info'>
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" type="primary" @click="handleView(scope.row)">查看</el-button>
            <el-button size="small" type="success" style="margin-left: 5px" @click="handleScore(scope.row)">成绩</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="10"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Document, DataAnalysis, School, Notebook, UserFilled, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const searchQuery = ref('')
const total = ref(9)
const currentPage = ref(1)

const courses = ref([
  { courseName: '分布式开发', credit: 6, studentCount: 45, startTime: '2025-11-01 00:00', endTime: '2025-11-27 00:00', status: '进行中' },
  { courseName: 'J2EE高级框架设计', credit: 5, studentCount: 38, startTime: '2025-11-01 00:00', endTime: '2025-11-27 00:00', status: '进行中' },
  { courseName: '软件1', credit: 1, studentCount: 50, startTime: '2025-11-01 00:00', endTime: '2025-11-20 00:00', status: '已结束' },
  { courseName: '微服务', credit: 6, studentCount: 42, startTime: '2025-11-01 00:00', endTime: '2025-11-11 00:00', status: '已结束' },
  { courseName: 'J2EE框架', credit: 8, studentCount: 35, startTime: '2025-11-02 00:00', endTime: '2025-11-27 00:00', status: '进行中' }
])

const handleView = (row) => {
  // 查看课程详情
  console.log('View course:', row)
}

const handleScore = (row) => {
  // 管理课程成绩
  console.log('Manage score:', row)
  router.push('/teacher/score')
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  // 分页查询
  console.log('Current page:', current)
}
</script>

<style scoped>
.teacher-course-content {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>