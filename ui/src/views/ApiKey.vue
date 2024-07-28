<template>
    <div class="page-container">
        <div class="content-wrapper">
            <div class="table-container">
                <el-table :fit="false" :data="tableData" style="width: 70%">
                    <el-table-column label="创建人" width="180">
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <span class="table-item">桑某昌</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column show-overflow-tooltip label="ApiKey" width="400px">
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <span class="table-item">{{ maskApiKey(scope.row.apiKey) }}</span>
                                <el-tooltip content="点击复制完整 ApiKey" placement="top">
                                    <el-button type="text" @click="copyApiKey(scope.row.apiKey)">
                                        <el-icon><DocumentCopy /></el-icon>
                                    </el-button>
                                </el-tooltip>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="创建时间" width="200px">
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <span class="table-item">{{ DateUtil.dateFormat(scope.row.createTime) }}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="备注" width="200px">
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <span>{{ scope.row.remark }}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column fixed="right" label="Operations" min-width="150">
                        <template #default="scope">
                            <el-button link type="primary" size="small" @click="handleClickModelList(scope.row.apiKey)">
                                查看授权模型
                            </el-button>
                            <el-button link type="primary" size="small" @click="toChatPage(scope.row.apiKey)">测试</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>

            <div class="pagination-container">
                <el-pagination
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page="currentPage"
                    :page-sizes="[5, 10, 20, 50]"
                    :page-size="pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="dataTotal"
                >
                </el-pagination>
            </div>
        </div>


        <el-dialog  v-model="modelListDialogTableVisible"  width="70%">
            <div v-loading="modelListDialogLoading">
                <el-table @row-click="checkModelInfo"  :fit="false" :data="modelData.tableData"
                >
                    <el-table-column label="模型名称" width="200px">
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <span>{{ scope.row.modelName }}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="发行公司" width="150px">
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <span>{{ scope.row.authorCompanyNameZh }}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="部署公司" width="150px">
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <span>{{ scope.row.deployCompanyNameZh }}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="BaseUrl " show-overflow-tooltip width="300px">
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <span>{{ scope.row.modelServerBaseUrl }}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="创建时间" width="200px">
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <span>{{ DateUtil.dateFormat(scope.row.createTime) }}</span>
                            </div>
                        </template>
                    </el-table-column>
<!--                    <el-table-column fixed="right" label="Operations" min-width="150">-->
<!--                        <template #default="scope">-->
<!--                            &lt;!&ndash;                            <el-button link type="primary" size="small">测试</el-button>&ndash;&gt;-->
<!--                        </template>-->
<!--                    </el-table-column>-->
<!--                    <el-table-column fixed="right" label="状态" width="300px">-->
<!--                        <template #default="scope">-->
<!--                            <div style="display: flex; align-items: center">-->
<!--                                <span :class="['status-dot',scope.row.modelHealthyInfo ? (scope.row.modelHealthyInfo.healthy == 1?'enabled':'disabled') : 'unknown'    ]"></span>-->
<!--                                <span>{{ scope.row.modelHealthyInfo ? (scope.row.modelHealthyInfo.healthy == 1?'健康':'异常') : '未更新' }}</span>-->
<!--                                <span style="margin-left: 20px"> {{ scope.row.modelHealthyInfo ? (scope.row.modelHealthyInfo.healthy == 1? '更新于： '+ DateUtil.dateFormat(scope.row.modelHealthyInfo.update) :'') : '' }}</span>-->
<!--                            </div>-->
<!--                        </template>-->
<!--                    </el-table-column>-->
                </el-table>
            </div>

        </el-dialog>

    </div>
</template>

<script>
import { Timer, DocumentCopy } from '@element-plus/icons-vue'
import keyApi from "@/api/KeyApi";
import DateUtil from "@/utils/DateUtil";
import { ElMessage } from 'element-plus';
import router from "@/router";

export default {
    name: "ApiKey",
    computed: {
        DateUtil() {
            return DateUtil
        }
    },
    components: { Timer, DocumentCopy },
    created() {
        this.searchData()
    },
    data() {
        return {
            modelListDialogTableVisible:false,
            modelListDialogLoading:false,
            currentPage: 1,
            dataTotal: 1,
            pageSize: 8,
            tableData: [],
            modelData:{
                tableData:[],
                totalCount:0,
            }
        }
    },
    methods: {
        toChatPage(apiKey = ''){
            this.$router.push("/chat/"+apiKey)
        },
        handleClickModelList(apiKey = ''){
            this.modelListDialogLoading = true
            this.modelListDialogTableVisible = true
            keyApi.getModelPageList(1, 100,apiKey).then(res => {
                this.modelData.tableData = res.data.records
                this.modelData.totalCount = res.data.total
                this.modelListDialogLoading = false
            })
        },
        searchData() {
            keyApi.getApiKeyList(this.currentPage, this.pageSize).then(res => {
                this.tableData = res.data.records
                this.dataTotal = res.data.total
            })
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.searchData()
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.searchData()
        },
        maskApiKey(apiKey) {
            if (apiKey.length <= 8) {
                return '*'.repeat(apiKey.length);
            }
            return apiKey.substring(0, 4) + '*'.repeat(apiKey.length - 8) + apiKey.substring(apiKey.length - 4);
        },
        copyApiKey(apiKey) {
            navigator.clipboard.writeText(apiKey).then(() => {
                ElMessage({
                    message: 'ApiKey 已复制到剪贴板',
                    type: 'success',
                });
            }).catch(() => {
                ElMessage({
                    message: '复制失败，请手动复制',
                    type: 'error',
                });
            });
        }
    }
}
</script>

<style scoped>
.table-item {
    font-weight: bolder;
    font-size: 16px;
}

.page-container {
    display: flex;
    justify-content: center;
    width: 100%;
}

.content-wrapper {
    width: 100%;
    max-width: 1500px; /* 或者您认为合适的最大宽度 */
    padding: 20px;
}

.table-container {
    padding-top: 50px;
    display: flex;
    justify-content: center;
    width: 100%;
}


.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}
</style>
