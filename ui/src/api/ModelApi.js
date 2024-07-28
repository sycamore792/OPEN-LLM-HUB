import axiosInstance from '@/api/BaseApi';
import { fetchEventSource } from "@microsoft/fetch-event-source";

export default {

    // 获取模型厂商列表
    getModelCompanyList() {
        return axiosInstance.get(`/model/company/list`);
    },
    // 获取模型列表
    getModelPageList(
        pageNum = 1,
        pageSize = 10,
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

    chatCompletion({
                       model = "",
                       messages = [{
                           role: '',
                           content: ''
                       }],
                       temperature = 0.7,
                       max_tokens = 2048,
                       apiKey = ""
                   }) {
        // 设置请求头
        const token =  `Bearer ${apiKey}`;
        return axiosInstance.post(`/v1/chat/completions`, {
            model,
            messages,
            temperature,
            max_tokens,
        }, {
            headers: {
                'Authorization': token
            }
        })
    }
};
