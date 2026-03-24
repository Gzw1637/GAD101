import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

// 懒加载方式（推荐）
const StudentList = () => import('@/views/StudentList.vue')
const Home = () => import('@/views/Home.vue')
const Login = () => import('@/views/Login.vue')
const Admin = () => import('@/views/Admin.vue')
const AdminUser = () => import('@/views/AdminUser.vue')
const AdminStudent = () => import('@/views/AdminStudent.vue')
const AdminDashboard = () => import('@/views/AdminDashboard.vue')
const AdminTeacher = () => import('@/views/AdminTeacher.vue')
const AdminExam = () => import('@/views/AdminExam.vue')
const AdminScore = () => import('@/views/AdminScore.vue')
const AdminSubject = () => import('@/views/AdminSubject.vue')
const AdminSubjectDetail = () => import('@/views/AdminSubjectDetail.vue')
const AdminAdvice = () => import('@/views/AdminAdvice.vue')
const Teacher = () => import('@/views/Teacher.vue')
const TeacherCourse = () => import('@/views/TeacherCourse.vue')
const TeacherScore = () => import('@/views/TeacherScore.vue')
const Student = () => import('@/views/Student.vue')
const StudentScore = () => import('@/views/StudentScore.vue')
const StudentAdviceSent = () => import('@/views/StudentAdviceSent.vue')
const StudentAdviceReceived = () => import('@/views/StudentAdviceReceived.vue')

const routes = [
    {path: '/', name: 'Home', component: Home},
    {path: '/login', name: 'Login', component: Login},
    {path: '/student', name: 'Student', component: Student, meta: { requiresAuth: true, role: 14981001 }, children: [
        {path: 'score', name: 'StudentScore', component: StudentScore},
        {path: 'advice/sent', name: 'StudentAdviceSent', component: StudentAdviceSent},
        {path: 'advice/received', name: 'StudentAdviceReceived', component: StudentAdviceReceived}
    ]},
    {path: '/teacher', name: 'Teacher', component: Teacher, meta: { requiresAuth: true, role: 14981002 }, children: [
        {path: '', name: 'TeacherCourse', component: TeacherCourse},
        {path: 'score', name: 'TeacherScore', component: TeacherScore}
    ]},
    {path: '/admin', name: 'Admin', component: Admin, meta: { requiresAuth: true, role: 14981003 }, children: [
        {path: '', name: 'AdminDashboard', component: AdminDashboard},
        {path: 'user', name: 'AdminUser', component: AdminUser},
        {path: 'student', name: 'AdminStudent', component: AdminStudent},
        {path: 'teacher', name: 'AdminTeacher', component: AdminTeacher},
        {path: 'exam', name: 'AdminExam', component: AdminExam},
        {path: 'score', name: 'AdminScore', component: AdminScore},
        {path: 'subject', name: 'AdminSubject', component: AdminSubject},
        {path: 'subject/:subjectCode/:subjectName', name: 'AdminSubjectDetail', component: AdminSubjectDetail},
        {path: 'advice', name: 'AdminAdvice', component: AdminAdvice}
    ]}
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    // 访问首页时直接跳转到登录页
    if (to.path === '/') {
        next('/login')
        return
    }
    
    const token = localStorage.getItem('token')
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    
    if (to.meta.requiresAuth && !token) {
        next('/login')
        return
    }
    
    if (to.meta.requiresAuth && to.meta.role) {
        if (userInfo.role !== to.meta.role) {
            ElMessage.error('无权访问该页面')
            switch (userInfo.role) {
                case 14981001:
                    next('/student')
                    break
                case 14981002:
                    next('/teacher')
                    break
                case 14981003:
                    next('/admin')
                    break
                default:
                    next('/login')
            }
            return
        }
    }
    
    next()
})

export default router