<template>
    <div class="page-container">
        <div class="content-wrapper">
            <div class="search-section">
                <span style="padding-right: 10px">模型名称</span>
                <el-input
                        v-model="searchModelName"
                        style="width: 240px"
                        placeholder="请输入模型名称"
                        clearable
                />
                <span style="padding-left: 10px; padding-right: 10px">发行厂商</span>
                <el-select
                        clearable
                        @change="searchData"
                        v-model="authorCompany"
                        value-key="id"
                        placeholder="请选择发行厂商"
                        size="large"
                        style="width: 240px"
                >
                    <el-option
                            v-for="item in companyOptions"
                            :key="item.id"
                            :label="item.companyNameZh"
                            :value="item"
                    >
                        <img :src="item.companyIcon" alt="Icon" class="icon"
                             style="width: 20px; height: 20px; margin-right: 10px">
                        {{ item.companyNameZh }}
                    </el-option>
                </el-select>
                <span style="padding-left: 10px; padding-right: 10px">部署厂商</span>
                <el-select
                        clearable
                        @change="searchData"
                        v-model="deployCompany"
                        value-key="id"
                        placeholder="请选择部署厂商"
                        size="large"
                        style="width: 240px"
                >
                    <el-option
                            v-for="item in companyOptions"
                            :key="item.id"
                            :label="item.companyNameZh"
                            :value="item"
                    >
                        <img :src="item.companyIcon" alt="Icon" class="icon"
                             style="width: 20px; height: 20px; margin-right: 10px">
                        {{ item.companyNameZh }}
                    </el-option>
                </el-select>
                <el-button @click="searchData" style="margin-left: 20px" :icon="Search" round>搜索</el-button>
            </div>

            <div class="table-container">
                <el-table @row-click="checkModelInfo" :row-class-name="tableRowClassName" :fit="false" :data="tableData"
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
                    <el-table-column label="状态" width="400px">
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <span :class="['status-dot',scope.row.modelHealthyInfo ? (scope.row.modelHealthyInfo.healthy == 1?'enabled':'disabled') : 'unknown'    ]"></span>
                                <span>{{ scope.row.modelHealthyInfo ? (scope.row.modelHealthyInfo.healthy == 1?'健康':'异常') : '未更新' }}</span>
                                <span style="margin-left: 20px"> {{ scope.row.modelHealthyInfo ? (scope.row.modelHealthyInfo.healthy == 1? '更新于： '+ DateUtil.dateFormat(scope.row.modelHealthyInfo.update) :'') : '' }}</span>
                            </div>
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
    </div>

    <el-dialog  v-model="dialogTableVisible" :title="modelInfo.modelName" width="70%">
        <div v-loading="dialogLoading">
            <el-descriptions class="description" title="基础信息">
                <el-descriptions-item label="发行公司：">{{modelInfo.authorCompanyNameZh? modelInfo.authorCompanyNameZh :''}} </el-descriptions-item>
                <el-descriptions-item label="部署公司：">{{modelInfo.deployCompanyNameZh? modelInfo.deployCompanyNameZh :''}}</el-descriptions-item>
                <el-descriptions-item label="模型类别：">
                    <el-tooltip :content="modelInfo.modelType? modelInfo.modelType :''" placement="top">
                        <el-tag size="small" >{{modelInfo.modelTypeCode? modelInfo.modelTypeCode :''}}</el-tag>
                    </el-tooltip>
                </el-descriptions-item>
                <el-descriptions-item v-if="modelInfo.modelServerName" label="部署方模型 ServerName：">{{modelInfo.modelServerName? modelInfo.modelServerName :''}}</el-descriptions-item>
                <el-descriptions-item v-if="modelInfo.protocolDesc" label="部署方模型协议：">
                    <el-tag size="small" >   {{modelInfo.protocolDesc? modelInfo.protocolDesc :''}}</el-tag>
                </el-descriptions-item>
                <el-descriptions-item v-if="modelInfo.modelServerBaseUrl" label="部署方模型服务地址：">{{modelInfo.modelServerBaseUrl? modelInfo.modelServerBaseUrl :''}}</el-descriptions-item>
                <el-descriptions-item v-if="modelInfo.remark" label="备注：">{{modelInfo.remark? modelInfo.remark :''}}</el-descriptions-item>
                <el-descriptions-item v-if="modelInfo.createTime" label="模型创建时间：">{{modelInfo.createTime? DateUtil.dateFormat(modelInfo.createTime) :''}}</el-descriptions-item>

            </el-descriptions>

            <el-divider content-position="left"></el-divider>

            <el-descriptions class="description" title="运行时信息">
                <el-descriptions-item label="平均TPS：">{{modelInfo.modelStatistic? modelInfo.modelStatistic.avgTps?modelInfo.modelStatistic.avgTps+"(个/秒) ":'' :''}} </el-descriptions-item>
                <el-descriptions-item label="平均首字延迟：">{{modelInfo.modelStatistic? modelInfo.modelStatistic.avgFirstChunkDelay?modelInfo.modelStatistic.avgFirstChunkDelay+"(毫秒)":"":''}}</el-descriptions-item>
                <el-descriptions-item label="请求token总和：">{{modelInfo.modelStatistic? modelInfo.modelStatistic.totalPromptTokens?modelInfo.modelStatistic.totalPromptTokens+"(个)":"":''}}</el-descriptions-item>
                <el-descriptions-item label="推理token总和：">{{modelInfo.modelStatistic? modelInfo.modelStatistic.totalCompletionTokens?modelInfo.modelStatistic.totalCompletionTokens+"(个)":"":''}}</el-descriptions-item>
                <el-descriptions-item label="token总和：">{{modelInfo.modelStatistic? modelInfo.modelStatistic.totalTokens?modelInfo.modelStatistic.totalTokens+"(个)":"":''}}</el-descriptions-item>
            </el-descriptions>

            <div class="description">

            </div>
        </div>


    </el-dialog>
</template>

<script>
import {Search, Timer} from '@element-plus/icons-vue'
import ModelApi from "@/api/ModelApi";
import modelApi from "@/api/ModelApi";
import DateUtil from "@/utils/DateUtil";

export default {
    name: "Model",
    computed: {
        Search() {
            return Search
        },
        DateUtil() {
            return DateUtil
        }

    },

    components: {Timer},
    created() {
        ModelApi.getModelPageList(this.currentPage, this.pageSize).then(res => {
            this.tableData = res.data.records
            this.dataTotal = res.data.total
        })
        modelApi.getModelCompanyList().then(res => {
            this.companyOptions = res.data.data
            console.log(this.companyOptions)
        })
    },
    data() {
        return {
            searchModelName: "",
            authorCompany: {},
            deployCompany: {},
            companyOptions: [
                {
                    companyNameZh: "ALL"
                }
            ],
            tableData: [],
            currentPage: 1,
            dataTotal: 1,
            pageSize: 20,
            dialogTableVisible: false,
            dialogLoading: false,
            modelInfo: {}
        }
    },
    methods: {
        getModelDetail(id){
            ModelApi.getModelDetail(id).then(res => {
                this.modelInfo = res.data.data
                this.dialogLoading = false
            })
        },
        searchData() {
            ModelApi.getModelPageList(this.currentPage, this.pageSize, this.searchModelName, this.deployCompany ? this.deployCompany.id : '', this.authorCompany ? this.authorCompany.id : '').then(res => {
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
        tableRowClassName({row, rowIndex}) {
            if (!row.modelHealthyInfo ) {
                return 'warning-row';
            }
            if (!(row.modelHealthyInfo.healthy) || !(row.modelHealthyInfo.healthy === 1)) {
                return 'warning-row';
            }
            return ''
        },
        checkModelInfo(row, column, event) {
            this.modelInfo = row

            this.dialogLoading = true
            this.dialogTableVisible = true
            this.getModelDetail(row.id)
        },
        handleEdit(index, row) {
            console.log(index, row);
        },
        handleDelete(index, row) {
            console.log(index, row);
        }
    }
}
</script>

<style scoped>
.status-dot {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    display: inline-block;
    margin-right: 5px;
}

.status-dot.enabled {
    background-color: green;
}

.status-dot.disabled {
    background-color: #9f2525;
}
.status-dot.unknown {
    background-color: #3f4556;
}
.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}

.description {
    margin: 20px;
}

.table-container {

    width: 100%;
}

.el-table >>> .warning-row {
    color: #000000;
    font-weight: bolder;
    background: #eac493;
}

.el-table >>> .success-row {
    background: #f0f9eb;
}

.page-container {
    display: flex;
    justify-content: center;
    width: 100%;
}

.content-wrapper {
    width: 100%;
    max-width: 1400px; /* 或者您认为合适的最大宽度 */
    padding: 20px;
}

.search-section {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-wrap: wrap;
    gap: 10px;
    margin: 20px;
}
</style>
