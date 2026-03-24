<template>
  <div class="admin-subject-content">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>学科查询</span>
          <el-button type="info" @click="goBack">返回</el-button>
        </div>
      </template>
      
      <div class="subject-grid">
        <el-button 
          v-for="subject in subjects" 
          :key="subject.value"
          type="primary" 
          size="large"
          @click="goToSubjectDetail(subject)"
          class="subject-button"
        >
          {{ subject.label }}
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

// 学科列表
const subjects = [
  { label: '语文', value: '10111001' },
  { label: '数学', value: '10111002' },
  { label: '英语', value: '10111003' },
  { label: '物理', value: '10111004' },
  { label: '化学', value: '10111005' },
  { label: '生物', value: '10111006' },
  { label: '历史', value: '10111007' },
  { label: '地理', value: '10111008' },
  { label: '政治', value: '10111009' }
]

// 跳转到学科详情页面
const goToSubjectDetail = (subject) => {
  router.push({
    name: 'AdminSubjectDetail',
    params: {
      subjectCode: subject.value,
      subjectName: subject.label
    }
  })
}

// 返回上一页
const goBack = () => {
  router.back()
}
</script>

<style scoped>
.admin-subject-content {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.subject-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 30px;
}

.subject-button {
  width: 100%;
  height: 100px;
  font-size: 18px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  padding: 0;
  margin: 0;
  border: none;
}

@media (max-width: 768px) {
  .subject-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .subject-grid {
    grid-template-columns: 1fr;
  }
}
</style>