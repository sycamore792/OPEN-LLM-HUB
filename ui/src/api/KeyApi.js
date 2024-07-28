import axiosInstance from '@/api/BaseApi';

export default {

    // 获取apikey列表
    getApiKeyList(   pageNum=1,
                     pageSize=10,) {
        return axiosInstance.get(`/apiKey/list/${pageNum}/${pageSize}`);
    },
    // 获取模型列表
    getModelPageList(
        pageNum=1,
        pageSize=10,
        apiKey = "",
    ) {
        return axiosInstance.get(`/apiKey/model/list/${pageNum}/${pageSize}?apiKey=${apiKey}`);
    },
};
