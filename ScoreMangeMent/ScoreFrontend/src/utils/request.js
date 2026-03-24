import axios from "axios";
import { ElMessage } from "element-plus";

const request = axios.create({
    timeout: 30000 // 后台接口超时时间
});

// request 拦截器
// 可以自请求发送前对请求做一些处理
request.interceptors.request.use(
    (config) => {
        // 只有非 FormData 请求才设置 Content-Type 为 application/json
        // FormData（文件上传）让浏览器自动设置 multipart/form-data
        if (!(config.data instanceof FormData)) {
            config.headers["Content-Type"] = "application/json;charset=utf-8";
        }
        
        const token = localStorage.getItem('token');
        if (token) {
            config.headers["Authorization"] = `Bearer ${token}`;
        }
        
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// response 拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
    (response) => {
        let res = response.data;
        if (typeof res === "string") {
            res = res ? JSON.parse(res) : res;
        }
        
        // 401 错误处理，但登录接口除外
        if (res.code === 401) {
            const isLoginRequest = response.config.url.includes('/api/login');
            if (!isLoginRequest) {
                localStorage.removeItem('token');
                localStorage.removeItem('userInfo');
                window.location.href = '/login';
                ElMessage.error(res.msg || '登录已过期，请重新登录');
            }
        }
        
        return res;
    },
    (error) => {
        if (error.response) {
            const status = error.response.status;
            const isLoginRequest = error.response.config.url.includes('/api/login');
            
            if (status === 401) {
                if (!isLoginRequest) {
                    localStorage.removeItem('token');
                    localStorage.removeItem('userInfo');
                    window.location.href = '/login';
                    ElMessage.error(error.response.data.msg || '登录已过期，请重新登录');
                }
            } else if (status === 404) {
                // 登录请求不显示 404 全局提示
                if (!isLoginRequest) {
                    ElMessage.error("未找到请求接口");
                }
            } else if (status === 500) {
                // 登录请求不显示 500 全局提示，让登录页面自己处理
                if (!isLoginRequest) {
                    ElMessage.error("系统异常，请查看后端控制台报错");
                }
            } else {
                // 登录请求不显示其他全局提示
                if (!isLoginRequest) {
                    ElMessage.error(error.response.data.msg || '请求失败');
                }
            }
        } else {
            // 网络错误，登录请求也显示提示
            ElMessage.error("网络错误，请检查网络连接");
        }
        return Promise.reject(error);
    }
);

export default request;