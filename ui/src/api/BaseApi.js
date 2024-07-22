import axios from 'axios'
import router from '@/router'; // 假设你使用了Vue Router
// import { useUserStore } from '@/store/userStore'; // 假设你使用了Pinia来管理用户状态

// 创建Axios实例
const axiosInstance = axios.create({
    baseURL: process.env.VUE_APP_API_BASE_URL, // 从环境变量中获取基础URL
    timeout: 10000, // 请求超时时间
    headers: {
        'Content-Type': 'application/json',
    },
});
//
// // 请求拦截器
// axiosInstance.interceptors.request.use(
//     (config) => {
//
//         const user = localStorage.getItem('user');
//         if (user){
//             let token = JSON.parse(user)['token'];
//             if (token) {
//                 config.headers['Authorization'] = token;
//             }
//         }
//         return config;
//     },
//     (error) => {
//         return Promise.reject(error);
//     }
// );
//
// // 响应拦截器
// axiosInstance.interceptors.response.use(
//     (response) => {
//         return response;
//     },
//     (error) => {
//         if (error.response) {
//             switch (error.response.status) {
//                 case 401:
//                     // 未授权，跳转到登录页
//                     router.push('/home');
//                     useUserStore().loginCardVisible = true;
//                     break;
//                 case 403:
//                     // 权限不足
//                     console.error('权限不足');
//                     break;
//                 case 500:
//                     // 服务器错误
//                     console.error('服务器错误');
//                     break;
//                 default:
//                     console.error(`未处理的错误: ${error.response.status}`);
//             }
//         } else if (error.request) {
//             // 请求已经发出，但没有收到响应
//             console.error('网络错误，请检查您的网络连接');
//         } else {
//             // 发送请求时出了点问题
//             console.error('请求配置错误', error.message);
//         }
//         return Promise.reject(error);
//     }
// );

export default axiosInstance;
