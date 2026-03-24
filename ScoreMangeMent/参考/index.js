import { createRouter, createWebHistory } from 'vue-router'
import Student from '../views/Student.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {path: '/',redirect:'/login'},
    {path: '/studentmanager',component: () => import('../views/StudentManager.vue'),children:[
         { path: 'mycourse', component: () => import('@/views/MyCourse.vue') }, // 我的选课
        {path: 'courseselection',meta:{title:'学生端'},component: () => import('../views/CourseSelection.vue'),},
        {path: 'personal',meta:{title:'个人信息'},component: () => import('../views/Personal.vue'),},
        {path: 'password',meta:{title:'修改密码'},component: () => import('../views/Password.vue'),},
      ]},
    {path: '/teachermanager',component: () => import('../views/TeacherManager.vue'),children:[
        {path: 'setcourse',meta:{title:'教师端'},component: () => import('../views/SetCourse.vue'),},
        {path: 'teacherstudent',meta:{title:'学生信息'},component: () => import('../views/TeacherStudent.vue'),},
        {path: 'personal',meta:{title:'个人信息'},component: () => import('../views/Personal.vue'),},
        {path: 'password',meta:{title:'修改密码'},component: () => import('../views/Password.vue'),},
      ]},
    {path: '/adminmanager',component: () => import('../views/AdminManager.vue'),children:[
        {path: 'student',meta:{title:'管理端'},component: () => import('../views/Student.vue'),},
        {path: 'teacher',meta:{title:'教师信息'},component: () => import('../views/Teacher.vue'),},
        {path: 'getcourse',meta:{title:'课程信息'},component: () => import('../views/GetCourse.vue'),},
        {path: 'getcourse2',meta:{title:'课程信息'},component: () => import('../views/GetCourse2.vue'),},
        {path: 'personal',meta:{title:'个人信息'},component: () => import('../views/Personal.vue'),},
        {path: 'password',meta:{title:'修改密码'},component: () => import('../views/Password.vue'),},
      ]},
    {path:'/login',meta:{title:'学生端(教师端)登录'},component:()=>import("../views/Login.vue")},
    {path:'/test',meta:{title:'学生端(教师端)登录'},component:()=>import("../views/CourseDetail.vue")},
    {path:'/adminlogin',meta:{title:'管理员登录'},component:()=>import("../views/AdminLogin.vue")},
    {path:'/registerpersonal',meta:{title:'注册身份'},component:()=>import("../views/RegisterPersonal.vue")},
    {path:'/studentregister',meta:{title:'学生注册'},component:()=>import("../views/StudentRegister.vue")},
    {path:'/teacherregister',meta:{title:'教师注册'},component:()=>import("../views/TeacherRegister.vue")},
    {path:'/404',meta:{title:'404页面'},component:()=>import("../views/404.vue")},
    {path:'/:pathMatch(.*)*', redirect:"/404"},
  ],
})
router.beforeEach((to,from,next) => {
  document.title=to.meta.title
  next();
});
export default router

