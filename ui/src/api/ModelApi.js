import axiosInstance from '@/api/BaseApi';

export default {

    // 获取模型厂商列表
    getModelCompanyList() {
        return axiosInstance.get(`/model/company/list`);
    },
    // 获取模型列表
    getModelPageList(
        pageNum=1,
        pageSize=10,
        modelName = "",
        deployCompanyId = "",
        authorCompanyId = "",
    ) {
        return axiosInstance.get(`/model/list/${pageNum}/${pageSize}?deployCompanyId=${deployCompanyId}&authorCompanyId=${authorCompanyId}&modelName=${modelName}`);
    },

    // 获取模型详情
    getModelDetail(id) {
        return axiosInstance.get(`/model/${id}`);
    },

};
