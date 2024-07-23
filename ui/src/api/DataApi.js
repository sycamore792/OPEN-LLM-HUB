import axiosInstance from '@/api/BaseApi';

export default {

    // 获取api请求数据
    getApiRequestData(   ) {
        return axiosInstance.get(`/model_log/api_request_count`);
    },
};
